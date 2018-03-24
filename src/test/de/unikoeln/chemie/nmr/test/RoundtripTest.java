package de.unikoeln.chemie.nmr.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.jcamp.parser.JCAMPException;
import org.openscience.cdk.exception.CDKException;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.NmreDataException;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import de.unikoeln.chemie.nmr.io.NmredataWriter;
import junit.framework.TestCase;

public class RoundtripTest  extends TestCase{
	
	public void testStandardRoundtrip() throws JCAMPException, CloneNotSupportedException, CDKException, IOException, NmreDataException{
		NmredataWriterTest.writeStandardFile();
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/test.nmredata.sdf");
        InputStream ins = new FileInputStream(testfile);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        File testfile2=new File(System.getProperty("java.io.tmpdir")+"/test2.nmredata.sdf");
        FileOutputStream fos=new FileOutputStream(testfile2);
        NmredataWriter writer=new NmredataWriter(fos);
        writer.write(data);
        fos.close();
        assertTrue(FileUtils.contentEquals(testfile, testfile2));
	}

}
