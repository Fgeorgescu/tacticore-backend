# Tesis - Proyecto Spring Boot con AWS Lambda

Este proyecto implementa una aplicación Spring Boot que puede ejecutarse tanto localmente como en AWS Lambda. Incluye un endpoint simple que devuelve "Hello World" y está configurado para ser desplegado fácilmente en AWS usando **CloudFormation** o **Terraform**.

## 🏗️ Arquitectura

- **Framework**: Spring Boot 3.2.0
- **Java**: JDK 17
- **Build Tool**: Maven
- **Cloud**: AWS Lambda + API Gateway
- **Infrastructure as Code**: AWS CloudFormation **Y** Terraform

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
├── terraform/                              # 🆕 Configuración de Terraform
│   ├── modules/
│   │   ├── lambda/                        # Módulo para Lambda y API Gateway
│   │   └── s3/                            # Módulo para bucket S3
│   └── environments/
│       └── dev/                           # Configuración del entorno de desarrollo
├── pom.xml                                # Configuración de Maven
├── template.yaml                          # Template de CloudFormation
├── deploy.sh                              # Script de despliegue con CloudFormation
├── deploy-terraform.sh                   # 🆕 Script de despliegue con Terraform
└── README.md                              # Este archivo
```

## 🚀 Opciones de Despliegue

### Opción 1: Terraform (Recomendado) 🆕

**Ventajas:**
- ✅ Sintaxis más clara y legible
- ✅ Módulos reutilizables
- ✅ Mejor manejo de dependencias
- ✅ Estado local controlado
- ✅ Comunidad activa

**Despliegue con Terraform:**
```bash
# Dar permisos de ejecución
chmod +x deploy-terraform.sh

# Ejecutar despliegue
./deploy-terraform.sh
```

**Ver documentación completa:** [terraform/README.md](terraform/README.md)

### Opción 2: CloudFormation

**Despliegue con CloudFormation:**
```bash
# Dar permisos de ejecución
chmod +x deploy.sh

# Ejecutar despliegue
./deploy.sh
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

4. **Terraform instalado** (solo para despliegue con Terraform)
   ```bash
   # Instalar Terraform (macOS)
   brew install terraform
   
   # Verificar instalación
   terraform version
   ```

### Despliegue Automatizado

#### Con Terraform (Recomendado)
```bash
./deploy-terraform.sh
```

#### Con CloudFormation
```bash
./deploy.sh
```

**¿Qué hacen los scripts?**
- ✅ Compilan el proyecto con Maven
- ✅ Crean bucket S3 para almacenar el código
- ✅ Suben el JAR compilado a S3
- ✅ Despliegan la infraestructura (Terraform/CloudFormation)
- ✅ Configuran API Gateway automáticamente
- ✅ Muestran la URL final de la API

### Despliegue Manual

#### Con Terraform
```bash
# 1. Compilar el proyecto
mvn clean package -DskipTests

# 2. Navegar al directorio de Terraform
cd terraform/environments/dev

# 3. Inicializar Terraform
terraform init

# 4. Validar configuración
terraform validate

# 5. Planificar despliegue
terraform plan

# 6. Aplicar configuración
terraform apply
```

#### Con CloudFormation
```bash
# 1. Compilar el proyecto
mvn clean package -DskipTests

# 2. Crear bucket S3
aws s3 mb s3://tu-bucket-nombre-unico --region us-east-1

# 3. Subir código a S3
aws s3 cp target/tesis-lambda-1.0.0.jar s3://tu-bucket-nombre-unico/lambda-function.jar

# 4. Desplegar con CloudFormation
aws cloudformation create-stack \
    --stack-name tesis-lambda-stack \
    --template-body file://template.yaml \
    --capabilities CAPABILITY_IAM \
    --region us-east-1
```

### Configuración Personalizada

#### Terraform
Edita `terraform/environments/dev/terraform.tfvars`:
```hcl
aws_region = "us-east-1"
function_name = "tesis-lambda-function-dev"
s3_bucket_name = "tesis-lambda-deployment-bucket-dev"
```

#### CloudFormation
Edita las variables en `deploy.sh`:
```bash
STACK_NAME="tesis-lambda-stack"
FUNCTION_NAME="tesis-lambda-function"
REGION="us-east-1"
BUCKET_NAME="tesis-lambda-deployment-bucket"
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

#### Con Terraform
```bash
# Obtener la URL de la API Gateway
cd terraform/environments/dev
terraform output api_url

# Probar el endpoint
curl $(terraform output -raw api_url)
```

#### Con CloudFormation
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
# Con Terraform
FUNCTION_NAME=$(cd terraform/environments/dev && terraform output -raw function_name)
aws logs tail /aws/lambda/$FUNCTION_NAME --follow --region us-east-1

# Con CloudFormation
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
- Verifica que el handler coincida con la clase `LambdaHandler`
- Asegúrate de que el JAR se haya subido correctamente a S3

### Error: "Timeout"
- Aumenta el timeout en la configuración
- Optimiza el código para reducir el tiempo de ejecución

### Error: "Out of Memory"
- Aumenta la memoria asignada
- Optimiza el uso de memoria en el código

### Error: "Permission Denied"
- Verifica que el rol IAM tenga los permisos necesarios
- Asegúrate de que las credenciales de AWS estén configuradas correctamente

### Error: "Bucket already exists" (Terraform)
- Cambiar el nombre del bucket en `terraform.tfvars`
- Los nombres de bucket deben ser únicos globalmente

## 📝 Notas Importantes

- **Cold Start**: La primera invocación puede tardar más tiempo debido al cold start
- **Timeout**: El timeout máximo para Lambda es 15 minutos
- **Memoria**: Más memoria = más CPU = mejor rendimiento
- **Región**: Elige la región más cercana a tus usuarios para reducir latencia
- **Terraform vs CloudFormation**: Ambos son válidos, Terraform ofrece más flexibilidad

## 🗑️ Limpieza

### Con Terraform
```bash
cd terraform/environments/dev
terraform destroy
```

### Con CloudFormation
```bash
# Eliminar el stack de CloudFormation
aws cloudformation delete-stack --stack-name tesis-lambda-stack --region us-east-1

# Eliminar el bucket S3 (debe estar vacío)
aws s3 rb s3://tu-bucket-nombre-unico --force
```

## 📚 Recursos Adicionales

- [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Terraform Documentation](https://www.terraform.io/docs)
- [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html)
- [Maven Documentation](https://maven.apache.org/guides/)