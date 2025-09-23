# 🔍 Troubleshooting

> Solución de problemas comunes

## 🚀 Problemas de Inicio

### Error de Puerto en Uso

**Síntoma**: `Port 8080 was already in use`

**Solución**:
```bash
# Verificar procesos en puerto 8080
lsof -i :8080

# Detener aplicación
make stop

# O cambiar puerto en application.yml
server:
  port: 8081
```

### Error de Java

**Síntoma**: `java: command not found`

**Solución**:
```bash
# Verificar versión de Java
java -version

# Configurar JAVA_HOME
export JAVA_HOME=/path/to/java17
export PATH=$JAVA_HOME/bin:$PATH
```

### Error de Maven

**Síntoma**: `mvn: command not found`

**Solución**:
```bash
# Verificar configuración de Maven
mvn -version

# Instalar Maven
brew install maven  # macOS
sudo apt install maven  # Ubuntu
```

## 🗄️ Problemas de Base de Datos

### Error de Conexión H2

**Síntoma**: `Connection refused`

**Solución**:
```bash
# Verificar que la aplicación esté ejecutándose
curl http://localhost:8080/ping

# Verificar configuración en application.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
```

### Error de Datos

**Síntoma**: `No data found`

**Solución**:
```bash
# Limpiar y recargar datos
make db-clear
make db-reload

# Verificar estado de la base de datos
curl http://localhost:8080/api/data/status
```

### Error de H2 Console

**Síntoma**: `404 Not Found` en `/h2-console`

**Solución**:
```bash
# Verificar que H2 console esté habilitada
grep -A 5 "h2:" src/main/resources/application.yml

# Habilitar H2 console
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
```

## 🔧 Problemas de Compilación

### Error de Dependencias

**Síntoma**: `Could not resolve dependencies`

**Solución**:
```bash
# Limpiar y recompilar
make clean
make build

# Actualizar dependencias
mvn dependency:resolve
```

### Error de Tests

**Síntoma**: `Tests failed`

**Solución**:
```bash
# Ejecutar tests específicos
mvn test -Dtest=ApiControllerTest

# Verificar configuración de tests
ls -la src/test/resources/
```

### Error de Maven Shade Plugin

**Síntoma**: `BUILD FAILURE` con Maven Shade Plugin

**Solución**:
```bash
# Usar perfil correcto
mvn clean package -P dev

# O para Lambda
mvn clean package -P lambda
```

## ☁️ Problemas de AWS

### Error de Credenciales

**Síntoma**: `Unable to locate credentials`

**Solución**:
```bash
# Verificar configuración de AWS
aws configure list

# Configurar credenciales
aws configure

# Verificar credenciales
aws sts get-caller-identity
```

### Error de Lambda

**Síntoma**: `Function not found`

**Solución**:
```bash
# Verificar función Lambda
aws lambda get-function --function-name tacticore-backend

# Crear función si no existe
aws lambda create-function \
  --function-name tacticore-backend \
  --runtime java17 \
  --role arn:aws:iam::123456789012:role/lambda-role \
  --handler com.tacticore.lambda.LambdaHandler::handleRequest \
  --zip-file fileb://target/tacticore-backend-1.0.0.jar
```

### Error de API Gateway

**Síntoma**: `403 Forbidden` en API Gateway

**Solución**:
```bash
# Verificar configuración de API Gateway
aws apigateway get-rest-apis

# Verificar integración con Lambda
aws apigateway get-integration \
  --rest-api-id your-api-id \
  --resource-id your-resource-id \
  --http-method ANY
```

## 🐳 Problemas de Docker

### Error de Construcción

**Síntoma**: `docker build failed`

**Solución**:
```bash
# Verificar que el JAR existe
ls -la target/tacticore-backend-1.0.0.jar

# Construir el proyecto primero
make build
make docker-build
```

### Error de Puerto

**Síntoma**: `Port already in use`

**Solución**:
```bash
# Verificar contenedores en ejecución
docker ps

# Usar puerto diferente
docker run -p 8081:8080 tacticore-backend:latest
```

### Error de Permisos

**Síntoma**: `Permission denied`

**Solución**:
```bash
# Verificar permisos de Docker
docker --version

# Agregar usuario al grupo docker
sudo usermod -aG docker $USER
```

## 🧪 Problemas de Testing

### Error de Tests

**Síntoma**: `Tests failed`

**Solución**:
```bash
# Limpiar y ejecutar tests
make clean
make test

# Ejecutar tests específicos
mvn test -Dtest=ApiControllerTest
```

### Error de Cobertura

**Síntoma**: `JaCoCo report not generated`

**Solución**:
```bash
# Generar reporte manualmente
mvn jacoco:report

# Verificar configuración de JaCoCo
grep -A 10 "jacoco" pom.xml
```

### Error de SpotBugs

**Síntoma**: `SpotBugs analysis failed`

**Solución**:
```bash
# Ejecutar SpotBugs manualmente
mvn spotbugs:check

# Verificar configuración
mvn spotbugs:help
```

## 📊 Problemas de CI/CD

### Error de GitHub Actions

**Síntoma**: `Workflow failed`

**Solución**:
```bash
# Verificar logs de GitHub Actions
# Ir a Actions tab en GitHub

# Verificar configuración local
make ci
```

### Error de Despliegue

**Síntoma**: `Deployment failed`

**Solución**:
```bash
# Verificar credenciales de AWS
aws configure list

# Verificar estado de Lambda
aws lambda get-function --function-name tacticore-backend
```

### Error de Terraform

**Síntoma**: `Terraform plan failed`

**Solución**:
```bash
# Verificar estado de Terraform
cd terraform/environments/dev
terraform plan

# Inicializar Terraform
terraform init
```

## 🔍 Problemas de Documentación

### Error de Docsify

**Síntoma**: `404 Not Found` en documentación

**Solución**:
```bash
# Verificar que Docsify esté ejecutándose
make docs-serve

# Verificar archivos de documentación
ls -la docs-site/
```

### Error de Navegación

**Síntoma**: `Sidebar not loading`

**Solución**:
```bash
# Verificar archivo _sidebar.md
cat docs-site/_sidebar.md

# Verificar configuración en index.html
grep -A 5 "loadSidebar" docs-site/index.html
```

## 📚 Recursos de Ayuda

### Logs

```bash
# Ver logs de la aplicación
make logs

# Ver logs de Docker
docker logs tacticore-backend

# Ver logs de Lambda
aws logs tail /aws/lambda/tacticore-backend --follow
```

### Comandos de Diagnóstico

```bash
# Verificar estado general
make status

# Verificar información del proyecto
make info

# Verificar versión
make version
```

### Enlaces de Ayuda

- [Spring Boot Troubleshooting](https://spring.io/guides/gs/actuator-service/)
- [AWS Lambda Troubleshooting](https://docs.aws.amazon.com/lambda/latest/dg/troubleshooting.html)
- [Docker Troubleshooting](https://docs.docker.com/config/troubleshooting/)
- [GitHub Actions Troubleshooting](https://docs.github.com/en/actions/learn-github-actions/debugging-workflows)

## 🆘 Contacto

Si no encuentras la solución a tu problema:

1. **Verificar logs** de la aplicación
2. **Buscar en Issues** de GitHub
3. **Crear un nuevo Issue** con detalles del problema
4. **Incluir logs** y configuración relevante

### Información Útil para Reportar

- **Versión de Java**: `java -version`
- **Versión de Maven**: `mvn -version`
- **Sistema operativo**: `uname -a`
- **Logs de error**: `make logs`
- **Configuración**: `application.yml`
