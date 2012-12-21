package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.EditionPatternPatternRole;
import org.openflexo.foundation.viewpoint.FlexoModelObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;

public class PatternRolePathElement<T extends Object> implements SimplePathElement<T>, BindingVariable<T> {
	private static final Logger logger = Logger.getLogger(PatternRolePathElement.class.getPackage().getName());

	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	public static BindingVariable<?> makePatternRolePathElement(PatternRole pr, Bindable container) {
		if (pr.getModelSlot() != null) {
			return pr.getModelSlot().makePatternRolePathElement(pr, container);
		}
		if (pr instanceof FlexoModelObjectPatternRole) {
			logger.warning("Not implemented");
			return null;
		} else if (pr instanceof EditionPatternPatternRole) {
			return new EditionPatternPathElement<Bindable>(((EditionPatternPatternRole) pr).getEditionPatternType(), null);
		}
		return null;
	}

	private PatternRole patternRole;
	private Bindable container;

	public PatternRolePathElement(PatternRole aPatternRole, Bindable container) {
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
		return EMPTY_LIST;
	}

	public PatternRole getPatternRole() {
		return patternRole;
	}

	@Override
	public String toString() {
		return patternRole + "/" + getClass().getSimpleName();
	}
}