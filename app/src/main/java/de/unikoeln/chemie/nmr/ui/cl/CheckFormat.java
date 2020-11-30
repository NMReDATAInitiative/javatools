package de.unikoeln.chemie.nmr.ui.cl;

import java.io.FileInputStream;

import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.notes.Note;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import de.unikoeln.chemie.nmr.data.NMR2DSpectrum;
import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import javafx.scene.control.Tab;

public class CheckFormat {

	public static void main(String[] args) throws Exception {
		if(args.length!=1){
			System.out.println("Please provide a file name");
			return;
		}			
		NmredataReader reader = new NmredataReader(new FileInputStream(args[0]));
		NmreData data = reader.read();
		IMolecularFormula mfa = MolecularFormulaManipulator.getMolecularFormula(data.getMolecule());
        System.out.println("The molecule in your file has formula "+MolecularFormulaManipulator.getString(mfa));
		System.out.println("Your file contains "+data.getSpectra().size()+" spectra");
		for(int i=0; i<data.getSpectra().size(); i++){
			if(data.getSpectra().get(i) instanceof NMRSpectrum) {
		        NoteDescriptor noteDescriptor=new NoteDescriptor("CorType");
				System.out.println("1D Spectrum "+i+" is a "+(data.getSpectra().get(i).getNotes(noteDescriptor)==null ? "unknown type" : ((Note)data.getSpectra().get(i).getNotes(noteDescriptor).get(0)).getValue())+" spectrum and has "+((NMRSpectrum)data.getSpectra().get(i)).getPeakTable().length+" peaks");
			}
			if(data.getSpectra().get(i) instanceof NMR2DSpectrum) {
		        NoteDescriptor noteDescriptor=new NoteDescriptor("CorType");
				System.out.println("2D Spectrum "+i+" is a "+(data.getSpectra().get(i).getNotes(noteDescriptor)==null ? "unknown type" : ((Note)data.getSpectra().get(i).getNotes(noteDescriptor).get(0)).getValue())+" spectrum and has "+((NMR2DSpectrum)data.getSpectra().get(i)).getPeakTable().length+" peaks");
			}
		}
	}

}
