import { Injectable, inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({ providedIn: 'root' })
export class ToastNotifier {
  private readonly toastr = inject(ToastrService);

  success(message: string, title = 'Erfolg'): void {
    this.toastr.success(message, title);
  }

  error(message: string, title = 'Fehler'): void {
    this.toastr.error(message, title);
  }

  info(message: string, title = 'Info'): void {
    this.toastr.info(message, title);
  }

  warning(message: string, title = 'Warnung'): void {
    this.toastr.warning(message, title);
  }
}
