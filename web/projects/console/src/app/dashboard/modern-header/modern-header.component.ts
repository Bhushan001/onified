import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User } from '../../models/auth.models';

interface DropdownOption {
  id: string;
  label: string;
  value: string;
  selected?: boolean;
}

@Component({
  selector: 'app-modern-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <header class="modern-header">
      <div class="header-container">
        <!-- Left side - Sidebar Toggle and Logo -->
        <div class="header-left">
          <!-- Sidebar Toggle Button (All devices) -->
          <button class="sidebar-toggle-btn" (click)="onSidebarToggle()">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="3" x2="21" y1="6" y2="6"></line>
              <line x1="3" x2="21" y1="12" y2="12"></line>
              <line x1="3" x2="21" y1="18" y2="18"></line>
            </svg>
          </button>

          <div class="logo">
            <div class="logo-icon">O</div>
            <span class="logo-text">Onified</span>
          </div>
        </div>

        <!-- Right side - Search, Dropdowns, Notifications, User -->
        <div class="header-right">
          <!-- Search -->
          <div class="search-container">
            <input type="text" placeholder="Search..." class="search-input">
            <i class="search-icon">🔍</i>
          </div>
          
          <!-- View As Dropdown -->
          <div class="dropdown-container">
            <button class="dropdown-trigger" (click)="toggleDropdown('viewAs')">
              <span class="dropdown-label">View as:</span>
              <span class="dropdown-value">{{ getSelectedOption('viewAs') }}</span>
              <i class="dropdown-arrow" [class.rotated]="activeDropdown === 'viewAs'">▼</i>
            </button>
            <div class="dropdown-menu" *ngIf="activeDropdown === 'viewAs'">
              <button 
                *ngFor="let option of viewAsOptions" 
                class="dropdown-item"
                [class.selected]="option.selected"
                (click)="selectOption('viewAs', option)">
                {{ option.label }}
              </button>
            </div>
          </div>

          <!-- Tenant Dropdown -->
          <div class="dropdown-container">
            <button class="dropdown-trigger" (click)="toggleDropdown('tenant')">
              <span class="dropdown-label">Tenant:</span>
              <span class="dropdown-value">{{ getSelectedOption('tenant') }}</span>
              <i class="dropdown-arrow" [class.rotated]="activeDropdown === 'tenant'">▼</i>
            </button>
            <div class="dropdown-menu" *ngIf="activeDropdown === 'tenant'">
              <button 
                *ngFor="let option of tenantOptions" 
                class="dropdown-item"
                [class.selected]="option.selected"
                (click)="selectOption('tenant', option)">
                {{ option.label }}
              </button>
            </div>
          </div>

          <!-- Application Dropdown -->
          <div class="dropdown-container">
            <button class="dropdown-trigger" (click)="toggleDropdown('application')">
              <span class="dropdown-label">App:</span>
              <span class="dropdown-value">{{ getSelectedOption('application') }}</span>
              <i class="dropdown-arrow" [class.rotated]="activeDropdown === 'application'">▼</i>
            </button>
            <div class="dropdown-menu" *ngIf="activeDropdown === 'application'">
              <button 
                *ngFor="let option of applicationOptions" 
                class="dropdown-item"
                [class.selected]="option.selected"
                (click)="selectOption('application', option)">
                {{ option.label }}
              </button>
            </div>
          </div>
          
          <!-- Notification Bell -->
          <button class="notification-btn" (click)="toggleNotifications()">
            <i class="notification-icon">🔔</i>
            <span class="notification-badge" *ngIf="notificationCount > 0">{{ notificationCount }}</span>
          </button>
          
          <!-- User Dropdown -->
          <div class="dropdown-container user-dropdown">
            <button class="dropdown-trigger user-trigger" (click)="toggleDropdown('user')" *ngIf="currentUser">
              <div class="user-avatar">
                <div class="avatar-placeholder" *ngIf="!currentUser.avatar">
                  {{ getInitials(currentUser.name) }}
                </div>
                <img *ngIf="currentUser.avatar && currentUser.name" 
                     [src]="currentUser.avatar" 
                     [alt]="currentUser.name">
              </div>
              <div class="user-info">
                <div class="user-name">{{ getUserDisplayName() }}</div>
                <div class="user-role">{{ getUserPrimaryRole() }} • {{ getUserDepartment() }}</div>
              </div>
              <i class="dropdown-arrow" [class.rotated]="activeDropdown === 'user'">▼</i>
            </button>
            <div class="dropdown-menu user-menu" *ngIf="activeDropdown === 'user'">
              <div class="user-menu-header">
                <div class="user-avatar-large">
                  <div class="avatar-placeholder" *ngIf="!currentUser?.avatar">
                    {{ getInitials(currentUser?.name || '') }}
                  </div>
                  <img *ngIf="currentUser?.avatar && currentUser?.name" 
                       [src]="currentUser?.avatar" 
                       [alt]="currentUser?.name">
                </div>
                <div class="user-details">
                  <div class="user-name-large">{{ getUserDisplayName() }}</div>
                  <div class="user-email">{{ currentUser?.email }}</div>
                  <div class="user-role-large">{{ getUserPrimaryRole() }} • {{ getUserDepartment() }}</div>
                </div>
              </div>
              <div class="menu-divider"></div>
              <button class="dropdown-item" (click)="onMenuAction('profile')">
                <i class="menu-icon">👤</i>
                Profile Settings
              </button>
              <button class="dropdown-item" (click)="onMenuAction('preferences')">
                <i class="menu-icon">⚙️</i>
                Preferences
              </button>
              <button class="dropdown-item" (click)="onMenuAction('help')">
                <i class="menu-icon">❓</i>
                Help & Support
              </button>
              <div class="menu-divider"></div>
              <button class="dropdown-item logout-item" (click)="onMenuAction('logout')">
                <i class="menu-icon">🚪</i>
                Sign Out
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Dropdown Backdrop -->
      <div class="dropdown-backdrop" *ngIf="activeDropdown" (click)="closeDropdowns()"></div>
    </header>
  `,
  styleUrls: ['./modern-header.component.scss']
})
export class ModernHeaderComponent {
  @Input() currentUser: User | null = null;
  @Output() logout = new EventEmitter<void>();
  @Output() toggleSidebar = new EventEmitter<void>();

  activeDropdown: string | null = null;
  notificationCount: number = 3;

  viewAsOptions: DropdownOption[] = [
    { id: 'admin', label: 'Administrator', value: 'admin', selected: true },
    { id: 'user', label: 'End User', value: 'user', selected: false },
    { id: 'manager', label: 'Manager', value: 'manager', selected: false },
    { id: 'viewer', label: 'Viewer', value: 'viewer', selected: false }
  ];

  tenantOptions: DropdownOption[] = [
    { id: 'onified', label: 'Onified Corp', value: 'onified', selected: true },
    { id: 'acme', label: 'Acme Industries', value: 'acme', selected: false },
    { id: 'techstart', label: 'TechStart Inc', value: 'techstart', selected: false },
    { id: 'global', label: 'Global Solutions', value: 'global', selected: false }
  ];

  applicationOptions: DropdownOption[] = [
    { id: 'dashboard', label: 'Dashboard', value: 'dashboard', selected: true },
    { id: 'analytics', label: 'Analytics Suite', value: 'analytics', selected: false },
    { id: 'crm', label: 'CRM System', value: 'crm', selected: false },
    { id: 'inventory', label: 'Inventory Mgmt', value: 'inventory', selected: false }
  ];

  // Get user's primary role for display
  getUserPrimaryRole(): string {
    if (!this.currentUser?.roles || this.currentUser.roles.length === 0) {
      return 'User';
    }
    
    const role = this.currentUser.roles.find(r => 
      ['admin', 'administrator', 'manager', 'supervisor'].includes(r.toLowerCase())
    );
    
    return role ? this.capitalizeFirst(role) : 'User';
  }

  // Get user's display name
  getUserDisplayName(): string {
    if (!this.currentUser) return 'Guest User';
    
    if (this.currentUser.firstName && this.currentUser.lastName) {
      return `${this.currentUser.firstName} ${this.currentUser.lastName}`;
    }
    
    return this.currentUser.name || this.currentUser.username || 'Unknown User';
  }

  // Get user's department or default
  getUserDepartment(): string {
    return this.currentUser?.department || 'General';
  }

  // Capitalize first letter
  private capitalizeFirst(str: string): string {
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
  }

  toggleDropdown(dropdownId: string): void {
    if (this.activeDropdown === dropdownId) {
      this.activeDropdown = null;
    } else {
      this.activeDropdown = dropdownId;
    }
  }

  closeDropdowns(): void {
    this.activeDropdown = null;
  }

  toggleNotifications(): void {
    // Placeholder for notification logic
    this.notificationCount = 0;
  }

  onSidebarToggle(): void {
    this.toggleSidebar.emit();
  }

  selectOption(dropdownType: string, option: DropdownOption): void {
    let options;
    switch (dropdownType) {
      case 'viewAs':
        options = this.viewAsOptions;
        break;
      case 'tenant':
        options = this.tenantOptions;
        break;
      case 'application':
        options = this.applicationOptions;
        break;
      default:
        return;
    }
    options.forEach(opt => (opt.selected = false));
    option.selected = true;
    this.closeDropdowns();
  }

  getSelectedOption(dropdownType: string): string {
    let options;
    switch (dropdownType) {
      case 'viewAs':
        options = this.viewAsOptions;
        break;
      case 'tenant':
        options = this.tenantOptions;
        break;
      case 'application':
        options = this.applicationOptions;
        break;
      default:
        return '';
    }
    const selected = options.find(opt => opt.selected);
    return selected ? selected.label : '';
  }

  onMenuAction(action: string): void {
    switch (action) {
      case 'logout':
        this.logout.emit();
        break;
      // Add more actions as needed
    }
    this.closeDropdowns();
  }

  getUserRole(): string {
    if (!this.currentUser) return '';
    // Use 'roles' array if present, else fallback to 'User'
    if (Array.isArray(this.currentUser.roles) && this.currentUser.roles.length > 0) {
      return this.currentUser.roles[0];
    }
    return 'User';
  }

  getInitials(name: string | undefined): string {
    if (!name) return '';
    const parts = name.split(' ');
    return parts.map(p => p[0]).join('').toUpperCase();
  }
} 