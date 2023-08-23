FROM adoptopenjdk/openjdk11

COPY ./build/libs/<project>-<version>-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
