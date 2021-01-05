FROM openjdk:8-jre-alpine

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /server
RUN chown -R $APPLICATION_USER /server

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

USER $APPLICATION_USER

COPY ./build/libs/piskvorky-server.jar /server/piskvorky-server.jar
WORKDIR /server

CMD ["/wait-for-it.sh", "mysql:3306", "--", "java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "piskvorky-server.jar"]