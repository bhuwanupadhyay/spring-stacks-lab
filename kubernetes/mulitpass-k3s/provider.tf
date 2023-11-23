terraform {
  required_providers {
    helm = {
      source  = "hashicorp/helm"
      version = "2.10.1"
    }
    local = {
      source  = "hashicorp/local"
      version = "2.4.0"
    }
    ssh = {
      source  = "loafoe/ssh"
      version = "2.6.0"
    }
  }
  required_version = ">= 1.0.0"
}

provider "helm" {
  kubernetes {
    config_path = local_file.kube_config_server_yaml.filename
  }
}


