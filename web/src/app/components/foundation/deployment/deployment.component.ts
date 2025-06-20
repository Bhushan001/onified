import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-deployment',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Deployment</h1>
        <p class="page-description">Manage application deployment and environment configurations</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Environment Configuration</h2>
          <p class="card-description">Configure deployment environments</p>
          
          <div class="form-group">
            <label class="form-label">Current Environment</label>
            <select class="form-select">
              <option>Development</option>
              <option>Staging</option>
              <option>Production</option>
            </select>
          </div>
          
          <div class="form-group">
            <label class="form-label">API Base URL</label>
            <input type="url" class="form-input" placeholder="https://api.example.com">
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable debug mode</span>
            </label>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Deployment History</h2>
          <p class="card-description">View recent deployments</p>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Version</th>
                  <th>Environment</th>
                  <th>Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>v1.2.3</td>
                  <td>Production</td>
                  <td>2024-01-15</td>
                  <td><span class="status-success">Success</span></td>
                </tr>
                <tr>
                  <td>v1.2.2</td>
                  <td>Staging</td>
                  <td>2024-01-14</td>
                  <td><span class="status-success">Success</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['../../../shared/page-styles.scss']
})
export class DeploymentComponent {}