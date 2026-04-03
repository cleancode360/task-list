resource "aws_ssm_parameter" "eks_cluster_name" {
  name  = "/${var.ssm_param_prefix}/eks-cluster-name"
  type  = "String"
  value = module.eks.cluster_name

  tags = local.tags
}

resource "aws_ssm_parameter" "ecr_repository_name" {
  name  = "/${var.ssm_param_prefix}/ecr-repository-name"
  type  = "String"
  value = aws_ecr_repository.backend.name

  tags = local.tags
}

resource "aws_ssm_parameter" "rds_endpoint" {
  name  = "/${var.ssm_param_prefix}/rds-endpoint"
  type  = "String"
  value = module.rds.db_instance_address

  tags = local.tags
}

resource "aws_ssm_parameter" "rds_db_name" {
  name  = "/${var.ssm_param_prefix}/rds-db-name"
  type  = "String"
  value = module.rds.db_instance_name

  tags = local.tags
}

resource "aws_ssm_parameter" "amplify_app_id" {
  name  = "/${var.ssm_param_prefix}/amplify-app-id"
  type  = "String"
  value = aws_amplify_app.frontend.id

  tags = local.tags
}

resource "aws_ssm_parameter" "amplify_default_domain" {
  name  = "/${var.ssm_param_prefix}/amplify-default-domain"
  type  = "String"
  value = "${var.frontend_branch}.${aws_amplify_app.frontend.default_domain}"

  tags = local.tags
}

resource "aws_ssm_parameter" "backend_cloudfront_domain" {
  count = var.alb_hostname != "" ? 1 : 0
  name  = "/${var.ssm_param_prefix}/backend-cloudfront-domain"
  type  = "String"
  value = aws_cloudfront_distribution.backend_api[0].domain_name

  tags = local.tags
}

resource "aws_ssm_parameter" "alb_security_group_id" {
  name  = "/${var.ssm_param_prefix}/alb-security-group-id"
  type  = "String"
  value = aws_security_group.alb_cloudfront_only.id

  tags = local.tags
}

resource "aws_ssm_parameter" "backend_secrets_key" {
  name  = "/${var.ssm_param_prefix}/backend-secrets-key"
  type  = "String"
  value = aws_secretsmanager_secret.backend.name

  tags = local.tags
}

resource "aws_ssm_parameter" "github_actions_role_arn" {
  name  = "/${var.ssm_param_prefix}/github-actions-role-arn"
  type  = "String"
  value = aws_iam_role.github_actions.arn

  tags = local.tags
}
