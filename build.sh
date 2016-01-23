#!/bin/bash
export JP=$JAVA_HOME/bin/javapackager
CMD="$JP -deploy -srcfiles ./app/build/libs/app-0.9.3-all.jar -outdir ./out -outfile WT4 -native installer -appclass lt.markmerkk.Main -name WT4"
eval $CMD
