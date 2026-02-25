resource "aws_amplify_app" "frontend" {
  name = "${local.name_prefix}-frontend"

  platform = "WEB"

  tags = local.tags
}

resource "aws_amplify_branch" "frontend" {
  app_id      = aws_amplify_app.frontend.id
  branch_name = var.frontend_branch
  stage       = "PRODUCTION"
}
