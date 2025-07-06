import { Component } from '@angular/core';

@Component({
  selector: 'app-templates',
  standalone: true,
  template: `
    <div class="templates-section">
      <h2>Console Templates</h2>
      <p>Templates management goes here.</p>
    </div>
  `,
  styles: [`
    .templates-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class TemplatesComponent {} 