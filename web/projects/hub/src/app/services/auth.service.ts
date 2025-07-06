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
    if (this.isMicroFrontendMode()) {
      const mockUser = this.getMockUserForMicroFrontend();
      if (mockUser) {
        this.currentUserSubject.next(mockUser);
        this.isAuthenticatedSubject.next(true);
        return;
      }
    }
    const token = localStorage.getItem('onified-token');
    const user = localStorage.getItem('onified-user');
    if (token && user) {
      try {
        const parsedUser = JSON.parse(user);
        if (this.isTokenExpired(token)) {
          this.refreshToken().subscribe({
            next: () => {
              this.currentUserSubject.next(parsedUser);
              this.isAuthenticatedSubject.next(true);
            },
            error: () => {
              this.logout();
            }
          });
        } else {
          this.currentUserSubject.next(parsedUser);
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
            if (response.body.userProfile) {
              localStorage.setItem('userProfile', JSON.stringify(response.body.userProfile));
            }
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

  private createUserFromResponse(responseBody: any): User {
    const token = responseBody.accessToken || responseBody.jwtToken;
    const tokenData = this.extractDataFromToken(token);
    const user: User = {
      id: tokenData.sub || '',
      username: responseBody.username || tokenData.preferred_username || '',
      name: responseBody.username || tokenData.preferred_username || '',
      roles: tokenData.realm_access?.roles || [],
      lastLogin: new Date().toISOString(),
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

  private extractDataFromToken(token: string): any {
    try {
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
      return window.location.pathname.includes('/host/console') || 
             window.location.href.includes('localhost:4202');
    } catch {
      return false;
    }
  }

  private getMockUserForMicroFrontend(): User | null {
    // Provide mock user data for micro-frontend development
    const mockUser: User = {
      id: 'mock-user-001',
      username: 'admin',
      name: 'John Doe',
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@onified.ai',
      roles: ['admin', 'user'],
      lastLogin: new Date().toISOString(),
      phone: '+1 (555) 123-4567',
      tenant: 'onified',
      avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
      department: 'Engineering',
      status: 'active'
    };
    return mockUser;
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