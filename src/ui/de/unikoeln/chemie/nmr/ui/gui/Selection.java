package de.unikoeln.chemie.nmr.ui.gui;

import java.util.Collection;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.selection.AbstractSelection;

public class Selection extends AbstractSelection {
	
	public IAtomContainer ac;

	@Override
	public boolean contains(IChemObject arg0) {
		if(arg0 instanceof IAtom)
			return ac.contains((IAtom)arg0);
		else
			return false;
	}

	@Override
	public <E extends IChemObject> Collection<E> elements(Class<E> arg0) {
		return null;
	}

	@Override
	public IAtomContainer getConnectedAtomContainer() {
		return ac;
	}

	@Override
	public boolean isFilled() {
		return ac.getAtomCount()>0;
	}

}
