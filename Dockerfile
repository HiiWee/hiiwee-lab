FROM adoptopenjdk/openjdk11

COPY ./build/libs/github-action-docker-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
