package org.openflexo.foundation.viewpoint.inspector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.IsAStatementPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.RestrictionStatementPatternRole;
import org.openflexo.foundation.viewpoint.inspector.OntologicObjectPatternRolePathElement.IsAStatementPatternRolePathElement;
import org.openflexo.foundation.viewpoint.inspector.OntologicObjectPatternRolePathElement.ObjectPropertyStatementPatternRolePathElement;
import org.openflexo.foundation.viewpoint.inspector.OntologicObjectPatternRolePathElement.OntologicClassPatternRolePathElement;
import org.openflexo.foundation.viewpoint.inspector.OntologicObjectPatternRolePathElement.OntologicDataPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.inspector.OntologicObjectPatternRolePathElement.OntologicIndividualPatternRolePathElement;
import org.openflexo.foundation.viewpoint.inspector.OntologicObjectPatternRolePathElement.OntologicObjectPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.inspector.OntologicObjectPatternRolePathElement.RestrictionStatementPatternRolePathElement;

public class PatternRolePathElement implements SimplePathElement, BindingVariable
{
	private static final Logger logger = Logger.getLogger(PatternRolePathElement.class.getPackage().getName());

	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	public static PatternRolePathElement makePatternRolePathElement(PatternRole pr)
	{
		if (pr instanceof OntologicObjectPatternRole) {
			if (pr instanceof ClassPatternRole) {
				return new OntologicClassPatternRolePathElement((ClassPatternRole)pr);
			}
			if (pr instanceof IndividualPatternRole) {
				return new OntologicIndividualPatternRolePathElement((IndividualPatternRole)pr);
			}
			if (pr instanceof ObjectPropertyPatternRole) {
				return new OntologicObjectPropertyPatternRolePathElement((ObjectPropertyPatternRole)pr);
			}
			if (pr instanceof DataPropertyPatternRole) {
				return new OntologicDataPropertyPatternRolePathElement((DataPropertyPatternRole)pr);
			}
			if (pr instanceof IsAStatementPatternRole) {
				return new IsAStatementPatternRolePathElement((IsAStatementPatternRole)pr);
			}
			if (pr instanceof ObjectPropertyStatementPatternRole) {
				return new ObjectPropertyStatementPatternRolePathElement((ObjectPropertyStatementPatternRole)pr);
			}
			if (pr instanceof RestrictionStatementPatternRole) {
				return new RestrictionStatementPatternRolePathElement((RestrictionStatementPatternRole)pr);
			}
			else {
				logger.warning("Unexpected "+pr);
				return null;
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