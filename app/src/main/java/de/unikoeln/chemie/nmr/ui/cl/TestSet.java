package de.unikoeln.chemie.nmr.ui.cl;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;

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
				NmreData data = null;
				NmredataReader reader = null;
				try {
					System.out.println("Testing "+file.getName());
					reader = new NmredataReader(new FileInputStream(file));
					data = reader.read();
				}catch(Exception ex){
					System.out.println("Problems parsing "+file.getName());
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
				try {
					String filename=file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(File.separator)+1,file.getAbsolutePath().length()-13);
					if(Character.isDigit(filename.charAt(0)))
						filename="c"+filename;
					filename=filename.replace("_", "");
					filename=filename.replace("-", "");
					File javafile=new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf(File.separator)+1)+filename+".java");
					if(javafile.exists()) {
						JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
						int compilationResult =	compiler.run(null, null, null, javafile.getAbsolutePath());
						if(compilationResult == 0){
							File dirde=new File(file.getParent()+"/de/unikoeln/chemie/nmr/ui/cl");
							if(!dirde.exists())
								dirde.mkdirs();					
							File classfile=new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf(File.separator)+1)+filename+".class");
							FileUtils.copyFileToDirectory(classfile, dirde);
							classfile.delete();
							URL[] classpath= {new File(javafile.getParent()).toURL()};
							URLClassLoader classLoader = new URLClassLoader(classpath, TestSet.class.getClassLoader());
							Class<?> testclass = Class.forName("de.unikoeln.chemie.nmr.ui.cl."+filename,true,classLoader);
							Object testclassobj=classLoader.loadClass("de.unikoeln.chemie.nmr.ui.cl."+filename).getConstructor().newInstance();
							Method method = testclass.getMethod("setNmreData", NmreData.class, List.class);
							method.invoke(testclassobj,data,reader.couplings);
							System.out.println("Performing java tests for "+file.getName()+" using "+javafile.getAbsolutePath());
							method = testclass.getMethod("test");
							method.invoke(testclassobj);
						}else{
							System.out.println("Compilation Failed for "+javafile.getName());
						}
					}
					System.out.println("Done with "+file.getName());
				}catch(Exception ex){
					System.out.println("Problems executing tests "+file.getName());
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
				System.out.println();
			}
		}
	}

}
