# 🏗️ Terraform

> Configuración de infraestructura como código

## 📁 Estructura del Proyecto

```
terraform/
├── environments/
│   └── dev/
│       ├── main.tf
│       ├── variables.tf
│       ├── outputs.tf
│       └── terraform.tfvars
└── modules/
    ├── lambda/
    │   ├── main.tf
    │   ├── variables.tf
    │   └── outputs.tf
    └── s3/
        ├── main.tf
        ├── variables.tf
        └── outputs.tf
```

## 🔧 Configuración

### Variables Principales

```hcl
# variables.tf
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "lambda_function_name" {
  description = "Name of the Lambda function"
  type        = string
  default     = "tacticore-backend"
}

variable "api_gateway_name" {
  description = "Name of the API Gateway"
  type        = string
  default     = "tacticore-api"
}
```

### Configuración Principal

```hcl
# main.tf
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Lambda Function
module "lambda" {
  source = "../../modules/lambda"
  
  function_name = var.lambda_function_name
  environment   = var.environment
}

# API Gateway
module "api_gateway" {
  source = "../../modules/api_gateway"
  
  api_name      = var.api_gateway_name
  lambda_arn    = module.lambda.function_arn
  environment   = var.environment
}

# S3 Bucket
module "s3" {
  source = "../../modules/s3"
  
  bucket_name = "${var.lambda_function_name}-${var.environment}"
  environment = var.environment
}
```

## 🚀 Comandos de Terraform

### Inicialización

```bash
# Inicializar Terraform
make terraform-init

# O manualmente
cd terraform/environments/dev
terraform init
```

### Planificación

```bash
# Ver plan de cambios
make terraform-plan

# O manualmente
terraform plan
```

### Aplicación

```bash
# Aplicar cambios
make terraform-apply

# O manualmente
terraform apply
```

### Destrucción

```bash
# Destruir infraestructura
make terraform-destroy

# O manualmente
terraform destroy
```

## 📊 Outputs

### Outputs Principales

```hcl
# outputs.tf
output "lambda_function_arn" {
  description = "ARN of the Lambda function"
  value       = module.lambda.function_arn
}

output "api_gateway_url" {
  description = "URL of the API Gateway"
  value       = module.api_gateway.api_url
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket"
  value       = module.s3.bucket_name
}
```

## 🔧 Módulos

### Módulo Lambda

```hcl
# modules/lambda/main.tf
resource "aws_lambda_function" "main" {
  function_name = var.function_name
  role         = aws_iam_role.lambda_role.arn
  handler      = "com.tacticore.lambda.LambdaHandler::handleRequest"
  runtime      = "java17"
  timeout      = 30
  memory_size  = 512

  filename         = var.filename
  source_code_hash = filebase64sha256(var.filename)

  environment {
    variables = {
      SPRING_PROFILES_ACTIVE = "lambda"
    }
  }
}

resource "aws_iam_role" "lambda_role" {
  name = "${var.function_name}-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
```

### Módulo API Gateway

```hcl
# modules/api_gateway/main.tf
resource "aws_api_gateway_rest_api" "main" {
  name        = var.api_name
  description = "API Gateway for ${var.api_name}"
}

resource "aws_api_gateway_resource" "proxy" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_rest_api.main.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "proxy" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.proxy.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_method.proxy.resource_id
  http_method = aws_api_gateway_method.proxy.http_method

  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = var.lambda_arn
}

resource "aws_api_gateway_deployment" "main" {
  depends_on = [
    aws_api_gateway_integration.lambda,
  ]

  rest_api_id = aws_api_gateway_rest_api.main.id
  stage_name  = var.environment
}
```

### Módulo S3

```hcl
# modules/s3/main.tf
resource "aws_s3_bucket" "main" {
  bucket = var.bucket_name
}

resource "aws_s3_bucket_versioning" "main" {
  bucket = aws_s3_bucket.main.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "main" {
  bucket = aws_s3_bucket.main.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}
```

## 🔍 Troubleshooting

### Problemas Comunes

#### Error de Credenciales

```bash
# Verificar configuración de AWS
aws configure list

# Verificar credenciales
aws sts get-caller-identity
```

#### Error de Estado

```bash
# Verificar estado de Terraform
terraform state list

# Importar recurso existente
terraform import aws_lambda_function.main function-name
```

#### Error de Plan

```bash
# Refrescar estado
terraform refresh

# Verificar configuración
terraform validate
```

## 📚 Recursos Adicionales

### Documentación

- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [AWS Lambda with Terraform](https://learn.hashicorp.com/tutorials/terraform/lambda-api-gateway)
- [Terraform Best Practices](https://www.terraform.io/docs/cloud/guides/recommended-practices/index.html)

### Herramientas

- [Terraform CLI](https://www.terraform.io/downloads.html)
- [Terraform Cloud](https://app.terraform.io/)
- [AWS CLI](https://aws.amazon.com/cli/)

### Enlaces Útiles

- [Terraform Registry](https://registry.terraform.io/)
- [AWS Console](https://console.aws.amazon.com/)
- [Terraform Documentation](https://www.terraform.io/docs/)
