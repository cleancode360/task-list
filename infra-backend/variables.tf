variable "aws_region" {
  description = "AWS region where the remote Terraform state backend is created."
  type        = string
}

variable "project_name" {
  description = "Project slug used in remote state resource names."
  type        = string
}
