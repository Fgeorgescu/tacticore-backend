variable "bucket_name" {
  description = "Nombre del bucket S3"
  type        = string
}

variable "tags" {
  description = "Tags para el bucket S3"
  type        = map(string)
  default     = {}
}
