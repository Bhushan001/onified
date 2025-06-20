import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { User } from '../../../models/auth.models';

interface MenuItem {
  id: string;
  label: string;
  icon?: string;
  route?: string;
  children?: MenuItem[];
  active?: boolean;
  expanded?: boolean;
  isSection?: boolean;
  isChild?: boolean;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="sidebar" [class.collapsed]="isCollapsed" [class.mobile-open]="isMobileOpen">
      <!-- Sidebar Header -->
      <div class="sidebar-header" *ngIf="!isCollapsed">
        <div class="hub-title">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="hub-icon">
            <rect width="18" height="18" x="3" y="3" rx="2"></rect>
            <path d="M3 9h18"></path>
            <path d="M3 15h18"></path>
            <path d="M9 3v18"></path>
            <path d="M15 3v18"></path>
          </svg>
          <span class="hub-text">Onified Hub</span>
        </div>
      </div>

      

      <!-- Navigation Menu -->
      <nav class="sidebar-nav">
        <ul class="nav-list">
          <li class="nav-item" *ngFor="let item of menuItems" 
              [class.active]="item.active"
              [class.section-header]="item.isSection"
              [class.has-children]="item.children && item.children.length > 0"
              [class.child-item]="item.isChild">
            
            <!-- Section Header -->
            <div class="section-title" *ngIf="item.isSection && !isCollapsed">
              {{ item.label }}
            </div>

            <!-- Regular Menu Item -->
            <button class="nav-link" 
                    *ngIf="!item.isSection"
                    (click)="onMenuClick(item)"
                    [class.active]="item.active"
                    [class.child-link]="item.isChild"
                    [title]="isCollapsed ? item.label : ''">
              
              <!-- Settings Icon -->
              <svg *ngIf="item.id === 'core-settings' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>

              <!-- Shield Icon -->
              <svg *ngIf="item.id === 'identity-access' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <path d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z"></path>
                <path d="m9 12 2 2 4-4"></path>
              </svg>

              <!-- Database Icon -->
              <svg *ngIf="item.id === 'org-structure' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
                <path d="M3 5V19A9 3 0 0 0 21 19V5"></path>
                <path d="M3 12A9 3 0 0 0 21 12"></path>
              </svg>

              <!-- Check Square Icon -->
              <svg *ngIf="item.id === 'roles-permissions' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <path d="m9 11 3 3L22 4"></path>
                <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>
              </svg>

              <!-- Credit Card Icon -->
              <svg *ngIf="item.id === 'licensing-billing' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <rect width="20" height="14" x="2" y="5" rx="2"></rect>
                <line x1="2" x2="22" y1="10" y2="10"></line>
              </svg>

              <!-- User Check Icon -->
              <svg *ngIf="item.id === 'user-management' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <polyline points="16 11 18 13 22 9"></polyline>
              </svg>

              <!-- Bell Icon -->
              <svg *ngIf="item.id === 'notifications-alerts' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"></path>
                <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"></path>
              </svg>

              <!-- Help Circle Icon -->
              <svg *ngIf="item.id === 'help-support' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <circle cx="12" cy="12" r="10"></circle>
                <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path>
                <path d="M12 17h.01"></path>
              </svg>

              <!-- Star Icon -->
              <svg *ngIf="item.id === 'feedback-settings' && !item.isChild" 
                   xmlns="http://www.w3.org/2000/svg" 
                   width="16" height="16" viewBox="0 0 24 24" 
                   fill="none" stroke="currentColor" stroke-width="2" 
                   stroke-linecap="round" stroke-linejoin="round" 
                   class="nav-icon">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>

              <span class="nav-text" *ngIf="!isCollapsed" [class.child-text]="item.isChild">{{ item.label }}</span>
            </button>

            <!-- Child Items -->
            <ul class="nav-submenu" *ngIf="item.children && item.expanded && !isCollapsed">
              <li class="nav-subitem" *ngFor="let child of item.children">
                <button class="nav-sublink" 
                        (click)="onChildClick(child)"
                        [class.active]="child.active">
                  <span class="nav-subtext">{{ child.label }}</span>
                </button>
              </li>
            </ul>
          </li>
        </ul>
      </nav>

      <!-- Sidebar Footer -->
      <div class="sidebar-footer" *ngIf="!isCollapsed">
        <div class="powered-by">
          <span class="powered-text">Powered by</span>
          <span class="onified-link">Onified</span>
        </div>
      </div>
    </div>

    <!-- Sidebar Overlay for Mobile -->
    <div class="sidebar-overlay" 
         *ngIf="isMobileOpen && isMobile" 
         (click)="onToggleSidebar()"></div>
  `,
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit, OnDestroy {
  @Input() currentUser: User | null = null;
  @Input() isCollapsed: boolean = false;
  @Input() isMobileOpen: boolean = false;
  @Output() toggleSidebar = new EventEmitter<void>();

  isMobile: boolean = false;

  menuItems: MenuItem[] = [
    // Foundation Section
    {
      id: 'foundation-section',
      label: 'Foundation',
      isSection: true
    },
    {
      id: 'core-settings',
      label: 'Core Settings',
      route: '/dashboard/foundation/core-settings',
      children: [
        { id: 'dashboard-analytics', label: 'Dashboard & Analytics', route: '/dashboard/foundation/dashboard-analytics', isChild: true },
        { id: 'tenant-initialization', label: 'Tenant Initialization', route: '/dashboard/foundation/tenant-initialization', isChild: true },
        { id: 'branding', label: 'Branding', route: '/dashboard/foundation/branding', isChild: true },
        { id: 'localization', label: 'Localization', route: '/dashboard/foundation/localization', isChild: true },
        { id: 'deployment', label: 'Deployment', route: '/dashboard/foundation/deployment', isChild: true },
        { id: 'naming-conventions', label: 'Naming Conventions', route: '/dashboard/foundation/naming-conventions', isChild: true },
        { id: 'templates', label: 'Templates', route: '/dashboard/foundation/templates', isChild: true },
        { id: 'setup-wizard', label: 'Setup Wizard', route: '/dashboard/foundation/setup-wizard', isChild: true },
        { id: 'compliance', label: 'Compliance', route: '/dashboard/foundation/compliance', isChild: true }
      ],
      expanded: true
    },

    // Standalone Security Items
    {
      id: 'identity-access',
      label: 'Identity & Access',
      route: '/dashboard/security/identity-access'
    },
    {
      id: 'org-structure',
      label: 'Org Structure',
      route: '/dashboard/security/org-structure'
    },
    {
      id: 'roles-permissions',
      label: 'Roles & Permissions',
      route: '/dashboard/security/roles-permissions'
    },

    // Standalone Finance Item
    {
      id: 'licensing-billing',
      label: 'Licensing & Billing',
      route: '/dashboard/finance/licensing-billing'
    },

    // Users Section
    {
      id: 'users-section',
      label: 'Users',
      isSection: true
    },
    {
      id: 'user-management',
      label: 'User Management',
      route: '/dashboard/users/user-management'
    },
    {
      id: 'notifications-alerts',
      label: 'Notifications & Alerts',
      route: '/dashboard/users/notifications-alerts'
    },
    {
      id: 'help-support',
      label: 'Help & Support',
      route: '/dashboard/users/help-support'
    },
    {
      id: 'feedback-settings',
      label: 'Feedback Settings',
      route: '/dashboard/users/feedback-settings'
    }
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.checkMobile();
    window.addEventListener('resize', this.onResize.bind(this));
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.onResize.bind(this));
  }

  private onResize(): void {
    this.checkMobile();
  }

  onMenuClick(item: MenuItem): void {
    if (item.isSection) return;

    // Toggle submenu for items with children
    if (item.children && item.children.length > 0) {
      // Close other expanded items
      this.menuItems.forEach(menuItem => {
        if (menuItem.id !== item.id && !menuItem.isSection) {
          menuItem.expanded = false;
        }
      });
      
      // Toggle current item
      item.expanded = !item.expanded;
    } else {
      // Navigate to route for items without children
      this.setActiveItem(item.id);
      
      if (item.route) {
        this.router.navigate([item.route]);
      }

      // Close sidebar on mobile after navigation
      if (this.isMobile) {
        this.onToggleSidebar();
      }
    }
  }

  onChildClick(child: MenuItem): void {
    this.setActiveItem(child.id);
    
    if (child.route) {
      this.router.navigate([child.route]);
    }

    // Close sidebar on mobile after navigation
    if (this.isMobile) {
      this.onToggleSidebar();
    }
  }

  private setActiveItem(activeId: string): void {
    this.menuItems.forEach(menuItem => {
      if (!menuItem.isSection) {
        menuItem.active = menuItem.id === activeId;
        
        if (menuItem.children) {
          menuItem.children.forEach(child => {
            child.active = child.id === activeId;
          });
        }
      }
    });
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  private checkMobile(): void {
    this.isMobile = window.innerWidth <= 768;
  }
}