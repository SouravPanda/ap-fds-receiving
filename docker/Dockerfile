FROM docker.prod.walmart.com/mesh/soa-linkerd:1.31.0

#Adding jar
ADD ./target/ap-fds-receive-*-SNAPSHOT.jar  /app.jar

#Adding certificates
RUN mkdir certificate

ADD ./docker/api.wal-mart.com.crt certificate/clientauthentication.crt
ADD ./docker/api.qa.wal-mart.com.crt certificate/clientauthenticationApicQA.crt

RUN keytool -noprompt -import -v -trustcacerts -alias receiving -keypass changeit -file certificate/clientauthentication.crt -keystore /opt/java/openjdk/jre/lib/security/cacerts -storepass changeit
RUN keytool -noprompt -import -v -trustcacerts -alias receivingQa -keypass changeit -file certificate/clientauthenticationApicQA.crt -keystore /opt/java/openjdk/jre/lib/security/cacerts -storepass changeit

#ssh config starts
ENV SSH_PASSWD "root:Docker!"
RUN echo 'https://repository.cache.walmart.com/content/repositories/alpine-v38/main' > /etc/apk/repositories
RUN echo 'https://repository.cache.walmart.com/content/repositories/alpine-v38/community' >> /etc/apk/repositories
RUN apk update && apk add openssh-server
RUN apk add --no-cache openrc && rc-update add sshd sysinit
RUN echo "$SSH_PASSWD" | chpasswd
RUN apk add openssh
ADD ./docker/sshd_config /etc/ssh/sshd_config
#ssh config ends

COPY ./docker/config.yaml /config.yaml
COPY ./docker/init.sh /init.sh
RUN chmod u+x /init.sh
ENTRYPOINT ["/init.sh"]
CMD ["/config.yaml"]

EXPOSE 4141 2222