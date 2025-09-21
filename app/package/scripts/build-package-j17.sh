#!/bin/bash

set -x

JAVA_VERSION=${1}
J17_HOME=${2}
JMODS_HOME=${3}

APP_VERSION=${4}
APP_NAME=${5}
APP_DESCRIPTION=${6}
APP_VENDOR=${7}
APP_MAIN_JAR=${8}
APP_MAIN_CLASS=${9}
IMAGE_TYPE=${10}
BUILD_DIR=${11}
INPUT_LIBS_DIR=${12}
INPUT=${13}
OUTPUT=${14}
APP_ICON=${15}
JVM_ARGS=${16}
PLATFORM_ARG1=${17}
PLATFORM_ARG2=${18}
PLATFORM_ARG3=${19}
PLATFORM_ARG4=${20}
PLATFORM_ARG5=${21}

echo "Running dynamic jdeps"
#detected_modules=`$J17_HOME/bin/jdeps \
#  -q \
#  --multi-release ${JAVA_VERSION} \
#  --ignore-missing-deps \
#  --print-module-deps \
#  --class-path "target/installer/input/libs/*" \
#    target/classes/com/dlsc/jpackagefx/App.class`
#echo "detected modules: ${detected_modules}"

$J17_HOME/bin/jdeps \
  --multi-release ${JAVA_VERSION} \
  --print-module-deps \
  --ignore-missing-deps \
  --class-path "${INPUT_LIBS_DIR}/*"

## java.base,java.desktop,java.logging,java.naming
MODULES_BASE=java.base,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.prefs,java.scripting,java.security.jgss,java.sql,jdk.compiler,jdk.jfr,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom
MODULES_MANUAL=jdk.crypto.ec,jdk.localedata
MODULES_JFX=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web

echo "Creating runtime image"
${J17_HOME}/bin/jlink \
  --no-header-files \
  --no-man-pages \
  --compress=2 \
  --strip-debug \
  --module-path ${JMODS_HOME} \
  --add-modules "${MODULES_BASE},${MODULES_MANUAL},${MODULES_JFX}" \
  --output ${BUILD_DIR}/java-runtime

echo "Packaging app"
${J17_HOME}/bin/jpackage \
  --app-version ${APP_VERSION} \
  --name ${APP_NAME} \
  --description "${APP_DESCRIPTION}" \
  --vendor "${APP_VENDOR}" \
  --input ${INPUT_LIBS_DIR} \
  --main-jar ${APP_MAIN_JAR} \
  --main-class ${APP_MAIN_CLASS} \
  --type ${IMAGE_TYPE} \
  --input ${INPUT} \
  --dest ${OUTPUT} \
  --java-options "${JVM_ARGS}" \
  --icon ${APP_ICON} \
  --runtime-image ${BUILD_DIR}/java-runtime \
  $PLATFORM_ARG1\
  $PLATFORM_ARG2\
  $PLATFORM_ARG3\
  $PLATFORM_ARG4\
  $PLATFORM_ARG5\
  --verbose
