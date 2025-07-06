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
                  <div class="user-username">{{ '@' + (currentUser?.username || '') }}</div>
                  <div class="user-role-large">{{ getUserPrimaryRole() }} • {{ getUserDepartment() }}</div>
                </div>
              </div>
              <div class="menu-divider"></div>
              <button class="dropdown-item" (click)="onMenuAction('profile')">
                <i class="menu-icon">👤</i>
                Profile Settings
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
  private _currentUser: User | null = null;
  
  @Input() 
  set currentUser(user: User | null) {
    this._currentUser = user;
    this.populateViewAsOptions();
  }
  
  get currentUser(): User | null {
    return this._currentUser;
  }
  
  @Output() logout = new EventEmitter<void>();
  @Output() toggleSidebar = new EventEmitter<void>();

  activeDropdown: string | null = null;
  notificationCount: number = 3;

  viewAsOptions: DropdownOption[] = [];

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

  // Populate viewAsOptions from user roles
  private populateViewAsOptions(): void {
    if (!this.currentUser?.roles || this.currentUser.roles.length === 0) {
      this.viewAsOptions = [
        { id: 'user', label: 'User', value: 'user', selected: true }
      ];
      return;
    }

    this.viewAsOptions = this.currentUser.roles.map((role, index) => ({
      id: role.toLowerCase(),
      label: this.capitalizeFirst(role),
      value: role.toLowerCase(),
      selected: index === 0 // First role is selected by default
    }));
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
    // Handle notification click
  }

  onSidebarToggle(): void {
    this.toggleSidebar.emit();
  }

  selectOption(dropdownType: string, option: DropdownOption): void {
    switch (dropdownType) {
      case 'viewAs':
        this.viewAsOptions.forEach(opt => opt.selected = false);
        option.selected = true;
        break;
      case 'tenant':
        this.tenantOptions.forEach(opt => opt.selected = false);
        option.selected = true;
        break;
      case 'application':
        this.applicationOptions.forEach(opt => opt.selected = false);
        option.selected = true;
        break;
    }
    this.closeDropdowns();
  }

  getSelectedOption(dropdownType: string): string {
    switch (dropdownType) {
      case 'viewAs':
        return this.viewAsOptions.find(opt => opt.selected)?.label || '';
      case 'tenant':
        return this.tenantOptions.find(opt => opt.selected)?.label || '';
      case 'application':
        return this.applicationOptions.find(opt => opt.selected)?.label || '';
      default:
        return '';
    }
  }

  onMenuAction(action: string): void {
    switch (action) {
      case 'profile':
        break;
      case 'logout':
        this.logout.emit();
        break;
    }
    this.closeDropdowns();
  }

  getUserRole(): string {
    if (!this.currentUser?.roles || this.currentUser.roles.length === 0) {
      return 'User';
    }
    
    const role = this.currentUser.roles[0];
    const roleParts = role.split('.');
    return roleParts[roleParts.length - 1] || 'User';
  }

  getInitials(name: string | undefined): string {
    if (!name) return 'U';
    
    const names = name.split(' ');
    if (names.length >= 2) {
      return (names[0][0] + names[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
} 