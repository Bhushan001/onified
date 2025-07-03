import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { PortalHostComponent } from './components/portal-host/portal-host.component';
import { RootRedirectComponent } from './components/root-redirect/root-redirect.component';

export const routes: Routes = [
  { path: '', component: RootRedirectComponent },
  { path: 'login', component: LoginComponent },
  { path: 'host/:remote', component: PortalHostComponent},
  { path: 'create-platform-admin', loadComponent: () => import('./components/signup/signup-platform-admin.component').then(m => m.SignupPlatformAdminComponent) },
  { path: 'create-tenant-admin', loadComponent: () => import('./components/signup/signup-tenant-admin.component').then(m => m.SignupTenantAdminComponent) },
  { path: 'create-user', loadComponent: () => import('./components/signup/signup-platform-user.component').then(m => m.SignupPlatformUserComponent) },
  { path: '**', redirectTo: '/login' }
];