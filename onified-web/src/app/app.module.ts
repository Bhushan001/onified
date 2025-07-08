import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { LoginComponent } from './components/login/login.component';
import { SignupPlatformUserComponent } from './components/signup/signup-platform-user.component';
import { SignupPlatformAdminComponent } from './components/signup/signup-platform-admin.component';
import { SignupTenantAdminComponent } from './components/signup/signup-tenant-admin.component';
import { FooterComponent } from './components/shared/footer/footer.component';
import { TestimonialComponent } from './components/shared/testimonial/testimonial.component';
import { PortalHostComponent } from './components/portal-host/portal-host.component';
import { RootRedirectComponent } from './components/root-redirect/root-redirect.component';
import { AuthCallbackComponent } from './components/auth-callback/auth-callback.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupPlatformUserComponent,
    SignupPlatformAdminComponent,
    SignupTenantAdminComponent,
    FooterComponent,
    TestimonialComponent,
    PortalHostComponent,
    RootRedirectComponent,
    AuthCallbackComponent
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