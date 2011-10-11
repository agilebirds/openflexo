package org.openflexo.foundation.viewpoint.inspector;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Vector;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionPatternPathElement implements BindingVariable
{
	EditionPattern editionPattern;
	private int index;
	private Hashtable<PatternRole,PatternRolePathElement> patternRoleElements;
	private Vector<PatternRolePathElement> allPatternRoleElements;
	
	public EditionPatternPathElement(EditionPattern anEditionPattern, int index)
	{
		this.editionPattern = anEditionPattern;
		this.index = index;
		patternRoleElements = new Hashtable<PatternRole, PatternRolePathElement>();
		allPatternRoleElements = new Vector<PatternRolePathElement>();
		for (PatternRole pr : editionPattern.getPatternRoles()) {
			PatternRolePathElement newPathElement = null;
			newPathElement = PatternRolePathElement.makePatternRolePathElement(pr);
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
	public Class getDeclaringClass() {
		return null;
	}

	@Override
	public Type getType() {
		return EditionPattern.class;
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
	public Bindable getContainer() {
		//return patternRole.getEditionPattern();
		return null;
	}

	@Override
	public String getVariableName() {
		return getSerializationRepresentation();
	}
			
	@Override
	public Object evaluateBinding(Object target, BindingEvaluationContext context) 
	{
		InspectorEntry.logger.info("evaluateBinding EditionPatternPathElement with target="+target+" context="+context);
		return null;
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	public int getIndex() {
		return index;
	}
}