import { Component } from '@angular/core';

@Component({
  selector: 'app-user-management',
  standalone: true,
  template: `
    <div class="user-management-section">
      <h2>Console User Management</h2>
      <p>User management goes here.</p>
    </div>
  `,
  styles: [`
    .user-management-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class UserManagementComponent {} 