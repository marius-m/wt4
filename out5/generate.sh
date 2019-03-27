#!/bin/bash
java -jar tools/updatefx-app-1.6-SNAPSHOT.jar --url=http://89.40.3.216/wt4_2/ .
scp site/* dedikuoti:/var/www/html/wt4_2/
