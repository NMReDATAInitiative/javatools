[![Build with Maven](https://github.com/NMReDATAInitiative/javatools/actions/workflows/maven.yml/badge.svg)](https://github.com/NMReDATAInitiative/javatools/actions/workflows/maven.yml)

# Overview

This project contains code to read and write NMReDATA files. More information can be found in the [wiki](https://github.com/NMReDATAInitiative/javatools/wiki).

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
