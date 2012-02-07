puts `ant -f generate-jar.xml`
puts `java -jar sootInsert.jar client testeBin/NewUserAccess/bin/ -cp /usr/lib/jvm/java-6-openjdk/jre/lib/rt.jar:testeBin/NewUserAccess/lib/api.jar:testeBin/NewUserAccess/bin/:testeBin/NewUserAccess/lib/javacardframework.jar`
puts `java -jar sootInsert.jar host testeBin/NewUserAccessHost/bin/ -cp /usr/lib/jvm/java-6-openjdk/jre/lib/rt.jar:testeBin/NewUserAccessHost/bin/:testeBin/handlers/`
