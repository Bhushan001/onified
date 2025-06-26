# Onified Platform - Kubernetes Deployment

This document provides a quick start guide for deploying the Onified Platform on Kubernetes using native service discovery.

## Quick Start

### Prerequisites

1. **Kubernetes Cluster** (v1.24+)
   - Minikube, Docker Desktop, Kind, or cloud provider (EKS, GKE, AKS)
   - At least 4 vCPUs, 8GB RAM, 50GB storage

2. **Required Tools**
   ```bash
   # Install kubectl
   curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
   chmod +x kubectl
   sudo mv kubectl /usr/local/bin/

   # Install Helm (optional)
   curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
   ```

3. **Docker Registry**
   - Set up a container registry (Docker Hub, AWS ECR, Google GCR, etc.)
   - Update `scripts/build-and-push.sh` with your registry URL

### 1. Build and Push Images

```bash
# Update registry URL in the script
vim scripts/build-and-push.sh

# Build and push all images
./scripts/build-and-push.sh
```

### 2. Deploy to Kubernetes

```bash
# Deploy all components
./scripts/deploy.sh
```

### 3. Verify Deployment

```bash
# Check deployment status
./scripts/health-check.sh

# Or manually check
kubectl get pods -n onified
kubectl get services -n onified
```

### 4. Access the Application

#### Option 1: Port Forwarding (Quick Test)
```bash
# Frontend
kubectl port-forward svc/onified-frontend 8080:80 -n onified

# API Gateway
kubectl port-forward svc/onified-gateway 9080:9080 -n onified

# Keycloak
kubectl port-forward svc/keycloak 9090:8080 -n onified
```

#### Option 2: Ingress (Production)
1. Install Nginx Ingress Controller:
   ```bash
   helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
   helm install ingress-nginx ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace
   ```

2. Update `/etc/hosts`:
   ```
   127.0.0.1 onified.local
   ```

3. Access the application:
   - Frontend: http://onified.local
   - API: http://onified.local/api
   - Keycloak: http://onified.local/auth

## Architecture

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

### Service Discovery

The platform uses **Kubernetes Native Service Discovery**:

- **No Eureka**: Eliminates the need for a separate service discovery component
- **Kubernetes Services**: Each microservice is exposed as a Kubernetes Service
- **DNS Resolution**: Services communicate using service names (e.g., `platform-management-service`)
- **Built-in Load Balancing**: Kubernetes provides automatic load balancing across replicas
- **Health Monitoring**: Kubernetes probes ensure service availability

## Configuration

### Environment Variables

Key configuration files:
- `k8s/values.yaml` - Helm values (if using Helm)
- `k8s/postgres/postgres-configmap.yaml` - Database configuration
- `k8s/postgres/postgres-secret.yaml` - Database credentials

### Service Discovery Configuration

Services are configured to use Kubernetes native service discovery:

```yaml
# In deployment files
env:
- name: EUREKA_CLIENT_ENABLED
  value: "false"
- name: SPRING_CLOUD_DISCOVERY_ENABLED
  value: "false"
```

### Customization

1. **Update Image Registry**:
   ```bash
   # In scripts/build-and-push.sh
   REGISTRY="your-registry.com"
   ```

2. **Modify Resource Limits**:
   ```yaml
   # In deployment files
   resources:
     requests:
       memory: "512Mi"
       cpu: "500m"
     limits:
       memory: "1Gi"
       cpu: "1000m"
   ```

3. **Scale Services**:
   ```bash
   kubectl scale deployment platform-management-service --replicas=3 -n onified
   ```

## Monitoring and Troubleshooting

### Health Checks

```bash
# Comprehensive health check
./scripts/health-check.sh

# Check specific service
kubectl logs -f deployment/platform-management-service -n onified
```

### Common Issues

1. **Pods Not Starting**:
   ```bash
   kubectl describe pod <pod-name> -n onified
   kubectl logs <pod-name> -n onified
   ```

2. **Service Discovery Issues**:
   ```bash
   # Test service connectivity using Kubernetes DNS
   kubectl exec -it <pod-name> -n onified -- nslookup platform-management-service
   
   # Test service health
   kubectl exec -it <pod-name> -n onified -- curl http://platform-management-service:9081/actuator/health
   ```

3. **Database Connection Issues**:
   ```bash
   kubectl logs -l app=postgres -n onified
   kubectl exec -it <pod-name> -n onified -- psql -h postgres -U postgres -d postgres
   ```

4. **Ingress Issues**:
   ```bash
   kubectl get pods -n ingress-nginx
   kubectl describe ingress onified-ingress -n onified
   kubectl logs -l app.kubernetes.io/name=ingress-nginx -n ingress-nginx
   ```

### Performance Monitoring

```bash
# Resource usage
kubectl top pods -n onified

# Events
kubectl get events -n onified --sort-by='.lastTimestamp'
```

## Security Considerations

### 1. Secrets Management
```bash
# Create secrets for production
kubectl create secret generic app-secrets \
  --from-literal=db-password=secure-password \
  --from-literal=keycloak-admin-password=secure-password \
  -n onified
```

### 2. Network Policies
```yaml
# Enable network policies in k8s/values.yaml
security:
  networkPolicies:
    enabled: true
```

### 3. RBAC
```bash
# Apply RBAC configuration
kubectl apply -f k8s/rbac/rbac.yaml
```

## Production Deployment

### 1. High Availability
- Deploy multiple replicas of each service
- Use anti-affinity rules to spread pods across nodes
- Implement proper health checks and readiness probes

### 2. Backup and Recovery
- Set up regular database backups
- Use persistent volumes for data storage
- Implement disaster recovery procedures

### 3. Monitoring Stack
```bash
# Install Prometheus and Grafana
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring --create-namespace
```

### 4. SSL/TLS
```bash
# Install cert-manager for automatic SSL certificates
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.12.0/cert-manager.yaml
```

## Benefits of Kubernetes Service Discovery

1. **Simplified Architecture**: No additional service discovery components needed
2. **Better Performance**: Native Kubernetes DNS resolution
3. **Automatic Load Balancing**: Built-in load balancing across replicas
4. **Health Monitoring**: Kubernetes probes and health checks
5. **Scalability**: Easy horizontal scaling with service discovery
6. **Reduced Complexity**: Fewer components to manage and maintain

## Cleanup

```bash
# Remove all resources
kubectl delete namespace onified

# Remove persistent volumes (if needed)
kubectl delete pv --all
```

## Support

For detailed documentation, see:
- [Kubernetes Deployment Guide](docs/deployment-guide/KUBERNETES_DEPLOYMENT.md)
- [Service Documentation](docs/)
- [Troubleshooting Guide](docs/deployment-guide/KUBERNETES_DEPLOYMENT.md#troubleshooting)

## Next Steps

1. **Customize Configuration**: Update manifests for your environment
2. **Set Up CI/CD**: Implement automated deployment pipelines
3. **Configure Monitoring**: Set up comprehensive monitoring and alerting
4. **Security Hardening**: Implement additional security measures
5. **Performance Tuning**: Optimize resource allocation and scaling policies 