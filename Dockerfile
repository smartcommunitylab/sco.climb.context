# syntax=docker/dockerfile:experimental
FROM maven:3-jdk-11 AS mvn
WORKDIR /tmp
COPY ./sco.climb.context-model /tmp/sco.climb.context-model
WORKDIR /tmp/sco.climb.context-model
RUN --mount=type=cache,target=/root/.m2 mvn clean install -Dmaven.test.skip=true
COPY ./sco.climb.domain /tmp/sco.climb.domain
WORKDIR /tmp/sco.climb.domain
RUN --mount=type=cache,target=/root/.m2  mvn clean install -Dmaven.test.skip=true

FROM adoptopenjdk/openjdk8:alpine
ENV FOLDER=/tmp/sco.climb.domain/target
ARG VER=1.0
ENV APP=sco.climb.domain-${VER}.jar
ARG USER=climb
ARG USER_ID=3005
ARG USER_GROUP=climb
ARG USER_GROUP_ID=3005
ARG USER_HOME=/home/${USER}

RUN  addgroup -g ${USER_GROUP_ID} ${USER_GROUP}; \
     adduser -u ${USER_ID} -D -g '' -h ${USER_HOME} -G ${USER_GROUP} ${USER} ;

WORKDIR  /home/${USER}/app
RUN chown ${USER}:${USER_GROUP} /home/${USER}/app
RUN mkdir indexes && chown ${USER}:${USER_GROUP} indexes
# COPY --chown=aac-org:aac-org ./init.sh /tmp/server/target/init.sh
# #COPY ./app.jar /tmp/server/target/app.jar
COPY --from=mvn --chown=climb:climb ${FOLDER}/domain.jar /home/${USER}/app/climb.jar

USER climb
#CMD ["java", "-XX:MaxRAMPercentage=50", "-jar", "climb.jar"]
CMD ["java", "-jar", "climb.jar"]
