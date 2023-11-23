#!/bin/bash

echo "Writing out Docker config file"
mkdir -p /home/go/.config/docker/

if [ ! -f /home/go/.config/docker/daemon.json ]; then
  echo "{}" > /home/go/.config/docker/daemon.json
fi

echo "Starting Docker (rootless)"
nohup /home/go/bin/dockerd-rootless.sh --config-file /home/go/.config/docker/daemon.json > ~/docker-rootless.log 2>&1 &
RUNNER_INIT_PID=$!
echo $RUNNER_INIT_PID > ~/docker-rootless.pid
echo "Runner init started with pid $RUNNER_INIT_PID"
