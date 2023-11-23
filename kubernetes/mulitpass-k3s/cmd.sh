#!/bin/bash

set -eu


X_SSH_USER=vmuser
X_INSTANCE_NAME="k8s-rancher"

start_configs() {
  mkdir -p build

  if [[ ! -f build/multipass-ssh-key ]]; then
    ssh-keygen -C $X_SSH_USER -f build/multipass-ssh-key
  fi

  if [[ ! -f build/cloud-init.yaml ]]; then
    echo "Creating cloud-init.yaml"
    cat <<-EOF > build/cloud-init.yaml
users:
  - default
  - name: "$X_SSH_USER"
    sudo: ALL=(ALL) NOPASSWD:ALL
    ssh_authorized_keys:
    - "$(cat build/multipass-ssh-key.pub)"
EOF
  fi
}

tf_vars() {
  PUBLIC_IP=$(multipass info $X_INSTANCE_NAME --format json | jq -r ".info.\"$X_INSTANCE_NAME\".ipv4[0]")
  echo "Public IP: $PUBLIC_IP"
  cat <<-EOF > build/terraform.tfvars
node_username="$X_SSH_USER"
node_public_ip="$PUBLIC_IP"
ssh_private_key_pem_file = "./build/multipass-ssh-key"
cloudinit_file = "./build/multipass-ssh-key.pub"
EOF
}


start() {
  start_configs
  multipass launch --name $X_INSTANCE_NAME --memory 32G --disk 100G --cpus 4 --cloud-init build/cloud-init.yaml
}

shell() {
  PUBLIC_IP=$(multipass info $X_INSTANCE_NAME --format json | jq -r ".info.\"$X_INSTANCE_NAME\".ipv4[0]")
  ssh "$X_SSH_USER@$PUBLIC_IP" -i build/multipass-ssh-key -o StrictHostKeyChecking=no
}

apply() {
  tf_vars
  terraform init
  terraform apply -auto-approve -var-file build/terraform.tfvars
  cp build/kube_config_server.yaml ~/.kube/config
}

destroy() {
  terraform destroy -auto-approve -var-file build/terraform.tfvars
  multipass stop $X_INSTANCE_NAME
  multipass delete $X_INSTANCE_NAME
  multipass purge
}

execute_function() {
  local function_name=$1
  $function_name "${@}"
}

echo "Executing function: $1"
execute_function "$1" "${@}"

