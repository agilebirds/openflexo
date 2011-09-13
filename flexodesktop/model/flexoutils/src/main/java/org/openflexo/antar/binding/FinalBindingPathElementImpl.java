package org.openflexo.antar.binding;

import java.util.ArrayList;
import java.util.List;

public abstract class FinalBindingPathElementImpl implements BindingPathElement {

	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	@Override
	public BindingPathElement getBindingPathElement(String propertyName)
	{
		return null;
	}
	
	@Override
	public List<? extends BindingPathElement> getAccessibleBindingPathElements() 
	{
		return EMPTY_LIST;
	}
	
	@Override
	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements() 
	{
		return EMPTY_LIST;
	}
}
