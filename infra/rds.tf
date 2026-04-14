resource "aws_security_group" "rds" {
  name        = "${local.name_prefix}-rds-sg"
  description = "Allow PostgreSQL access from EKS cluster"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = module.vpc.private_subnets_cidr_blocks
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = [module.vpc.vpc_cidr_block]
  }

  tags = local.tags
}

module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "~> 6.10"

  identifier = "${local.name_prefix}-postgres"

  engine               = "postgres"
  engine_version       = "16.6"
  family               = "postgres16"
  major_engine_version = "16"
  instance_class       = "db.t4g.micro"
  allocated_storage    = 20

  db_name  = replace("${local.name_prefix}_db", "-", "_")
  username = var.db_username
  port     = 5432

  manage_master_user_password = false
  password                    = var.db_password

  multi_az               = var.rds_multi_az
  create_db_subnet_group = true
  subnet_ids             = module.vpc.private_subnets
  vpc_security_group_ids = [aws_security_group.rds.id]

  backup_retention_period = 1
  deletion_protection     = var.rds_deletion_protection
  skip_final_snapshot     = var.rds_skip_final_snapshot
  final_snapshot_identifier_prefix = "${local.name_prefix}-postgres-final"

  tags = local.tags
}
