#!/bin/bash

set -eu

mkdir -p build

CLUSTER_FQDN="local.nip.io"

certs_init() {

  if [[ ! -f "build/key.pem" ]]; then
      mkcert -key-file build/key.pem -cert-file build/cert.pem $CLUSTER_FQDN "*.$CLUSTER_FQDN"
  fi

  SECRET_NAME="ingress-certs"

  for ns in default ingress-nginx; do

    if kubectl get namespace $ns >/dev/null 2>&1; then
      echo "Namespace $ns already exists"
    else
      kubectl create namespace $ns
    fi

    kubectl create secret tls $SECRET_NAME \
      --key "build/key.pem" \
      --cert "build/cert.pem" \
      --namespace $ns \
      --dry-run=client \
      -o yaml | kubectl apply -f -

  done

}

tf_vars() {
  cat <<-EOF > build/terraform.tfvars
kube_config_file="$HOME/.kube/config"
rancher_server_dns="rancher.$CLUSTER_FQDN"
rancher_bootstrap_password="admin"
EOF
}

apply() {
  certs_init
  tf_vars
  terraform init
  terraform apply -auto-approve -var-file build/terraform.tfvars
}

destroy() {
  terraform destroy -auto-approve -var-file build/terraform.tfvars
}

execute_function() {
  local function_name=$1
  $function_name "${@}"
}

echo "Executing function: $1"
execute_function "$1" "${@}"

