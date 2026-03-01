module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.24"

  cluster_name    = "${local.name_prefix}-eks"
  cluster_version = "1.30"

  cluster_endpoint_public_access = true

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  cluster_enabled_log_types = ["api", "audit", "authenticator", "controllerManager", "scheduler"]

  cluster_addons = {
    eks-pod-identity-agent = {
      most_recent = true
    }
  }

  fargate_profiles = {
    todo_app = {
      name = "todo-app"
      selectors = [
        {
          namespace = "todo-app"
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
          labels = {
            "k8s-app" = "kube-dns"
          }
        }
      ]
      subnet_ids = module.vpc.private_subnets
    }
  }

  tags = local.tags
}
