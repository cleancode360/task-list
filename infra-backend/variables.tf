variable "aws_region" {
  description = "AWS region where the remote Terraform state backend is created."
  type        = string
}

variable "project_name" {
  description = "Project slug used in remote state resource names."
  type        = string
}

variable "environment" {
  description = "Environment name (dev/stage/prod)."
  type        = string
  default     = "dev"
}

variable "ssm_param_prefix" {
  description = "SSM parameter path prefix without leading slash."
  type        = string
}
