import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { User } from '../../../models/auth.models';

interface MenuItem {
  id: string;
  label: string;
  icon: string;
  route?: string;
  children?: MenuItem[];
  badge?: string;
  active?: boolean;
}

/**
 * Sidebar Navigation Component for Admin Dashboard
 * 
 * Provides navigation menu with hierarchical structure, user profile display,
 * and responsive collapse functionality.
 * 
 * @component SidebarComponent
 */
@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="sidebar" [class.collapsed]="isCollapsed">
      <!-- Logo Section -->
      <div class="sidebar-header">
        <div class="logo">
          <span class="logo-icon">O</span>
          <span class="logo-text" *ngIf="!isCollapsed">Onified.ai</span>
        </div>
      </div>

      <!-- User Profile Section -->
      <div class="user-profile" *ngIf="currentUser">
        <div class="user-avatar">
          <div class="avatar-placeholder" *ngIf="!currentUser.avatar">
            {{ getInitials(currentUser.name) }}
          </div>
          <img *ngIf="currentUser.avatar" 
               [src]="currentUser.avatar" 
               [alt]="currentUser.name"
               (error)="onImageError($event)">
        </div>
        <div class="user-info" *ngIf="!isCollapsed">
          <div class="user-name">{{ currentUser.name }}</div>
          <div class="user-role">{{ getUserRole() }}</div>
        </div>
      </div>

      <!-- Navigation Menu -->
      <nav class="sidebar-nav">
        <ul class="nav-list">
          <li class="nav-item" *ngFor="let item of menuItems" 
              [class.active]="item.active"
              [class.has-children]="item.children && item.children.length > 0">
            
            <a class="nav-link" 
               [routerLink]="item.route" 
               (click)="onMenuClick(item)"
               [title]="isCollapsed ? item.label : ''">
              <i class="nav-icon" [class]="item.icon"></i>
              <span class="nav-text" *ngIf="!isCollapsed">{{ item.label }}</span>
              <span class="nav-badge" *ngIf="item.badge && !isCollapsed">{{ item.badge }}</span>
              <i class="nav-arrow" *ngIf="item.children && !isCollapsed" 
                 [class.expanded]="item.active"></i>
            </a>

            <!-- Submenu -->
            <ul class="nav-submenu" *ngIf="item.children && item.active && !isCollapsed">
              <li class="nav-subitem" *ngFor="let child of item.children">
                <a class="nav-sublink" [routerLink]="child.route">
                  <i class="nav-subicon" [class]="child.icon"></i>
                  <span class="nav-subtext">{{ child.label }}</span>
                </a>
              </li>
            </ul>
          </li>
        </ul>
      </nav>

      <!-- Sidebar Footer -->
      <div class="sidebar-footer">
        <button class="collapse-btn" (click)="onToggleSidebar()" 
                [title]="isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'">
          <i class="icon-chevron" [class.rotated]="isCollapsed"></i>
        </button>
        
        <button class="logout-btn" (click)="onLogout()" 
                [title]="isCollapsed ? 'Logout' : ''">
          <i class="icon-logout"></i>
          <span *ngIf="!isCollapsed">Logout</span>
        </button>
      </div>
    </div>
  `,
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input() currentUser: User | null = null;
  @Input() isCollapsed: boolean = false;
  @Output() toggleSidebar = new EventEmitter<void>();
  @Output() logout = new EventEmitter<void>();

  menuItems: MenuItem[] = [
    {
      id: 'dashboard',
      label: 'Dashboard',
      icon: 'icon-dashboard',
      route: '/dashboard',
      active: true
    },
    {
      id: 'analytics',
      label: 'Analytics',
      icon: 'icon-chart',
      route: '/analytics',
      badge: '12'
    },
    {
      id: 'users',
      label: 'User Management',
      icon: 'icon-users',
      children: [
        { id: 'users-list', label: 'All Users', icon: 'icon-list', route: '/users' },
        { id: 'users-roles', label: 'Roles & Permissions', icon: 'icon-shield', route: '/users/roles' },
        { id: 'users-activity', label: 'Activity Log', icon: 'icon-activity', route: '/users/activity' }
      ]
    },
    {
      id: 'content',
      label: 'Content Management',
      icon: 'icon-file-text',
      children: [
        { id: 'content-pages', label: 'Pages', icon: 'icon-page', route: '/content/pages' },
        { id: 'content-media', label: 'Media Library', icon: 'icon-image', route: '/content/media' },
        { id: 'content-seo', label: 'SEO Settings', icon: 'icon-search', route: '/content/seo' }
      ]
    },
    {
      id: 'settings',
      label: 'Settings',
      icon: 'icon-settings',
      children: [
        { id: 'settings-general', label: 'General', icon: 'icon-gear', route: '/settings/general' },
        { id: 'settings-security', label: 'Security', icon: 'icon-lock', route: '/settings/security' },
        { id: 'settings-integrations', label: 'Integrations', icon: 'icon-plug', route: '/settings/integrations' }
      ]
    },
    {
      id: 'reports',
      label: 'Reports',
      icon: 'icon-bar-chart',
      route: '/reports'
    },
    {
      id: 'support',
      label: 'Support',
      icon: 'icon-help-circle',
      route: '/support'
    }
  ];

  constructor(private router: Router) {}

  onMenuClick(item: MenuItem): void {
    // Toggle submenu for items with children
    if (item.children && item.children.length > 0) {
      this.menuItems.forEach(menuItem => {
        if (menuItem.id === item.id) {
          menuItem.active = !menuItem.active;
        } else {
          menuItem.active = false;
        }
      });
    } else {
      // Navigate to route for items without children
      this.menuItems.forEach(menuItem => {
        menuItem.active = menuItem.id === item.id;
      });
      
      if (item.route) {
        this.router.navigate([item.route]);
      }
    }
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  onLogout(): void {
    this.logout.emit();
  }

  getUserRole(): string {
    if (!this.currentUser?.roles || this.currentUser.roles.length === 0) {
      return 'User';
    }
    
    // Extract role name from the role string (e.g., "SOP.Checklist.User" -> "User")
    const role = this.currentUser.roles[0];
    const roleParts = role.split('.');
    return roleParts[roleParts.length - 1] || 'User';
  }

  getInitials(name: string): string {
    if (!name) return 'U';
    
    const names = name.split(' ');
    if (names.length >= 2) {
      return (names[0][0] + names[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  onImageError(event: any): void {
    // Hide the image and show placeholder instead
    event.target.style.display = 'none';
  }
}