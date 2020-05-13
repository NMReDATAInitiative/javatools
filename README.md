This project contains code to read and write NMReDATA files.

The project is an Eclipse project and was written in Eclipse Neon. If the project is checked out and open in Eclipse there are three source folders:
- lib contains code to read and write files. This can potentially be used in other projects.
- test contains junit tests and test data. This is mainly for internal quality maintenance.
- ui contains user interface classes. Details see below.

The ui sub project:
Currrently the following tools are available:
- CheckFormat (command line): This reads an NMReDATA file and prints a summary of its content. If there are problems with the format error messages will be displayed.
The class takes the file name as a parameter. In Eclipse right-click on the the class and choose Run as->Java Application. This will say "Please provide a file" name in the console window. Then go to Run->Run Configurations... in the menu. Choose "CheckFormat" under "Java Applications", go to the "Arguments" tab and enter a file name with a path (e. g. /home/user/workspace/nmredata/src/test/testdata/example.sd). Click the "Run" button. You should now get the analysis of the file.
- NMReDATAeditor (GUI): Opens NMReDATA files, checks them, saves as NMReDATA or LSD.
# Visualizer

## Installation

Upload  [nmredata-editor](http://nmr-sdbtest.nmr.uni-koeln.de/nmredata-editor.jar).
http://nmr-sdbtest.nmr.uni-koeln.de/nmredata-editor.jar
```
weget http://nmr-sdbtest.nmr.uni-koeln.de/nmredata-editor.jar
```
## Get an example

The nmredata.sdf file : [isoflavone.nmredata.sdf](https://github.com/NMReDATAInitiative/Examples-of-NMR-records/blob/master/isoflavone/isoflavone1_02.nmredata.sdf
) file.
```
wget https://github.com/NMReDATAInitiative/Examples-of-NMR-records/blob/master/isoflavone/isoflavone1_02.nmredata.sdf
```
## Usage
Double click on the .jar file and open the .dsf file. 
```
java -jar nmredata-editor.jar
```
