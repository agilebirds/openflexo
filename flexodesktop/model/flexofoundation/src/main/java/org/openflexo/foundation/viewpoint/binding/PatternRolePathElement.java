package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.EditionPatternPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.IsAStatementPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.PropertyPatternRole;
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
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.RestrictionStatementPatternRolePathElement;

public class PatternRolePathElement<T extends Object> implements SimplePathElement<T>, BindingVariable<T> {
	private static final Logger logger = Logger.getLogger(PatternRolePathElement.class.getPackage().getName());

	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	public static BindingVariable<?> makePatternRolePathElement(PatternRole pr, Bindable container) {
		if (pr instanceof OntologicObjectPatternRole) {
			if (pr instanceof ClassPatternRole) {
				return new OntologicClassPatternRolePathElement((ClassPatternRole) pr, container);
			} else if (pr instanceof IndividualPatternRole) {
				return new OntologicIndividualPatternRolePathElement((IndividualPatternRole) pr, container);
			} else if (pr instanceof ObjectPropertyPatternRole) {
				return new OntologicObjectPropertyPatternRolePathElement((ObjectPropertyPatternRole) pr, container);
			} else if (pr instanceof DataPropertyPatternRole) {
				return new OntologicDataPropertyPatternRolePathElement((DataPropertyPatternRole) pr, container);
			} else if (pr instanceof PropertyPatternRole) {
				return new OntologicPropertyPatternRolePathElement((PropertyPatternRole) pr, container);
			} else if (pr instanceof DataPropertyPatternRole) {
				return new OntologicDataPropertyPatternRolePathElement((DataPropertyPatternRole) pr, container);
			} else if (pr instanceof IsAStatementPatternRole) {
				return new IsAStatementPatternRolePathElement((IsAStatementPatternRole) pr, container);
			} else if (pr instanceof ObjectPropertyStatementPatternRole) {
				return new ObjectPropertyStatementPatternRolePathElement((ObjectPropertyStatementPatternRole) pr, container);
			} else if (pr instanceof DataPropertyStatementPatternRole) {
				return new DataPropertyStatementPatternRolePathElement((DataPropertyStatementPatternRole) pr, container);
			} else if (pr instanceof RestrictionStatementPatternRole) {
				return new RestrictionStatementPatternRolePathElement((RestrictionStatementPatternRole) pr, container);
			} else {
				logger.warning("Unexpected " + pr);
				return null;
			}
		} else if (pr instanceof ShapePatternRole) {
			return new ShapePatternRolePathElement((ShapePatternRole) pr, container);
		} else if (pr instanceof ConnectorPatternRole) {
			return new ConnectorPatternRolePathElement((ConnectorPatternRole) pr, container);
		} else if (pr instanceof EditionPatternPatternRole) {
			return new EditionPatternPathElement(pr.getPatternRoleName(), ((EditionPatternPatternRole) pr).getEditionPatternType(),
					container);
		} else {
			return new PatternRolePathElement(pr, container);
		}
	}

	private PatternRole patternRole;
	private Bindable container;

	PatternRolePathElement(PatternRole aPatternRole, Bindable container) {
		this.patternRole = aPatternRole;
		this.container = container;
	}

	@Override
	public Class<?> getDeclaringClass() {
		if (container != null) {
			return container.getClass();
		} else {
			return Object.class;
		}
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
	public final boolean isSettable() {
		return true;
	}

	@Override
	public Bindable getContainer() {
		// return patternRole.getEditionPattern();
		return container;
	}

	@Override
	public String getVariableName() {
		return patternRole.getName();
	}

	@Override
	public T getBindingValue(Object target, BindingEvaluationContext context) {
		if (target instanceof EditionPatternInstance) {
			return (T) ((EditionPatternInstance) target).getPatternActor(patternRole);
		} else {
			logger.warning("What to return with a " + target + " ? "
					+ (target != null ? "(" + target.getClass().getSimpleName() + ")" : ""));
		}
		return null;
	}

	@Override
	public void setBindingValue(T value, Object target, BindingEvaluationContext context) {
		if (target instanceof EditionPatternInstance && value instanceof FlexoModelObject) {
			((EditionPatternInstance) target).setPatternActor((FlexoModelObject) value, patternRole);
		} else {
			logger.warning("What to do with a " + target + " ?");
		}
	}

	public List<BindingPathElement> getAllProperties() {
		return Collections.emptyList();
	}

	public PatternRole getPatternRole() {
		return patternRole;
	}

	@Override
	public String toString() {
		return patternRole + "/" + getClass().getSimpleName();
	}
}