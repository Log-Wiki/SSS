FROM openjdk:17-oracle

COPY ./build/libs/specialsurveyservice-0.0.1-SNAPSHOT.jar app.jar

ARG EVIRONMENT

ENV SPRIING_PROFIlES_ACTIVE=${EVIRONMENT}

ENTRYPOINT ["java", "-jar", "app.jar"]