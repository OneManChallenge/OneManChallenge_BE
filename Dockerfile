FROM openjdk:17
COPY build/libs/OneManITNews-*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]