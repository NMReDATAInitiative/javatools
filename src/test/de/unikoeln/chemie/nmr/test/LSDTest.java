package de.unikoeln.chemie.nmr.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.jcamp.parser.JCAMPException;
import org.openscience.cdk.exception.CDKException;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.LSDWriter;
import de.unikoeln.chemie.nmr.io.NmreDataException;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import junit.framework.Assert;
import junit.framework.TestCase;

public class LSDTest extends TestCase{
	
	public void testSimple() throws IOException, NmreDataException, JCAMPException, CDKException {
		String filename = "testdata/cmcse.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/cmcse.lsd");
        FileOutputStream pw = new FileOutputStream(testfile);
        LSDWriter lsdwrtier=new LSDWriter(pw);
        lsdwrtier.write(data, reader.getSignals(), reader.getAssignments());
        lsdwrtier.close();
        Assert.assertTrue(testfile.exists());
	}

	public void testMRC() throws IOException, NmreDataException, JCAMPException, CDKException {
		String filename = "testdata/MRC_Cyprinol_2018.nmredata.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/MRC_Cyprinol_2018.lsd");
        FileOutputStream pw = new FileOutputStream(testfile);
        LSDWriter lsdwrtier=new LSDWriter(pw);
        lsdwrtier.write(data, reader.getSignals(), reader.getAssignments());
        lsdwrtier.close();
        Assert.assertTrue(testfile.exists());
	}
}
