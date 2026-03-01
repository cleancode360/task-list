resource "aws_cloudwatch_log_group" "eks_app" {
  name              = local.k8s_log_group
  retention_in_days = var.cloudwatch_retention_days

  tags = local.tags
}

resource "aws_sns_topic" "infra_alerts" {
  name = "${local.name_prefix}-infra-alerts"

  tags = local.tags
}

resource "aws_sns_topic_subscription" "infra_alerts_email" {
  count = var.alert_email != "" ? 1 : 0

  topic_arn = aws_sns_topic.infra_alerts.arn
  protocol  = "email"
  endpoint  = var.alert_email
}

resource "aws_cloudwatch_metric_alarm" "rds_high_cpu" {
  alarm_name          = "${local.name_prefix}-rds-high-cpu"
  alarm_description   = "RDS CPU utilization above 80%."
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "CPUUtilization"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  treat_missing_data  = "notBreaching"

  dimensions = {
    DBInstanceIdentifier = module.rds.db_instance_id
  }

  alarm_actions = [aws_sns_topic.infra_alerts.arn]
}

resource "aws_cloudwatch_metric_alarm" "rds_low_storage" {
  alarm_name          = "${local.name_prefix}-rds-low-storage"
  alarm_description   = "RDS free storage below 2 GiB."
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 1
  metric_name         = "FreeStorageSpace"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 2147483648
  treat_missing_data  = "notBreaching"

  dimensions = {
    DBInstanceIdentifier = module.rds.db_instance_id
  }

  alarm_actions = [aws_sns_topic.infra_alerts.arn]
}

resource "aws_cloudwatch_metric_alarm" "rds_high_connections" {
  alarm_name          = "${local.name_prefix}-rds-high-connections"
  alarm_description   = "RDS active connections above 80."
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  treat_missing_data  = "notBreaching"

  dimensions = {
    DBInstanceIdentifier = module.rds.db_instance_id
  }

  alarm_actions = [aws_sns_topic.infra_alerts.arn]
}
