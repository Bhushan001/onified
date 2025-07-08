import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { SignupPlatformUserComponent } from './components/signup/signup-platform-user.component';
import { SignupPlatformAdminComponent } from './components/signup/signup-platform-admin.component';
import { SignupTenantAdminComponent } from './components/signup/signup-tenant-admin.component';
import { PortalHostComponent } from './components/portal-host/portal-host.component';
import { RootRedirectComponent } from './components/root-redirect/root-redirect.component';
import { AuthCallbackComponent } from './components/auth-callback/auth-callback.component';

export const routes: Routes = [
  { path: '', component: RootRedirectComponent },
  { path: 'login', component: LoginComponent },
  { path: 'host/:remote', component: PortalHostComponent},
  { path: 'create-platform-admin', component: SignupPlatformAdminComponent },
  { path: 'create-tenant-admin', component: SignupTenantAdminComponent },
  { path: 'create-user', component: SignupPlatformUserComponent },
  { path: 'auth/callback', component: AuthCallbackComponent },
  { path: '**', redirectTo: '/login' }
];
