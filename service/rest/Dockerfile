FROM openjdk:8-jre-slim
COPY build/libs/rest-0.0.1-SNAPSHOT.jar .
CMD java -Xmx2G -Xms2G  -XX:TieredStopAtLevel=1 -noverify -jar rest-0.0.1-SNAPSHOT.jar
EXPOSE 8080
