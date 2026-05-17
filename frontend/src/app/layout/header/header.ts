import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { NgbCollapseModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { DEMO_ROLES, DemoRole } from '@core/auth/demo-user-model';
import { DemoUser } from '@core/auth/demo-user';
import { MAIN_NAVIGATION, MANAGER_NAVIGATION } from '../main-navigation';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, TitleCasePipe, NgbCollapseModule, NgbDropdownModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  private readonly demoUserService = inject(DemoUser);
  private readonly router = inject(Router);

  protected readonly demoRoles = DEMO_ROLES;
  protected readonly isMenuCollapsed = signal(true);
  protected readonly mainNavItems = MAIN_NAVIGATION;
  protected readonly currentRole = this.demoUserService.currentRole;
  protected readonly managerNavItems = computed(() =>
    MANAGER_NAVIGATION.filter((item) => !item.roles || item.roles.includes(this.currentRole())),
  );
  protected readonly showManagerMenu = computed(() => this.managerNavItems().length > 0);

  protected changeRole(role: DemoRole): void {
    this.demoUserService.setRole(role);
    this.closeMobileMenu();

    if (role === DemoRole.Employee && this.router.url.startsWith('/manager')) {
      void this.router.navigateByUrl('/orders/create');
    }
  }

  protected closeMobileMenu(): void {
    this.isMenuCollapsed.set(true);
  }

  protected toggleMobileMenu(): void {
    this.isMenuCollapsed.update((collapsed) => !collapsed);
  }
}
