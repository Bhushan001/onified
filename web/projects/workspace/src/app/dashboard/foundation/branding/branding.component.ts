import { Component } from '@angular/core';

@Component({
  selector: 'app-branding',
  standalone: true,
  template: `
    <div class="branding-section">
      <h2>Workspace Branding</h2>
      <p>Branding settings go here.</p>
    </div>
  `,
  styles: [`
    .branding-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class BrandingComponent {} 