import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-naming-conventions',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Naming Conventions</h1>
        <p class="page-description">Define and manage naming standards across the application</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Entity Naming Rules</h2>
          <p class="card-description">Configure naming patterns for different entities</p>
          
          <div class="form-group">
            <label class="form-label">User ID Format</label>
            <input type="text" class="form-input" value="USR-{YYYY}-{####}" placeholder="USR-{YYYY}-{####}">
          </div>
          
          <div class="form-group">
            <label class="form-label">Project Code Format</label>
            <input type="text" class="form-input" value="PRJ-{###}" placeholder="PRJ-{###}">
          </div>
          
          <div class="form-group">
            <label class="form-label">Document Reference Format</label>
            <input type="text" class="form-input" value="DOC-{YYYY}-{MM}-{####}" placeholder="DOC-{YYYY}-{MM}-{####}">
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Validation Rules</h2>
          <p class="card-description">Set validation criteria for naming</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Enforce uppercase for codes</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Validate unique identifiers</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox">
              <span>Allow special characters</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['../../../shared/page-styles.scss']
})
export class NamingConventionsComponent {}
