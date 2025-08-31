#!/bin/bash

# Script de despliegue para AWS Lambda
# Este script compila el proyecto y lo despliega a AWS Lambda

set -e

echo "🚀 Iniciando despliegue a AWS Lambda..."

# Variables de configuración
STACK_NAME="tesis-lambda-stack"
FUNCTION_NAME="tesis-lambda-function"
REGION="us-east-1"  # Cambiar según tu región preferida
BUCKET_NAME="tesis-lambda-deployment-bucket"  # Cambiar por tu bucket S3

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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

print_status "Compilando el proyecto con Maven..."
mvn clean package -DskipTests

# Crear bucket S3 si no existe
print_status "Verificando bucket S3..."
if ! aws s3 ls "s3://$BUCKET_NAME" &> /dev/null; then
    print_warning "Bucket S3 no existe. Creándolo..."
    aws s3 mb "s3://$BUCKET_NAME" --region $REGION
fi

# Subir el JAR a S3
JAR_FILE="target/tesis-lambda-1.0.0.jar"
S3_KEY="lambda-functions/tesis-lambda-$(date +%Y%m%d-%H%M%S).jar"

print_status "Subiendo JAR a S3..."
aws s3 cp "$JAR_FILE" "s3://$BUCKET_NAME/$S3_KEY"

# Crear o actualizar el stack de CloudFormation
print_status "Desplegando stack de CloudFormation..."

# Crear archivo de parámetros temporal
cat > parameters.json << EOF
[
  {
    "ParameterKey": "FunctionName",
    "ParameterValue": "$FUNCTION_NAME"
  },
  {
    "ParameterKey": "Runtime",
    "ParameterValue": "java17"
  },
  {
    "ParameterKey": "Handler",
    "ParameterValue": "com.tesis.lambda.LambdaHandler::handleRequest"
  },
  {
    "ParameterKey": "Timeout",
    "ParameterValue": "30"
  },
  {
    "ParameterKey": "MemorySize",
    "ParameterValue": "512"
  }
]
EOF

# Desplegar stack
if aws cloudformation describe-stacks --stack-name $STACK_NAME &> /dev/null; then
    print_status "Actualizando stack existente..."
    aws cloudformation update-stack \
        --stack-name $STACK_NAME \
        --template-body file://template.yaml \
        --parameters file://parameters.json \
        --capabilities CAPABILITY_IAM \
        --region $REGION
else
    print_status "Creando nuevo stack..."
    aws cloudformation create-stack \
        --stack-name $STACK_NAME \
        --template-body file://template.yaml \
        --parameters file://parameters.json \
        --capabilities CAPABILITY_IAM \
        --region $REGION
fi

# Esperar a que el stack se complete
print_status "Esperando a que el stack se complete..."
aws cloudformation wait stack-create-complete --stack-name $STACK_NAME --region $REGION || \
aws cloudformation wait stack-update-complete --stack-name $STACK_NAME --region $REGION

# Obtener la URL de la API Gateway
print_status "Obteniendo URL de la API Gateway..."
API_URL=$(aws cloudformation describe-stacks \
    --stack-name $STACK_NAME \
    --region $REGION \
    --query 'Stacks[0].Outputs[?OutputKey==`ApiUrl`].OutputValue' \
    --output text)

print_status "✅ Despliegue completado exitosamente!"
print_status "🌐 URL de la API: $API_URL"
print_status "📝 Para probar la función, ejecuta: curl $API_URL"

# Limpiar archivos temporales
rm -f parameters.json

echo ""
print_status "📋 Resumen del despliegue:"
echo "   - Stack Name: $STACK_NAME"
echo "   - Function Name: $FUNCTION_NAME"
echo "   - Region: $REGION"
echo "   - S3 Bucket: $BUCKET_NAME"
echo "   - JAR File: $S3_KEY"
