### apt packages ------------------------------------------------------
sudo apt update && sudo apt -y upgrade && sudo apt-get update && sudo apt-get -y upgrade
sudo apt-get install -y \
  mercurial \
  make \
  binutils \
  bison \
  gcc \
  build-essential \
  ca-certificates \
  curl \
  git \
  uidmap \
  zip \
  unzip \
  wget \
  make

### python setup ------------------------------------------------------
sudo apt install -y python3-pip python3-dev

### BEGIN: common setup ------------------------------------------------------

machine=$(uname -m)

if [[ "$machine" == "x86_64" ]]; then
    os_arch="x86_64"
    os_type="amd64"
elif [[ "$machine" == "aarch64" ]]; then
    os_arch="aarch64"
    os_type="arm64"
else
    echo "Unsupported architecture: $machine"
    exit 1
fi

### virtualenv setup ------------------------------------------------------
sudo -H pip3 install --upgrade pip
sudo -H pip3 install virtualenv
virtualenv --version

### aws setup ------------------------------------------------------
curl "https://awscli.amazonaws.com/awscli-exe-linux-${os_arch}.zip" -o "awscliv2.zip"
unzip -qq awscliv2.zip
sudo ./aws/install --update
rm -rf ./awscli-bundle ./aws
rm -rf awscliv2.zip
aws --version

### kubectl setup ------------------------------------------------------
curl -LO https://dl.k8s.io/release/v1.26.1/bin/linux/${os_type}/kubectl
chmod +x kubectl
sudo mv ./kubectl /usr/local/bin/
kubectl version --client

### k9s setup ------------------------------------------------------
curl -L https://github.com/derailed/k9s/releases/download/v0.27.4/k9s_Linux_${os_type}.tar.gz  -o k9s.tar.gz
tar -xvf k9s.tar.gz
chmod +x k9s
sudo mv ./k9s /usr/local/bin/
rm -rf k9s.tar.gz LICENSE README.md
k9s version

### helm setup ------------------------------------------------------
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash -s -- -v v3.11.2
helm version
helm plugin install https://github.com/hypnoglow/helm-s3.git

### mc client setup ------------------------------------------------------
curl https://dl.min.io/client/mc/release/linux-${os_type}/mc --create-dirs -o mc
chmod +x mc
sudo mv ./mc /usr/local/bin/

### END: common setup ------------------------------------------------------

### terraform setup ------------------------------------------------------
sudo apt-get update && sudo apt-get install -y gnupg software-properties-common
wget -O- https://apt.releases.hashicorp.com/gpg | \
gpg --dearmor | \
sudo tee /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] \
https://apt.releases.hashicorp.com $(lsb_release -cs) main" | \
sudo tee /etc/apt/sources.list.d/hashicorp.list
sudo apt update
sudo apt-get install -y terraform
terraform version

### jq setup ------------------------------------------------------
sudo apt-get install jq -y
jq --version

### nfs setup ------------------------------------------------------
sudo apt install nfs-common nfs-kernel-server -y

### docker setup ------------------------------------------------------
sudo apt install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker "$USER"

exec bash -l;
