resource "aws_secretsmanager_secret" "backend" {
  name        = "${local.name_prefix}/backend-secrets"
  description = "Runtime secrets for todo backend"

  tags = local.tags
}

resource "aws_secretsmanager_secret_version" "backend" {
  secret_id = aws_secretsmanager_secret.backend.id

  secret_string = jsonencode({
    APP_USERNAME                 = var.app_username
    APP_PASSWORD                 = var.app_password
    SPRING_DATASOURCE_USERNAME   = var.db_username
    SPRING_DATASOURCE_PASSWORD   = var.db_password
  })
}
