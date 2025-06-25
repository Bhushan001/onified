import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PasswordPolicy {
  minLength: number;
  maxLength: number;
  requireUppercase: boolean;
  requireLowercase: boolean;
  requireNumbers: boolean;
  requireSpecialCharacters: boolean;
  preventCommonPasswords: boolean;
  preventUsernameInPassword: boolean;
  preventSequentialCharacters: boolean;
  preventRepeatedCharacters: boolean;
  passwordHistoryCount: number;
  passwordExpiryDays: number;
}

export interface PasswordValidationResult {
  isValid: boolean;
  errors: string[];
  warnings: string[];
  strength: 'weak' | 'medium' | 'strong' | 'very-strong';
  score: number; // 0-100
}

@Injectable({
  providedIn: 'root'
})
export class PasswordPolicyService {
  private policySubject = new BehaviorSubject<PasswordPolicy | null>(null);
  public policy$ = this.policySubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadPasswordPolicy();
  }

  /**
   * Load password policy from backend
   */
  loadPasswordPolicy(): Observable<PasswordPolicy> {
    const url = `${environment.apiUrl}/platform-management/password-policies`;
    return this.http.get<PasswordPolicy>(url);
  }

  /**
   * Get current password policy
   */
  getPasswordPolicy(): PasswordPolicy | null {
    return this.policySubject.value;
  }

  /**
   * Validate password against current policy
   */
  validatePassword(password: string, username?: string): PasswordValidationResult {
    const policy = this.getPasswordPolicy();
    if (!policy) {
      return {
        isValid: false,
        errors: ['Password policy not loaded'],
        warnings: [],
        strength: 'weak',
        score: 0
      };
    }

    const errors: string[] = [];
    const warnings: string[] = [];
    let score = 0;

    // Length validation
    if (password.length < policy.minLength) {
      errors.push(`Password must be at least ${policy.minLength} characters long`);
    } else {
      score += 20;
    }

    if (password.length > policy.maxLength) {
      errors.push(`Password must not exceed ${policy.maxLength} characters`);
    }

    // Character requirements
    if (policy.requireUppercase && !/[A-Z]/.test(password)) {
      errors.push('Password must contain at least one uppercase letter');
    } else if (policy.requireUppercase) {
      score += 15;
    }

    if (policy.requireLowercase && !/[a-z]/.test(password)) {
      errors.push('Password must contain at least one lowercase letter');
    } else if (policy.requireLowercase) {
      score += 15;
    }

    if (policy.requireNumbers && !/\d/.test(password)) {
      errors.push('Password must contain at least one number');
    } else if (policy.requireNumbers) {
      score += 15;
    }

    if (policy.requireSpecialCharacters && !/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
      errors.push('Password must contain at least one special character');
    } else if (policy.requireSpecialCharacters) {
      score += 20;
    }

    // Additional security checks
    if (policy.preventUsernameInPassword && username && password.toLowerCase().includes(username.toLowerCase())) {
      errors.push('Password cannot contain your username');
    }

    if (policy.preventSequentialCharacters && this.hasSequentialChars(password)) {
      warnings.push('Avoid sequential characters (e.g., 123, abc)');
      score -= 10;
    }

    if (policy.preventRepeatedCharacters && this.hasRepeatedChars(password)) {
      warnings.push('Avoid repeated characters (e.g., aaa, 111)');
      score -= 10;
    }

    // Common password check
    if (policy.preventCommonPasswords && this.isCommonPassword(password)) {
      errors.push('Password is too common, please choose a stronger password');
    }

    // Calculate strength based on score
    let strength: 'weak' | 'medium' | 'strong' | 'very-strong' = 'weak';
    if (score >= 80) strength = 'very-strong';
    else if (score >= 60) strength = 'strong';
    else if (score >= 40) strength = 'medium';
    else strength = 'weak';

    return {
      isValid: errors.length === 0,
      errors,
      warnings,
      strength,
      score: Math.max(0, Math.min(100, score))
    };
  }

  /**
   * Check if password contains sequential characters
   */
  private hasSequentialChars(password: string): boolean {
    const sequences = ['123', '234', '345', '456', '789', 'abc', 'bcd', 'cde', 'def', 'efg', 'fgh', 'ghi', 'hij', 'ijk', 'jkl', 'klm', 'lmn', 'mno', 'nop', 'opq', 'pqr', 'qrs', 'rst', 'stu', 'tuv', 'uvw', 'vwx', 'wxy', 'xyz'];
    const lowerPassword = password.toLowerCase();
    return sequences.some(seq => lowerPassword.includes(seq));
  }

  /**
   * Check if password contains repeated characters
   */
  private hasRepeatedChars(password: string): boolean {
    return /(.)\1{2,}/.test(password);
  }

  /**
   * Check if password is in common passwords list
   */
  private isCommonPassword(password: string): boolean {
    const commonPasswords = [
      'password', '123456', '123456789', 'qwerty', 'abc123', 'password123',
      'admin', 'letmein', 'welcome', 'monkey', 'dragon', 'master', 'sunshine',
      'princess', 'qwerty123', 'admin123', 'password1', '12345678', 'baseball',
      'football', 'superman', 'trustno1', 'butterfly', 'dragon123', 'master123'
    ];
    return commonPasswords.includes(password.toLowerCase());
  }

  /**
   * Get password strength color
   */
  getStrengthColor(strength: string): string {
    switch (strength) {
      case 'very-strong': return '#10b981'; // green
      case 'strong': return '#059669'; // green-600
      case 'medium': return '#f59e0b'; // amber
      case 'weak': return '#ef4444'; // red
      default: return '#6b7280'; // gray
    }
  }

  /**
   * Get password strength text
   */
  getStrengthText(strength: string): string {
    switch (strength) {
      case 'very-strong': return 'Very Strong';
      case 'strong': return 'Strong';
      case 'medium': return 'Medium';
      case 'weak': return 'Weak';
      default: return 'Unknown';
    }
  }
} 