import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-sidebar',
  standalone: false,
  templateUrl: './dashboard-sidebar.component.html',
  styleUrls: ['./dashboard-sidebar.component.scss']
})
export class DashboardSidebarComponent {
  // Sidebar navigation items
  navItems = [
    { icon: 'fa-home', label: 'Dashboard', active: true },
    { icon: 'fa-id-card', label: 'E-Verification', active: false },
    { icon: 'fa-user-plus', label: 'Onboarding', active: false },
    { icon: 'fa-database', label: 'DB Verification', active: false },
    { icon: 'fa-cog', label: 'Settings', active: false }
  ];
} 