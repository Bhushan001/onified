import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-branding',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Branding</h1>
        <p class="page-description">Customize your application's visual identity and branding</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Logo & Identity</h2>
          <p class="card-description">Upload and configure your brand assets</p>
          
          <div class="form-group">
            <label class="form-label">Company Logo</label>
            <div class="file-upload">
              <button class="btn btn-secondary">Upload Logo</button>
              <span class="file-info">PNG, JPG up to 2MB</span>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">Favicon</label>
            <div class="file-upload">
              <button class="btn btn-secondary">Upload Favicon</button>
              <span class="file-info">ICO, PNG 32x32px</span>
            </div>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Color Scheme</h2>
          <p class="card-description">Define your brand colors</p>
          
          <div class="color-grid">
            <div class="form-group">
              <label class="form-label">Primary Color</label>
              <input type="color" class="color-input" value="#6366f1">
            </div>
            
            <div class="form-group">
              <label class="form-label">Secondary Color</label>
              <input type="color" class="color-input" value="#8b5cf6">
            </div>
            
            <div class="form-group">
              <label class="form-label">Accent Color</label>
              <input type="color" class="color-input" value="#10b981">
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class BrandingComponent {}
