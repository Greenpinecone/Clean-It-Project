import { computed, Injectable, signal } from '@angular/core';
import { DemoRole, DemoUserObject } from './demo-user-model';

@Injectable({ providedIn: 'root' })
export class DemoUser {
  private readonly currentUserState = signal<DemoUserObject>({
    name: 'Demo Employee',
    role: DemoRole.Employee,
  });

  readonly currentUser = this.currentUserState.asReadonly();
  readonly currentRole = computed(() => this.currentUser().role);
  readonly isManager = computed(() => this.currentRole() === DemoRole.Manager);
  readonly isEmployee = computed(() => this.currentRole() === DemoRole.Employee);

  setRole(role: DemoRole): void {
    this.currentUserState.update((user) => ({ ...user, role }));
  }
}
