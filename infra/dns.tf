resource "aws_route53_zone" "public" {
  name = var.root_domain

  tags = local.tags
}

resource "aws_acm_certificate" "backend_api" {
  domain_name       = local.backend_api_domain
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }

  tags = local.tags
}

resource "aws_route53_record" "backend_api_validation" {
  for_each = {
    for option in aws_acm_certificate.backend_api.domain_validation_options : option.domain_name => {
      name   = option.resource_record_name
      record = option.resource_record_value
      type   = option.resource_record_type
    }
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = aws_route53_zone.public.zone_id
}

resource "aws_acm_certificate_validation" "backend_api" {
  certificate_arn         = aws_acm_certificate.backend_api.arn
  validation_record_fqdns = [for record in aws_route53_record.backend_api_validation : record.fqdn]
}

module "external_dns_irsa" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks"
  version = "~> 5.51"

  role_name = "${local.name_prefix}-external-dns"

  attach_external_dns_policy   = true
  external_dns_hosted_zone_arns = [aws_route53_zone.public.arn]

  oidc_providers = {
    eks = {
      provider_arn               = module.eks.oidc_provider_arn
      namespace_service_accounts = ["kube-system:external-dns"]
    }
  }

  tags = local.tags
}

resource "helm_release" "external_dns" {
  name       = "external-dns"
  repository = "https://kubernetes-sigs.github.io/external-dns/"
  chart      = "external-dns"
  namespace  = "kube-system"

  set {
    name  = "provider.name"
    value = "aws"
  }

  set {
    name  = "policy"
    value = "upsert-only"
  }

  set {
    name  = "registry"
    value = "txt"
  }

  set {
    name  = "txtOwnerId"
    value = aws_route53_zone.public.zone_id
  }

  set {
    name  = "domainFilters[0]"
    value = var.root_domain
  }

  set {
    name  = "sources[0]"
    value = "ingress"
  }

  set {
    name  = "env[0].name"
    value = "AWS_DEFAULT_REGION"
  }

  set {
    name  = "env[0].value"
    value = var.aws_region
  }

  set {
    name  = "serviceAccount.create"
    value = "true"
  }

  set {
    name  = "serviceAccount.name"
    value = "external-dns"
  }

  set {
    name  = "serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
    value = module.external_dns_irsa.iam_role_arn
  }

  depends_on = [
    module.eks,
    aws_acm_certificate_validation.backend_api,
  ]
}
