package org.openflexo.foundation.viewpoint.inspector;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.OntologyObject;

public abstract class StatementPathElement<T> implements SimplePathElement<OntologyObject,T>
{
	private static final Logger logger = Logger.getLogger(StatementPathElement.class.getPackage().getName());

	protected List<BindingPathElement> allProperties;
	
	private BindingPathElement parent;
	
	public StatementPathElement(BindingPathElement aParent) 
	{
		super();
		parent = aParent;
		allProperties = new Vector<BindingPathElement>();
	}

	public List<BindingPathElement> getAllProperties() 
	{
		return allProperties;
	}

	@Override
	public Class getDeclaringClass() 
	{
		return TypeUtils.getBaseClass(parent.getType());
	}

	@Override
	public String getSerializationRepresentation() 
	{
		return getLabel();
	}

	@Override
	public boolean isBindingValid() 
	{
		return true;
	}

}