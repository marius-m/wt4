#!/bin/bash
$JAVA_HOME/bin/javapackager -deploy -outdir out -outfile WT4.dmg -name WT4 -native dmg -appclass com.threerings.getdown.launcher.GetdownApp -srcfiles tools/getdown-1.5.jar -nosign

