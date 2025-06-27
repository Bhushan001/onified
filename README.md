# Onified Microservices Platform

A comprehensive microservices platform built with Spring Boot, Angular, and Keycloak for identity management.

## 🏗️ System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular Web   │    │  API Gateway    │    │   Eureka Server │
│   Application   │◄──►│  (Spring Cloud) │◄──►│  (Discovery)    │
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

### Core Services

#### 1. **Eureka Server** (Port: 8761)
- **Purpose**: Service discovery and registration
- **Technology**: Spring Cloud Netflix Eureka Server
- **Configuration**: Service registry for all microservices
- **Health Check**: http://localhost:8761

#### 2. **API Gateway** (Port: 9080)
- **Purpose**: Centralized routing, security, and load balancing
- **Technology**: Spring Cloud Gateway
- **Configuration**: Route definitions, security filters, circuit breakers
- **Features**: CORS handling, authentication, rate limiting

#### 3. **Authentication Service** (Port: 9083)
- **Purpose**: User authentication and authorization
- **Technology**: Spring Boot + Keycloak OIDC
- **Configuration**: JWT token validation, OAuth2 integration
- **Features**: Login/logout, token refresh, user session management

#### 4. **User Management Service** (Port: 9085)
- **Purpose**: User CRUD operations and profile management
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: Database connection, validation rules
- **Features**: User creation, updates, deletion, profile management

#### 5. **Permission Registry Service** (Port: 9084)
- **Purpose**: Role-based access control (RBAC) and permissions
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: Permission definitions, role mappings
- **Features**: Permission management, role assignment, access control

#### 6. **Application Config Service** (Port: 9082)
- **Purpose**: Application configuration management
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: App settings, feature flags, configuration storage
- **Features**: Dynamic configuration, version control, environment-specific settings

#### 7. **Platform Management Service** (Port: 9081)
- **Purpose**: Platform-wide management and administration
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: Platform settings, tenant management
- **Features**: Platform configuration, tenant isolation, system administration

#### 8. **Tenant Management Service** (Port: 9086)
- **Purpose**: Multi-tenant architecture management
- **Technology**: Spring Boot + JPA/Hibernate
- **Configuration**: Tenant isolation, data partitioning
- **Features**: Tenant creation, isolation, data management

### Infrastructure Services

#### 9. **PostgreSQL Database** (Port: 5432)
- **Purpose**: Primary data storage for all services
- **Technology**: PostgreSQL 15
- **Configuration**: Database schemas, connection pooling
- **Features**: ACID compliance, JSONB support, partitioning

#### 10. **Keycloak** (Port: 9090)
- **Purpose**: Identity and access management
- **Technology**: Keycloak 24.0.2
- **Configuration**: Realms, clients, users, roles
- **Features**: OAuth2/OIDC, SSO, user federation

#### 11. **Angular Web Application** (Port: 4200)
- **Purpose**: Frontend user interface
- **Technology**: Angular 17.x
- **Configuration**: API endpoints, authentication flow
- **Features**: Responsive design, real-time updates, PWA support

## ⚙️ Basic Configuration Needs

### Environment Variables
Each service requires specific environment variables for:
- Database connections
- Service discovery URLs
- Authentication settings
- Logging configuration
- Port assignments

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

#### Frontend
- **Angular**: 17.x
- **Node.js**: 18+
- **TypeScript**: Latest stable version

#### Infrastructure
- **Docker**: Latest stable version
- **Docker Compose**: Latest stable version
- **PostgreSQL**: 15
- **Keycloak**: 24.0.2

## 📚 Documentation

### Setup Guides
- [Local Setup Guide](docs/setup-guide/Local-Setup.md) - Non-Docker development setup
- [Docker Setup Guide](docs/setup-guide/DockerSetup.md) - Docker deployment instructions

### Service Documentation
- [Eureka Server](docs/services/eureka-server.md) - Service discovery documentation
- [API Gateway](docs/services/api-gateway.md) - Gateway service documentation
- [Authentication Service](docs/services/authentication-service.md) - Authentication service documentation
- [User Management Service](docs/services/user-management-service.md) - User management documentation
- [Permission Registry Service](docs/services/permission-registry-service.md) - Permission management documentation
- [Application Config Service](docs/services/application-config-service.md) - Configuration service documentation
- [Platform Management Service](docs/services/platform-management-service.md) - Platform management documentation
- [Tenant Management Service](docs/services/tenant-management-service.md) - Tenant management documentation
- [Angular Web Application](docs/services/angular-web-app.md) - Frontend application documentation

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

## 🔗 Service URLs

Once running, access the services at:
- **Angular Web App**: http://localhost:4200
- **API Gateway**: http://localhost:9080
- **Eureka Server**: http://localhost:8761
- **Keycloak Admin**: http://localhost:9090
- **Authentication Service**: http://localhost:9083
- **Application Config Service**: http://localhost:9082
- **Permission Registry Service**: http://localhost:9084
- **User Management Service**: http://localhost:9085
- **Platform Management Service**: http://localhost:9081
- **Tenant Management Service**: http://localhost:9086

## 🤝 Contributing

1. Follow the service build order
2. Update documentation for any changes
3. Test thoroughly before committing
4. Use conventional commit messages

## 📄 License

This project is proprietary to Onified. 