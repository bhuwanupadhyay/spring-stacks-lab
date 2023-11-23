# Local resources
resource "local_file" "kube_config_server_yaml" {
  filename = format("%s/%s/%s", path.root, "build","kube_config_server.yaml")
  content  = ssh_resource.retrieve_config.result
}