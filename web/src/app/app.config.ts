import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';
import { routes } from './app.routes';
import { AuthInterceptor } from './interceptors/auth.interceptor';

/**
 * Application configuration for the Onified.ai Angular application
 * 
 * This configuration sets up the core providers and services needed for the application:
 * - Zone change detection optimization for better performance
 * - Router configuration for navigation
 * - HTTP client with interceptor support
 * - Authentication interceptor for automatic token injection
 * 
 * @constant appConfig - Main application configuration object
 */
export const appConfig: ApplicationConfig = {
  providers: [
    // Enable zone change detection with event coalescing for better performance
    // This reduces the number of change detection cycles by batching events
    provideZoneChangeDetection({ eventCoalescing: true }),
    
    // Configure the Angular router with application routes
    provideRouter(routes),
    
    // Provide HTTP client with support for legacy interceptors
    // This allows us to use class-based interceptors like AuthInterceptor
    provideHttpClient(withInterceptorsFromDi()),
    
    // Register the authentication interceptor to automatically add
    // authorization headers to HTTP requests
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true // Allow multiple interceptors to be registered
    }
  ]
};