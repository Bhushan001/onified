import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-compliance',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Compliance</h1>
        <p class="page-description">Manage regulatory compliance and data protection settings</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Data Protection</h2>
          <p class="card-description">Configure GDPR and privacy compliance</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable GDPR compliance mode</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Require cookie consent</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="form-label">Data Retention Period (days)</label>
            <input type="number" class="form-input" value="365" min="1">
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Audit Trail</h2>
          <p class="card-description">Configure audit logging and compliance reporting</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable audit logging</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Log user access events</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox">
              <span>Generate compliance reports</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class ComplianceComponent {}
