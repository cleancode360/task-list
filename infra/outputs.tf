output "eks_cluster_name" {
  description = "EKS cluster name."
  value       = module.eks.cluster_name
}

output "eks_cluster_endpoint" {
  description = "EKS API endpoint."
  value       = module.eks.cluster_endpoint
}

output "ecr_backend_repository_url" {
  description = "ECR URL for backend image pushes."
  value       = aws_ecr_repository.backend.repository_url
}

output "rds_endpoint" {
  description = "RDS endpoint hostname."
  value       = module.rds.db_instance_address
}

output "secrets_manager_arn" {
  description = "Secrets Manager ARN containing runtime credentials."
  value       = aws_secretsmanager_secret.backend.arn
}

output "amplify_default_domain" {
  description = "Amplify hosted frontend domain."
  value       = aws_amplify_app.frontend.default_domain
}

output "github_actions_role_arn" {
  description = "IAM role ARN to be assumed by GitHub Actions via OIDC."
  value       = aws_iam_role.github_actions.arn
}
