# To-do App (Spring Boot + Expo)

Full-stack to-do list app with a Spring Boot HATEOAS API, an Expo-based frontend, local Docker support, and AWS deployment assets for EKS Fargate + RDS + Amplify.

## Stack
- Backend: Java 17, Spring Boot, Maven, PostgreSQL
- Frontend: React Native Expo, TypeScript
- Local deployment: Docker Compose
- AWS deployment: Terraform, EKS Fargate, RDS PostgreSQL, Amplify Hosting
- Monitoring: CloudWatch Logs, liveness/readiness probes, pod CPU/memory metrics

## Repository layout
- `backend/`: Spring Boot API
- `frontend/`: Expo application
- `infra/`: Terraform infrastructure for AWS
- `infra-backend/`: One-time Terraform bootstrap for shared remote state
- `utility-containers/`: Dockerized CLI tools (AWS, kubectl, infra bootstrap)
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
- `APP_CORS_ALLOWED_ORIGINS` (default: `http://localhost:5173`)

### Frontend
```bash
cd frontend
npm install
npm run dev
```

Optionally set `EXPO_PUBLIC_API_BASE_URL` to point at the backend.

### Docker Compose (local)
```bash
docker compose up --build
```

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`

To reset the database (e.g. after schema changes), remove the volume and rebuild:
```bash
docker compose down -v && docker compose up --build
```

### Querying the database

Connect to the local PostgreSQL instance (standalone or Docker Compose):
```bash
psql -h localhost -p 5432 -U todo -d todo
```
Password: `todo`

If running via Docker Compose, you can also exec into the container:
```bash
docker compose exec db psql -U todo -d todo
```

Useful queries:
```sql
\dt                           -- list all tables
SELECT * FROM users;          -- list registered users
SELECT * FROM tasks;          -- list all tasks
SELECT * FROM tags;           -- list all tags
SELECT * FROM task_tags;      -- list task-tag associations
```

## AWS deployment prerequisites

Install and configure:
- Terraform `>= 1.6`
- Docker
- Access to a GitHub repository connected to this project

Optional: instead of installing AWS CLI and `kubectl` locally, use `utility-containers/`:

```bash
cd utility-containers

export AWS_ACCESS_KEY_ID="..."
export AWS_SECRET_ACCESS_KEY="..."
export AWS_DEFAULT_REGION="us-east-1"

docker compose run --rm aws-kubectl -c "aws sts get-caller-identity"
docker compose run --rm aws-kubectl -c "aws eks update-kubeconfig --name todo-dev-eks --region us-east-1"
docker compose run --rm aws-kubectl -c "kubectl get pods -n todo-namespace"
```

## Terraform workflow (AWS infrastructure)

1. Move to infra:
```bash
cd infra
```

2. Create the shared Terraform backend once:

```bash
cd ../infra-backend
export TF_VAR_project_name="todo"
export TF_VAR_aws_region="us-east-1"
terraform init
terraform apply -auto-approve
cd ../infra
```

3. For local runs, provide non-sensitive values via environment variables:
```bash
export TF_VAR_project_name="todo"
export TF_VAR_k8s_namespace="todo-app"
export TF_VAR_github_repository="your-org/your-repo"
export TF_VAR_environment="dev"
export TF_VAR_aws_region="us-east-1"
export TF_VAR_ssm_param_prefix="todo-dev"
export TF_VAR_root_domain="example.com"
export TF_VAR_backend_api_subdomain="api"
export TF_VAR_db_username="todo"
export TF_VAR_frontend_branch="main"
export TF_VAR_rds_multi_az="false"
export TF_VAR_rds_deletion_protection="false"
export TF_VAR_rds_skip_final_snapshot="true"
export TF_VAR_eks_public_endpoint="true"
export TF_VAR_cloudwatch_retention_days="14"
export TF_VAR_alert_email=""
```

4. Provide sensitive values via environment variables:
```bash
export TF_VAR_db_password="change-me"
```

5. Migrate any existing local state into the shared backend:
```bash
MIGRATE_STATE=true sh scripts/init-backend.sh
```

6. Apply infrastructure:
```bash
sh scripts/init-backend.sh
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
- CloudWatch log groups with configurable retention
- CloudWatch alarms for RDS with SNS notifications

## Kubernetes deployment workflow

1. Placeholder resolution:
- `backend/k8s/configmap.yaml` uses `#{RDS_ENDPOINT}#`, `#{DB_NAME}#`, `#{AMPLIFY_DOMAIN}#`, `#{SECRETS_MANAGER_KEY}#`, and `#{AWS_REGION}#`
- `backend/k8s/aws-logging.yaml` uses `#{AWS_REGION}#` and `#{PROJECT_NAME}#`
- `backend/k8s/deployment.yaml` uses `#{IMAGE_URI}#`
- `backend/k8s/deployment-debug.yaml` uses `#{IMAGE_URI}#`, `#{K8S_NAMESPACE}#`, and `#{PROJECT_NAME}#`
- In CI/CD, `deploy-backend.yml` resolves these values automatically (infra values from SSM, image URI from build step)

2. Apply manifests:
```bash
kubectl apply -f backend/k8s/namespace.yaml
kubectl apply -f backend/k8s/aws-logging.yaml
kubectl apply -f backend/k8s/service-account.yaml
kubectl apply -f backend/k8s/configmap.yaml
kubectl apply -f backend/k8s/deployment.yaml
kubectl apply -f backend/k8s/service.yaml
kubectl apply -f backend/k8s/ingress.yaml
```

3. Check rollout:
```bash
kubectl get pods -n todo-app
kubectl rollout status deployment/todo-backend -n todo-app
```

### Temporary remote debug session

Use `backend/k8s/deployment-debug.yaml` to start a short-lived debug pod that reuses the normal backend `ConfigMap` and `ServiceAccount` but stays out of the main `Service` selector.

Important:
- Keep this deployment temporary and remove it when you finish debugging.
- Do not expose port `5005` through an Ingress or public `Service`.
- The debug pod talks to the same AWS Secrets Manager entry, RDS instance, and Redis service as the normal backend deployment.

1. Connect `kubectl` to the cluster and resolve the current backend image:
```bash
export AWS_REGION="us-east-1"
export EKS_CLUSTER_NAME="todo-dev-eks"
export K8S_NAMESPACE="todo-app"
export PROJECT_NAME="todo"

aws eks update-kubeconfig --name "$EKS_CLUSTER_NAME" --region "$AWS_REGION"
IMAGE_URI=$(kubectl get deployment "$PROJECT_NAME-backend" -n "$K8S_NAMESPACE" \
  -o jsonpath='{.spec.template.spec.containers[0].image}')
```

2. Resolve the debug manifest placeholders into a temporary file and apply it:
```bash
DEBUG_MANIFEST="$(mktemp)"
cp backend/k8s/deployment-debug.yaml "$DEBUG_MANIFEST"

sed -i.bak "s|#{IMAGE_URI}#|$IMAGE_URI|g" "$DEBUG_MANIFEST"
sed -i.bak "s|#{K8S_NAMESPACE}#|$K8S_NAMESPACE|g" "$DEBUG_MANIFEST"
sed -i.bak "s|#{PROJECT_NAME}#|$PROJECT_NAME|g" "$DEBUG_MANIFEST"
rm "$DEBUG_MANIFEST.bak"

kubectl apply -f "$DEBUG_MANIFEST"
kubectl rollout status deployment/"$PROJECT_NAME-backend-debug" -n "$K8S_NAMESPACE"
```

3. Forward the JDWP port to your machine:
```bash
kubectl port-forward deployment/"$PROJECT_NAME-backend-debug" 5005:5005 -n "$K8S_NAMESPACE"
```

4. In IntelliJ IDEA, create a `Remote JVM Debug` configuration:
- Host: `localhost`
- Port: `5005`

5. When you are done, remove the temporary deployment:
```bash
kubectl delete deployment "$PROJECT_NAME-backend-debug" -n "$K8S_NAMESPACE"
rm -f "$DEBUG_MANIFEST"
```

The debug deployment starts the JVM with `suspend=n`. If you need to stop on startup, change `JAVA_TOOL_OPTIONS` in `backend/k8s/deployment-debug.yaml` to use `suspend=y` before applying it.

## Backend monitoring and health

### CloudWatch Logs
- Fargate log routing is configured by `backend/k8s/aws-logging.yaml`
- Logs are sent to `/eks/todo-app` (retention controlled by `TF_VAR_cloudwatch_retention_days`)

Useful commands:
```bash
aws logs tail /eks/todo-app --follow
kubectl logs -f deployment/todo-backend -n todo-app
```

### CloudWatch alarms (RDS)
- Terraform creates alarms for `CPUUtilization`, `FreeStorageSpace`, and `DatabaseConnections`
- Alarm notifications are published to an SNS topic (`<project>-<environment>-infra-alerts`)
- Set `TF_VAR_alert_email` to receive email notifications from the SNS topic

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

Terraform state is stored remotely in S3 and locked via DynamoDB so local runs and GitHub Actions share the same infrastructure state.

## GitHub Actions CI/CD

Workflows:
- `.github/workflows/deploy-backend.yml` (backend image build + rollout)
- `.github/workflows/deploy-frontend.yml` (frontend build + Amplify deploy)
- `.github/workflows/deploy-infra.yml` (Terraform plan/apply)

On push to `main`:
- `backend/**` changes: build and push Docker image to ECR, update kubeconfig, roll out new image
- `frontend/**` changes: export the Expo web build and upload artifacts to Amplify
- `infra/**` changes: run Terraform plan and apply

Set these repository variables/secrets:
- Variables:
  - `AWS_REGION`
  - `AWS_GITHUB_ACTIONS_ROLE_ARN`
  - `AMPLIFY_BRANCH_NAME`
  - `PROJECT_NAME`
  - `K8S_NAMESPACE` (used by Terraform and backend deploy workflow as the Kubernetes namespace)
  - `ENVIRONMENT`
  - `SSM_PARAM_PREFIX`
  - `ROOT_DOMAIN`
  - `BACKEND_API_SUBDOMAIN`
- Secrets:
  - `DB_USERNAME`
  - `DB_PASSWORD`
  - `JWT_SECRET`

SSM parameter path prefix is controlled by `SSM_PARAM_PREFIX` (without leading slash, example: `todo-dev`) and used by both Terraform and deploy workflows.

## Amplify frontend hosting

- Amplify app is provisioned via Terraform (`infra/amplify.tf`) as a hosting-only service
- Frontend is built by GitHub Actions (`deploy-frontend.yml`) and deployed to Amplify via the AWS CLI
- `EXPO_PUBLIC_API_BASE_URL` is set at build time to the HTTPS backend base URL from SSM (`backend-public-url`)
- In production the frontend calls the backend directly at `https://api.<root-domain>` (cross-origin); the backend's CORS configuration allows the Amplify origin
- The Amplify app ID is written by Terraform to SSM and resolved by workflows at deploy time

## Auth
The app supports multiple users with session-based authentication. Users register via the UI or `POST /api/auth/register`, then log in to receive a `JSESSIONID` session cookie. Each user's tasks and tags are fully isolated.

### Endpoints
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | No | Create a new account |
| POST | `/api/auth/login` | No | Log in and start a session |
| GET | `/api/auth/me` | Yes | Get current session user |
| POST | `/api/auth/logout` | Yes | Invalidate session |

### Data isolation
Tasks and tags are scoped per user. A user can only read, update, and delete their own data.

## API examples (HATEOAS)
```bash
# Register a new user
curl -c cookies.txt -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}' \
  http://localhost:8080/api/auth/register

# Log in (stores session cookie)
curl -c cookies.txt -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}' \
  http://localhost:8080/api/auth/login

# Use the session cookie for subsequent requests
curl -b cookies.txt http://localhost:8080/api/tasks
curl -b cookies.txt http://localhost:8080/api/tasks/1
curl -b cookies.txt -H "Content-Type: application/json" \
  -d '{"title":"First task","description":"Write docs"}' \
  http://localhost:8080/api/tasks
curl -b cookies.txt http://localhost:8080/api/tags
curl -b cookies.txt -H "Content-Type: application/json" \
  -d '{"name":"work"}' \
  http://localhost:8080/api/tags
curl -b cookies.txt -X PUT -H "Content-Type: application/json" \
  -d '{"name":"personal"}' \
  http://localhost:8080/api/tags/1
curl -b cookies.txt -X DELETE http://localhost:8080/api/tags/1
curl -b cookies.txt -X POST http://localhost:8080/api/tasks/1/tags/2
curl -b cookies.txt -X DELETE http://localhost:8080/api/tasks/1/tags/2

# Log out
curl -b cookies.txt -X POST http://localhost:8080/api/auth/logout
```

HATEOAS links are exposed in `_links` for each entity to guide updates, deletes, and toggles.
