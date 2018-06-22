package de.unikoeln.chemie.nmr.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.openscience.cdk.exception.CDKException;

import de.unikoeln.chemie.nmr.data.NmreDataRecord;

public class NmredataRecordWriter {
	OutputStream os;

	public NmredataRecordWriter(OutputStream os){
		this.os=os;
	}
	
	/**
	 * Writes the NMReData record to an output stream
	 * 
	 * @param data The NmrDataRecords object.
	 * @param id The name which will be used for the nmredata sd file (this will be id.sd) and the root directory in the zip.
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 * @throws CDKException
	 */
	public void write(NmreDataRecord data, String id) throws IOException, CloneNotSupportedException, CDKException{
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ZipOutputStream zipout=new ZipOutputStream(baos);
        for(File file : data.getFiles()){
        	zipDir(file, zipout, new File(file.getAbsolutePath()+File.separator+".."), id);
        }
        zipout.putNextEntry(new ZipEntry(id+"/"+id+".sd"));
        NmredataWriter writer=new NmredataWriter(zipout);
        writer.write(data.getData());
        writer.close();
        InputStream is =  new ByteArrayInputStream(baos.toByteArray());
        byte[] buf = new byte[32 * 1024]; // 32k buffer
        int nRead = 0;
        while( (nRead=is.read(buf)) != -1 ) {
            os.write(buf, 0, nRead);
        }
	}

	  /**
	   *  Recursivly zips a directory in an zipoutputstream
	   *
	   * @param  zipDir           The directory.
	   * @param  zos              The stream
	 * @param id 
	   * @exception  IOException  Description of Exception
	   */
	  public static void zipDir(File zipDir, ZipOutputStream zos, File tempdir, String id) throws IOException {
	    /*if(zipDir.isFile()){
	    	int bytesIn = 0;
	    	byte[] readBuffer = new byte[2156];
        	FileInputStream fis = new FileInputStream(zipDir);
            ZipEntry anEntry = new ZipEntry(id+"/"+zipDir.getPath());
        	//place the zip entry in the ZipOutputStream object
        	zos.putNextEntry(anEntry);
        	//now write the content of the file to the ZipOutputStream
        	while ((bytesIn = fis.read(readBuffer)) != -1) {
        		zos.write(readBuffer, 0, bytesIn);
        	}
        	//close the Stream
        	fis.close();
        }else{*/
		    //get a listing of the directory content
		    String[] dirList = zipDir.list();
		    byte[] readBuffer = new byte[2156];
		    int bytesIn = 0;
		    //loop through dirList, and zip the files
		    for (int i = 0; i < dirList.length; i++) {
		      File f = new File(zipDir, dirList[i]);
		      if (f.isDirectory()) {
		        //if the File object is a directory, call this
		        //function again to add its content recursively
		        String filePath = f.getPath();
		        zipDir(new File(filePath), zos, tempdir, id);
		        //loop again
		        continue;
		      }
		      //if we reached here, the File object f was not a directory
		      //create a FileInputStream on top of f
		      FileInputStream fis = new FileInputStream(f);
		      ZipEntry anEntry = new ZipEntry(id+"/"+getRelativePath(tempdir,f));
		      //place the zip entry in the ZipOutputStream object
		      zos.putNextEntry(anEntry);
		      //now write the content of the file to the ZipOutputStream
		      while ((bytesIn = fis.read(readBuffer)) != -1) {
		        zos.write(readBuffer, 0, bytesIn);
		      }
		      //close the Stream
		      fis.close();
		  //}
	    }
	  }

		/**
		 * get relative path of File 'f' with respect to 'home' directory
		 * example : home = /a/b/c
		 *           f    = /a/d/e/x.txt
		 *           s = getRelativePath(home,f) = ../../d/e/x.txt
		 * @param home base path, should be a directory, not a file, or it doesn't make sense
		 * @param f file to generate path for
		 * @return path from home to f as a string
		 */
		public static String getRelativePath(File home,File f){
			List<String> homelist;
			List<String> filelist;
			String s;

			homelist = getPathList(home);
			filelist = getPathList(f);
			s = matchPathLists(homelist,filelist);

			return s;
		}

		/**
		 * break a path down into individual elements and add to a list.
		 * example : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
		 * @param f input file
		 * @return a List collection with the individual elements of the path in reverse order
		 */
		private static List<String> getPathList(File f) {
			List<String> l = new ArrayList<String>();
			File r;
			try {
				r = f.getCanonicalFile();
				while(r != null) {
					l.add(r.getName());
					r = r.getParentFile();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				l = null;
			}
			return l;
		}

		/**
		 * figure out a string representing the relative path of
		 * 'f' with respect to 'r'
		 * @param r home path
		 * @param f path of file
		 */
		private static String matchPathLists(List<String> r,List<String> f) {
			int i;
			int j;
			String s;
			// start at the beginning of the lists
			// iterate while both lists are equal
			s = "";
			i = r.size()-1;
			j = f.size()-1;

			// first eliminate common root
			while((i >= 0)&&(j >= 0)&&(r.get(i).equals(f.get(j)))) {
				i--;
				j--;
			}

			// for each remaining level in the home path, add a ..
			for(;i>=0;i--) {
				s += ".." + File.separator;
			}

			// for each level in the file path, add the path
			for(;j>=1;j--) {
				s += f.get(j) + File.separator;
			}

			// file name
			s += f.get(j);
			return s;
		}

		public void close() throws IOException{
			os.close();
		}
}
