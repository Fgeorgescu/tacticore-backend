output "function_arn" {
  description = "ARN de la función Lambda"
  value       = module.lambda_function.function_arn
}

output "function_name" {
  description = "Nombre de la función Lambda"
  value       = module.lambda_function.function_name
}

output "api_url" {
  description = "URL de la API Gateway"
  value       = module.lambda_function.api_url
}

output "api_id" {
  description = "ID de la API Gateway"
  value       = module.lambda_function.api_id
}

output "s3_bucket_name" {
  description = "Nombre del bucket S3"
  value       = module.s3_bucket.bucket_name
}

output "s3_bucket_arn" {
  description = "ARN del bucket S3"
  value       = module.s3_bucket.bucket_arn
}
