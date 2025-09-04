#!/bin/bash

# Script de despliegue con Terraform
# Este script compila el proyecto y lo despliega usando Terraform

set -e

echo "🚀 Iniciando despliegue con Terraform..."

# Variables de configuración
TERRAFORM_DIR="terraform/environments/dev"
REGION="us-east-1"
FUNCTION_NAME="tacticore-backend-function-dev"
BUCKET_NAME="tacticore-backend-deployment-bucket-dev"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes con colores
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# Verificar que Terraform esté instalado
if ! command -v terraform &> /dev/null; then
    print_error "Terraform no está instalado. Por favor instálalo primero."
    print_warning "Puedes instalarlo con: brew install terraform"
    exit 1
fi

# Verificar que AWS CLI esté instalado
if ! command -v aws &> /dev/null; then
    print_error "AWS CLI no está instalado. Por favor instálalo primero."
    exit 1
fi

# Verificar que estés autenticado en AWS
if ! aws sts get-caller-identity &> /dev/null; then
    print_error "No estás autenticado en AWS. Ejecuta 'aws configure' primero."
    exit 1
fi

# Verificar que Maven esté instalado
if ! command -v mvn &> /dev/null; then
    print_error "Maven no está instalado. Por favor instálalo primero."
    exit 1
fi

print_step "Compilando el proyecto con Maven..."
mvn clean package -DskipTests

# Generar hash del código fuente para detectar cambios
print_step "Generando hash del código fuente..."
SOURCE_HASH=$(find src -name "*.java" -exec sha256sum {} \; | sort | sha256sum | cut -d' ' -f1)
print_status "Hash del código fuente: $SOURCE_HASH"

# Actualizar el archivo de configuración de Terraform con el hash
print_step "Actualizando configuración de Terraform..."
sed -i.bak "s/source_code_hash = \"dummy-hash\"/source_code_hash = \"$SOURCE_HASH\"/" "$TERRAFORM_DIR/variables.tf"

# Navegar al directorio de Terraform
cd "$TERRAFORM_DIR"

print_step "Inicializando Terraform..."
terraform init

print_step "Validando configuración de Terraform..."
terraform validate

print_step "Planificando despliegue..."
terraform plan -out=tfplan

print_warning "¿Deseas continuar con el despliegue? (y/N)"
read -r response
if [[ ! "$response" =~ ^[Yy]$ ]]; then
    print_warning "Despliegue cancelado por el usuario."
    exit 0
fi

print_step "Aplicando configuración de Terraform..."
terraform apply tfplan

# Obtener outputs
print_step "Obteniendo información del despliegue..."
API_URL=$(terraform output -raw api_url)
FUNCTION_ARN=$(terraform output -raw function_arn)
BUCKET_NAME_OUTPUT=$(terraform output -raw s3_bucket_name)

print_status "✅ Despliegue completado exitosamente!"
print_status "🌐 URL de la API: $API_URL"
print_status "🔗 ARN de la función: $FUNCTION_ARN"
print_status "📦 Bucket S3: $BUCKET_NAME_OUTPUT"

# Subir el JAR al bucket S3
print_step "Subiendo JAR al bucket S3..."
JAR_FILE="../../target/tesis-lambda-1.0.0.jar"
S3_KEY="lambda-functions/tesis-lambda-$(date +%Y%m%d-%H%M%S).jar"

aws s3 cp "$JAR_FILE" "s3://$BUCKET_NAME_OUTPUT/$S3_KEY"

print_status "📝 Para probar la función, ejecuta: curl $API_URL"

# Limpiar archivos temporales
rm -f tfplan
rm -f variables.tf.bak

echo ""
print_status "📋 Resumen del despliegue:"
echo "   - Función Lambda: $FUNCTION_NAME"
echo "   - Región: $REGION"
echo "   - Bucket S3: $BUCKET_NAME_OUTPUT"
echo "   - JAR File: $S3_KEY"
echo "   - API URL: $API_URL"
