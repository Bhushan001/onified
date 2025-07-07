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
    if (this.authService.isAuthenticated()) {
      const currentUser = this.authService.getCurrentUser();
      if (currentUser && currentUser.roles) {
        // Redirect to appropriate portal based on user role
        if (currentUser.roles.includes('PLATFORM.Management.Admin')) {
          this.router.navigate(['/host', 'hub']);
        } else if (currentUser.roles.includes('PLATFORM.Management.TenantAdmin')) {
          this.router.navigate(['/host', 'console']);
        } else if (currentUser.roles.includes('PLATFORM.Management.User')) {
          this.router.navigate(['/host', 'workspace']);
        } else {
          // Default to hub for authenticated users without specific roles
          this.router.navigate(['/host', 'hub']);
        }
      } else {
        // Default to hub for authenticated users
        this.router.navigate(['/host', 'hub']);
      }
      return false; // Prevent access to the current route
    }
    
    // Not authenticated, allow access to login
    return true;
  }
} 