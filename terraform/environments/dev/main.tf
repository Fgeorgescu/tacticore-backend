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
  
  default_tags {
    tags = {
      Project     = "tesis"
      Environment = "dev"
      ManagedBy   = "terraform"
    }
  }
}

# Módulo S3 para almacenar el código de la función
module "s3_bucket" {
  source = "../modules/s3"
  
  bucket_name = var.s3_bucket_name
  tags        = var.tags
}

# Módulo Lambda
module "lambda_function" {
  source = "../modules/lambda"
  
  function_name       = var.function_name
  runtime             = var.runtime
  handler             = var.handler
  timeout             = var.timeout
  memory_size         = var.memory_size
  source_code_hash    = var.source_code_hash
  s3_bucket           = module.s3_bucket.bucket_name
  s3_key              = var.s3_key
  environment_variables = var.environment_variables
  tags                = var.tags
}
