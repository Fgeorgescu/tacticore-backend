# Configuración para el entorno de desarrollo
aws_region = "us-east-1"

# Configuración de la función Lambda
function_name = "tacticore-backend-function-dev"
runtime       = "java17"
handler       = "com.tacticore.lambda.LambdaHandler::handleRequest"
timeout       = 30
memory_size   = 512

# Configuración de S3
s3_bucket_name = "tacticore-backend-deployment-bucket-dev"
s3_key         = "lambda-functions/tacticore-backend-dev.jar"

# Variables de entorno
environment_variables = {
  ENVIRONMENT = "development"
  LOG_LEVEL   = "DEBUG"
}

# Tags adicionales
tags = {
  Owner       = "tacticore-team"
  CostCenter  = "tacticore-project"
}
