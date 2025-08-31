variable "aws_region" {
  description = "Región de AWS donde se desplegarán los recursos"
  type        = string
  default     = "us-east-1"
}

variable "function_name" {
  description = "Nombre de la función Lambda"
  type        = string
  default     = "tesis-lambda-function"
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
  default     = "dummy-hash"
}

variable "s3_bucket_name" {
  description = "Nombre del bucket S3 para almacenar el código"
  type        = string
  default     = "tesis-lambda-deployment-bucket"
}

variable "s3_key" {
  description = "Clave S3 del archivo JAR de la función"
  type        = string
  default     = "lambda-functions/tesis-lambda.jar"
}

variable "environment_variables" {
  description = "Variables de entorno para la función Lambda"
  type        = map(string)
  default     = {}
}

variable "tags" {
  description = "Tags adicionales para los recursos"
  type        = map(string)
  default     = {}
}
