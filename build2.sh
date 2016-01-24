#!/bin/bash
if [ $# -eq 0 ]; then
	echo usage: $(basename $0) "build number"
	exit 1
fi
gradle clean shadowJar
cp app/build/libs/app-0.9.4-all.jar out/builds/$1.jar
#sed -i .bak 's/public static int VERSION = 1;/public static int VERSION = 3;/' app/src/main/java/lt/markmerkk/Main.java
#gradle clean shadowJar
#cp app/build/libs/app-0.9.4-all.jar out/builds/3.jar
#mv app/src/main/java/lt/markmerkk/Main.java.bak app/src/main/java/lt/markmerkk/Main.java

java -jar tools/updatefx-app-1.6-SNAPSHOT.jar --url=http://localhost:80/ out

$JAVA_HOME/bin/javapackager -deploy -outdir out -outfile WT4.dmg -name WT4 -native dmg -appclass lt.markmerkk.Main -srcfiles out/builds/processed/$1.jar -nosign

