import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ViewContainerRef, Injector } from '@angular/core';
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
  `]
})
export class DashboardWrapperComponent implements OnInit {
  @ViewChild('contentContainer', { read: ViewContainerRef, static: true }) contentContainer!: ViewContainerRef;
  
  currentUser: any = null;
  sidebarCollapsed: boolean = false;
  mobileSidebarOpen: boolean = false;
  windowWidth: number = window.innerWidth;
  isMobile: boolean = window.innerWidth <= 768;
  currentRoute: string = 'foundation/dashboard-analytics';

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
    console.log('DashboardWrapperComponent initialized');
    
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
    console.log('Route change requested:', route);
    this.currentRoute = route;
    this.loadComponent();
    
    // Update browser URL without triggering navigation
    if (this.isMicroFrontendMode()) {
      const currentUrl = this.router.url;
      const baseUrl = currentUrl.split('/host/hub')[0] + '/host/hub';
      const newUrl = baseUrl + route;
      window.history.pushState({}, '', newUrl);
    }
  }

  private loadComponent(): void {
    if (this.contentContainer) {
      this.contentContainer.clear();
      
      const componentClass = this.componentMap[this.currentRoute];
      if (componentClass) {
        console.log('Loading component for route:', this.currentRoute);
        this.contentContainer.createComponent(componentClass, { injector: this.injector });
      } else {
        console.warn('No component found for route:', this.currentRoute);
        // Load default component
        this.contentContainer.createComponent(DashboardAnalyticsComponent, { injector: this.injector });
      }
    }
  }

  private isMicroFrontendMode(): boolean {
    return window.location.pathname.includes('/host/');
  }
} 