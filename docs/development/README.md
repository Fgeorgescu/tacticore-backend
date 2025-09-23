# 🔧 Desarrollo

> Guías y documentación para desarrolladores

## 📚 Secciones

- **[Configuración](setup.md)** - Configuración del entorno de desarrollo
- **[Estructura del Proyecto](structure.md)** - Organización del código
- **[Comandos Makefile](makefile.md)** - Comandos disponibles
- **[Testing](testing.md)** - Guías de testing

## 🚀 Inicio Rápido

### Prerrequisitos

- **Java 17** o superior
- **Maven 3.9+**
- **Git**

### Configuración Inicial

```bash
# Clonar el repositorio
git clone https://github.com/Fgeorgescu/tacticore-backend.git
cd tacticore-backend

# Compilar el proyecto
make build

# Ejecutar la aplicación
make run
```

### Verificar Instalación

```bash
# Probar el endpoint de salud
curl http://localhost:8080/ping

# Ver información del proyecto
make info
```

## 🏗️ Arquitectura del Proyecto

### Stack Tecnológico

- **Framework**: Spring Boot 3.2.0
- **Java**: JDK 17
- **Build Tool**: Maven
- **Base de Datos**: H2 (desarrollo)
- **Testing**: JUnit 5, Mockito
- **CI/CD**: GitHub Actions

### Estructura de Carpetas

```
src/
├── main/
│   ├── java/com/tacticore/lambda/
│   │   ├── config/          # Configuración
│   │   ├── controller/      # Controladores REST
│   │   ├── model/          # Entidades y DTOs
│   │   ├── repository/     # Repositorios JPA
│   │   ├── service/        # Lógica de negocio
│   │   └── LambdaApplication.java
│   └── resources/
│       ├── application.yml # Configuración de Spring
│       └── example.json    # Datos de prueba
└── test/
    └── java/com/tacticore/lambda/
        ├── controller/     # Tests de controladores
        └── service/        # Tests de servicios
```

## 🔧 Comandos de Desarrollo

### Compilación y Ejecución

```bash
make build          # Compilar proyecto
make run            # Ejecutar aplicación
make stop           # Detener aplicación
make dev            # Modo desarrollo (build + run)
```

### Testing

```bash
make test           # Ejecutar tests unitarios
make test-coverage  # Generar reporte de cobertura
make test-integration # Tests de integración
make test-api       # Probar endpoints de API
```

### Base de Datos

```bash
make db-console     # Abrir consola H2
make db-clear       # Limpiar base de datos
make db-reload      # Recargar datos dummy
```

### Calidad de Código

```bash
make quality        # Análisis con SpotBugs
make security       # Verificación de seguridad
make ci             # Pipeline completo de CI
```

## 📊 Perfiles Maven

### Perfil de Desarrollo (dev)

```bash
mvn clean compile -P dev
mvn package -DskipTests -P dev
```

**Características**:
- Spring Boot Maven Plugin
- Configuración para desarrollo local
- Exclusión de Tomcat para Lambda

### Perfil de Lambda

```bash
mvn package -DskipTests -P lambda
```

**Características**:
- Configuración específica para AWS Lambda
- Main class: `com.tacticore.lambda.LambdaHandler`
- Optimizado para despliegue en la nube

### Perfil de Integración

```bash
mvn verify -P integration-tests
```

**Características**:
- Tests de integración con Failsafe
- Configuración para testing completo

## 🗄️ Base de Datos

### H2 en Memoria

- **URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: `password`
- **Consola**: `http://localhost:8080/h2-console`

### Entidades Principales

- **MatchEntity** - Información de partidas
- **KillEntity** - Datos de kills individuales
- **ChatMessageEntity** - Mensajes de chat
- **AnalyticsDataEntity** - Datos de analytics
- **MapEntity** - Información de mapas
- **WeaponEntity** - Información de armas

### Datos de Prueba

El sistema incluye datos de prueba que se cargan automáticamente:

- **1 partida** de ejemplo
- **Múltiples kills** con diferentes usuarios
- **Mensajes de chat** de ejemplo
- **Datos de analytics** históricos
- **Mapas y armas** predefinidos

## 🧪 Testing

### Tests Unitarios

```bash
# Ejecutar todos los tests
make test

# Tests específicos
mvn test -Dtest=ApiControllerTest
mvn test -Dtest=AnalyticsServiceTest
```

### Tests de Integración

```bash
# Tests de integración
make test-integration

# Tests de API
make test-api
```

### Cobertura de Código

```bash
# Generar reporte de cobertura
make test-coverage

# Ver reporte
open target/site/jacoco/index.html
```

## 🔍 Debugging

### Logs

```bash
# Ver logs de la aplicación
make logs

# Logs en tiempo real
tail -f app.log
```

### Base de Datos

```bash
# Abrir consola H2
make db-console

# Verificar estado
curl http://localhost:8080/api/data/status
```

### Endpoints

```bash
# Health check
curl http://localhost:8080/ping

# Información del sistema
curl http://localhost:8080/api/health
```

## 🚀 Despliegue

### Desarrollo Local

```bash
# Iniciar en modo desarrollo
make dev

# La aplicación estará disponible en:
# http://localhost:8080
```

### AWS Lambda

```bash
# Empaquetar para Lambda
make lambda-package

# Desplegar con Terraform
make terraform-apply
```

### Docker

```bash
# Construir imagen
make docker-build

# Ejecutar contenedor
make docker-run
```

## 🔧 Configuración

### Variables de Entorno

```yaml
# application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### Perfiles de Spring

- **default** - Configuración base
- **dev** - Desarrollo local
- **lambda** - AWS Lambda
- **test** - Testing

## 📝 Convenciones de Código

### Java

- **Indentación**: 4 espacios
- **Línea máxima**: 120 caracteres
- **Naming**: camelCase para variables, PascalCase para clases
- **Documentación**: JavaDoc para métodos públicos

### Git

- **Commits**: Mensajes descriptivos en español
- **Branches**: `feature/`, `bugfix/`, `hotfix/`
- **Pull Requests**: Descripción detallada de cambios

### Testing

- **Naming**: `testMethodName_Scenario_ExpectedResult`
- **Estructura**: Arrange, Act, Assert
- **Cobertura**: Mínimo 80% para código nuevo

## 🐛 Troubleshooting

### Problemas Comunes

#### Puerto en Uso

```bash
# Verificar procesos en puerto 8080
lsof -i :8080

# Detener aplicación
make stop
```

#### Error de Base de Datos

```bash
# Limpiar y recargar datos
make db-clear
make db-reload
```

#### Error de Compilación

```bash
# Limpiar y recompilar
make clean
make build
```

#### Tests Fallando

```bash
# Limpiar y ejecutar tests
make clean
make test
```

### Logs de Error

```bash
# Ver logs de error
grep -i error app.log

# Logs de Spring Boot
grep -i "spring" app.log
```

## 📚 Recursos Adicionales

### Documentación

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [H2 Database](https://www.h2database.com/)
- [Maven Documentation](https://maven.apache.org/guides/)

### Herramientas

- [Postman](https://www.postman.com/) - Testing de API
- [DBeaver](https://dbeaver.io/) - Cliente de base de datos
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - IDE recomendado

### Enlaces Útiles

- [GitHub Repository](https://github.com/Fgeorgescu/tacticore-backend)
- [Issues](https://github.com/Fgeorgescu/tacticore-backend/issues)
- [Documentación](http://localhost:3000)
