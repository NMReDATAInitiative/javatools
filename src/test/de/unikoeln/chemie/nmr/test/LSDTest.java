package de.unikoeln.chemie.nmr.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jcamp.parser.JCAMPException;
import org.openscience.cdk.exception.CDKException;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.LSDWriter;
import de.unikoeln.chemie.nmr.io.NmreDataException;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import junit.framework.Assert;
import junit.framework.TestCase;

public class LSDTest extends TestCase{
	
	public void testSimple() throws IOException, NmreDataException, JCAMPException, CDKException, CloneNotSupportedException {
		String filename = "testdata/cmcse.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/cmcse.lsd");
        FileOutputStream pw = new FileOutputStream(testfile);
        LSDWriter lsdwrtier=new LSDWriter(pw);
        lsdwrtier.write(data);
        lsdwrtier.close();
        Assert.assertTrue(testfile.exists());
		String filenamesample = "testdata/result_cmcse.lsd";
        InputStream inssample = this.getClass().getClassLoader().getResourceAsStream(filenamesample);
        try (BufferedReader br = new BufferedReader(new FileReader(testfile)); BufferedReader brsample = new BufferedReader(new InputStreamReader(inssample, "UTF-8"))) {
            String line;
            String linesample;
            //we ignore first two lines with date etc.
            br.readLine();
            br.readLine();
            brsample.readLine();
            brsample.readLine();
            while ((line = br.readLine()) != null && (linesample = brsample.readLine()) != null) {
               Assert.assertEquals(linesample, line);
            }
        }
	}

	public void testMRC() throws IOException, NmreDataException, JCAMPException, CDKException, CloneNotSupportedException {
		String filename = "testdata/MRC_Cyprinol_2018.nmredata.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/MRC_Cyprinol_2018.lsd");
        FileOutputStream pw = new FileOutputStream(testfile);
        LSDWriter lsdwrtier=new LSDWriter(pw);
        lsdwrtier.write(data);
        lsdwrtier.close();
        Assert.assertTrue(testfile.exists());
		String filenamesample = "testdata/result_MRC_Cyprinol_2018.lsd";
        InputStream inssample = this.getClass().getClassLoader().getResourceAsStream(filenamesample);
        try (BufferedReader br = new BufferedReader(new FileReader(testfile)); BufferedReader brsample = new BufferedReader(new InputStreamReader(inssample, "UTF-8"))) {
            String line;
            String linesample;
            //we ignore first two lines with date etc.
            br.readLine();
            br.readLine();
            brsample.readLine();
            brsample.readLine();
            while ((line = br.readLine()) != null && (linesample = brsample.readLine()) != null) {
               Assert.assertEquals(linesample, line);
            }
        }
	}
}
