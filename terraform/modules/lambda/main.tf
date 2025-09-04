# Variables del módulo
variable "function_name" {
  description = "Nombre de la función Lambda"
  type        = string
}

variable "runtime" {
  description = "Runtime de la función Lambda"
  type        = string
  default     = "java17"
}

variable "handler" {
  description = "Handler de la función Lambda"
  type        = string
  default     = "com.tesis.lambda.LambdaHandler::handleRequest"
}

variable "timeout" {
  description = "Timeout de la función Lambda en segundos"
  type        = number
  default     = 30
}

variable "memory_size" {
  description = "Memoria asignada a la función Lambda en MB"
  type        = number
  default     = 512
}

variable "source_code_hash" {
  description = "Hash del código fuente para detectar cambios"
  type        = string
}

variable "s3_bucket" {
  description = "Bucket S3 donde se almacena el código de la función"
  type        = string
}

variable "s3_key" {
  description = "Clave S3 del archivo JAR de la función"
  type        = string
}

variable "environment_variables" {
  description = "Variables de entorno para la función Lambda"
  type        = map(string)
  default     = {}
}

variable "tags" {
  description = "Tags para los recursos"
  type        = map(string)
  default     = {}
}

# Data sources
data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

# IAM Role para la función Lambda
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

  tags = var.tags
}

# Política básica de ejecución para Lambda
resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# Función Lambda
resource "aws_lambda_function" "function" {
  filename         = "dummy.zip"  # Se actualizará con el JAR real
  function_name    = var.function_name
  role            = aws_iam_role.lambda_role.arn
  handler         = var.handler
  runtime         = var.runtime
  timeout         = var.timeout
  memory_size     = var.memory_size
  source_code_hash = var.source_code_hash

  environment {
    variables = var.environment_variables
  }

  tags = var.tags
}

# API Gateway
resource "aws_api_gateway_rest_api" "api" {
  name        = "${var.function_name}-api"
  description = "API Gateway para ${var.function_name}"

  tags = var.tags
}

# Recurso raíz de API Gateway
resource "aws_api_gateway_resource" "root" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "hello"
}

# Método GET para el endpoint /hello
resource "aws_api_gateway_method" "get" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.root.id
  http_method   = "GET"
  authorization = "NONE"
}

# Integración con Lambda para /hello
resource "aws_api_gateway_integration" "lambda_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.root.id
  http_method = aws_api_gateway_method.get.http_method

  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = aws_lambda_function.function.invoke_arn
}

# Recurso /api
resource "aws_api_gateway_resource" "api" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "api"
}

# Recurso /api/health
resource "aws_api_gateway_resource" "health" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_resource.api.id
  path_part   = "health"
}

# Método GET para /api/health
resource "aws_api_gateway_method" "health_get" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.health.id
  http_method   = "GET"
  authorization = "NONE"
}

# Integración con Lambda para /api/health
resource "aws_api_gateway_integration" "health_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.health.id
  http_method = aws_api_gateway_method.health_get.http_method

  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = aws_lambda_function.function.invoke_arn
}

# Recurso /api/matches
resource "aws_api_gateway_resource" "matches" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_resource.api.id
  path_part   = "matches"
}

# Método POST para /api/matches
resource "aws_api_gateway_method" "matches_post" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.matches.id
  http_method   = "POST"
  authorization = "NONE"
}

# Integración con Lambda para /api/matches POST
resource "aws_api_gateway_integration" "matches_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.matches.id
  http_method = aws_api_gateway_method.matches_post.http_method

  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = aws_lambda_function.function.invoke_arn
}

# Recurso /api/matches/{matchId}
resource "aws_api_gateway_resource" "match_id" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_resource.matches.id
  path_part   = "{matchId}"
}

# Método GET para /api/matches/{matchId}
resource "aws_api_gateway_method" "match_id_get" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.match_id.id
  http_method   = "GET"
  authorization = "NONE"
}

# Integración con Lambda para /api/matches/{matchId} GET
resource "aws_api_gateway_integration" "match_id_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.match_id.id
  http_method = aws_api_gateway_method.match_id_get.http_method

  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = aws_lambda_function.function.invoke_arn
}

# Deployment de API Gateway
resource "aws_api_gateway_deployment" "deployment" {
  depends_on = [
    aws_api_gateway_integration.lambda_integration
  ]

  rest_api_id = aws_api_gateway_rest_api.api.id
  stage_name  = "prod"

  lifecycle {
    create_before_destroy = true
  }
}

# Permisos para que API Gateway invoque Lambda
resource "aws_lambda_permission" "api_gateway" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.api.execution_arn}/*/*/*"
}

# Outputs
output "function_arn" {
  description = "ARN de la función Lambda"
  value       = aws_lambda_function.function.arn
}

output "function_name" {
  description = "Nombre de la función Lambda"
  value       = aws_lambda_function.function.function_name
}

output "api_url" {
  description = "URL de la API Gateway"
  value       = "${aws_api_gateway_deployment.deployment.invoke_url}/hello"
}

output "api_id" {
  description = "ID de la API Gateway"
  value       = aws_api_gateway_rest_api.api.id
}

output "role_arn" {
  description = "ARN del rol IAM de la función Lambda"
  value       = aws_iam_role.lambda_role.arn
}
