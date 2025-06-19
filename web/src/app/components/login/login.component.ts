import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { InternalLoginRequest } from '../../models/auth.models';
import { HeaderComponent } from '../shared/header/header.component';
import { FooterComponent } from '../shared/footer/footer.component';
import { TestimonialComponent, Testimonial } from '../shared/testimonial/testimonial.component';
import { environment } from '../../../environments/environment';

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
 * @implements OnInit
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    FooterComponent, 
    TestimonialComponent
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  // Form data properties
  /** User identifier input (username, phone, or domain) */
  identifier: string = '';
  
  /** Password input for username/domain authentication */
  password: string = '';
  
  // UI state properties
  /** Loading state for main login button */
  isLoading: boolean = false;
  
  /** Loading state for QR login button */
  isQRLoading: boolean = false;
  
  /** Error message to display to user */
  errorMessage: string = '';
  
  /** Whether to show password field based on identifier type */
  showPasswordField: boolean = false;
  
  /** Detected type of identifier entered by user */
  identifierType: 'username' | 'phone' | 'domain' | '' = '';
  
  /** Current step in the multi-step login flow */
  currentStep: 'identifier' | 'password' = 'identifier';
  
  /** Testimonial data for the login page */
  testimonial: Testimonial = {
    quote: "Onified.ai transformed our workflow completely. The authentication system is seamless and our team loves how easy it is to use.",
    author: "Sarah Johnson",
    title: "CTO"
  };

  /**
   * Constructor - Injects required services
   * @param authService - Service for handling authentication (public for template access)
   * @param router - Angular router for navigation
   */
  constructor(
    public authService: AuthService, // Made public for template access
    private router: Router
  ) {}

  /**
   * Component initialization lifecycle hook
   * Checks if user is already authenticated and redirects if necessary
   */
  ngOnInit(): void {
    // Debug logging for development
    console.log('Login component initialized');
    console.log('Testimonial object:', this.testimonial);
    
    // Redirect to dashboard if user is already authenticated
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  /**
   * Handles changes to the identifier input field
   * Automatically detects identifier type and determines if password is needed
   */
  onIdentifierChange(): void {
    // Clear any existing error messages
    this.errorMessage = '';
    
    if (this.identifier && this.identifier.trim()) {
      // Detect the type of identifier (username, phone, or domain)
      this.identifierType = this.authService.getIdentifierType(this.identifier);
      
      // Show password field for username and domain authentication
      // Phone authentication typically uses OTP instead of password
      this.showPasswordField = this.identifierType === 'username' || this.identifierType === 'domain';
      
      // Validate username format if it's a username
      if (this.identifierType === 'username' && !this.authService.isValidUsername(this.identifier)) {
        this.errorMessage = 'Username must be 3-30 characters and contain only letters, numbers, underscores, and hyphens';
      }
    } else {
      // Reset state when identifier is empty
      this.identifierType = '';
      this.showPasswordField = false;
    }
  }

  /**
   * Handles the continue/login button click
   * Manages the multi-step flow and performs authentication
   */
  onContinue(): void {
    // Validate identifier input
    if (!this.identifier || !this.identifier.trim()) {
      this.errorMessage = 'Please enter your username, phone, or tenant domain';
      return;
    }

    // Validate username format if it's a username
    if (this.identifierType === 'username' && !this.authService.isValidUsername(this.identifier)) {
      this.errorMessage = 'Please enter a valid username';
      return;
    }

    // If we're on the identifier step and need password, move to password step
    if (this.currentStep === 'identifier' && this.showPasswordField) {
      this.currentStep = 'password';
      return;
    }

    // Validate password if required
    if (this.showPasswordField && (!this.password || !this.password.trim())) {
      this.errorMessage = 'Please enter your password';
      return;
    }

    // Start login process
    this.isLoading = true;
    this.errorMessage = '';

    // Prepare login credentials
    const credentials: InternalLoginRequest = {
      identifier: this.identifier.trim(),
      password: this.showPasswordField ? this.password.trim() : undefined
    };

    // Perform authentication
    this.authService.login(credentials).subscribe({
      next: (result) => {
        this.isLoading = false;
        if (result && result.success) {
          // Login successful - redirect to dashboard
          this.router.navigate(['/dashboard']);
        } else {
          // Login failed - show error message
          this.errorMessage = (result && result.message) ? result.message : 'Login failed';
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = (error && error.message) ? error.message : 'An error occurred. Please try again.';
        console.error('Login error:', error);
      }
    });
  }

  /**
   * Handles the back button click to return to identifier step
   * Clears password and error state
   */
  onBackToIdentifier(): void {
    this.currentStep = 'identifier';
    this.password = '';
    this.errorMessage = '';
  }

  /**
   * Handles QR code login button click
   * Initiates QR-based authentication flow
   */
  onQRLogin(): void {
    this.isQRLoading = true;
    this.errorMessage = '';

    this.authService.loginWithQR().subscribe({
      next: (result) => {
        this.isQRLoading = false;
        if (result && result.success) {
          // QR login successful - redirect to dashboard
          this.router.navigate(['/dashboard']);
        } else {
          // QR login failed - show error message
          this.errorMessage = (result && result.message) ? result.message : 'QR login failed';
        }
      },
      error: (error) => {
        this.isQRLoading = false;
        this.errorMessage = (error && error.message) ? error.message : 'QR login failed. Please try again.';
        console.error('QR login error:', error);
      }
    });
  }

  /**
   * Gets appropriate placeholder text for the identifier input
   * Changes based on detected identifier type
   * @returns Placeholder text string
   */
  getIdentifierPlaceholder(): string {
    switch (this.identifierType) {
      case 'username':
        return 'Enter your username';
      case 'phone':
        return 'Enter your phone number';
      case 'domain':
        return 'Enter your tenant domain';
      default:
        return 'Enter username, phone, or tenant domain';
    }
  }

  /**
   * Gets appropriate placeholder text for the password input
   * Changes based on identifier type (domain vs username)
   * @returns Placeholder text string
   */
  getPasswordPlaceholder(): string {
    return this.identifierType === 'domain' ? 'Enter domain password' : 'Enter your password';
  }

  /**
   * Gets appropriate button text based on current state
   * Changes based on loading state and current step
   * @returns Button text string
   */
  getButtonText(): string {
    if (this.isLoading) {
      return 'Signing in...';
    }
    
    if (this.currentStep === 'identifier') {
      return this.showPasswordField ? 'Continue' : 'Sign In';
    }
    
    return 'Sign In';
  }

  /**
   * Gets appropriate label text for the identifier input
   * Changes based on current step and identifier type
   * @returns Label text string
   */
  getIdentifierLabel(): string {
    if (this.currentStep === 'password') {
      return `Signing in as: ${this.identifier}`;
    }
    
    switch (this.identifierType) {
      case 'username':
        return 'Username';
      case 'phone':
        return 'Phone Number';
      case 'domain':
        return 'Tenant Domain';
      default:
        return 'Username, Phone, or Domain';
    }
  }

  /**
   * Checks if the current identifier is valid
   * Used for form validation and button state
   * @returns boolean indicating if identifier is valid
   */
  isIdentifierValid(): boolean {
    if (!this.identifier || !this.identifier.trim()) {
      return false;
    }
    
    if (this.identifierType === 'username') {
      return this.authService.isValidUsername(this.identifier);
    }
    
    return true; // Phone and domain validation can be added here if needed
  }

  /**
   * Handles keyboard events for form submission
   * Allows users to submit form by pressing Enter
   * @param event - Keyboard event
   */
  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.onContinue();
    }
  }

  /**
   * Handles forgot password link click
   * Navigates to password reset flow
   */
  onForgotPassword(): void {
    // You can implement password reset navigation here
    // For now, we'll just show an alert
    if (this.identifier && this.identifier.trim()) {
      this.authService.resetPassword(this.identifier).subscribe({
        next: (result) => {
          if (result.success) {
            alert('Password reset instructions have been sent to your registered email.');
          } else {
            alert(result.message || 'Password reset failed. Please try again.');
          }
        },
        error: (error) => {
          alert('Password reset failed. Please try again.');
          console.error('Password reset error:', error);
        }
      });
    } else {
      alert('Please enter your username first, then click "Forgot Password".');
    }
  }

  /**
   * Handles sign up link click
   * Navigates to registration page
   */
  onSignUp(): void {
    // Navigate to registration page
    this.router.navigate(['/register']);
  }
}