import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface Notification {
  type: 'error' | 'warning' | 'info' | 'success';
  title: string;
  desc: string;
  meta: string;
  actions: string[];
}

@Component({
  selector: 'app-notification-list',
  standalone: false,
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.scss']
})
export class NotificationListComponent {
  @Input() notifications: Notification[] = [];
  @Input() showViewAll: boolean = true;
  @Input() title: string = 'Notifications';
  
  @Output() viewAll = new EventEmitter<void>();
  @Output() notificationAction = new EventEmitter<{notification: Notification, action: string}>();

  onViewAll(): void {
    this.viewAll.emit();
  }

  onNotificationAction(notification: Notification, action: string): void {
    this.notificationAction.emit({ notification, action });
  }
} 