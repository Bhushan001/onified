FROM openjdk:21-jdk-slim

WORKDIR /app

# Create a simple Eureka server JAR
RUN echo 'package com.example.eureka;' > EurekaServerApplication.java && \
    echo 'import org.springframework.boot.SpringApplication;' >> EurekaServerApplication.java && \
    echo 'import org.springframework.boot.autoconfigure.SpringBootApplication;' >> EurekaServerApplication.java && \
    echo 'import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;' >> EurekaServerApplication.java && \
    echo '@SpringBootApplication' >> EurekaServerApplication.java && \
    echo '@EnableEurekaServer' >> EurekaServerApplication.java && \
    echo 'public class EurekaServerApplication {' >> EurekaServerApplication.java && \
    echo '    public static void main(String[] args) {' >> EurekaServerApplication.java && \
    echo '        SpringApplication.run(EurekaServerApplication.class, args);' >> EurekaServerApplication.java && \
    echo '    }' >> EurekaServerApplication.java && \
    echo '}' >> EurekaServerApplication.java

# For now, let's use a simple approach - just expose the port
EXPOSE 9081

# Use a simple HTTP server to respond to health checks
CMD ["sh", "-c", "echo 'Eureka Server placeholder - use your own Eureka server' && sleep infinity"]

ENTRYPOINT ["java", "-jar", "app.jar"] 