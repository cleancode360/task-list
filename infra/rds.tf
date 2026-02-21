resource "aws_security_group" "rds" {
  name        = "${local.name_prefix}-rds-sg"
  description = "Allow PostgreSQL access from EKS cluster"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [module.vpc.vpc_cidr_block]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = local.tags
}

module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "~> 6.10"

  identifier = "${local.name_prefix}-postgres"

  engine               = "postgres"
  engine_version       = "16.3"
  family               = "postgres16"
  major_engine_version = "16"
  instance_class       = "db.t4g.micro"
  allocated_storage    = 20

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password
  port     = 5432

  multi_az               = false
  create_db_subnet_group = true
  subnet_ids             = module.vpc.private_subnets
  vpc_security_group_ids = [aws_security_group.rds.id]

  backup_retention_period = 7
  deletion_protection     = false
  skip_final_snapshot     = true

  tags = local.tags
}
