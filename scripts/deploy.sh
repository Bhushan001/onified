#!/bin/bash

set -e

# Configuration
NAMESPACE="onified"
TIMEOUT=300

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting Kubernetes deployment for Onified Platform...${NC}"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}Error: kubectl is not installed or not in PATH${NC}"
    exit 1
fi

# Check if we can connect to cluster
if ! kubectl cluster-info &> /dev/null; then
    echo -e "${RED}Error: Cannot connect to Kubernetes cluster${NC}"
    exit 1
fi

echo -e "${BLUE}Current context: $(kubectl config current-context)${NC}"

# Create namespace
echo -e "${YELLOW}Creating namespace...${NC}"
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Set namespace as default
kubectl config set-context --current --namespace=$NAMESPACE

echo -e "${GREEN}Namespace '$NAMESPACE' created/verified${NC}"

# Function to wait for pods to be ready
wait_for_pods() {
    local label=$1
    local service_name=$2
    echo -e "${YELLOW}Waiting for $service_name pods to be ready...${NC}"
    kubectl wait --for=condition=ready pod -l $label -n $NAMESPACE --timeout=${TIMEOUT}s
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}$service_name is ready!${NC}"
    else
        echo -e "${RED}Timeout waiting for $service_name to be ready${NC}"
        exit 1
    fi
}

# Apply secrets and configmaps first
echo -e "${YELLOW}Applying secrets and configmaps...${NC}"
kubectl apply -f k8s/postgres/postgres-configmap.yaml
kubectl apply -f k8s/postgres/postgres-secret.yaml
kubectl apply -f k8s/postgres/postgres-init-configmap.yaml

# Deploy PostgreSQL
echo -e "${YELLOW}Deploying PostgreSQL...${NC}"
kubectl apply -f k8s/postgres/postgres-statefulset.yaml
kubectl apply -f k8s/postgres/postgres-service.yaml
wait_for_pods "app=postgres" "PostgreSQL"

# Deploy Keycloak
echo -e "${YELLOW}Deploying Keycloak...${NC}"
kubectl apply -f k8s/keycloak/keycloak-deployment.yaml
kubectl apply -f k8s/keycloak/keycloak-service.yaml
wait_for_pods "app=keycloak" "Keycloak"

# Deploy API Gateway (using Kubernetes service discovery)
echo -e "${YELLOW}Deploying API Gateway...${NC}"
kubectl apply -f k8s/gateway/gateway-deployment.yaml
wait_for_pods "app=onified-gateway" "API Gateway"

# Deploy microservices
echo -e "${YELLOW}Deploying microservices...${NC}"

# Platform Management Service
echo -e "${BLUE}Deploying Platform Management Service...${NC}"
kubectl apply -f k8s/platform-management/platform-management-deployment.yaml
wait_for_pods "app=platform-management-service" "Platform Management Service"

# User Management Service
if [ -f "k8s/user-management/user-management-deployment.yaml" ]; then
    echo -e "${BLUE}Deploying User Management Service...${NC}"
    kubectl apply -f k8s/user-management/user-management-deployment.yaml
    wait_for_pods "app=user-management-service" "User Management Service"
fi

# Tenant Management Service
if [ -f "k8s/tenant-management/tenant-management-deployment.yaml" ]; then
    echo -e "${BLUE}Deploying Tenant Management Service...${NC}"
    kubectl apply -f k8s/tenant-management/tenant-management-deployment.yaml
    wait_for_pods "app=tenant-management-service" "Tenant Management Service"
fi

# Permission Registry Service
if [ -f "k8s/permission-registry/permission-registry-deployment.yaml" ]; then
    echo -e "${BLUE}Deploying Permission Registry Service...${NC}"
    kubectl apply -f k8s/permission-registry/permission-registry-deployment.yaml
    wait_for_pods "app=permission-registry-service" "Permission Registry Service"
fi

# Application Config Service
if [ -f "k8s/application-config/application-config-deployment.yaml" ]; then
    echo -e "${BLUE}Deploying Application Config Service...${NC}"
    kubectl apply -f k8s/application-config/application-config-deployment.yaml
    wait_for_pods "app=application-config-service" "Application Config Service"
fi

# Authentication Service
if [ -f "k8s/authentication/authentication-deployment.yaml" ]; then
    echo -e "${BLUE}Deploying Authentication Service...${NC}"
    kubectl apply -f k8s/authentication/authentication-deployment.yaml
    wait_for_pods "app=authentication-service" "Authentication Service"
fi

# Deploy frontend
echo -e "${YELLOW}Deploying Frontend...${NC}"
kubectl apply -f k8s/frontend/frontend-deployment.yaml
wait_for_pods "app=onified-frontend" "Frontend"

# Apply services
echo -e "${YELLOW}Applying services...${NC}"
kubectl apply -f k8s/services/services.yaml

# Apply ingress (optional - requires ingress controller)
echo -e "${YELLOW}Applying ingress rules...${NC}"
if [ -f "k8s/ingress/ingress-rules.yaml" ]; then
    kubectl apply -f k8s/ingress/ingress-rules.yaml
    echo -e "${GREEN}Ingress rules applied${NC}"
else
    echo -e "${YELLOW}Ingress rules file not found, skipping...${NC}"
fi

# Show deployment status
echo -e "${GREEN}Deployment completed successfully!${NC}"
echo -e "${BLUE}Checking deployment status...${NC}"

kubectl get pods -n $NAMESPACE
kubectl get services -n $NAMESPACE
kubectl get ingress -n $NAMESPACE 2>/dev/null || echo -e "${YELLOW}No ingress found${NC}"

echo -e "${GREEN}Onified Platform is now deployed on Kubernetes!${NC}"
echo -e "${YELLOW}To access the application:${NC}"
echo -e "${BLUE}1. Update your /etc/hosts file to point your domain to the cluster IP${NC}"
echo -e "${BLUE}2. Or use port-forwarding: kubectl port-forward svc/onified-frontend 8080:80 -n $NAMESPACE${NC}"
echo -e "${BLUE}3. Access the application at: http://localhost:8080${NC}" 