#!/bin/bash
DB_NAME=wt4_2.db
set -x
rm $DB_NAME
rm -r build
sqlite3 $DB_NAME < initial.sql
java -cp jooq-3.11.10.jar:jooq-meta-3.11.10.jar:jooq-codegen-3.11.10.jar:sqlite-jdbc-3.25.2.jar:. org.jooq.codegen.GenerationTool jooq.xml
