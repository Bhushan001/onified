import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tenant-initialization',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Tenant Initialization</h1>
        <p class="page-description">Set up and configure new tenant environments</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Tenant Setup Wizard</h2>
          <p class="card-description">Initialize new tenant with default configurations</p>
          
          <div class="form-group">
            <label class="form-label">Tenant Name</label>
            <input type="text" class="form-input" placeholder="Enter tenant name">
          </div>
          
          <div class="form-group">
            <label class="form-label">Tenant Domain</label>
            <input type="text" class="form-input" placeholder="tenant.onified.com">
          </div>
          
          <div class="form-group">
            <label class="form-label">Initial Admin Email</label>
            <input type="email" class="form-input" placeholder="admin&#64;tenant.com">
          </div>
          
          <button class="btn btn-primary">Initialize Tenant</button>
        </div>
        
        <div class="card">
          <h2 class="card-title">Default Configurations</h2>
          <p class="card-description">Set default settings for new tenants</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable default user roles</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Setup default workflows</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox">
              <span>Enable sample data</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class TenantInitializationComponent {}