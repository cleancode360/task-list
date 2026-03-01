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

variable "rds_multi_az" {
  description = "Enable Multi-AZ deployment for RDS."
  type        = bool
  default     = true
}

variable "rds_deletion_protection" {
  description = "Enable deletion protection on the RDS instance."
  type        = bool
  default     = true
}

variable "rds_skip_final_snapshot" {
  description = "Skip creating a final DB snapshot on destroy."
  type        = bool
  default     = false
}

variable "eks_public_endpoint" {
  description = "Allow public access to the EKS API endpoint."
  type        = bool
  default     = true
}

variable "cloudwatch_retention_days" {
  description = "CloudWatch Logs retention in days."
  type        = number
  default     = 365
}

variable "alert_email" {
  description = "Optional email address subscribed to infrastructure alarms."
  type        = string
  default     = ""
}

variable "ssm_param_prefix" {
  description = "SSM parameter path prefix without leading slash (example: todo-dev)."
  type        = string

  validation {
    condition     = !startswith(var.ssm_param_prefix, "/") && !endswith(var.ssm_param_prefix, "/")
    error_message = "ssm_param_prefix must not start or end with '/'. Example: todo-dev."
  }
}
