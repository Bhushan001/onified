# 🚨 EMERGENCY EUREKA RESPONSE GUIDE

## Issue Description
Eureka is incorrectly reporting instances as UP when they are actually DOWN. This is caused by:
- Self-preservation mode preventing proper instance eviction
- High renewal threshold (85%) preventing aggressive cleanup
- Standard eviction intervals being too slow for emergency situations

## Root Cause Analysis
1. **Self-preservation enabled**: Eureka refuses to evict instances when renewals are below threshold
2. **High renewal threshold**: 85% threshold is too conservative for development/emergency scenarios
3. **Slow eviction intervals**: 60-second intervals allow dead instances to remain registered too long
4. **Standard heartbeat intervals**: 30s renewal/90s expiration is too slow for rapid detection

## Emergency Configuration Changes Applied

### Eureka Server (`eureka-server/src/main/resources/application.yml`)
```yaml
eureka:
  server:
    # EMERGENCY: Disable self-preservation
    enable-self-preservation: false
    # EMERGENCY: Faster eviction (30s instead of 60s)
    eviction-interval-timer-in-ms: 30000
    # EMERGENCY: Lower threshold (60% instead of 85%)
    renewal-percent-threshold: 0.60
```

### Eureka Client (`authentication-service/src/main/resources/application.yml`)
```yaml
eureka:
  instance:
    # EMERGENCY: Faster heartbeat (15s renewal, 45s expiration)
    lease-renewal-interval-in-seconds: 15
    lease-expiration-duration-in-seconds: 45
  client:
    # EMERGENCY: More frequent registry fetches
    registry-fetch-interval-seconds: 10
```

## Emergency Response Steps

### 1. Apply Emergency Configuration
```bash
# Run the emergency fix script
./scripts/emergency-eureka-fix.sh
```

### 2. Monitor the Situation
```bash
# Start real-time monitoring
./scripts/monitor-eureka-instances.sh
```

### 3. Verify Fix
- Check Eureka dashboard: http://localhost:8761
- Monitor logs: `./logs/eureka-server/`
- Verify instances are being properly evicted

### 4. Restore Normal Configuration (After Issue Resolution)
```bash
# Restore normal settings
./scripts/restore-eureka-normal.sh
```

## Monitoring Commands

### Check Eureka Health
```bash
curl http://localhost:8761/actuator/health
```

### List Registered Instances
```bash
curl http://localhost:8761/eureka/apps
```

### Check Specific Application
```bash
curl http://localhost:8761/eureka/apps/AUTHENTICATION-SERVICE
```

### View Eureka Logs
```bash
tail -f ./logs/eureka-server/eureka-server.log
```

## Emergency Configuration Details

| Setting | Normal | Emergency | Impact |
|---------|--------|-----------|---------|
| Self-preservation | Enabled | Disabled | Allows eviction even with low renewals |
| Renewal threshold | 85% | 60% | More aggressive eviction |
| Eviction interval | 60s | 30s | Faster cleanup |
| Client renewal | 30s | 15s | Faster heartbeat |
| Client expiration | 90s | 45s | Faster detection of dead instances |

## Warning ⚠️
**This emergency configuration is NOT suitable for production!**
- Disables important safety mechanisms
- May cause unnecessary instance evictions in normal operation
- Should be restored immediately after issue resolution

## Troubleshooting

### If Eureka Won't Start
1. Check logs: `docker-compose logs eureka-server`
2. Verify port 8761 is available
3. Check Docker container status: `docker ps -a`

### If Instances Still Not Evicted
1. Verify emergency config is applied
2. Check renewal statistics in Eureka dashboard
3. Restart problematic services
4. Check network connectivity between services

### If Services Can't Register
1. Verify Eureka server is running
2. Check service configuration
3. Verify network connectivity
4. Check service logs for registration errors

## Recovery Checklist
- [ ] Emergency configuration applied
- [ ] Eureka server restarted
- [ ] Instances properly evicted
- [ ] Services re-registering correctly
- [ ] System stability confirmed
- [ ] Normal configuration restored
- [ ] Monitoring continued for 24 hours

## Contact Information
For additional support, check the logs and Eureka dashboard for detailed error information. 