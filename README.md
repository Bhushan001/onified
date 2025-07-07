# Onified Microservices Platform

A comprehensive microservices platform built with Spring Boot, Angular Module Federation, and Keycloak for identity management.

## 🏗️ System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular Shell │    │  API Gateway    │    │   Eureka Server │
│   Application   │◄──►│  (Spring Cloud) │◄──►│  (Discovery)    │
│   (Port: 4200)  │    └─────────────────┘    └─────────────────┘
└─────────────────┘
         │
         ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Hub App       │    │  Console App    │    │  Workspace App  │
│  (Port: 4300)   │    │  (Port: 4400)   │    │  (Port: 4500)   │
│  (Admin Users)  │    │(Tenant Admins)  │    │ (Regular Users) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Authentication │    │ Application     │    │ Permission      │
│     Service     │    │ Config Service  │    │ Registry        │
│  (Keycloak OIDC)│    │                 │    │ Service         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ User Management │    │   PostgreSQL    │    │    Keycloak     │
│     Service     │    │   Database      │    │  (Identity)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📋 Service Overview

### Frontend Applications (Angular Module Federation)

#### 1. **Shell Application** (Port: 4200)
- **Purpose**: Main host application that orchestrates remote apps
- **Technology**: Angular 20 + Module Federation
- **Features**: 
  - Role-based routing to remote applications
  - Centralized authentication and user management
  - Dynamic loading of remote modules
  - Responsive design with modern UI
- **Access**: http://localhost:4200

#### 2. **Hub Application** (Port: 4300)
- **Purpose**: Admin dashboard for platform administrators
- **Technology**: Angular 20 + Module Federation (Remote)
- **Features**:
  - Platform-wide administration tools
  - User and tenant management
  - System configuration and monitoring
  - Advanced analytics and reporting
- **Access**: http://localhost:4300 (standalone) or via Shell app
- **User Role**: Admin users

#### 3. **Console Application** (Port: 4400)
- **Purpose**: Tenant administration dashboard
- **Technology**: Angular 20 + Module Federation (Remote)
- **Features**:
  - Tenant-specific configuration
  - User management within tenant scope
  - Billing and licensing management
  - Tenant analytics and reporting
- **Access**: http://localhost:4400 (standalone) or via Shell app
- **User Role**: Tenant admin users

#### 4. **Workspace Application** (Port: 4500)
- **Purpose**: End-user workspace for regular users
- **Technology**: Angular 20 + Module Federation (Remote)
- **Features**:
  - User workspace and productivity tools
  - Personal settings and preferences
  - Application access and management
  - User-specific analytics
- **Access**: http://localhost:4500 (standalone) or via Shell app
- **User Role**: Regular users

### Backend Services

#### 5. **Eureka Server** (Port: 8761)
- **Purpose**: Service discovery and registration
- **Technology**: Spring Cloud Netflix Eureka Server
- **Configuration**: Service registry for all microservices
- **Health Check**: http://localhost:8761

#### 6. **API Gateway** (Port: 9080)
- **Purpose**: Centralized routing, security, and load balancing
- **Technology**: Spring Cloud Gateway
- **Configuration**: Route definitions, security filters, circuit breakers
- **Features**: CORS handling, authentication, rate limiting

#### 7. **Authentication Service** (Port: 9083)
- **Purpose**: User authentication and authorization
- **Technology**: Spring Boot + Keycloak OIDC
- **Configuration**: JWT token validation, OAuth2 integration
- **Features**: Login/logout, token refresh, user session management

#### 8. **User Management Service** (Port: 9085)
- **Purpose**: User CRUD operations and profile management
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: Database connection, validation rules
- **Features**: User creation, updates, deletion, profile management

#### 9. **Permission Registry Service** (Port: 9084)
- **Purpose**: Role-based access control (RBAC) and permissions
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: Permission definitions, role mappings
- **Features**: Permission management, role assignment, access control

#### 10. **Application Config Service** (Port: 9082)
- **Purpose**: Application configuration management
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: App settings, feature flags, configuration storage
- **Features**: Dynamic configuration, version control, environment-specific settings

#### 11. **Platform Management Service** (Port: 9081)
- **Purpose**: Platform-wide management and administration
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: Platform settings, tenant management
- **Features**: Platform configuration, tenant isolation, system administration

#### 12. **Tenant Management Service** (Port: 9086)
- **Purpose**: Multi-tenant architecture management
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: Tenant isolation, data partitioning
- **Features**: Tenant creation, isolation, data management

### Infrastructure Services

#### 13. **PostgreSQL Database** (Port: 5432)
- **Purpose**: Primary data storage for all services
- **Technology**: PostgreSQL 15
- **Configuration**: Database schemas, connection pooling
- **Features**: ACID compliance, JSONB support, partitioning

#### 14. **Keycloak** (Port: 9090)
- **Purpose**: Identity and access management
- **Technology**: Keycloak 24.0.2
- **Configuration**: Realms, clients, users, roles
- **Features**: OAuth2/OIDC, SSO, user federation

## 🎯 Frontend Architecture

### Module Federation Setup

The frontend uses Angular Module Federation to create a micro-frontend architecture:

```
Shell App (Host)
├── Hub App (Remote) - Admin Dashboard
├── Console App (Remote) - Tenant Admin Dashboard
└── Workspace App (Remote) - User Workspace
```

### Key Features

#### **Shell Application**
- **Portal Loader Service**: Dynamically loads remote applications
- **Role-based Routing**: Routes users to appropriate apps based on their role
- **Authentication Integration**: Centralized auth with Keycloak
- **Error Handling**: Graceful fallbacks for remote app failures
- **Responsive Design**: Modern UI with SCSS theming

#### **Remote Applications**
- **Independent Development**: Each app can be developed and deployed independently
- **Shared Dependencies**: Common libraries shared across apps
- **Dynamic Styling**: Injected styles ensure proper appearance when loaded in shell
- **Internal Routing**: Each app manages its own internal navigation
- **Component Wrappers**: DashboardWrapperComponent handles routing and styling

### Technology Stack

#### **Frontend Framework**
- **Angular**: 20.x (Latest LTS)
- **TypeScript**: Latest stable version
- **SCSS**: Advanced styling with variables and mixins
- **Module Federation**: @angular-architects/module-federation
- **Build Tools**: ngx-build-plus for webpack integration

#### **UI/UX**
- **Responsive Design**: Mobile-first approach
- **Modern Components**: Custom Angular components
- **Font Awesome**: Icon library
- **Roboto Font**: Google Fonts integration
- **Theme System**: Light/dark theme support

#### **Development Tools**
- **Angular CLI**: Latest version
- **Webpack**: Custom configurations for Module Federation
- **ESLint**: Code quality and consistency
- **Prettier**: Code formatting

## ⚙️ Basic Configuration Needs

### Environment Variables
Each service requires specific environment variables for:
- Database connections
- Service discovery URLs
- Authentication settings
- Logging configuration
- Port assignments

### Frontend Configuration
- **Module Federation**: Remote entry URLs and exposed modules
- **Authentication**: Keycloak client configurations
- **API Endpoints**: Backend service URLs
- **Environment Variables**: Build-time configuration

### Database Configuration
- PostgreSQL connection strings
- Database credentials
- Schema initialization
- Migration scripts

### Security Configuration
- Keycloak realm settings
- OAuth2 client configurations
- JWT secret keys
- CORS policies

### Service Discovery
- Eureka server URLs
- Service registration settings
- Health check endpoints

## 💻 System Requirements

### Development Environment
- **Java**: 21 (OpenJDK or Oracle JDK)
- **Maven**: 3.8+ (for building Spring Boot services)
- **Node.js**: 18+ (for Angular development)
- **npm**: 9+ (for package management)
- **Docker**: 20.10+ (for containerized services)
- **Docker Compose**: 2.0+ (for orchestration)

### Production Environment
- **Java**: 21 (OpenJDK or Oracle JDK)
- **Docker**: 20.10+ (for containerized deployment)
- **Docker Compose**: 2.0+ (for orchestration)
- **Memory**: Minimum 8GB RAM (16GB recommended)
- **Storage**: Minimum 50GB available space
- **Network**: Stable internet connection for Docker image pulls

### Technology Stack Versions

#### Backend Services (Spring Boot Microservices)
- **Spring Boot**: 3.2.5
- **Spring Cloud**: 2023.0.1
- **Java**: 21
- **Maven**: Latest stable version

#### Frontend (Angular Module Federation)
- **Angular**: 20.x
- **Node.js**: 18+
- **TypeScript**: Latest stable version
- **Module Federation**: @angular-architects/module-federation
- **ngx-build-plus**: 20.0.0

#### Infrastructure
- **Docker**: Latest stable version
- **Docker Compose**: Latest stable version
- **PostgreSQL**: 15
- **Keycloak**: 24.0.2

## 📚 Documentation

### Setup Guides
- [Local Setup Guide](docs/setup-guide/Local-Setup.md) - Non-Docker development setup
- [Docker Setup Guide](docs/setup-guide/DockerSetup.md) - Docker deployment instructions

### Frontend Documentation
- [Frontend Architecture](docs/frontend/architecture.md) - Module Federation setup
- [Shell App Guide](docs/frontend/shell-app.md) - Host application documentation
- [Hub App Guide](docs/frontend/hub-app.md) - Admin dashboard documentation
- [Console App Guide](docs/frontend/console-app.md) - Tenant admin dashboard documentation
- [Workspace App Guide](docs/frontend/workspace-app.md) - User workspace documentation
- [Module Federation Setup](docs/frontend/module-federation.md) - Federation configuration

### Service Documentation
- [Eureka Server](docs/services/eureka-server.md) - Service discovery documentation
- [API Gateway](docs/services/api-gateway.md) - Gateway service documentation
- [Authentication Service](docs/services/authentication-service.md) - Authentication service documentation
- [User Management Service](docs/services/user-management-service.md) - User management documentation
- [Permission Registry Service](docs/services/permission-registry-service.md) - Permission management documentation
- [Application Config Service](docs/services/application-config-service.md) - Configuration service documentation
- [Platform Management Service](docs/services/platform-management-service.md) - Platform management documentation
- [Tenant Management Service](docs/services/tenant-management-service.md) - Tenant management documentation

### Deployment Guides
- [Local Deployment](docs/deployment-guide/DEPLOYMENT-LOCAL.md) - Local development setup
- [Development Deployment](docs/deployment-guide/DEPLOYMENT-DEV.md) - Development environment
- [Kubernetes Deployment](docs/deployment-guide/KUBERNETES_DEPLOYMENT.md) - Kubernetes deployment

### Testing Guides
- [Testing Guide](docs/testing-guide/TESTING_GUIDE.md) - Comprehensive testing instructions

## 🚀 Quick Start

### Option 1: Docker Setup (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd onified

# Start all services with Docker
docker-compose up --build
```

### Option 2: Local Setup
```bash
# Follow the Local Setup Guide
# See: docs/setup-guide/Local-Setup.md
```

### Option 3: Frontend Development
```bash
# Install dependencies for all frontend apps
cd onified-web && npm install
cd ../hub && npm install
cd ../console && npm install
cd ../workspace && npm install

# Start shell app (host)
cd ../onified-web && npm start

# Start remote apps (in separate terminals)
cd ../hub && npm start
cd ../console && npm start
cd ../workspace && npm start
```

## 🔗 Service URLs

Once running, access the services at:

### Frontend Applications
- **Shell Application**: http://localhost:4200 (Main entry point)
- **Hub Application**: http://localhost:4300 (Admin dashboard)
- **Console Application**: http://localhost:4400 (Tenant admin dashboard)
- **Workspace Application**: http://localhost:4500 (User workspace)

### Backend Services
- **API Gateway**: http://localhost:9080
- **Eureka Server**: http://localhost:8761
- **Keycloak Admin**: http://localhost:9090
- **Authentication Service**: http://localhost:9083
- **Application Config Service**: http://localhost:9082
- **Permission Registry Service**: http://localhost:9084
- **User Management Service**: http://localhost:9085
- **Platform Management Service**: http://localhost:9081
- **Tenant Management Service**: http://localhost:9086

## 🎨 Frontend Features

### User Experience
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile
- **Modern UI**: Clean, intuitive interface with consistent design language
- **Fast Loading**: Optimized bundle sizes and lazy loading
- **Accessibility**: WCAG compliant components and navigation

### Development Experience
- **Hot Reload**: Instant feedback during development
- **Type Safety**: Full TypeScript support across all applications
- **Component Library**: Reusable components with consistent styling
- **Theme System**: Easy customization of colors and styling

### Architecture Benefits
- **Independent Development**: Teams can work on different apps simultaneously
- **Technology Flexibility**: Each app can use different Angular versions or libraries
- **Scalability**: Easy to add new remote applications
- **Maintainability**: Clear separation of concerns and responsibilities

## 🤝 Contributing

1. Follow the service build order
2. Update documentation for any changes
3. Test thoroughly before committing
4. Use conventional commit messages
5. Ensure all frontend apps build successfully
6. Test Module Federation integration

## 📄 License

This project is proprietary to Onified. 