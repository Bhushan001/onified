import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notifications-alerts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Notifications & Alerts</h1>
        <p class="page-description">Configure notification settings and alert preferences</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Email Notifications</h2>
          <p class="card-description">Configure email notification preferences</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>System alerts</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Security notifications</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox">
              <span>Weekly reports</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox">
              <span>Marketing updates</span>
            </label>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Push Notifications</h2>
          <p class="card-description">Configure browser and mobile push notifications</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable browser notifications</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox">
              <span>Enable mobile notifications</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="form-label">Notification Frequency</label>
            <select class="form-select">
              <option>Immediate</option>
              <option>Hourly digest</option>
              <option>Daily digest</option>
            </select>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Alert Rules</h2>
          <p class="card-description">Configure automated alert conditions</p>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Alert Name</th>
                  <th>Condition</th>
                  <th>Recipients</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>High CPU Usage</td>
                  <td>CPU > 80%</td>
                  <td>IT Team</td>
                  <td><span class="status-active">Active</span></td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Test</button>
                  </td>
                </tr>
                <tr>
                  <td>Failed Login Attempts</td>
                  <td>Failed logins > 5</td>
                  <td>Security Team</td>
                  <td><span class="status-active">Active</span></td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Test</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <button class="btn btn-primary">Create New Alert</button>
        </div>
      </div>
    </div>
  `,
})
export class NotificationsAlertsComponent {}
