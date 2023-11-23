variable "kube_config_file" {}

# Variables for rancher common module
variable "cert_manager_version" {
  type        = string
  description = "Cert-manager version (format: v0.0.0)"
  default     = "v1.12.0"
}

variable "ingress_nginx_version" {
  type        = string
  description = "Ingress-Nginx version (format: 0.0.0)"
  default     = "4.7.1"
}

variable "rancher_version" {
  type        = string
  description = "Rancher server version (format 0.0.0)"
  default     = "2.7.5"
}

# Required
variable "rancher_server_dns" {
  type        = string
  description = "DNS host name of the Rancher server"
}

# Required
variable "rancher_bootstrap_password" {
  type        = string
  description = "Password to use for Rancher server bootstrap"
  default     = "admin"
}