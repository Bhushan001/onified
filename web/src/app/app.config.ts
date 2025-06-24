import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';
import { provideOAuthClient } from 'angular-oauth2-oidc';

/**
 * Application configuration for the Onified.ai Angular application
 * 
 * This configuration sets up the core providers and services needed for the application:
 * - Router configuration for navigation
 * - HTTP client with interceptor support
 * - Authentication interceptor for automatic token injection
 * - OAuth2/OIDC providers for Keycloak integration
 * - Browser animations support
 * 
 * @constant appConfig - Main application configuration object
 */
export const appConfig: ApplicationConfig = {
  providers: [
    // Configure the Angular router with application routes
    provideRouter(routes),
    
    // Provide HTTP client with support for interceptors
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),
    
    // Register the OAuth2/OIDC providers for Keycloak integration
    provideOAuthClient(),
    
    // Provide browser animations
    provideAnimations()
  ]
};