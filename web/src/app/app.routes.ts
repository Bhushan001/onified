import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';

// Foundation Components
import { DashboardAnalyticsComponent } from './components/foundation/dashboard-analytics/dashboard-analytics.component';
import { TenantInitializationComponent } from './components/foundation/tenant-initialization/tenant-initialization.component';
import { BrandingComponent } from './components/foundation/branding/branding.component';
import { LocalizationComponent } from './components/foundation/localization/localization.component';
import { DeploymentComponent } from './components/foundation/deployment/deployment.component';
import { NamingConventionsComponent } from './components/foundation/naming-conventions/naming-conventions.component';
import { TemplatesComponent } from './components/foundation/templates/templates.component';
import { SetupWizardComponent } from './components/foundation/setup-wizard/setup-wizard.component';
import { ComplianceComponent } from './components/foundation/compliance/compliance.component';

// Security Components
import { IdentityAccessComponent } from './components/security/identity-access/identity-access.component';
import { OrgStructureComponent } from './components/security/org-structure/org-structure.component';
import { RolesPermissionsComponent } from './components/security/roles-permissions/roles-permissions.component';

// Finance Components
import { LicensingBillingComponent } from './components/finance/licensing-billing/licensing-billing.component';

// Users Components
import { UserManagementComponent } from './components/users/user-management/user-management.component';
import { NotificationsAlertsComponent } from './components/users/notifications-alerts/notifications-alerts.component';
import { HelpSupportComponent } from './components/users/help-support/help-support.component';
import { FeedbackSettingsComponent } from './components/users/feedback-settings/feedback-settings.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
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
  { path: '**', redirectTo: '/dashboard' }
];