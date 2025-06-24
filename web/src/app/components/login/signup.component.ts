import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FooterComponent } from '../shared/footer/footer.component';
import { TestimonialComponent } from '../shared/testimonial/testimonial.component';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, FooterComponent, TestimonialComponent],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  signupForm: FormGroup;
  submitted = false;
  error: string | null = null;
  showTermsModal = false;
  termsModalType: 'terms' | 'privacy' = 'terms';
  modalScrolledToEnd = false;
  showPassword = false;
  showConfirmPassword = false;

  testimonial = {
    quote: "Onified has transformed how we manage our enterprise applications. The seamless integration and powerful features have made our workflow incredibly efficient.",
    author: "Sarah Johnson",
    title: "CTO, TechCorp",
    avatar: "assets/images/testimonials/sarah.jpg"
  };

  constructor(private fb: FormBuilder, private router: Router) {
    this.signupForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      dateOfBirth: ['', [Validators.required]],
      country: ['', [Validators.required]],
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(10)]],
      confirmPassword: ['', [Validators.required]],
      consent: [false, [Validators.requiredTrue]]
    }, { validator: this.passwordMatchValidator });
  }

  get f() { return this.signupForm.controls; }

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
} 