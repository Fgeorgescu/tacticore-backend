# Dockerfile para Spring Boot Backend
FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Instalar curl para health checks
RUN apt-get update && \
    apt-get install -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copiar todo el proyecto
COPY . .


# Exponer puerto
EXPOSE 8080

# Comando para desarrollo
CMD ["mvn", "spring-boot:run"]
