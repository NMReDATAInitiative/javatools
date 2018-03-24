package de.unikoeln.chemie.nmr.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jcamp.spectrum.Assignment;
import org.jcamp.spectrum.IAssignmentTarget;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.Spectrum;
import org.jcamp.spectrum.assignments.AtomReference;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;

import de.unikoeln.chemie.nmr.data.NmreData;

public class NmredataWriter {
	OutputStream os;
	String separator=", ";

	public NmredataWriter(OutputStream os){
		this.os=os;
	}
	
	public void write(NmreData data) throws CloneNotSupportedException, CDKException, IOException{
		IAtomContainer ac=(IAtomContainer)data.getMolecule().clone();
		ac.setProperty("NMREDATA_VERSION", "1.1\\");
		ac.setProperty("NMREDATA_LEVEL", "1\\");
		StringBuffer assignment=new StringBuffer();
		Map<Double,String> peaklabelmap=new HashMap<>();
		for(Spectrum spectrum : data.getSpectra()){
			for(int i=0;i<((NMRSpectrum)spectrum).getPeakTable().length;i++){
				peaklabelmap.put(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0],"s"+i);
				assignment.append("s"+i+separator+((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]);
				if(((NMRSpectrum)spectrum).getAssignments()!=null){
					for(Assignment assignmentlocal : ((NMRSpectrum)spectrum).getAssignments()){
						for(IAssignmentTarget atomref : assignmentlocal.getTargets()){
							IAtom atom = data.getMolecule().getAtom((((AtomReference)atomref)).getAtomNumber());
							if(atom.getSymbol().equals("H")){
								assignment.append(separator+"H"+(data.getMolecule().getAtomNumber(data.getMolecule().getConnectedAtomsList(atom).get(0))+1));
							}else{
								assignment.append(separator+(data.getMolecule().getAtomNumber(atom)+1));
							}
						}
					}
				}
				assignment.append("\\\r\n");
			}
		}
		ac.setProperty("NMREDATA_ASSIGNMENT", assignment);
		for(Spectrum spectrum : data.getSpectra()){
			StringBuffer spectrumbuffer=new StringBuffer();
			spectrumbuffer.append("Larmor="+((NMRSpectrum)spectrum).getFrequency()+"\\\r\n");
			spectrumbuffer.append("Spectrum_Location=none"+"\\\r\n");//TODO
			for(int i=0;i<((NMRSpectrum)spectrum).getPeakTable().length;i++){
				spectrumbuffer.append(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0]+separator);
				if(((NMRSpectrum)spectrum).getPatternTable()!=null && ((NMRSpectrum)spectrum).getPatternTable()[i]!=null)
					spectrumbuffer.append("S="+((NMRSpectrum)spectrum).getPatternTable()[i].getLabel()+separator);
				spectrumbuffer.append("L="+peaklabelmap.get(((NMRSpectrum)spectrum).getPeakTable()[i].getPosition()[0])+separator);
				spectrumbuffer.append("\\\r\n");
			}
			ac.setProperty("NMREDATA_1D_"+((NMRSpectrum)spectrum).getNucleus(), spectrumbuffer.toString());
		}
		SDFWriter sdfwriter=new SDFWriter(os);
		sdfwriter.write(ac);
		sdfwriter.close();
	}
}