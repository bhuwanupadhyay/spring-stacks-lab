FROM golang:1.19-alpine as builder
WORKDIR /sourcecode
ARG CORAZA_VERSION=0.1.2
ARG REPO=https://repo1.maven.org/maven2
ARG ZIP_FILE=https://github.com/corazawaf/coraza-proxy-wasm/archive/refs/tags/${CORAZA_VERSION}.zip
RUN echo && echo "==== Docker build for coraza-proxy-wasm ${CORAZA_VERSION} ====" && echo

RUN apk add --no-cache bash curl unzip

RUN curl -sSL ${ZIP_FILE} -o /tmp/coraza-proxy-wasm.zip \
    && unzip -q /tmp/coraza-proxy-wasm.zip -d ./ \
    && rm /tmp/coraza-proxy-wasm.zip \
    && mkdir -p ./coraza-proxy-wasm \
    && cd ./coraza-proxy-wasm-*/ \
    && cp -r . ../ \
    && cd .. \
    && rm -rf ./coraza-proxy-wasm-* \
    && ls -la

RUN go build -o build/main.wasm
RUN pwd && ls -la

FROM scratch
COPY --from=builder /sourcecode/build/main.wasm /plugin.wasm