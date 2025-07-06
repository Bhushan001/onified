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
    const isIndependent = !this.isMicroFrontendMode();
    if (isIndependent) {
      const token = localStorage.getItem('onified-token');
      const user = localStorage.getItem('onified-user');
      if (token && user) {
        try {
          const parsedUser = JSON.parse(user);
          console.log(parsedUser);
          console.log(this.isTokenNotExpired(token));
          
          if (this.isTokenNotExpired(token)) {
            if (this.shouldUserBeInThisPortal(parsedUser.roles)) {
              return true;
            } else {
              this.redirectToAppropriatePortal(parsedUser.roles);
              return false;
            }
          }
        } catch (error) {}
      }
      this.router.navigate(['/auth-config']);
      return false;
    } else {
      if (this.authService.isAuthenticated()) {
        return true;
      } else {
        this.router.navigate(['/auth-config']);
        return false;
      }
    }
  }

  private isMicroFrontendMode(): boolean {
    try {
      return window.location.pathname.includes('/host/hub') || 
             window.location.href.includes('localhost:4300');
    } catch {
      return false;
    }
  }

  private shouldUserBeInThisPortal(roles: string[]): boolean {
    // Console portal is for Tenant Admins
    return roles && roles.includes('PLATFORM.Management.Admin');
  }

  private redirectToAppropriatePortal(roles: string[]): void {
    if (roles && roles.includes('PLATFORM.Management.Admin')) {
      this.router.navigate(['/dashboard']);
    } else if (roles && roles.includes('PLATFORM.Management.TenantAdmin')) {
      window.location.href = 'http://localhost:4400'; // Hub app URL
    } else if (roles && roles.includes('PLATFORM.Management.User')) {
      window.location.href = 'http://localhost:4500'; // Workspace app URL
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  private isTokenNotExpired(token: string): boolean {
    try {
      const payload = this.extractDataFromToken(token);
      const exp = payload.exp;
      if (!exp) {
        return false;
      }
      const now = Math.floor(Date.now() / 1000);
      return exp > now;
    } catch (error) {
      return false;
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
} 