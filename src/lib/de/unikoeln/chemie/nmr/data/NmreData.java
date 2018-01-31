package de.unikoeln.chemie.nmr.data;

import java.util.ArrayList;
import java.util.List;

import org.jcamp.spectrum.Spectrum;
import org.openscience.cdk.interfaces.IAtomContainer;

public class NmreData {
	
	private IAtomContainer molecule;
	private List<Spectrum> spectra;
	private String version;
	private int level;

	public IAtomContainer getMolecule() {
		return molecule;
	}
	public void setMolecule(IAtomContainer molecule) {
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
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}	
}
