# Utility Containers

Run AWS CLI, `kubectl`, Terraform, and Maven through Docker instead of installing them on your machine.

## Usage

From this directory:

```bash
cd utility-containers
```

Set credentials for the current shell:

```bash
export AWS_ACCESS_KEY_ID="..."
export AWS_SECRET_ACCESS_KEY="..."
export AWS_DEFAULT_REGION="us-east-1"
```

Run AWS CLI commands:

```bash
docker compose run --rm aws-kubectl -c "aws sts get-caller-identity"
```

Run Maven commands from the backend module:

```bash
docker compose run --rm maven mvn test
docker compose run --rm maven mvn spring-boot:run
```

Configure kubeconfig for EKS:

```bash
docker compose run --rm aws-kubectl -c "aws eks update-kubeconfig --name todo-dev-eks --region us-east-1"
```

Run `kubectl` commands:

```bash
docker compose run --rm aws-kubectl -c "kubectl get pods -n todo-namespace"
docker compose run --rm aws-kubectl -c "kubectl logs deployment/todo-backend -n todo-namespace --tail=200"
```

Apply local manifests from the repo:

```bash
docker compose run --rm aws-kubectl -c "kubectl apply -f /workspace/backend/k8s/namespace.yaml"
```

## Temporary remote debug session

Resolve the current backend image, apply the debug deployment, and wait for it to become ready:

```bash
docker compose run --rm aws-kubectl -lc '
set -euo pipefail
export AWS_REGION="${AWS_DEFAULT_REGION:-us-east-1}"
export EKS_CLUSTER_NAME="todo-dev-eks"
export K8S_NAMESPACE="todo-app"
export PROJECT_NAME="todo"

aws eks update-kubeconfig --name "$EKS_CLUSTER_NAME" --region "$AWS_REGION"
IMAGE_URI=$(kubectl get deployment "$PROJECT_NAME-backend" -n "$K8S_NAMESPACE" \
  -o jsonpath="{.spec.template.spec.containers[0].image}")

DEBUG_MANIFEST=/tmp/deployment-debug.yaml
cp /workspace/backend/k8s/deployment-debug.yaml "$DEBUG_MANIFEST"
sed -i "s|#{IMAGE_URI}#|$IMAGE_URI|g" "$DEBUG_MANIFEST"
sed -i "s|#{K8S_NAMESPACE}#|$K8S_NAMESPACE|g" "$DEBUG_MANIFEST"
sed -i "s|#{PROJECT_NAME}#|$PROJECT_NAME|g" "$DEBUG_MANIFEST"

kubectl apply -f "$DEBUG_MANIFEST"
kubectl rollout status deployment/"$PROJECT_NAME-backend-debug" -n "$K8S_NAMESPACE"
'
```

Forward the remote JDWP port to your machine for IntelliJ:

```bash
docker compose run --rm --service-ports aws-kubectl -c \
  "kubectl port-forward deployment/todo-backend-debug 5005:5005 --address 0.0.0.0 -n todo-app"
```

When you finish debugging, remove the temporary deployment:

```bash
docker compose run --rm aws-kubectl -c "kubectl delete deployment todo-backend-debug -n todo-app"
```

## Infra Bootstrap

One-time local provisioning of AWS infrastructure via Docker. This is required before the GitHub Actions workflows can run, because they authenticate using an IAM role that Terraform itself creates.

This bootstrap expects the shared Terraform backend to exist already. Create it once via `../infra-backend`, then migrate any existing local `infra/terraform.tfstate` into that backend.

### Prerequisites

- Docker
- AWS account with admin-level credentials (see below)
- Terraform `>= 1.6` for the one-time backend bootstrap and state migration
- A populated `infra/terraform.tfvars` file (see below)

### AWS Account Setup

1. Go to [aws.amazon.com](https://aws.amazon.com) and create an account
2. Create an IAM user with **AdministratorAccess** (needed for the Terraform bootstrap to create VPC, EKS, RDS, etc.)
3. Generate access keys for that user

### terraform.tfvars

Create `infra/terraform.tfvars` with the required variables:

```hcl
aws_region            = "us-east-1"
project_name          = "todo"
environment           = "dev"
github_repository     = "your-org/your-repo"
db_password           = "..."
cognito_domain_prefix = "todo-dev-auth"
k8s_namespace         = "todo"
ssm_param_prefix      = "todo-dev"
```

This file is gitignored because it contains sensitive values.

### Create Shared Terraform Backend

Create the remote state bucket and lock table once:

```bash
cd ../infra-backend

export TF_VAR_project_name="todo"
export TF_VAR_aws_region="us-east-1"

terraform init
terraform apply -auto-approve
```

This creates:

- S3 bucket: `<project>-terraform-state-<account-id>-<region>`
- DynamoDB table: `<project>-terraform-locks`

### Migrate Existing Local State

If `infra/terraform.tfstate` already exists locally, migrate it into the remote backend before using GitHub Actions:

```bash
cd ../infra

export TF_VAR_project_name="todo"
export TF_VAR_environment="dev"
export TF_VAR_aws_region="us-east-1"
export AWS_DEFAULT_REGION="us-east-1"

MIGRATE_STATE=true sh scripts/init-backend.sh
```

After migration, `terraform plan` in `infra/` and GitHub Actions will both use the same shared state.

### Bootstrap Usage

```bash
cd utility-containers

export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_DEFAULT_REGION=us-east-1

docker compose up terraform --build
```

The container bind-mounts `../infra`, configures the shared backend, and runs `terraform apply -auto-approve`, then exits.

### After Bootstrap

Set the GitHub Actions variable so the CI/CD workflows can authenticate with AWS:

```bash
cd ../infra
gh variable set AWS_GITHUB_ACTIONS_ROLE_ARN \
  --body "$(terraform output -raw github_actions_role_arn)"
```

### Tear Down / Recreate

The EKS control plane costs ~$73/month and cannot be paused. To avoid charges when the cluster is not in use, destroy the infrastructure and recreate it when needed.

#### Destroy

```bash
cd utility-containers

export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_DEFAULT_REGION=us-east-1

docker compose run --rm terraform sh -c "terraform init && terraform destroy -auto-approve"
```

If the backend has already been created, use:

```bash
docker compose run --rm terraform sh -c "sh scripts/init-backend.sh && terraform destroy -auto-approve"
```

#### Recreate

Run the normal bootstrap again:

```bash
docker compose up terraform --build
```

After recreating, redeploy the backend so that K8s workloads, the ALB, and the SSM backend URL are restored:

```bash
cd ../infra
gh variable set AWS_GITHUB_ACTIONS_ROLE_ARN \
  --body "$(terraform output -raw github_actions_role_arn)"
```

Then trigger the backend workflow (push to `main` under `backend/` or re-run the workflow manually).

## Notes

- The repo is mounted at `/workspace` inside the `aws-kubectl` container.
- Kubeconfig is stored in the shared `kube-config` Docker volume so it persists across runs.
- The `aws-kubectl` container image includes both AWS CLI and `kubectl`.
