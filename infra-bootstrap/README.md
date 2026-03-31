# Infra Bootstrap

One-time local provisioning of AWS infrastructure via Docker. This is required before the GitHub Actions workflows can run, because they authenticate using an IAM role that Terraform itself creates.

## Prerequisites

- Docker
- AWS CLI credentials with admin-level permissions
- A populated `infra/terraform.tfvars` file (see below)

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
export AWS_SESSION_TOKEN=...        # only for temporary credentials
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
