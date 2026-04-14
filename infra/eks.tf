resource "aws_iam_policy" "fargate_logging" {
  name        = "${local.name_prefix}-fargate-logging"
  description = "Allow Fargate Fluent Bit sidecar to ship logs to CloudWatch"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogStream",
          "logs:CreateLogGroup",
          "logs:DescribeLogStreams",
          "logs:PutLogEvents"
        ]
        Resource = "*"
      }
    ]
  })

  tags = local.tags
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.24"

  cluster_name    = "${local.name_prefix}-eks"
  cluster_version = "1.32"

  cluster_endpoint_public_access  = var.eks_public_endpoint
  cluster_endpoint_private_access = true

  enable_cluster_creator_admin_permissions = var.eks_enable_cluster_creator_admin_permissions

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  create_kms_key = true
  cluster_enabled_log_types = var.eks_log_types
  cloudwatch_log_group_retention_in_days = var.cloudwatch_retention_days

  cluster_addons = {
    coredns = {
      most_recent = true
      configuration_values = jsonencode({
        computeType = "Fargate"
      })
    }
    eks-pod-identity-agent = {
      most_recent = true
    }
  }

  fargate_profile_defaults = {
    iam_role_additional_policies = {
      cloudwatch_logging = aws_iam_policy.fargate_logging.arn
    }
  }

  fargate_profiles = {
    todo_app = {
      name = local.k8s_namespace
      selectors = [
        {
          namespace = local.k8s_namespace
        },
        {
          namespace = "aws-observability"
        }
      ]
      subnet_ids = module.vpc.private_subnets
    }
    kube_system = {
      name = "kube-system"
      selectors = [
        {
          namespace = "kube-system"
        }
      ]
      subnet_ids = module.vpc.private_subnets
    }
  }

  tags = local.tags
}
