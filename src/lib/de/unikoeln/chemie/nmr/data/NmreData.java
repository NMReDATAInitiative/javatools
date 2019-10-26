package de.unikoeln.chemie.nmr.data;

import java.util.ArrayList;
import java.util.List;

import org.jcamp.spectrum.Spectrum;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

public class NmreData {
	public enum NmredataVersion {ONE,ONEPOINTONE};
	
	private IAtomContainer molecule;
	private IAtomContainer moleculeOriginal;
	private List<Spectrum> spectra;
	private NmredataVersion version;
	private int level=-1;
	private String solvent;
	private String ID;
	private double ph;
	private String smiles;
	private IMolecularFormula molecularFormula;
	private double concentration;
	private double temperature;

	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getConcentration() {
		return concentration;
	}
	public void setConcentration(double concentration) {
		this.concentration = concentration;
	}
	public IMolecularFormula getMolecularFormula() {
		return molecularFormula;
	}
	public void setMolecularFormula(IMolecularFormula molecularFormula) {
		this.molecularFormula = molecularFormula;
	}
	public String getSmiles() {
		return smiles;
	}
	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}
	public double getPh() {
		return ph;
	}
	public void setPh(double ph) {
		this.ph = ph;
	}
	public String getSolvent() {
		return solvent;
	}
	public void setSolvent(String solvent) {
		this.solvent = solvent;
	}
	public IAtomContainer getMolecule() {
		return molecule;
	}
	public IAtomContainer getMoleculeOriginal() {
		return moleculeOriginal;
	}
	public void setMolecule(IAtomContainer molecule) throws CDKException, CloneNotSupportedException {
		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
	    for (IAtom atom : molecule.atoms()) {
	      IAtomType type = matcher.findMatchingAtomType(molecule, atom);
	      AtomTypeManipulator.configure(atom, type);
	    }
	    this.moleculeOriginal=molecule.clone();
	    CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(molecule.getBuilder());
	    adder.addImplicitHydrogens(molecule);
	    this.molecule = molecule;
		
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
	public void setID(String property) {
		this.ID=property;
	}
	public String getID(){
		return ID;
	}
}
