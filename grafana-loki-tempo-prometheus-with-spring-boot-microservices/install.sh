#!/bin/bash

set -e

# create self-signed certificate
DOMAIN=k8s.localdev

if test -f "$DOMAIN+1-key.pem"; then
  echo "File $DOMAIN+1-key.pem already exists"
else
  echo "File $DOMAIN+1-key.pem does not exist"
  mkcert $DOMAIN "*.$DOMAIN"
fi

# create ingress-certs secret
SECRET_NAME=ingress-certs

for ns in kube-system auth-system; do

  # create namespace if not exists
  if kubectl get namespace $ns >/dev/null 2>&1; then
    echo "Namespace $ns already exists"
  else
    kubectl create namespace $ns
  fi

  # create secret if not exists
  if kubectl get secret $SECRET_NAME -n $ns >/dev/null 2>&1; then
    echo "Secret $SECRET_NAME already exists"
  else
    kubectl create secret tls ingress-certs \
      --key $DOMAIN+1-key.pem \
      --cert $DOMAIN+1.pem \
      --namespace $ns \
      --dry-run=client \
      -o yaml | kubectl apply -f -
  fi

done

# apply crds kustomization
kustomize build ./k8s/crds | kubectl apply -f - --server-side=true
tilt up
