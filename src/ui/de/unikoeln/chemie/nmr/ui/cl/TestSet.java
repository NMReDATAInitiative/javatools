package de.unikoeln.chemie.nmr.ui.cl;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

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
				try {
					System.out.println("Testing "+file.getName());
					NmredataReader reader = new NmredataReader(new FileInputStream(file));
					data = reader.read();
				}catch(Exception ex){
					System.out.println("Problems parsing "+file.getName());
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
				try {
					File javafile=new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-12)+"java");
					if(javafile.exists()) {
						JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
						int compilationResult =	compiler.run(null, null, null, javafile.getAbsolutePath());
						if(compilationResult == 0){
							File dirde=new File(file.getParent()+"/de/unikoeln/chemie/nmr/ui/gui");
							if(!dirde.exists())
								dirde.mkdirs();					
							File classfile=new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-12)+"class");
							FileUtils.copyFileToDirectory(classfile, dirde);
							classfile.delete();
							URL[] classpath= {new File(javafile.getParent()).toURL()};
							URLClassLoader classLoader = new URLClassLoader(classpath, TestSet.class.getClassLoader());
							Class<?> testclass = Class.forName("de.unikoeln.chemie.nmr.ui.gui.Asunepravir",true,classLoader);
							Object testclassobj=classLoader.loadClass("de.unikoeln.chemie.nmr.ui.gui.Asunepravir").getConstructor().newInstance();
							Method method = testclass.getMethod("setNmreData", NmreData.class);
							method.invoke(testclassobj,data);
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
