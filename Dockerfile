FROM openjdk:17-jdk
ENV SPRING_PROFILES_ACTIVE=test
ARG JAR_FILE=build/libs/joonggonara-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]