import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ViewContainerRef, Injector } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from './services/auth.service';
import { ModernHeaderComponent } from './dashboard/modern-header/modern-header.component';
import { SidebarComponent } from './dashboard/sidebar/sidebar.component';

@Component({
  selector: 'app-dashboard-wrapper',
  standalone: true,
  imports: [
    CommonModule,
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
        [isMobile]="isMobile"
        (toggleSidebar)="onToggleSidebar()"
        (routeChange)="onRouteChange($event)">
      </app-sidebar>

      <!-- Main Content -->
      <main class="dashboard-main" [class.sidebar-collapsed]="sidebarCollapsed">
        <ng-container #contentContainer></ng-container>
      </main>
    </div>
  `,
  styles: [`
    .modern-dashboard {
      display: flex;
      flex-direction: column;
      height: 100vh;
      background-color: #f8f9fa;
      position: relative;
    }

    .dashboard-main {
      flex: 1;
      padding: 20px;
      margin-left: 280px;
      transition: margin-left 0.3s ease;
      overflow-y: auto;
    }

    .dashboard-main.sidebar-collapsed {
      margin-left: 80px;
    }

    /* Sidebar styles for micro-frontend */
    :host ::ng-deep .sidebar {
      width: 240px;
      height: calc(100vh - 70px);
      background: white;
      border-right: 1px solid #e5e7eb;
      position: fixed;
      left: 12px;
      top: 60px;
      z-index: 900;
      display: flex;
      flex-direction: column;
      transition: all 0.3s ease;
      overflow-y: auto;
    }

    :host ::ng-deep .sidebar.collapsed {
      width: 60px;
    }

    /* Mobile sidebar styles */
    @media (max-width: 1024px) {
      :host ::ng-deep .sidebar {
        transform: translateX(-100%);
      }

      :host ::ng-deep .sidebar:not(.collapsed) {
        transform: translateX(0);
      }
    }

    @media (max-width: 768px) {
      .dashboard-main {
        margin-left: 0;
      }

      :host ::ng-deep .sidebar {
        width: 240px;
        height: calc(100vh - 70px);
        transform: translateX(-100%);
        z-index: 1100;
      }

      :host ::ng-deep .sidebar.mobile-open {
        transform: translateX(0);
      }

      :host ::ng-deep .sidebar.collapsed {
        transform: translateX(-100%);
      }
    }

    /* Sidebar overlay for mobile */
    :host ::ng-deep .sidebar-overlay {
      position: fixed;
      top: 70px;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      z-index: 899;
    }
  `]
})
export class DashboardWrapperComponent implements OnInit {
  @ViewChild('contentContainer', { read: ViewContainerRef, static: true }) contentContainer!: ViewContainerRef;
  
  currentUser: any = {
    name: 'Admin User',
    email: 'admin@example.com',
    role: 'Administrator'
  };
  sidebarCollapsed: boolean = false;
  mobileSidebarOpen: boolean = false;
  windowWidth: number = window.innerWidth;
  isMobile: boolean = window.innerWidth <= 768;
  currentRoute: string = 'dashboard/foundation/dashboard-analytics';

  constructor(
    private injector: Injector,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log(this.authService.isAuthenticated());
    // Check if mobile and collapse sidebar by default
    if (window.innerWidth <= 1024) {
      this.sidebarCollapsed = true;
    }
    
    // Update window width and mobile state on resize
    window.addEventListener('resize', () => {
      this.windowWidth = window.innerWidth;
      this.isMobile = window.innerWidth <= 768;
    });

    // Initialize with default component
    this.loadComponent();
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
    // Call the auth service logout method
    this.authService.logout().subscribe();
  }

  onRouteChange(route: string): void {
    this.currentRoute = route;
    this.loadComponent();
    
    // Update browser URL without triggering navigation
    if (this.isMicroFrontendMode()) {
      const currentUrl = this.router.url;
      const baseUrl = currentUrl.split('/host/console')[0] + '/host/console';
      const newUrl = baseUrl + route;
      window.history.pushState({}, '', newUrl);
    }
  }

  private loadComponent(): void {
    if (this.contentContainer) {
      this.contentContainer.clear();
      // Placeholder: load a default component or content
      // In a real app, you would dynamically load the component for the current route
    }
  }

  private isMicroFrontendMode(): boolean {
    return window.location.href.includes('/host/console');
  }
} 