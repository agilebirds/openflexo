package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.IsAStatementPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.RestrictionStatementPatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPatternRolePathElement.ConnectorPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPatternRolePathElement.ShapePatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.DataPropertyStatementPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.IsAStatementPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.ObjectPropertyStatementPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicClassPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicDataPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicIndividualPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicObjectPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.RestrictionStatementPatternRolePathElement;

public class PatternRolePathElement<E extends Bindable,T> implements SimplePathElement<E,T>, BindingVariable<E,T>
{
	private static final Logger logger = Logger.getLogger(PatternRolePathElement.class.getPackage().getName());

	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	public static <E extends Bindable> PatternRolePathElement<E,?> makePatternRolePathElement(PatternRole pr, E container)
	{
		if (pr instanceof OntologicObjectPatternRole) {
			if (pr instanceof ClassPatternRole) {
				return new OntologicClassPatternRolePathElement<E>((ClassPatternRole)pr,container);
			}
			if (pr instanceof IndividualPatternRole) {
				return new OntologicIndividualPatternRolePathElement<E>((IndividualPatternRole)pr,container);
			}
			if (pr instanceof ObjectPropertyPatternRole) {
				return new OntologicObjectPropertyPatternRolePathElement<E>((ObjectPropertyPatternRole)pr,container);
			}
			if (pr instanceof DataPropertyPatternRole) {
				return new OntologicDataPropertyPatternRolePathElement<E>((DataPropertyPatternRole)pr,container);
			}
			if (pr instanceof IsAStatementPatternRole) {
				return new IsAStatementPatternRolePathElement<E>((IsAStatementPatternRole)pr,container);
			}
			if (pr instanceof ObjectPropertyStatementPatternRole) {
				return new ObjectPropertyStatementPatternRolePathElement<E>((ObjectPropertyStatementPatternRole)pr,container);
			}
			if (pr instanceof DataPropertyStatementPatternRole) {
				return new DataPropertyStatementPatternRolePathElement<E>((DataPropertyStatementPatternRole)pr,container);
			}
			if (pr instanceof RestrictionStatementPatternRole) {
				return new RestrictionStatementPatternRolePathElement<E>((RestrictionStatementPatternRole)pr,container);
			}
			else {
				logger.warning("Unexpected "+pr);
				return null;
			}
		}
		else if (pr instanceof ShapePatternRole) {
			return new ShapePatternRolePathElement<E>((ShapePatternRole)pr,container);
		}
		else if (pr instanceof ConnectorPatternRole) {
			return new ConnectorPatternRolePathElement<E>((ConnectorPatternRole)pr,container);
		}
		else {
			return new PatternRolePathElement(pr,container);
		}
	}

	private PatternRole patternRole;
	private E container;
	
	PatternRolePathElement(PatternRole aPatternRole, E container)
	{
		this.patternRole = aPatternRole;
		this.container = container;
	}
	
	@Override
	public Class<E> getDeclaringClass() 
	{
		return (Class<E>) container.getClass();
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
	public E getContainer() {
		//return patternRole.getEditionPattern();
		return null;
	}

	@Override
	public String getVariableName() {
		return patternRole.getName();
	}
	
	
	@Override
	public T getBindingValue(E target, BindingEvaluationContext context) 
	{
		if (target instanceof EditionPatternInstance) 
			return (T)((EditionPatternInstance)target).getPatternActor(patternRole);
		else {
			logger.warning("What to return with a "+target+" ?");
		}
		return null;
	}
	
	@Override
	public void setBindingValue(T value, E target,BindingEvaluationContext context)
	{
		// not settable
	}
	
	public List<BindingPathElement> getAllProperties() 
	{
		return EMPTY_LIST;
	}

}