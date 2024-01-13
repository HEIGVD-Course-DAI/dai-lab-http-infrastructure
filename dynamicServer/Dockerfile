FROM openjdk:21

EXPOSE 7001

RUN mkdir -p API
ARG JAR_FILE=target/dynamicServer-1.0-SNAPSHOT-jar-with-dependencies.jar
ADD ${JAR_FILE} API/HTTP-API.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","API/HTTP-API.jar"]