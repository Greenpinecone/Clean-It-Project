import { CurrencyPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  computed,
  inject,
  input,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormArray,
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  finalize,
  map,
  of,
  startWith,
  switchMap,
} from 'rxjs';
import { getApiValidationErrors } from '@core/api-errors/api-problem';
import { DemoUser } from '@core/auth/demo-user';
import { ToastNotifier } from '@core/notifications/toast-notifier';
import { CustomerApi } from '../data-access/api/customer-api';
import { OrderApi } from '../data-access/api/order-api';
import { AssignedShopResponse } from '../data-access/dto/assigned-shop-response';
import { CleaningServiceResponse } from '../data-access/dto/cleaning-service-response';
import { CreateOrderRequest } from '../data-access/dto/create-order-request';
import { CustomerSearchResponse } from '../data-access/dto/customer-search-response';
import { CustomerSearchParams } from '../models/customer-search-params';
import { TEXTILE_OPTIONS } from '../models/textile-option';
import { TextileType } from '../models/textile-type';
import { customerContactRequiredValidator } from './customer-contact-required.validator';
import { CleaningServiceCode } from '../models/cleaning-service-codes';
import {
  applyServerValidationErrors,
  clearServerValidationErrors,
} from '@core/forms/server-validation-errors';

type CustomerForm = FormGroup<{
  id: FormControl<string>;
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
  private readonly customerApi = inject(CustomerApi);
  private readonly destroyRef = inject(DestroyRef);
  private readonly demoUser = inject(DemoUser);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly orderApi = inject(OrderApi);
  private readonly router = inject(Router);
  private readonly toastNotifier = inject(ToastNotifier);

  protected readonly assignedShop = input.required<AssignedShopResponse>();
  protected readonly cleaningServices = input.required<CleaningServiceResponse[]>();

  protected readonly customerResults = signal<CustomerSearchResponse[]>([]);
  protected readonly selectedCustomer = signal<CustomerSearchResponse | null>(null);
  protected readonly isNewCustomerMode = signal(false);
  protected readonly isSearchingCustomers = signal(false);
  protected readonly isSubmitting = signal(false);
  protected readonly submitted = signal(false);
  protected readonly showCustomerResults = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly customerSearchMessage = signal<string | null>(null);
  private readonly formChanged = signal(0);
  protected readonly canOpenManagerOverview = this.demoUser.isManager;

  protected readonly textileOptions = TEXTILE_OPTIONS;

  protected readonly receptionForm: ReceptionForm = this.fb.group({
    customer: this.fb.group(
      {
        id: [''],
        displayName: ['', [Validators.required, Validators.maxLength(120)]],
        email: ['', [Validators.email, Validators.maxLength(160)]],
        phoneNumber: ['', [Validators.maxLength(40)]],
      },
      { validators: [customerContactRequiredValidator] },
    ),
    expectedPickupDate: ['', [Validators.required]],
    items: this.fb.array<OrderItemForm>([], [Validators.required]),
  });

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
    this.watchCustomerSearch();
    this.watchFormChanges();
  }

  protected addTextile(textileType: TextileType): void {
    this.items.push(
      this.fb.group({
        textileType: [textileType, [Validators.required]],
        quantity: [1, [Validators.required, Validators.min(1), Validators.max(20)]],
        material: ['', [Validators.maxLength(80)]],
        serviceCodes: this.fb.control<CleaningServiceCode[]>([], [Validators.required]),
      }),
    );

    this.items.markAsTouched();
  }

  protected removeItem(index: number): void {
    this.items.removeAt(index);
    this.items.markAsTouched();
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

  protected selectCustomer(customer: CustomerSearchResponse): void {
    this.selectedCustomer.set(customer);
    this.showCustomerResults.set(false);
    this.customerResults.set([]);
    this.customerSearchMessage.set(null);

    this.customer.setValue(
      {
        id: customer.id,
        displayName: customer.displayName,
        email: customer.email ?? '',
        phoneNumber: customer.phoneNumber ?? '',
      },
      { emitEvent: false },
    );
  }

  protected clearSelectedCustomer(): void {
    if (this.isNewCustomerMode()) {
      return;
    }

    if (!this.selectedCustomer() && !this.customer.controls.id.value) {
      return;
    }

    this.selectedCustomer.set(null);
    this.customer.controls.id.setValue('', { emitEvent: false });
  }

  protected setNewCustomerMode(enabled: boolean): void {
    this.isNewCustomerMode.set(enabled);
    this.customerResults.set([]);
    this.customerSearchMessage.set(null);
    this.showCustomerResults.set(false);

    if (enabled) {
      this.selectedCustomer.set(null);
      this.customer.controls.id.setValue('', { emitEvent: false });
    }
  }

  protected submit(): void {
    this.submitted.set(true);
    this.errorMessage.set(null);
    clearServerValidationErrors(this.receptionForm);

    if (this.receptionForm.invalid) {
      this.receptionForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    this.orderApi
      .createOrder(this.toCreateOrderRequest())
      .pipe(
        finalize(() => {
          this.isSubmitting.set(false);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (order) => {
          this.resetForNextOrder();
          this.toastNotifier.success(`Order ${order.orderNumber} was received.`);
          if (this.canOpenManagerOverview()) {
            void this.router.navigate(['/manager', 'orders']);
          }
        },
        error: (error: unknown) => {
          const validationErrors = getApiValidationErrors(error);

          if (validationErrors.length > 0) {
            applyServerValidationErrors(this.receptionForm, validationErrors, (message) => {
              this.toastNotifier.error(message);
            });
            return;
          }
        },
      });
  }

  protected resetForNextOrder(): void {
    this.submitted.set(false);
    this.errorMessage.set(null);
    this.selectedCustomer.set(null);
    this.customerResults.set([]);
    this.isNewCustomerMode.set(false);
    this.customerSearchMessage.set(null);
    this.showCustomerResults.set(false);
    this.items.clear({ emitEvent: false });
    this.receptionForm.reset(
      {
        customer: { id: '', displayName: '', email: '', phoneNumber: '' },
        expectedPickupDate: this.calculateExpectedPickupDate(0),
        items: [],
      },
      { emitEvent: false },
    );
    this.formChanged.update((value) => value + 1);
  }

  protected shouldShowCustomerDropdown(): boolean {
    return (
      !this.isNewCustomerMode() &&
      this.showCustomerResults() &&
      (this.isSearchingCustomers() ||
        this.customerResults().length > 0 ||
        this.customerSearchMessage() !== null)
    );
  }

  private watchCustomerSearch(): void {
    this.customer.valueChanges
      .pipe(
        startWith(this.customer.getRawValue()),
        map(() => this.toCustomerSearchParams()),
        debounceTime(300),
        distinctUntilChanged(
          (previous, current) =>
            previous.displayName === current.displayName &&
            previous.email === current.email &&
            previous.phoneNumber === current.phoneNumber,
        ),
        switchMap((params) => {
          if (
            this.isNewCustomerMode() ||
            (!params.displayName && !params.email && !params.phoneNumber)
          ) {
            this.customerResults.set([]);
            this.customerSearchMessage.set(null);
            this.showCustomerResults.set(false);
            return of([]);
          }

          this.isSearchingCustomers.set(true);
          this.showCustomerResults.set(true);
          this.customerSearchMessage.set(null);

          return this.customerApi.searchCustomers(params).pipe(
            catchError(() => {
              this.customerSearchMessage.set('Customer search is currently unavailable.');
              return of([]);
            }),
            finalize(() => this.isSearchingCustomers.set(false)),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((customers) => {
        this.customerResults.set(customers);

        if (customers.length === 0 && this.showCustomerResults() && !this.customerSearchMessage()) {
          this.customerSearchMessage.set('No existing customer found.');
        }
      });
  }

  private watchFormChanges(): void {
    this.receptionForm.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.formChanged.update((value) => value + 1);
      this.updateExpectedPickupDate();
    });
  }

  protected showCustomerSearchOnFocus(): void {
    if (!this.isNewCustomerMode()) {
      this.showCustomerResults.set(true);
    }
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

  private toCustomerSearchParams(): CustomerSearchParams {
    const value = this.customer.getRawValue();

    return {
      displayName: this.toSearchValue(value.displayName),
      email: this.toSearchValue(value.email),
      phoneNumber: this.toSearchValue(value.phoneNumber),
    };
  }

  private toSearchValue(value: string): string | undefined {
    const trimmedValue = value.trim();
    return trimmedValue.length >= 2 ? trimmedValue : undefined;
  }

  private toCreateOrderRequest(): CreateOrderRequest {
    const value = this.receptionForm.getRawValue();
    const shop = this.assignedShop();

    return {
      shopCode: shop.shopCode,
      expectedPickupDate: value.expectedPickupDate,
      customer: {
        id: this.toOptionalString(value.customer.id),
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
}
