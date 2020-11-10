set J11_HOME=%1
set J14_HOME=%2

set APP_VERSION=%3
set APP_NAME=%4
set APP_DESCRIPTION=%5
set APP_VENDOR=%6
set APP_MAIN_JAR=%7
set APP_MAIN_CLASS=%8
set IMAGE_TYPE=%9
set BUILD_DIR=%10
set INPUT=%11
set OUTPUT=%12
set APP_ICON=%13
set JVM_ARGS=%14

call "%J11_HOME%\bin\jlink" --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules ALL-MODULE-PATH --output %BUILD_DIR%\java-runtime
REM call "%J11_HOME%\bin\jlink" --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.sql,java.logging,jdk.unsupported,java.xml,java.prefs,javafx.base,javafx.controls,javafx.graphics,javafx.swing --output %BUILD_DIR%\java-runtime

call "%J14_HOME%\bin\jpackage" ^
  --app-version %APP_VERSION% ^
  --name %APP_NAME% ^
  --description %APP_DESCRIPTION% ^
  --vendor %APP_VENDOR% ^
  --main-jar %APP_MAIN_JAR% ^
  --main-class %APP_MAIN_CLASS% ^
  --type %IMAGE_TYPE% ^
  --input %INPUT% ^
  --dest %OUTPUT% ^
  --java-options "%JVM_ARGS%" ^
  --icon %APP_ICON% ^
  --runtime-image "%BUILD_DIR%/java-runtime" ^
  --win-shortcut --win-menu ^
  --verbose

REM call "%J14_HOME%\bin\jpackage.exe" ^
REM   --type exe ^
REM   --app-version %VERSION% ^
REM   --input %INPUT% ^
REM   --name "pdf-map" ^
REM   --description "PDF Mapping tool" ^
REM   --vendor "iTo" ^
REM   --main-jar %JAR% ^
REM   --main-class "lt.ito.pdfmap.MainKt" ^
REM   --dest %OUTPUT% ^
REM   --java-options "-splash:$APPDIR/resources/splash.png -Xmx600m -Xms128m" ^
REM   --icon %APP_ICON% ^
REM   --temp %BUILD_DIR%\tmp ^
REM   --runtime-image %BUILD_DIR%\java-runtime ^
REM   --win-shortcut --win-menu ^
REM   --verbose
