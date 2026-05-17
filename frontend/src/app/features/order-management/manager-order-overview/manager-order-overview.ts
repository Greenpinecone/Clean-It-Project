import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { AssignedShopResponse } from '../data-access/dto/assigned-shop-response';

@Component({
  selector: 'app-manager-order-overview',
  templateUrl: './manager-order-overview.html',
  styleUrl: './manager-order-overview.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagerOrderOverview {
  private readonly route = inject(ActivatedRoute);

  protected readonly assignedShop = this.route.snapshot.data['assignedShop'] as AssignedShopResponse;
}
