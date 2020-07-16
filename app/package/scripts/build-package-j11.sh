#!/bin/bash

set -x

J11_HOME=${1}
J14_HOME=${2}

APP_VERSION=${3}
APP_NAME=${4}
APP_DESCRIPTION=${5}
APP_VENDOR=${6}
APP_MAIN_JAR=${7}
APP_MAIN_CLASS=${8}
IMAGE_TYPE=${9}
BUILD_DIR=${10}
INPUT=${11}
OUTPUT=${12}
APP_ICON=${13}
JVM_ARGS=${14}

echo "Creating runtime image"
${J11_HOME}/bin/jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules ALL-MODULE-PATH --output ${BUILD_DIR}/java-runtime
#${J11_HOME}/bin/jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.sql,java.logging,jdk.unsupported,java.xml,java.prefs,javafx.base,javafx.controls,javafx.graphics,javafx.swing --output ${BUILD_DIR}/java-runtime

echo "Packaging app"
${J14_HOME}/bin/jpackage \
  --app-version ${APP_VERSION}\
  --name ${APP_NAME} \
  --description ${APP_DESCRIPTION} \
  --vendor ${APP_VENDOR} \
  --main-jar ${APP_MAIN_JAR} \
  --main-class ${APP_MAIN_CLASS} \
  --type ${IMAGE_TYPE} \
  --input ${INPUT} \
  --dest ${OUTPUT} \
  --java-options "${JVM_ARGS}" \
  --icon ${APP_ICON} \
  --runtime-image ${BUILD_DIR}/java-runtime \
  --verbose
