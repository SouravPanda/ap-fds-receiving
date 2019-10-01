# We need to run it from base project directory i.e ap-fds-receive
# Use command 'docker build -f docker/Dockerfile . -t ap-fds-receive'
FROM hub.docker.prod.walmart.com/library/openjdk:8

RUN mkdir certificate
ADD ./target/ap-fds-receive-*-SNAPSHOT.jar  /app.jar
ADD ./docker/api.wal-mart.com.crt certificate/clientauthentication.crt
ADD ./docker/api.qa.wal-mart.com.crt certificate/clientauthenticationqa.crt

RUN keytool -noprompt -import -v -trustcacerts -alias receiving -keypass changeit -file certificate/clientauthentication.crt -keystore /usr/local/openjdk-8/jre/lib/security/cacerts -storepass changeit
RUN keytool -noprompt -import -v -trustcacerts -alias receivingqa -keypass changeit -file certificate/clientauthenticationqa.crt -keystore /usr/local/openjdk-8/jre/lib/security/cacerts -storepass changeit
EXPOSE 8080


### SSH Configs
ADD ./docker/apt_http /etc/apt/sources.list
ENV SSH_PASSWD "root:Docker!"
RUN apt-get -y update && apt-get install -y apt-transport-https

ADD ./docker/apt_https /etc/apt/sources.list
RUN apt-get -y update
RUN apt-get install -y --allow-unauthenticated openssh-server traceroute telnet
RUN echo "$SSH_PASSWD" | chpasswd

ADD ./docker/sshd_config /etc/ssh/sshd_config
EXPOSE 2222
## SSH Config ends

ADD ./docker/init.sh /usr/local/bin/init.sh
RUN chmod u+x /usr/local/bin/init.sh
ENTRYPOINT ["/usr/local/bin/init.sh"]
