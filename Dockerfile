FROM openjdk:17-oracle

COPY ./build/libs/specialsurveyservice-0.0.1-SNAPSHOT.jar app.jar

ARG ENVIRONMENT

ENV SPRIING_PROFIlES_ACTIVE=${ENVIRONMENT}

ENTRYPOINT ["java", "-jar", "app.jar"]