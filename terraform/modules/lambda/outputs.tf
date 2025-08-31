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
