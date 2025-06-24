# Angular Frontend Setup Guide

## Overview
This guide explains how to set up and run the Angular frontend application with Keycloak OAuth2/OIDC integration.

## Prerequisites
- Node.js 18+ 
- npm or yarn
- Angular CLI (`npm install -g @angular/cli`)

## Quick Start

### 1. Install Dependencies
```bash
cd web
npm install
```

### 2. Install OAuth2 Dependencies (Optional - for advanced Keycloak integration)
```bash
npm install angular-oauth2-oidc @auth0/angular-jwt
```

### 3. Start Development Server
```bash
ng serve
```

The application will be available at: http://localhost:4200

## Configuration

### Environment Setup
The application uses environment-specific configuration files:

#### Development Environment (`src/environments/environment.ts`)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:9080/api',
  appName: 'Onified.ai',
  version: '1.0.0',
  enableLogging: true,
  theme: {
    default: 'light',
    allowToggle: true
  },
  auth: {
    tokenKey: 'onified-token',
    userKey: 'onified-user',
    refreshTokenKey: 'onified-refresh-token'
  },
  keycloak: {
    issuer: 'http://localhost:9090/realms/onified',
    clientId: 'onified-web-app',
    redirectUri: 'http://localhost:4200',
    postLogoutRedirectUri: 'http://localhost:4200',
    scope: 'openid profile email',
    responseType: 'code',
    showDebugInformation: true
  }
};
```

#### Production Environment (`src/environments/environment.prod.ts`)
Update with your production URLs and settings.

## Development Workflow

### Development Mode (Recommended)

#### 1. Start Development Server
```bash
cd web
ng serve
```

**Features:**
- Hot reload on code changes
- Source maps for debugging
- Fast compilation
- Live reload

#### 2. Development with Backend Services
```bash
# Terminal 1: Start backend services
docker-compose up -d

# Terminal 2: Start Angular dev server
cd web
ng serve
```

#### 3. Code Changes
- Edit files in `src/` directory
- Changes automatically reload in browser
- No need to rebuild or restart

### Production Build and Deployment

#### 1. Build for Production
```bash
cd web
ng build --configuration production
```

#### 2. Docker Build and Deploy
```bash
# Build Docker image
docker-compose build onified-frontend

# Deploy container
docker-compose up -d onified-frontend
```

#### 3. Update Production Build
```bash
# Stop frontend container
docker-compose stop onified-frontend

# Rebuild with latest code
docker-compose build onified-frontend

# Start updated container
docker-compose up -d onified-frontend

# Check logs
docker-compose logs -f onified-frontend
```

### Single Service Updates

#### Frontend Only Updates
```bash
# Development mode (hot reload)
cd web
ng serve

# Production mode (containerized)
docker-compose build onified-frontend
docker-compose up -d onified-frontend
```

#### Backend + Frontend Updates
```bash
# Update backend services
docker-compose build authentication-service
docker-compose up -d authentication-service

# Update frontend (if needed)
docker-compose build onified-frontend
docker-compose up -d onified-frontend
```

### Build Optimization

#### Production Build with Optimization
```bash
# Full optimization
ng build --configuration production --optimization

# With source maps (for debugging)
ng build --configuration production --source-map

# With bundle analyzer
ng build --configuration production --stats-json
npx webpack-bundle-analyzer dist/onified-app/stats.json
```

#### Development Build
```bash
# Fast development build
ng build --configuration development

# With watch mode
ng build --configuration development --watch
```

## Authentication Flow

### Current Implementation
The Angular app currently uses a custom authentication service that communicates with the authentication service API:

1. **Login**: User enters credentials → API call to `/api/auth/login`
2. **Token Storage**: OAuth2 tokens stored in localStorage
3. **Token Refresh**: Automatic refresh before expiration
4. **Logout**: API call to `/api/auth/logout`

### Keycloak Integration (Optional)
For full OAuth2/OIDC integration with Keycloak:

1. **Install OAuth2 Dependencies**:
   ```bash
   npm install angular-oauth2-oidc @auth0/angular-jwt
   ```

2. **Update App Configuration** (`src/app/app.config.ts`):
   ```typescript
   import { provideOAuthClient } from 'angular-oauth2-oidc';
   import { provideJwtHelper } from '@auth0/angular-jwt';

   export const appConfig: ApplicationConfig = {
     providers: [
       // ... existing providers
       provideOAuthClient(),
       provideJwtHelper()
     ]
   };
   ```

3. **Create OAuth2 Service**:
   ```typescript
   // src/app/services/oauth.service.ts
   import { Injectable } from '@angular/core';
   import { OAuthService } from 'angular-oauth2-oidc';
   import { environment } from '../../environments/environment';

   @Injectable({
     providedIn: 'root'
   })
   export class OAuthService {
     constructor(private oauthService: OAuthService) {
       this.configureOAuth();
     }

     private configureOAuth(): void {
       this.oauthService.configure({
         issuer: environment.keycloak.issuer,
         clientId: environment.keycloak.clientId,
         redirectUri: environment.keycloak.redirectUri,
         postLogoutRedirectUri: environment.keycloak.postLogoutRedirectUri,
         scope: environment.keycloak.scope,
         responseType: environment.keycloak.responseType,
         showDebugInformation: environment.keycloak.showDebugInformation
       });
     }

     login(): void {
       this.oauthService.initLoginFlow();
     }

     logout(): void {
       this.oauthService.logOut();
     }
   }
   ```

## Project Structure

```
web/
├── src/
│   ├── app/
│   │   ├── components/          # UI Components
│   │   │   ├── login/          # Login component
│   │   │   ├── dashboard/      # Dashboard components
│   │   │   └── shared/         # Shared components
│   │   ├── services/           # Business logic services
│   │   │   ├── auth.service.ts # Authentication service
│   │   │   └── theme.service.ts # Theme management
│   │   ├── models/             # TypeScript interfaces
│   │   │   └── auth.models.ts  # Authentication models
│   │   ├── interceptors/       # HTTP interceptors
│   │   │   └── auth.interceptor.ts # Token injection
│   │   └── app.config.ts       # Application configuration
│   ├── environments/           # Environment configurations
│   ├── assets/                 # Static assets
│   └── styles/                 # Global styles
├── package.json               # Dependencies
├── angular.json              # Angular CLI configuration
├── Dockerfile                # Docker build configuration
└── nginx.conf               # Nginx server configuration
```

## Key Features

### Authentication Service
- **Multi-method login**: Username, phone, domain
- **Token management**: Automatic refresh, expiration handling
- **Session persistence**: localStorage with reactive state
- **Error handling**: Comprehensive error management

### HTTP Interceptor
- **Automatic token injection**: Adds Bearer tokens to API requests
- **Token refresh**: Handles 401 responses with automatic refresh
- **Error handling**: Centralized error processing

### Reactive State Management
- **BehaviorSubjects**: Real-time authentication state
- **Observables**: Reactive data streams
- **Local storage**: Persistent authentication state

## Development Commands

### 1. Making Changes
```bash
# Start development server with auto-reload
ng serve

# Build for production
ng build --configuration production

# Run tests
ng test

# Run e2e tests
ng e2e

# Generate component
ng generate component components/new-component

# Generate service
ng generate service services/new-service
```

### 2. Code Organization
- **Components**: UI logic and presentation
- **Services**: Business logic and API communication
- **Models**: TypeScript interfaces and types
- **Interceptors**: HTTP request/response processing

### 3. Styling
- **SCSS**: Component-specific styles
- **Global styles**: `src/styles.scss`
- **Theme system**: Light/dark theme support

## Docker Operations

### Build and Deploy
```bash
# Build production image
docker-compose build onified-frontend

# Deploy container
docker-compose up -d onified-frontend

# Check container status
docker-compose ps onified-frontend

# View logs
docker-compose logs -f onified-frontend
```

### Development with Docker
```bash
# Stop containerized frontend
docker-compose stop onified-frontend

# Start development server
cd web
ng serve

# Access at http://localhost:4200
```

### Container Management
```bash
# Restart frontend container
docker-compose restart onified-frontend

# Rebuild without cache
docker-compose build --no-cache onified-frontend

# Remove old images
docker rmi repository_onified-frontend

# Access container shell
docker-compose exec onified-frontend /bin/sh
```

## Testing

### Unit Tests
```bash
ng test
```

### E2E Tests
```bash
ng e2e
```

### Manual Testing
1. Start the backend services (see `DEPLOYMENT-LOCAL.md`)
2. Start the Angular app: `ng serve`
3. Navigate to http://localhost:4200
4. Test login with Keycloak users

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure backend CORS is configured for `http://localhost:4200`
   - Check API gateway configuration

2. **Authentication Failures**
   - Verify Keycloak is running and configured
   - Check client secret in environment
   - Ensure user exists in Keycloak

3. **Build Errors**
   - Clear node_modules: `rm -rf node_modules package-lock.json && npm install`
   - Update Angular CLI: `npm update -g @angular/cli`
   - Check TypeScript errors: `ng build --verbose`

4. **Port Conflicts**
   - Change port: `ng serve --port 4201`
   - Update environment configuration

5. **Docker Build Failures**
   - Clear Docker cache: `docker system prune -a`
   - Check Dockerfile syntax
   - Verify nginx configuration

### Debug Commands
```bash
# Check Angular version
ng version

# Check dependencies
npm list

# Clear cache
npm cache clean --force

# Reinstall dependencies
rm -rf node_modules package-lock.json && npm install

# Check Docker images
docker images | grep onified-frontend

# Check container logs
docker-compose logs -f onified-frontend

# Test nginx configuration
docker-compose exec onified-frontend nginx -t
```

### Performance Issues
```bash
# Analyze bundle size
ng build --configuration production --stats-json
npx webpack-bundle-analyzer dist/onified-app/stats.json

# Check memory usage
docker stats onified-frontend

# Monitor nginx performance
docker-compose exec onified-frontend nginx -s reload
```

## Production Deployment

### 1. Build Application
```bash
ng build --configuration production
```

### 2. Deploy to Web Server
- Copy `dist/onified-app` to your web server
- Configure server for SPA routing
- Set up HTTPS

### 3. Docker Deployment
```bash
# Build production image
docker-compose build onified-frontend

# Deploy to production
docker-compose -f docker-compose.prod.yml up -d onified-frontend
```

### 4. Environment Configuration
- Update `environment.prod.ts` with production URLs
- Configure Keycloak for production domain
- Set up proper CORS policies

## Next Steps

1. **Implement OAuth2 Integration**: Add full Keycloak OAuth2 flow
2. **Add Unit Tests**: Comprehensive test coverage
3. **Implement E2E Tests**: End-to-end testing
4. **Add Error Boundaries**: Better error handling
5. **Implement PWA**: Progressive Web App features
6. **Add Internationalization**: Multi-language support 

## Environment Variable Management

All environment variables for the platform are now managed via per-environment JSON config files in the `configs/` directory at the project root.

- `configs/config.local.json` (local development)
- `configs/config.dev.json` (development)
- `configs/config.prod.json` (production)

To generate a `.env` file for your desired environment, use the setup script:

```bash
bash setup-env.sh local   # for local
bash setup-env.sh dev     # for development
bash setup-env.sh prod    # for production
```

Edit the appropriate `configs/config.<env>.json` file to set your environment variables. The script will create a `.env` file at the project root, which is used by Docker Compose and all backend services.

> **Note:** The `configs/` directory is gitignored and should not be committed to version control.

## Automated Stack Build

For a fully automated environment setup and stack build, use the provided scripts:

### Linux/macOS
```bash
./build-stack.sh local    # or dev, prod, etc.
```

### Windows (PowerShell)
```powershell
./build-stack.ps1 local   # or dev, prod, etc.
```

These scripts will:
- Generate the correct .env file for your chosen environment using the configs in configs/.
- Build and start the stack with docker-compose up --build.

> You can still use the setup scripts in configs/ directly if you want to only generate the .env file without starting the stack. 