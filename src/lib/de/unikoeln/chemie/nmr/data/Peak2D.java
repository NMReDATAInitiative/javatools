package de.unikoeln.chemie.nmr.data;

import org.jcamp.math.Range2D;
import org.jcamp.spectrum.IOrderedDataArray1D;
import org.jcamp.spectrum.ISpectrumLabel;
import org.jcamp.spectrum.Peak;
/**
 * peak for 2D spectra.
 * @author Stefan Kuhn
 */
public class Peak2D extends Peak implements Cloneable {
    /** peak range */
    Range2D.Double range = new Range2D.Double();
    Range2D.Int irange = new Range2D.Int();
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
