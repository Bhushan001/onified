import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User } from '../../../models/auth.models';

/**
 * Dashboard Header Component
 * 
 * Provides the top navigation bar with search, notifications, and user menu.
 * 
 * @component DashboardHeaderComponent
 */
@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <header class="dashboard-header">
      <div class="header-left">
        <button class="menu-toggle" (click)="onToggleSidebar()" 
                [class.hidden]="!sidebarCollapsed">
          <i class="icon-menu"></i>
        </button>
        
        <div class="search-box">
          <i class="search-icon"></i>
          <input type="text" placeholder="Search..." class="search-input">
        </div>
      </div>

      <div class="header-right">
        <div class="header-actions">
          <button class="action-btn notification-btn">
            <i class="icon-bell"></i>
            <span class="notification-badge">3</span>
          </button>
          
          <button class="action-btn">
            <i class="icon-mail"></i>
          </button>
          
          <div class="user-menu" *ngIf="currentUser">
            <div class="user-avatar">
              <div class="avatar-placeholder" *ngIf="!currentUser.avatar">
                {{ getInitials(currentUser.name) }}
              </div>
              <img *ngIf="currentUser.avatar" 
                   [src]="currentUser.avatar" 
                   [alt]="currentUser.name"
                   (error)="onImageError($event)">
            </div>
            <div class="user-details">
              <div class="user-name">{{ currentUser.name }}</div>
              <div class="user-status">Online</div>
            </div>
            <i class="dropdown-arrow"></i>
          </div>
        </div>
      </div>
    </header>
  `,
  styleUrls: ['./dashboard-header.component.scss']
})
export class DashboardHeaderComponent {
  @Input() currentUser: User | null = null;
  @Input() sidebarCollapsed: boolean = false;
  @Output() toggleSidebar = new EventEmitter<void>();

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
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