import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-identity-access',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Identity & Access</h1>
        <p class="page-description">Manage authentication and access control settings</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Authentication Settings</h2>
          <p class="card-description">Configure how users authenticate</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable two-factor authentication</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox">
              <span>Require strong passwords</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="form-label">Session Timeout (minutes)</label>
            <input type="number" class="form-input" value="30" min="5" max="480">
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Access Control</h2>
          <p class="card-description">Define access policies and restrictions</p>
          
          <div class="form-group">
            <label class="form-label">Default User Role</label>
            <select class="form-select">
              <option>Viewer</option>
              <option>User</option>
              <option>Editor</option>
              <option>Admin</option>
            </select>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable IP restrictions</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class IdentityAccessComponent {}
