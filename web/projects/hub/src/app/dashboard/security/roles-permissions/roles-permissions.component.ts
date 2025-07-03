import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-roles-permissions',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Roles & Permissions</h1>
        <p class="page-description">Define user roles and manage access permissions</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">User Roles</h2>
          <p class="card-description">Create and manage user roles</p>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Role Name</th>
                  <th>Description</th>
                  <th>Users</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Administrator</td>
                  <td>Full system access</td>
                  <td>3</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Permissions</button>
                  </td>
                </tr>
                <tr>
                  <td>Manager</td>
                  <td>Department management</td>
                  <td>8</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Permissions</button>
                  </td>
                </tr>
                <tr>
                  <td>User</td>
                  <td>Standard user access</td>
                  <td>45</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Permissions</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <button class="btn btn-primary">Create New Role</button>
        </div>
        
        <div class="card">
          <h2 class="card-title">Permission Matrix</h2>
          <p class="card-description">Configure permissions for each role</p>
          
          <div class="permission-matrix">
            <div class="permission-row">
              <span class="permission-name">User Management</span>
              <div class="permission-checkboxes">
                <label class="checkbox-label">
                  <input type="checkbox" class="checkbox" checked>
                  <span>Admin</span>
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" class="checkbox">
                  <span>Manager</span>
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" class="checkbox">
                  <span>User</span>
                </label>
              </div>
            </div>
            
            <div class="permission-row">
              <span class="permission-name">Reports Access</span>
              <div class="permission-checkboxes">
                <label class="checkbox-label">
                  <input type="checkbox" class="checkbox" checked>
                  <span>Admin</span>
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" class="checkbox" checked>
                  <span>Manager</span>
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" class="checkbox">
                  <span>User</span>
                </label>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class RolesPermissionsComponent {}
