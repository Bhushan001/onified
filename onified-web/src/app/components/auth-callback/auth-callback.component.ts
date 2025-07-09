import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SocialProvider } from '../../models/auth.models';

@Component({
  selector: 'app-auth-callback',
  template: `
    <div class="auth-callback-container">
      <div class="auth-callback-card">
        <div class="loading-spinner">
          <div class="spinner"></div>
        </div>
        <h2>Connecting your account...</h2>
        <p>{{ statusMessage }}</p>
        <div *ngIf="errorMessage" class="error-message">
          <p>{{ errorMessage }}</p>
          <div class="button-group">
            <button class="btn btn-primary" (click)="retryAuth()">Try Again</button>
            <button class="btn btn-secondary" (click)="goToLogin()">Back to Login</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./auth-callback.component.scss'],
  standalone: false
})
export class AuthCallbackComponent implements OnInit {
  statusMessage = 'Processing your login...';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.handleCallback();
  }

  private handleCallback(): void {
    // Get URL parameters
    const code = this.route.snapshot.queryParamMap.get('code');
    const state = this.route.snapshot.queryParamMap.get('state');
    const error = this.route.snapshot.queryParamMap.get('error');
    const provider = this.route.snapshot.queryParamMap.get('provider') as SocialProvider;

    // Check for OAuth errors
    if (error) {
      this.errorMessage = `Authentication failed: ${error}`;
      this.statusMessage = 'Authentication failed';
      return;
    }

    // Check for required parameters
    if (!code || !state) {
      this.errorMessage = 'Invalid callback parameters';
      this.statusMessage = 'Authentication failed';
      return;
    }

    // Determine provider from URL path, parameter, or localStorage
    const pathProvider = this.getProviderFromPath();
    const storedProvider = localStorage.getItem('socialLoginProvider') as SocialProvider;
    const finalProvider = provider || pathProvider || storedProvider;

    if (!finalProvider) {
      this.errorMessage = 'Unable to determine authentication provider';
      this.statusMessage = 'Authentication failed';
      return;
    }

    // Clear stored provider
    localStorage.removeItem('socialLoginProvider');

    // Check if this is a social signup (check for stored signup flow)
    const signupFlow = localStorage.getItem('socialSignupFlow');
    
    if (signupFlow) {
      // This is a social signup
      this.statusMessage = `Completing ${finalProvider} signup...`;
      localStorage.removeItem('socialSignupFlow'); // Clear the stored signup flow
      
      this.authService.handleSocialSignup(code, state, finalProvider, signupFlow as any).subscribe({
        next: (result) => {
          if (result.success) {
            this.statusMessage = 'Signup successful!';
            
            // Redirect to appropriate dashboard based on user role
            const currentUser = this.authService.getCurrentUser();
            if (currentUser && currentUser.roles) {
              this.redirectToDashboard(currentUser.roles);
            } else {
              this.router.navigate(['/host', 'hub']);
            }
          } else {
            this.errorMessage = result.message || 'Signup failed';
            this.statusMessage = 'Signup failed';
          }
        },
        error: (error) => {
          let errorMsg = error.message || 'Signup failed';
          
          // Provide more helpful error messages for common issues
          if (errorMsg.includes('400 Bad Request') || errorMsg.includes('authorization code has expired')) {
            errorMsg = 'The authorization code has expired. Please try the signup process again.';
          } else if (errorMsg.includes('401 Unauthorized')) {
            errorMsg = 'Authentication failed. Please check your credentials and try again.';
          }
          
          this.errorMessage = errorMsg;
          this.statusMessage = 'Signup failed';
        }
      });
    } else {
      // This is a social login
      this.statusMessage = `Completing ${finalProvider} authentication...`;

      // Handle the social login callback
      this.authService.handleSocialLoginCallback(code, state, finalProvider).subscribe({
        next: (result) => {
          if (result.success) {
            this.statusMessage = 'Authentication successful!';
            
            // Check if this is a new user
            if (result.isNewUser) {
              // Redirect to role selection or onboarding
              this.router.navigate(['/onboarding']);
            } else {
              // Redirect to appropriate dashboard based on user role
              const currentUser = this.authService.getCurrentUser();
              if (currentUser && currentUser.roles) {
                this.redirectToDashboard(currentUser.roles);
              } else {
                this.router.navigate(['/host', 'hub']);
              }
            }
          } else {
            this.errorMessage = result.message || 'Authentication failed';
            this.statusMessage = 'Authentication failed';
          }
        },
        error: (error) => {
          let errorMsg = error.message || 'Authentication failed';
          
          // Provide more helpful error messages for common issues
          if (errorMsg.includes('400 Bad Request') || errorMsg.includes('authorization code has expired')) {
            errorMsg = 'The authorization code has expired. Please try the login process again.';
          } else if (errorMsg.includes('401 Unauthorized')) {
            errorMsg = 'Authentication failed. Please check your credentials and try again.';
          }
          
          this.errorMessage = errorMsg;
          this.statusMessage = 'Authentication failed';
        }
      });
    }
  }

  private getProviderFromPath(): SocialProvider | null {
    const path = window.location.pathname;
    if (path.includes('/google')) {
      return 'google';
    } else if (path.includes('/linkedin')) {
      return 'linkedin';
    }
    return null;
  }

  private redirectToDashboard(roles: string[]): void {
    let remote = 'hub'; // default
    
    if (roles.includes('PLATFORM.Management.Admin')) {
      remote = 'hub';
    } else if (roles.includes('PLATFORM.Management.TenantAdmin')) {
      remote = 'console';
    } else if (roles.includes('PLATFORM.Management.User')) {
      remote = 'workspace';
    }
    
    this.router.navigate(['/host', remote]);
  }

  retryAuth(): void {
    // Clear any stored state and redirect to login
    localStorage.removeItem('socialLoginProvider');
    localStorage.removeItem('socialSignupFlow');
    localStorage.removeItem('socialLoginState');
    this.router.navigate(['/login']);
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
} 