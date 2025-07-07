import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-org-structure',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Organization Structure</h1>
        <p class="page-description">Define and manage your organizational hierarchy</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Departments</h2>
          <p class="card-description">Create and manage organizational departments</p>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Department Name</th>
                  <th>Manager</th>
                  <th>Members</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Engineering</td>
                  <td>John Doe</td>
                  <td>25</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm btn-danger">Delete</button>
                  </td>
                </tr>
                <tr>
                  <td>Marketing</td>
                  <td>Jane Smith</td>
                  <td>12</td>
                  <td>
                    <button class="btn btn-sm">Edit</button>
                    <button class="btn btn-sm btn-danger">Delete</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <button class="btn btn-primary">Add Department</button>
        </div>
        
        <div class="card">
          <h2 class="card-title">Hierarchy Settings</h2>
          <p class="card-description">Configure organizational hierarchy rules</p>
          
          <div class="form-group">
            <label class="form-label">Maximum Hierarchy Levels</label>
            <input type="number" class="form-input" value="5" min="1" max="10">
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enable cross-department collaboration</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class OrgStructureComponent {}
