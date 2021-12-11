#!/bin/bash

if [ $# -gt 0 ]
then
echo "Using Maven located at $1."
mvnloc=$1
else
echo "Using Maven located at $(type -p mvn)."
mvnloc=$(type -p mvn)
fi

echo "Compiling and packaging"
if [ ! -d "logs" ]; then mkdir logs; fi
"$mvnloc" compile org.apache.maven.plugins:maven-assembly-plugin:single > "logs/$(date).txt"

# Find the jar with the highest version number
jarpath=$(find target/ | grep with-dependencies | sort -r | head -n 1)
echo "Executing $jarpath"
java -jar "$jarpath"
