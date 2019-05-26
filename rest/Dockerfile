FROM openjdk:8-jre-slim
COPY build/libs/rest-0.0.1-SNAPSHOT.jar .
CMD /usr/bin/java -Xmx400m -Xms400m  -XX:TieredStopAtLevel=1 -noverify -jar rest-0.0.1-SNAPSHOT.jar
EXPOSE 8080
