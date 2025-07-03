import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-licensing-billing',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Licensing & Billing</h1>
        <p class="page-description">Manage software licenses and billing configurations</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Current License</h2>
          <p class="card-description">View your current license details</p>
          
          <div class="license-info">
            <div class="info-row">
              <span class="info-label">License Type:</span>
              <span class="info-value">Enterprise</span>
            </div>
            <div class="info-row">
              <span class="info-label">Users Allowed:</span>
              <span class="info-value">100</span>
            </div>
            <div class="info-row">
              <span class="info-label">Expiry Date:</span>
              <span class="info-value">December 31, 2024</span>
            </div>
            <div class="info-row">
              <span class="info-label">Status:</span>
              <span class="info-value status-active">Active</span>
            </div>
          </div>
          
          <button class="btn btn-primary">Upgrade License</button>
          <button class="btn btn-secondary">Renew License</button>
        </div>
        
        <div class="card">
          <h2 class="card-title">Billing Settings</h2>
          <p class="card-description">Configure billing and payment options</p>
          
          <div class="form-group">
            <label class="form-label">Billing Cycle</label>
            <select class="form-select">
              <option>Monthly</option>
              <option>Quarterly</option>
              <option>Annually</option>
            </select>
          </div>
          
          <div class="form-group">
            <label class="form-label">Payment Method</label>
            <select class="form-select">
              <option>Credit Card</option>
              <option>Bank Transfer</option>
              <option>Invoice</option>
            </select>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Auto-renewal enabled</span>
            </label>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Billing History</h2>
          <p class="card-description">View past invoices and payments</p>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Invoice #</th>
                  <th>Date</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>INV-2024-001</td>
                  <td>2024-01-01</td>
                  <td>$299.00</td>
                  <td><span class="status-paid">Paid</span></td>
                  <td><button class="btn btn-sm">Download</button></td>
                </tr>
                <tr>
                  <td>INV-2023-012</td>
                  <td>2023-12-01</td>
                  <td>$299.00</td>
                  <td><span class="status-paid">Paid</span></td>
                  <td><button class="btn btn-sm">Download</button></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class LicensingBillingComponent {}
