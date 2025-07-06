import { Component } from '@angular/core';

@Component({
  selector: 'app-localization',
  standalone: true,
  template: `
    <div class="localization-section">
      <h2>Workspace Localization</h2>
      <p>Localization settings go here.</p>
    </div>
  `,
  styles: [`
    .localization-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class LocalizationComponent {} 