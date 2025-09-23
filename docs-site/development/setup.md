# 🔧 Configuración

> Guía de configuración del entorno de desarrollo

## 📋 Prerrequisitos

### Software Requerido

- **Java 17** o superior
- **Maven 3.9+**
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)

### Herramientas Opcionales

- **Docker** (para contenedores)
- **AWS CLI** (para despliegue)
- **Terraform** (para infraestructura)

## 🚀 Instalación

### 1. Clonar Repositorio

```bash
git clone https://github.com/Fgeorgescu/tacticore-backend.git
cd tacticore-backend
```

### 2. Verificar Prerrequisitos

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar Git
git --version
```

### 3. Configurar IDE

#### IntelliJ IDEA

1. **Importar Proyecto**
   - File → Open → Seleccionar carpeta del proyecto
   - Seleccionar "Import project from external model" → Maven

2. **Configurar JDK**
   - File → Project Structure → Project
   - Project SDK: Java 17
   - Project language level: 17

3. **Configurar Maven**
   - File → Settings → Build, Execution, Deployment → Build Tools → Maven
   - Maven home directory: Usar Maven embebido o especificar ruta

#### Eclipse

1. **Importar Proyecto**
   - File → Import → Maven → Existing Maven Projects
   - Seleccionar carpeta del proyecto

2. **Configurar JDK**
   - Project → Properties → Java Build Path → Libraries
   - Agregar JRE System Library (Java 17)

#### VS Code

1. **Instalar Extensiones**
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Maven for Java

2. **Configurar Java**
   - Ctrl+Shift+P → Java: Configure Java Runtime
   - Seleccionar Java 17

## 🔧 Configuración del Proyecto

### Variables de Entorno

```bash
# Crear archivo .env (opcional)
echo "SPRING_PROFILES_ACTIVE=dev" > .env
echo "SERVER_PORT=8080" >> .env
```

### Configuración de Maven

```xml
<!-- Verificar en pom.xml -->
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

### Configuración de Spring Boot

```yaml
# src/main/resources/application.yml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

server:
  port: 8080

logging:
  level:
    com.tacticore: DEBUG
    org.springframework: INFO
```

## 🧪 Verificación

### 1. Compilar Proyecto

```bash
make build
```

### 2. Ejecutar Tests

```bash
make test
```

### 3. Iniciar Aplicación

```bash
make run
```

### 4. Verificar Funcionamiento

```bash
# Health check
curl http://localhost:8080/ping

# Información del proyecto
curl http://localhost:8080/api/health
```

## 🔍 Troubleshooting

### Problemas Comunes

#### Error de Java

```bash
# Verificar versión de Java
java -version

# Configurar JAVA_HOME
export JAVA_HOME=/path/to/java17
export PATH=$JAVA_HOME/bin:$PATH
```

#### Error de Maven

```bash
# Verificar configuración de Maven
mvn -version

# Limpiar caché de Maven
mvn dependency:purge-local-repository
```

#### Error de Puerto

```bash
# Verificar procesos en puerto 8080
lsof -i :8080

# Cambiar puerto en application.yml
server:
  port: 8081
```

#### Error de Base de Datos

```bash
# Verificar que H2 esté configurada
curl http://localhost:8080/h2-console

# Verificar logs de la aplicación
make logs
```

## 📚 Recursos Adicionales

### Documentación

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Java Documentation](https://docs.oracle.com/en/java/)

### Herramientas

- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Eclipse](https://www.eclipse.org/)
- [VS Code](https://code.visualstudio.com/)

### Enlaces Útiles

- [Spring Boot Guides](https://spring.io/guides)
- [Maven Central Repository](https://search.maven.org/)
- [Java Documentation](https://docs.oracle.com/en/java/)
