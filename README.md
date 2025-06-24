# Onified Microservices Platform

A comprehensive microservices platform built with Spring Boot, Angular, and Keycloak for identity management.

## 🏗️ Architecture Overview

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

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 21 (for local development)
- Node.js 18+ (for Angular development)

### 1. Environment Setup

All environment variables are managed via per-environment JSON config files in the `configs/` directory. Use the provided scripts to generate a `.env` file at the project root.

- `configs/config.local.json` (local development)
- `configs/config.dev.json` (development)
- `configs/config.prod.json` (production)

To generate a `.env` file for your desired environment, use one of the setup scripts from the `configs/` directory:

**On Linux/macOS:**
```bash
bash configs/setup-env.sh local
```
**On Windows (PowerShell):**
```powershell
cd configs
./setup-env.ps1 local
```

**How it works:**
- The generated `.env` file is automatically used by Docker Compose for all services.
- All Spring Boot services are configured (in their `application.yml`) to import environment variables from `.env` at startup.
- Dockerfiles do not need to reference `.env` directly; configuration is injected at runtime by Docker Compose.

> **Note:** No manual changes are needed in Dockerfiles or `application.yml` as long as `.env` is generated at the project root. The `configs/` directory is gitignored and should not be committed to version control.

### 2. Start All Services
```bash
# Build and start all services
docker-compose up --build

# Or start in background
docker-compose up -d --build
```

### 3. Access Services
- **Angular Web App**: http://localhost:4200
- **API Gateway**: http://localhost:9080
- **Eureka Server**: http://localhost:8761
- **Keycloak Admin**: http://localhost:8080 (admin/admin)
- **Authentication Service**: http://localhost:9083
- **Application Config Service**: http://localhost:9082
- **Permission Registry Service**: http://localhost:9084
- **User Management Service**: http://localhost:9085

## 📋 Service Build Order

Services should be built in the following order:

1. **Eureka Server** - Service discovery
2. **Application Config Service** - Configuration management
3. **Authentication Service** - Identity and authentication
4. **Permission Registry Service** - Permissions and roles
5. **User Management Service** - User operations
6. **API Gateway** - Routing and security
7. **Angular Web App** - Frontend application

## 🔧 Development

### Local Development
```bash
# Start only infrastructure services
docker-compose up postgres keycloak eureka-server

# Run individual services locally
cd application-config-service && mvn spring-boot:run
cd authentication-service && mvn spring-boot:run
# ... etc
```

### Service Updates
```bash
# Rebuild and restart a specific service
docker-compose build service-name
docker-compose up -d service-name

# Rebuild and restart all services
docker-compose up --build -d
```

### Logs
```bash
# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f service-name

# View logs from local log directories
tail -f logs/service-name/application.log
```

## 🔐 Authentication & Security

### Keycloak Setup
1. Access Keycloak Admin Console: http://localhost:8080
2. Login with admin/admin
3. Create realm: `onified`
4. Create clients:
   - `authentication-service` (confidential)
   - `web-app` (public)
5. Configure redirect URIs and CORS

### OAuth2/OIDC Flow
1. User accesses Angular app
2. Redirected to Keycloak login
3. After authentication, redirected back with code
4. Angular exchanges code for tokens
5. API calls include Bearer token
6. Gateway validates tokens with Keycloak

## 📊 Monitoring & Health

### Health Checks
- **Eureka**: http://localhost:8761
- **Gateway**: http://localhost:9080/actuator/health
- **Services**: http://localhost:908X/actuator/health

### Metrics
- **Gateway**: http://localhost:9080/actuator/metrics
- **Services**: http://localhost:908X/actuator/metrics

## 🗄️ Database

### PostgreSQL
- **Host**: localhost:5432
- **Database**: onified
- **Username**: onified_user
- **Password**: Set in .env file

### Schema Management
Each service manages its own database schema using JPA/Hibernate.

## 📁 Project Structure

```
repository/
├── eureka-server/           # Service discovery
├── application-config-service/  # Configuration management
├── authentication-service/      # Identity & auth
├── permission-registry-service/ # Permissions & roles
├── user-management-service/     # User operations
├── onified-gateway/            # API Gateway
├── web/                        # Angular frontend
├── docker-compose.yml          # Container orchestration
├── env.example                 # Environment template
├── docs/                       # All documentation
│   ├── eureka-server.md        # Service 1 documentation
│   ├── application-config-service.md # Service 2 documentation
│   ├── authentication-service.md     # Service 3 documentation
│   ├── permission-registry-service.md # Service 4 documentation
│   ├── user-management-service.md    # Service 5 documentation
│   ├── onified-gateway.md           # Service 6 documentation
│   ├── angular-web-app.md           # Service 7 documentation
│   ├── shared-templates.md          # Common templates
│   ├── setup-guide/                 # Setup instructions
│   ├── deployment-guide/            # Deployment guides
│   └── testing-guide/               # Testing guides
└── logs/                       # Application logs
```

## 🛠️ Troubleshooting

### Common Issues

1. **Port Conflicts**
   ```bash
   # Check what's using a port
   netstat -ano | findstr :9080
   ```

2. **Database Connection Issues**
   ```bash
   # Check PostgreSQL logs
   docker-compose logs postgres
   ```

3. **Keycloak Connection Issues**
   ```bash
   # Check Keycloak logs
   docker-compose logs keycloak
   ```

4. **Service Discovery Issues**
   ```bash
   # Check Eureka dashboard
   # http://localhost:8761
   ```

### Log Locations
- **Docker logs**: `docker-compose logs service-name`
- **Local logs**: `logs/service-name/application.log`

## 📚 Documentation Structure

### Service Documentation (docs/)
- [Eureka Server](docs/eureka-server.md) - Service discovery
- [Application Config Service](docs/application-config-service.md) - Configuration management
- [Authentication Service](docs/authentication-service.md) - Identity & authentication
- [Permission Registry Service](docs/permission-registry-service.md) - Permissions & roles
- [User Management Service](docs/user-management-service.md) - User operations
- [API Gateway](docs/onified-gateway.md) - Routing & security
- [Angular Web App](docs/angular-web-app.md) - Frontend application
- [Shared Templates](docs/shared-templates.md) - Common documentation templates

### Setup Guides (docs/setup-guide/)
- [Keycloak Setup](docs/setup-guide/KEYCLOAK_SETUP.md) - Identity provider setup
- [Keycloak Client Configuration](docs/setup-guide/KEYCLOAK_CLIENT_CONFIG.md) - Client setup
- [Angular Setup](docs/setup-guide/ANGULAR_SETUP.md) - Frontend setup

### Deployment Guides (docs/deployment-guide/)
- [Local Deployment](docs/deployment-guide/DEPLOYMENT-LOCAL.md) - Local development setup
- [Development Deployment](docs/deployment-guide/DEPLOYMENT-DEV.md) - Development environment

### Testing Guides (docs/testing-guide/)
- [Testing Guide](docs/testing-guide/TESTING_GUIDE.md) - Comprehensive testing instructions

## 🤝 Contributing

1. Follow the service build order
2. Update documentation for any changes
3. Test thoroughly before committing
4. Use conventional commit messages

## 📄 License

This project is proprietary to Onified.
