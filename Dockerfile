FROM docker.prod.walmart.com/ap-fds-mesh/servicemesh:1.14


#Adding certificates
COPY certs certs

RUN keytool -noprompt -import -v -trustcacerts -alias receiving -keypass changeit -file certs/api.qa.wal-mart.com.crt -keystore /opt/java/openjdk/jre/lib/security/cacerts -storepass changeit
RUN keytool -noprompt -import -v -trustcacerts -alias receivingQa -keypass changeit -file certs/api.wal-mart.com.crt -keystore /opt/java/openjdk/jre/lib/security/cacerts -storepass changeit

# keeping name app.war because service mesh base image this name to start the jar
ADD target/ap-fds-receive-*-SNAPSHOT.jar  /app.war