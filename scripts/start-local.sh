#!/bin/bash

# Onified Platform Local Startup Script
# This script starts all services locally with Eureka enabled

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if port is available
check_port() {
    local port=$1
    local service=$2
    
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_warning "Port $port is already in use. $service may not start properly."
        return 1
    else
        print_success "Port $port is available for $service"
        return 0
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service=$2
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for $service to be ready..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" >/dev/null 2>&1; then
            print_success "$service is ready!"
            return 0
        fi
        
        print_status "Attempt $attempt/$max_attempts: $service not ready yet..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "$service failed to start within expected time"
    return 1
}

# Function to start service in background
start_service() {
    local service_dir=$1
    local service_name=$2
    local port=$3
    local health_url=$4
    
    print_status "Starting $service_name..."
    
    # Check if port is available
    check_port $port $service_name
    
    # Start service in background
    cd "$service_dir"
    nohup mvn spring-boot:run > "../logs/$service_name.log" 2>&1 &
    local pid=$!
    echo $pid > "../logs/$service_name.pid"
    
    print_success "$service_name started with PID $pid"
    
    # Wait for service to be ready
    if [ -n "$health_url" ]; then
        wait_for_service "$health_url" "$service_name"
    fi
    
    cd ..
}

# Main script
main() {
    print_status "Starting Onified Platform locally..."
    
    # Create logs directory
    mkdir -p logs
    
    # Load environment variables
    if [ -f .env ]; then
        print_status "Loading environment variables from .env file..."
        export $(cat .env | grep -v '^#' | xargs)
    else
        print_warning ".env file not found. Using default values."
    fi
    
    # Check prerequisites
    print_status "Checking prerequisites..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    
    # Check PostgreSQL
    if ! command -v psql &> /dev/null; then
        print_error "PostgreSQL client is not installed or not in PATH"
        exit 1
    fi
    
    print_success "Prerequisites check passed"
    
    # Check database connectivity
    print_status "Checking database connectivity..."
    if ! psql -h localhost -U postgres -d auth_db -c "SELECT 1;" >/dev/null 2>&1; then
        print_error "Cannot connect to PostgreSQL. Please ensure PostgreSQL is running and databases are created."
        exit 1
    fi
    print_success "Database connectivity check passed"
    
    # Start services in order
    
    # 1. Start Eureka Server
    print_status "=== Starting Eureka Server ==="
    start_service "eureka-server" "Eureka Server" "8761" "http://localhost:8761/actuator/health"
    
    # Wait a bit for Eureka to fully start
    sleep 5
    
    # 2. Start Core Services (in parallel)
    print_status "=== Starting Core Services ==="
    
    # Platform Management Service
    start_service "platform-management-service" "Platform Management Service" "9081" "http://localhost:9081/actuator/health" &
    
    # Application Config Service
    start_service "application-config-service" "Application Config Service" "9082" "http://localhost:9082/actuator/health" &
    
    # User Management Service
    start_service "user-management-service" "User Management Service" "9085" "http://localhost:9085/actuator/health" &
    
    # Permission Registry Service
    start_service "permission-registry-service" "Permission Registry Service" "9084" "http://localhost:9084/actuator/health" &
    
    # Tenant Management Service
    start_service "tenant-management-service" "Tenant Management Service" "9086" "http://localhost:9086/actuator/health" &
    
    # Wait for core services to start
    wait
    
    # 3. Start Authentication Service
    print_status "=== Starting Authentication Service ==="
    start_service "authentication-service" "Authentication Service" "9083" "http://localhost:9083/actuator/health"
    
    # 4. Start API Gateway
    print_status "=== Starting API Gateway ==="
    start_service "onified-gateway" "API Gateway" "9080" "http://localhost:9080/actuator/health"
    
    # 5. Start Frontend (optional)
    if [ "$1" = "--with-frontend" ]; then
        print_status "=== Starting Angular Frontend ==="
        if [ -d "web" ]; then
            cd web
            if [ ! -d "node_modules" ]; then
                print_status "Installing npm dependencies..."
                npm install
            fi
            print_status "Starting Angular development server..."
            nohup ng serve --port 4200 > "../logs/frontend.log" 2>&1 &
            local frontend_pid=$!
            echo $frontend_pid > "../logs/frontend.pid"
            print_success "Frontend started with PID $frontend_pid"
            cd ..
        else
            print_warning "Frontend directory not found. Skipping frontend startup."
        fi
    fi
    
    # Final status
    print_success "=== All services started successfully! ==="
    print_status "Service URLs:"
    echo "  Eureka Server: http://localhost:8761"
    echo "  API Gateway: http://localhost:9080"
    echo "  Platform Management: http://localhost:9081"
    echo "  Application Config: http://localhost:9082"
    echo "  Authentication: http://localhost:9083"
    echo "  Permission Registry: http://localhost:9084"
    echo "  User Management: http://localhost:9085"
    echo "  Tenant Management: http://localhost:9086"
    if [ "$1" = "--with-frontend" ]; then
        echo "  Frontend: http://localhost:4200"
    fi
    
    print_status "Check Eureka dashboard to verify all services are registered: http://localhost:8761"
    print_status "Logs are available in the logs/ directory"
    
    # Create a simple health check script
    cat > scripts/health-check.sh << 'EOF'
#!/bin/bash
echo "Checking service health..."
for port in 8761 9080 9081 9082 9083 9084 9085 9086; do
    echo -n "Port $port: "
    if curl -s http://localhost:$port/actuator/health >/dev/null 2>&1; then
        echo "UP"
    else
        echo "DOWN"
    fi
done
EOF
    chmod +x scripts/health-check.sh
    
    print_success "Health check script created: scripts/health-check.sh"
}

# Function to stop all services
stop_services() {
    print_status "Stopping all services..."
    
    if [ -d "logs" ]; then
        for pid_file in logs/*.pid; do
            if [ -f "$pid_file" ]; then
                local pid=$(cat "$pid_file")
                local service=$(basename "$pid_file" .pid)
                
                if kill -0 "$pid" 2>/dev/null; then
                    print_status "Stopping $service (PID: $pid)..."
                    kill "$pid"
                    rm "$pid_file"
                else
                    print_warning "$service is not running"
                    rm "$pid_file"
                fi
            fi
        done
    fi
    
    print_success "All services stopped"
}

# Function to show status
show_status() {
    print_status "Service Status:"
    
    if [ -d "logs" ]; then
        for pid_file in logs/*.pid; do
            if [ -f "$pid_file" ]; then
                local pid=$(cat "$pid_file")
                local service=$(basename "$pid_file" .pid)
                
                if kill -0 "$pid" 2>/dev/null; then
                    print_success "$service: RUNNING (PID: $pid)"
                else
                    print_error "$service: NOT RUNNING"
                    rm "$pid_file"
                fi
            fi
        done
    else
        print_warning "No service PIDs found"
    fi
}

# Parse command line arguments
case "$1" in
    "start")
        main "$2"
        ;;
    "stop")
        stop_services
        ;;
    "status")
        show_status
        ;;
    "restart")
        stop_services
        sleep 2
        main "$2"
        ;;
    *)
        echo "Usage: $0 {start|stop|status|restart} [--with-frontend]"
        echo ""
        echo "Commands:"
        echo "  start [--with-frontend]  Start all services (optionally with frontend)"
        echo "  stop                     Stop all services"
        echo "  status                   Show status of all services"
        echo "  restart [--with-frontend] Restart all services"
        echo ""
        echo "Examples:"
        echo "  $0 start                 Start backend services only"
        echo "  $0 start --with-frontend Start all services including frontend"
        echo "  $0 stop                  Stop all services"
        echo "  $0 status                Check service status"
        exit 1
        ;;
esac 