echo "Packaging"
mvn package -q
echo "Executing"
mvn exec:java -q -Dexec.mainClass=com.brenbrit.brenbot.Bot -Dexec.classpathScope=runtime -Dexec.args=$(echo /usr/share/brenbot/token)
