# To-do App (Spring Boot + React)

Full-stack to-do list app with a Spring Boot HATEOAS API, React + Bootstrap UI, local Docker support, and AWS deployment assets for EKS Fargate + RDS + Amplify.

## Stack
- Backend: Java 17, Spring Boot, Maven, PostgreSQL
- Frontend: React, Vite, Bootstrap
- Local deployment: Docker Compose
- AWS deployment: Terraform, EKS Fargate, RDS PostgreSQL, Amplify Hosting
- Monitoring: CloudWatch Logs, liveness/readiness probes, pod CPU/memory metrics

## Repository layout
- `backend/`: Spring Boot API
- `frontend/`: React application
- `infra/`: Terraform infrastructure for AWS
- `backend/k8s/`: Kubernetes manifests for backend workload
- `.github/workflows/deploy-backend.yml`: CI/CD for backend image rollout to EKS
- `.github/workflows/deploy-frontend.yml`: CI/CD for frontend build + deploy to Amplify
- `.github/workflows/deploy-infra.yml`: CI/CD for Terraform plan/apply

## Local development

### Backend
```bash
cd backend
mvn spring-boot:run
```

Environment variables (optional):
- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/todo`)
- `SPRING_DATASOURCE_USERNAME` (default: `todo`)
- `SPRING_DATASOURCE_PASSWORD` (default: `todo`)
- `APP_USERNAME` (default: `admin`)
- `APP_PASSWORD` (default: `admin`)
- `APP_CORS_ALLOWED_ORIGINS` (default: `http://localhost:5173`)

### Frontend
```bash
cd frontend
npm install
npm run dev
```

Optionally set `VITE_API_BASE_URL` to point at the backend.

### Docker Compose (local)
```bash
docker compose up --build
```

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`

## AWS deployment prerequisites

Install and configure:
- Terraform `>= 1.6`
- AWS CLI v2 (`aws configure`)
- `kubectl`
- Docker
- Access to a GitHub repository connected to this project

## Terraform workflow (AWS infrastructure)

1. Move to infra:
```bash
cd infra
```

2. For local runs, provide non-sensitive values via environment variables:
```bash
export TF_VAR_project_name="todo"
export TF_VAR_github_repository="your-org/your-repo"
export TF_VAR_environment="dev"
export TF_VAR_db_username="todo"
export TF_VAR_app_username="admin"
export TF_VAR_frontend_branch="master"
```

3. Provide sensitive values via environment variables:
```bash
export TF_VAR_db_password="change-me"
export TF_VAR_app_password="change-me"
```

4. Apply infrastructure:
```bash
terraform init
terraform plan
terraform apply
```

Terraform provisions:
- VPC + subnets + NAT gateway
- EKS cluster with Fargate profiles
- RDS PostgreSQL
- ECR repository for backend image
- SSM Parameter Store entries for infra outputs consumed by CI/CD
- IAM roles/policies (including GitHub Actions role)
- Amplify app and production branch
- AWS Load Balancer Controller (via Helm)

## Kubernetes deployment workflow

1. Placeholder resolution:
- `backend/k8s/configmap.yaml` uses `#{RDS_ENDPOINT}#`, `#{DB_NAME}#`, and `#{AMPLIFY_DOMAIN}#`
- `backend/k8s/deployment.yaml` uses `#{IMAGE_URI}#`
- In CI/CD, `deploy-backend.yml` resolves these values automatically (infra values from SSM, image URI from build step)

2. Apply manifests:
```bash
kubectl apply -f backend/k8s/namespace.yaml
kubectl apply -f backend/k8s/aws-logging.yaml
kubectl apply -f backend/k8s/configmap.yaml
kubectl apply -f backend/k8s/external-secret.yaml
kubectl apply -f backend/k8s/deployment.yaml
kubectl apply -f backend/k8s/service.yaml
kubectl apply -f backend/k8s/ingress.yaml
```

3. Check rollout:
```bash
kubectl get pods -n todo-app
kubectl rollout status deployment/todo-backend -n todo-app
```

## Backend monitoring and health

### CloudWatch Logs
- Fargate log routing is configured by `backend/k8s/aws-logging.yaml`
- Logs are sent to `/eks/todo-app`

Useful commands:
```bash
aws logs tail /eks/todo-app --follow
kubectl logs -f deployment/todo-backend -n todo-app
```

### Liveness and readiness probes
Configured in `backend/k8s/deployment.yaml` and backed by Spring Actuator:
- `/actuator/health/liveness`
- `/actuator/health/readiness`

### Pod CPU and memory usage
```bash
kubectl top pods -n todo-app
kubectl top pod <pod-name> -n todo-app --containers
```

If Metrics Server is not installed, install it first:
```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

## GitHub Actions CI/CD

Workflows:
- `.github/workflows/deploy-backend.yml` (backend image build + rollout)
- `.github/workflows/deploy-frontend.yml` (frontend build + Amplify deploy)
- `.github/workflows/deploy-infra.yml` (Terraform plan/apply)

On push to `master`:
- `backend/**` changes: build and push Docker image to ECR, update kubeconfig, roll out new image
- `frontend/**` changes: build frontend with Vite, upload artifacts to Amplify
- `infra/**` changes: run Terraform plan and apply

Set these repository variables/secrets:
- Variables:
  - `AWS_REGION`
  - `AMPLIFY_BRANCH_NAME`
  - `BACKEND_PUBLIC_URL`
  - `TF_VAR_PROJECT_NAME`
  - `TF_VAR_GITHUB_REPOSITORY`
  - `TF_VAR_ENVIRONMENT`
  - `TF_VAR_SSM_PARAM_PREFIX`
  - `TF_VAR_DB_USERNAME`
  - `TF_VAR_APP_USERNAME`
- Secrets:
  - `AWS_GITHUB_ACTIONS_ROLE_ARN`
  - `TF_VAR_DB_PASSWORD`
  - `TF_VAR_APP_PASSWORD`

SSM parameter path prefix is controlled by `TF_VAR_SSM_PARAM_PREFIX` (without leading slash, example: `todo-dev`) and used by both Terraform and deploy workflows.

## Amplify frontend hosting

- Amplify app is provisioned via Terraform (`infra/amplify.tf`) as a hosting-only service
- Frontend is built by GitHub Actions (`deploy-frontend.yml`) and deployed to Amplify via the AWS CLI
- `VITE_API_BASE_URL` is injected at build time from the `BACKEND_PUBLIC_URL` GitHub Variable
- The Amplify app ID is written by Terraform to SSM and resolved by workflows at deploy time

## Auth
The API is protected with HTTP Basic auth. The UI prompts for username/password and uses those credentials for API calls.

## API examples (HATEOAS)
```bash
curl -u admin:admin http://localhost:8080/api/tasks
curl -u admin:admin http://localhost:8080/api/tasks/1
curl -u admin:admin -H "Content-Type: application/json" \
  -d '{"title":"First task","description":"Write docs"}' \
  http://localhost:8080/api/tasks
```

HATEOAS links are exposed in `_links` for each entity to guide updates, deletes, and toggles.
