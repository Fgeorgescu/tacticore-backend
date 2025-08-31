# Configuración para el entorno de desarrollo
aws_region = "us-east-1"

# Configuración de la función Lambda
function_name = "tesis-lambda-function-dev"
runtime       = "java17"
handler       = "com.tesis.lambda.LambdaHandler::handleRequest"
timeout       = 30
memory_size   = 512

# Configuración de S3
s3_bucket_name = "tesis-lambda-deployment-bucket-dev"
s3_key         = "lambda-functions/tesis-lambda-dev.jar"

# Variables de entorno
environment_variables = {
  ENVIRONMENT = "development"
  LOG_LEVEL   = "DEBUG"
}

# Tags adicionales
tags = {
  Owner       = "tesis-team"
  CostCenter  = "tesis-project"
}
