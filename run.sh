#!/bin/bash

if [ $# -gt 0 ]
then
echo "Using Maven located at $1."
mvnloc=$1
export JAVA_HOME="/etc/jdk-16.0.2+7"
export PATH="/etc/jdk-16.0.2+7/bin:$PATH"
else
echo "Using Maven located at $(type -p mvn)."
mvnloc=$(type -p mvn)
fi

if [ ! -d "logs" ]; then mkdir logs; fi
(
logloc="logs/$(date).txt"
echo "Compiling"
"$mvnloc" compile | tee "$logloc" || exit 1
echo "Packaging"
"$mvnloc" org.apache.maven.plugins:maven-assembly-plugin:single -q | tee "$logloc" || exit 1
)

# Find the jar with the highest version number
jarpath=$(find target/ | grep with-dependencies | sort -r | head -n 1)
echo "Executing $jarpath"
java -jar "$jarpath"
