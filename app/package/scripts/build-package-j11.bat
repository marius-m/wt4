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
shift
set PLATFORM_ARG1=%9
shift
set PLATFORM_ARG2=%9
shift
set PLATFORM_ARG3=%9
shift
set PLATFORM_ARG4=%9
shift
set PLATFORM_ARG5=%9

REM call %J11_HOME%\bin\jlink.exe --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules ALL-MODULE-PATH --output %BUILD_DIR%\java-runtime
call %J11_HOME%\bin\jlink.exe --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.xml,javafx.base,javafx.controls,javafx.graphics,javafx.web,java.sql,java.logging,javafx.fxml,jdk.unsupported,java.security.jgss,java.compiler,java.prefs,javafx.media --output %BUILD_DIR%\java-runtime

call %J14_HOME%\bin\jpackage.exe ^
  --app-version %APP_VERSION%^
  --name %APP_NAME%^
  --description %APP_DESCRIPTION%^
  --vendor %APP_VENDOR%^
  --main-jar %APP_MAIN_JAR%^
  --main-class %APP_MAIN_CLASS%^
  --type %IMAGE_TYPE%^
  --input %INPUT%^
  --dest %OUTPUT%^
  --java-options %JVM_ARGS%^
  --icon %APP_ICON%^
  --runtime-image %BUILD_DIR%\java-runtime^
  %PLATFORM_ARG1%^
  %PLATFORM_ARG2%^
  %PLATFORM_ARG3%^
  %PLATFORM_ARG4%^
  %PLATFORM_ARG5%^
  --verbose
