package org.openflexo.foundation.viewpoint.inspector;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionSchemePathElement<E extends Bindable> implements BindingVariable<E,EditionScheme>
{
	static final Logger logger = Logger.getLogger(EditionSchemePathElement.class.getPackage().getName());

	private E container;
	private EditionScheme editionScheme;
	
	private EditionSchemeParameterListPathElement parametersElement;
	private Hashtable<PatternRole,PatternRolePathElement> patternRoleElements;
	private Vector<BindingPathElement> allElements;
	
	public EditionSchemePathElement(EditionScheme anEditionScheme, E container)
	{
		this.editionScheme = anEditionScheme;
		this.container = container;
		patternRoleElements = new Hashtable<PatternRole, PatternRolePathElement>();
		allElements = new Vector<BindingPathElement>();
		parametersElement = new EditionSchemeParameterListPathElement(editionScheme,this);
		for (PatternRole pr : anEditionScheme.getEditionPattern().getPatternRoles()) {
			PatternRolePathElement newPathElement = PatternRolePathElement.makePatternRolePathElement(pr,editionScheme);
			patternRoleElements.put(pr,newPathElement);
			allElements.add(newPathElement);
		}
	}
	
	public Vector<BindingPathElement> getAllElements() 
	{
		return allElements;
	}

	public PatternRolePathElement getPatternRolePathElement(PatternRole pr)
	{
		return patternRoleElements.get(pr);
	}
	
	@Override
	public Class<E> getDeclaringClass() 
	{
		if (container != null) return (Class<E>)container.getClass();
		return null;
	}

	@Override
	public Type getType() {
		return EditionScheme.class;
	}

	@Override
	public String getSerializationRepresentation() 
	{
		return editionScheme.getName();
	}

	@Override
	public boolean isBindingValid() {
		return true;
	}

	@Override
	public String getLabel() {
		return getSerializationRepresentation();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return editionScheme.getDescription();
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public E getContainer() {
		return container;
	}

	@Override
	public String getVariableName() {
		return getSerializationRepresentation();
	}
			
	@Override
	public EditionScheme getBindingValue(E target, BindingEvaluationContext context) 
	{
		return editionScheme;
	}

    @Override
    public void setBindingValue(EditionScheme value, E target, BindingEvaluationContext context) 
    {
    	// Not settable
    }

	public EditionScheme getEditionScheme() {
		return editionScheme;
	}

}