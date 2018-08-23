package de.unikoeln.chemie.nmr.data;

import java.util.ArrayList;
import java.util.List;

import org.jcamp.spectrum.Spectrum;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

public class NmreData {
	public enum NmredataVersion {ONE,ONEPOINTONE};
	
	private IAtomContainer molecule;
	private List<Spectrum> spectra;
	private NmredataVersion version;
	private int level;
	private String solvent;

	public String getSolvent() {
		return solvent;
	}
	public void setSolvent(String solvent) {
		this.solvent = solvent;
	}
	public IAtomContainer getMolecule() {
		return molecule;
	}
	public void setMolecule(IAtomContainer molecule) throws CDKException {
		this.molecule = molecule;
		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
	    for (IAtom atom : molecule.atoms()) {
	      IAtomType type = matcher.findMatchingAtomType(molecule, atom);
	      AtomTypeManipulator.configure(atom, type);
	    }
	    CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(molecule.getBuilder());
	    adder.addImplicitHydrogens(molecule);
	}
	public List<Spectrum> getSpectra() {
		return spectra;
	}
	public void setSpectra(List<Spectrum> spectra) {
		this.spectra = spectra;
	}
	public void addSpectrum(Spectrum spectrum) {
		if(spectra==null)
			spectra=new ArrayList<>();
		spectra.add(spectrum);
	}
	
	public NmredataVersion getVersion() {
		return version;
	}
	
	public void setVersion(NmredataVersion version) {
		this.version = version;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}	
}
