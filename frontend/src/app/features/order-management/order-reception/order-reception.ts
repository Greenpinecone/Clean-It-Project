import { CurrencyPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  computed,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { getApiValidationErrors } from '@core/api-errors/api-problem';
import { DemoUser } from '@core/auth/demo-user';
import { ToastNotifier } from '@core/notifications/toast-notifier';

import { CleaningServiceApi } from '../data-access/api/cleaning-service-api';
import { OrderApi } from '../data-access/api/order-api';
import { ShopApi } from '../data-access/api/shop-api';
import { AssignedShopResponse } from '../data-access/dto/assigned-shop-response';
import { CleaningServiceResponse } from '../data-access/dto/cleaning-service-response';
import { CreateOrderRequest } from '../data-access/dto/create-order-request';
import { OrderSummaryResponse } from '../data-access/dto/order-summary-response';
import { TEXTILE_OPTIONS } from '../models/textile-option';
import { TextileType } from '../models/textile-type';
import { customerContactRequiredValidator } from './customer-contact-required.validator';
import { CleaningServiceCode } from '../models/cleaning-service-codes';

type CustomerForm = FormGroup<{
  displayName: FormControl<string>;
  email: FormControl<string>;
  phoneNumber: FormControl<string>;
}>;

type OrderItemForm = FormGroup<{
  textileType: FormControl<TextileType>;
  quantity: FormControl<number>;
  material: FormControl<string>;
  serviceCodes: FormControl<CleaningServiceCode[]>;
}>;

type ReceptionForm = FormGroup<{
  customer: CustomerForm;
  expectedPickupDate: FormControl<string>;
  items: FormArray<OrderItemForm>;
}>;

@Component({
  selector: 'app-order-reception',
  imports: [CurrencyPipe, ReactiveFormsModule],
  templateUrl: './order-reception.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OrderReception {
  private readonly cleaningServiceApi = inject(CleaningServiceApi);
  private readonly destroyRef = inject(DestroyRef);
  private readonly demoUser = inject(DemoUser);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly orderApi = inject(OrderApi);
  private readonly router = inject(Router);
  private readonly shopApi = inject(ShopApi);
  private readonly toastNotifier = inject(ToastNotifier);

  protected readonly textileOptions = TEXTILE_OPTIONS;

  protected readonly receptionForm: ReceptionForm = this.fb.group({
    customer: this.fb.group(
      {
        displayName: ['', [Validators.required, Validators.maxLength(120)]],
        email: ['', [Validators.email, Validators.maxLength(160)]],
        phoneNumber: ['', [Validators.maxLength(40)]],
      },
      { validators: [customerContactRequiredValidator] },
    ),
    expectedPickupDate: ['', [Validators.required]],
    items: this.fb.array<OrderItemForm>([], [Validators.required]),
  });

  protected readonly assignedShop = signal<AssignedShopResponse | null>(null);
  protected readonly cleaningServices = signal<CleaningServiceResponse[]>([]);
  protected readonly createdOrder = signal<OrderSummaryResponse | null>(null);

  protected readonly isLoading = signal(false);
  protected readonly isSubmitting = signal(false);
  protected readonly submitted = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  private readonly formChanged = signal(0);

  protected readonly canOpenManagerOverview = this.demoUser.isManager;

  protected readonly servicePriceByCode = computed(
    () =>
      new Map(this.cleaningServices().map((service) => [service.code, service.priceMinorUnits])),
  );

  protected readonly totalItemCount = computed(() => {
    this.formChanged();
    return this.items.controls.reduce((total, item) => total + item.controls.quantity.value, 0);
  });

  protected readonly totalPriceMinorUnits = computed(() => {
    this.formChanged();

    return this.items.controls.reduce(
      (total, item) => total + this.calculateItemPriceMinorUnits(item),
      0,
    );
  });

  constructor() {
    this.expectedPickupDate.setValue(this.calculateExpectedPickupDate(0), { emitEvent: false });
    this.loadInitialData();
    this.watchFormChanges();
  }

  protected get customer(): CustomerForm {
    return this.receptionForm.controls.customer;
  }

  protected get displayName(): FormControl<string> {
    return this.customer.controls.displayName;
  }

  protected get email(): FormControl<string> {
    return this.customer.controls.email;
  }

  protected get phoneNumber(): FormControl<string> {
    return this.customer.controls.phoneNumber;
  }

  protected get expectedPickupDate(): FormControl<string> {
    return this.receptionForm.controls.expectedPickupDate;
  }

  protected get items(): FormArray<OrderItemForm> {
    return this.receptionForm.controls.items;
  }

  protected addTextile(textileType: TextileType): void {
    if (this.isTextileSelected(textileType)) {
      return;
    }

    this.items.push(
      this.fb.group({
        textileType: [textileType, [Validators.required]],
        quantity: [1, [Validators.required, Validators.min(1), Validators.max(20)]],
        material: ['', [Validators.maxLength(80)]],
        serviceCodes: this.fb.control<CleaningServiceCode[]>([], [Validators.required]),
      }),
    );

    this.items.markAsTouched();
    this.formChanged.update((value) => value + 1);
    this.updateExpectedPickupDate();
  }

  protected removeItem(index: number): void {
    this.items.removeAt(index);
    this.items.markAsTouched();
    this.formChanged.update((value) => value + 1);
    this.updateExpectedPickupDate();
  }

  protected isTextileSelected(textileType: TextileType): boolean {
    return this.items.controls.some((item) => item.controls.textileType.value === textileType);
  }

  protected toggleService(
    item: OrderItemForm,
    serviceCode: CleaningServiceCode,
    checked: boolean,
  ): void {
    const serviceCodes = item.controls.serviceCodes;
    const current = serviceCodes.value;
    const next = checked
      ? Array.from(new Set([...current, serviceCode]))
      : current.filter((code) => code !== serviceCode);

    serviceCodes.setValue(next);
    serviceCodes.markAsTouched();
    this.formChanged.update((value) => value + 1);
  }

  protected isServiceSelected(item: OrderItemForm, serviceCode: CleaningServiceCode): boolean {
    return item.controls.serviceCodes.value.includes(serviceCode);
  }

  protected calculateItemPriceMinorUnits(item: OrderItemForm): number {
    const quantity = item.controls.quantity.value;
    const serviceTotal = item.controls.serviceCodes.value.reduce(
      (total, serviceCode) => total + (this.servicePriceByCode().get(serviceCode) ?? 0),
      0,
    );

    return quantity * serviceTotal;
  }

  protected submit(): void {
    this.submitted.set(true);
    this.errorMessage.set(null);
    this.createdOrder.set(null);
    this.clearServerErrors(this.receptionForm);

    if (this.receptionForm.invalid || !this.assignedShop()) {
      this.receptionForm.markAllAsTouched();
      this.errorMessage.set('Please correct the highlighted fields.');
      return;
    }

    this.isSubmitting.set(true);

    this.orderApi
      .createOrder(this.toCreateOrderRequest())
      .pipe(
        finalize(() => this.isSubmitting.set(false)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (order) => {
          this.createdOrder.set(order);
          this.toastNotifier.success(`Order ${order.orderNumber} was received.`);
        },
        error: (error: unknown) => {
          const validationErrors = getApiValidationErrors(error);

          if (validationErrors.length > 0) {
            this.applyServerValidationErrors(validationErrors);
            this.errorMessage.set('Please correct the highlighted fields.');
            return;
          }

          this.errorMessage.set('The order could not be created.');
        },
      });
  }

  protected resetForNextOrder(): void {
    this.submitted.set(false);
    this.errorMessage.set(null);
    this.createdOrder.set(null);
    this.items.clear({ emitEvent: false });
    this.receptionForm.reset(
      {
        customer: { displayName: '', email: '', phoneNumber: '' },
        expectedPickupDate: this.calculateExpectedPickupDate(0),
        items: [],
      },
      { emitEvent: false },
    );
    this.formChanged.update((value) => value + 1);
  }

  protected goToManagerOverview(): void {
    void this.router.navigate(['/manager', 'orders']);
  }

  private loadInitialData(): void {
    this.isLoading.set(true);

    this.shopApi
      .getAssignedShop()
      .pipe(
        finalize(() => this.isLoading.set(false)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (shop) => this.assignedShop.set(shop),
        error: () => this.errorMessage.set('Assigned shop could not be loaded.'),
      });

    this.cleaningServiceApi
      .getCleaningServices()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (services) => this.cleaningServices.set(services),
        error: () => this.errorMessage.set('Cleaning services could not be loaded.'),
      });
  }

  private watchFormChanges(): void {
    this.receptionForm.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.formChanged.update((value) => value + 1);
      this.updateExpectedPickupDate();
    });
  }

  private updateExpectedPickupDate(): void {
    const expectedDate = this.calculateExpectedPickupDate(this.totalItemCount());

    if (this.expectedPickupDate.value !== expectedDate) {
      this.expectedPickupDate.setValue(expectedDate, { emitEvent: false });
    }
  }

  private calculateExpectedPickupDate(totalQuantity: number): string {
    const date = new Date();
    date.setDate(date.getDate() + Math.max(totalQuantity, 1));

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  private toCreateOrderRequest(): CreateOrderRequest {
    const value = this.receptionForm.getRawValue();
    const shop = this.assignedShop();

    if (!shop) {
      throw new Error('Assigned shop is required.');
    }

    return {
      shopCode: shop.shopCode,
      expectedPickupDate: value.expectedPickupDate,
      customer: {
        displayName: value.customer.displayName.trim(),
        email: this.toOptionalString(value.customer.email),
        phoneNumber: this.toOptionalString(value.customer.phoneNumber),
      },
      items: value.items.map((item) => ({
        textileType: item.textileType,
        quantity: item.quantity,
        material: this.toOptionalString(item.material),
        serviceCodes: item.serviceCodes,
      })),
    };
  }

  private toOptionalString(value: string): string | undefined {
    const trimmedValue = value.trim();
    return trimmedValue ? trimmedValue : undefined;
  }

  private applyServerValidationErrors(errors: { field?: string; message: string }[]): void {
    for (const error of errors) {
      const control = error.field
        ? this.receptionForm.get(error.field.replace(/\[(\d+)]/g, '.$1'))
        : null;

      if (!control) {
        this.errorMessage.set(error.message);
        continue;
      }

      control.setErrors({ ...control.errors, server: error.message });
      control.markAsTouched();
    }
  }

  private clearServerErrors(control: AbstractControl): void {
    if (control.errors?.['server']) {
      const { ...remainingErrors } = control.errors;
      control.setErrors(Object.keys(remainingErrors).length > 0 ? remainingErrors : null);
    }

    if (control instanceof FormGroup || control instanceof FormArray) {
      Object.values(control.controls).forEach((child) => this.clearServerErrors(child));
    }
  }
}
