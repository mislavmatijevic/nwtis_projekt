#!/bin/sh
cd /opt/hsqldb-2.6.1/hsqldb/data/
sudo java -classpath ../lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:nwtis_bp_2 --dbname.0 nwtis_bp_2 --port 9001