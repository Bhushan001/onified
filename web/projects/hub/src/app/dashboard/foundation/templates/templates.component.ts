import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-templates',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Templates</h1>
        <p class="page-description">Manage document and email templates</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Email Templates</h2>
          <p class="card-description">Configure automated email templates</p>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Template Name</th>
                  <th>Type</th>
                  <th>Last Modified</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Welcome Email</td>
                  <td>User Onboarding</td>
                  <td>2024-01-10</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Preview</button>
                  </td>
                </tr>
                <tr>
                  <td>Password Reset</td>
                  <td>Security</td>
                  <td>2024-01-08</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm">Preview</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <button class="btn btn-primary">Create New Template</button>
        </div>
        
        <div class="card">
          <h2 class="card-title">Document Templates</h2>
          <p class="card-description">Manage document generation templates</p>
          
          <div class="form-group">
            <label class="form-label">Default Document Format</label>
            <select class="form-select">
              <option>PDF</option>
              <option>Word Document</option>
              <option>HTML</option>
            </select>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Include company branding</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class TemplatesComponent {}
