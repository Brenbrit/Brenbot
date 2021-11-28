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
$mvnloc package -q
echo "Executing"
$mvnloc exec:java -q -Dexec.mainClass=com.brenbrit.brenbot.Bot -Dexec.classpathScope=runtime
