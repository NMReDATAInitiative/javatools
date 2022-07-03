FROM maven:3-jdk-11 AS builder


ADD lib /javatools/lib

WORKDIR /javatools/lib
RUN mvn clean package

FROM openjdk:17-slim-bullseye

COPY --from=builder /javatools/lib/target/lib-1.0-SNAPSHOT.jar /javatools.jar

WORKDIR /

ENTRYPOINT ["java", "-cp", "javatools.jar", "de.unikoeln.chemie.nmr.ui.cl.Convert"]

