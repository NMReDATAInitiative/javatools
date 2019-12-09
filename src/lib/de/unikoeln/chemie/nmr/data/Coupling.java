package de.unikoeln.chemie.nmr.data;

import org.jcamp.spectrum.IAssignmentTarget;

public class Coupling {
	private double constant;
	private IAssignmentTarget[] assignments1;
	private IAssignmentTarget[] assignments2;
	
	public Coupling(double constant, IAssignmentTarget[] assignments1, IAssignmentTarget[] assignments2) {
		this.constant=constant;
		this.assignments1=assignments1;
		this.assignments2=assignments2;
	}

	public double getConstant() {
		return constant;
	}

	public IAssignmentTarget[] getAssignments1() {
		return assignments1;
	}

	public IAssignmentTarget[] getAssignments2() {
		return assignments2;
	}
}
