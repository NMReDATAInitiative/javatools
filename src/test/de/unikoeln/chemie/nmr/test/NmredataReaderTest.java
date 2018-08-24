package de.unikoeln.chemie.nmr.test;

import java.io.IOException;
import java.io.InputStream;

import org.jcamp.parser.JCAMPException;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.assignments.AtomReference;
import org.openscience.cdk.exception.CDKException;

import de.unikoeln.chemie.nmr.data.NMR2DSpectrum;
import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NmredataReaderTest  extends TestCase{
	
	public void testSimple() throws Exception, IOException{
		String filename = "testdata/example.nmredata.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(32, data.getMolecule().getAtomCount());
        Assert.assertEquals(36, data.getMolecule().getBondCount());
        Assert.assertEquals(1, data.getSpectra().size());
        Assert.assertEquals(1.3, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable()[0].getPosition()[0]);
        Assert.assertEquals(17, ((AtomReference)((NMRSpectrum)data.getSpectra().get(0)).getAssignments()[0].getTargets()[0]).getAtomNumber());
        Assert.assertEquals(6, ((NMRSpectrum)data.getSpectra().get(0)).getAssignments()[0].getTargets().length);
	}
	
	public void testHAP() throws Exception, IOException{
		String filename = "testdata/HAP_benzo(a)pyrene_assignments_1.nmredata.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(20, data.getMolecule().getAtomCount());
        Assert.assertEquals(24, data.getMolecule().getBondCount());
        Assert.assertEquals(12, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable().length);
        Assert.assertEquals(6, data.getSpectra().size());
	}

	public void testCmcse() throws Exception, IOException{
		String filename = "testdata/cmcse.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(8, data.getMolecule().getAtomCount());
        Assert.assertEquals(7, data.getMolecule().getBondCount());
        Assert.assertEquals(5, data.getSpectra().size());
        Assert.assertEquals(5, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable().length);
        Assert.assertEquals(6, ((NMRSpectrum)data.getSpectra().get(1)).getPeakTable().length);
        Assert.assertEquals(3, ((NMR2DSpectrum)data.getSpectra().get(2)).getPeakTable().length);
	}
	
	public void testReadStandard() throws Exception, IOException{
		String filename = "testdata/test.nmredata.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(6, data.getMolecule().getAtomCount());
        Assert.assertEquals(6, data.getMolecule().getBondCount());
        Assert.assertEquals(1, data.getSpectra().size());
        Assert.assertEquals(3, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable().length);
	}
}
