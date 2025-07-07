import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PasswordPolicyService, PasswordValidationResult, PasswordPolicy } from '../../services/password-policy.service';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/auth.models';

@Component({
  selector: 'app-signup-tenant-admin',
  templateUrl: './signup-tenant-admin.component.html',
  styleUrls: ['./signup-tenant-admin.component.scss'],
  standalone: false
})
export class SignupTenantAdminComponent implements OnInit, OnDestroy {
  signupForm: FormGroup;
  submitted = false;
  error: string | null = null;
  showTermsModal = false;
  termsModalType: 'terms' | 'privacy' = 'terms';
  modalScrolledToEnd = false;
  showPassword = false;
  showConfirmPassword = false;
  passwordValidation: PasswordValidationResult | null = null;
  isPasswordPolicyLoaded = false;
  private policySubscription: Subscription | null = null;
  public passwordPolicy: PasswordPolicy | null = null;

  testimonial = {
    quote: "Onified has transformed how we manage our enterprise applications. The seamless integration and powerful features have made our workflow incredibly efficient.",
    author: "Sarah Johnson",
    title: "CTO, TechCorp",
    avatar: "assets/images/testimonials/sarah.jpg"
  };

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private passwordPolicyService: PasswordPolicyService,
    private authService: AuthService
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
    this.policySubscription = this.passwordPolicyService.policy$.subscribe(policy => {
      this.isPasswordPolicyLoaded = !!policy;
      this.passwordPolicy = policy;
      if (policy) {
        this.validatePassword();
      }
    });
    this.passwordPolicyService.loadPasswordPolicy().subscribe({
      next: (policy) => {
        // Password policy loaded
      },
      error: (error) => {
        this.error = 'Failed to load password requirements. Please try again.';
      }
    });
    this.signupForm.get('password')?.valueChanges.subscribe(() => {
      this.validatePassword();
      this.validatePasswordMatch();
    });
    this.signupForm.get('username')?.valueChanges.subscribe(() => {
      this.validatePassword();
    });
    this.signupForm.get('confirmPassword')?.valueChanges.subscribe(() => {
      this.validatePasswordMatch();
    });
    const saved = localStorage.getItem('signupForm');
    if (saved) {
      try {
        this.signupForm.patchValue(JSON.parse(saved));
      } catch (e) {}
    }
    this.signupForm.valueChanges.subscribe(val => {
      localStorage.setItem('signupForm', JSON.stringify(val));
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

  validatePasswordMatch(): void {
    const password = this.signupForm.get('password')?.value;
    const confirmPassword = this.signupForm.get('confirmPassword')?.value;
    if (confirmPassword && password !== confirmPassword) {
      this.signupForm.get('confirmPassword')?.setErrors({ mismatch: true });
    } else if (confirmPassword && password === confirmPassword) {
      const currentErrors = this.signupForm.get('confirmPassword')?.errors;
      if (currentErrors) {
        delete currentErrors['mismatch'];
        if (Object.keys(currentErrors).length === 0) {
          this.signupForm.get('confirmPassword')?.setErrors(null);
        } else {
          this.signupForm.get('confirmPassword')?.setErrors(currentErrors);
        }
      }
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    if (!password || !confirmPassword) {
      return null;
    }
    return password === confirmPassword ? null : { mismatch: true };
  }

  onSubmit() {
    this.submitted = true;
    this.error = null;
    if (this.passwordValidation && !this.passwordValidation.isValid) {
      this.error = 'Please fix password requirements before submitting.';
      return;
    }
    if (this.signupForm.invalid) {
      return;
    }
    const formValue = this.signupForm.value;
    const registerData: RegisterRequest = {
      username: formValue.username,
      email: formValue.email,
      password: formValue.password,
      confirmPassword: formValue.confirmPassword,
      name: `${formValue.firstName} ${formValue.lastName}`,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
    };
    this.authService.registerTenantAdmin(registerData).subscribe({
      next: (result) => {
        if (result.success) {
          this.router.navigate(['/login']);
          localStorage.removeItem('signupForm');
        } else {
          this.error = result.message || 'Signup failed. Please try again.';
        }
      },
      error: (err) => {
        this.error = err.message || 'Signup failed. Please try again.';
      }
    });
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
    return this.passwordValidation && typeof this.passwordValidation.strength === 'number'
      ? `${this.passwordValidation.strength * 20}%`
      : '0%';
  }

  debugPasswordValidation(): void {
    // For debugging password validation in the UI
    console.log(this.passwordValidation);
  }
} 