import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { MAIN_NAVIGATION } from '../main-navigation';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, NgbCollapseModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  protected readonly isMenuCollapsed = signal(true);
  protected readonly mainNavItems = MAIN_NAVIGATION;

  protected closeMobileMenu(): void {
    this.isMenuCollapsed.set(true);
  }

  protected toggleMobileMenu(): void {
    this.isMenuCollapsed.update((collapsed) => !collapsed);
  }
}
