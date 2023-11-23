# Install Ingress Nginx helm chart
resource "helm_release" "ingress_nginx" {
  name             = "ingress-nginx"
  repository       = "https://kubernetes.github.io/ingress-nginx"
  chart            = "ingress-nginx"
  version          = var.ingress_nginx_version
  namespace        = "ingress-nginx"
  create_namespace = true
  wait             = true

  values = [
    <<-EOF
rbac:
  create: true
defaultBackend:
  enabled: false
controller:
  service:
    type: LoadBalancer
    externalTrafficPolicy: Local
  config:
    ssl-redirect: true
    force-ssl-redirect: true
  watchIngressWithoutClass: true
  extraArgs:
    default-ssl-certificate: ingress-nginx/ingress-certs
EOF
  ]
}

# Install Cert Manager helm chart
resource "helm_release" "cert_manager" {
  name             = "cert-manager"
  repository       = "https://charts.jetstack.io"
  chart            = "cert-manager"
  version          = var.cert_manager_version
  namespace        = "cert-manager"
  create_namespace = true
  wait             = true

  set {
    name  = "installCRDs"
    value = "true"
  }
}

# Install Rancher helm chart
resource "helm_release" "rancher_server" {
  depends_on       = [helm_release.cert_manager, helm_release.ingress_nginx]
  name             = "rancher"
  repository       = "https://releases.rancher.com/server-charts/stable"
  chart            = "rancher"
  version          = var.rancher_version
  namespace        = "cattle-system"
  create_namespace = true
  wait             = true

  set {
    name  = "hostname"
    value = var.rancher_server_dns
  }

  set {
    name  = "replicas"
    value = "1"
  }

  set {
    name  = "bootstrapPassword"
    value = var.rancher_bootstrap_password
  }
}