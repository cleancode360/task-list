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

2. Create a `terraform.tfvars` file:
```hcl
aws_region         = "us-east-1"
project_name       = "todo"
environment        = "dev"
github_repository  = "your-org/your-repo"
github_oauth_token = "ghp_xxx"
db_name            = "todo"
db_username        = "todo"
db_password        = "change-me"
app_username       = "admin"
app_password       = "change-me"
frontend_branch    = "main"
backend_public_url = ""
```

3. Apply infrastructure:
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
- IAM roles/policies (including GitHub Actions role)
- Amplify app and production branch
- AWS Load Balancer Controller (via Helm)

## Kubernetes deployment workflow

1. Update placeholders before apply:
- `backend/k8s/configmap.yaml`: replace `REPLACE_RDS_ENDPOINT` and `REPLACE_AMPLIFY_DOMAIN`
- `backend/k8s/deployment.yaml`: replace `REPLACE_ECR_URL`

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

Workflow: `.github/workflows/deploy-backend.yml`

On push to `main` (`backend/**` changes), pipeline will:
1. Build and push Docker image to ECR
3. Update kubeconfig for EKS
4. Roll out new image to deployment `todo-backend`

Set these repository variables/secrets:
- Variables:
  - `AWS_REGION`
  - `EKS_CLUSTER_NAME`
  - `ECR_REPOSITORY`
- Secret:
  - `AWS_GITHUB_ACTIONS_ROLE_ARN`

## Amplify frontend hosting

- Amplify app is provisioned via Terraform (`infra/amplify.tf`)
- Build instructions are defined inline in Terraform (`build_spec` in `infra/amplify.tf`)
- Set `VITE_API_BASE_URL` in Amplify environment variables to your backend ALB URL

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
