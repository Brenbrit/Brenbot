# How many arguments are there?
if [ $# -gt 0 ]
then
		mvnloc=$1
else
		mvnloc=$(whereis mvn | cut -f 2 -d ' ')
fi

echo "Packaging"
$mvnloc package -q
echo "Executing"
$mvnloc exec:java -q -Dexec.mainClass=com.brenbrit.brenbot.Bot -Dexec.classpathScope=runtime
