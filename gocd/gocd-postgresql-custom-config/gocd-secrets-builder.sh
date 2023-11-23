#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

DEST_DIR="$SCRIPT_DIR/build"
mkdir -p "$DEST_DIR"

if ! [ -f "$DEST_DIR/gocd-file-based-secrets-plugin.jar" ]; then
  echo "----"
  echo "Installing gocd-file-based-secrets-plugin...."
  VERSION=1.1.2-170
  curl -L "https://github.com/gocd/gocd-file-based-secrets-plugin/releases/download/v$VERSION/gocd-file-based-secrets-plugin-$VERSION.jar" --output "$DEST_DIR/gocd-file-based-secrets-plugin.jar"
  echo "----"
fi

execute_jar() {
  java -jar "$DEST_DIR/gocd-file-based-secrets-plugin.jar" "$@"
}

SECRET_FILE="$1"
if [ -z "$SECRET_FILE" ]; then
  echo "Usage: $0 <secret-file>"
  exit 1
fi

if ! [ -f "$SECRET_FILE" ]; then
  execute_jar init -f "$SECRET_FILE"
fi

PS3='Choose your secret option: '
menu_options=("add/modify" "quit")
select fav in "${menu_options[@]}"; do
  case $fav in
  "init")
    execute_jar init -f "$SECRET_FILE"
    ;;
  "add/modify")
    read -rp "Enter key: " key
    read -rp "Enter value: " value
    execute_jar add -f "$SECRET_FILE" -n "$key" -v "$value"
    ;;
  "quit")
    echo "User requested exit"
    exit
    ;;
  *) echo "invalid option $REPLY" ;;
  esac
done

echo "See change on file at $SECRET_FILE"
