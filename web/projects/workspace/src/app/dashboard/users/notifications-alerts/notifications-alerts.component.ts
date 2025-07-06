import { Component } from '@angular/core';

@Component({
  selector: 'app-notifications-alerts',
  standalone: true,
  template: `
    <div class="notifications-alerts-section">
      <h2>Workspace Notifications & Alerts</h2>
      <p>Notifications and alerts management goes here.</p>
    </div>
  `,
  styles: [`
    .notifications-alerts-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class NotificationsAlertsComponent {} 