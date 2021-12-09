# How many arguments are there?
if [ $# -gt 0 ]
then
		echo "Using Maven located at $1."
		mvnloc=$1
else
		echo "Using Maven located at $(whereis mvn | cut -f 2 -d ' ')."
		mvnloc=$(whereis mvn | cut -f 2 -d ' ')
fi

echo "Packaging"
$mvnloc -q org.apache.maven.plugins:maven-assembly-plugin:single

echo "Executing"
# Find the jar with the highest version number
jarpath=$(find target/ | grep with-dependencies | sort -r | head -n 1)
java -jar $jarpath
