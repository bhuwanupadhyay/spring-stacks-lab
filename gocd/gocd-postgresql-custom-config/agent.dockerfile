FROM gocd/gocd-agent-ubuntu-22.04:v23.1.0 as core
ARG TARGETPLATFORM
RUN test -n "$TARGETPLATFORM" || (echo "TARGETPLATFORM must be set" && false)
ENV DEBIAN_FRONTEND=noninteractive
#------------------------------
#install_pyenv_prerequisites
USER root
RUN apt-get update && TZ=Etc/UTC apt-get install -y --no-install-recommends  \
    make \
    build-essential \
    libssl-dev \
    zlib1g-dev \
    libbz2-dev \
    libreadline-dev \
    libsqlite3-dev \
    wget \
    ca-certificates \
    curl\
    llvm \
    libncurses5-dev \
    xz-utils \
    tk-dev \
    libxml2-dev \
    libxmlsec1-dev \
    libffi-dev \
    liblzma-dev \
    mecab-ipadic-utf8 \
    git && \
    rm -rf /var/lib/apt/lists/*

RUN apt-get update -y \
    && apt-get install -y software-properties-common \
    && add-apt-repository -y ppa:git-core/ppa \
    && apt-get update -y \
    && apt-get install -y --no-install-recommends \
    curl \
    ca-certificates \
    git \
    git-lfs \
    iproute2 \
    iptables \
    jq \
    sudo \
    uidmap \
    unzip \
    zip \
    && rm -rf /var/lib/apt/lists/*

FROM core as user_envs

# Set-up subuid and subgid so that "--userns-remap=default" works
RUN set -eux; \
    addgroup --system dockremap; \
    adduser --system --ingroup dockremap dockremap; \
    echo 'dockremap:165536:65536' >> /etc/subuid; \
    echo 'dockremap:165536:65536' >> /etc/subgid

RUN mkdir /run/user/1000 \
    && chown go:0 /run/user/1000 \
    && chmod a+x /run/user/1000

# Add the Python "User Script Directory" to the PATH
ENV PATH="${PATH}:/home/go/.local/bin:/home/go/bin"
ENV ImageOS=ubuntu22
ENV DOCKER_HOST=unix:///run/user/1000/docker.sock
ENV XDG_RUNTIME_DIR=/run/user/1000

RUN echo "PATH=${PATH}" > /etc/environment \
    && echo "ImageOS=${ImageOS}" >> /etc/environment \
    && echo "DOCKER_HOST=${DOCKER_HOST}" >> /etc/environment \
    && echo "XDG_RUNTIME_DIR=${XDG_RUNTIME_DIR}" >> /etc/environment
# Add candidate path to $PATH environment variable
RUN echo "JAVA_HOME=/home/go/.sdkman/candidates/java/current" >> /etc/environment

FROM user_envs as codebuild
USER go

#------------------------------
# install pyenv
ARG PYTHON_VERSION=3.8.16
ENV PYENV_ROOT /home/go/.pyenv
ENV PATH $PYENV_ROOT/shims:$PYENV_ROOT/bin:$PATH
RUN set -ex \
    && curl https://pyenv.run | bash \
    && pyenv update \
    && pyenv install $PYTHON_VERSION \
    && pyenv global $PYTHON_VERSION \
    && pyenv rehash
RUN python --version
RUN python -m pip install --upgrade pip
# install pylint
RUN pip install --no-cache-dir pylint
# install checkov
RUN pip install --no-cache-dir checkov
#------------------------------

#------------------------------
# install docker
ARG DOCKER_COMPOSE_VERSION=v2.16.0
ARG DOCKER_BUILDX_VERSION="0.10.4"
RUN export SKIP_IPTABLES=1 \
    && curl -fsSL https://get.docker.com/rootless | sh \
    && /home/go/bin/docker -v
RUN export Z_ARCH=$(echo ${TARGETPLATFORM} | cut -d / -f2) \
    && if [ "$Z_ARCH" = "arm64" ]; then export ARCH=aarch64 ; fi \
    && if [ "$Z_ARCH" = "amd64" ] || [ "$Z_ARCH" = "i386" ]; then export ARCH=x86_64 ; fi \
    && mkdir -p /home/go/.docker/cli-plugins \
    && curl -fLo /home/go/.docker/cli-plugins/docker-compose https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-linux-${ARCH} \
    && chmod +x /home/go/.docker/cli-plugins/docker-compose \
    && ln -s /home/go/.docker/cli-plugins/docker-compose /home/go/bin/docker-compose \
    && which docker-compose \
    && docker compose version \
    && curl -fLo /home/go/.docker/cli-plugins/docker-buildx https://github.com/docker/buildx/releases/download/v${DOCKER_BUILDX_VERSION}/buildx-v${DOCKER_BUILDX_VERSION}.linux-${Z_ARCH} \
    && chmod +x /home/go/.docker/cli-plugins/docker-buildx \
    && ln -s /home/go/.docker/cli-plugins/docker-buildx /home/go/bin/docker-buildx \
    && docker-buildx version
#------------------------------

#------------------------------
# install awscli
USER root
RUN export Z_ARCH=$(echo ${TARGETPLATFORM} | cut -d / -f2) \
    && if [ "$Z_ARCH" = "arm64" ]; then export ARCH=aarch64 ; fi \
    && if [ "$Z_ARCH" = "amd64" ] || [ "$Z_ARCH" = "i386" ]; then export ARCH=x86_64 ; fi \
    && curl https://awscli.amazonaws.com/awscli-exe-linux-${ARCH}.zip -o /tmp/awscliv2.zip \
    && unzip -q /tmp/awscliv2.zip -d /tmp/ \
    && sudo /tmp/aws/install \
    && rm /tmp/awscliv2.zip \
    && rm -rf /tmp/aws \
    && aws --version
USER go
#------------------------------

#------------------------------
# install sdkman, java and maven
ENV JAVA_VERSION 17.0.7-tem
# Downloading SDKMAN!
RUN curl -s "https://get.sdkman.io" | bash
# Installing Java and Maven, removing some unnecessary SDKMAN files
RUN bash -c "source /home/go/.sdkman/bin/sdkman-init.sh && \
    yes | sdk install java $JAVA_VERSION && \
    yes | sdk install maven $MAVEN_VERSION && \
    rm -rf /home/go/.sdkman/archives/* && \
    rm -rf /home/go/.sdkman/tmp/*"

RUN ln -s "/home/go/.sdkman/candidates/java/current/bin/java" /home/go/bin/java \
    && which java \
    && java -version
#------------------------------

#------------------------------
# install sonarqube-scanner-cli
ENV SONAR_SCANNER_VERSION=4.8.0.2856
RUN export ARCH=$(echo ${TARGETPLATFORM} | cut -d / -f2) \
    && if [ "$ARCH" = "arm64" ]; then export ARCH=macosx ; fi \
    && if [ "$ARCH" = "amd64" ] || [ "$ARCH" = "i386" ]; then export ARCH=linux ; fi \
    && mkdir -p /home/go/ztools/ \
    && curl -sSL "https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-$SONAR_SCANNER_VERSION.zip" -o /tmp/sonar-scanner-cli-$SONAR_SCANNER_VERSION.zip \
    && unzip -q /tmp/sonar-scanner-cli-$SONAR_SCANNER_VERSION -d "/home/go/ztools/" \
    && rm -rf /tmp/sonar-scanner-cli-$SONAR_SCANNER_VERSION.zip

RUN ln -s "/home/go/ztools/sonar-scanner-4.8.0.2856/bin/sonar-scanner" /home/go/bin/sonar-scanner \
    && which sonar-scanner \
    && sonar-scanner --version
#------------------------------

#------------------------------
# install dependency-check
ENV DEPENDENCY_CHECK_VERSION=7.4.4
RUN mkdir -p /home/go/ztools/ \
    && curl -sSL "https://github.com/jeremylong/DependencyCheck/releases/download/v$DEPENDENCY_CHECK_VERSION/dependency-check-$DEPENDENCY_CHECK_VERSION-release.zip" -o /tmp/dependency-check.zip \
    && unzip -q /tmp/dependency-check.zip -d "/home/go/ztools/" \
    && rm -rf /tmp/dependency-check.zip

RUN ln -s "/home/go/ztools/dependency-check/bin/dependency-check.sh" /home/go/bin/dependency-check \
     && which dependency-check \
     && dependency-check --version
#------------------------------

#------------------------------
# install terraform
ARG TERRAFORM_VERSION="1.5.1"
RUN export ARCH=$(echo ${TARGETPLATFORM} | cut -d / -f2) \
    && if [ "$ARCH" = "arm64" ]; then export ARCH=arm64 ; fi \
    && if [ "$ARCH" = "amd64" ] || [ "$ARCH" = "i386" ]; then export ARCH=amd64 ; fi \
    && mkdir -p /home/go/ztools/ \
    && curl -sSL "https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_${ARCH}.zip" -o /tmp/terraform.zip \
    && unzip /tmp/terraform.zip -d "/home/go/bin/" \
    && rm -rf /tmp/terraform.zip \
    && which terraform \
    && terraform --version
#------------------------------
#go
RUN git clone https://github.com/syndbg/goenv.git /home/go/.goenv
ENV PATH="/home/go/.goenv/shims:/home/go/.goenv/bin:/go/bin:$PATH"
ENV GOENV_DISABLE_GOPATH=1
ENV GOPATH="/go"

ENV GOLANG_19_VERSION="1.19.10"

RUN goenv install $GOLANG_19_VERSION && rm -rf /tmp/* && \
    goenv global  $GOLANG_19_VERSION && \
    go env -w GO111MODULE=auto

#------------------------------
FROM codebuild as go

USER go
SHELL [ "/bin/bash", "-c" ]