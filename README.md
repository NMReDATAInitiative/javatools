[![Build with Maven](https://github.com/NMReDATAInitiative/javatools/actions/workflows/maven.yml/badge.svg)](https://github.com/NMReDATAInitiative/javatools/actions/workflows/maven.yml)

# Overview

This project contains code to read and write NMReDATA files. More information can be found in the [wiki](https://github.com/NMReDATAInitiative/javatools/wiki).

In addition, there is a **docker file to convert from/to NMReDATA, jcamp-dx** and the LSD format using docker. [See instructions below](#docker-convertor) for this.

The project is organized using maven and can be used in Eclipse as well (Neon is recomended). If the project is checked out there are two sub-project, lib and app. Both of them have a maven pom.xml. So for both running 'mvn install' builds the projects and installs the jar file in the maven repository. The jar files can also be found in the target directories of each sub-project. In Eclipse, both subprojects should open automatically as Eclipse project when the repository is checked out or opened as a project.

- `lib/` contains code to read and write files. This can potentially be used in other projects. There is also a set of test files and data. This is mainly for internal quality maintenance. For details, see the [wiki](https://github.com/NMReDATAInitiative/javatools/wiki/library).
- `app/` contains application code. Details see the [wiki](https://github.com/NMReDATAInitiative/javatools/wiki/standalone).

This page explaines how to run the the application, also without compiling it.

# Prerequisites

JRE 11 is needed to run the applications.

For development using the command line, Maven is needed. Eclipse can be used alternatively.

# Visualizer

## Installation

In order to install the visualizer, you can either checkout the project as explained above or simply download a precompiled file from the latest release at:

[Downloads](https://github.com/NMReDATAInitiative/javatools/releases)

The nmredata-editor.jar file contains everything needed to run the visualizer.

On linux, you can use wget for this:
```
wget https://github.com/NMReDATAInitiative/javatools/releases/download/vX.Y.Z/nmredata-editor.jar
```
where vX.Y.Z is the desired release.

## Get an example

As an exampe, you can use this nmredata.sdf file : [isoflavone.nmredata.sdf](https://github.com/NMReDATAInitiative/Examples-of-NMR-records/blob/master/isoflavone/isoflavone1_02.nmredata.sdf) file.

On linux, download it using wget:
```
wget https://github.com/NMReDATAInitiative/Examples-of-NMR-records/blob/master/isoflavone/isoflavone1_02.nmredata.sdf
```

## Usage

If you have installed a Java Runtime Environment and associated the .jar files with it, you can double click on the .jar file and open the .sdf file. Alternatively, run the program on the command line using:

```
java -jar nmredata-editor.jar
```

In order to check this file for compliance, run

```
java -cp nmredata-editor.jar de.unikoeln.chemie.nmr.ui.cl.CheckFormat <yourfile.nmredata.sdf>
```

You can also download the nmredata-app.jar, which contains only the application classes and not the dependencies. For dependencies, consult the [pom file](https://github.com/NMReDATAInitiative/javatools/blob/master/app/pom.xml).

#Docker convertor

This container is providing a prepackaged wrapper to convert between NMReDATA and the JCAMP-DX output format and to (not from) [LSD files](http://eos.univ-reims.fr/LSD/).

## Building

The container can be built with
```
docker build -t nfdi4chem/javatools . 
```

## Running

convert NMReDATA to jcamp

```
docker run -v $PWD/lib/src/test/resources/de/unikoeln/chemie/nmr:/data nfdi4chem/javatools /data/cmcse.sdf /data/test.jdx
```

convert jcamp to NMReDATA

```
docker run -v $PWD/lib/src/test/resources/de/unikoeln/chemie/nmr:/data nfdi4chem/javatools /data/1567755.jdx /data/test.sdf
```

convert NMReDATA to LSD:

```
docker run -v $PWD/lib/src/test/resources/de/unikoeln/chemie/nmr:/data nfdi4chem/javatools /data/cmcse.sdf /data/test.lsd
```

The sample files are part of the checkout, so the directory containing them is mounted as /data and then used in the path to the files. This needs to be adopted for the situation.

An NMReDATA file can contain several spectra. Those are written into separate output files, with a number added before the file extension.