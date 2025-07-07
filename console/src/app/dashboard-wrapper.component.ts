import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ViewContainerRef, Injector, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from './services/auth.service';
import { ModernHeaderComponent } from './dashboard/modern-header/modern-header.component';
import { SidebarComponent } from './dashboard/sidebar/sidebar.component';
import { DashboardAnalyticsComponent } from './dashboard/foundation/dashboard-analytics/dashboard-analytics.component';
import { TenantInitializationComponent } from './dashboard/foundation/tenant-initialization/tenant-initialization.component';
import { BrandingComponent } from './dashboard/foundation/branding/branding.component';
import { LocalizationComponent } from './dashboard/foundation/localization/localization.component';
import { DeploymentComponent } from './dashboard/foundation/deployment/deployment.component';
import { NamingConventionsComponent } from './dashboard/foundation/naming-conventions/naming-conventions.component';
import { TemplatesComponent } from './dashboard/foundation/templates/templates.component';
import { SetupWizardComponent } from './dashboard/foundation/setup-wizard/setup-wizard.component';
import { ComplianceComponent } from './dashboard/foundation/compliance/compliance.component';
import { IdentityAccessComponent } from './dashboard/security/identity-access/identity-access.component';
import { OrgStructureComponent } from './dashboard/security/org-structure/org-structure.component';
import { RolesPermissionsComponent } from './dashboard/security/roles-permissions/roles-permissions.component';
import { LicensingBillingComponent } from './dashboard/finance/licensing-billing/licensing-billing.component';
import { UserManagementComponent } from './users/user-management/user-management.component';
import { NotificationsAlertsComponent } from './users/notifications-alerts/notifications-alerts.component';
import { HelpSupportComponent } from './users/help-support/help-support.component';
import { FeedbackSettingsComponent } from './users/feedback-settings/feedback-settings.component';

@Component({
  selector: 'app-dashboard-wrapper',
  standalone: false,
  template: `
    <div class="modern-dashboard console-app">
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
      background-color: #f5f5f5;
    }

    .dashboard-main {
      flex: 1;
      padding: 20px;
      margin-left: 250px;
      transition: margin-left 0.3s ease;
      overflow-y: auto;
    }

    .dashboard-main.sidebar-collapsed {
      margin-left: 60px;
    }

    @media (max-width: 768px) {
      .dashboard-main {
        margin-left: 0;
      }
    }

    /* Console-specific styles to ensure they're applied */
    .console-app {
      font-family: 'Roboto', Arial, sans-serif;
    }

    .console-app .page-container {
      padding: 24px;
      max-width: 1200px;
      margin: 0 auto;
    }

    .console-app .page-header {
      margin-bottom: 32px;
      padding-bottom: 16px;
      border-bottom: 1px solid #e5e7eb;
    }

    .console-app .page-title {
      font-size: 1.6875rem;
      font-weight: 700;
      color: #111827;
      margin: 0 0 8px 0;
      line-height: 1.2;
    }

    .console-app .page-description {
      font-size: 0.9rem;
      color: #6b7280;
      margin: 0;
      line-height: 1.5;
    }

    .console-app .content-section {
      display: grid;
      gap: 24px;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    }

    .console-app .card {
      background: white;
      border: 1px solid #e5e7eb;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
      transition: box-shadow 0.2s ease;
    }

    .console-app .card:hover {
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
    }

    .console-app .card.full-width {
      grid-column: 1 / -1;
    }

    .console-app .card-title {
      font-size: 1.125rem;
      font-weight: 600;
      color: #111827;
      margin: 0 0 8px 0;
    }

    .console-app .card-description {
      font-size: 0.7875rem;
      color: #6b7280;
      margin: 0 0 20px 0;
    }

    .console-app .form-group {
      margin-bottom: 20px;
    }

    .console-app .form-group:last-child {
      margin-bottom: 0;
    }

    .console-app .form-label {
      display: block;
      font-size: 0.7875rem;
      font-weight: 500;
      color: #374151;
      margin-bottom: 6px;
    }

    .console-app .form-input,
    .console-app .form-select {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #e5e7eb;
      border-radius: 8px;
      font-size: 0.7875rem;
      background: white;
      transition: border-color 0.2s ease, box-shadow 0.2s ease;
    }

    .console-app .form-input:focus,
    .console-app .form-select:focus {
      outline: none;
      border-color: #6366f1;
      box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
    }

    .console-app .form-input::placeholder {
      color: #9ca3af;
    }

    .console-app .btn {
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

    .console-app .btn:hover {
      border-color: #a5b4fc;
      background: #eef2ff;
      color: #4338ca;
    }

    .console-app .btn.btn-primary {
      background: #6366f1;
      border-color: #6366f1;
      color: white;
    }

    .console-app .btn.btn-primary:hover {
      background: #4f46e5;
      border-color: #4f46e5;
    }
  `]
})
export class DashboardWrapperComponent implements OnInit, OnDestroy {
  @ViewChild('contentContainer', { read: ViewContainerRef, static: true }) contentContainer!: ViewContainerRef;
  
  currentUser: any = null;
  sidebarCollapsed: boolean = false;
  mobileSidebarOpen: boolean = false;
  windowWidth: number = window.innerWidth;
  isMobile: boolean = window.innerWidth <= 768;
  currentRoute: string = 'foundation/dashboard-analytics';
  private styleElement: HTMLStyleElement | null = null;

  private componentMap: { [key: string]: any } = {
    '/dashboard/foundation/dashboard-analytics': DashboardAnalyticsComponent,
    '/dashboard/foundation/tenant-initialization': TenantInitializationComponent,
    '/dashboard/foundation/branding': BrandingComponent,
    '/dashboard/foundation/localization': LocalizationComponent,
    '/dashboard/foundation/deployment': DeploymentComponent,
    '/dashboard/foundation/naming-conventions': NamingConventionsComponent,
    '/dashboard/foundation/templates': TemplatesComponent,
    '/dashboard/foundation/setup-wizard': SetupWizardComponent,
    '/dashboard/foundation/compliance': ComplianceComponent,
    '/dashboard/security/identity-access': IdentityAccessComponent,
    '/dashboard/security/org-structure': OrgStructureComponent,
    '/dashboard/security/roles-permissions': RolesPermissionsComponent,
    '/dashboard/finance/licensing-billing': LicensingBillingComponent,
    '/dashboard/users/user-management': UserManagementComponent,
    '/dashboard/users/notifications-alerts': NotificationsAlertsComponent,
    '/dashboard/users/help-support': HelpSupportComponent,
    '/dashboard/users/feedback-settings': FeedbackSettingsComponent
  };

  constructor(
    private injector: Injector,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log('Console DashboardWrapperComponent initialized');
    
    // Inject console styles dynamically
    this.injectConsoleStyles();
    
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
    this.removeConsoleStyles();
  }

  private injectConsoleStyles(): void {
    // Create a style element to inject console styles
    this.styleElement = document.createElement('style');
    this.styleElement.id = 'console-app-styles';
    this.styleElement.textContent = `
      /* Console App Styles */
      .console-app {
        font-family: 'Roboto', Arial, sans-serif !important;
      }
      
      .console-app * {
        box-sizing: border-box;
      }
      
      .console-app .page-container {
        padding: 24px;
        max-width: 1200px;
        margin: 0 auto;
      }
      
      .console-app .page-header {
        margin-bottom: 32px;
        padding-bottom: 16px;
        border-bottom: 1px solid #e5e7eb;
      }
      
      .console-app .page-title {
        font-size: 1.6875rem;
        font-weight: 700;
        color: #111827;
        margin: 0 0 8px 0;
        line-height: 1.2;
      }
      
      .console-app .page-description {
        font-size: 0.9rem;
        color: #6b7280;
        margin: 0;
        line-height: 1.5;
      }
      
      .console-app .content-section {
        display: grid;
        gap: 24px;
        grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      }
      
      .console-app .card {
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 12px;
        padding: 24px;
        box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
        transition: box-shadow 0.2s ease;
      }
      
      .console-app .card:hover {
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
      }
      
      .console-app .card.full-width {
        grid-column: 1 / -1;
      }
      
      .console-app .card-title {
        font-size: 1.125rem;
        font-weight: 600;
        color: #111827;
        margin: 0 0 8px 0;
      }
      
      .console-app .card-description {
        font-size: 0.7875rem;
        color: #6b7280;
        margin: 0 0 20px 0;
      }
      
      .console-app .form-group {
        margin-bottom: 20px;
      }
      
      .console-app .form-group:last-child {
        margin-bottom: 0;
      }
      
      .console-app .form-label {
        display: block;
        font-size: 0.7875rem;
        font-weight: 500;
        color: #374151;
        margin-bottom: 6px;
      }
      
      .console-app .form-input,
      .console-app .form-select {
        width: 100%;
        padding: 10px 12px;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.7875rem;
        background: white;
        transition: border-color 0.2s ease, box-shadow 0.2s ease;
      }
      
      .console-app .form-input:focus,
      .console-app .form-select:focus {
        outline: none;
        border-color: #6366f1;
        box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
      }
      
      .console-app .form-input::placeholder {
        color: #9ca3af;
      }
      
      .console-app .btn {
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
      
      .console-app .btn:hover {
        border-color: #a5b4fc;
        background: #eef2ff;
        color: #4338ca;
      }
      
      .console-app .btn.btn-primary {
        background: #6366f1;
        border-color: #6366f1;
        color: white;
      }
      
      .console-app .btn.btn-primary:hover {
        background: #4f46e5;
        border-color: #4f46e5;
      }
    `;
    
    // Append to head
    document.head.appendChild(this.styleElement);
  }

  private removeConsoleStyles(): void {
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
    console.log('Console route change requested:', route);
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
      
      const componentClass = this.componentMap[this.currentRoute];
      if (componentClass) {
        console.log('Loading console component for route:', this.currentRoute);
        this.contentContainer.createComponent(componentClass, { injector: this.injector });
      } else {
        console.warn('No console component found for route:', this.currentRoute);
        // Load default component
        this.contentContainer.createComponent(DashboardAnalyticsComponent, { injector: this.injector });
      }
    }
  }

  private isMicroFrontendMode(): boolean {
    return window.location.pathname.includes('/host/');
  }
} 