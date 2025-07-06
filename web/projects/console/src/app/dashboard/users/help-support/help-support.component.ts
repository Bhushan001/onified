import { Component } from '@angular/core';

@Component({
  selector: 'app-help-support',
  standalone: true,
  template: `
    <div class="help-support-section">
      <h2>Console Help & Support</h2>
      <p>Help and support content goes here.</p>
    </div>
  `,
  styles: [`
    .help-support-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class HelpSupportComponent {} 