import { Component } from '@angular/core';

@Component({
  selector: 'app-deployment',
  standalone: true,
  template: `
    <div class="deployment-section">
      <h2>Console Deployment</h2>
      <p>Deployment settings go here.</p>
    </div>
  `,
  styles: [`
    .deployment-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class DeploymentComponent {} 