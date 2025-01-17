FROM openjdk:17
COPY build/libs/tro-0.0.1-SNAPSHOT.war app.jar
ENTRYPOINT ["java","-jar","/app.jar"]