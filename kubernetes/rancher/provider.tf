terraform {
  required_providers {
    helm = {
      source  = "hashicorp/helm"
      version = "2.10.1"
    }
    rancher2 = {
      source  = "rancher/rancher2"
      version = "3.0.0"
    }
  }
  required_version = ">= 1.0.0"
}

data "local_sensitive_file" "kube_config_server_yaml" {
  filename = var.kube_config_file
}

provider "helm" {
  kubernetes {
    config_path = data.local_sensitive_file.kube_config_server_yaml.filename
  }
}
