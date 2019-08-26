#!/bin/bash

set -x

INSTALLER_TYPE=${1}
INPUT=${2}
OUTPUT=${3}
JAR=${4}
VERSION=${5}
APP_ICON=${6}
EXTRA_BUNDLER_ARGUMENTS=${7}

$JAVA_HOME/bin/javapackager \
  -deploy  \
  -Bruntime=${JRE_HOME} \
  -srcdir ${INPUT} \
  -srcfiles ${JAR} \
  -outdir ${OUTPUT} \
  -outfile "wt4" \
  -appclass "lt.markmerkk.MainAsJava" \
  -native ${INSTALLER_TYPE} \
  -name "WT4" \
  -title "WT4" \
  -v \
  -nosign \
  -BjvmOptions=-Xmx600m -BjvmOptions=-Xms128m \
  -Bicon=${APP_ICON} \
  -BappVersion=${VERSION} \
  $EXTRA_BUNDLER_ARGUMENTS
