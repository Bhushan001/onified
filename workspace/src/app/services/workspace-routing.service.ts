import { Injectable } from '@angular/core';
import { DashboardAnalyticsComponent } from '../dashboard/foundation/dashboard-analytics/dashboard-analytics.component';
import { TenantInitializationComponent } from '../dashboard/foundation/tenant-initialization/tenant-initialization.component';
import { BrandingComponent } from '../dashboard/foundation/branding/branding.component';
import { LocalizationComponent } from '../dashboard/foundation/localization/localization.component';
import { DeploymentComponent } from '../dashboard/foundation/deployment/deployment.component';
import { NamingConventionsComponent } from '../dashboard/foundation/naming-conventions/naming-conventions.component';
import { TemplatesComponent } from '../dashboard/foundation/templates/templates.component';
import { SetupWizardComponent } from '../dashboard/foundation/setup-wizard/setup-wizard.component';
import { ComplianceComponent } from '../dashboard/foundation/compliance/compliance.component';
import { IdentityAccessComponent } from '../dashboard/security/identity-access/identity-access.component';
import { OrgStructureComponent } from '../dashboard/security/org-structure/org-structure.component';
import { RolesPermissionsComponent } from '../dashboard/security/roles-permissions/roles-permissions.component';
import { LicensingBillingComponent } from '../dashboard/finance/licensing-billing/licensing-billing.component';
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
      'dashboard/foundation/dashboard-analytics': DashboardAnalyticsComponent,
      'dashboard/foundation/tenant-initialization': TenantInitializationComponent,
      'dashboard/foundation/branding': BrandingComponent,
      'dashboard/foundation/localization': LocalizationComponent,
      'dashboard/foundation/deployment': DeploymentComponent,
      'dashboard/foundation/naming-conventions': NamingConventionsComponent,
      'dashboard/foundation/templates': TemplatesComponent,
      'dashboard/foundation/setup-wizard': SetupWizardComponent,
      'dashboard/foundation/compliance': ComplianceComponent,
      'dashboard/security/identity-access': IdentityAccessComponent,
      'dashboard/security/org-structure': OrgStructureComponent,
      'dashboard/security/roles-permissions': RolesPermissionsComponent,
      'dashboard/finance/licensing-billing': LicensingBillingComponent,
      'dashboard/users/user-management': UserManagementComponent,
      'dashboard/users/notifications-alerts': NotificationsAlertsComponent,
      'dashboard/users/help-support': HelpSupportComponent,
      'dashboard/users/feedback-settings': FeedbackSettingsComponent,
    };

    // Default to dashboard analytics if route not found
    const component = componentMap[cleanRoute] || DashboardAnalyticsComponent;
    this.currentComponent = component;
  }

  getCurrentComponent(): any {
    return this.currentComponent || DashboardAnalyticsComponent;
  }
} 