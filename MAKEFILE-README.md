# 🛠️ Makefile para Tacticore Backend

## 📋 Resumen

Este Makefile centraliza todos los comandos y scripts del proyecto Tacticore Backend, proporcionando una interfaz unificada y fácil de usar para todas las operaciones de desarrollo, testing, despliegue y mantenimiento.

## 🚀 Inicio Rápido

```bash
# Ver todos los comandos disponibles
make help

# Desarrollo básico
make dev          # Compilar y ejecutar
make test         # Ejecutar tests
make run          # Iniciar aplicación

# Testing de API
make test-api     # Probar endpoints básicos
make test-all     # Todas las pruebas de API

# Información del proyecto
make info         # Ver información del proyecto
make status       # Verificar estado de la aplicación
```

## 📚 Comandos Disponibles

### 🔨 Desarrollo

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make build` | Compilar el proyecto | `mvn clean compile` |
| `make package` | Empaquetar la aplicación | `mvn clean package` |
| `make clean` | Limpiar archivos generados | `mvn clean` |
| `make dev` | Modo desarrollo (build + run) | - |
| `make dev-test` | Desarrollo con tests | - |
| `make dev-full` | Desarrollo completo | - |

### 🧪 Testing

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make test` | Ejecutar tests unitarios | `mvn test` |
| `make test-coverage` | Generar reporte de cobertura | `mvn jacoco:report` |
| `make test-integration` | Tests de integración | `mvn verify -P integration-tests` |
| `make test-api` | Probar endpoints básicos | `./test-api.sh` |
| `make test-analytics` | Probar endpoints de analytics | - |
| `make test-matches` | Probar endpoints de matches | - |
| `make test-kills` | Probar endpoints de kills | - |
| `make test-all` | Todas las pruebas de API | - |

### 🗄️ Base de Datos

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make db-console` | Abrir consola de H2 | - |
| `make db-clear` | Limpiar base de datos | `curl -X DELETE /api/data/clear` |
| `make db-reload` | Recargar datos dummy | `curl -X POST /api/data/reload-dummy` |

### 🚀 Aplicación

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make run` | Iniciar la aplicación | `mvn spring-boot:run` |
| `make run-background` | Iniciar en segundo plano | `nohup mvn spring-boot:run &` |
| `make stop` | Detener la aplicación | `pkill -f "spring-boot:run"` |
| `make status` | Verificar estado | `curl /ping` |
| `make logs` | Ver logs de la aplicación | `tail -f app.log` |

### 🔍 Calidad de Código

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make quality` | Análisis de calidad | `mvn spotbugs:check` |
| `make security` | Verificación de seguridad | `mvn org.owasp:dependency-check-maven:check` |
| `make format` | Formatear código | `mvn spotless:apply` |

### 🚀 CI/CD

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make ci` | Pipeline completo de CI | `./test-ci-local.sh` |
| `make ci-quick` | CI rápido | - |

### 🐳 Docker

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make docker-build` | Construir imagen Docker | `docker build -t tacticore-backend .` |
| `make docker-run` | Ejecutar contenedor | `docker run -p 8080:8080 tacticore-backend` |
| `make docker-compose-up` | Iniciar con Docker Compose | `docker-compose up -d` |
| `make docker-compose-down` | Detener Docker Compose | `docker-compose down` |

### 🏗️ Terraform

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make terraform-init` | Inicializar Terraform | `cd terraform/environments/dev && terraform init` |
| `make terraform-plan` | Planificar cambios | `cd terraform/environments/dev && terraform plan` |
| `make terraform-apply` | Aplicar cambios | `cd terraform/environments/dev && terraform apply` |
| `make terraform-destroy` | Destruir infraestructura | `cd terraform/environments/dev && terraform destroy` |

### 📊 Utilidades

| Comando | Descripción | Equivalente Anterior |
|---------|-------------|---------------------|
| `make info` | Información del proyecto | - |
| `make version` | Mostrar versión | - |
| `make dependencies` | Mostrar dependencias | `mvn dependency:tree` |
| `make update-dependencies` | Actualizar dependencias | `mvn versions:use-latest-versions` |
| `make clean-all` | Limpieza completa | - |

## 🎯 Flujos de Trabajo Comunes

### Desarrollo Diario
```bash
# Iniciar desarrollo
make dev

# En otra terminal, probar cambios
make test-api

# Ver logs
make logs

# Detener cuando termines
make stop
```

### Testing Completo
```bash
# Ejecutar todos los tests
make test
make test-coverage
make test-all

# Ver reportes
open target/site/jacoco/index.html
```

### CI/CD Local
```bash
# Pipeline completo
make ci

# O pipeline rápido
make ci-quick
```

### Despliegue
```bash
# Con Docker
make docker-build
make docker-run

# Con Docker Compose
make docker-compose-up

# Con Terraform
make terraform-init
make terraform-plan
make terraform-apply
```

## 🔧 Configuración

### Variables del Makefile

```makefile
APP_NAME = tacticore-backend
VERSION = 1.0.0
PORT = 8080
BASE_URL = http://localhost:8080
```

### Personalización

Puedes modificar las variables en el Makefile para adaptarlas a tu entorno:

```makefile
# Cambiar puerto
PORT = 8081

# Cambiar URL base
BASE_URL = http://localhost:8081

# Cambiar versión
VERSION = 1.1.0
```

## 🎨 Características

### ✅ Ventajas del Makefile

1. **Unificación**: Todos los comandos en un solo lugar
2. **Consistencia**: Misma interfaz para todas las operaciones
3. **Documentación**: Help integrado con `make help`
4. **Colores**: Output colorizado para mejor legibilidad
5. **Validación**: Verificación de estado antes de ejecutar comandos
6. **Flexibilidad**: Fácil de extender y modificar

### 🎯 Beneficios

- **Menos scripts**: Un solo archivo en lugar de múltiples scripts
- **Mejor mantenimiento**: Código centralizado y organizado
- **Fácil de usar**: Comandos intuitivos y bien documentados
- **Cross-platform**: Funciona en Linux, macOS y Windows (con make)
- **Integración**: Se integra bien con IDEs y herramientas de CI/CD

## 🚀 Migración desde Scripts

### Script de Migración

```bash
# Ejecutar migración automática
./migrate-to-makefile.sh
```

Este script:
1. Crea un backup de todos los scripts existentes
2. Muestra la equivalencia de comandos
3. Proporciona una guía de transición

### Equivalencias Principales

| Script Anterior | Comando Makefile |
|----------------|------------------|
| `./test-api.sh` | `make test-api` |
| `./test-ci-local.sh` | `make ci` |
| `./test-no-mocks.sh` | `make test-all` |
| `./deploy.sh` | `make docker-build && make docker-run` |
| `./deploy-terraform.sh` | `make terraform-apply` |

## 🐛 Troubleshooting

### Error: "make: command not found"
```bash
# Instalar make en macOS
brew install make

# Instalar make en Ubuntu/Debian
sudo apt-get install make

# Instalar make en CentOS/RHEL
sudo yum install make
```

### Error: "No rule to make target"
```bash
# Verificar que estás en el directorio correcto
pwd
ls -la Makefile

# Ver todos los targets disponibles
make help
```

### Error: "Permission denied"
```bash
# Dar permisos de ejecución si es necesario
chmod +x migrate-to-makefile.sh
```

## 📚 Recursos Adicionales

- [GNU Make Manual](https://www.gnu.org/software/make/manual/)
- [Makefile Tutorial](https://makefiletutorial.com/)
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)

## 🤝 Contribuir

Para agregar nuevos comandos al Makefile:

1. Agrega el target en la sección correspondiente
2. Incluye la descripción en el comentario `##`
3. Actualiza esta documentación
4. Prueba el comando antes de hacer commit

---

**¡El Makefile está listo para usar!** 🎉

Ejecuta `make help` para ver todos los comandos disponibles y comienza a usar esta interfaz unificada para tu desarrollo.

