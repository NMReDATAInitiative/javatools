package de.unikoeln.chemie.nmr.ui.cl;

import java.io.FileInputStream;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.NmredataReader;

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
			System.out.println("Spectrum "+i+" has "+data.getSpectra().get(i).getPeakTable().length+" peaks");
		}
	}

}
