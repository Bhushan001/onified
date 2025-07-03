import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-feedback-settings',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Feedback Settings</h1>
        <p class="page-description">Configure user feedback collection and management</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Feedback Collection</h2>
          <p class="card-description">Configure how feedback is collected from users</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable feedback widget</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Show feedback button in header</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox">
              <span>Require user authentication for feedback</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="form-label">Feedback Categories</label>
            <div class="tag-list">
              <span class="tag">Bug Report</span>
              <span class="tag">Feature Request</span>
              <span class="tag">General Feedback</span>
              <span class="tag">Complaint</span>
              <button class="btn btn-sm">Add Category</button>
            </div>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Recent Feedback</h2>
          <p class="card-description">View and manage user feedback</p>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>User</th>
                  <th>Category</th>
                  <th>Rating</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>2024-01-15</td>
                  <td>John Doe</td>
                  <td>Feature Request</td>
                  <td>⭐⭐⭐⭐⭐</td>
                  <td><span class="status-pending">Pending</span></td>
                  <td>
                    <button class="btn btn-sm">View</button>
                    <button class="btn btn-sm">Respond</button>
                  </td>
                </tr>
                <tr>
                  <td>2024-01-14</td>
                  <td>Jane Smith</td>
                  <td>Bug Report</td>
                  <td>⭐⭐⭐</td>
                  <td><span class="status-resolved">Resolved</span></td>
                  <td>
                    <button class="btn btn-sm">View</button>
                    <button class="btn btn-sm">Respond</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Feedback Analytics</h2>
          <p class="card-description">View feedback statistics and trends</p>
          
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-number">4.2</span>
              <span class="stat-label">Average Rating</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">156</span>
              <span class="stat-label">Total Feedback</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">89%</span>
              <span class="stat-label">Response Rate</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class FeedbackSettingsComponent {}
