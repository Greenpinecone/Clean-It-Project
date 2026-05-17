import { CurrencyPipe, DatePipe, NgClass, TitleCasePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  input,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { debounceTime, finalize, Subscription } from 'rxjs';

import { ToastNotifier } from '@core/notifications/toast-notifier';
import { AssignedShopResponse } from '../data-access/dto/assigned-shop-response';
import { OrderApi } from '../data-access/api/order-api';
import { OrderStatus, ORDER_STATUSES } from '../models/order-status';
import { OrderSummaryResponse } from '../data-access/dto/order-summary-response';
import {
  applyServerValidationErrors,
  clearServerValidationErrors,
} from '@core/forms/server-valdiation-errors';
import { getApiValidationErrors } from '@core/api-errors/api-problem';

type OrderFilterForm = FormGroup<{
  query: FormControl<string>;
  status: FormControl<OrderStatus | ''>;
}>;

@Component({
  selector: 'app-manager-order-overview',
  imports: [
    CurrencyPipe,
    DatePipe,
    NgbPaginationModule,
    ReactiveFormsModule,
    TitleCasePipe,
    NgClass,
  ],
  templateUrl: './manager-order-overview.html',
  styleUrl: './manager-order-overview.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagerOrderOverview implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly orderApi = inject(OrderApi);
  private readonly toastNotifier = inject(ToastNotifier);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly assignedShop = input.required<AssignedShopResponse>();

  protected readonly pageSize = 10;
  protected readonly statusOptions = ORDER_STATUSES;
  protected readonly orders = signal<OrderSummaryResponse[]>([]);
  protected readonly page = signal(1);
  protected readonly totalItems = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly isOrdersLoading = signal(false);
  protected readonly ordersLoadErrorMessage = signal<string | null>(null);
  protected readonly updatingOrderId = signal<string | null>(null);
  protected readonly hasOrders = computed(() => this.orders().length > 0);
  private ordersRequestSub?: Subscription;

  protected readonly filtersForm: OrderFilterForm = this.fb.group({
    query: ['', [Validators.maxLength(100)]],
    status: this.fb.control<OrderStatus | ''>(''),
  });

  private readonly allowedStatusTransitions: Record<OrderStatus, readonly OrderStatus[]> = {
    [OrderStatus.Received]: [OrderStatus.InCleaning, OrderStatus.Cancelled],
    [OrderStatus.InCleaning]: [OrderStatus.ReadyForPickup, OrderStatus.Cancelled],
    [OrderStatus.ReadyForPickup]: [OrderStatus.Returned, OrderStatus.Cancelled],
    [OrderStatus.Returned]: [],
    [OrderStatus.Cancelled]: [],
  };

  constructor() {
    this.filtersForm.valueChanges
      .pipe(debounceTime(300), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.page.set(1);
        this.loadOrders();
      });
  }

  ngOnInit(): void {
    this.page.set(1);
    this.loadOrders();
  }

  protected onPageChanged(page: number): void {
    this.page.set(page);
    this.loadOrders();
  }

  protected clearFilters(): void {
    if (!this.query.value.trim() && !this.status.value) {
      return;
    }

    this.filtersForm.reset();
  }

  protected getAllowedTargetStatuses(status: OrderStatus): readonly OrderStatus[] {
    return this.allowedStatusTransitions[status];
  }

  protected changeStatus(order: OrderSummaryResponse, targetStatusValue: string): void {
    if (this.updatingOrderId() !== null) {
      return;
    }

    if (!this.isOrderStatus(targetStatusValue) || targetStatusValue === order.status) {
      return;
    }

    this.updatingOrderId.set(order.id);

    this.orderApi
      .changeOrderStatus(order.id, {
        targetStatus: targetStatusValue,
      })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.updatingOrderId.set(null)),
      )
      .subscribe({
        next: (updatedOrder) => {
          this.orders.update((orders) =>
            orders.map((currentOrder) =>
              currentOrder.id === updatedOrder.id ? updatedOrder : currentOrder,
            ),
          );

          this.toastNotifier.success(`Order ${order.orderNumber} was updated.`);
        },
      });
  }

  protected onStatusSelectChanged(
    order: OrderSummaryResponse,
    statusSelect: HTMLSelectElement,
  ): void {
    this.changeStatus(order, statusSelect.value);
    statusSelect.value = '';
  }

  protected toDisplayLabel(value: string): string {
    return value.replaceAll('_', ' ').toLowerCase();
  }

  private loadOrders(): void {
    this.ordersRequestSub?.unsubscribe();
    clearServerValidationErrors(this.filtersForm);

    if (this.filtersForm.invalid) {
      this.filtersForm.markAllAsTouched();
      return;
    }

    const shop = this.assignedShop();
    const filters = this.filtersForm.getRawValue();

    this.isOrdersLoading.set(true);
    this.ordersLoadErrorMessage.set(null);

    this.ordersRequestSub = this.orderApi
      .searchOrders({
        shopCode: shop.shopCode,
        query: filters.query?.trim(),
        status: filters.status || undefined,
        page: this.page() - 1,
        size: this.pageSize,
      })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isOrdersLoading.set(false)),
      )
      .subscribe({
        next: (response) => {
          this.orders.set(response.items);
          this.totalItems.set(response.totalItems);
          this.totalPages.set(response.totalPages);
        },
        error: (error: unknown) => {
          console.log(error);
          const validationErrors = getApiValidationErrors(error);

          if (validationErrors.length > 0) {
            applyServerValidationErrors(this.filtersForm, validationErrors, (message) => {
              this.toastNotifier.error(message);
            });
            return;
          }

          this.orders.set([]);
          this.totalItems.set(0);
          this.totalPages.set(0);
          this.ordersLoadErrorMessage.set('Orders could not be loaded.');
        },
      });
  }

  private isOrderStatus(value: string): value is OrderStatus {
    return ORDER_STATUSES.includes(value as OrderStatus);
  }

  protected canChangeStatus(status: OrderStatus): boolean {
    return this.getAllowedTargetStatuses(status).length > 0;
  }

  protected readonly statusBadgeClasses: Record<OrderStatus, string> = {
    [OrderStatus.Received]: 'text-bg-secondary',
    [OrderStatus.InCleaning]: 'text-bg-primary',
    [OrderStatus.ReadyForPickup]: 'text-bg-success',
    [OrderStatus.Returned]: 'text-bg-dark',
    [OrderStatus.Cancelled]: 'text-bg-danger',
  };

  protected get query(): FormControl<string> {
    return this.filtersForm.controls.query;
  }

  protected get status(): FormControl<OrderStatus | ''> {
    return this.filtersForm.controls.status;
  }
}
