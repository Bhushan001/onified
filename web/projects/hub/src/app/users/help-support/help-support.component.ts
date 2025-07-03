import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-help-support',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Help & Support</h1>
        <p class="page-description">Access help resources and contact support</p>
      </div>
      
      <div class="content-section">
        <div class="card">
          <h2 class="card-title">Quick Help</h2>
          <p class="card-description">Find answers to common questions</p>
          
          <div class="help-links">
            <a href="#" class="help-link">
              <span class="help-icon">📚</span>
              <span class="help-text">User Guide</span>
            </a>
            <a href="#" class="help-link">
              <span class="help-icon">❓</span>
              <span class="help-text">FAQ</span>
            </a>
            <a href="#" class="help-link">
              <span class="help-icon">🎥</span>
              <span class="help-text">Video Tutorials</span>
            </a>
            <a href="#" class="help-link">
              <span class="help-icon">📖</span>
              <span class="help-text">API Documentation</span>
            </a>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">Contact Support</h2>
          <p class="card-description">Get help from our support team</p>
          
          <div class="support-options">
            <div class="support-option">
              <h3>Email Support</h3>
              <p>Get help via email within 24 hours</p>
              <button class="btn btn-primary">Send Email</button>
            </div>
            
            <div class="support-option">
              <h3>Live Chat</h3>
              <p>Chat with support during business hours</p>
              <button class="btn btn-secondary">Start Chat</button>
            </div>
            
            <div class="support-option">
              <h3>Phone Support</h3>
              <p>Call us for urgent issues</p>
              <p class="phone-number">+1 (555) 123-4567</p>
            </div>
          </div>
        </div>
        
        <div class="card">
          <h2 class="card-title">System Status</h2>
          <p class="card-description">Check current system status</p>
          
          <div class="status-items">
            <div class="status-item">
              <span class="status-indicator status-operational"></span>
              <span class="status-text">API Services</span>
              <span class="status-label">Operational</span>
            </div>
            <div class="status-item">
              <span class="status-indicator status-operational"></span>
              <span class="status-text">Database</span>
              <span class="status-label">Operational</span>
            </div>
            <div class="status-item">
              <span class="status-indicator status-degraded"></span>
              <span class="status-text">Email Service</span>
              <span class="status-label">Degraded Performance</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class HelpSupportComponent {}
