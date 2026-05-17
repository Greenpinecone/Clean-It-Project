export enum DemoRole {
  Employee = 'EMPLOYEE',
  Manager = 'MANAGER',
}

export const DEMO_ROLES: readonly DemoRole[] = Object.values(DemoRole);

export interface DemoUserObject {
  name: string;
  role: DemoRole;
}
