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

output "amplify_app_id" {
  description = "Amplify app ID used by the frontend deploy workflow."
  value       = aws_amplify_app.frontend.id
}

output "amplify_default_domain" {
  description = "Amplify hosted frontend domain."
  value       = aws_amplify_app.frontend.default_domain
}

output "backend_cloudfront_domain" {
  description = "CloudFront domain for backend API requests (empty until ALB hostname is available)."
  value       = var.alb_hostname != "" ? aws_cloudfront_distribution.backend_api[0].domain_name : ""
}

output "github_actions_role_arn" {
  description = "IAM role ARN to be assumed by GitHub Actions via OIDC."
  value       = aws_iam_role.github_actions.arn
}

output "cognito_user_pool_id" {
  description = "Cognito User Pool ID for backend JWT validation."
  value       = aws_cognito_user_pool.main.id
}

output "cognito_client_id" {
  description = "Cognito app client ID for frontend authentication."
  value       = aws_cognito_user_pool_client.main.id
}

output "cognito_domain" {
  description = "Cognito Hosted UI domain."
  value       = "${aws_cognito_user_pool_domain.main.domain}.auth.${var.aws_region}.amazoncognito.com"
}
