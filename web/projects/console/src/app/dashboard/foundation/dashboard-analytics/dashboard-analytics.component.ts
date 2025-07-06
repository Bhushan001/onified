import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard-analytics',
  standalone: true,
  template: `
    <div class="dashboard-analytics">
      <h2>Console Dashboard Analytics</h2>
      <p>Analytics content goes here.</p>
    </div>
  `,
  styles: [`
    .dashboard-analytics {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class DashboardAnalyticsComponent {} 