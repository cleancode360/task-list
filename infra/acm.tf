resource "aws_acm_certificate" "backend" {
  domain_name       = var.backend_domain
  validation_method = "DNS"

  tags = local.tags

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "backend" {
  certificate_arn = aws_acm_certificate.backend.arn
}
