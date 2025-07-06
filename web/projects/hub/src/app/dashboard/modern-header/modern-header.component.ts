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
  templateUrl: './modern-header.component.html',
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
      case 'preferences':
        break;
      case 'help':
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