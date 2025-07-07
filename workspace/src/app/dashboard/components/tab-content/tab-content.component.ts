import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Application } from '../application-cards/application-cards.component';
import { Task } from '../task-list/task-list.component';
import { Notification } from '../notification-list/notification-list.component';

@Component({
  selector: 'app-tab-content',
  standalone: false,
  templateUrl: './tab-content.component.html',
  styleUrls: ['./tab-content.component.scss']
})
export class TabContentComponent {
  @Input() activeTab: string = 'overview';
  @Input() applications: Application[] = [];
  @Input() tasks: Task[] = [];
  @Input() notifications: Notification[] = [];
  
  @Output() applicationSelect = new EventEmitter<Application>();
  @Output() viewAllApplications = new EventEmitter<void>();
  @Output() viewAllTasks = new EventEmitter<void>();
  @Output() viewAllNotifications = new EventEmitter<void>();
  @Output() taskAction = new EventEmitter<{task: Task, action: string}>();
  @Output() notificationAction = new EventEmitter<{notification: Notification, action: string}>();

  onApplicationClick(app: Application): void {
    this.applicationSelect.emit(app);
  }

  onViewAllApplications(): void {
    this.viewAllApplications.emit();
  }

  onViewAllTasks(): void {
    this.viewAllTasks.emit();
  }

  onViewAllNotifications(): void {
    this.viewAllNotifications.emit();
  }

  onTaskAction(task: Task, action: string): void {
    this.taskAction.emit({ task, action });
  }

  onNotificationAction(notification: Notification, action: string): void {
    this.notificationAction.emit({ notification, action });
  }
} 