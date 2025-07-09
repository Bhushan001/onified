import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthConfigComponent } from './components/auth-config/auth-config.component';
import { AuthCallbackComponent } from './components/auth-callback/auth-callback.component';
import { UserManagementComponent } from './users/user-management/user-management.component';
import { NotificationsAlertsComponent } from './users/notifications-alerts/notifications-alerts.component';
import { HelpSupportComponent } from './users/help-support/help-support.component';
import { FeedbackSettingsComponent } from './users/feedback-settings/feedback-settings.component';

export const routes: Routes = [
  { path: '', redirectTo: '/auth-config', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent ,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      // Users Routes
      { path: 'users/user-management', component: UserManagementComponent },
      { path: 'users/notifications-alerts', component: NotificationsAlertsComponent },
      { path: 'users/help-support', component: HelpSupportComponent },
      { path: 'users/feedback-settings', component: FeedbackSettingsComponent },
    ]
  },
  { path: 'auth-config', component: AuthConfigComponent },
  { path: 'auth-callback', component: AuthCallbackComponent }
  // Add more routes as needed
];
