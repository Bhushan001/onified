import { Component } from '@angular/core';

@Component({
  selector: 'app-roles-permissions',
  standalone: true,
  template: `
    <div class="roles-permissions-section">
      <h2>Workspace Roles & Permissions</h2>
      <p>Roles and permissions management goes here.</p>
    </div>
  `,
  styles: [`
    .roles-permissions-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class RolesPermissionsComponent {} 