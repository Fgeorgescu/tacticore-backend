output "bucket_name" {
  description = "Nombre del bucket S3"
  value       = aws_s3_bucket.bucket.bucket
}

output "bucket_arn" {
  description = "ARN del bucket S3"
  value       = aws_s3_bucket.bucket.arn
}

output "bucket_id" {
  description = "ID del bucket S3"
  value       = aws_s3_bucket.bucket.id
}
