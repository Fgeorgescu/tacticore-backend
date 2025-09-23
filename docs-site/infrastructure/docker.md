# 🐳 Docker

> Contenedores y orquestación

## 🚀 Configuración

### Dockerfile

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivo JAR
COPY target/tacticore-backend-1.0.0.jar app.jar

# Exponer puerto
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  tacticore-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
      - SPRING_DATASOURCE_USERNAME=sa
      - SPRING_DATASOURCE_PASSWORD=password
    volumes:
      - ./logs:/app/logs
    networks:
      - tacticore-network

  # Base de datos (opcional para desarrollo)
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: tacticore
      POSTGRES_USER: tacticore
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - tacticore-network

volumes:
  postgres_data:

networks:
  tacticore-network:
    driver: bridge
```

## 🔧 Comandos Docker

### Construcción

```bash
# Construir imagen
make docker-build

# O manualmente
docker build -t tacticore-backend:latest .
```

### Ejecución

```bash
# Ejecutar contenedor
make docker-run

# O manualmente
docker run -p 8080:8080 tacticore-backend:latest
```

### Docker Compose

```bash
# Iniciar servicios
make docker-compose-up

# O manualmente
docker-compose up -d

# Detener servicios
make docker-compose-down

# O manualmente
docker-compose down
```

## 📊 Monitoreo

### Logs

```bash
# Ver logs del contenedor
docker logs tacticore-backend

# Ver logs en tiempo real
docker logs -f tacticore-backend

# Ver logs de Docker Compose
docker-compose logs -f
```

### Estado

```bash
# Ver contenedores en ejecución
docker ps

# Ver todas las imágenes
docker images

# Ver uso de recursos
docker stats
```

## 🔍 Troubleshooting

### Problemas Comunes

#### Error de Construcción

```bash
# Verificar que el JAR existe
ls -la target/tacticore-backend-1.0.0.jar

# Construir el proyecto primero
make build
make docker-build
```

#### Error de Puerto

```bash
# Verificar que el puerto esté libre
lsof -i :8080

# Usar un puerto diferente
docker run -p 8081:8080 tacticore-backend:latest
```

#### Error de Permisos

```bash
# Verificar permisos de Docker
docker --version

# Agregar usuario al grupo docker
sudo usermod -aG docker $USER
```

## 📚 Recursos Adicionales

### Documentación

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)

### Herramientas

- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Docker CLI](https://docs.docker.com/engine/reference/commandline/cli/)
- [Docker Compose CLI](https://docs.docker.com/compose/reference/)

### Enlaces Útiles

- [Docker Hub](https://hub.docker.com/)
- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
