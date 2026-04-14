# Infra Backend

Creates the shared Terraform backend used by `infra/` and GitHub Actions, and writes the backend configuration to SSM Parameter Store.

## Usage

```bash
cd infra-backend

export TF_VAR_project_name="todo"
export TF_VAR_aws_region="us-east-1"
export TF_VAR_environment="dev"
export TF_VAR_ssm_param_prefix="todo-dev"

terraform init
terraform apply -auto-approve
```

This creates:

- S3 bucket: `<project>-terraform-state-<account-id>-<region>`
- DynamoDB table: `<project>-terraform-locks`
- SSM parameters under `/<ssm_param_prefix>/`: `terraform-state-bucket`, `terraform-lock-table`, `terraform-state-key`, `terraform-state-region`
