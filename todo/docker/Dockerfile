FROM openjdk:8

RUN mkdir /data

RUN mkdir /.aws

ADD ./todo-svc-1.1.0-SNAPSHOT.jar /data/todo-svc-1.1.0-SNAPSHOT.jar

ADD ./todo.yml /data/todo.yml

ADD ./example.keystore /data/example.keystore

ADD credentials /data/credentials

VOLUME /data

ENV AWS_CREDENTIAL_PROFILES_FILE=/data/credentials

CMD java -jar data/todo-svc-1.1.0-SNAPSHOT.jar server /data/todo.yml

EXPOSE 8080
EXPOSE 8081
EXPOSE 5005
