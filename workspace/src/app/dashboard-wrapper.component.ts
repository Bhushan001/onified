import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ViewContainerRef, Injector, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from './services/auth.service';
import { UserManagementComponent } from './users/user-management/user-management.component';
import { NotificationsAlertsComponent } from './users/notifications-alerts/notifications-alerts.component';
import { HelpSupportComponent } from './users/help-support/help-support.component';
import { FeedbackSettingsComponent } from './users/feedback-settings/feedback-settings.component';
import { DashboardComponent } from './dashboard/dashboard.component';

@Component({
  selector: 'app-dashboard-wrapper',
  standalone: false,
  template: `
    <app-dashboard></app-dashboard>
  `
})
export class DashboardWrapperComponent implements OnInit, OnDestroy {
  @ViewChild('contentContainer', { read: ViewContainerRef, static: true }) contentContainer!: ViewContainerRef;
  
  currentUser: any = null;
  sidebarCollapsed: boolean = false;
  mobileSidebarOpen: boolean = false;
  windowWidth: number = window.innerWidth;
  isMobile: boolean = window.innerWidth <= 768;
  currentRoute: string = 'dashboard';
  private styleElement: HTMLStyleElement | null = null;

  private componentMap: { [key: string]: any } = {
    'dashboard': DashboardComponent,
  };

  constructor(
    private injector: Injector,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log('Workspace DashboardWrapperComponent initialized');
    
    // Inject workspace styles dynamically
    this.injectWorkspaceStyles();
    
    // Check if user is authenticated
    if (!this.authService.isAuthenticated()) {
      console.log('User not authenticated, redirecting to login');
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
    
    // Update window width and mobile state on resize
    window.addEventListener('resize', () => {
      this.windowWidth = window.innerWidth;
      this.isMobile = window.innerWidth <= 768;
    });

    // Initialize with default component
    this.loadComponent();
  }

  ngOnDestroy(): void {
    // Clean up injected styles
    this.removeWorkspaceStyles();
  }

  private injectWorkspaceStyles(): void {
    // Create a style element to inject workspace styles
    this.styleElement = document.createElement('style');
    this.styleElement.id = 'workspace-app-styles';
    this.styleElement.textContent = `
      /* Workspace App Styles */
      .workspace-app {
        font-family: 'Roboto', Arial, sans-serif !important;
      }
      
      .workspace-app * {
        box-sizing: border-box;
      }
      
      .workspace-app .page-container {
        padding: 24px;
        max-width: 1200px;
        margin: 0 auto;
      }
      
      .workspace-app .page-header {
        margin-bottom: 32px;
        padding-bottom: 16px;
        border-bottom: 1px solid #e5e7eb;
      }
      
      .workspace-app .page-title {
        font-size: 1.6875rem;
        font-weight: 700;
        color: #111827;
        margin: 0 0 8px 0;
        line-height: 1.2;
      }
      
      .workspace-app .page-description {
        font-size: 0.9rem;
        color: #6b7280;
        margin: 0;
        line-height: 1.5;
      }
      
      .workspace-app .content-section {
        display: grid;
        gap: 24px;
        grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      }
      
      .workspace-app .card {
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 12px;
        padding: 24px;
        box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
        transition: box-shadow 0.2s ease;
      }
      
      .workspace-app .card:hover {
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
      }
      
      .workspace-app .card.full-width {
        grid-column: 1 / -1;
      }
      
      .workspace-app .card-title {
        font-size: 1.125rem;
        font-weight: 600;
        color: #111827;
        margin: 0 0 8px 0;
      }
      
      .workspace-app .card-description {
        font-size: 0.7875rem;
        color: #6b7280;
        margin: 0 0 20px 0;
      }
      
      .workspace-app .form-group {
        margin-bottom: 20px;
      }
      
      .workspace-app .form-group:last-child {
        margin-bottom: 0;
      }
      
      .workspace-app .form-label {
        display: block;
        font-size: 0.7875rem;
        font-weight: 500;
        color: #374151;
        margin-bottom: 6px;
      }
      
      .workspace-app .form-input,
      .workspace-app .form-select {
        width: 100%;
        padding: 10px 12px;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.7875rem;
        background: white;
        transition: border-color 0.2s ease, box-shadow 0.2s ease;
      }
      
      .workspace-app .form-input:focus,
      .workspace-app .form-select:focus {
        outline: none;
        border-color: #6366f1;
        box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
      }
      
      .workspace-app .form-input::placeholder {
        color: #9ca3af;
      }
      
      .workspace-app .btn {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        padding: 10px 16px;
        border: 1px solid #e5e7eb;
        background: white;
        color: #374151;
        font-size: 0.7875rem;
        font-weight: 500;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;
        text-decoration: none;
      }
      
      .workspace-app .btn:hover {
        border-color: #a5b4fc;
        background: #eef2ff;
        color: #4338ca;
      }
      
      .workspace-app .btn.btn-primary {
        background: #6366f1;
        border-color: #6366f1;
        color: white;
      }
      
      .workspace-app .btn.btn-primary:hover {
        background: #4f46e5;
        border-color: #4f46e5;
      }
    `;
    
    // Append to head
    document.head.appendChild(this.styleElement);
  }

  private removeWorkspaceStyles(): void {
    if (this.styleElement) {
      document.head.removeChild(this.styleElement);
      this.styleElement = null;
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
    this.authService.logout().subscribe({ error: () => {} });
  }

  onRouteChange(route: string): void {
    console.log('Workspace route change requested:', route);
    this.currentRoute = route;
    this.loadComponent();
    
    // Update browser URL without triggering navigation
    if (this.isMicroFrontendMode()) {
      const currentUrl = this.router.url;
      const baseUrl = currentUrl.split('/host/workspace')[0] + '/host/workspace';
      const newUrl = baseUrl + route;
      window.history.pushState({}, '', newUrl);
    }
  }

  private loadComponent(): void {
    if (this.contentContainer) {
      this.contentContainer.clear();
      
      const componentClass = this.componentMap[this.currentRoute];
      if (componentClass) {
        console.log('Loading workspace component for route:', this.currentRoute);
        this.contentContainer.createComponent(componentClass, { injector: this.injector });
      } else {
        console.warn('No workspace component found for route:', this.currentRoute);
        // Load default component
        this.contentContainer.createComponent(DashboardComponent, { injector: this.injector });
      }
    }
  }

  private isMicroFrontendMode(): boolean {
    return window.location.pathname.includes('/host/');
  }
} 