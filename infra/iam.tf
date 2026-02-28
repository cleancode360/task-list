data "tls_certificate" "eks_oidc" {
  url = module.eks.cluster_oidc_issuer_url
}

resource "aws_iam_openid_connect_provider" "eks" {
  url             = module.eks.cluster_oidc_issuer_url
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = [data.tls_certificate.eks_oidc.certificates[0].sha1_fingerprint]

  tags = local.tags
}

resource "aws_iam_policy" "fargate_secrets_access" {
  name        = "${local.name_prefix}-fargate-secrets-access"
  description = "Allow backend pods to read runtime secrets"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue"
        ]
        Resource = aws_secretsmanager_secret.backend.arn
      }
    ]
  })
}

resource "aws_iam_role" "github_actions" {
  name = "${local.name_prefix}-github-actions-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.github.arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          }
          StringLike = {
            "token.actions.githubusercontent.com:sub" = "repo:${var.github_repository}:*"
          }
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_openid_connect_provider" "github" {
  url = "https://token.actions.githubusercontent.com"

  client_id_list = [
    "sts.amazonaws.com"
  ]

  thumbprint_list = [
    "6938fd4d98bab03faadb97b34396831e3780aea1"
  ]
}

resource "aws_iam_role_policy_attachment" "github_actions_eks" {
  role       = aws_iam_role.github_actions.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
}

resource "aws_iam_role_policy_attachment" "github_actions_ecr" {
  role       = aws_iam_role.github_actions.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryPowerUser"
}

resource "aws_iam_policy" "github_actions_amplify_deploy" {
  name        = "${local.name_prefix}-github-actions-amplify-deploy"
  description = "Allow GitHub Actions to deploy frontend artifacts to Amplify"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "amplify:CreateDeployment",
          "amplify:StartDeployment",
          "amplify:GetDeployment"
        ]
        Resource = "${aws_amplify_app.frontend.arn}/*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "github_actions_amplify" {
  role       = aws_iam_role.github_actions.name
  policy_arn = aws_iam_policy.github_actions_amplify_deploy.arn
}

resource "aws_iam_policy" "github_actions_infra_read" {
  name        = "${local.name_prefix}-github-actions-infra-read"
  description = "Allow GitHub Actions to read infrastructure outputs from SSM Parameter Store"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = ["ssm:GetParameter", "ssm:GetParametersByPath"]
        Resource = "arn:aws:ssm:*:*:parameter/${var.ssm_param_prefix}/*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "github_actions_infra_read" {
  role       = aws_iam_role.github_actions.name
  policy_arn = aws_iam_policy.github_actions_infra_read.arn
}
