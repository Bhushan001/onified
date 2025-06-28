#!/bin/bash

# EMERGENCY EUREKA FIX SCRIPT
# This script restarts Eureka server with emergency configuration to fix
# instances being incorrectly reported as UP when they're actually down

echo "🚨 EMERGENCY EUREKA FIX - Starting..."
echo "======================================"

# Stop Eureka server
echo "1. Stopping Eureka server..."
docker-compose stop eureka-server

# Wait a moment for clean shutdown
sleep 5

# Remove Eureka container to ensure clean restart
echo "2. Removing Eureka container for clean restart..."
docker-compose rm -f eureka-server

# Start Eureka server with new configuration
echo "3. Starting Eureka server with emergency configuration..."
docker-compose up -d eureka-server

# Wait for Eureka to start
echo "4. Waiting for Eureka server to start..."
sleep 15

# Check Eureka health
echo "5. Checking Eureka server health..."
for i in {1..10}; do
    if curl -s http://localhost:8761/actuator/health | grep -q '"status":"UP"'; then
        echo "✅ Eureka server is UP and running"
        break
    else
        echo "⏳ Waiting for Eureka to start... (attempt $i/10)"
        sleep 5
    fi
done

# Show current instances
echo "6. Current registered instances:"
curl -s http://localhost:8761/eureka/apps | grep -E "(instanceId|status)" | head -20

echo ""
echo "🚨 EMERGENCY CONFIGURATION APPLIED:"
echo "- Self-preservation: DISABLED"
echo "- Renewal threshold: 60% (was 85%)"
echo "- Eviction interval: 30s (was 60s)"
echo "- Client heartbeat: 15s renewal, 45s expiration"
echo ""
echo "⚠️  WARNING: This configuration is for emergency use only!"
echo "   Restore normal settings after the issue is resolved."
echo ""
echo "📊 Monitor Eureka dashboard at: http://localhost:8761"
echo "📋 Check logs at: ./logs/eureka-server/" 