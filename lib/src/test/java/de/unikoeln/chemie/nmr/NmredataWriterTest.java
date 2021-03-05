package de.unikoeln.chemie.nmr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jcamp.parser.JCAMPException;
import org.jcamp.parser.JCAMPReader;
import org.jcamp.spectrum.ArrayData;
import org.jcamp.spectrum.Assignment;
import org.jcamp.spectrum.IAssignmentTarget;
import org.jcamp.spectrum.IDataArray1D;
import org.jcamp.spectrum.IOrderedDataArray1D;
import org.jcamp.spectrum.Multiplicity;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.OrderedArrayData;
import org.jcamp.spectrum.Pattern;
import org.jcamp.spectrum.Peak1D;
import org.jcamp.spectrum.assignments.AtomReference;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.jcamp.units.CommonUnit;
import org.jcamp.units.Unit;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import de.unikoeln.chemie.nmr.data.NMR2DSpectrum;
import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.data.NmreData.NmredataVersion;
import de.unikoeln.chemie.nmr.data.Peak2D;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import de.unikoeln.chemie.nmr.io.NmredataWriter;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NmredataWriterTest extends TestCase{
	
	public void testWriteStandard() throws JCAMPException, CloneNotSupportedException, IOException, CDKException{
		writeStandardFile();
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/test.nmredata.sd");
        assertTrue(testfile.exists());
        assertTrue(testfile.length()>10);
		String filenamesample = "result_standard.nmredata.sdf";
        InputStream inssample = NmredataWriterTest.class.getResource(filenamesample).openStream();
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
	
	public static void writeStandardFile() throws JCAMPException, CloneNotSupportedException, CDKException, IOException{
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/test.nmredata.sd");
        if(testfile.exists())
        	testfile.delete();
        FileOutputStream fos=new FileOutputStream(testfile);
        NmredataWriter writer=new NmredataWriter(fos);
        NmreData data=getNmredata11();
        writer.write(data, NmredataVersion.ONEPOINTONE);
        writer.close();
        fos.close();
	}
	
	public static void writeStandardFile2() throws JCAMPException, CloneNotSupportedException, CDKException, IOException{
        File testfile=new File(System.getProperty("java.io.tmpdir")+"/test.nmredata.sd");
        if(testfile.exists())
        	testfile.delete();
        FileOutputStream fos=new FileOutputStream(testfile);
        NmredataWriter writer=new NmredataWriter(fos);
        NmreData data=getNmredata2();
        writer.write(data, NmredataVersion.TWO);
        writer.close();
        fos.close();
	}

    private static NmreData getNmredata2() throws CDKException, CloneNotSupportedException, JCAMPException {
    	NmreData data=getNmredata11();
    	data.setAuthor("Version=1\n" + 
    			"Author=Stefan Kuhn\n" + 
    			"Affilition=De Montfort University, Leicester\n" + 
    			"ORCID=0000-0002-5990-4157");
    	data.setVersion(NmredataVersion.TWO);
    	IAtomContainer ac = data.getMolecule().clone();
    	ac.getProperties().clear();
        data.setMolecule3d(ac);
    	return data;
    }
    
    private static NmreData getNmredata11() throws CDKException, CloneNotSupportedException, JCAMPException {
		double freq=400;
		String location=null;
        Peak1D[] peaks1d = new Peak1D[3];
		peaks1d[0]=new Peak1D(5,0);
		peaks1d[1]=new Peak1D(10,0);
		peaks1d[2]=new Peak1D(15,0);
        Unit xUnit =  CommonUnit.hertz;
        Unit yUnit = CommonUnit.intensity;
        double reference = 0;
		Assignment[] assignmentslocal=new Assignment[3];
		assignmentslocal[0]=new Assignment(new Pattern(peaks1d[0].getPosition()[0], Multiplicity.UNKNOWN), new IAssignmentTarget[]{new AtomReference(null, 0)});
		assignmentslocal[1]=new Assignment(new Pattern(peaks1d[1].getPosition()[0], Multiplicity.UNKNOWN), new IAssignmentTarget[]{new AtomReference(null, 1)});
		assignmentslocal[2]=new Assignment(new Pattern(peaks1d[2].getPosition()[0], Multiplicity.UNKNOWN), new IAssignmentTarget[]{new AtomReference(null, 2),new AtomReference(null, 3)});
        double[][] xy=new double[0][];
        IOrderedDataArray1D x = new OrderedArrayData(new double[0], xUnit);
        IDataArray1D y = new ArrayData(new double[0], yUnit);
        if(peaks1d.length>0){
	        xy = NmredataReader.peakTableToPeakSpectrum(peaks1d);
	        x = new OrderedArrayData(xy[0], xUnit);
	        y = new ArrayData(xy[1], yUnit);
        }        
        NMRSpectrum spectrum = new NMRSpectrum(x, y, "C", freq, reference, false, JCAMPReader.RELAXED);
        spectrum.setPeakTable(peaks1d);
        spectrum.setAssignments(assignmentslocal);
        NoteDescriptor noteDescriptor=new NoteDescriptor("Spectrum_Location");
        spectrum.setNote(noteDescriptor, location);
        Peak2D[] peaks2d = new Peak2D[3];
		peaks2d[0]=new Peak2D(5,15,0);
		peaks2d[1]=new Peak2D(10,5,0);
		peaks2d[2]=new Peak2D(15,10,0);
        xUnit =  CommonUnit.hertz;
        yUnit =  CommonUnit.hertz;
        Unit zUnit = CommonUnit.intensity;
        xy=new double[0][];
        IOrderedDataArray1D x2 = new OrderedArrayData(new double[0], xUnit);
        IOrderedDataArray1D y2 = new OrderedArrayData(new double[0], xUnit);
        IDataArray1D z2 = new ArrayData(new double[0], yUnit);
        if(peaks2d.length>0){
	        xy = NmredataReader.peakTableToPeakSpectrum(peaks2d);
	        x2 = new OrderedArrayData(xy[0], xUnit);
	        y2 = new OrderedArrayData(xy[1], yUnit);
	        z2 = new ArrayData(xy[2], zUnit);
        }
    	NMR2DSpectrum cosy = new NMR2DSpectrum(x2, y2, z2, new String[] {"1H","1H"}, new double[] { freq, freq}, new double[] {reference,reference});
    	cosy.setPeakTable(peaks2d);
		cosy.setNote(noteDescriptor, location);
        NoteDescriptor noteDescriptor2=new NoteDescriptor("CorType");
        cosy.setNote(noteDescriptor2, "cosy");
        NmreData data=new NmreData();
        data.addSpectrum(spectrum);
        data.addSpectrum(cosy);
        data.setMolecule(makeBenzene());
        data.setID("Title=Title=NMReDATA test file");
        data.setLevel(0);
        data.setVersion(NmredataVersion.ONE);
        data.setTemperature(298);
        return data;
	}

	public static IAtomContainer makeBenzene() throws CDKException {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.DOUBLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.DOUBLE); // 6
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder.getInstance(mol.getBuilder()).addImplicitHydrogens(mol);
        return mol;
    }
}
