package de.unikoeln.chemie.nmr.test;

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
        BufferedReader br = new BufferedReader(new FileReader(testfile));
        String strLine;
        String origfile=FileUtils.readFileToString(testfile2);
        while ((strLine = br.readLine()) != null)   {
            assertTrue(origfile.indexOf(strLine)>-1);
        }
        br.close();
	}

}
