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

      <!-- Sidebar Header Icon -->
      <i *ngIf="!isCollapsed" class="fa fa-th-large hub-icon"></i>

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
              
              <!-- Menu Icons -->
              <i *ngIf="item.id === 'core-settings' && !item.isChild" class="nav-icon"></i>
              <i *ngIf="item.id === 'identity-access' && !item.isChild" class="nav-icon"></i>
              <i *ngIf="item.id === 'org-structure' && !item.isChild" class="nav-icon"></i>
              <i *ngIf="item.id === 'roles-permissions' && !item.isChild" class="nav-icon"></i>
              <i *ngIf="item.id === 'licensing-billing' && !item.isChild" class="nav-icon"></i>
              <i *ngIf="item.id === 'user-management' && !item.isChild" class="nav-icon"></i>
              <i *ngIf="item.id === 'notifications-alerts' && !item.isChild" class="nav-icon"></i>
              <i *ngIf="item.id === 'help-support' && !item.isChild" class="nav-icon"></i>
              <i *ngIf="item.id === 'feedback-settings' && !item.isChild" class="nav-icon"></i>

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