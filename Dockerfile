FROM docker.prod.walmart.com/gbs-fds/servicemesh:1.36.8

#Adding certificates
ADD WalmartRootCA-SHA256.crt certs/WalmartRootCA-SHA256.crt

RUN keytool -noprompt -import -v -trustcacerts -alias walmartRoot -keypass changeit -file certs/WalmartRootCA-SHA256.crt -keystore /opt/java/openjdk/jre/lib/security/cacerts -storepass changeit

# keeping name app.war because service mesh base image this name to start the jar
ADD target/ap-fds-receive-*-SNAPSHOT.jar  /app.war