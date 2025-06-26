#!/bin/bash

# Configuration
NAMESPACE="onified"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}Onified Platform Health Check${NC}"
echo "=================================="

# Check if namespace exists
if ! kubectl get namespace $NAMESPACE &> /dev/null; then
    echo -e "${RED}Error: Namespace '$NAMESPACE' does not exist${NC}"
    exit 1
fi

echo -e "${BLUE}Namespace: $NAMESPACE${NC}"
echo ""

# Check pod status
echo -e "${YELLOW}Pod Status:${NC}"
kubectl get pods -n $NAMESPACE

echo ""
echo -e "${YELLOW}Services:${NC}"
kubectl get services -n $NAMESPACE

echo ""
echo -e "${YELLOW}Ingress:${NC}"
kubectl get ingress -n $NAMESPACE 2>/dev/null || echo -e "${YELLOW}No ingress found${NC}"

echo ""
echo -e "${YELLOW}Persistent Volumes:${NC}"
kubectl get pv,pvc -n $NAMESPACE

echo ""
echo -e "${YELLOW}Recent Events:${NC}"
kubectl get events -n $NAMESPACE --sort-by='.lastTimestamp' | tail -10

echo ""
echo -e "${YELLOW}Resource Usage:${NC}"
kubectl top pods -n $NAMESPACE 2>/dev/null || echo -e "${YELLOW}Metrics server not available${NC}"

echo ""
echo -e "${YELLOW}Service Health Check:${NC}"

# Check if Gateway is accessible
GATEWAY_POD=$(kubectl get pods -n $NAMESPACE -l app=onified-gateway -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ ! -z "$GATEWAY_POD" ]; then
    echo -e "${BLUE}API Gateway:${NC}"
    kubectl exec -n $NAMESPACE $GATEWAY_POD -- curl -s http://localhost:9080/actuator/health 2>/dev/null | grep -q "UP" && echo -e "${GREEN}✓ Gateway is healthy${NC}" || echo -e "${RED}✗ Gateway health check failed${NC}"
else
    echo -e "${RED}✗ Gateway pod not found${NC}"
fi

# Check if PostgreSQL is accessible
POSTGRES_POD=$(kubectl get pods -n $NAMESPACE -l app=postgres -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ ! -z "$POSTGRES_POD" ]; then
    echo -e "${BLUE}PostgreSQL:${NC}"
    kubectl exec -n $NAMESPACE $POSTGRES_POD -- pg_isready -U postgres 2>/dev/null && echo -e "${GREEN}✓ PostgreSQL is ready${NC}" || echo -e "${RED}✗ PostgreSQL is not ready${NC}"
else
    echo -e "${RED}✗ PostgreSQL pod not found${NC}"
fi

# Check if Keycloak is accessible
KEYCLOAK_POD=$(kubectl get pods -n $NAMESPACE -l app=keycloak -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ ! -z "$KEYCLOAK_POD" ]; then
    echo -e "${BLUE}Keycloak:${NC}"
    kubectl exec -n $NAMESPACE $KEYCLOAK_POD -- curl -s http://localhost:8080/health 2>/dev/null | grep -q "UP" && echo -e "${GREEN}✓ Keycloak is healthy${NC}" || echo -e "${RED}✗ Keycloak health check failed${NC}"
else
    echo -e "${RED}✗ Keycloak pod not found${NC}"
fi

# Check if Platform Management Service is accessible
PLATFORM_POD=$(kubectl get pods -n $NAMESPACE -l app=platform-management-service -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ ! -z "$PLATFORM_POD" ]; then
    echo -e "${BLUE}Platform Management Service:${NC}"
    kubectl exec -n $NAMESPACE $PLATFORM_POD -- curl -s http://localhost:9081/actuator/health 2>/dev/null | grep -q "UP" && echo -e "${GREEN}✓ Platform Management Service is healthy${NC}" || echo -e "${RED}✗ Platform Management Service health check failed${NC}"
else
    echo -e "${RED}✗ Platform Management Service pod not found${NC}"
fi

echo ""
echo -e "${YELLOW}Kubernetes Service Discovery Test:${NC}"

# Test service-to-service communication using Kubernetes DNS
echo -e "${BLUE}Testing Kubernetes service discovery...${NC}"

# Test Gateway to Platform Management Service
if [ ! -z "$GATEWAY_POD" ]; then
    kubectl exec -n $NAMESPACE $GATEWAY_POD -- nslookup platform-management-service 2>/dev/null | grep -q "Name:" && echo -e "${GREEN}✓ Gateway can resolve Platform Management Service${NC}" || echo -e "${RED}✗ Gateway cannot resolve Platform Management Service${NC}"
fi

# Test Gateway to PostgreSQL
if [ ! -z "$GATEWAY_POD" ] && [ ! -z "$POSTGRES_POD" ]; then
    kubectl exec -n $NAMESPACE $GATEWAY_POD -- nslookup postgres 2>/dev/null | grep -q "Name:" && echo -e "${GREEN}✓ Gateway can resolve PostgreSQL${NC}" || echo -e "${RED}✗ Gateway cannot resolve PostgreSQL${NC}"
fi

# Test Gateway to Keycloak
if [ ! -z "$GATEWAY_POD" ] && [ ! -z "$KEYCLOAK_POD" ]; then
    kubectl exec -n $NAMESPACE $GATEWAY_POD -- nslookup keycloak 2>/dev/null | grep -q "Name:" && echo -e "${GREEN}✓ Gateway can resolve Keycloak${NC}" || echo -e "${RED}✗ Gateway cannot resolve Keycloak${NC}"
fi

echo ""
echo -e "${YELLOW}Recent Logs (last 5 lines):${NC}"

# Show recent logs for each service
services=("onified-gateway" "platform-management-service" "postgres" "keycloak")

for service in "${services[@]}"; do
    POD=$(kubectl get pods -n $NAMESPACE -l app=$service -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
    if [ ! -z "$POD" ]; then
        echo -e "${BLUE}$service:${NC}"
        kubectl logs -n $NAMESPACE $POD --tail=5 2>/dev/null | sed 's/^/  /'
        echo ""
    fi
done

echo -e "${GREEN}Health check completed!${NC}"

# Summary
echo ""
echo -e "${YELLOW}Summary:${NC}"
POD_COUNT=$(kubectl get pods -n $NAMESPACE --no-headers | wc -l)
READY_PODS=$(kubectl get pods -n $NAMESPACE --no-headers | grep -c "Running")
echo -e "Total Pods: $POD_COUNT"
echo -e "Ready Pods: $READY_PODS"

if [ $POD_COUNT -eq $READY_PODS ] && [ $POD_COUNT -gt 0 ]; then
    echo -e "${GREEN}✓ All pods are running${NC}"
else
    echo -e "${RED}✗ Some pods are not ready${NC}"
fi 