FROM openjdk:17-jdk

WORKDIR /app

## just copying the application from target folder into a container
COPY /target/spongeurl-0.0.1-SNAPSHOT.jar /app/spongeurl.jar

EXPOSE 8088

CMD ["java","-jar", "spongeurl.jar"]
