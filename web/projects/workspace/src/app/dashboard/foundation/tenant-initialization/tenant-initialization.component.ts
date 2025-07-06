import { Component } from '@angular/core';

@Component({
  selector: 'app-tenant-initialization',
  standalone: true,
  template: `
    <div class="tenant-initialization-section">
      <h2>Workspace Tenant Initialization</h2>
      <p>Tenant initialization content goes here.</p>
    </div>
  `,
  styles: [`
    .tenant-initialization-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class TenantInitializationComponent {} 