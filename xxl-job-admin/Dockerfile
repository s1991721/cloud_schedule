FROM openjdk:8-jre-slim
MAINTAINER lifc

WORKDIR /data/cloudpivot/
COPY target/xxl-job-admin-*.jar /data/cloudpivot/app.jar
EXPOSE 8080

ENV PARAMS=""
ENV JAVA_OPTS="-Xms2000m -Xmx2000m -Xss256k -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+UseCGroupMemoryLimitForHeap -XX:+HeapDumpOnOutOfMemoryError"

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS app.jar $PARAMS"]