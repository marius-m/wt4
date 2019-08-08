set INSTALLER_TYPE=%1
set INPUT=%2
set OUTPUT=%3
set JAR=%4
set VERSION=%5
set APP_ICON=%6

call "%JAVA_HOME%\bin\javapackager.exe" ^
    -deploy  ^
    -Bruntime="%JAVA_HOME%\jre" ^
    -srcdir "%INPUT%" ^
    -srcfiles "%JAR%" ^
    -outdir "%OUTPUT%" ^
    -outfile "pdf-map" ^
    -appclass "lt.markmerkk.MainAsJava" ^
    -native "%INSTALLER_TYPE%" ^
    -name "WT4" ^
    -title "WT4" ^
    -v ^
    -nosign ^
    -BjvmOptions=-Xmx600m -BjvmOptions=-Xms128m ^
    -Bicon="%APP_ICON%" ^
    -BappVersion="%VERSION%"
