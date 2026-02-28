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
