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
  UpdateProfileResponse,
  SocialLoginRequest,
  SocialSignupRequest,
  SocialLoginResponse,
  SocialProvider
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
 * Updated to work with Keycloak OAuth2/OIDC authentication system.
 * 
 * Features:
 * - Reactive state management with BehaviorSubjects
 * - Automatic token refresh before expiration
 * - Multiple authentication methods (password, QR)
 * - Persistent authentication state
 * - Comprehensive error handling
 * - Smart request formatting based on identifier type
 * - OAuth2 token extraction for user creation
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

    return this.http.post<LoginResponse>(`${this.API_URL}/auth/auth/login`, loginPayload)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            // Save userProfile in localStorage if present
            if (response.body.userProfile) {
              localStorage.setItem('userProfile', JSON.stringify(response.body.userProfile));
            }
            // Use accessToken if present, else fallback to jwtToken
            const token = response.body.accessToken || response.body.jwtToken;
            if (!token) {
              throw new Error('No access token found in login response');
            }
            const user = this.createUserFromResponse({ ...response.body, jwtToken: token });
            this.handleAuthSuccess(token, user, response.body.refreshToken);
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
   * Extracts user information from OAuth2 token and response data
   * 
   * @param responseBody - The response body from login
   * @returns User object
   * @private
   */
  private createUserFromResponse(responseBody: any): User {
    // Use accessToken if present, else fallback to jwtToken
    const token = responseBody.accessToken || responseBody.jwtToken;
    const tokenData = this.extractDataFromToken(token);
    
    // Create user object combining response data and token data
    const user: User = {
      id: tokenData.sub || '', // Subject from JWT
      username: responseBody.username || tokenData.preferred_username || '',
      name: responseBody.username || tokenData.preferred_username || '', // Use username as display name for now
      roles: tokenData.realm_access?.roles || [],
      lastLogin: new Date().toISOString(),
      // Add any additional fields from response if available
      email: responseBody.email || tokenData.email,
      firstName: responseBody.firstName || tokenData.given_name,
      lastName: responseBody.lastName || tokenData.family_name,
      phone: responseBody.phone,
      tenant: responseBody.tenant,
      avatar: responseBody.avatar,
      department: responseBody.department,
      status: responseBody.status || 'active'
    };

    return user;
  }

  /**
   * Extracts data from OAuth2 token payload
   * 
   * @param token - OAuth2 token
   * @returns Decoded token payload
   * @private
   */
  private extractDataFromToken(token: string): any {
    try {
      if (!token || typeof token !== 'string' || token.split('.').length < 2) {
        throw new Error('Invalid or missing JWT token');
      }
      const payload = token.split('.')[1];
      // Add padding if needed for base64 decoding
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
      return JSON.parse(atob(padded));
    } catch (error) {
      console.error('Error extracting data from token:', error);
      return {};
    }
  }

  /**
   * Creates the appropriate login payload based on identifier type
   * 
   * @param identifier - User identifier (username, phone, or domain)
   * @param password - User password
   * @param identifierType - Type of identifier
   * @returns LoginRequest object
   * @private
   */
  private createLoginPayload(identifier: string, password: string | undefined, identifierType: 'username' | 'phone' | 'domain'): LoginRequest {
    switch (identifierType) {
      case 'phone':
        return { phone: identifier, password };
      case 'domain':
        return { domain: identifier, password };
      case 'username':
      default:
        return { username: identifier, password };
    }
  }

  /**
   * Authenticates user using QR code
   * 
   * @param qrData - QR code authentication data
   * @returns Observable with success status and optional error message
   */
  public loginWithQR(qrData?: QRLoginRequest): Observable<{ success: boolean; message?: string }> {
    const payload = qrData || { deviceId: this.generateDeviceId() };

    return this.http.post<QRLoginResponse>(`${this.API_URL}/auth/auth/qr-login`, payload)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            const user = this.createUserFromResponse(response.body);
            const token = response.body.accessToken || response.body.jwtToken;
            this.handleAuthSuccess(token, user, response.body.refreshToken);
            return { success: true };
          } else {
            return { 
              success: false, 
              message: response.message || response.error || 'QR login failed' 
            };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Initiates social login with Google or LinkedIn
   * 
   * @param provider - Social provider (google or linkedin)
   * @returns Observable with success status and optional error message
   */
  public initiateSocialLogin(provider: SocialProvider): Observable<{ success: boolean; message?: string }> {
    const redirectUri = `${window.location.origin}/auth/callback`;
    const state = this.generateState();
    
    // Store state for CSRF protection
    localStorage.setItem('socialLoginState', state);
    
    let authUrl: string;
    
    if (provider === 'google') {
      authUrl = `${this.API_URL}/auth/oauth2/authorize/google?redirect_uri=${encodeURIComponent(redirectUri)}&state=${state}`;
    } else if (provider === 'linkedin') {
      authUrl = `${this.API_URL}/auth/oauth2/authorize/linkedin?redirect_uri=${encodeURIComponent(redirectUri)}&state=${state}`;
    } else {
      return throwError(() => new Error('Unsupported social provider'));
    }
    
    // Redirect to OAuth provider
    window.location.href = authUrl;
    
    return new Observable(observer => {
      observer.next({ success: true });
      observer.complete();
    });
  }

  /**
   * Handles social login callback with authorization code
   * 
   * @param code - Authorization code from OAuth provider
   * @param state - State parameter for CSRF protection
   * @param provider - Social provider
   * @returns Observable with success status and optional error message
   */
  public handleSocialLoginCallback(code: string, state: string, provider: SocialProvider): Observable<{ success: boolean; message?: string; isNewUser?: boolean }> {
    const storedState = localStorage.getItem('socialLoginState');
    
    // Verify state parameter for CSRF protection
    if (state !== storedState) {
      return throwError(() => new Error('Invalid state parameter'));
    }
    
    // Clear stored state
    localStorage.removeItem('socialLoginState');
    
    const redirectUri = `${window.location.origin}/auth/callback`;
    const payload: SocialLoginRequest = {
      provider,
      code,
      state,
      redirectUri
    };
    
    return this.http.post<SocialLoginResponse>(`${this.API_URL}/auth/auth/social-login`, payload)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            // Save userProfile in localStorage if present
            if (response.body.userProfile) {
              localStorage.setItem('userProfile', JSON.stringify(response.body.userProfile));
            }
            
            const token = response.body.accessToken || response.body.jwtToken;
            if (!token) {
              throw new Error('No access token found in social login response');
            }
            
            const user = this.createUserFromResponse({ ...response.body, jwtToken: token });
            this.handleAuthSuccess(token, user, response.body.refreshToken);
            
            return { 
              success: true, 
              isNewUser: response.body.isNewUser || false 
            };
          } else {
            return { 
              success: false, 
              message: response.message || response.error || 'Social login failed' 
            };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Handles social signup for new users
   * 
   * @param code - Authorization code from OAuth provider
   * @param state - State parameter for CSRF protection
   * @param provider - Social provider
   * @param signupFlow - Signup flow type (platform-admin, tenant-admin, user)
   * @param role - Optional explicit user role (overrides signupFlow)
   * @param userInfo - Additional user information from social profile
   * @returns Observable with success status and optional error message
   */
  public handleSocialSignup(
    code: string, 
    state: string, 
    provider: SocialProvider, 
    signupFlow: 'platform-admin' | 'tenant-admin' | 'user',
    role?: 'PLATFORM.Management.Admin' | 'PLATFORM.Management.TenantAdmin' | 'PLATFORM.Management.User',
    userInfo?: { firstName?: string; lastName?: string; email?: string; avatar?: string }
  ): Observable<{ success: boolean; message?: string }> {
    const storedState = localStorage.getItem('socialSignupState');
    
    // Verify state parameter for CSRF protection
    if (state !== storedState) {
      return throwError(() => new Error('Invalid state parameter'));
    }
    
    // Clear stored state
    localStorage.removeItem('socialSignupState');
    
    const redirectUri = `${window.location.origin}/auth/callback`;
    const payload: SocialSignupRequest = {
      provider,
      code,
      state,
      redirectUri,
      signupFlow,
      role,
      userInfo
    };
    
    return this.http.post<SocialLoginResponse>(`${this.API_URL}/auth/auth/social-signup`, payload)
      .pipe(
        map(response => {
          if (response.statusCode === 201 && response.status === 'SUCCESS' && response.body) {
            // Save userProfile in localStorage if present
            if (response.body.userProfile) {
              localStorage.setItem('userProfile', JSON.stringify(response.body.userProfile));
            }
            
            const token = response.body.accessToken || response.body.jwtToken;
            if (!token) {
              throw new Error('No access token found in social signup response');
            }
            
            const user = this.createUserFromResponse({ ...response.body, jwtToken: token });
            this.handleAuthSuccess(token, user, response.body.refreshToken);
            
            return { success: true };
          } else {
            return { 
              success: false, 
              message: response.message || response.error || 'Social signup failed' 
            };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Generates a random state parameter for CSRF protection
   * 
   * @returns Random state string
   * @private
   */
  private generateState(): string {
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  }

  /**
   * Initiates password reset process
   * 
   * @param identifier - Username or email for password reset
   * @returns Observable with success status and optional error message
   */
  public resetPassword(identifier: string): Observable<{ success: boolean; message?: string }> {
    const payload: PasswordResetRequest = { identifier };

    return this.http.post<PasswordResetResponse>(`${this.API_URL}/auth/auth/reset-password`, payload)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS') {
            return { success: true, message: response.message };
          } else {
            return { 
              success: false, 
              message: response.message || response.error || 'Password reset failed' 
            };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Updates user profile information
   * 
   * @param profileData - Profile update data
   * @returns Observable with success status, optional error message, and updated user
   */
  public updateProfile(profileData: UpdateProfileRequest): Observable<{ success: boolean; message?: string; user?: User }> {
    return this.http.put<UpdateProfileResponse>(`${this.API_URL}/auth/auth/profile`, profileData)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            const updatedUser = response.body.user;
            this.currentUserSubject.next(updatedUser);
            localStorage.setItem(this.USER_KEY, JSON.stringify(updatedUser));
            return { success: true, user: updatedUser };
          } else {
            return { 
              success: false, 
              message: response.message || response.error || 'Profile update failed' 
            };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Refreshes the current access token using refresh token
   * 
   * @returns Observable with success status
   */
  public refreshToken(): Observable<boolean> {
    const refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);
    
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<RefreshTokenResponse>(`${this.API_URL}/auth/auth/refresh?refreshToken=${refreshToken}`, {})
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            const token = response.body.accessToken || response.body.jwtToken;
            if (!token) {
              throw new Error('No access token found in refresh token response');
            }
            localStorage.setItem(this.TOKEN_KEY, token);
            if (response.body.refreshToken) {
              localStorage.setItem(this.REFRESH_TOKEN_KEY, response.body.refreshToken);
            }
            if (response.body.expiresIn) {
              this.setTokenExpirationTimer(token);
            }
            return true;
          } else {
            return false;
          }
        }),
        catchError(error => {
          this.logout();
          return throwError(() => error);
        })
      );
  }

  /**
   * Logs out the current user
   * 
   * @returns Observable that completes when logout is done
   */
  public logout(): Observable<void> {
    return this.http.post<LogoutResponse>(`${this.API_URL}/auth/auth/logout`, {})
      .pipe(
        tap(() => {
          this.clearAuthData();
          this.redirectAfterLogout();
        }),
        map(() => void 0),
        catchError(() => {
          // Even if logout API fails, clear local data
          this.clearAuthData();
          this.redirectAfterLogout();
          return throwError(() => new Error('Logout failed'));
        })
      );
  }

  private redirectAfterLogout(): void {
    // Shell app always redirects to login page
    window.location.href = window.location.origin + '/login';
  }

  /**
   * Gets the current authenticated user
   * 
   * @returns Current user or null if not authenticated
   */
  public getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Gets the current authenticated user's username
   * 
   * @returns Username or null if not authenticated
   */
  public getCurrentUsername(): string | null {
    const user = this.getCurrentUser();
    return user ? user.username : null;
  }

  /**
   * Checks if user is currently authenticated
   * 
   * @returns True if authenticated, false otherwise
   */
  public isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  /**
   * Gets the current access token
   * 
   * @returns Access token or null if not available
   */
  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Determines the type of identifier (username, phone, or domain)
   * 
   * @param identifier - User identifier
   * @returns Identifier type
   */
  public getIdentifierType(identifier: string): 'username' | 'phone' | 'domain' {
    // Phone number pattern (basic)
    const phonePattern = /^\+?[\d\s\-\(\)]+$/;
    
    // Domain pattern (basic)
    const domainPattern = /^[a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9]\.[a-zA-Z]{2,}$/;
    
    if (phonePattern.test(identifier) && identifier.replace(/\D/g, '').length >= 10) {
      return 'phone';
    } else if (domainPattern.test(identifier)) {
      return 'domain';
    } else {
      return 'username';
    }
  }

  /**
   * Validates username format
   * 
   * @param username - Username to validate
   * @returns True if valid, false otherwise
   */
  public isValidUsername(username: string): boolean {
    // Username should be 3-30 characters, alphanumeric with underscores and hyphens
    const usernamePattern = /^[a-zA-Z0-9_-]{3,30}$/;
    return usernamePattern.test(username);
  }

  /**
   * Checks if username is available for registration
   * 
   * @param username - Username to check
   * @returns Observable with availability status
   */
  public checkUsernameAvailability(username: string): Observable<boolean> {
    return this.http.get<{ available: boolean }>(`${this.API_URL}/auth/auth/check-username?username=${username}`)
      .pipe(
        map(response => response.available),
        catchError(() => throwError(() => new Error('Failed to check username availability')))
      );
  }

  /**
   * Handles successful authentication
   * 
   * @param token - Access token
   * @param user - User information
   * @param refreshToken - Refresh token (optional)
   * @private
   */
  private handleAuthSuccess(token: string, user: User, refreshToken?: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    if (refreshToken) {
      localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
    }
    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
    this.setTokenExpirationTimer(token);
  }

  /**
   * Clears all authentication data
   * 
   * @private
   */
  public clearAuthData(): void {
    // Clear stored data
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    
    // Clear reactive state
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    
    // Clear timer
    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
      this.tokenExpirationTimer = null;
    }
  }

  /**
   * Checks if token is expired
   * 
   * @param token - Token to check
   * @returns True if expired, false otherwise
   * @private
   */
  private isTokenExpired(token: string): boolean {
    try {
      const payload = this.extractDataFromToken(token);
      const exp = payload.exp;
      
      if (!exp) {
        return true; // No expiration means expired
      }
      
      // Check if token expires in the next 5 minutes
      const now = Math.floor(Date.now() / 1000);
      return exp < (now + 300);
    } catch (error) {
      return true; // If we can't parse the token, consider it expired
    }
  }

  /**
   * Sets a timer to refresh the token before it expires
   * 
   * @param token - Token to set timer for
   * @private
   */
  private setTokenExpirationTimer(token: string): void {
    try {
      const payload = this.extractDataFromToken(token);
      const exp = payload.exp;
      
      if (!exp) {
        return; // No expiration, no timer needed
      }
      
      const now = Math.floor(Date.now() / 1000);
      const timeUntilExpiry = (exp - now - 300) * 1000; // Refresh 5 minutes before expiry
      
      if (timeUntilExpiry > 0) {
        // Clear existing timer
        if (this.tokenExpirationTimer) {
          clearTimeout(this.tokenExpirationTimer);
        }
        
        // Set new timer
        this.tokenExpirationTimer = setTimeout(() => {
          this.refreshToken().subscribe({
            error: () => {
              // If refresh fails, logout
              this.logout();
            }
          });
        }, timeUntilExpiry);
      }
    } catch (error) {
      console.error('Error setting token expiration timer:', error);
    }
  }

  /**
   * Generates a unique device identifier
   * 
   * @returns Device identifier
   * @private
   */
  private generateDeviceId(): string {
    // Simple device ID generation
    return 'device_' + Math.random().toString(36).substr(2, 9);
  }

  /**
   * Handles HTTP errors
   * 
   * @param error - HTTP error response
   * @returns Observable with error information
   * @private
   */
  private handleError(error: HttpErrorResponse): Observable<{ success: boolean; message: string }> {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      if (error.status === 401) {
        errorMessage = 'Invalid credentials';
      } else if (error.status === 403) {
        errorMessage = 'Access denied';
      } else if (error.status === 404) {
        errorMessage = 'Service not found';
      } else if (error.status >= 500) {
        errorMessage = 'Server error';
      } else if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else {
        errorMessage = `Error ${error.status}: ${error.statusText}`;
      }
    }
    
    return throwError(() => ({ success: false, message: errorMessage }));
  }

  public registerPlatformAdmin(registrationData: RegisterRequest): Observable<{ success: boolean; message?: string }> {
    return this.http.post<any>(`${this.API_URL}/auth/auth/create-platform-admin`, registrationData)
      .pipe(
        map(response => {
          if (response.statusCode === 201 && response.status === 'SUCCESS' && response.body) {
            return { success: true };
          } else {
            return { success: false, message: response.message || response.error || 'Signup failed' };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  public registerTenantAdmin(registrationData: RegisterRequest): Observable<{ success: boolean; message?: string }> {
    return this.http.post<any>(`${this.API_URL}/auth/auth/create-tenant-admin`, registrationData)
      .pipe(
        map(response => {
          if (response.statusCode === 201 && response.status === 'SUCCESS' && response.body) {
            return { success: true };
          } else {
            return { success: false, message: response.message || response.error || 'Signup failed' };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  public registerPlatformUser(registrationData: RegisterRequest): Observable<{ success: boolean; message?: string }> {
    return this.http.post<any>(`${this.API_URL}/auth/auth/create-platform-user`, registrationData)
      .pipe(
        map(response => {
          if (response.statusCode === 201 && response.status === 'SUCCESS' && response.body) {
            return { success: true };
          } else {
            return { success: false, message: response.message || response.error || 'Signup failed' };
          }
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Fetch user profile by username
   */
  public getUserProfile(username: string) {
    return this.http.get<any>(`${this.API_URL}/auth/auth/profile/${username}`);
  }
}