import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: false,
  template: `
    <div class="callback-container">
      <div class="loading-spinner">
        <div class="spinner"></div>
        <p>Processing authentication...</p>
      </div>
    </div>
  `,
  styles: [`
    .callback-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      background-color: #f5f5f5;
    }
    
    .loading-spinner {
      text-align: center;
    }
    
    .spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #f3f3f3;
      border-top: 4px solid #3498db;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 20px;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    
    p {
      color: #666;
      font-size: 16px;
    }
  `]
})
export class AuthCallbackComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.handleCallback();
  }

  private handleCallback(): void {
    // Get the authorization code from URL parameters
    const code = this.route.snapshot.queryParams['code'];
    const error = this.route.snapshot.queryParams['error'];
    const state = this.route.snapshot.queryParams['state'];

    if (error) {
      console.error('Authentication error:', error);
      this.router.navigate(['/auth-config'], { 
        queryParams: { error: error } 
      });
      return;
    }

    if (!code) {
      console.error('No authorization code received');
      this.router.navigate(['/auth-config'], { 
        queryParams: { error: 'no_code' } 
      });
      return;
    }

    // For social login, we don't need to exchange the code
    // The backend will handle the social login flow
    // Just redirect to auth-config which will check for existing tokens
    this.router.navigate(['/auth-config']);
  }
} 