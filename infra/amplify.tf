resource "aws_amplify_app" "frontend" {
  name         = "${local.name_prefix}-frontend"
  repository   = "https://github.com/${var.github_repository}"
  access_token = var.github_oauth_token

  platform = "WEB"

  environment_variables = {
    VITE_API_BASE_URL = var.backend_public_url
  }

  tags = local.tags
}

resource "aws_amplify_branch" "frontend" {
  app_id      = aws_amplify_app.frontend.id
  branch_name = var.frontend_branch
  stage       = "PRODUCTION"
}
