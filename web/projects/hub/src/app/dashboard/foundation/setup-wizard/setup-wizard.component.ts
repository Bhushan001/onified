import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-setup-wizard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Setup Wizard</h1>
        <p class="page-description">Guide users through initial application setup</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Wizard Configuration</h2>
          <p class="card-description">Configure the setup wizard flow</p>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Show wizard on first login</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" class="checkbox" checked>
              <span>Allow skipping optional steps</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="form-label">Wizard Steps</label>
            <div class="wizard-steps">
              <div class="step-item">
                <span class="step-number">1</span>
                <span class="step-title">Welcome & Introduction</span>
                <button class="btn btn-sm">Configure</button>
              </div>
              <div class="step-item">
                <span class="step-number">2</span>
                <span class="step-title">Basic Settings</span>
                <button class="btn btn-sm">Configure</button>
              </div>
              <div class="step-item">
                <span class="step-number">3</span>
                <span class="step-title">User Preferences</span>
                <button class="btn btn-sm">Configure</button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Preview Wizard</h2>
          <p class="card-description">Test the setup wizard flow</p>
          
          <button class="btn btn-primary">Launch Preview</button>
          <button class="btn btn-secondary">Reset Wizard Data</button>
        </div>
      </div>
    </div>
  `,
})
export class SetupWizardComponent {}
