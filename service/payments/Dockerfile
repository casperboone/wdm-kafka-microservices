FROM openjdk:8-jre-slim
COPY build/libs/payments-0.0.1-SNAPSHOT.jar .
CMD java -Xmx400m -Xms400m  -XX:TieredStopAtLevel=1 -noverify -jar payments-0.0.1-SNAPSHOT.jar
EXPOSE 8080
