variable "aws_region" {
  description = "AWS region where resources are provisioned."
  type        = string
}

variable "project_name" {
  description = "Project slug used in resource names."
  type        = string
  default     = "todo"
}

variable "environment" {
  description = "Environment name (dev/stage/prod)."
  type        = string
  default     = "dev"
}

variable "github_repository" {
  description = "GitHub repository in owner/repo format."
  type        = string
}

variable "db_username" {
  description = "PostgreSQL username."
  type        = string
  default     = "todo"
}

variable "db_password" {
  description = "PostgreSQL password."
  type        = string
  sensitive   = true
}

variable "app_username" {
  description = "Application basic auth username."
  type        = string
  default     = "admin"
}

variable "app_password" {
  description = "Application basic auth password."
  type        = string
  sensitive   = true
}

variable "frontend_branch" {
  description = "Git branch tracked by Amplify."
  type        = string
  default     = "master"
}

variable "ssm_param_prefix" {
  description = "SSM parameter path prefix without leading slash (example: todo-dev)."
  type        = string

  validation {
    condition     = !startswith(var.ssm_param_prefix, "/") && !endswith(var.ssm_param_prefix, "/")
    error_message = "ssm_param_prefix must not start or end with '/'. Example: todo-dev."
  }
}
