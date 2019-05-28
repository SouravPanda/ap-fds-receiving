FROM hub.docker.prod.walmart.com/library/openjdk:8
RUN mkdir certificate
ADD target/ap-fds-receive-2.1.7-SNAPSHOT.jar  /receive-service.jar

ADD ./api.wal-mart.com.crt certificate/clientauthentication.crt
RUN keytool -noprompt -import -v -trustcacerts -alias receiving -keypass changeit -file certificate/clientauthentication.crt -keystore /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts -storepass changeit
#RUN cp cacerts /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts

EXPOSE 8080
CMD ["java", "-jar", "/receive-service.jar"]
