package de.unikoeln.chemie.nmr.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcamp.spectrum.Assignment;
import org.jcamp.spectrum.IAssignmentTarget;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.Spectrum;
import org.jcamp.spectrum.Spectrum1D;
import org.jcamp.spectrum.Spectrum2D;
import org.jcamp.spectrum.assignments.AtomReference;
import org.jcamp.spectrum.assignments.TwoAtomsReference;
import org.jcamp.spectrum.notes.Note;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

import de.unikoeln.chemie.nmr.data.NMR2DSpectrum;
import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.data.Peak2D;

public class LSDWriter {
	OutputStream os;
	PrintWriter pw;
	String separator=", ";
	BufferedWriter sdfwriter;

	public LSDWriter(OutputStream os){
		this.os=os;
	}
	
	public LSDWriter(PrintWriter pw){
		this.pw=pw;
	}
	
	public void write(NmreData data) throws IOException{
		if(os!=null)
			sdfwriter=new BufferedWriter(new OutputStreamWriter(os));
		else
			sdfwriter=new BufferedWriter(pw);
		sdfwriter.write("; file generated by NMReDATA javatools\r\n");
		sdfwriter.write("; "+new Date()+"\r\n");
		for(IAtom atom : data.getMolecule().atoms()) {
			if(!atom.getSymbol().equals("H")) {
				int doublebonds=0;
				for(IBond bond : data.getMolecule().getConnectedBondsList(atom)) {
					if(bond.getOrder()==Order.DOUBLE)
						doublebonds++;
				}
				String valence="";
				if(atom.getSymbol().equals("S"))
					valence=(int)data.getMolecule().getBondOrderSum(atom)+"";
				sdfwriter.write("MULT "+(data.getMolecule().indexOf(atom)+1)+" "+atom.getSymbol()+valence+" "+(doublebonds==2 ? 1 :atom.getHybridization()==IAtomType.Hybridization.SP2 ? 2 :atom.getHybridization()==IAtomType.Hybridization.SP3 ? 3 : 1) +" "+(getHcount(data.getMolecule(), atom)+atom.getImplicitHydrogenCount())+" "+atom.getFormalCharge()+"\r\n");
			}
		}
		sdfwriter.write("\r\n");
		Map<Double,IAssignmentTarget[]> assignmenets1d=new HashMap<Double,IAssignmentTarget[]>();
		for(Spectrum spectrum : data.getSpectra()) {
			if(spectrum instanceof Spectrum1D && ((Spectrum1D) spectrum).getAssignments()!=null) {
				for(Assignment assignment : ((Spectrum1D) spectrum).getAssignments()) {
					assignmenets1d.put(assignment.getPattern().getPosition()[0], assignment.getTargets());
					for(IAssignmentTarget atom : assignment.getTargets()) {
						if(((NMRSpectrum)spectrum).getNucleus().equals("1H")){
							int atomnumber=((AtomReference)atom).getAtomNumber();
							if(data.getMolecule().getAtom(atomnumber).getSymbol().equals("H")){
									atomnumber=data.getMolecule().indexOf(data.getMolecule().getConnectedAtomsList(data.getMolecule().getAtom(atomnumber)).get(0));
							}
							sdfwriter.write("SHIH "+(atomnumber+1)+" "+assignment.getPattern().getPosition()[0]+"\r\n");
						}else{
							sdfwriter.write("SHIX "+(((AtomReference)atom).getAtomNumber()+1)+" "+assignment.getPattern().getPosition()[0]+"\r\n");
						}
					}
				}
				sdfwriter.write("\r\n");
			}
		}
		for(Spectrum spectrum : data.getSpectra()) {
            NoteDescriptor noteDescriptor=new NoteDescriptor("CorType");
            List<String> entries = new ArrayList<String>();
			if(spectrum instanceof Spectrum2D && (((Note)spectrum.getNotes(noteDescriptor).get(0)).getValue().equals("HSQC") || ((Note)spectrum.getNotes(noteDescriptor).get(0)).getValue().equals("COSY") || ((Note)spectrum.getNotes(noteDescriptor).get(0)).getValue().equals("HMBC"))) {
			    for(Assignment ass : ((NMR2DSpectrum)spectrum).getAssignments()) {
                    int firstatom=((TwoAtomsReference)ass.getTargets()[0]).getAtomNumber1()[0];
                    int secondatom=((TwoAtomsReference)ass.getTargets()[0]).getAtomNumber2()[0];
                    if(data.getMolecule().getAtom(firstatom).getSymbol().equals("H")){
                        firstatom=data.getMolecule().indexOf(data.getMolecule().getConnectedAtomsList(data.getMolecule().getAtom(firstatom)).get(0));
                    }
                    if(data.getMolecule().getAtom(secondatom).getSymbol().equals("H")){
                        secondatom=data.getMolecule().indexOf(data.getMolecule().getConnectedAtomsList(data.getMolecule().getAtom(secondatom)).get(0));
                    }
                    if(!entries.contains(firstatom+" "+secondatom))
                        sdfwriter.write(((Note)spectrum.getNotes(noteDescriptor).get(0)).getValue()+" "+(firstatom+1)+" "+(secondatom+1)+"\r\n");
                    entries.add(firstatom+" "+secondatom);                          
			    }
				sdfwriter.write("\r\n");
			}
		}
	}
	
	public static int getHcount(IAtomContainer mol, IAtom atom) {
		List<IAtom> atoms = mol.getConnectedAtomsList(atom);
		int NumberOfHs=0;
        for (int k = 0; k < atoms.size(); k++) {
          if (atoms.get(k).getSymbol().equals("H")) {
            NumberOfHs++;
          }
        }		
		return NumberOfHs;
	}
	
	public void close() throws IOException{
		sdfwriter.close();
	}
}