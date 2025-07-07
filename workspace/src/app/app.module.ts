import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthConfigComponent } from './components/auth-config/auth-config.component';
import { SidebarComponent } from './dashboard/sidebar/sidebar.component';
import { ModernHeaderComponent } from './dashboard/modern-header/modern-header.component';
// Dashboard subcomponents
import { OrgStructureComponent } from './dashboard/security/org-structure/org-structure.component';
import { RolesPermissionsComponent } from './dashboard/security/roles-permissions/roles-permissions.component';
import { IdentityAccessComponent } from './dashboard/security/identity-access/identity-access.component';
import { BrandingComponent } from './dashboard/foundation/branding/branding.component';
import { DashboardAnalyticsComponent } from './dashboard/foundation/dashboard-analytics/dashboard-analytics.component';
import { NamingConventionsComponent } from './dashboard/foundation/naming-conventions/naming-conventions.component';
import { ComplianceComponent } from './dashboard/foundation/compliance/compliance.component';
import { TenantInitializationComponent } from './dashboard/foundation/tenant-initialization/tenant-initialization.component';
import { DeploymentComponent } from './dashboard/foundation/deployment/deployment.component';
import { TemplatesComponent } from './dashboard/foundation/templates/templates.component';
import { SetupWizardComponent } from './dashboard/foundation/setup-wizard/setup-wizard.component';
import { LocalizationComponent } from './dashboard/foundation/localization/localization.component';
import { LicensingBillingComponent } from './dashboard/finance/licensing-billing/licensing-billing.component';
// User subcomponents
import { NotificationsAlertsComponent } from './users/notifications-alerts/notifications-alerts.component';
import { FeedbackSettingsComponent } from './users/feedback-settings/feedback-settings.component';
import { HelpSupportComponent } from './users/help-support/help-support.component';
import { UserManagementComponent } from './users/user-management/user-management.component';
// Add more imports as needed for other migrated components

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    AuthConfigComponent,
    SidebarComponent,
    ModernHeaderComponent,
    OrgStructureComponent,
    RolesPermissionsComponent,
    IdentityAccessComponent,
    BrandingComponent,
    DashboardAnalyticsComponent,
    NamingConventionsComponent,
    ComplianceComponent,
    TenantInitializationComponent,
    DeploymentComponent,
    TemplatesComponent,
    SetupWizardComponent,
    LocalizationComponent,
    LicensingBillingComponent,
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
    HttpClientModule
  ],
  bootstrap: [AppComponent],
})
export class AppModule {} 