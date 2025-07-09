import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { InternalLoginRequest, LoginRequest, LoginResponse, LogoutResponse, RefreshTokenResponse, User } from '../models/auth.models';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = environment.apiUrl;
  private readonly TOKEN_KEY = environment.auth.tokenKey;
  private readonly USER_KEY = environment.auth.userKey;
  private readonly REFRESH_TOKEN_KEY = environment.auth.refreshTokenKey;

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private tokenExpirationTimer: any;

  constructor(private http: HttpClient) {
    this.checkAuthStatus();
  }

  private checkAuthStatus(): void {
    const token = localStorage.getItem(this.TOKEN_KEY);
    const userData = localStorage.getItem(this.USER_KEY);
    
    if (token && userData) {
      try {
        const parsedUserData = JSON.parse(userData);
        if (this.isTokenExpired(token)) {
          this.refreshToken().subscribe({
            next: () => {
              const user = this.createUserFromUserProfile(parsedUserData);
              this.currentUserSubject.next(user);
              this.isAuthenticatedSubject.next(true);
            },
            error: () => {
              this.logout();
            }
          });
        } else {
          const user = this.createUserFromUserProfile(parsedUserData);
          this.currentUserSubject.next(user);
          this.isAuthenticatedSubject.next(true);
          this.setTokenExpirationTimer(token);
        }
      } catch (error) {
        this.logout();
      }
    }
  }

  public login(credentials: InternalLoginRequest): Observable<{ success: boolean; message?: string }> {
    const identifierType = credentials.identifierType || this.getIdentifierType(credentials.identifier);
    const loginPayload = this.createLoginPayload(credentials.identifier, credentials.password, identifierType);
    return this.http.post<LoginResponse>(`${this.API_URL}/auth/auth/login`, loginPayload)
      .pipe(
        map(response => {
          if (response.statusCode === 200 && response.status === 'SUCCESS' && response.body) {
            // Store user data using the correct key
            if (response.body.userProfile) {
              localStorage.setItem(this.USER_KEY, JSON.stringify(response.body.userProfile));
            } else if (response.body.user) {
              localStorage.setItem(this.USER_KEY, JSON.stringify(response.body.user));
            }
            
            const token = response.body.accessToken || response.body.jwtToken;
            if (!token) {
              throw new Error('No access token found in login response');
            }
            
            // Handle placeholder tokens (social login)
            if (token.startsWith('placeholder_')) {
              const user = this.createUserFromResponse({ ...response.body, jwtToken: token });
              this.handleAuthSuccess(token, user, response.body.refreshToken);
              return { success: true };
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

  private createUserFromResponse(responseBody: any): User {
    const token = responseBody.accessToken || responseBody.jwtToken;
    const tokenData = this.extractDataFromToken(token);
    
    // For social login, use user data from response
    const user: User = {
      id: responseBody.id || responseBody.userId || tokenData.sub || '',
      username: responseBody.username || responseBody.email || tokenData.preferred_username || '',
      name: responseBody.name || responseBody.username || responseBody.email || tokenData.preferred_username || '',
      roles: responseBody.roles || tokenData.realm_access?.roles || ['user'],
      lastLogin: new Date().toISOString(),
      email: responseBody.email || tokenData.email,
      firstName: responseBody.firstName || responseBody.givenName || tokenData.given_name || '',
      lastName: responseBody.lastName || responseBody.familyName || tokenData.family_name || '',
      phone: responseBody.phone || '',
      tenant: responseBody.tenant || '',
      avatar: responseBody.avatar || responseBody.picture || '',
      department: responseBody.department || '',
      status: responseBody.status || 'active'
    };
    return user;
  }

  private createUserFromUserProfile(userProfile: any): User {
    const user: User = {
      id: userProfile.id || '',
      username: userProfile.username || '',
      name: userProfile.username || '',
      roles: userProfile.roles || [],
      lastLogin: new Date().toISOString(),
      firstName: userProfile.firstName || '',
      lastName: userProfile.lastName || '',
      phone: userProfile.phone || '',
      tenant: userProfile.tenant || '',
      avatar: userProfile.avatar || '',
      department: userProfile.department || '',
      status: userProfile.status || 'active'
    };
    return user;
  }

  private extractDataFromToken(token: string): any {
    try {
      // Handle placeholder tokens (social login)
      if (token.startsWith('placeholder_')) {
        return {
          sub: 'social_user',
          preferred_username: 'social_user',
          email: 'social@example.com',
          realm_access: { roles: ['user'] }
        };
      }
      
      if (!token || typeof token !== 'string' || token.split('.').length < 2) {
        throw new Error('Invalid or missing JWT token');
      }
      const payload = token.split('.')[1];
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
      return JSON.parse(atob(padded));
    } catch (error) {
      console.error('Error extracting data from token:', error);
      return {};
    }
  }

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

  public logout(): Observable<void> {
    return this.http.post<LogoutResponse>(`${this.API_URL}/auth/auth/logout`, {})
      .pipe(
        tap(() => {
          this.clearAuthData();
          this.redirectAfterLogout();
        }),
        map(() => void 0),
        catchError(() => {
          this.clearAuthData();
          this.redirectAfterLogout();
          return throwError(() => new Error('Logout failed'));
        })
      );
  }

  private redirectAfterLogout(): void {
    if (this.isMicroFrontendMode()) {
      window.location.href = 'http://localhost:4200/login';
    } else {
      window.location.href = window.location.origin + '/auth-config';
    }
  }

  public getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  public getCurrentUsername(): string | null {
    const user = this.getCurrentUser();
    return user ? user.username : null;
  }

  public isAuthenticated(): boolean {
    const isAuth = this.isAuthenticatedSubject.value;
    return isAuth;
  }

  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  public getIdentifierType(identifier: string): 'username' | 'phone' | 'domain' {
    if (/^\d{10,}$/.test(identifier)) {
      return 'phone';
    } else if (identifier.includes('.')) {
      return 'domain';
    } else {
      return 'username';
    }
  }

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

  public clearAuthData(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    // Clean up old keys
    localStorage.removeItem('userProfile');
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
      this.tokenExpirationTimer = null;
    }
  }

  private isTokenExpired(token: string): boolean {
    try {
      // Handle placeholder tokens (social login)
      if (token.startsWith('placeholder_')) {
        // Extract timestamp from placeholder token
        const timestampStr = token.replace('placeholder_', '');
        const timestamp = parseInt(timestampStr, 10);
        if (isNaN(timestamp)) {
          return true;
        }
        const now = Date.now();
        const expirationTime = timestamp + (24 * 60 * 60 * 1000); // 24 hours
        return now >= expirationTime;
      }
      
      const payload = this.extractDataFromToken(token);
      const exp = payload.exp;
      if (!exp) {
        return true;
      }
      const now = Math.floor(Date.now() / 1000);
      return exp <= now;
    } catch (error) {
      return true;
    }
  }

  private setTokenExpirationTimer(token: string): void {
    try {
      // Handle placeholder tokens (social login)
      if (token.startsWith('placeholder_')) {
        const timestampStr = token.replace('placeholder_', '');
        const timestamp = parseInt(timestampStr, 10);
        if (isNaN(timestamp)) {
          return;
        }
        const now = Date.now();
        const expirationTime = timestamp + (24 * 60 * 60 * 1000); // 24 hours
        const expiresIn = expirationTime - now;
        if (expiresIn > 0) {
          this.tokenExpirationTimer = setTimeout(() => {
            this.logout(); // For placeholder tokens, just logout when expired
          }, expiresIn - 60000); // Logout 1 minute before expiry
        }
        return;
      }
      
      const payload = this.extractDataFromToken(token);
      const exp = payload.exp;
      if (!exp) {
        return;
      }
      const now = Math.floor(Date.now() / 1000);
      const expiresIn = exp - now;
      if (expiresIn > 0) {
        this.tokenExpirationTimer = setTimeout(() => {
          this.refreshToken().subscribe();
        }, expiresIn * 1000 - 60000); // Refresh 1 minute before expiry
      }
    } catch (error) {
      // Ignore errors
    }
  }

  private isMicroFrontendMode(): boolean {
    try {
      return window.location.pathname.includes('/host/workspace') || 
             window.location.href.includes('localhost:4300');
    } catch {
      return false;
    }
  }



  private handleError(error: HttpErrorResponse): Observable<{ success: boolean; message: string }> {
    let errorMessage = 'An unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client/network error: ${error.error.message}`;
    } else if (error.error && error.error.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }
    return throwError(() => ({ success: false, message: errorMessage }));
  }
} 