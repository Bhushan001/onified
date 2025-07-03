import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-hub-dashboard',
  standalone: true,
  imports: [CommonModule, DashboardComponent],
  template: `
    <div class="hub-dashboard-container">
      <app-dashboard></app-dashboard>
    </div>
  `,
  styles: [`
    .hub-dashboard-container {
      width: 100%;
      height: 100%;
    }
  `]
})
export class HubDashboardComponent implements OnInit {
  title = 'hub-dashboard';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // When loaded as micro-frontend, we might need to handle auth differently
    // For now, let's just ensure the service is available
  }
} 