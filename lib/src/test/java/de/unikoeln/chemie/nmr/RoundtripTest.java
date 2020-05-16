package de.unikoeln.chemie.nmr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.jcamp.parser.JCAMPException;
import org.openscience.cdk.exception.CDKException;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.data.NmreData.NmredataVersion;
import de.unikoeln.chemie.nmr.io.NmreDataException;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import de.unikoeln.chemie.nmr.io.NmredataWriter;
import junit.framework.Assert;
import junit.framework.TestCase;

public class RoundtripTest  extends TestCase{
	
	public void testStandardRoundtrip() throws JCAMPException, CloneNotSupportedException, CDKException, IOException, NmreDataException{
		NmredataWriterTest.writeStandardFile();
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/test.nmredata.sd");
        InputStream ins = new FileInputStream(testfile);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        File testfile2=new File(System.getProperty("java.io.tmpdir")+"/test2.nmredata.sd");
        FileOutputStream fos=new FileOutputStream(testfile2);
        NmredataWriter writer=new NmredataWriter(fos);
        writer.write(data, NmredataVersion.ONEPOINTONE);
        writer.close();
        StringBuffer result=new StringBuffer();
        try (BufferedReader br = new BufferedReader(new FileReader(testfile2))) {
            String line;
            while ((line = br.readLine()) != null) {
            	result.append(line+"\r\n");
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(testfile))) {
            String line;
            boolean inblock=false;
            StringBuffer block=null;
            while ((line = br.readLine()) != null) {
               if(line.startsWith("> <NMREDATA")){
            	   inblock=true;
            	   block=new StringBuffer(line+"\r\n");
               }else if(line.trim().length()==0 && inblock){
            	   Assert.assertTrue(result.toString().contains(block));
               }else if(line.trim().length()==0){
            	   inblock=false;
               }else if(inblock){
            	   block.append(line+"\r\n");
               }
            }
        }
	}
}
