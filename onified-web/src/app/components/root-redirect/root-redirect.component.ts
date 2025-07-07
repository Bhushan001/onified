import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-root-redirect',
  standalone: false,
  template: '<div>Redirecting...</div>',
  styles: [`
    div {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      font-size: 18px;
      color: #666;
    }
  `]
})
export class RootRedirectComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
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
    } else {
      // Not authenticated, redirect to login
      this.router.navigate(['/login']);
    }
  }
} 