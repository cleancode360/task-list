FROM hashicorp/terraform:1.12

RUN apk add --no-cache aws-cli

WORKDIR /infra

ENTRYPOINT ["sh", "-c", "terraform init && terraform plan && terraform apply -auto-approve && echo '\nGitHub Actions Role ARN:' && terraform output -raw github_actions_role_arn"]
