package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionPatternInstancePathElement<E extends Bindable> implements BindingVariable<E,EditionPatternInstance>
{
	static final Logger logger = Logger.getLogger(EditionPatternInstancePathElement.class.getPackage().getName());

	private EditionPattern editionPattern;
	private int index;
	private Hashtable<PatternRole,PatternRolePathElement> patternRoleElements;
	private Vector<PatternRolePathElement> allPatternRoleElements;
	private Class<E> declaringClass;
	
	public EditionPatternInstancePathElement(EditionPattern anEditionPattern, int index, Class<E> declaringClass)
	{
		this.editionPattern = anEditionPattern;
		this.declaringClass = declaringClass;
		this.index = index;
		patternRoleElements = new Hashtable<PatternRole, PatternRolePathElement>();
		allPatternRoleElements = new Vector<PatternRolePathElement>();
		for (PatternRole pr : editionPattern.getPatternRoles()) {
			PatternRolePathElement newPathElement = null;
			newPathElement = PatternRolePathElement.makePatternRolePathElement(pr,(EditionPatternInstance)null);
			patternRoleElements.put(pr,newPathElement);
		}
	}
	
	public Vector<PatternRolePathElement> getAllPatternRoleElements() 
	{
		return allPatternRoleElements;
	}

	public PatternRolePathElement getPatternRolePathElement(PatternRole pr)
	{
		return patternRoleElements.get(pr);
	}
	
	@Override
	public Class<E> getDeclaringClass() 
	{
		return declaringClass;
	}

	@Override
	public Type getType() {
		return EditionPatternInstance.class;
	}

	@Override
	public String getSerializationRepresentation() {
		return editionPattern.getCalc().getName()+"_"+editionPattern.getName()+"_"+index;
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
		return editionPattern.getDescription();
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public E getContainer() {
		//return patternRole.getEditionPattern();
		return null;
	}

	@Override
	public String getVariableName() {
		return getSerializationRepresentation();
	}
			
	@Override
	public EditionPatternInstance getBindingValue(E target, BindingEvaluationContext context) 
	{
		if (target != null) logger.info("TODO: evaluateBinding EditionPatternPathElement with target="+target+" context="+context);
		return null;
	}

    @Override
    public void setBindingValue(EditionPatternInstance value, E target, BindingEvaluationContext context) 
    {
    	// Not settable
    }

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	public int getIndex() {
		return index;
	}
}