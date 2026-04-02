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

## Notes

- The repo is mounted at `/workspace` inside the container.
- Kubeconfig is stored in the shared `kube-config` Docker volume so it persists across runs.
- The container image includes both AWS CLI and `kubectl`.
