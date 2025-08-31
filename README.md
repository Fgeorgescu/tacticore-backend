# Tesis - Proyecto Spring Boot con AWS Lambda

Este proyecto implementa una aplicación Spring Boot que puede ejecutarse tanto localmente como en AWS Lambda. Incluye un endpoint simple que devuelve "Hello World" y está configurado para ser desplegado fácilmente en AWS.

## 🏗️ Arquitectura

- **Framework**: Spring Boot 3.2.0
- **Java**: JDK 17
- **Build Tool**: Maven
- **Cloud**: AWS Lambda + API Gateway
- **Infrastructure as Code**: AWS CloudFormation

## 📁 Estructura del Proyecto

```
tesis/
├── src/
│   ├── main/
│   │   ├── java/com/tesis/lambda/
│   │   │   ├── LambdaApplication.java      # Clase principal de Spring Boot
│   │   │   ├── LambdaHandler.java         # Handler para AWS Lambda
│   │   │   └── HelloController.java       # Controlador REST
│   │   └── resources/
│   │       └── application.yml            # Configuración de Spring Boot
│   └── test/
│       └── java/com/tesis/lambda/         # Tests unitarios
├── pom.xml                                # Configuración de Maven
├── template.yaml                          # Template de CloudFormation
├── deploy.sh                              # Script de despliegue automatizado
└── README.md                              # Este archivo
```

## 🚀 Despliegue a AWS

### Prerrequisitos

1. **AWS CLI instalado y configurado**
   ```bash
   # Instalar AWS CLI (macOS)
   brew install awscli
   
   # Configurar credenciales
   aws configure
   ```

2. **Java 17 instalado**
   ```bash
   # Verificar versión de Java
   java -version
   ```

3. **Maven instalado**
   ```bash
   # Instalar Maven (macOS)
   brew install maven
   
   # Verificar instalación
   mvn -version
   ```

### Opción 1: Despliegue Automatizado (Recomendado)

El proyecto incluye un script de despliegue automatizado que maneja todo el proceso:

```bash
# Dar permisos de ejecución al script
chmod +x deploy.sh

# Ejecutar el despliegue
./deploy.sh
```

**¿Qué hace el script?**
- ✅ Compila el proyecto con Maven
- ✅ Crea un bucket S3 para almacenar el código
- ✅ Sube el JAR compilado a S3
- ✅ Despliega la infraestructura con CloudFormation
- ✅ Configura API Gateway automáticamente
- ✅ Muestra la URL final de la API

### Opción 2: Despliegue Manual

Si prefieres controlar cada paso del proceso:

#### Paso 1: Compilar el Proyecto
```bash
mvn clean package -DskipTests
```

#### Paso 2: Crear Bucket S3
```bash
aws s3 mb s3://tu-bucket-nombre-unico --region us-east-1
```

#### Paso 3: Subir Código a S3
```bash
aws s3 cp target/tesis-lambda-1.0.0.jar s3://tu-bucket-nombre-unico/lambda-function.jar
```

#### Paso 4: Desplegar con CloudFormation
```bash
aws cloudformation create-stack \
    --stack-name tesis-lambda-stack \
    --template-body file://template.yaml \
    --capabilities CAPABILITY_IAM \
    --region us-east-1
```

#### Paso 5: Verificar Despliegue
```bash
aws cloudformation describe-stacks \
    --stack-name tesis-lambda-stack \
    --region us-east-1
```

### Configuración Personalizada

Antes de desplegar, puedes modificar las siguientes variables en `deploy.sh`:

```bash
STACK_NAME="tesis-lambda-stack"           # Nombre del stack de CloudFormation
FUNCTION_NAME="tesis-lambda-function"     # Nombre de la función Lambda
REGION="us-east-1"                        # Región de AWS
BUCKET_NAME="tesis-lambda-deployment-bucket" # Nombre del bucket S3
```

## 🧪 Pruebas

### Prueba Local
```bash
# Ejecutar la aplicación localmente
mvn spring-boot:run

# Probar el endpoint
curl http://localhost:8080/hello
```

### Prueba en AWS Lambda
```bash
# Obtener la URL de la API Gateway
aws cloudformation describe-stacks \
    --stack-name tesis-lambda-stack \
    --region us-east-1 \
    --query 'Stacks[0].Outputs[?OutputKey==`ApiUrl`].OutputValue' \
    --output text

# Probar el endpoint
curl https://tu-api-gateway-url.amazonaws.com/hello
```

## 📊 Monitoreo

### Ver Logs de Lambda
```bash
# Ver logs en tiempo real
aws logs tail /aws/lambda/tesis-lambda-function --follow --region us-east-1
```

### Métricas de CloudWatch
- Ve a la consola de AWS CloudWatch
- Navega a "Métricas" > "Lambda"
- Selecciona tu función para ver métricas de invocación, duración, errores, etc.

## 🔧 Desarrollo

### Agregar Nuevos Endpoints

1. Crea un nuevo controlador en `src/main/java/com/tesis/lambda/`
2. Agrega los métodos necesarios
3. Actualiza `LambdaHandler.java` para manejar las nuevas rutas

### Ejemplo de Nuevo Endpoint
```java
@RestController
public class UserController {
    
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}
```

## 🛠️ Troubleshooting

### Error: "Handler not found"
- Verifica que el handler en `template.yaml` coincida con la clase `LambdaHandler`
- Asegúrate de que el JAR se haya subido correctamente a S3

### Error: "Timeout"
- Aumenta el timeout en `template.yaml` (máximo 15 minutos)
- Optimiza el código para reducir el tiempo de ejecución

### Error: "Out of Memory"
- Aumenta la memoria asignada en `template.yaml`
- Optimiza el uso de memoria en el código

### Error: "Permission Denied"
- Verifica que el rol IAM tenga los permisos necesarios
- Asegúrate de que las credenciales de AWS estén configuradas correctamente

## 📝 Notas Importantes

- **Cold Start**: La primera invocación puede tardar más tiempo debido al cold start
- **Timeout**: El timeout máximo para Lambda es 15 minutos
- **Memoria**: Más memoria = más CPU = mejor rendimiento
- **Región**: Elige la región más cercana a tus usuarios para reducir latencia

## 🗑️ Limpieza

Para eliminar todos los recursos de AWS:

```bash
# Eliminar el stack de CloudFormation
aws cloudformation delete-stack --stack-name tesis-lambda-stack --region us-east-1

# Eliminar el bucket S3 (debe estar vacío)
aws s3 rb s3://tu-bucket-nombre-unico --force
```

## 📚 Recursos Adicionales

- [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html)
- [Maven Documentation](https://maven.apache.org/guides/)