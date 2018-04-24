package de.unikoeln.chemie.nmr.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jcamp.spectrum.Assignment;
import org.jcamp.spectrum.IAssignmentTarget;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.Spectrum;
import org.jcamp.spectrum.assignments.AtomReference;
import org.jcamp.spectrum.notes.Note;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.smiles.SmilesGenerator;

import de.unikoeln.chemie.nmr.data.NmreData;
import net.sf.jniinchi.INCHI_RET;

public class NmredataWriter {
	OutputStream os;
	PrintWriter pw;
	String separator=", ";

	public NmredataWriter(OutputStream os){
		this.os=os;
	}
	
	public NmredataWriter(PrintWriter pw){
		this.pw=pw;
	}
	
	public void write(NmreData data) throws CloneNotSupportedException, CDKException, IOException{
		IAtomContainer ac=(IAtomContainer)data.getMolecule().clone();
		ac.setProperty("NMREDATA_VERSION", "1.1\\");
		ac.setProperty("NMREDATA_LEVEL", "1\\");
		if(data.getSolvent()!=null)
			ac.setProperty("NMREDATA_SOLVENT", data.getSolvent()+"\\");
		StringBuffer assignment=new StringBuffer();
		Map<Double,String> peaklabelmap=new HashMap<>();
		int k=0;
		for(Spectrum spectrum : data.getSpectra()){
			for(int i=0;i<((NMRSpectrum)spectrum).getPeakTable().length;i++){
				peaklabelmap.put(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0],"s"+i);
				assignment.append("s"+k+separator+((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]);
				if(((NMRSpectrum)spectrum).getAssignments()!=null){
					for(Assignment assignmentlocal : ((NMRSpectrum)spectrum).getAssignments()){
						if(assignmentlocal.getPattern().getPosition()[0]==((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]){
							for(IAssignmentTarget atomref : assignmentlocal.getTargets()){
								IAtom atom = data.getMolecule().getAtom((((AtomReference)atomref)).getAtomNumber());
								if(((NMRSpectrum)spectrum).getNucleus().equals("H") && !atom.getSymbol().equals("H")){
									assignment.append(separator+"H"+(data.getMolecule().getAtomNumber(atom)+1));
								}else {
									assignment.append(separator+(data.getMolecule().getAtomNumber(atom)+1));
								}
							}
						}
					}
				}
				k++;
				assignment.append("\\\r\n");
			}
		}
		ac.setProperty("NMREDATA_ASSIGNMENT", assignment);
		for(Spectrum spectrum : data.getSpectra()){
			StringBuffer spectrumbuffer=new StringBuffer();
			spectrumbuffer.append("Larmor="+((NMRSpectrum)spectrum).getFrequency()+"\\\r\n");
	        NoteDescriptor noteDescriptor=new NoteDescriptor("Spectrum_Location");
			spectrumbuffer.append("Spectrum_Location="+((Note)spectrum.getNotes(noteDescriptor).get(0)).getValue()+"\\\r\n");//TODO
			for(int i=0;i<((NMRSpectrum)spectrum).getPeakTable().length;i++){
				spectrumbuffer.append(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]+separator);
				if(((NMRSpectrum)spectrum).getPatternTable()!=null && ((NMRSpectrum)spectrum).getPatternTable()[i]!=null)
					spectrumbuffer.append("S="+((NMRSpectrum)spectrum).getPatternTable()[i].getLabel()+separator);
				spectrumbuffer.append("L="+peaklabelmap.get(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]));
				spectrumbuffer.append("\\\r\n");
			}
			ac.setProperty("NMREDATA_1D_"+((NMRSpectrum)spectrum).getNucleus(), spectrumbuffer.toString());
		}
		ac.setProperty("NMREDATA_SMILES", SmilesGenerator.generic().create(data.getMolecule()));
  	  	InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
  	  	InChIGenerator gen = factory.getInChIGenerator(data.getMolecule());
  	  	if (gen.getReturnStatus() == INCHI_RET.OKAY || gen.getReturnStatus() == INCHI_RET.WARNING)
  	  		ac.setProperty("NMREDATA_INCHI", gen.getInchi());
		SDFWriter sdfwriter;
		if(os!=null)
			sdfwriter=new SDFWriter(os);
		else
			sdfwriter=new SDFWriter(pw);
		sdfwriter.write(ac);
		sdfwriter.close();
	}
}