import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';

/**
 * Application routing configuration for Onified.ai
 * 
 * Defines the navigation structure and route mappings for the application.
 * Updated to include dashboard route for post-login navigation.
 * 
 * Route Structure:
 * - '' (empty) -> redirects to /login
 * - '/login' -> displays LoginComponent
 * - '/dashboard' -> displays DashboardComponent (placeholder for now)
 * - '**' (wildcard) -> redirects to /login for any unmatched routes
 * 
 * @constant routes - Array of route configurations
 */
export const routes: Routes = [
  // Default route - redirect empty path to login
  { 
    path: '', 
    redirectTo: '/login', 
    pathMatch: 'full' // Ensure exact match for empty path
  },
  
  // Login route - main authentication entry point
  { 
    path: 'login', 
    component: LoginComponent 
  },
  
  // Dashboard route - post-login landing page
  // TODO: Create DashboardComponent and replace with actual component
  { 
    path: 'dashboard', 
    loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  
  // Wildcard route - catch all unmatched routes and redirect to login
  // This should always be the last route in the array
  { 
    path: '**', 
    redirectTo: '/login' 
  }
];