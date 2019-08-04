package de.unikoeln.chemie.nmr.data;

public class Peak1D extends org.jcamp.spectrum.Peak1D {

    public double getShift() {
		return this.getPosition()[0];
	}
	public void setShift(double shift) {
		this.getPosition()[0]=shift;
	}

	
	public Peak1D(double x, double y) {
		super(x, y);
	}

}
