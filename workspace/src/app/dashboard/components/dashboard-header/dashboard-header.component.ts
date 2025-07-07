import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User } from '../../../models/auth.models';

@Component({
  selector: 'app-dashboard-header',
  standalone: false,
  templateUrl: './dashboard-header.component.html',
  styleUrls: ['./dashboard-header.component.scss']
})
export class DashboardHeaderComponent {
  @Input() currentApplication: string = 'E-Verification';
  @Input() currentUser: User | null = null;
  @Input() userDropdownOpen: boolean = false;
  
  @Output() toggleUserDropdown = new EventEmitter<void>();
  @Output() closeUserDropdown = new EventEmitter<void>();
  @Output() logout = new EventEmitter<void>();

  getUserName(): string {
    return this.currentUser?.name || 'Anushka';
  }
} 