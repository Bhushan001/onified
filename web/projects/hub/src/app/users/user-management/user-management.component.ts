import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">User Management</h1>
        <p class="page-description">Manage user accounts and access controls</p>
      </div>
      
      <div class="content-section">
        <div class="card full-width">
          <h2 class="card-title">User Directory</h2>
          <p class="card-description">View and manage all user accounts</p>
          
          <div class="table-actions">
            <div class="search-box">
              <input type="text" class="form-input" placeholder="Search users...">
            </div>
            <button class="btn btn-primary">Add New User</button>
          </div>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Department</th>
                  <th>Status</th>
                  <th>Last Login</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>John Doe</td>
                  <td>john.doe&#64;company.com</td>
                  <td>Administrator</td>
                  <td>IT</td>
                  <td><span class="status-active">Active</span></td>
                  <td>2024-01-15 10:30</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Reset Password</button>
                    <button class="btn btn-sm btn-danger">Deactivate</button>
                  </td>
                </tr>
                <tr>
                  <td>Jane Smith</td>
                  <td>jane.smith&#64;company.com</td>
                  <td>Manager</td>
                  <td>Sales</td>
                  <td><span class="status-active">Active</span></td>
                  <td>2024-01-14 16:45</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Reset Password</button>
                    <button class="btn btn-sm btn-danger">Deactivate</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">User Statistics</h2>
          <p class="card-description">Overview of user metrics</p>
          
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-number">156</span>
              <span class="stat-label">Total Users</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">142</span>
              <span class="stat-label">Active Users</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">14</span>
              <span class="stat-label">Inactive Users</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class UserManagementComponent {}