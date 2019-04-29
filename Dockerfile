FROM hub.docker.prod.walmart.com/library/openjdk:8
RUN mkdir certificate
ADD target/ap-fds-receive-0.0.9-SNAPSHOT.jar  /receive-service.jar
EXPOSE 8080
CMD ["java", "-jar", "/receive-service.jar"]

