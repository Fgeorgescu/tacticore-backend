# Terraform - Infrastructure as Code para AWS Lambda

Este directorio contiene la configuración de Terraform para desplegar la infraestructura de AWS Lambda y sus componentes asociados.

## 📁 Estructura del Proyecto Terraform

```
terraform/
├── modules/
│   ├── lambda/
│   │   ├── main.tf          # Recursos principales de Lambda y API Gateway
│   │   ├── variables.tf     # Variables del módulo Lambda
│   │   └── outputs.tf       # Outputs del módulo Lambda
│   └── s3/
│       ├── main.tf          # Recursos de S3
│       ├── variables.tf     # Variables del módulo S3
│       └── outputs.tf       # Outputs del módulo S3
└── environments/
    └── dev/
        ├── main.tf          # Configuración principal del entorno
        ├── variables.tf     # Variables del entorno
        ├── outputs.tf       # Outputs del entorno
        └── terraform.tfvars # Valores de las variables
```

## 🏗️ Módulos Disponibles

### Módulo Lambda (`modules/lambda/`)

**Recursos creados:**
- **Función Lambda**: Con runtime Java 17 y configuración personalizable
- **IAM Role**: Con permisos básicos de ejecución
- **API Gateway**: REST API con endpoint `/hello`
- **Integración**: Entre API Gateway y Lambda
- **Permisos**: Para que API Gateway pueda invocar Lambda

**Variables principales:**
- `function_name`: Nombre de la función Lambda
- `runtime`: Runtime de Java (default: java17)
- `handler`: Handler de la función
- `timeout`: Timeout en segundos (default: 30)
- `memory_size`: Memoria en MB (default: 512)
- `source_code_hash`: Hash para detectar cambios en el código

### Módulo S3 (`modules/s3/`)

**Recursos creados:**
- **Bucket S3**: Para almacenar el código de la función
- **Versionado**: Habilitado para mantener historial
- **Encriptación**: AES256 por defecto
- **Acceso público**: Bloqueado por seguridad
- **Política**: Que requiere encriptación en uploads

**Variables principales:**
- `bucket_name`: Nombre del bucket S3
- `tags`: Tags adicionales para el bucket

## 🚀 Despliegue

### Prerrequisitos

1. **Terraform instalado** (versión >= 1.0)
   ```bash
   # Instalar Terraform (macOS)
   brew install terraform
   
   # Verificar instalación
   terraform version
   ```

2. **AWS CLI configurado**
   ```bash
   aws configure
   ```

3. **Maven instalado**
   ```bash
   mvn -version
   ```

### Despliegue Automatizado

El proyecto incluye un script que automatiza todo el proceso:

```bash
# Dar permisos de ejecución
chmod +x deploy-terraform.sh

# Ejecutar despliegue
./deploy-terraform.sh
```

**¿Qué hace el script?**
- ✅ Compila el proyecto con Maven
- ✅ Genera hash del código fuente
- ✅ Actualiza configuración de Terraform
- ✅ Inicializa Terraform
- ✅ Valida la configuración
- ✅ Planifica el despliegue
- ✅ Aplica la configuración
- ✅ Sube el JAR a S3
- ✅ Muestra la URL de la API

### Despliegue Manual

Si prefieres controlar cada paso:

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

## ⚙️ Configuración

### Variables del Entorno

Edita `terraform/environments/dev/terraform.tfvars` para personalizar:

```hcl
# Región de AWS
aws_region = "us-east-1"

# Configuración de Lambda
function_name = "tesis-lambda-function-dev"
runtime       = "java17"
timeout       = 30
memory_size   = 512

# Configuración de S3
s3_bucket_name = "tesis-lambda-deployment-bucket-dev"

# Variables de entorno
environment_variables = {
  ENVIRONMENT = "development"
  LOG_LEVEL   = "DEBUG"
}
```

### Múltiples Entornos

Para crear otros entornos (staging, production):

```bash
# Copiar configuración de desarrollo
cp -r terraform/environments/dev terraform/environments/staging
cp -r terraform/environments/dev terraform/environments/production

# Editar variables específicas del entorno
# terraform/environments/staging/terraform.tfvars
# terraform/environments/production/terraform.tfvars
```

## 📊 Monitoreo y Logs

### Ver Estado de Terraform
```bash
cd terraform/environments/dev
terraform show
```

### Ver Outputs
```bash
terraform output
```

### Ver Logs de Lambda
```bash
# Obtener nombre de la función
FUNCTION_NAME=$(terraform output -raw function_name)

# Ver logs en tiempo real
aws logs tail /aws/lambda/$FUNCTION_NAME --follow --region us-east-1
```

## 🛠️ Comandos Útiles

### Planificar Cambios
```bash
terraform plan -out=tfplan
```

### Aplicar Cambios Específicos
```bash
terraform apply -target=module.lambda_function
```

### Destruir Recursos
```bash
terraform destroy
```

### Importar Recursos Existentes
```bash
terraform import aws_lambda_function.function arn:aws:lambda:us-east-1:123456789012:function:existing-function
```

## 🔧 Troubleshooting

### Error: "Provider configuration not found"
```bash
terraform init
```

### Error: "Invalid configuration"
```bash
terraform validate
```

### Error: "Access Denied"
- Verificar permisos de AWS
- Verificar que las credenciales estén configuradas

### Error: "Bucket already exists"
- Cambiar el nombre del bucket en `terraform.tfvars`
- Los nombres de bucket deben ser únicos globalmente

## 📝 Ventajas de Terraform vs CloudFormation

### ✅ Ventajas de Terraform:
- **Sintaxis más clara**: HCL es más legible que JSON/YAML
- **Estado local**: Mejor control del estado de la infraestructura
- **Módulos reutilizables**: Fácil reutilización de código
- **Mejor manejo de dependencias**: Dependencias implícitas
- **Comunidad activa**: Más providers y módulos disponibles
- **Versionado**: Mejor integración con Git

### 🔄 Migración desde CloudFormation:
Si ya tienes recursos creados con CloudFormation, puedes:

1. **Importar recursos existentes** a Terraform
2. **Crear nueva infraestructura** con Terraform
3. **Migrar gradualmente** recursos existentes

## 🗑️ Limpieza

Para eliminar todos los recursos creados por Terraform:

```bash
cd terraform/environments/dev
terraform destroy
```

**⚠️ Advertencia:** Este comando eliminará todos los recursos creados por Terraform.

## 📚 Recursos Adicionales

- [Terraform Documentation](https://www.terraform.io/docs)
- [AWS Provider Documentation](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Terraform Best Practices](https://www.terraform.io/docs/cloud/guides/recommended-practices/index.html)
- [AWS Lambda with Terraform](https://learn.hashicorp.com/tutorials/terraform/aws-lambda)
