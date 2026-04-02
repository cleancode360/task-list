variable "aws_region" {
  description = "AWS region where resources are provisioned."
  type        = string
}

variable "project_name" {
  description = "Project slug used in resource names."
  type        = string
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

variable "jwt_secret" {
  description = "Secret key used to sign JWTs."
  type        = string
  sensitive   = true
}

variable "frontend_branch" {
  description = "Git branch tracked by Amplify."
  type        = string
  default     = "main"
}

variable "rds_multi_az" {
  description = "Enable Multi-AZ deployment for RDS."
  type        = bool
  default     = false
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

variable "eks_enable_cluster_creator_admin_permissions" {
  description = "Grant cluster-admin access to the identity running Terraform."
  type        = bool
  default     = true
}

variable "eks_log_types" {
  description = "EKS control plane log types forwarded to CloudWatch."
  type        = list(string)
  default     = ["api", "authenticator"]
}

variable "cloudwatch_retention_days" {
  description = "CloudWatch Logs retention in days."
  type        = number
  default     = 30
}

variable "alert_email" {
  description = "Optional email address subscribed to infrastructure alarms."
  type        = string
  default     = ""
}

variable "backend_waf_rate_limit" {
  description = "Max requests per 5-minute window per IP before WAF blocks."
  type        = number
  default     = 1000
}

variable "k8s_namespace" {
  description = "Kubernetes namespace used for backend workloads."
  type        = string

  validation {
    condition     = trimspace(var.k8s_namespace) != ""
    error_message = "k8s_namespace must be a non-empty string."
  }
}

variable "ssm_param_prefix" {
  description = "SSM parameter path prefix without leading slash (example: todo-dev)."
  type        = string

  validation {
    condition     = !startswith(var.ssm_param_prefix, "/") && !endswith(var.ssm_param_prefix, "/")
    error_message = "ssm_param_prefix must not start or end with '/'. Example: todo-dev."
  }
}
