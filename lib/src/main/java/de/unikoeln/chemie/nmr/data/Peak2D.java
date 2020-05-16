package de.unikoeln.chemie.nmr.data;

import java.util.ArrayList;
import java.util.List;

import org.jcamp.math.Range2D;
import org.jcamp.spectrum.Assignment;
import org.jcamp.spectrum.IAssignmentTarget;
import org.jcamp.spectrum.IOrderedDataArray1D;
import org.jcamp.spectrum.ISpectrumLabel;
import org.jcamp.spectrum.Peak;
import org.jcamp.spectrum.assignments.AtomReference;
/**
 * peak for 2D spectra.
 * @author Stefan Kuhn
 */
public class Peak2D extends Peak implements Cloneable {
    /** peak range */
    Range2D.Double range = new Range2D.Double();
    Range2D.Int irange = new Range2D.Int();
    public List<Assignment> assignments;
    //This is for javfax
    public double getFirstShift() {
		return this.getPosition()[0];
	}
	public void setFirstShift(double firstShift) {
		this.getPosition()[0]=firstShift;
	}
	public double getSecondShift() {
		return this.getPosition()[1];
	}
	public void setSecondShift(double secondShift) {
		this.getPosition()[1]=secondShift;
	}
    public String getAtoms1() {
    	StringBuffer atoms=new StringBuffer();
    	if(assignments!=null){
    		for(Assignment assignment : assignments){
    			if(assignment.getPattern().getPosition()[0]==getFirstShift()){
    				for(IAssignmentTarget atom : assignment.getTargets()){
    					atoms.append((((AtomReference)atom).getAtomNumber()+1));
    					atoms.append(", ");
    				}
    				return atoms.toString().substring(0, atoms.toString().length()-2);
    			}
    		}
    	}
		return "";
	}
    public List<String> getAtoms1List() {
    	List<String> atoms=new ArrayList<>();
    	if(assignments!=null){
    		for(Assignment assignment : assignments){
    			if(assignment.getPattern().getPosition()[0]==getFirstShift()){
    				for(IAssignmentTarget atom : assignment.getTargets()){
    					atoms.add("a"+(((AtomReference)atom).getAtomNumber()+1));
    				}
    				return atoms;
    			}
    		}
    	}
		return atoms;
	}
    public String getAtoms2() {
    	StringBuffer atoms=new StringBuffer();
    	if(assignments!=null){
    		for(Assignment assignment : assignments){
    			if(assignment.getPattern().getPosition()[0]==getSecondShift()){
    				for(IAssignmentTarget atom : assignment.getTargets()){
    					atoms.append((((AtomReference)atom).getAtomNumber()+1));
    					atoms.append(", ");
    				}
    				return atoms.toString().substring(0, atoms.toString().length()-2);
    			}
    		}
    	}
		return "";
	}
    public List<String> getAtoms2List() {
    	List<String> atoms=new ArrayList<>();
    	if(assignments!=null){
    		for(Assignment assignment : assignments){
    			if(assignment.getPattern().getPosition()[0]==getSecondShift()){
    				for(IAssignmentTarget atom : assignment.getTargets()){
    					atoms.add("a"+(((AtomReference)atom).getAtomNumber()+1));
    				}
    				return atoms;
    			}
    		}
    	}
		return atoms;
	}
    public void setAtoms1(String atoms) {
		//TODO for edit
	}
	public void setAtoms2(String atoms) {
		//TODO for edit
	}
	/** number format for peak label */
    static protected java.text.NumberFormat formatPeakPosition = java.text.NumberFormat.getInstance();
    static {
        formatPeakPosition.setMaximumFractionDigits(3);
        formatPeakPosition.setMinimumFractionDigits(0);
        formatPeakPosition.setGroupingUsed(false);
    }
    /**
     * Peak constructor comment.
     */
    public Peak2D(double x, double y, double z) {
        super(null, new double[] { x, y }, z);
        this.range.set(x, z, y, z);
    }
    /**
     * Peak constructor comment.
     */
    public Peak2D(double x, double y, double z, double wx, double wy) {
        super(null, new double[] { x, y }, z);
        this.range.setCenterAndWidth(x, wx, y, wy);
    }
    /**
     * Peak constructor comment.
     */
    public Peak2D(NMR2DSpectrum spectrum, double positionx, double positiony) {
        super(spectrum, new double[] { positionx, positiony});
        this.range.set(positionx, positionx, positiony, positiony);
    }
    /**
     * Peak constructor comment.
     */
    public Peak2D(NMR2DSpectrum spectrum, double positionx, double positiony, Range2D.Double range) {
        super(spectrum, new double[] { positionx, positiony });
        setRange(range);
    }
    /**
     * ctor with spectrum and data point index
     * @param spectrum Spectrum1D
     * @param xIndex int
     */
    public Peak2D(NMR2DSpectrum spectrum, int xIndex, int yIndex) {
        this(spectrum, spectrum.getXData().pointAt(xIndex), spectrum.getYData().pointAt(yIndex));
    }
    /**
     * Peak constructor comment.
     */
    public Peak2D(NMR2DSpectrum spectrum, int xIndex, int yIndex, Range2D.Double range) {
        this(spectrum, spectrum.getXData().pointAt(xIndex), spectrum.getYData().pointAt(yIndex), range);
    }
    /**
     * cloning.
     * @return java.lang.Object
     */
    public Object clone() {
        Peak2D peak = new Peak2D((NMR2DSpectrum) spectrum, position[0], position[1], range);
        return peak;
    }
    /**
     * compareTo method comment.
     */
    public int compareTo(java.lang.Object obj) {
        double p00 = getPosition()[0];
        double p01 = getPosition()[1];
        double p10 = ((ISpectrumLabel) obj).getPosition()[0];
        double p11 = ((ISpectrumLabel) obj).getPosition()[1];
        if (p00 < p10)
            return -1;
        if (p00 > p10)
            return 1;
        if (p01 < p11)
            return -1;
        if (p01 > p11)
            return 1;
        return 0;
    }
    /**
     * format position label
     * @param x double
     */
    static private String formatPeakPosition(double x) {
        double prec = Math.pow(10.0, formatPeakPosition.getMaximumFractionDigits());
        StringBuffer label = new StringBuffer();
        java.text.FieldPosition fp = new java.text.FieldPosition(java.text.NumberFormat.INTEGER_FIELD);
        label.setLength(0);
        x = Math.floor(x * prec + .5) / prec;
        formatPeakPosition.format(x, label, fp);
        return label.toString();
    }
    /**
     * gets range of data point indices.
     * @return Range1D.Int
     */
    public Range2D.Int getIndexRange() {
        return irange;
    }
    /**
     * gets label on peak.
     * @return java.lang.String
     */
    public String getLabel() {
        return formatPeakPosition(position[0]);
    }
    /**
     * gets peak range.
     * @return Range1D.Double
     */
    public Range2D.Double getRange() {
        return range;
    }
    /**
     * sets peak range.
     * @param newRange Range1D
     */
    public void setRange(Range2D newRange) {
        this.range.set(newRange);
	if (spectrum != null) {
            IOrderedDataArray1D xdata = ((NMR2DSpectrum) spectrum).getXData();
            IOrderedDataArray1D ydata = ((NMR2DSpectrum) spectrum).getYData();
            irange = new Range2D.Int(xdata.indexAt(this.range.getXMin()), xdata.indexAt(this.range.getXMax()), ydata.indexAt(this.range.getYMin()), ydata.indexAt(this.range.getYMax()));

        }
    }
}
