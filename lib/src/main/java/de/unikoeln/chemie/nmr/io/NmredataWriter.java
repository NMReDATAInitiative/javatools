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
import org.jcamp.spectrum.assignments.TwoAtomsReference;
import org.jcamp.spectrum.notes.Note;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.smiles.SmilesGenerator;

import de.unikoeln.chemie.nmr.data.NMR2DSpectrum;
import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.data.NmreData.NmredataVersion;
import de.unikoeln.chemie.nmr.data.Peak2D;
import net.sf.jniinchi.INCHI_RET;

public class NmredataWriter {
	OutputStream os;
	PrintWriter pw;
	String separator=", ";
	SDFWriter sdfwriter;

	public NmredataWriter(OutputStream os){
		this.os=os;
	}
	
	public NmredataWriter(PrintWriter pw){
		this.pw=pw;
	}
	
	public void write(NmreData data, NmredataVersion version) throws CloneNotSupportedException, CDKException, IOException{
		IAtomContainer ac=new AtomContainer();
		if(data.getMolecule()!=null)
			ac = (IAtomContainer)data.getMolecule().clone();
		String endofline="";
		if(version.compareTo(NmreData.NmredataVersion.ONE)>0)
			endofline="\\";
		ac.setProperty("NMREDATA_VERSION", version==NmredataVersion.ONE? "1.0" : version==NmredataVersion.ONEPOINTONE? "1.1\\" : "2.0");
		ac.setProperty("NMREDATA_LEVEL", "1"+endofline);
		ac.setProperty("NMREDATA_ID", data.getID()+endofline);
		if(data.getSolvent()!=null)
			ac.setProperty("NMREDATA_SOLVENT", data.getSolvent()+endofline);
		if(data.getTemperature()>-1)
			ac.setProperty("NMREDATA_TEMPERATURE", data.getTemperature()+" K"+endofline);
		StringBuffer assignment=new StringBuffer();
		Map<Double,String> peaklabelmap=new HashMap<>();
		int k=0;
		for(Spectrum spectrum : data.getSpectra()){
			if(spectrum instanceof NMRSpectrum){
				for(int i=0;i<((NMRSpectrum)spectrum).getPeakTable().length;i++){
					peaklabelmap.put(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0],"s"+k);
					assignment.append("s"+k+separator+((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]);
					if(((NMRSpectrum)spectrum).getAssignments()!=null){
						for(Assignment assignmentlocal : ((NMRSpectrum)spectrum).getAssignments()){
							if(assignmentlocal.getPattern().getPosition()[0]==((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]){
								for(IAssignmentTarget atomref : assignmentlocal.getTargets()){
									IAtom atom = data.getMolecule().getAtom((((AtomReference)atomref)).getAtomNumber());
									if(((NMRSpectrum)spectrum).getNucleus().equals("H") && !atom.getSymbol().equals("H")){
										assignment.append(separator+"H"+(data.getMolecule().indexOf(atom)+1));
									}else {
										assignment.append(separator+(data.getMolecule().indexOf(atom)+1));
									}
								}
							}
						}
					}
					k++;
					assignment.append(endofline+"\r\n");
				}
			}else if(spectrum instanceof NMR2DSpectrum) {
				for(int i=0;i<((NMR2DSpectrum)spectrum).getPeakTable().length;i++){
					if(!peaklabelmap.containsKey(((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[0])) {
						peaklabelmap.put(((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[0],"s"+k);
						assignment.append("s"+k+separator+((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[0]);
						if(((NMR2DSpectrum)spectrum).getAssignments()!=null){
							for(Assignment assignmentlocal : ((NMR2DSpectrum)spectrum).getAssignments()){
								if(assignmentlocal.getPattern().getPosition()[0]==((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[0]){
									for(IAssignmentTarget atomref : assignmentlocal.getTargets()){
										IAtom atom = data.getMolecule().getAtom((((TwoAtomsReference)atomref)).getAtomNumber1()[0]);
										if(((NMR2DSpectrum)spectrum).getXNucleus().equals("H") && !atom.getSymbol().equals("H")){
											assignment.append(separator+"H"+(data.getMolecule().indexOf(atom)+1));
										}else {
											assignment.append(separator+(data.getMolecule().indexOf(atom)+1));
										}
									}
								}
							}
						}
						k++;
						assignment.append(endofline+"\r\n");
					}
					if(!peaklabelmap.containsKey(((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[1])) {
						peaklabelmap.put(((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[1],"s"+k);
						assignment.append("s"+k+separator+((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[1]);
						if(((NMR2DSpectrum)spectrum).getAssignments()!=null){
							for(Assignment assignmentlocal : ((NMR2DSpectrum)spectrum).getAssignments()){
								if(assignmentlocal.getPattern().getPosition()[1]==((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[1]){
									for(IAssignmentTarget atomref : assignmentlocal.getTargets()){
										IAtom atom = data.getMolecule().getAtom((((TwoAtomsReference)atomref)).getAtomNumber2()[0]);
										if(((NMR2DSpectrum)spectrum).getYNucleus().equals("H") && !atom.getSymbol().equals("H")){
											assignment.append(separator+"H"+(data.getMolecule().indexOf(atom)+1));
										}else {
											assignment.append(separator+(data.getMolecule().indexOf(atom)+1));
										}
									}
								}
							}
						}
						k++;
						assignment.append(endofline+"\r\n");
					}
				}				
			}
		}
		ac.setProperty("NMREDATA_ASSIGNMENT", assignment.toString());
		Map<String,Integer> types=new HashMap<>();
		for(Spectrum spectrum : data.getSpectra()){
			StringBuffer spectrumbuffer=new StringBuffer();
	        NoteDescriptor noteDescriptor=new NoteDescriptor("Spectrum_Location");
			spectrumbuffer.append("Spectrum_Location="+((Note)spectrum.getNotes(noteDescriptor).get(0)).getValue()+endofline+"\r\n");//TODO
	        NoteDescriptor jcampDescriptor=new NoteDescriptor("Spectrum_Jcamp");
	        if(data.getVersion().compareTo(NmreData.NmredataVersion.ONEPOINTONE)>0 && spectrum.getNotes(jcampDescriptor)!=null && spectrum.getNotes(jcampDescriptor).size()>0 && ((Note)spectrum.getNotes(jcampDescriptor).get(0)).getValue()!=null)
	        	spectrumbuffer.append("Spectrum_Jcamp="+((Note)spectrum.getNotes(jcampDescriptor).get(0)).getValue()+endofline+"\r\n");
			if(spectrum instanceof NMRSpectrum){
				//we need to count how often each type of spectrum exists for numbering
				if(!types.containsKey(((NMRSpectrum)spectrum).getNucleus()))
					types.put(((NMRSpectrum)spectrum).getNucleus(),0);
				types.put(((NMRSpectrum)spectrum).getNucleus(), types.get(((NMRSpectrum)spectrum).getNucleus()).intValue()+1);
				spectrumbuffer.append("Larmor="+((NMRSpectrum)spectrum).getFrequency()+endofline+"\r\n");
				for(int i=0;i<((NMRSpectrum)spectrum).getPeakTable().length;i++){
					spectrumbuffer.append(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]+separator);
					if(((NMRSpectrum)spectrum).getPatternTable()!=null && ((NMRSpectrum)spectrum).getPatternTable()[i]!=null)
						spectrumbuffer.append("S="+((NMRSpectrum)spectrum).getPatternTable()[i].getLabel()+separator);
					spectrumbuffer.append("L="+peaklabelmap.get(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]));
					spectrumbuffer.append(endofline+"\r\n");
				}
				ac.setProperty("NMREDATA_1D_"+((NMRSpectrum)spectrum).getNucleus()+(types.get(((NMRSpectrum)spectrum).getNucleus()).intValue()>1 ? "#"+types.get(((NMRSpectrum)spectrum).getNucleus()).intValue() : ""), spectrumbuffer.toString());
			}else if(spectrum instanceof NMR2DSpectrum){
				//we need to count how often each type of spectrum exists for numbering
				if(!types.containsKey(((NMR2DSpectrum)spectrum).getXNucleus()+"_"+((NMR2DSpectrum)spectrum).getYNucleus()))
					types.put(((NMR2DSpectrum)spectrum).getXNucleus()+"_"+((NMR2DSpectrum)spectrum).getYNucleus(),0);
				types.put(((NMR2DSpectrum)spectrum).getXNucleus()+"_"+((NMR2DSpectrum)spectrum).getYNucleus(), types.get(((NMR2DSpectrum)spectrum).getXNucleus()+"_"+((NMR2DSpectrum)spectrum).getYNucleus()).intValue()+1);
				spectrumbuffer.append("Larmor="+((NMR2DSpectrum)spectrum).getYFrequency()+endofline+"\r\n");
		        noteDescriptor=new NoteDescriptor("CorType");
				spectrumbuffer.append("CorType="+((Note)spectrum.getNotes(noteDescriptor).get(0)).getValue()+endofline+"\r\n");
				for(int i=0;i<((NMR2DSpectrum)spectrum).getPeakTable().length;i++){
					spectrumbuffer.append(peaklabelmap.get(((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[0])+"/"+peaklabelmap.get(((NMR2DSpectrum)spectrum).getPeakTable()[i].getPosition()[1]));
					if(((NMR2DSpectrum)spectrum).getPeakTable()[i].getHeight()>0)
						spectrumbuffer.append(separator).append("I=").append(((NMR2DSpectrum)spectrum).getPeakTable()[i].getHeight());
					spectrumbuffer.append(endofline+"\r\n");
				}
				ac.setProperty("NMREDATA_2D_"+((NMR2DSpectrum)spectrum).getXNucleus()+"_NJ_"+((NMR2DSpectrum)spectrum).getYNucleus()+(types.get(((NMR2DSpectrum)spectrum).getXNucleus()+"_"+((NMR2DSpectrum)spectrum).getYNucleus()).intValue()>1 ? "#"+types.get(((NMR2DSpectrum)spectrum).getXNucleus()+"_"+((NMR2DSpectrum)spectrum).getYNucleus()).intValue() : ""), spectrumbuffer.toString());//TODO NJ
			}
		}
		if(data.getMolecule()!=null) {
			ac.setProperty("NMREDATA_SMILES", SmilesGenerator.generic().create(data.getMolecule()));
			InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
  	  		InChIGenerator gen = factory.getInChIGenerator(data.getMolecule());
			if (gen.getReturnStatus() == INCHI_RET.OKAY || gen.getReturnStatus() == INCHI_RET.WARNING)
	  	  		ac.setProperty("NMREDATA_INCHI", gen.getInchi());
		}
  	  	if(version.compareTo(NmreData.NmredataVersion.ONEPOINTONE)>0 && data.getAuthor()!=null)
  	  		ac.setProperty("NMREDATA_AUTHOR", data.getAuthor());
		if(os!=null)
			sdfwriter=new SDFWriter(os);
		else
			sdfwriter=new SDFWriter(pw);
		sdfwriter.write(ac);
		if(version.compareTo(NmreData.NmredataVersion.ONEPOINTONE)>0 && data.has3dCoordinates())
  	  		sdfwriter.write(data.getMoleculeOriginal3d());
	}
	
	public void close() throws IOException{
		sdfwriter.close();
	}
}