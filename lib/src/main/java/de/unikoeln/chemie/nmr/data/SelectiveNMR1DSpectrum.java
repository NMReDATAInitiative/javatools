package de.unikoeln.chemie.nmr.data;

import org.jcamp.math.AxisMap;
import org.jcamp.spectrum.IDataArray1D;
import org.jcamp.spectrum.IOrderedDataArray1D;
import org.jcamp.spectrum.NMRSpectrum;

public class SelectiveNMR1DSpectrum extends NMRSpectrum {
    String[] nucleus = new String[] { "13C", "13C" };

    
    public SelectiveNMR1DSpectrum(IOrderedDataArray1D x, IDataArray1D y, double freq, double ref, String xnucleus, String ynucleus) {
    	super(x,y,xnucleus,freq,ref);
    	nucleus=new String[] { xnucleus, ynucleus };
    }
    
    public SelectiveNMR1DSpectrum(IOrderedDataArray1D x, IDataArray1D y, double freq, double ref, boolean fullspectrum, String mode, String xnucleus, String ynucleus) {
    	super(x,y,xnucleus,freq,ref,fullspectrum,mode);
    	nucleus=new String[] { xnucleus, ynucleus };
    }
    	
    /**
     * Insert the method's description here.
     * Creation date: (01/18/00 17:22:51)
     * @return java.lang.String
     */
    public java.lang.String getXNucleus() {
        return nucleus[0];
    }

    /**
     * Insert the method's description here.
     * Creation date: (01/18/00 17:22:51)
     * @return java.lang.String
     */
    public java.lang.String getYNucleus() {
        return nucleus[1];
    }

}
