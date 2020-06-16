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
    -outfile "wt4" ^
    -appclass "lt.markmerkk.MainAsJava" ^
    -native "%INSTALLER_TYPE%" ^
    -name "WT4" ^
    -title "WT4" ^
    -v ^
    -nosign ^
    -BjvmOptions=-Xmx300m ^
    -BjvmOptions=-Xms128m ^
    -BjvmOptions=-XX:+UseG1GC ^
    -BjvmOptions=-Dlog4j.configurationFile=prod_log4j2.xml ^
    -BjvmOptions=-Dfile.encoding="UTF-8" ^
    -BjvmOptions=-Dsun.jnu.encoding="UTF-8" ^
    -Bicon="%APP_ICON%" ^
    -BappVersion="%VERSION%" ^
    -BsystemWide=false
