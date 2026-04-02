# Infra Backend

Creates the shared Terraform backend used by `infra/` and GitHub Actions.

## Usage

```bash
cd infra-backend

export TF_VAR_project_name="todo"
export TF_VAR_aws_region="us-east-1"

terraform init
terraform apply -auto-approve
```

This creates:

- S3 bucket: `<project>-terraform-state-<account-id>-<region>`
- DynamoDB table: `<project>-terraform-locks`

After creating the backend, migrate any existing local `infra/terraform.tfstate`:

```bash
cd ../infra

export TF_VAR_project_name="todo"
export TF_VAR_environment="dev"
export TF_VAR_aws_region="us-east-1"
export AWS_DEFAULT_REGION="us-east-1"

MIGRATE_STATE=true sh scripts/init-backend.sh
```
