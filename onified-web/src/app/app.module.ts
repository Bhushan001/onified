import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { LoginComponent } from './components/login/login.component';
import { SignupPlatformUserComponent } from './components/signup/signup-platform-user.component';
import { SignupPlatformAdminComponent } from './components/signup/signup-platform-admin.component';
import { SignupTenantAdminComponent } from './components/signup/signup-tenant-admin.component';
import { FooterComponent } from './components/shared/footer/footer.component';
import { TestimonialComponent } from './components/shared/testimonial/testimonial.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupPlatformUserComponent,
    SignupPlatformAdminComponent,
    SignupTenantAdminComponent,
    FooterComponent,
    TestimonialComponent
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