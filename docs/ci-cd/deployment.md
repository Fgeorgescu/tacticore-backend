# 🚀 Despliegue

> Estrategias y procesos de despliegue

## 🎯 Estrategias de Despliegue

### Despliegue en AWS Lambda

```bash
# Empaquetar para Lambda
make lambda-package

# Desplegar con Terraform
make terraform-apply
```

### Despliegue con Docker

```bash
# Construir imagen
make docker-build

# Ejecutar contenedor
make docker-run
```

## 🔧 Configuración

### Variables de Entorno

```bash
# AWS Configuration
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key

# Application Configuration
SPRING_PROFILES_ACTIVE=lambda
SERVER_PORT=8080
```

### Perfiles de Spring

- **dev** - Desarrollo local
- **lambda** - AWS Lambda
- **docker** - Contenedores
- **prod** - Producción

## 🚀 Procesos de Despliegue

### 1. Desarrollo Local

```bash
# Iniciar en modo desarrollo
make dev

# La aplicación estará disponible en:
# http://localhost:8080
```

### 2. AWS Lambda

```bash
# Empaquetar para Lambda
make lambda-package

# Desplegar con Terraform
make terraform-apply

# Verificar despliegue
aws lambda get-function --function-name tacticore-backend
```

### 3. Docker

```bash
# Construir imagen
make docker-build

# Ejecutar contenedor
make docker-run

# Con Docker Compose
make docker-compose-up
```

## 📊 Monitoreo

### Logs

```bash
# Ver logs de Lambda
aws logs tail /aws/lambda/tacticore-backend --follow

# Ver logs de Docker
docker logs tacticore-backend
```

### Métricas

```bash
# Ver métricas de Lambda
aws cloudwatch get-metric-statistics \
  --namespace AWS/Lambda \
  --metric-name Duration \
  --dimensions Name=FunctionName,Value=tacticore-backend
```

## 🔍 Troubleshooting

### Problemas Comunes

#### Error de Despliegue

```bash
# Verificar credenciales de AWS
aws configure list

# Verificar estado de Lambda
aws lambda get-function --function-name tacticore-backend
```

#### Error de Docker

```bash
# Verificar que Docker esté ejecutándose
docker --version

# Verificar imágenes
docker images
```

#### Error de Terraform

```bash
# Verificar estado de Terraform
cd terraform/environments/dev
terraform plan
```

## 📚 Recursos Adicionales

### Documentación

- [AWS Lambda](https://docs.aws.amazon.com/lambda/)
- [Docker](https://docs.docker.com/)
- [Terraform](https://www.terraform.io/docs/)

### Herramientas

- [AWS CLI](https://aws.amazon.com/cli/)
- [Docker](https://www.docker.com/)
- [Terraform](https://www.terraform.io/)

### Enlaces Útiles

- [AWS Console](https://console.aws.amazon.com/)
- [Docker Hub](https://hub.docker.com/)
- [Terraform Registry](https://registry.terraform.io/)
