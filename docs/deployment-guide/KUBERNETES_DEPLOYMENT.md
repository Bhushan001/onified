# Kubernetes Deployment Guide for Onified Platform

This guide provides comprehensive instructions for deploying the Onified microservices platform on Kubernetes using native service discovery.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Architecture Overview](#architecture-overview)
3. [Namespace Setup](#namespace-setup)
4. [Database Setup](#database-setup)
5. [Keycloak Setup](#keycloak-setup)
6. [Microservices Deployment](#microservices-deployment)
7. [Frontend Deployment](#frontend-deployment)
8. [Ingress Configuration](#ingress-configuration)
9. [Monitoring and Logging](#monitoring-and-logging)
10. [Security Considerations](#security-considerations)
11. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Tools
- `kubectl` (v1.24+)
- `helm` (v3.12+)
- `docker` (for building images)
- `kubectx` and `kubens` (optional, for easier context switching)

### Kubernetes Cluster Requirements
- **Minimum**: 4 vCPUs, 8GB RAM, 50GB storage
- **Recommended**: 8 vCPUs, 16GB RAM, 100GB storage
- **Storage**: Persistent volume support
- **Load Balancer**: Ingress controller (nginx, traefik, etc.)

### Cluster Setup Options
1. **Local Development**:
   - Minikube
   - Docker Desktop Kubernetes
   - Kind (Kubernetes in Docker)

2. **Cloud Providers**:
   - AWS EKS
   - Google GKE
   - Azure AKS
   - DigitalOcean Kubernetes

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Ingress       │    │   Load Balancer │    │   External      │
│   Controller    │    │                 │    │   Access        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular       │    │   API Gateway   │    │   Keycloak      │
│   Frontend      │    │   (Spring       │    │   (Auth)        │
│                 │    │    Cloud)       │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Microservices Layer                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │  Platform   │ │   User      │ │  Tenant     │ │ Permission  │ │
│  │ Management  │ │ Management  │ │ Management  │ │ Registry    │ │
│  │             │ │             │ │             │ │             │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │ Application │ │Authentication│ │             │ │             │ │
│  │   Config    │ │             │ │             │ │             │ │
│  │             │ │             │ │             │ │             │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Data Layer                                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │ PostgreSQL  │ │ PostgreSQL  │ │ PostgreSQL  │ │ PostgreSQL  │ │
│  │ (Platform)  │ │ (User Mgmt) │ │ (Tenant)    │ │ (Auth)      │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### Service Discovery Architecture

The platform uses **Kubernetes Native Service Discovery** instead of Eureka:

- **Kubernetes Services**: Each microservice is exposed as a Kubernetes Service
- **DNS Resolution**: Services can communicate using service names (e.g., `platform-management-service`)
- **Load Balancing**: Kubernetes provides built-in load balancing across service replicas
- **Health Checks**: Kubernetes probes ensure service availability

## Namespace Setup

Create a dedicated namespace for the Onified platform:

```bash
kubectl create namespace onified
kubectl config set-context --current --namespace=onified
```

## Database Setup

### PostgreSQL Deployment

The platform uses multiple PostgreSQL databases for different services. We'll deploy them using StatefulSets for data persistence.

#### 1. Create PostgreSQL ConfigMap

```yaml
# k8s/postgres/postgres-configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-config
  namespace: onified
data:
  POSTGRES_DB: postgres
  POSTGRES_USER: postgres
  POSTGRES_PASSWORD: root
  PGDATA: /var/lib/postgresql/data/pgdata
```

#### 2. Create PostgreSQL Secret

```yaml
# k8s/postgres/postgres-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: postgres-secret
  namespace: onified
type: Opaque
data:
  postgres-password: cm9vdA==  # "root" in base64
  postgres-user: cG9zdGdyZXM=  # "postgres" in base64
```

#### 3. Create PostgreSQL StatefulSet

```yaml
# k8s/postgres/postgres-statefulset.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: onified
spec:
  serviceName: postgres
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          valueFrom:
            configMapKeyRef:
              name: postgres-config
              key: POSTGRES_DB
        - name: POSTGRES_USER
          valueFrom:
            configMapKeyRef:
              name: postgres-config
              key: POSTGRES_USER
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: postgres-password
        - name: PGDATA
          valueFrom:
            configMapKeyRef:
              name: postgres-config
              key: PGDATA
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
        - name: init-script
          mountPath: /docker-entrypoint-initdb.d
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - postgres
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - postgres
          initialDelaySeconds: 5
          periodSeconds: 5
      volumes:
      - name: init-script
        configMap:
          name: postgres-init-script
  volumeClaimTemplates:
  - metadata:
      name: postgres-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
```

#### 4. Create PostgreSQL Service

```yaml
# k8s/postgres/postgres-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: onified
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
  clusterIP: None
```

#### 5. Create Database Initialization Script

```yaml
# k8s/postgres/postgres-init-configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-init-script
  namespace: onified
data:
  init.sql: |
    -- Create databases for each service
    CREATE DATABASE platform_mgmt_db;
    CREATE DATABASE tenant_management_db;
    CREATE DATABASE user_management_db;
    CREATE DATABASE permission_registry_db;
    CREATE DATABASE application_config_db;
    CREATE DATABASE authentication_db;
    CREATE DATABASE keycloak_db;
    
    -- Grant permissions
    GRANT ALL PRIVILEGES ON DATABASE platform_mgmt_db TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE tenant_management_db TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE user_management_db TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE permission_registry_db TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE application_config_db TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE authentication_db TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO postgres;
```

## Keycloak Setup

### Keycloak Deployment

```yaml
# k8s/keycloak/keycloak-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: keycloak
  namespace: onified
spec:
  replicas: 1
  selector:
    matchLabels:
      app: keycloak
  template:
    metadata:
      labels:
        app: keycloak
    spec:
      containers:
      - name: keycloak
        image: quay.io/keycloak/keycloak:24.0.2
        ports:
        - containerPort: 8080
        env:
        - name: KC_DB
          value: "postgres"
        - name: KC_DB_URL
          value: "jdbc:postgresql://postgres:5432/keycloak_db"
        - name: KC_DB_USERNAME
          value: "postgres"
        - name: KC_DB_PASSWORD
          value: "root"
        - name: KEYCLOAK_ADMIN
          value: "admin"
        - name: KEYCLOAK_ADMIN_PASSWORD
          value: "admin123"
        - name: KC_HOSTNAME_STRICT
          value: "false"
        - name: KC_HOSTNAME_STRICT_HTTPS
          value: "false"
        - name: KC_HTTP_ENABLED
          value: "true"
        args:
        - start-dev
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### Keycloak Service

```yaml
# k8s/keycloak/keycloak-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: keycloak
  namespace: onified
spec:
  selector:
    app: keycloak
  ports:
  - port: 8080
    targetPort: 8080
    name: http
```

## Microservices Deployment

### 1. API Gateway (Kubernetes Service Discovery)

```yaml
# k8s/gateway/gateway-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: onified-gateway
  namespace: onified
spec:
  replicas: 2
  selector:
    matchLabels:
      app: onified-gateway
  template:
    metadata:
      labels:
        app: onified-gateway
    spec:
      containers:
      - name: onified-gateway
        image: onified/gateway:latest
        ports:
        - containerPort: 9080
        env:
        - name: SPRING_APPLICATION_NAME
          value: "onified-gateway"
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        # Disable Eureka - use Kubernetes service discovery
        - name: EUREKA_CLIENT_ENABLED
          value: "false"
        - name: SPRING_CLOUD_DISCOVERY_ENABLED
          value: "false"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 9080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 9080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### 2. Platform Management Service

```yaml
# k8s/platform-management/platform-management-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: platform-management-service
  namespace: onified
spec:
  replicas: 2
  selector:
    matchLabels:
      app: platform-management-service
  template:
    metadata:
      labels:
        app: platform-management-service
    spec:
      containers:
      - name: platform-management-service
        image: onified/platform-management:latest
        ports:
        - containerPort: 9081
        env:
        - name: PLATFORM_MGMT_DB_URL
          value: "jdbc:postgresql://postgres:5432/platform_mgmt_db"
        - name: PLATFORM_MGMT_DB_USERNAME
          value: "postgres"
        - name: PLATFORM_MGMT_DB_PASSWORD
          value: "root"
        - name: SPRING_APPLICATION_NAME
          value: "platform-management-service"
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        # Disable Eureka - use Kubernetes service discovery
        - name: EUREKA_CLIENT_ENABLED
          value: "false"
        - name: SPRING_CLOUD_DISCOVERY_ENABLED
          value: "false"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 9081
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 9081
          initialDelaySeconds: 30
          periodSeconds: 10
```

### 3. Other Microservices

Create similar deployment files for other services:
- User Management Service
- Tenant Management Service
- Permission Registry Service
- Application Config Service
- Authentication Service

## Frontend Deployment

### Angular Frontend

```yaml
# k8s/frontend/frontend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: onified-frontend
  namespace: onified
spec:
  replicas: 2
  selector:
    matchLabels:
      app: onified-frontend
  template:
    metadata:
      labels:
        app: onified-frontend
    spec:
      containers:
      - name: onified-frontend
        image: onified/frontend:latest
        ports:
        - containerPort: 80
        env:
        - name: NODE_ENV
          value: "production"
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
        livenessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 30
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 10
          periodSeconds: 10
```

## Services Configuration

### Create Services for All Components

```yaml
# k8s/services/services.yaml
apiVersion: v1
kind: Service
metadata:
  name: onified-gateway
  namespace: onified
spec:
  selector:
    app: onified-gateway
  ports:
  - port: 9080
    targetPort: 9080
---
apiVersion: v1
kind: Service
metadata:
  name: platform-management-service
  namespace: onified
spec:
  selector:
    app: platform-management-service
  ports:
  - port: 9081
    targetPort: 9081
---
apiVersion: v1
kind: Service
metadata:
  name: user-management-service
  namespace: onified
spec:
  selector:
    app: user-management-service
  ports:
  - port: 9085
    targetPort: 9085
---
apiVersion: v1
kind: Service
metadata:
  name: tenant-management-service
  namespace: onified
spec:
  selector:
    app: tenant-management-service
  ports:
  - port: 9086
    targetPort: 9086
---
apiVersion: v1
kind: Service
metadata:
  name: permission-registry-service
  namespace: onified
spec:
  selector:
    app: permission-registry-service
  ports:
  - port: 9084
    targetPort: 9084
---
apiVersion: v1
kind: Service
metadata:
  name: application-config-service
  namespace: onified
spec:
  selector:
    app: application-config-service
  ports:
  - port: 9082
    targetPort: 9082
---
apiVersion: v1
kind: Service
metadata:
  name: authentication-service
  namespace: onified
spec:
  selector:
    app: authentication-service
  ports:
  - port: 9083
    targetPort: 9083
---
apiVersion: v1
kind: Service
metadata:
  name: onified-frontend
  namespace: onified
spec:
  selector:
    app: onified-frontend
  ports:
  - port: 80
    targetPort: 80
```

## Ingress Configuration

### Nginx Ingress Controller

First, install the Nginx Ingress Controller:

```bash
# For Helm
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace

# Or using kubectl
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
```

### Ingress Rules

```yaml
# k8s/ingress/ingress-rules.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: onified-ingress
  namespace: onified
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/cors-allow-origin: "*"
    nginx.ingress.kubernetes.io/cors-allow-methods: "GET, POST, PUT, DELETE, OPTIONS"
    nginx.ingress.kubernetes.io/cors-allow-headers: "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization"
spec:
  ingressClassName: nginx
  rules:
  - host: onified.local  # Replace with your domain
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: onified-frontend
            port:
              number: 80
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: onified-gateway
            port:
              number: 9080
      - path: /auth
        pathType: Prefix
        backend:
          service:
            name: keycloak
            port:
              number: 8080
```

## Service Discovery Configuration

### Kubernetes Native Service Discovery

The platform uses Kubernetes native service discovery instead of Eureka:

1. **Service Registration**: Kubernetes automatically registers services when pods start
2. **Service Discovery**: Services can communicate using service names
3. **Load Balancing**: Kubernetes provides built-in load balancing
4. **Health Checks**: Kubernetes probes ensure service availability

### Service Communication

Services communicate using Kubernetes service names:

```yaml
# Example: Gateway calling Platform Management Service
# In gateway configuration
spring:
  cloud:
    gateway:
      routes:
        - id: platform-management
          uri: http://platform-management-service:9081
          predicates:
            - Path=/api/platform/**
```

### Benefits of Kubernetes Service Discovery

1. **Simplified Architecture**: No need for additional service discovery components
2. **Better Performance**: Native Kubernetes DNS resolution
3. **Automatic Load Balancing**: Built-in load balancing across replicas
4. **Health Monitoring**: Kubernetes probes and health checks
5. **Scalability**: Easy horizontal scaling with service discovery

## Monitoring and Logging

### Prometheus and Grafana Setup

```bash
# Add Prometheus Helm repository
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Install Prometheus and Grafana
helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace \
  --set grafana.enabled=true \
  --set prometheus.enabled=true
```

### Application Monitoring

Create ServiceMonitor for microservices:

```yaml
# k8s/monitoring/servicemonitor.yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: onified-services
  namespace: monitoring
spec:
  selector:
    matchLabels:
      app: onified-gateway
  endpoints:
  - port: http
    path: /actuator/prometheus
    interval: 30s
```

## Security Considerations

### 1. Secrets Management

Use Kubernetes secrets for sensitive data:

```yaml
# k8s/secrets/app-secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
  namespace: onified
type: Opaque
data:
  # Base64 encoded values
  db-password: <base64-encoded-password>
  keycloak-admin-password: <base64-encoded-password>
  jwt-secret: <base64-encoded-jwt-secret>
```

### 2. Network Policies

```yaml
# k8s/security/network-policies.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: onified-network-policy
  namespace: onified
spec:
  podSelector:
    matchLabels:
      app: onified-gateway
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: ingress-nginx
    ports:
    - protocol: TCP
      port: 9080
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: platform-management-service
    ports:
    - protocol: TCP
      port: 9081
```

### 3. RBAC Configuration

```yaml
# k8s/rbac/rbac.yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: onified-sa
  namespace: onified
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: onified-role
  namespace: onified
rules:
- apiGroups: [""]
  resources: ["pods", "services"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: onified-rolebinding
  namespace: onified
subjects:
- kind: ServiceAccount
  name: onified-sa
  namespace: onified
roleRef:
  kind: Role
  name: onified-role
  apiGroup: rbac.authorization.k8s.io
```

## Deployment Scripts

### 1. Build and Push Images

```bash
#!/bin/bash
# scripts/build-and-push.sh

set -e

# Set your registry
REGISTRY="your-registry.com"
NAMESPACE="onified"

# Build and push all images (no Eureka)
services=("onified-gateway" "platform-management-service" "user-management-service" "tenant-management-service" "permission-registry-service" "application-config-service" "authentication-service")

for service in "${services[@]}"; do
  echo "Building $service..."
  docker build -t $REGISTRY/$NAMESPACE/$service:latest ./$service
  docker push $REGISTRY/$NAMESPACE/$service:latest
done

echo "All images built and pushed successfully!"
```

### 2. Deploy to Kubernetes

```bash
#!/bin/bash
# scripts/deploy.sh

set -e

NAMESPACE="onified"

# Create namespace
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Apply secrets and configmaps first
kubectl apply -f k8s/postgres/
kubectl apply -f k8s/keycloak/
kubectl apply -f k8s/secrets/

# Deploy database
kubectl apply -f k8s/postgres/
kubectl wait --for=condition=ready pod -l app=postgres -n $NAMESPACE --timeout=300s

# Deploy Keycloak
kubectl apply -f k8s/keycloak/
kubectl wait --for=condition=ready pod -l app=keycloak -n $NAMESPACE --timeout=300s

# Deploy API Gateway (using Kubernetes service discovery)
kubectl apply -f k8s/gateway/
kubectl wait --for=condition=ready pod -l app=onified-gateway -n $NAMESPACE --timeout=300s

# Deploy microservices
kubectl apply -f k8s/platform-management/
kubectl apply -f k8s/user-management/
kubectl apply -f k8s/tenant-management/
kubectl apply -f k8s/permission-registry/
kubectl apply -f k8s/application-config/
kubectl apply -f k8s/authentication/

# Deploy frontend
kubectl apply -f k8s/frontend/

# Apply services
kubectl apply -f k8s/services/

# Apply ingress
kubectl apply -f k8s/ingress/

echo "Deployment completed successfully!"
```

### 3. Health Check Script

```bash
#!/bin/bash
# scripts/health-check.sh

NAMESPACE="onified"

echo "Checking pod status..."
kubectl get pods -n $NAMESPACE

echo "Checking services..."
kubectl get services -n $NAMESPACE

echo "Checking ingress..."
kubectl get ingress -n $NAMESPACE

echo "Checking Kubernetes service discovery..."
for pod in $(kubectl get pods -n $NAMESPACE -o jsonpath='{.items[*].metadata.name}'); do
  echo "=== Logs for $pod ==="
  kubectl logs $pod -n $NAMESPACE --tail=10
done
```

## Troubleshooting

### Common Issues and Solutions

#### 1. Pod Startup Issues

```bash
# Check pod status
kubectl get pods -n onified

# Check pod events
kubectl describe pod <pod-name> -n onified

# Check pod logs
kubectl logs <pod-name> -n onified
```

#### 2. Service Discovery Issues

```bash
# Check if services are accessible
kubectl exec -it <pod-name> -n onified -- nslookup <service-name>

# Test service connectivity
kubectl exec -it <pod-name> -n onified -- curl http://<service-name>:<port>/actuator/health
```

#### 3. Database Connection Issues

```bash
# Check PostgreSQL logs
kubectl logs -l app=postgres -n onified

# Test database connection
kubectl exec -it <pod-name> -n onified -- psql -h postgres -U postgres -d postgres
```

#### 4. Ingress Issues

```bash
# Check ingress controller
kubectl get pods -n ingress-nginx

# Check ingress status
kubectl describe ingress onified-ingress -n onified

# Check ingress controller logs
kubectl logs -l app.kubernetes.io/name=ingress-nginx -n ingress-nginx
```

### Performance Optimization

#### 1. Resource Limits

Adjust resource requests and limits based on actual usage:

```bash
# Monitor resource usage
kubectl top pods -n onified
kubectl top nodes
```

#### 2. Horizontal Pod Autoscaling

```yaml
# k8s/hpa/hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: onified-gateway-hpa
  namespace: onified
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: onified-gateway
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

## Production Considerations

### 1. High Availability

- Deploy multiple replicas of each service
- Use anti-affinity rules to spread pods across nodes
- Implement proper health checks and readiness probes

### 2. Backup and Recovery

- Set up regular database backups
- Use persistent volumes for data storage
- Implement disaster recovery procedures

### 3. Security

- Use TLS certificates for HTTPS
- Implement proper RBAC
- Regular security updates
- Network policies for pod-to-pod communication

### 4. Monitoring

- Set up comprehensive logging (ELK stack)
- Implement application performance monitoring
- Set up alerting for critical metrics

## Next Steps

1. **Customize Configuration**: Update the manifests with your specific requirements
2. **Set Up CI/CD**: Implement automated deployment pipelines
3. **Configure Monitoring**: Set up Prometheus, Grafana, and alerting
4. **Security Hardening**: Implement additional security measures
5. **Performance Tuning**: Optimize resource allocation and scaling policies

For additional support and advanced configurations, refer to the individual service documentation in the `docs/` directory. 