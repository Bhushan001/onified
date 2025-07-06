import { Component } from '@angular/core';

@Component({
  selector: 'app-feedback-settings',
  standalone: true,
  template: `
    <div class="feedback-settings-section">
      <h2>Workspace Feedback Settings</h2>
      <p>Feedback settings management goes here.</p>
    </div>
  `,
  styles: [`
    .feedback-settings-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class FeedbackSettingsComponent {} 