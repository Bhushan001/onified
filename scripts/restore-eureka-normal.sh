#!/bin/bash

# RESTORE NORMAL EUREKA CONFIGURATION
# This script restores the original Eureka configuration after emergency

echo "🔄 RESTORING NORMAL EUREKA CONFIGURATION"
echo "========================================"

# Backup current emergency config
echo "1. Creating backup of emergency configuration..."
cp eureka-server/src/main/resources/application.yml eureka-server/src/main/resources/application.yml.emergency.backup

# Restore original configuration
echo "2. Restoring normal configuration..."

cat > eureka-server/src/main/resources/application.yml << 'EOF'
server:
  port: 8761

spring:
  application:
    name: eureka-server

# Eureka Server Configuration
eureka:
  instance:
    hostname: localhost
    prefer-ip-address: true
  client:
    # Disable client behavior for server
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka/
  server:
    # Enable self-preservation mode for Docker environment
    enable-self-preservation: true
    # Eviction interval (in milliseconds) - standard for production
    eviction-interval-timer-in-ms: 60000
    # Response cache update interval
    response-cache-update-interval-ms: 3000
    # Renewal threshold - standard for production
    renewal-percent-threshold: 0.85
    # Peer node timeout
    peer-eureka-nodes-update-interval-ms: 60000
    # Enable/disable peer node updates
    enable-replicated-request-compression: false
    # Wait time in ms before replica sync
    wait-time-in-ms-when-sync-empty: 0

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,eureka
  endpoint:
    health:
      show-details: always

# Logging Configuration
logging:
  level:
    com.netflix.eureka: DEBUG
    com.netflix.discovery: DEBUG
    org.springframework.cloud.netflix.eureka: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: ${LOG_DIR:-/app/logs}/eureka-server.log
EOF

echo "3. Restoring normal client configuration..."
# Restore authentication service to normal
cat > authentication-service/src/main/resources/application.yml << 'EOF'
spring:
  config:
    import: optional:.env[.properties]
  application:
    name: authentication-service
  datasource:
    url: ${AUTH_DB_URL:jdbc:postgresql://localhost:5432/authentication_db}
    username: ${AUTH_DB_USERNAME:postgres}
    password: ${AUTH_DB_PASSWORD:root}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    open-in-view: false

server:
  port: ${AUTH_PORT:9083}

jwt:
  secret: yourVerySecretKeyForJWTTokenGenerationThatShouldBeLongAndComplex12345!@#$%^&*() # !! IMPORTANT: Change this in production !!
  expiration: 3600000 # 1 hour in milliseconds (adjust as needed)

# Feign client config for user-management-service:
# - If USER_MANAGEMENT_URL is set, it is used.
# - Otherwise, http://localhost:${USER_MGMT_PORT:9085} is used (local dev by default).
# - For Docker Compose, set USER_MANAGEMENT_URL to http://user-management-service:${USER_MGMT_PORT:9085} in your .env.
feign:
  client:
    config:
      user-management-service:
        url: ${USER_MANAGEMENT_URL:http://localhost:${USER_MGMT_PORT:9085}}

logging:
  level:
    com.onified.ai.authentication_service: DEBUG
    org.springframework.security: DEBUG
    org.keycloak: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: ${LOG_DIR:-/app/logs}/authentication-service.log
    max-size: 10MB
    max-history: 30
    total-size-cap: 1GB

# Keycloak Configuration
keycloak:
  auth-server-url: ${KEYCLOAK_AUTH_SERVER_URL:http://localhost:9090}
  realm: ${KEYCLOAK_REALM:onified}
  client-id: ${KEYCLOAK_CLIENT_ID:onified-auth-service}
  client-secret: ${KEYCLOAK_CLIENT_SECRET:qflytBTZoCyPJbNbeHsNjB2RGCike2ia}
  admin:
    username: admin
    password: admin123
    realm: master

# Eureka Client Configuration
eureka:
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
    health-check-url-path: /actuator/health
  client:
    enabled: ${EUREKA_ENABLED:true}
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:http://localhost:8761/eureka/}
    register-with-eureka: ${EUREKA_ENABLED:true}
    fetch-registry: ${EUREKA_ENABLED:true}

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
EOF

echo "4. Restarting Eureka server with normal configuration..."
docker-compose stop eureka-server
sleep 5
docker-compose rm -f eureka-server
docker-compose up -d eureka-server

echo "5. Waiting for Eureka to start..."
sleep 15

# Check Eureka health
echo "6. Checking Eureka server health..."
for i in {1..10}; do
    if curl -s http://localhost:8761/actuator/health | grep -q '"status":"UP"'; then
        echo "✅ Eureka server is UP and running with normal configuration"
        break
    else
        echo "⏳ Waiting for Eureka to start... (attempt $i/10)"
        sleep 5
    fi
done

echo ""
echo "✅ NORMAL CONFIGURATION RESTORED:"
echo "- Self-preservation: ENABLED"
echo "- Renewal threshold: 85%"
echo "- Eviction interval: 60s"
echo "- Client heartbeat: 30s renewal, 90s expiration"
echo ""
echo "📊 Monitor Eureka dashboard at: http://localhost:8761"
echo "📋 Emergency backup saved as: eureka-server/src/main/resources/application.yml.emergency.backup" 