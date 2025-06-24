# Angular Web Application

## Overview
The Angular Web Application is the frontend user interface for the Onified platform. It provides a modern, responsive web interface for users to interact with all microservices through the API Gateway.

## Build Order: 7th Service (Final)
This service should be built last, after all backend services are running.

## Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular Web   │    │  API Gateway    │    │   Keycloak      │
│   Application   │◄──►│  (Spring Cloud) │◄──►│  (Identity)     │
│   Port: 4200    │    │   Port: 9080    │    │   Port: 8080    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Configuration

### Environment Configuration

#### Development Environment (environment.ts)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:9080',
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'onified',
    clientId: 'web-app'
  },
  appName: 'Onified Platform',
  version: '1.0.0'
};
```

#### Production Environment (environment.prod.ts)
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.onified.com',
  keycloak: {
    url: 'https://auth.onified.com',
    realm: 'onified',
    clientId: 'web-app'
  },
  appName: 'Onified Platform',
  version: '1.0.0'
};
```

### Docker Configuration
```dockerfile
# Stage 1: Build the application
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

# Stage 2: Serve the application
FROM nginx:alpine
COPY --from=build /app/dist/web /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Nginx Configuration (nginx.conf)
```nginx
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    server {
        listen 80;
        server_name localhost;
        root /usr/share/nginx/html;
        index index.html;
        
        # Handle Angular routing
        location / {
            try_files $uri $uri/ /index.html;
        }
        
        # API proxy
        location /api/ {
            proxy_pass http://onified-gateway:9080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # Static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
}
```

## Dependencies

### Core Dependencies
```json
{
  "dependencies": {
    "@angular/core": "^17.0.0",
    "@angular/common": "^17.0.0",
    "@angular/router": "^17.0.0",
    "@angular/forms": "^17.0.0",
    "@angular/material": "^17.0.0",
    "@angular/cdk": "^17.0.0",
    "@angular/flex-layout": "^15.0.0-beta.42",
    "keycloak-js": "^23.0.0",
    "rxjs": "^7.8.0",
    "zone.js": "^0.14.0"
  },
  "devDependencies": {
    "@angular-devkit/build-angular": "^17.0.0",
    "@angular/cli": "^17.0.0",
    "@angular/compiler-cli": "^17.0.0",
    "@types/node": "^20.0.0",
    "typescript": "^5.0.0"
  }
}
```

## Application Structure

### Core Modules
```
src/app/
├── app.component.ts
├── app.component.html
├── app.component.scss
├── app.module.ts
├── app-routing.module.ts
├── components/
│   ├── dashboard/
│   ├── login/
│   ├── shared/
│   └── users/
├── services/
│   ├── auth.service.ts
│   ├── theme.service.ts
│   └── font-loader.service.ts
├── models/
│   └── auth.models.ts
├── interceptors/
│   └── auth.interceptor.ts
└── shared/
    └── page-styles.scss
```

### Component Architecture
- **Dashboard**: Main application dashboard
- **Login**: Authentication interface
- **Shared**: Reusable components (header, footer, sidebar)
- **Users**: User management interface
- **Security**: Security and access control
- **Foundation**: Platform foundation components

## Authentication Integration

### Keycloak Configuration
```typescript
// auth.service.ts
import Keycloak from 'keycloak-js';

export class AuthService {
  private keycloak: Keycloak.KeycloakInstance;
  
  constructor() {
    this.keycloak = new Keycloak({
      url: environment.keycloak.url,
      realm: environment.keycloak.realm,
      clientId: environment.keycloak.clientId
    });
  }
  
  async init(): Promise<void> {
    try {
      await this.keycloak.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html'
      });
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
    }
  }
  
  login(): void {
    this.keycloak.login();
  }
  
  logout(): void {
    this.keycloak.logout();
  }
  
  getToken(): string | undefined {
    return this.keycloak.token;
  }
  
  isAuthenticated(): boolean {
    return !!this.keycloak.authenticated;
  }
}
```

### HTTP Interceptor
```typescript
// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(req);
  }
}
```

## Routing Configuration

### App Routing Module
```typescript
// app-routing.module.ts
const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', component: DashboardHomeComponent },
      { path: 'users', component: UserManagementComponent },
      { path: 'security', component: SecurityComponent },
      { path: 'foundation', component: FoundationComponent }
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];
```

### Auth Guard
```typescript
// auth.guard.ts
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(private authService: AuthService, private router: Router) {}
  
  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    
    this.router.navigate(['/login']);
    return false;
  }
}
```

## UI Components

### Dashboard Component
```typescript
// dashboard.component.ts
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  
  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}
  
  ngOnInit(): void {
    this.loadUserData();
  }
  
  private loadUserData(): void {
    // Load user profile and permissions
  }
}
```

### Login Component
```typescript
// login.component.ts
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  
  constructor(private authService: AuthService) {}
  
  ngOnInit(): void {
    // Initialize login form
  }
  
  login(): void {
    this.authService.login();
  }
}
```

## Services

### User Service
```typescript
// user.service.ts
@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  private apiUrl = environment.apiUrl;
  
  constructor(private http: HttpClient) {}
  
  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/api/v1/users`);
  }
  
  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/api/v1/users/${id}`);
  }
  
  createUser(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/api/v1/users`, user);
  }
  
  updateUser(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/api/v1/users/${id}`, user);
  }
  
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/v1/users/${id}`);
  }
}
```

### Theme Service
```typescript
// theme.service.ts
@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  
  private currentTheme = 'light';
  
  setTheme(theme: 'light' | 'dark'): void {
    this.currentTheme = theme;
    document.body.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
  }
  
  getTheme(): string {
    return this.currentTheme;
  }
  
  toggleTheme(): void {
    const newTheme = this.currentTheme === 'light' ? 'dark' : 'light';
    this.setTheme(newTheme);
  }
}
```

## Styling

### Global Styles (styles.scss)
```scss
@import 'styles/themes/variables';
@import 'styles/themes/light-theme';
@import 'styles/themes/dark-theme';
@import 'styles/mixins';
@import 'styles/responsive';

// Global styles
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Roboto', sans-serif;
  line-height: 1.6;
  color: var(--text-color);
  background-color: var(--background-color);
}

// Theme variables
:root {
  --primary-color: #1976d2;
  --secondary-color: #dc004e;
  --background-color: #ffffff;
  --surface-color: #f5f5f5;
  --text-color: #333333;
  --text-secondary: #666666;
}
```

### Component Styles
```scss
// dashboard.component.scss
.dashboard {
  display: flex;
  height: 100vh;
  
  &__sidebar {
    width: 250px;
    background-color: var(--surface-color);
    border-right: 1px solid var(--border-color);
  }
  
  &__main {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  
  &__header {
    height: 64px;
    background-color: var(--primary-color);
    color: white;
    display: flex;
    align-items: center;
    padding: 0 24px;
  }
  
  &__content {
    flex: 1;
    padding: 24px;
    overflow-y: auto;
  }
}
```

## Testing

### Unit Tests
```bash
# Run unit tests
ng test

# Run with coverage
ng test --code-coverage

# Run specific test
ng test --include="**/auth.service.spec.ts"
```

### E2E Tests
```bash
# Run e2e tests
ng e2e

# Run with specific configuration
ng e2e --configuration=production
```

### Manual Testing
```bash
# Start development server
ng serve

# Build for production
ng build --configuration=production

# Serve production build
ng serve --configuration=production
```

## Deployment

### Docker Deployment
```bash
# Build and start
docker-compose up -d web

# Check status
docker-compose ps web

# View logs
docker-compose logs web
```

### Local Development
```bash
# Install dependencies
npm install

# Start development server
ng serve

# Build application
ng build

# Serve production build
ng serve --configuration=production
```

## Troubleshooting

### Common Issues

1. **Authentication Issues**
   ```bash
   # Check Keycloak configuration
   # Verify client settings
   # Check redirect URIs
   ```

2. **API Connection Issues**
   ```bash
   # Check API Gateway status
   # Verify CORS configuration
   # Check network connectivity
   ```

3. **Build Issues**
   ```bash
   # Clear node modules
   rm -rf node_modules package-lock.json
   npm install
   
   # Clear Angular cache
   ng cache clean
   ```

### Log Analysis
```bash
# View browser console logs
# Check network tab for API calls
# Verify authentication flow
```

## Performance Optimization

### Build Optimization
- Tree shaking
- Code splitting
- Lazy loading
- Bundle analysis

### Runtime Optimization
- Change detection optimization
- Memory leak prevention
- Image optimization
- Caching strategies

## Security Best Practices

### Frontend Security
- Input validation
- XSS prevention
- CSRF protection
- Secure storage

### Authentication Security
- Token management
- Session handling
- Secure logout
- Error handling

## Next Steps
After Angular Web Application is running:
1. Test complete user journey
2. Verify all integrations
3. Performance testing
4. Security testing
5. User acceptance testing 