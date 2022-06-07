FROM maven:3-jdk-11 AS builder

ADD lib /javatools/lib

WORKDIR /javatools/lib
RUN mvn clean package
