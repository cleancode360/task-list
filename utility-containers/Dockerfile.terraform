FROM hashicorp/terraform:1.12

RUN apk add --no-cache aws-cli

WORKDIR /infra

ENTRYPOINT ["sh", "-c", "\
  sh scripts/init-backend.sh \
  && terraform apply -auto-approve -target=module.vpc -target=module.eks \
  && terraform apply -auto-approve \
  && echo '\nGitHub Actions Role ARN:' \
  && terraform output -raw github_actions_role_arn\
"]
