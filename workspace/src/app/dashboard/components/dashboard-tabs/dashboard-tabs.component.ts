import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-tabs',
  standalone: false,
  templateUrl: './dashboard-tabs.component.html',
  styleUrls: ['./dashboard-tabs.component.scss']
})
export class DashboardTabsComponent {
  @Input() activeTab: string = 'overview';
  @Input() currentUser: any = null;
  
  @Output() tabChange = new EventEmitter<string>();
  @Output() userPreferences = new EventEmitter<void>();

  tabs = [
    { id: 'overview', label: 'Overview' },
    { id: 'applications', label: 'Your Applications' },
    { id: 'notifications', label: 'Notifications' },
    { id: 'tasks', label: 'My Tasks' },
    { id: 'activity', label: 'Recent Activity' },
    { id: 'help', label: 'Help & Resources' }
  ];

  onTabClick(tabId: string): void {
    this.tabChange.emit(tabId);
  }

  onUserPreferences(): void {
    this.userPreferences.emit();
  }
} 