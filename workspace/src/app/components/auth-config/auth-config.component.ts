import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-auth-config',
  templateUrl: './auth-config.component.html',
  styleUrls: ['./auth-config.component.scss'],  
  standalone: false
})
export class AuthConfigComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  hasToken = false;
  hasUserData = false;
  isTokenValid = false;
  isAuthenticated = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      identifier: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
    this.checkAuthStatus();
    console.log(this.isAuthenticated);
  }

  private checkAuthStatus(): void {
    const isIndependent = !this.isMicroFrontendMode();
    if (isIndependent) {
      const token = localStorage.getItem(environment.auth.tokenKey);
      const user = localStorage.getItem(environment.auth.userKey);
      this.hasToken = !!token;
      this.hasUserData = !!user;
      if (token && user) {
        try {
          const parsedUser = JSON.parse(user);
          this.isTokenValid = this.isTokenNotExpired(token);
          this.isAuthenticated = this.isTokenValid;
          if (this.isAuthenticated) {
            this.redirectToAppropriatePortal(parsedUser.roles);
          }
        } catch (error) {
          this.isTokenValid = false;
          this.isAuthenticated = false;
        }
      }
    } else {
      this.isAuthenticated = this.authService.isAuthenticated();
      if (this.isAuthenticated) {
        this.router.navigate(['/dashboard']);
      }
    }
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      const loginData = {
        identifier: this.loginForm.get('identifier')?.value,
        password: this.loginForm.get('password')?.value
      };
      this.authService.login(loginData).subscribe({
        next: (result) => {
          this.isLoading = false;
          if (result.success) {
            const currentUser = this.authService.getCurrentUser();
            if (currentUser && currentUser.roles) {
              this.redirectToAppropriatePortal(currentUser.roles);
            } else {
              this.router.navigate(['/dashboard']);
            }
          } else {
            this.errorMessage = result.message || 'Login failed';
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.message || 'An error occurred during login';
        }
      });
    }
  }

  private redirectToAppropriatePortal(roles: string[]): void {
    console.log(roles);
    
    if (roles && roles.includes('PLATFORM.Management.Admin')) {
      window.location.href = 'http://localhost:4300'; // Hub app URL
    } else if (roles && roles.includes('PLATFORM.Management.TenantAdmin')) {
      window.location.href = 'http://localhost:4400'; // Console app URL
    } else if (roles && roles.includes('PLATFORM.Management.User')) {
      this.router.navigate(['/dashboard']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  private isMicroFrontendMode(): boolean {
    try {
      return window.location.pathname.includes('/host/workspace');
    } catch {
      return false;
    }
  }

  private isTokenNotExpired(token: string): boolean {
    try {
      if (!token || typeof token !== 'string' || token.split('.').length < 2) {
        return false;
      }
      const payload = token.split('.')[1];
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
      const parsed = JSON.parse(atob(padded));
      const exp = parsed.exp;
      if (!exp) return false;
      const now = Math.floor(Date.now() / 1000);
      return exp > now;
    } catch {
      return false;
    }
  }

  refreshAuth(): void {
    this.checkAuthStatus();
  }

  clearAuth(): void {
    localStorage.removeItem(environment.auth.tokenKey);
    localStorage.removeItem(environment.auth.userKey);
    this.checkAuthStatus();
  }
} 