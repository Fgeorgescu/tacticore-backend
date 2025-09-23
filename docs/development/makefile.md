# 📋 Comandos Makefile

> Documentación completa de todos los comandos disponibles en el Makefile

## 🚀 Comandos Principales

### Información y Ayuda

```bash
make help         # Mostrar ayuda completa
make info         # Información del proyecto
make version      # Versión del proyecto
```

### Desarrollo

```bash
make dev          # Modo desarrollo (build + run)
make build        # Compilar proyecto
make package      # Empaquetar aplicación
make clean        # Limpiar archivos generados
make run          # Iniciar aplicación
make stop         # Detener aplicación
make status       # Verificar estado de la aplicación
```

## 🧪 Testing

### Tests Unitarios

```bash
make test         # Ejecutar tests unitarios
make test-coverage # Generar reporte de cobertura
make test-integration # Ejecutar tests de integración
```

### Tests de API

```bash
make test-api     # Probar endpoints básicos de la API
make test-analytics # Probar endpoints de analytics
make test-matches # Probar endpoints de matches
make test-kills   # Probar endpoints de kills
make test-all     # Ejecutar todas las pruebas de API
```

## 🔍 Calidad de Código

```bash
make quality      # Análisis de calidad de código (SpotBugs)
make security     # Verificación de seguridad (OWASP)
```

## 🗄️ Base de Datos

```bash
make db-console   # Abrir consola de H2
make db-clear     # Limpiar base de datos
make db-reload    # Recargar datos dummy
```

## 🚀 CI/CD

```bash
make ci           # Ejecutar pipeline de CI localmente
make ci-quick     # Pipeline de CI rápido (sin análisis de calidad)
```

## 🐳 Docker

```bash
make docker-build # Construir imagen Docker
make docker-run   # Ejecutar contenedor Docker
make docker-compose-up # Iniciar servicios con Docker Compose
make docker-compose-down # Detener servicios de Docker Compose
```

## 🏗️ Terraform

```bash
make terraform-init # Inicializar Terraform
make terraform-plan # Planificar cambios de Terraform
make terraform-apply # Aplicar cambios de Terraform
make terraform-destroy # Destruir infraestructura de Terraform
```

## 📦 Despliegue

```bash
make lambda-package # Empaquetar para AWS Lambda
```

## 🔧 Utilidades

```bash
make logs         # Ver logs de la aplicación
make format       # Formatear código
make dependencies # Mostrar dependencias del proyecto
make update-dependencies # Actualizar dependencias
```

## 🧹 Limpieza

```bash
make clean-all    # Limpieza completa (incluye logs y archivos temporales)
```

## 📊 Desarrollo Completo

```bash
make dev-test     # Modo desarrollo con tests
make dev-full     # Desarrollo completo (limpiar, compilar, testear, empaquetar y ejecutar)
```

## 🎯 Ejemplos de Uso

### Flujo de Desarrollo Típico

```bash
# 1. Iniciar desarrollo
make dev

# 2. En otra terminal, probar
make test-api

# 3. Ver logs
make logs

# 4. Detener cuando termines
make stop
```

### Testing Completo

```bash
# Ejecutar todos los tests
make test-all

# Generar reporte de cobertura
make test-coverage

# Verificar calidad de código
make quality
```

### Despliegue

```bash
# Compilar y empaquetar
make build
make package

# Para Lambda
make lambda-package

# Para Docker
make docker-build
make docker-run
```

### CI/CD Local

```bash
# Pipeline completo
make ci

# Pipeline rápido
make ci-quick
```

## 🔍 Comandos de Diagnóstico

### Verificar Estado

```bash
# Estado de la aplicación
make status

# Información del proyecto
make info

# Versión
make version
```

### Logs y Debugging

```bash
# Ver logs
make logs

# Consola de base de datos
make db-console
```

### Base de Datos

```bash
# Limpiar datos
make db-clear

# Recargar datos de prueba
make db-reload
```

## 📋 Variables del Makefile

### Configuración

```makefile
APP_NAME = tacticore-backend
VERSION = 1.0.0
JAR_FILE = target/$(APP_NAME)-$(VERSION).jar
PORT = 8080
BASE_URL = http://localhost:$(PORT)
```

### Colores

```makefile
RED = \033[0;31m
GREEN = \033[0;32m
YELLOW = \033[1;33m
BLUE = \033[0;34m
NC = \033[0m # No Color
```

## 🎨 Personalización

### Agregar Nuevos Comandos

```makefile
.PHONY: mi-comando
mi-comando: ## Descripción del comando
	@echo "$(BLUE)Ejecutando mi comando...$(NC)"
	@# Tu comando aquí
	@echo "$(GREEN)Comando completado$(NC)"
```

### Modificar Comandos Existentes

```makefile
# Ejemplo: modificar el comando build
build: ## Compilar el proyecto
	@echo "$(BLUE)🔨 Compilando proyecto...$(NC)"
	@mvn clean compile -P dev
	@echo "$(GREEN)✅ Compilación exitosa$(NC)"
	@# Agregar tu lógica adicional aquí
```

## 🔧 Troubleshooting

### Comandos No Encontrados

```bash
# Verificar que el Makefile existe
ls -la Makefile

# Verificar permisos
chmod +x Makefile
```

### Errores de Maven

```bash
# Limpiar y recompilar
make clean
make build
```

### Problemas de Puerto

```bash
# Verificar procesos en puerto 8080
lsof -i :8080

# Detener aplicación
make stop
```

### Base de Datos

```bash
# Limpiar y recargar
make db-clear
make db-reload
```

## 📚 Referencias

### Makefile

- [GNU Make Manual](https://www.gnu.org/software/make/manual/)
- [Makefile Tutorial](https://makefiletutorial.com/)

### Maven

- [Maven Documentation](https://maven.apache.org/guides/)
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)

### Spring Boot

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-cli.html)
