# Required
variable "node_public_ip" {
  type        = string
  description = "Public IP of compute node for k3s cluster"
}

# Required
variable "node_username" {
  type        = string
  description = "Username used for SSH access to the k3s server cluster node"
}

# Required
variable "cloudinit_file" {
  type        = string
  description = "Multipass cloud-init YAML file for k3s server cluster node"
}

# Required
variable "ssh_private_key_pem_file" {
  type        = string
  description = "Private key file used for SSH access to the k3s server cluster node"
}

variable "k3s_kubernetes_version" {
  type        = string
  description = "Kubernetes version to use for k3s server cluster"
  default     = "v1.24.14+k3s1"
}