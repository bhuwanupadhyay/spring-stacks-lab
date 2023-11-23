#!/bin/bash
set -e

yell() { echo "$0: $*" >&2; }
die() { yell "$*"; exit 111; }
try() { echo "$ $*" 1>&2; "$@" || die "cannot $*"; }

if [ ! -e "/godata/config/cruise-config.xml" ]; then
  try cat "/xconfig/cruise-config.xml" > "/godata/config/cruise-config.xml"
else
  echo "cruise-config.xml already exists, skipping"
fi
if [ ! -e "/godata/config/logback-include.xml" ]; then
  try cat "/xconfig/logback-include.xml" > "/godata/config/logback-include.xml"
else
  echo "logback-include.xml already exists, skipping"
fi
try cat "/xsecret/password.properties" > "/godata/config/password.properties"
try cat "/xsecret/db.properties" > "/godata/config/db.properties"
try cat "/xsecret/cicd_secrets.json" > "/godata/config/cicd_secrets.json"