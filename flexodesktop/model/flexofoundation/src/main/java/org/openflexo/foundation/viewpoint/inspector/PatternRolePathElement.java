package org.openflexo.foundation.viewpoint.inspector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;

public class PatternRolePathElement implements SimplePathElement, BindingVariable
{
	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	public static PatternRolePathElement makePatternRolePathElement(PatternRole pr)
	{
		if (pr instanceof OntologicObjectPatternRole) {
			switch (((OntologicObjectPatternRole) pr).getOntologicObjectType()) {
			case Class:
				return new OntologicObjectPatternRolePathElement.OntologicClassPatternRolePathElement((OntologicObjectPatternRole)pr);
			case Individual:
				return new OntologicObjectPatternRolePathElement.OntologicIndividualPatternRolePathElement((OntologicObjectPatternRole)pr);
			case ObjectProperty:
				return new OntologicObjectPatternRolePathElement.OntologicObjectPropertyPatternRolePathElement((OntologicObjectPatternRole)pr);
			case DataProperty:
				return new OntologicObjectPatternRolePathElement.OntologicDataPropertyPatternRolePathElement((OntologicObjectPatternRole)pr);
			case OntologyStatement:
				return new OntologicObjectPatternRolePathElement.OntologicStatementPatternRolePathElement((OntologicObjectPatternRole)pr);
			default:
				return new PatternRolePathElement(pr);
			}
		}
		else {
			return new PatternRolePathElement(pr);
		}
	}

	private PatternRole patternRole;
	
	PatternRolePathElement(PatternRole aPatternRole)
	{
		this.patternRole = aPatternRole;
	}
	
	@Override
	public Class getDeclaringClass() {
		return EditionPattern.class;
	}

	@Override
	public Type getType() {
		return patternRole.getAccessedClass();
	}

	@Override
	public String getSerializationRepresentation() {
		return patternRole.getName();
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
		return patternRole.getDescription();
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
		return patternRole.getName();
	}
	
	
	@Override
	public Object evaluateBinding(Object target, BindingEvaluationContext context) 
	{
		if (target instanceof EditionPatternReference) {
			return ((EditionPatternReference) target).getEditionPatternInstance().getPatternActor(patternRole);
		}
		InspectorEntry.logger.warning("Unexpected call to evaluateBinding() target="+target+" context="+context);
		return null;
	}
	
	public List<BindingPathElement> getAllProperties() 
	{
		return EMPTY_LIST;
	}

}