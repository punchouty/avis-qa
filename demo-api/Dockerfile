FROM openjdk:11
EXPOSE 8080
ADD target/demo-api-0.0.1-SNAPSHOT.jar app.jar
#ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "app.jar" ]