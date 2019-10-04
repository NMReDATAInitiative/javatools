package de.unikoeln.chemie.nmr.ui.cl;

import java.io.File;
import java.io.FileInputStream;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.NmredataReader;

public class TestSet {

	public static void main(String[] args) {
		if(args.length!=1){
			System.out.println("Please provide a directory name!");
			return;
		}		
		//This is the directory we look for files in
		File directory=new File(args[0]);
		if(!directory.isDirectory()) {
			System.out.println("Seems your filename is not a directory!");
			return;			
		}
		parseDirectory(directory);
	}
	
	public static void parseDirectory(File directory) {
		File[] subdirs=directory.listFiles();
		for(File file : subdirs) {
			if(file.isDirectory()) {
				System.out.println("Looking into directory "+file.getName());
				parseDirectory(file);
			}else if(file.getName().endsWith("nmredata.sdf")) {
				try {
					System.out.println("Testing "+file.getName());
					NmredataReader reader = new NmredataReader(new FileInputStream(file));
					NmreData data = reader.read();
				}catch(Exception ex){
					System.out.println("Problems parsing "+file.getName());
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
				System.out.println();
			}
		}
	}

}
