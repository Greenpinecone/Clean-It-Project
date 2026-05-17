import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-page-placeholder',
  templateUrl: './page-placeholder.html',
  styleUrl: './page-placeholder.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PagePlaceholder {
  protected readonly heading = input.required<string>();
  protected readonly badge = input.required<string>();
  protected readonly description = input.required<string>();
}
