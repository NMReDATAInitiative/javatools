package de.unikoeln.chemie.nmr.data;

import java.util.ArrayList;
import java.util.List;

import org.jcamp.spectrum.NMRSpectrum;
import org.openscience.cdk.interfaces.IAtomContainer;

public class NmreData {
	
	private IAtomContainer molecule;
	private List<NMRSpectrum> spectra;
	
	public IAtomContainer getMolecule() {
		return molecule;
	}
	public void setMolecule(IAtomContainer molecule) {
		this.molecule = molecule;
	}
	public List<NMRSpectrum> getSpectra() {
		return spectra;
	}
	public void setSpectra(List<NMRSpectrum> spectra) {
		this.spectra = spectra;
	}
	public void addSpectrum(NMRSpectrum spectrum) {
		if(spectra==null)
			spectra=new ArrayList<>();
		spectra.add(spectrum);
	}
	
}
