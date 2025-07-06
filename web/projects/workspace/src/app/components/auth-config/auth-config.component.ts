import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-config',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="auth-config-container">
      <div class="auth-config-card">
        <div class="header">
          <h1>Workspace Configuration Required</h1>
          <p>This application requires valid authentication credentials to run independently.</p>
        </div>
        <div class="content">
          <div class="info-section">
            <h3>Authentication Status</h3>
            <div class="status-item">
              <span class="label">Token:</span>
              <span class="value" [class.valid]="hasToken" [class.invalid]="!hasToken">
                {{ hasToken ? 'Present' : 'Missing' }}
              </span>
            </div>
            <div class="status-item">
              <span class="label">User Data:</span>
              <span class="value" [class.valid]="hasUserData" [class.invalid]="!hasUserData">
                {{ hasUserData ? 'Present' : 'Missing' }}
              </span>
            </div>
            <div class="status-item">
              <span class="label">Token Valid:</span>
              <span class="value" [class.valid]="isTokenValid" [class.invalid]="!isTokenValid">
                {{ isTokenValid ? 'Valid' : 'Invalid/Expired' }}
              </span>
            </div>
          </div>
          <div class="login-section" *ngIf="!isAuthenticated">
            <h3>Login to Continue</h3>
            <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
              <div class="form-group">
                <label for="identifier">Username/Email/Phone</label>
                <input type="text" id="identifier" formControlName="identifier" placeholder="Enter your username, email, or phone number" class="form-control">
                <div class="error" *ngIf="loginForm.get('identifier')?.invalid && loginForm.get('identifier')?.touched">
                  Username/Email/Phone is required
                </div>
              </div>
              <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" formControlName="password" placeholder="Enter your password" class="form-control">
                <div class="error" *ngIf="loginForm.get('password')?.invalid && loginForm.get('password')?.touched">
                  Password is required
                </div>
              </div>
              <button type="submit" class="btn-primary" [disabled]="loginForm.invalid || isLoading">
                {{ isLoading ? 'Signing in...' : 'Sign In' }}
              </button>
            </form>
            <div class="error-message" *ngIf="errorMessage">
              {{ errorMessage }}
            </div>
          </div>
          <div class="actions">
            <button class="btn-secondary" (click)="refreshAuth()">Refresh Authentication</button>
            <button class="btn-secondary" (click)="clearAuth()">Clear Authentication</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-config-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; }
    .auth-config-card { background: white; border-radius: 12px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2); max-width: 500px; width: 100%; overflow: hidden; }
    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }
    .header h1 { margin: 0 0 10px 0; font-size: 24px; font-weight: 600; }
    .header p { margin: 0; opacity: 0.9; font-size: 14px; }
    .content { padding: 30px; }
    .info-section { margin-bottom: 30px; }
    .info-section h3 { margin: 0 0 15px 0; color: #333; font-size: 18px; }
    .status-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid #eee; }
    .status-item:last-child { border-bottom: none; }
    .label { font-weight: 500; color: #666; }
    .value { font-weight: 600; padding: 4px 8px; border-radius: 4px; font-size: 12px; }
    .value.valid { background: #d4edda; color: #155724; }
    .value.invalid { background: #f8d7da; color: #721c24; }
    .login-section { margin-bottom: 30px; }
    .login-section h3 { margin: 0 0 20px 0; color: #333; font-size: 18px; }
    .form-group { margin-bottom: 20px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: 500; color: #333; }
    .form-control { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; transition: border-color 0.3s; }
    .form-control:focus { outline: none; border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1); }
    .error { color: #dc3545; font-size: 12px; margin-top: 5px; }
    .btn-primary { width: 100%; padding: 12px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 6px; font-size: 16px; font-weight: 600; cursor: pointer; transition: transform 0.2s; }
    .btn-primary:hover:not(:disabled) { transform: translateY(-2px); }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-secondary { padding: 10px 20px; background: #6c757d; color: white; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; margin-right: 10px; transition: background-color 0.3s; }
    .btn-secondary:hover { background: #5a6268; }
    .actions { display: flex; justify-content: center; gap: 10px; }
    .error-message { background: #f8d7da; color: #721c24; padding: 12px; border-radius: 6px; margin-top: 15px; font-size: 14px; }
  `]
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
  }

  private checkAuthStatus(): void {
    const isIndependent = !this.isMicroFrontendMode();
    if (isIndependent) {
      const token = localStorage.getItem('onified-token');
      const user = localStorage.getItem('onified-user');
      console.log(user);
      
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
      window.location.href = 'http://localhost:4400';
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
    // Implement token expiry check logic here
    return true;
  }

  refreshAuth(): void {
    this.checkAuthStatus();
  }

  clearAuth(): void {
    localStorage.removeItem('onified-token');
    localStorage.removeItem('onified-user');
    this.checkAuthStatus();
  }
} 