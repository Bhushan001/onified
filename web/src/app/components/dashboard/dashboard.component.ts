import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.models';
import { ModernHeaderComponent } from './modern-header/modern-header.component';
import { SidebarComponent } from './sidebar/sidebar.component';

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
    RouterOutlet,
    ModernHeaderComponent,
    SidebarComponent
  ],
  template: `
    <div class="modern-dashboard">
      <!-- Modern Header -->
      <app-modern-header 
        [currentUser]="currentUser"
        (logout)="onLogout()"
        (toggleSidebar)="onToggleSidebar()">
      </app-modern-header>

      <!-- Sidebar -->
      <app-sidebar 
        [currentUser]="currentUser"
        [isCollapsed]="sidebarCollapsed"
        [isMobileOpen]="mobileSidebarOpen"
        (toggleSidebar)="onToggleSidebar()">
      </app-sidebar>

      <!-- Main Content -->
      <main class="dashboard-main" [class.sidebar-collapsed]="sidebarCollapsed">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  sidebarCollapsed: boolean = false;
  mobileSidebarOpen: boolean = false;

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

    // Check if mobile and collapse sidebar by default
    if (window.innerWidth <= 1024) {
      this.sidebarCollapsed = true;
    }
  }

  onToggleSidebar(): void {
    if (window.innerWidth <= 768) {
      // Mobile: toggle mobile sidebar
      this.mobileSidebarOpen = !this.mobileSidebarOpen;
    } else {
      // Desktop: toggle collapsed state
      this.sidebarCollapsed = !this.sidebarCollapsed;
    }
  }

  onLogout(): void {
    this.authService.clearAuthData();
    this.router.navigate(['/login']);
    this.authService.logout().subscribe({ error: () => {} });
  }
}