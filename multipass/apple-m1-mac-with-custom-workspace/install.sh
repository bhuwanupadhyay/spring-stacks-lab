#!/bin/bash

set -e

if [ -z "$(command -v multipass)" ]; then
    echo "multipass not found"
    brew install multipass
else
    echo "multipass found"
    read -p "Do you want to remove it? (y/n) " -n 1 -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        brew uninstall --zap multipass
        brew install multipass
    fi
fi


if [ -z "$(multipass list | grep k3s)" ]; then
    echo "k3s instance not found, creating..."
    multipass launch --name k3s --memory 32G --disk 250G --cpus 4
fi

multipass exec k3s -- sudo apt-get update
multipass exec k3s -- sudo apt-get install snapd
multipass exec k3s -- sudo snap install multipass-sshfs
multipass exec k3s -- sudo apt-get install -y curl git mercurial make binutils bison gcc build-essential
multipass exec k3s -- sudo snap install go --channel=1.19/stable --classic
multipass exec k3s -- curl -sfL https://raw.githubusercontent.com/bhuwanupadhyay/backendj/main/multipass/apple-m1-mac-with-custom-workspace/ubuntu_setup.sh | bash -s

if [ -z "$(multipass info k3s | grep ~/workspace)" ]; then
    echo "mounting..."
    multipass mount ./ k3s:~/workspace
fi

multipass shell k3s
