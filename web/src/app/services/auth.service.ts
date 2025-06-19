import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { 
  LoginRequest,
  InternalLoginRequest,
  UsernameLoginRequest,
  PhoneLoginRequest,
  DomainLoginRequest,
  LoginResponse, 
  User, 
  QRLoginRequest, 
  QRLoginResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  LogoutResponse,
  RegisterRequest,
  RegisterResponse,
  PasswordResetRequest,
  PasswordResetResponse,
  UpdateProfileRequest,
  UpdateProfileResponse
} from '../models/auth.models';

/**
 * Authentication Service for Onified.ai Application
 * 
 * This service handles all authentication-related operations including:
 * - User login with username/phone/domain using appropriate field names
 * - QR code authentication
 * - User registration
 * - Token management and refresh
 * - User session management
 * - Profile management
 * - Automatic token expiration handling
 * 
 * Updated to handle your backend's specific response format with statusCode, status, and body structure.
 * 
 * Features:
 * - Reactive state management with BehaviorSubjects
 * - Automatic token refresh before expiration
 * - Multiple authentication methods (password, QR)
 * - Persistent authentication state
 * - Comprehensive error handling
 * - Smart request formatting based on identifier type
 * - JWT token extraction from roles for user creation
 * 
 * @service AuthService
 * @injectable root
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // API and storage configuration from environment
  private readonly API_URL = environment.apiUrl;
  private readonly TOKEN_KEY = environment.auth.tokenKey;
  private readonly USER_KEY = environment.auth.userKey;
  private readonly REFRESH_TOKEN_KEY = environment.auth.refreshTokenKey;

  // Reactive state management for current user
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  // Reactive state management for authentication status
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  // Timer for automatic token refresh
  private tokenExpirationTimer: any;

  /**
   * Constructor - Initializes the service and checks existing authentication state
   * @param http - Angular HTTP client for API requests
   */
  constructor(private http: HttpClient) {
    this.checkAuthStatus();
  }

  /**
   * Checks if user is already authenticated on service initialization
   * Validates stored tokens and refreshes if necessary
   * @private
   */
  private checkAuthStatus(): void {
    const token = localStorage.getItem(this.TOKEN_KEY);
    const user = localStorage.getItem(this.USER_KEY);
    
    if (token && user) {
      try {
        const parsedUser = JSON.parse(user);
        
        // Check if token is expired and attempt refresh
        if (this.isTokenExpired(token)) {
          this.refreshToken().subscribe({
            next: () => {
              this.currentUserSubject.next(parsedUser);
              this.isAuthenticatedSubject.next(true);
            },
            error: () => {
              // If refresh fails, clear authentication
              this.logout();
            }
          });
        } else {
          // Token is valid, restore authentication state
          this.currentUserSubject.next(parsedUser);
          this.isAuthenticatedSubject.next(true);
          this.setTokenExpirationTimer(token);
        }
      } catch (error) {
        // If user data is corrupted, clear authentication
        this.logout();
      }
    }
  }

  /**
   * Authenticates user with username/phone/domain and password
   * Automatically formats the request with appropriate field names
   * 
   * @param credentials - Internal login credentials containing identifier and optional password
   * @returns Observable with success status and optional error message
   */
  public login(credentials: InternalLoginRequest): Observable<{ success: boolean; message?: string }> {
    // Determine identifier type if not provided
    const identifierType = credentials.identifierType || this.getIdentifierType(credentials.identifier);
    
    // Create the appropriate request payload based on identifier type
    const loginPayload = this.createLoginPayload(credentials.identifier, credentials.password, identifierType);

    return this.http.post<LoginResponse>(`${this.API_URL}/auth/login`, loginPayload)
      .pipe(
        map(response => {
          // Check if response is successful based on your backend format
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            // Extract user data from JWT token and response
            const user = this.createUserFromResponse(response.body);
            
            // Handle successful authentication
            this.handleAuthSuccess(response.body.jwtToken, user, response.body.refreshToken);
            
            return { success: true };
          } else {
            return { 
              success: false, 
              message: response.message || response.error || 'Login failed' 
            };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Creates a User object from the login response
   * Extracts user information from JWT token and response data
   * 
   * @param responseBody - The response body from login
   * @returns User object
   * @private
   */
  private createUserFromResponse(responseBody: any): User {
    // Extract user data from JWT token
    const tokenData = this.extractDataFromToken(responseBody.jwtToken);
    
    // Create user object combining response data and token data
    const user: User = {
      id: tokenData.sub || '', // Subject from JWT
      username: responseBody.username || tokenData.username || '',
      name: responseBody.username || tokenData.username || '', // Use username as display name for now
      roles: tokenData.roles || [],
      lastLogin: new Date().toISOString(),
      // Add any additional fields from response if available
      email: responseBody.email,
      firstName: responseBody.firstName,
      lastName: responseBody.lastName,
      phone: responseBody.phone,
      tenant: responseBody.tenant,
      avatar: responseBody.avatar,
      department: responseBody.department,
      status: responseBody.status || 'active'
    };

    return user;
  }

  /**
   * Extracts data from JWT token payload
   * 
   * @param token - JWT token
   * @returns Decoded token payload
   * @private
   */
  private extractDataFromToken(token: string): any {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload;
    } catch (error) {
      console.warn('Could not decode JWT token:', error);
      return {};
    }
  }

  /**
   * Creates the appropriate login payload based on identifier type
   * 
   * @param identifier - The user identifier
   * @param password - The password (optional for phone)
   * @param identifierType - The type of identifier
   * @returns Formatted login request payload
   * @private
   */
  private createLoginPayload(identifier: string, password: string | undefined, identifierType: 'username' | 'phone' | 'domain'): LoginRequest {
    switch (identifierType) {
      case 'username':
        const usernamePayload: UsernameLoginRequest = {
          username: identifier,
          password: password || ''
        };
        return usernamePayload;
        
      case 'phone':
        const phonePayload: PhoneLoginRequest = {
          phone: identifier,
          password: password // Phone might use OTP instead
        };
        return phonePayload;
        
      case 'domain':
        const domainPayload: DomainLoginRequest = {
          domain: identifier,
          password: password || ''
        };
        return domainPayload;
        
      default:
        // Fallback to username format
        const fallbackPayload: UsernameLoginRequest = {
          username: identifier,
          password: password || ''
        };
        return fallbackPayload;
    }
  }

  /**
   * Registers a new user account
   * 
   * @param registrationData - User registration information
   * @returns Observable with registration result
   */
  public register(registrationData: RegisterRequest): Observable<{ success: boolean; message?: string }> {
    return this.http.post<RegisterResponse>(`${this.API_URL}/auth/register`, registrationData)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            // If registration includes immediate login token, handle authentication
            if (response.body.jwtToken) {
              const user = this.createUserFromResponse(response.body);
              this.handleAuthSuccess(response.body.jwtToken, user);
            }
            return { success: true, message: response.message || 'Registration successful' };
          } else {
            return { success: false, message: response.message || response.error || 'Registration failed' };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Authenticates user using QR code
   * 
   * @param qrData - Optional QR login data, generates device ID if not provided
   * @returns Observable with success status and optional error message
   */
  public loginWithQR(qrData?: QRLoginRequest): Observable<{ success: boolean; message?: string }> {
    const qrPayload = {
      qrCode: qrData?.qrCode || '',
      deviceId: qrData?.deviceId || this.generateDeviceId()
    };

    return this.http.post<QRLoginResponse>(`${this.API_URL}/auth/qr-login`, qrPayload)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            const user = this.createUserFromResponse(response.body);
            this.handleAuthSuccess(response.body.jwtToken, user, response.body.refreshToken);
            return { success: true };
          } else {
            return { success: false, message: response.message || response.error || 'QR login failed' };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Initiates password reset for a user
   * 
   * @param identifier - Username or email for password reset
   * @returns Observable with password reset result
   */
  public resetPassword(identifier: string): Observable<{ success: boolean; message?: string }> {
    const resetPayload: PasswordResetRequest = { identifier };

    return this.http.post<PasswordResetResponse>(`${this.API_URL}/auth/reset-password`, resetPayload)
      .pipe(
        map(response => {
          return { 
            success: response.statusCode === 200 && response.status === 'SUCCESS', 
            message: response.message || (response.status === 'SUCCESS' ? 'Password reset email sent' : 'Password reset failed')
          };
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Updates user profile information
   * 
   * @param profileData - Updated profile information
   * @returns Observable with update result
   */
  public updateProfile(profileData: UpdateProfileRequest): Observable<{ success: boolean; message?: string; user?: User }> {
    return this.http.put<UpdateProfileResponse>(`${this.API_URL}/auth/profile`, profileData)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            // Update local user data
            this.currentUserSubject.next(response.body.user);
            localStorage.setItem(this.USER_KEY, JSON.stringify(response.body.user));
            
            return { 
              success: true, 
              message: response.message || 'Profile updated successfully',
              user: response.body.user
            };
          } else {
            return { success: false, message: response.message || response.error || 'Profile update failed' };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Refreshes the authentication token using the stored refresh token
   * 
   * @returns Observable<boolean> - true if refresh successful, error otherwise
   */
  public refreshToken(): Observable<boolean> {
    const refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);
    
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    const refreshPayload: RefreshTokenRequest = { refreshToken };

    return this.http.post<RefreshTokenResponse>(`${this.API_URL}/auth/refresh`, refreshPayload)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            // Update stored tokens
            localStorage.setItem(this.TOKEN_KEY, response.body.jwtToken);
            
            if (response.body.refreshToken) {
              localStorage.setItem(this.REFRESH_TOKEN_KEY, response.body.refreshToken);
            }
            
            // Set up new expiration timer
            this.setTokenExpirationTimer(response.body.jwtToken);
            return true;
          } else {
            throw new Error(response.error || 'Token refresh failed');
          }
        }),
        catchError((error) => {
          return throwError(() => ({ success: false, message: error.message || 'Token refresh failed' }));
        })
      );
  }

  /**
   * Logs out the current user
   * Clears local storage and notifies the backend
   * 
   * @returns Observable<void> - Completes when logout is finished
   */
  public logout(): Observable<void> {
    const token = localStorage.getItem(this.TOKEN_KEY);
    
    // Clear local storage and state immediately for better UX
    this.clearAuthData();
    
    // Notify backend (optional, don't wait for response)
    if (token) {
      this.http.post<LogoutResponse>(`${this.API_URL}/auth/logout`, {})
        .subscribe({
          next: () => console.log('Logout successful'),
          error: (error) => console.warn('Logout request failed:', error)
        });
    }

    return new Observable(observer => {
      observer.next();
      observer.complete();
    });
  }

  /**
   * Gets the current authenticated user
   * @returns User object or null if not authenticated
   */
  public getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Gets the current user's username
   * @returns Username string or null if not authenticated
   */
  public getCurrentUsername(): string | null {
    const user = this.getCurrentUser();
    return user ? user.username : null;
  }

  /**
   * Checks if user is currently authenticated
   * @returns boolean indicating authentication status
   */
  public isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  /**
   * Gets the current authentication token
   * @returns JWT token string or null if not available
   */
  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Determines the type of identifier (username, phone, or domain)
   * 
   * @param identifier - The user input identifier
   * @returns 'username' | 'phone' | 'domain' based on the format
   */
  public getIdentifierType(identifier: string): 'username' | 'phone' | 'domain' {
    // Check if it's a phone number (contains only digits, spaces, hyphens, parentheses, plus)
    if (/^\+?[\d\s\-\(\)]+$/.test(identifier)) {
      return 'phone';
    }
    
    // Check if it's a domain (contains dots but no @ symbol)
    if (identifier.includes('.') && !identifier.includes('@') && !identifier.includes(' ')) {
      return 'domain';
    }
    
    // Default to username for everything else
    return 'username';
  }

  /**
   * Validates username format
   * @param username - Username to validate
   * @returns boolean indicating if username is valid
   */
  public isValidUsername(username: string): boolean {
    // Username should be 3-30 characters, alphanumeric with underscores and hyphens
    const usernameRegex = /^[a-zA-Z0-9_-]{3,30}$/;
    return usernameRegex.test(username);
  }

  /**
   * Checks if a username is available
   * @param username - Username to check
   * @returns Observable<boolean> - true if available, false if taken
   */
  public checkUsernameAvailability(username: string): Observable<boolean> {
    return this.http.get<{ available: boolean }>(`${this.API_URL}/auth/check-username/${username}`)
      .pipe(
        map(response => response.available),
        catchError(() => {
          // If check fails, assume username might be taken
          return new Observable<boolean>(observer => {
            observer.next(false);
            observer.complete();
          });
        })
      );
  }

  /**
   * Handles successful authentication by storing tokens and user data
   * 
   * @param token - JWT access token
   * @param user - User information
   * @param refreshToken - Optional refresh token
   * @private
   */
  private handleAuthSuccess(token: string, user: User, refreshToken?: string): void {
    // Store authentication data
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    
    if (refreshToken) {
      localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
    }
    
    // Update reactive state
    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
    
    // Set up automatic token refresh
    this.setTokenExpirationTimer(token);

    // Log successful authentication in development
    if (environment.enableLogging) {
      console.log('Authentication successful for user:', user.username);
      console.log('User roles:', user.roles);
    }
  }

  /**
   * Clears all authentication data from storage and state
   * @private
   */
  private clearAuthData(): void {
    // Clear local storage
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    
    // Reset reactive state
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    
    // Clear expiration timer
    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
      this.tokenExpirationTimer = null;
    }
  }

  /**
   * Checks if a JWT token is expired
   * 
   * @param token - JWT token to check
   * @returns true if token is expired, false otherwise
   * @private
   */
  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp < currentTime;
    } catch (error) {
      // If we can't parse the token, consider it expired
      return true;
    }
  }

  /**
   * Sets up automatic token refresh timer
   * Refreshes token 5 minutes before expiration
   * 
   * @param token - JWT token to set timer for
   * @private
   */
  private setTokenExpirationTimer(token: string): void {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirationTime = payload.exp * 1000;
      const currentTime = Date.now();
      const timeUntilExpiration = expirationTime - currentTime;
      
      // Refresh token 5 minutes before expiration
      const refreshTime = Math.max(timeUntilExpiration - (5 * 60 * 1000), 0);
      
      // Clear existing timer
      if (this.tokenExpirationTimer) {
        clearTimeout(this.tokenExpirationTimer);
      }
      
      // Set new timer
      this.tokenExpirationTimer = setTimeout(() => {
        this.refreshToken().subscribe({
          error: () => this.logout() // Logout if refresh fails
        });
      }, refreshTime);
    } catch (error) {
      console.warn('Could not parse token for expiration timer:', error);
    }
  }

  /**
   * Generates a unique device ID for QR login
   * @returns Unique device identifier string
   * @private
   */
  private generateDeviceId(): string {
    return 'device-' + Math.random().toString(36).substr(2, 9) + '-' + Date.now();
  }

  /**
   * Handles HTTP errors and converts them to user-friendly messages
   * 
   * @param error - HTTP error response
   * @returns Observable with formatted error message
   * @private
   */
  private handleError(error: HttpErrorResponse): Observable<{ success: boolean; message: string }> {
    let errorMessage = 'An unexpected error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error (network, etc.)
      errorMessage = error.error.message;
    } else {
      // Server-side error - check your backend's error format
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.error?.error) {
        errorMessage = error.error.error;
      } else if (error.status === 401) {
        errorMessage = 'Invalid credentials';
        this.logout(); // Clear invalid session
      } else if (error.status === 403) {
        errorMessage = 'Access denied';
      } else if (error.status === 404) {
        errorMessage = 'Service not found';
      } else if (error.status === 409) {
        errorMessage = 'Username already exists';
      } else if (error.status === 422) {
        errorMessage = 'Invalid input data';
      } else if (error.status === 500) {
        errorMessage = 'Server error. Please try again later.';
      }
    }

    // Log error in development mode
    if (environment.enableLogging) {
      console.error('Auth Service Error:', error);
    }

    return new Observable(observer => {
      observer.next({ success: false, message: errorMessage });
      observer.complete();
    });
  }
}