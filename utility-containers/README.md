# Utility Containers

Run AWS CLI and `kubectl` through Docker instead of installing them on your machine.

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
docker compose run --rm cli -c "aws sts get-caller-identity"
```

Configure kubeconfig for EKS:

```bash
docker compose run --rm cli -c "aws eks update-kubeconfig --name todo-dev-eks --region us-east-1"
```

Run `kubectl` commands:

```bash
docker compose run --rm cli -c "kubectl get pods -n todo-namespace"
docker compose run --rm cli -c "kubectl logs deployment/todo-backend -n todo-namespace --tail=200"
```

Apply local manifests from the repo:

```bash
docker compose run --rm cli -c "kubectl apply -f /workspace/backend/k8s/namespace.yaml"
```

## Temporary remote debug session

Resolve the current backend image, apply the debug deployment, and wait for it to become ready:

```bash
docker compose run --rm cli -lc '
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
docker compose run --rm --service-ports cli -c \
  "kubectl port-forward deployment/todo-backend-debug 5005:5005 --address 0.0.0.0 -n todo-app"
```

When you finish debugging, remove the temporary deployment:

```bash
docker compose run --rm cli -c "kubectl delete deployment todo-backend-debug -n todo-app"
```

## Notes

- The repo is mounted at `/workspace` inside the container.
- Kubeconfig is stored in the shared `kube-config` Docker volume so it persists across runs.
- The container image includes both AWS CLI and `kubectl`.
