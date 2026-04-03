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

output "backend_api_domain" {
  description = "Custom backend API domain."
  value       = local.backend_api_domain
}

output "backend_https_base_url" {
  description = "HTTPS base URL for backend API requests."
  value       = local.backend_https_base_url
}

output "backend_tls_certificate_arn" {
  description = "ACM certificate ARN used by the backend ingress."
  value       = aws_acm_certificate_validation.backend_api.certificate_arn
}

output "route53_name_servers" {
  description = "Route53 name servers for delegating the hosted zone."
  value       = aws_route53_zone.public.name_servers
}

output "github_actions_role_arn" {
  description = "IAM role ARN to be assumed by GitHub Actions via OIDC."
  value       = aws_iam_role.github_actions.arn
}
