import { DemoRole } from '@core/auth/demo-user-model';

export interface NavItem {
  label: string;
  route: string;
  roles?: DemoRole[];
}
