package de.unikoeln.chemie.nmr.data;

import org.jcamp.spectrum.Assignment;
import org.jcamp.spectrum.IAssignmentTarget;
import org.jcamp.spectrum.assignments.AtomReference;

public class Peak1D extends org.jcamp.spectrum.Peak1D {
	
	public Assignment[] assignments;

    public double getShift() {
		return this.getPosition()[0];
	}
	public void setShift(double shift) {
		this.getPosition()[0]=shift;
	}
    public String getAtoms() {
    	StringBuffer atoms=new StringBuffer();
    	if(assignments!=null){
    		for(Assignment assignment : assignments){
    			if(assignment.getPattern().getPosition()[0]==getShift()){
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
	public void setAtoms(String atoms) {
		//TODO for edit
	}
	
	public Peak1D(double x, double y) {
		super(x, y);
	}

}
