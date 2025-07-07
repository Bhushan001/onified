import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ViewContainerRef, Injector } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from './services/auth.service';
import { ModernHeaderComponent } from './dashboard/modern-header/modern-header.component';
import { SidebarComponent } from './dashboard/sidebar/sidebar.component';
// If you have a ConsoleRoutingService, import it; otherwise, use the hubRoutingService logic as a placeholder
// import { ConsoleRoutingService } from './services/console-routing.service';

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
    // Replace with ConsoleRoutingService if you have one
    // private consoleRoutingService: ConsoleRoutingService,
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
    // It will handle redirection based on the app context
    this.authService.logout().subscribe();
  }

  onRouteChange(route: string): void {
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
      // Placeholder: load a default component or content
      // In a real app, you would dynamically load the component for the current route
    }
  }

  private isMicroFrontendMode(): boolean {
    // Placeholder for micro-frontend detection logic
    return window.location.pathname.includes('/host/');
  }
} 