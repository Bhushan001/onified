import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { DashboardModule } from './dashboard/dashboard.module';
import { DashboardWrapperComponent } from './dashboard-wrapper.component';
import { AuthConfigComponent } from './components/auth-config/auth-config.component';
// User subcomponents
import { NotificationsAlertsComponent } from './users/notifications-alerts/notifications-alerts.component';
import { FeedbackSettingsComponent } from './users/feedback-settings/feedback-settings.component';
import { HelpSupportComponent } from './users/help-support/help-support.component';
import { UserManagementComponent } from './users/user-management/user-management.component';
// Add more imports as needed for other migrated components

@NgModule({
  declarations: [
    AppComponent,
    DashboardWrapperComponent,
    AuthConfigComponent,
    NotificationsAlertsComponent,
    FeedbackSettingsComponent,
    HelpSupportComponent,
    UserManagementComponent
    // Add more declarations as needed
  ],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    HttpClientModule,
    DashboardModule
  ],
  bootstrap: [AppComponent],
})
export class AppModule {} 