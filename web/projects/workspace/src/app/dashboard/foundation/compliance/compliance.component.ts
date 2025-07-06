import { Component } from '@angular/core';

@Component({
  selector: 'app-compliance',
  standalone: true,
  template: `
    <div class="compliance-section">
      <h2>Workspace Compliance</h2>
      <p>Compliance settings go here.</p>
    </div>
  `,
  styles: [`
    .compliance-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class ComplianceComponent {} 