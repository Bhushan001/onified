import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { SignupPlatformUserComponent } from './components/signup/signup-platform-user.component';
import { SignupPlatformAdminComponent } from './components/signup/signup-platform-admin.component';
import { SignupTenantAdminComponent } from './components/signup/signup-tenant-admin.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupPlatformUserComponent },
  { path: 'signup/platform-admin', component: SignupPlatformAdminComponent },
  { path: 'signup/tenant-admin', component: SignupTenantAdminComponent },
  { path: 'signup/platform-user', component: SignupPlatformUserComponent }
];
