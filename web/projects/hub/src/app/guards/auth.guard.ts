import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
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
            // Check if user should be in this portal
            if (this.shouldUserBeInThisPortal(parsedUser.roles)) {
              return true; // Allow access
            } else {
              // User should be in a different portal, redirect them
              this.redirectToAppropriatePortal(parsedUser.roles);
              return false;
            }
          }
        } catch (error) {
          // Token or user data is corrupted
        }
      }
      
      // Not authenticated or token expired, redirect to auth config
      this.router.navigate(['/auth-config']);
      return false;
    } else {
      // In micro-frontend mode, use the existing auth service logic
      if (this.authService.isAuthenticated()) {
        return true;
      } else {
        this.router.navigate(['/auth-config']);
        return false;
      }
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

  private shouldUserBeInThisPortal(roles: string[]): boolean {
    // Hub portal is for Platform Admins
    return roles && roles.includes('PLATFORM.Management.Admin');
  }

  private redirectToAppropriatePortal(roles: string[]): void {
    if (roles && roles.includes('PLATFORM.Management.Admin')) {
      // Admin users should stay in hub
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