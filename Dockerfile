FROM openjdk:11-jre-slim
MAINTAINER Tester <tester@byzaneo.io>

ENV START_DELAY=0 \
    JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"

ADD ./target/*.war /app.war

EXPOSE 8080
CMD echo "test will start in ${START_DELAY}s..." && \
    sleep ${START_DELAY} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.war
