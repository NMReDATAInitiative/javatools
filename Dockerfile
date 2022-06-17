FROM maven:3-jdk-11 AS builder

ADD target/javatools.jar javatools.jar
ADD lib lib

WORKDIR /

ENTRYPOINT ["java", "-cp", "javatools.jar", "de.unikoeln.chemie.nmr.ui.cl.Convert"]
