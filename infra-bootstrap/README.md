# Infra Bootstrap

One-time local provisioning of AWS infrastructure via Docker. This is required before the GitHub Actions workflows can run, because they authenticate using an IAM role that Terraform itself creates.

## Prerequisites

- Docker
- AWS account with admin-level credentials (see below)
- A populated `infra/terraform.tfvars` file (see below)

## AWS Account Setup

1. Go to [aws.amazon.com](https://aws.amazon.com) and create an account
2. Create an IAM user with **AdministratorAccess** (needed for the Terraform bootstrap to create VPC, EKS, RDS, etc.)
3. Generate access keys for that user

## terraform.tfvars

Create `infra/terraform.tfvars` with the required variables:

```hcl
aws_region        = "us-east-1"
project_name      = "todo"
environment       = "dev"
github_repository = "your-org/your-repo"
db_password       = "..."
jwt_secret        = "..."
k8s_namespace     = "todo"
ssm_param_prefix  = "todo-dev"
```

This file is gitignored because it contains sensitive values.

## Usage

```bash
cd infra-bootstrap

export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_DEFAULT_REGION=us-east-1

docker compose up --build
```

The container bind-mounts `../infra`, runs `terraform init`, `plan`, and `apply -auto-approve`, then exits.

## After Bootstrap

Set the GitHub Actions variable so the CI/CD workflows can authenticate with AWS:

```bash
cd ../infra
gh variable set AWS_GITHUB_ACTIONS_ROLE_ARN \
  --body "$(terraform output -raw github_actions_role_arn)"
```

## Tear Down / Recreate

The EKS control plane costs ~$73/month and cannot be paused. To avoid charges when the cluster is not in use, destroy the infrastructure and recreate it when needed.

### Destroy

```bash
cd infra-bootstrap

export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_DEFAULT_REGION=us-east-1

docker compose run --rm bootstrap sh -c "terraform init && terraform destroy -auto-approve"
```

### Recreate

Run the normal bootstrap again:

```bash
docker compose up --build
```

After recreating, redeploy the backend so that K8s workloads, the ALB, and the SSM backend URL are restored:

```bash
cd ../infra
gh variable set AWS_GITHUB_ACTIONS_ROLE_ARN \
  --body "$(terraform output -raw github_actions_role_arn)"
```

Then trigger the backend workflow (push to `main` under `backend/` or re-run the workflow manually).
