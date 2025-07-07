import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { User } from '../models/auth.models';

/**
 * Main Dashboard Component for Onified.ai Admin Panel
 * 
 * This component serves as the main dashboard layout after successful authentication.
 * It provides a comprehensive admin interface with sidebar navigation, metrics cards,
 * charts, and data tables for managing the application.
 * 
 * Features:
 * - Responsive sidebar navigation
 * - Real-time metrics display
 * - Interactive charts and graphs
 * - Data management tables
 * - User profile management
 * - Role-based access control
 * 
 * @component DashboardComponent
 * @implements OnInit
 */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  sidebarCollapsed: boolean = false;
  mobileSidebarOpen: boolean = false;

  activeTab: string = 'overview';

  // Mock data
  applications = [
    { title: 'E-Verification', desc: 'Verify identities and business credentials', icon: 'fa-id-card', badge: 'Current' },
    { title: 'Track & Trace', desc: 'Verify identities and business credentials', icon: 'fa-search', badge: 'Default' },
    { title: 'Market Maps', desc: 'Verify identities and business credentials', icon: 'fa-map' },
    { title: 'Asset Management', desc: 'Monitor performance and insights', icon: 'fa-line-chart' },
    { title: 'SOP Management', desc: 'Create, review and manage procedures', icon: 'fa-file-text' },
    { title: 'Compliance', desc: 'Compliance management and reporting', icon: 'fa-shield' },
    { title: 'User Directory', desc: 'Manage users and permissions', icon: 'fa-users' },
    { title: 'Billing', desc: 'View and manage billing', icon: 'fa-credit-card' }
  ];

  notifications = [
    { type: 'error', title: 'Verification Error', desc: 'PAN Verification failed for acb firm', meta: '10 minutes ago', actions: ['Acknowledge', 'View'] },
    { type: 'warning', title: 'SOP Review Due', desc: '5 SOPs are due for review this month', meta: '1 hour ago', actions: ['Dismiss', 'View'] },
    { type: 'info', title: 'System Update', desc: 'Onified will be updated to v2.4.0 on April 15th', meta: '10 Minutes ago', actions: ['Dismiss', 'Learn'] },
    { type: 'info', title: 'New Feature', desc: 'A new dashboard feature is now available', meta: '2 hours ago', actions: ['Learn More'] },
    { type: 'success', title: 'Verification Complete', desc: 'John Doe was successfully verified', meta: 'Yesterday', actions: ['View'] }
  ];

  tasks = [
    { title: 'Review SOP 05/2023', tag: 'SOP Management', priority: 'high', due: 'Due tomorrow', assigned: 'Assigned 2 days ago', actions: ['Reassign', 'Review'] },
    { title: 'Approve verification for John Doe', tag: 'E-Verification', priority: 'medium', due: 'Due tomorrow', assigned: 'Assigned Yesterday', actions: ['View Details', 'Approve'] },
    { title: 'Update Market Maps', tag: 'Market Maps', priority: 'low', due: 'Due in 3 days', assigned: 'Assigned Today', actions: ['Edit', 'Complete'] },
    { title: 'Asset report review', tag: 'Asset Management', priority: 'medium', due: 'Due next week', assigned: 'Assigned 3 days ago', actions: ['View', 'Comment'] },
    { title: 'Compliance check', tag: 'Compliance', priority: 'high', due: 'Due in 2 days', assigned: 'Assigned Today', actions: ['Start', 'Delegate'] },
    { title: 'User onboarding', tag: 'Onboarding', priority: 'medium', due: 'Due in 5 days', assigned: 'Assigned Yesterday', actions: ['View', 'Approve'] },
    { title: 'Billing review', tag: 'Billing', priority: 'low', due: 'Due in 1 week', assigned: 'Assigned 4 days ago', actions: ['View', 'Pay'] }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log(this.authService.isAuthenticated());
    
    // Check if user is authenticated
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    // Get current user information
    this.currentUser = this.authService.getCurrentUser();
    
    // Subscribe to user changes
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    // Check if mobile and collapse sidebar by default
    if (window.innerWidth <= 1024) {
      this.sidebarCollapsed = true;
    }
  }

  onToggleSidebar(): void {
    if (window.innerWidth <= 768) {
      // Mobile: toggle mobile sidebar
      this.mobileSidebarOpen = !this.mobileSidebarOpen;
    } else {
      // Desktop: toggle collapsed state
      this.sidebarCollapsed = !this.sidebarCollapsed;
    }
  }

  onLogout(): void {
    this.authService.clearAuthData();
    this.router.navigate(['/login']);
    this.authService.logout().subscribe({ error: () => {} });
  }

  getTabLabel(tab: string): string {
    switch (tab) {
      case 'overview': return 'Overview';
      case 'applications': return 'Your Applications';
      case 'notifications': return 'Notifications';
      case 'tasks': return 'My Tasks';
      case 'activity': return 'Recent Activity';
      case 'help': return 'Help & Resources';
      default: return '';
    }
  }

  // For overview tab: show a summary (first 3 apps, 3 notifications, 3 tasks)
  get overviewApplications() { return this.applications.slice(0, 3); }
  get overviewNotifications() { return this.notifications.slice(0, 3); }
  get overviewTasks() { return this.tasks.slice(0, 3); }
}