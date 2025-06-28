#!/bin/bash

# EUREKA INSTANCE MONITORING SCRIPT
# Monitors Eureka instances in real-time during emergency

echo "🔍 EUREKA INSTANCE MONITORING"
echo "=============================="

while true; do
    clear
    echo "$(date '+%Y-%m-%d %H:%M:%S') - Eureka Instance Status"
    echo "=================================================="
    
    # Get Eureka server health
    echo "📊 Eureka Server Status:"
    if curl -s http://localhost:8761/actuator/health | grep -q '"status":"UP"'; then
        echo "✅ Eureka Server: UP"
    else
        echo "❌ Eureka Server: DOWN"
    fi
    
    echo ""
    echo "🏥 Registered Instances:"
    echo "------------------------"
    
    # Get all registered applications
    APPS=$(curl -s http://localhost:8761/eureka/apps | grep -o '<application><name>[^<]*</name>' | sed 's/<application><name>\(.*\)<\/name>/\1/')
    
    if [ -z "$APPS" ]; then
        echo "⚠️  No applications registered"
    else
        for app in $APPS; do
            echo "📱 Application: $app"
            
            # Get instances for this application
            INSTANCES=$(curl -s "http://localhost:8761/eureka/apps/$app" | grep -o '<instanceId>[^<]*</instanceId>' | sed 's/<instanceId>\(.*\)<\/instanceId>/\1/')
            STATUSES=$(curl -s "http://localhost:8761/eureka/apps/$app" | grep -o '<status>[^<]*</status>' | sed 's/<status>\(.*\)<\/status>/\1/')
            
            if [ -z "$INSTANCES" ]; then
                echo "   ⚠️  No instances found"
            else
                # Combine instances and statuses
                paste <(echo "$INSTANCES") <(echo "$STATUSES") | while read instance status; do
                    case $status in
                        "UP")
                            echo "   ✅ $instance: $status"
                            ;;
                        "DOWN")
                            echo "   ❌ $instance: $status"
                            ;;
                        "OUT_OF_SERVICE")
                            echo "   🔧 $instance: $status"
                            ;;
                        *)
                            echo "   ❓ $instance: $status"
                            ;;
                    esac
                done
            fi
            echo ""
        done
    fi
    
    echo "📈 Renewal Statistics:"
    echo "----------------------"
    # Get renewal stats from Eureka
    RENEWAL_COUNT=$(curl -s http://localhost:8761/actuator/health | grep -o '"renewalCount":[0-9]*' | cut -d':' -f2)
    RENEWAL_THRESHOLD=$(curl -s http://localhost:8761/actuator/health | grep -o '"renewalThreshold":[0-9]*' | cut -d':' -f2)
    
    if [ ! -z "$RENEWAL_COUNT" ] && [ ! -z "$RENEWAL_THRESHOLD" ]; then
        echo "   Renewals: $RENEWAL_COUNT"
        echo "   Threshold: $RENEWAL_THRESHOLD"
        
        if [ "$RENEWAL_COUNT" -lt "$RENEWAL_THRESHOLD" ]; then
            echo "   ⚠️  RENEWALS BELOW THRESHOLD!"
        else
            echo "   ✅ Renewals above threshold"
        fi
    else
        echo "   ⚠️  Unable to get renewal statistics"
    fi
    
    echo ""
    echo "⏰ Next update in 10 seconds... (Press Ctrl+C to exit)"
    sleep 10
done 