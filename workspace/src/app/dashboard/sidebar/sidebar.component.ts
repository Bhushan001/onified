import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { User } from '../../models/auth.models';

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
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit, OnDestroy {
  @Input() currentUser: User | null = null;
  @Input() isCollapsed: boolean = false;
  @Input() isMobileOpen: boolean = false;
  @Input() isMobile: boolean = false;
  @Output() toggleSidebar = new EventEmitter<void>();
  @Output() routeChange = new EventEmitter<string>();

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
    // Only check mobile if not provided as input
    if (!this.isMobile) {
      this.checkMobile();
    }
    window.addEventListener('resize', this.onResize.bind(this));
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.onResize.bind(this));
  }

  private onResize(): void {
    // Only check mobile if not provided as input
    if (!this.isMobile) {
      this.checkMobile();
    }
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
        // Emit route change event for micro-frontend
        this.routeChange.emit(item.route);
        
        // Only try normal navigation if not in micro-frontend mode
        if (!this.isMicroFrontendMode()) {
          try {
            this.router.navigate([item.route]);
          } catch (error) {
            // Router navigation failed
          }
        }
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
      // Emit route change event for micro-frontend
      this.routeChange.emit(child.route);
      
      // Only try normal navigation if not in micro-frontend mode
      if (!this.isMicroFrontendMode()) {
        try {
          this.router.navigate([child.route]);
        } catch (error) {
          // Router navigation failed
        }
      }
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

  private isMicroFrontendMode(): boolean {
    try {
      // Check if we're loaded in a micro-frontend context
      return window.location.pathname.includes('/host/workspace') || 
             window.location.href.includes('localhost:4200');
    } catch {
      return false;
    }
  }

  getIconForItem(itemId: string): string {
    const iconMap: { [key: string]: string } = {
      'core-settings': 'fa-cogs',
      'identity-access': 'fa-user',
      'org-structure': 'fa-sitemap',
      'roles-permissions': 'fa-key',
      'licensing-billing': 'fa-credit-card',
      'user-management': 'fa-users',
      'notifications-alerts': 'fa-bell',
      'help-support': 'fa-question-circle',
      'feedback-settings': 'fa-comment'
    };
    return iconMap[itemId] || 'fa-circle';
  }
}