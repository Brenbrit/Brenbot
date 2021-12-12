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
(
(set -o pipefail) || (SHELL ["/bin/bash", "-o", "pipefail"])
"$mvnloc" compile org.apache.maven.plugins:maven-assembly-plugin:single | tee "logs/$(date).txt" || exit 1
)

# Find the jar with the highest version number
jarpath=$(find target/ | grep with-dependencies | sort -r | head -n 1)
echo "Executing $jarpath"
returncode=java -jar "$jarpath"
if ( "$returncode" -eq "2" )
then
    git pull
fi
