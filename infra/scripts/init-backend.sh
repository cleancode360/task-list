#!/bin/sh

set -eu

resolve_var() {
  key="$1"
  default_value="${2:-}"

  if [ -n "${default_value}" ]; then
    printf '%s\n' "$default_value"
    return 0
  fi

  if [ -f "terraform.tfvars" ]; then
    value="$(awk -F'"' -v key="$key" '$1 ~ "^[[:space:]]*" key "[[:space:]]*=" { print $2; exit }' terraform.tfvars)"
    if [ -n "${value}" ]; then
      printf '%s\n' "$value"
      return 0
    fi
  fi

  printf 'Missing required value for %s\n' "$key" >&2
  exit 1
}

project_name="$(resolve_var "project_name" "${TF_VAR_project_name:-${PROJECT_NAME:-}}")"
environment="$(resolve_var "environment" "${TF_VAR_environment:-${ENVIRONMENT:-}}")"
aws_region="$(resolve_var "aws_region" "${TF_VAR_aws_region:-${AWS_REGION:-${AWS_DEFAULT_REGION:-}}}")"

account_id="$(aws sts get-caller-identity --query Account --output text)"
bucket_name="${project_name}-terraform-state-${account_id}-${aws_region}"
table_name="${project_name}-terraform-locks"
state_key="infra/${environment}/terraform.tfstate"

if ! aws s3api head-bucket --bucket "$bucket_name" >/dev/null 2>&1; then
  printf 'Remote state bucket %s not found. Create it from ../infra-backend first.\n' "$bucket_name" >&2
  exit 1
fi

if ! aws dynamodb describe-table --table-name "$table_name" >/dev/null 2>&1; then
  printf 'Terraform lock table %s not found. Create it from ../infra-backend first.\n' "$table_name" >&2
  exit 1
fi

set -- \
  init \
  -backend-config="bucket=${bucket_name}" \
  -backend-config="key=${state_key}" \
  -backend-config="region=${aws_region}" \
  -backend-config="dynamodb_table=${table_name}" \
  -backend-config="encrypt=true"

if [ "${MIGRATE_STATE:-false}" = "true" ]; then
  set -- "$@" -migrate-state -force-copy
else
  set -- "$@" -reconfigure
fi

terraform "$@"
