import { Injectable } from '@angular/core';
import { DashboardComponent } from '../dashboard/dashboard.component';
import { UserManagementComponent } from '../users/user-management/user-management.component';
import { NotificationsAlertsComponent } from '../users/notifications-alerts/notifications-alerts.component';
import { HelpSupportComponent } from '../users/help-support/help-support.component';
import { FeedbackSettingsComponent } from '../users/feedback-settings/feedback-settings.component';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceRoutingService {
  private currentComponent: any = null;

  constructor() {}

  navigateToRoute(route: string): void {
    // Remove leading slash if present
    const cleanRoute = route.startsWith('/') ? route.substring(1) : route;
    
    // Map routes to components
    const componentMap: { [key: string]: any } = {
      'dashboard': DashboardComponent,
      'dashboard/users/user-management': UserManagementComponent,
      'dashboard/users/notifications-alerts': NotificationsAlertsComponent,
      'dashboard/users/help-support': HelpSupportComponent,
      'dashboard/users/feedback-settings': FeedbackSettingsComponent,
    };

    // Default to dashboard analytics if route not found
    const component = componentMap[cleanRoute] || DashboardComponent;
    this.currentComponent = component;
  }

  getCurrentComponent(): any {
    return this.currentComponent || DashboardComponent;
  }
} 