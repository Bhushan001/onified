import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { InternalLoginRequest } from '../../models/auth.models';
import { TestimonialComponent } from '../shared/testimonial/testimonial.component';
import { FooterComponent } from '../shared/footer/footer.component';

/**
 * Login Component for Onified.ai Application
 * 
 * Handles user authentication with multiple login methods:
 * - Username/password authentication (sends "username" field)
 * - Phone number authentication (sends "phone" field)
 * - Tenant domain authentication (sends "domain" field)
 * - QR code authentication
 * 
 * Updated to send appropriate field names based on identifier type.
 * 
 * Features:
 * - Multi-step login flow (identifier → password)
 * - Automatic identifier type detection
 * - Smart request formatting (username/phone/domain fields)
 * - Real-time form validation
 * - Loading states and error handling
 * - Responsive design with testimonial section
 * - Keyboard navigation support
 * - Username validation and availability checking
 * 
 * @component LoginComponent
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, TestimonialComponent, FooterComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  isQRLoading = false;
  errorMessage = '';
  showPassword = false;
  currentStep: 'identifier' | 'password' = 'identifier';
  identifier = '';
  password = '';
  identifierType: 'username' | 'phone' | 'domain' = 'username';
  
  testimonial = {
    quote: "Onified has transformed how we manage our enterprise applications. The seamless integration and powerful features have made our workflow incredibly efficient.",
    author: "Sarah Johnson",
    title: "CTO, TechCorp",
    avatar: "assets/images/testimonials/sarah.jpg"
  };

  constructor(
    private fb: FormBuilder,
    public authService: AuthService, // Changed to public for template access
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      identifier: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });
  }

  ngOnInit(): void {
    console.log('Login component initialized');
    
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onContinue(): void {
    if (this.currentStep === 'identifier') {
      if (this.identifier && this.identifier.trim()) {
        this.identifierType = this.authService.getIdentifierType(this.identifier);
        this.currentStep = 'password';
      }
    } else {
      this.onSubmit();
    }
  }

  onBackToIdentifier(): void {
    this.currentStep = 'identifier';
  }

  onIdentifierChange(): void {
    this.identifierType = this.authService.getIdentifierType(this.identifier);
  }

  onQRLogin(): void {
    this.isQRLoading = true;
    // Implement QR login logic here
    setTimeout(() => {
      this.isQRLoading = false;
    }, 2000);
  }

  getIdentifierLabel(): string {
    switch (this.identifierType) {
      case 'phone': return 'Phone Number';
      case 'domain': return 'Domain';
      default: return 'Username';
    }
  }

  getPasswordPlaceholder(): string {
    return 'Enter your password';
  }

  getButtonText(): string {
    if (this.isLoading) return 'Signing in...';
    return this.currentStep === 'identifier' ? 'Continue' : 'Sign In';
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const loginData: InternalLoginRequest = {
        identifier: this.loginForm.get('identifier')?.value,
        password: this.loginForm.get('password')?.value
      };

      this.authService.login(loginData).subscribe({
        next: (result) => {
          this.isLoading = false;
          if (result.success) {
            // Navigate to dashboard or intended page
            this.router.navigate(['/dashboard']);
          } else {
            this.errorMessage = result.message || 'Login failed';
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.message || 'An error occurred during login';
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      control?.markAsTouched();
    });
  }

  getIdentifierType(): string {
    const identifier = this.loginForm.get('identifier')?.value;
    if (!identifier) return 'username';
    
    const type = this.authService.getIdentifierType(identifier);
    switch (type) {
      case 'phone': return 'Phone Number';
      case 'domain': return 'Domain';
      default: return 'Username';
    }
  }

  getIdentifierPlaceholder(): string {
    const type = this.getIdentifierType();
    switch (type) {
      case 'Phone Number': return 'Enter your phone number';
      case 'Domain': return 'Enter your domain';
      default: return 'Enter your username';
    }
  }

  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.onSubmit();
    }
  }

  onForgotPassword(): void {
    if (this.loginForm.get('identifier')?.value && this.loginForm.get('identifier')?.value.trim()) {
      this.authService.resetPassword(this.loginForm.get('identifier')?.value).subscribe({
        next: (result) => {
          if (result.success) {
            alert('Password reset instructions have been sent to your email.');
          } else {
            this.errorMessage = result.message || 'Password reset failed';
          }
        },
        error: (error) => {
          this.errorMessage = error.message || 'Password reset failed';
        }
      });
    } else {
      alert('Please enter your username or email first.');
    }
  }

  onSignUp(): void {
    this.router.navigate(['/register']);
  }
}