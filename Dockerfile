FROM hub.docker.prod.walmart.com/library/openjdk:8
RUN mkdir certificate
ADD target/ap-fds-receive-2.1.41-SNAPSHOT.jar  /receive-service.jar
ADD ./api.wal-mart.com.crt certificate/clientauthentication.crt
ADD ./api.qa.wal-mart.com.crt certificate/clientauthenticationqa.crt

RUN keytool -noprompt -import -v -trustcacerts -alias receiving -keypass changeit -file certificate/clientauthentication.crt -keystore /usr/local/openjdk-8/jre/lib/security/cacerts -storepass changeit
RUN keytool -noprompt -import -v -trustcacerts -alias receivingqa -keypass changeit -file certificate/clientauthenticationqa.crt -keystore /usr/local/openjdk-8/jre/lib/security/cacerts -storepass changeit
EXPOSE 8080
CMD ["java", "-jar", "/receive-service.jar"]