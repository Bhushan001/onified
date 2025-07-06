import { Component } from '@angular/core';

@Component({
  selector: 'app-setup-wizard',
  standalone: true,
  template: `
    <div class="setup-wizard-section">
      <h2>Workspace Setup Wizard</h2>
      <p>Setup wizard content goes here.</p>
    </div>
  `,
  styles: [`
    .setup-wizard-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class SetupWizardComponent {} 