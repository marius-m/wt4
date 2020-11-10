set INSTALLER_TYPE=%1
set INPUT=%2
set OUTPUT=%3
set JAR=%4
set VERSION=%5
set APP_ICON=%6
set BUILD_DIR=%7

REM call "%J11_HOME%\bin\jlink" --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules ALL-MODULE-PATH --output %BUILD_DIR%\java-runtime
call "%J11_HOME%\bin\jlink" --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.sql,java.logging,jdk.unsupported,java.xml,java.prefs,javafx.base,javafx.controls,javafx.graphics,javafx.swing --output %BUILD_DIR%\java-runtime

call "%J14_HOME%\bin\jpackage.exe" ^
  --type exe ^
  --app-version %VERSION% ^
  --input %INPUT% ^
  --name "pdf-map" ^
  --description "PDF Mapping tool" ^
  --vendor "iTo" ^
  --main-jar %JAR% ^
  --main-class "lt.ito.pdfmap.MainKt" ^
  --dest %OUTPUT% ^
  --java-options "-splash:$APPDIR/resources/splash.png -Xmx600m -Xms128m" ^
  --icon %APP_ICON% ^
  --temp %BUILD_DIR%\tmp ^
  --runtime-image %BUILD_DIR%\java-runtime ^
  --win-shortcut --win-menu ^
  --verbose
