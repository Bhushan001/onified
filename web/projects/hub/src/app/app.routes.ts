import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { DashboardAnalyticsComponent } from './dashboard/foundation/dashboard-analytics/dashboard-analytics.component';
import { TenantInitializationComponent } from './dashboard/foundation/tenant-initialization/tenant-initialization.component';
import { BrandingComponent } from './dashboard/foundation/branding/branding.component';
import { LocalizationComponent } from './dashboard/foundation/localization/localization.component';
import { DeploymentComponent } from './dashboard/foundation/deployment/deployment.component';
import { NamingConventionsComponent } from './dashboard/foundation/naming-conventions/naming-conventions.component';
import { TemplatesComponent } from './dashboard/foundation/templates/templates.component';
import { SetupWizardComponent } from './dashboard/foundation/setup-wizard/setup-wizard.component';
import { ComplianceComponent } from './dashboard/foundation/compliance/compliance.component';
import { IdentityAccessComponent } from './dashboard/security/identity-access/identity-access.component';
import { OrgStructureComponent } from './dashboard/security/org-structure/org-structure.component';
import { RolesPermissionsComponent } from './dashboard/security/roles-permissions/roles-permissions.component';
import { LicensingBillingComponent } from './dashboard/finance/licensing-billing/licensing-billing.component';
import { UserManagementComponent } from './users/user-management/user-management.component';
import { NotificationsAlertsComponent } from './users/notifications-alerts/notifications-alerts.component';
import { HelpSupportComponent } from './users/help-support/help-support.component';
import { FeedbackSettingsComponent } from './users/feedback-settings/feedback-settings.component';
import { AuthConfigComponent } from './components/auth-config/auth-config.component';
import { RootRedirectComponent } from './components/root-redirect/root-redirect.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: RootRedirectComponent },
  { path: 'auth-config', component: AuthConfigComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'foundation/dashboard-analytics', pathMatch: 'full' },

      // Foundation Routes
      { path: 'foundation/core-settings', redirectTo: 'foundation/dashboard-analytics', pathMatch: 'full' },
      { path: 'foundation/dashboard-analytics', component: DashboardAnalyticsComponent },
      { path: 'foundation/tenant-initialization', component: TenantInitializationComponent },
      { path: 'foundation/branding', component: BrandingComponent },
      { path: 'foundation/localization', component: LocalizationComponent },
      { path: 'foundation/deployment', component: DeploymentComponent },
      { path: 'foundation/naming-conventions', component: NamingConventionsComponent },
      { path: 'foundation/templates', component: TemplatesComponent },
      { path: 'foundation/setup-wizard', component: SetupWizardComponent },
      { path: 'foundation/compliance', component: ComplianceComponent },

      // Security Routes
      { path: 'security/identity-access', component: IdentityAccessComponent },
      { path: 'security/org-structure', component: OrgStructureComponent },
      { path: 'security/roles-permissions', component: RolesPermissionsComponent },

      // Finance Routes
      { path: 'finance/licensing-billing', component: LicensingBillingComponent },

      // Users Routes
      { path: 'users/user-management', component: UserManagementComponent },
      { path: 'users/notifications-alerts', component: NotificationsAlertsComponent },
      { path: 'users/help-support', component: HelpSupportComponent },
      { path: 'users/feedback-settings', component: FeedbackSettingsComponent },
    ]
  },
];
