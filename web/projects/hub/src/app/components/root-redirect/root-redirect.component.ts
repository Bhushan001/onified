import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-root-redirect',
  template: '<div>Redirecting to your portal...</div>',
  standalone: true
})
export class RootRedirectComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if we're running independently (not in micro-frontend mode)
    const isIndependent = !this.isMicroFrontendMode();
    
    if (isIndependent) {
      // Check localStorage for authentication data
      const token = localStorage.getItem('onified-token');
      const user = localStorage.getItem('onified-user');
      
      if (token && user) {
        try {
          const parsedUser = JSON.parse(user);
          // Basic token validation
          if (this.isTokenNotExpired(token)) {
            this.redirectToAppropriatePortal(parsedUser.roles);
          } else {
            // Token expired, redirect to auth config
            this.router.navigate(['/auth-config']);
          }
        } catch (error) {
          // Token or user data is corrupted, redirect to auth config
          this.router.navigate(['/auth-config']);
        }
      } else {
        // No authentication data, redirect to auth config
        this.router.navigate(['/auth-config']);
      }
    } else {
      // In micro-frontend mode, redirect to dashboard
      this.router.navigate(['/dashboard']);
    }
  }

  private isTokenNotExpired(token: string): boolean {
    try {
      const payload = this.extractDataFromToken(token);
      const exp = payload.exp;
      
      if (!exp) {
        return false; // No expiration means expired
      }
      
      const now = Math.floor(Date.now() / 1000);
      return exp > now;
    } catch (error) {
      return false; // If we can't parse the token, consider it expired
    }
  }

  private extractDataFromToken(token: string): any {
    try {
      if (!token || typeof token !== 'string' || token.split('.').length < 2) {
        throw new Error('Invalid or missing JWT token');
      }
      const payload = token.split('.')[1];
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
      return JSON.parse(atob(padded));
    } catch (error) {
      return {};
    }
  }

  private isMicroFrontendMode(): boolean {
    try {
      return window.location.pathname.includes('/host/hub') || 
             window.location.href.includes('localhost:4200');
    } catch {
      return false;
    }
  }

  private redirectToAppropriatePortal(roles: string[]): void {
    if (roles && roles.includes('PLATFORM.Management.Admin')) {
      // Admin users stay in hub
      this.router.navigate(['/dashboard']);
    } else if (roles && roles.includes('PLATFORM.Management.TenantAdmin')) {
      // Tenant admins should go to console
      window.location.href = 'http://localhost:4202'; // Console app URL
    } else if (roles && roles.includes('PLATFORM.Management.User')) {
      // Regular users should go to workspace
      window.location.href = 'http://localhost:4203'; // Workspace app URL
    } else {
      // Default to hub dashboard
      this.router.navigate(['/dashboard']);
    }
  }
} 