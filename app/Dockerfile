FROM alpine:latest
RUN apk add --no-cache openjdk17
WORKDIR /app
COPY target/HTTP-*.jar /app/HTTP.jar
CMD ["java", "-jar", "HTTP.jar"]
EXPOSE 7000
