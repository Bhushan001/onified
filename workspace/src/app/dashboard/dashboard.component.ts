import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { User } from '../models/auth.models';
import { Application } from './components/application-cards/application-cards.component';
import { Task } from './components/task-list/task-list.component';
import { Notification } from './components/notification-list/notification-list.component';

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
  userDropdownOpen: boolean = false;

  activeTab: string = 'overview';
  currentApplication: string = 'E-Verification'; // Default current application

  // Mock data
  applications: Application[] = [
    { title: 'E-Verification', desc: 'Electronic verification system', icon: 'fa-id-card', badge: 'Current' },
    { title: 'Onboarding', desc: 'New user onboarding process', icon: 'fa-user-plus', badge: 'Default' },
    { title: 'DB Verification', desc: 'Database verification tools', icon: 'fa-database' },
    { title: 'Document Manager', desc: 'Document management system', icon: 'fa-file-text' },
    { title: 'Analytics', desc: 'Data analytics dashboard', icon: 'fa-bar-chart' },
    { title: 'Settings', desc: 'System configuration', icon: 'fa-cog' }
  ];

  tasks: Task[] = [
    {
      title: 'Review E-Verification Application',
      tag: 'E-Verification',
      priority: 'high',
      due: 'Due in 2 hours',
      assigned: 'Assigned to you',
      actions: ['Review', 'Approve', 'Reject']
    },
    {
      title: 'Complete Onboarding Process',
      tag: 'Onboarding',
      priority: 'medium',
      due: 'Due tomorrow',
      assigned: 'Assigned to you',
      actions: ['Complete', 'Request Extension']
    },
    {
      title: 'Verify Database Records',
      tag: 'DB Verification',
      priority: 'low',
      due: 'Due in 3 days',
      assigned: 'Assigned to you',
      actions: ['Verify', 'Mark Complete']
    }
  ];

  notifications: Notification[] = [
    {
      type: 'warning',
      title: 'Document Verification Required',
      desc: 'Additional documents needed for E-Verification application',
      meta: '2 hours ago',
      actions: ['View', 'Resolve']
    },
    {
      type: 'info',
      title: 'New Application Submitted',
      desc: 'A new onboarding application has been submitted',
      meta: '4 hours ago',
      actions: ['View', 'Approve']
    },
    {
      type: 'success',
      title: 'Task Completed',
      desc: 'Database verification task has been completed successfully',
      meta: '1 day ago',
      actions: ['View']
    }
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

  // Application selection functionality
  onApplicationSelect(app: Application): void {
    this.currentApplication = app.title;
    
    // Update badges - remove 'Current' from all apps and add to selected app
    this.applications.forEach(application => {
      if (application.badge === 'Current') {
        application.badge = '';
      }
    });
    app.badge = 'Current';
    
    console.log('Application selected:', app.title);
  }

  // Header events
  onToggleUserDropdown(): void {
    this.userDropdownOpen = !this.userDropdownOpen;
  }

  onCloseUserDropdown(): void {
    this.userDropdownOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    // Close dropdown when clicking outside
    if (!(event.target as Element).closest('.user-profile')) {
      this.userDropdownOpen = false;
    }
  }

  // Tab events
  onTabChange(tabId: string): void {
    this.activeTab = tabId;
  }

  onUserPreferences(): void {
    console.log('User preferences clicked');
    // Add user preferences logic here
  }

  onViewAllApplications(): void {
    this.activeTab = 'applications';
  }

  // Task events
  onViewAllTasks(): void {
    this.activeTab = 'tasks';
  }

  onTaskAction(event: {task: Task, action: string}): void {
    console.log('Task action:', event.action, 'for task:', event.task.title);
    // Add task action logic here
  }

  // Notification events
  onViewAllNotifications(): void {
    this.activeTab = 'notifications';
  }

  onNotificationAction(event: {notification: Notification, action: string}): void {
    console.log('Notification action:', event.action, 'for notification:', event.notification.title);
    // Add notification action logic here
  }
}