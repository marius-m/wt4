@echo on

set J11_HOME=%1
set J14_HOME=%2

set APP_VERSION=%3
set APP_NAME=%4
set APP_DESCRIPTION=%5
set APP_VENDOR=%6
set APP_MAIN_JAR=%7
set APP_MAIN_CLASS=%8
set IMAGE_TYPE=%9

REM https://stackoverflow.com/questions/8328338/how-do-you-utilize-more-than-9-arguments-when-calling-a-label-in-a-cmd-batch-scr%
REM Shift arguments after 9
shift
set BUILD_DIR=%9
shift
set INPUT=%9
shift
set OUTPUT=%9
shift
set APP_ICON=%9
shift
set JVM_ARGS=%9

call %J11_HOME%\bin\jlink.exe --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules ALL-MODULE-PATH --output %BUILD_DIR%\java-runtime
REM call "%J11_HOME%\bin\jlink" --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.sql,java.logging,jdk.unsupported,java.xml,java.prefs,javafx.base,javafx.controls,javafx.graphics,javafx.swing --output %BUILD_DIR%\java-runtime

call %J14_HOME%\bin\jpackage.exe ^
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
  --runtime-image %BUILD_DIR%\java-runtime ^
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
