# Dev Deployment Guide (AWS EC2)

## Prerequisites
- AWS EC2 instance (Linux, Docker & Docker Compose installed)
- Access to your Docker registry (Docker Hub, ECR, etc.)
- Java 21 and Maven (for building JARs)

## Steps
1. **Build and push Docker images:**
   ```sh
   cd <service-directory>
   mvn clean package -DskipTests
   docker build -t <your-dockerhub-username>/<service-name>:latest .
   docker push <your-dockerhub-username>/<service-name>:latest
   # Repeat for each service
   ```
2. **Update `docker-compose.yml` on EC2:**
   - Set image names to `<your-dockerhub-username>/<service-name>:latest` for each service.
   - Set environment variables as needed (DB host, credentials, etc.).
3. **Start all services and PostgreSQL:**
   ```sh
   docker-compose up -d
   ```
4. **Access services:**
   - **Gateway (Main Entry Point):** http://<ec2-gateway-host>:9080
   - **Eureka Dashboard:** http://<ec2-eureka-host>:8761
   - **Direct Service Access:**
     - authentication-service: http://<ec2-auth-host>:9081
     - application-config-service: http://<ec2-app-config-host>:9082
     - user-management-service: http://<ec2-ums-host>:9083
     - permission-registry-service: http://<ec2-permission-host>:9084
   - **PostgreSQL:** <ec2-db-host>:5432

## Gateway Routes
- `http://<ec2-gateway-host>:9080/auth/**` → Authentication Service
- `http://<ec2-gateway-host>:9080/appConfig/**` → Application Config Service
- `http://<ec2-gateway-host>:9080/ums/**` → User Management Service
- `http://<ec2-gateway-host>:9080/permissions/**` → Permission Registry Service

## Troubleshooting
- Ensure security groups allow inbound traffic on required ports (9080-9084, 8761, 5432).
- Check logs with `docker-compose logs -f`.
- Rebuild/re-pull images if you push new versions. 