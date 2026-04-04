resource "aws_secretsmanager_secret" "backend" {
  name        = "${local.name_prefix}/backend-secrets"
  description = "Runtime secrets for todo backend"

  tags = local.tags
}

resource "aws_secretsmanager_secret_version" "backend" {
  secret_id = aws_secretsmanager_secret.backend.id

  secret_string = jsonencode({
    "spring.datasource.username" = var.db_username
    "spring.datasource.password" = var.db_password
  })
}
