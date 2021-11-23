echo "Packaging"
mvn package -q
echo "Executing"
mvn exec:java -q -Dexec.mainClass=com.brenbrit.brenbot.Bot -Dexec.classpathScope=runtime -Dexec.args=ODAxNDk0NjQ2NjEzNzM3NTAy.YAhgDg.NQb3GFTZRrJ2Q_s0ZjbTOHL0DNY
