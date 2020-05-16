package de.unikoeln.chemie.nmr;

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
		String filename = "example.nmredata.sdf";
        InputStream ins = NmredataReaderTest.class.getResource(filename).openStream();
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(32, data.getMolecule().getAtomCount());
        Assert.assertEquals(36, data.getMolecule().getBondCount());
        Assert.assertEquals(2, data.getSpectra().size());
        Assert.assertEquals(1.3, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable()[0].getPosition()[0]);
        Assert.assertEquals(17, ((AtomReference)((NMRSpectrum)data.getSpectra().get(0)).getAssignments()[0].getTargets()[0]).getAtomNumber());
        Assert.assertEquals(6, ((NMRSpectrum)data.getSpectra().get(0)).getAssignments()[0].getTargets().length);
	}
	
	public void testHAP() throws Exception, IOException{
		String filename = "HAP_benzo(a)pyrene_assignments_1.nmredata.sdf";
        InputStream ins = NmredataReaderTest.class.getResource(filename).openStream();
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(20, data.getMolecule().getAtomCount());
        Assert.assertEquals(24, data.getMolecule().getBondCount());
        Assert.assertEquals(12, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable().length);
        Assert.assertEquals(20, ((NMRSpectrum)data.getSpectra().get(1)).getPeakTable().length);
        Assert.assertEquals(14, ((NMR2DSpectrum)data.getSpectra().get(3)).getPeakTable().length);
        Assert.assertEquals(7.7914, ((NMR2DSpectrum)data.getSpectra().get(3)).getPeakTable()[0].getPosition()[0]);
        Assert.assertEquals(7.8453, ((NMR2DSpectrum)data.getSpectra().get(3)).getPeakTable()[0].getPosition()[1]);
        Assert.assertEquals(6, data.getSpectra().size());
	}

	public void testCmcse() throws Exception, IOException{
		String filename = "cmcse.sdf";
        InputStream ins = NmredataReaderTest.class.getResource(filename).openStream();
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
		String filename = "test.nmredata.sdf";
        InputStream ins = NmredataReaderTest.class.getResource(filename).openStream();
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(6, data.getMolecule().getAtomCount());
        Assert.assertEquals(6, data.getMolecule().getBondCount());
        Assert.assertEquals(1, data.getSpectra().size());
        Assert.assertEquals(3, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable().length);
	}

	public void testReadMnova() throws Exception, IOException{
		String filename = "4-picolin_108-89-4.nmredata_korr.sdf";
        InputStream ins = NmredataReaderTest.class.getResource(filename).openStream();
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(7, data.getMolecule().getAtomCount());
        Assert.assertEquals(7, data.getMolecule().getBondCount());
        Assert.assertEquals(6, data.getSpectra().size());
        Assert.assertEquals(3, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable().length);
        Assert.assertEquals(3, ((AtomReference)((NMRSpectrum)data.getSpectra().get(0)).getAssignments()[2].getTargets()[0]).getAtomNumber());
        Assert.assertEquals(5, ((AtomReference)((NMRSpectrum)data.getSpectra().get(0)).getAssignments()[2].getTargets()[1]).getAtomNumber());
	}
	
	public void testReadDamien() throws Exception, IOException{
		String filename = "compound1_with_jcamp.nmredata.sdf";
        InputStream ins = NmredataReaderTest.class.getResource(filename).openStream();
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(17, data.getMolecule().getAtomCount());
        Assert.assertEquals(17, data.getMolecule().getBondCount());
        Assert.assertEquals(1, data.getSpectra().size());
        Assert.assertEquals(14, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable().length);
	}
	
	
	public void testReadChristophe() throws Exception, IOException{
		String filename = "lom-la-288-011.nmredata.sdf";
        InputStream ins = NmredataReaderTest.class.getResource(filename).openStream();
        NmredataReader reader = new NmredataReader(ins);
        NmreData data = reader.read();
        Assert.assertEquals(24, data.getMolecule().getAtomCount());
	    Assert.assertEquals(24, data.getMolecule().getBondCount());
	    Assert.assertEquals(9, data.getSpectra().size());
	    Assert.assertEquals(9, ((NMRSpectrum)data.getSpectra().get(0)).getPeakTable().length);
	    Assert.assertEquals(11, ((NMRSpectrum)data.getSpectra().get(2)).getPeakTable().length);
	}
}
