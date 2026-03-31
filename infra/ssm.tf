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
  value = aws_amplify_app.frontend.default_domain

  tags = local.tags
}

resource "aws_ssm_parameter" "acm_certificate_arn" {
  name  = "/${var.ssm_param_prefix}/acm-certificate-arn"
  type  = "String"
  value = aws_acm_certificate.backend.arn

  tags = local.tags
}

resource "aws_ssm_parameter" "waf_acl_arn" {
  name  = "/${var.ssm_param_prefix}/waf-acl-arn"
  type  = "String"
  value = aws_wafv2_web_acl.backend.arn

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
