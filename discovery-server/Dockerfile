FROM eclipse-temurin:17-jdk

COPY --from=rxy57608.live.dynatrace.com/linux/oneagent-codemodules:java / /

ENV LD_PRELOAD=/opt/dynatrace/oneagent/agent/lib64/liboneagentproc.so

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]