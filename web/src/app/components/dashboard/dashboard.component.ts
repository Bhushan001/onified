import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.models';
import { ModernHeaderComponent } from './modern-header/modern-header.component';
import { MetricsCardsComponent } from './metrics-cards/metrics-cards.component';
import { ChartsComponent } from './charts/charts.component';
import { DataTableComponent } from './data-table/data-table.component';

/**
 * Main Dashboard Component for Onified.ai Admin Panel
 * 
 * This component serves as the main dashboard layout after successful authentication.
 * It provides a comprehensive admin interface with sidebar navigation, metrics cards,
 * charts, and data tables for managing the application.
 * 
 * Features:
 * - Responsive sidebar navigation
 * - Real-time metrics display
 * - Interactive charts and graphs
 * - Data management tables
 * - User profile management
 * - Role-based access control
 * 
 * @component DashboardComponent
 * @implements OnInit
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    ModernHeaderComponent,
    MetricsCardsComponent,
    ChartsComponent,
    DataTableComponent
  ],
  template: `
    <div class="modern-dashboard">
      <!-- Modern Header -->
      <app-modern-header 
        [currentUser]="currentUser"
        (tabChange)="onTabChange($event)">
      </app-modern-header>

      <!-- Main Content -->
      <main class="dashboard-main">
        <div class="dashboard-container">
          <!-- Page Title Section -->
          <div class="page-header">
            <div class="page-title-section">
              <h1 class="page-title">Core Strategy</h1>
              <p class="page-subtitle">Monitor your business performance and key metrics</p>
            </div>
            <div class="page-actions">
              <button class="action-btn secondary">
                <i class="icon-download">📥</i>
                Export
              </button>
              <button class="action-btn primary">
                <i class="icon-plus">+</i>
                Add New
              </button>
            </div>
          </div>

          <!-- Metrics Cards Section -->
          <app-metrics-cards></app-metrics-cards>

          <!-- Charts Section -->
          <app-charts></app-charts>

          <!-- Data Table Section -->
          <div class="data-section">
            <app-data-table></app-data-table>
          </div>
        </div>
      </main>
    </div>
  `,
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  activeTab: string = 'core-strategy';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is authenticated
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    // Get current user information
    this.currentUser = this.authService.getCurrentUser();
    
    // Subscribe to user changes
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  onTabChange(tabId: any): void {
    this.activeTab = tabId;
    // Handle tab change logic here
    console.log('Active tab changed to:', tabId);
  }
}