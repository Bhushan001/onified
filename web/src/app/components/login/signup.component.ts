import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FooterComponent } from '../shared/footer/footer.component';
import { TestimonialComponent } from '../shared/testimonial/testimonial.component';
import { PasswordPolicyService, PasswordValidationResult } from '../../services/password-policy.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, FooterComponent, TestimonialComponent],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit, OnDestroy {
  signupForm: FormGroup;
  submitted = false;
  error: string | null = null;
  showTermsModal = false;
  termsModalType: 'terms' | 'privacy' = 'terms';
  modalScrolledToEnd = false;
  showPassword = false;
  showConfirmPassword = false;
  
  // Password validation properties
  passwordValidation: PasswordValidationResult | null = null;
  isPasswordPolicyLoaded = false;
  private policySubscription: Subscription | null = null;

  testimonial = {
    quote: "Onified has transformed how we manage our enterprise applications. The seamless integration and powerful features have made our workflow incredibly efficient.",
    author: "Sarah Johnson",
    title: "CTO, TechCorp",
    avatar: "assets/images/testimonials/sarah.jpg"
  };

  constructor(
    private fb: FormBuilder, 
    private router: Router,
    private passwordPolicyService: PasswordPolicyService
  ) {
    this.signupForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      dateOfBirth: ['', [Validators.required]],
      country: ['', [Validators.required]],
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]],
      consent: [false, [Validators.requiredTrue]]
    }, { validator: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    // Subscribe to password policy changes
    this.policySubscription = this.passwordPolicyService.policy$.subscribe(policy => {
      this.isPasswordPolicyLoaded = !!policy;
      if (policy) {
        // Update password validation when policy is loaded
        this.validatePassword();
      }
    });

    // Load password policy from backend
    this.passwordPolicyService.loadPasswordPolicy().subscribe({
      next: (policy) => {
        console.log('Password policy loaded:', policy);
      },
      error: (error) => {
        console.error('Failed to load password policy:', error);
        this.error = 'Failed to load password requirements. Please try again.';
      }
    });

    // Add password change listener
    this.signupForm.get('password')?.valueChanges.subscribe(() => {
      this.validatePassword();
    });

    this.signupForm.get('username')?.valueChanges.subscribe(() => {
      this.validatePassword();
    });
  }

  ngOnDestroy(): void {
    if (this.policySubscription) {
      this.policySubscription.unsubscribe();
    }
  }

  get f() { return this.signupForm.controls; }

  validatePassword(): void {
    const password = this.signupForm.get('password')?.value;
    const username = this.signupForm.get('username')?.value;
    
    if (password) {
      this.passwordValidation = this.passwordPolicyService.validatePassword(password, username);
    } else {
      this.passwordValidation = null;
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')!.value;
    const confirmPassword = form.get('confirmPassword');
    if (!confirmPassword) return null;
    if (password !== confirmPassword.value) {
      confirmPassword.setErrors({ ...(confirmPassword.errors || {}), mismatch: true });
      return { mismatch: true };
    } else {
      if (confirmPassword.hasError('mismatch')) {
        const errors = { ...(confirmPassword.errors || {}) };
        delete errors['mismatch'];
        if (Object.keys(errors).length === 0) {
          confirmPassword.setErrors(null);
        } else {
          confirmPassword.setErrors(errors);
        }
      }
      return null;
    }
  }

  onSubmit() {
    this.submitted = true;
    this.error = null;
    
    // Validate password against policy
    if (this.passwordValidation && !this.passwordValidation.isValid) {
      this.error = 'Please fix password requirements before submitting.';
      return;
    }
    
    if (this.signupForm.invalid) {
      return;
    }
    
    // TODO: Call backend API to register platform_admin
    // On success:
    this.router.navigate(['/login']);
    // On error:
    // this.error = 'Signup failed. Please try again.';
  }

  openTerms(event: Event, type: 'terms' | 'privacy') {
    event.preventDefault();
    this.termsModalType = type;
    this.showTermsModal = true;
    this.modalScrolledToEnd = false;
    setTimeout(() => {
      const modalContent = document.querySelector('.terms-modal-content') as HTMLElement;
      if (modalContent) {
        modalContent.scrollTop = 0;
        if (modalContent.scrollHeight <= modalContent.clientHeight + 2) {
          this.modalScrolledToEnd = true;
        }
      }
    }, 0);
  }

  closeTermsModal() {
    this.showTermsModal = false;
  }

  onModalScroll(event: Event) {
    const target = event.target as HTMLElement;
    this.modalScrolledToEnd = target.scrollTop + target.clientHeight >= target.scrollHeight - 10;
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  getPasswordStrengthColor(): string {
    return this.passwordValidation 
      ? this.passwordPolicyService.getStrengthColor(this.passwordValidation.strength)
      : '#6b7280';
  }

  getPasswordStrengthText(): string {
    return this.passwordValidation 
      ? this.passwordPolicyService.getStrengthText(this.passwordValidation.strength)
      : 'Unknown';
  }

  getPasswordStrengthWidth(): string {
    return this.passwordValidation 
      ? `${this.passwordValidation.score}%`
      : '0%';
  }
} 