FROM docker.prod.walmart.com/ap-fds-mesh/servicemesh:1.14

# keeping name app.war because service mesh base image this name to start the jar
ADD target/ap-fds-receive-*-SNAPSHOT.jar  /app.war