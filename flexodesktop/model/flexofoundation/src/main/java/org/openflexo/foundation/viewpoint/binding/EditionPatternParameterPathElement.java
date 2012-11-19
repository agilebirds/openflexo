package org.openflexo.foundation.viewpoint.binding;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionPatternParameter;

public class EditionPatternParameterPathElement<E extends Bindable> extends EditionPatternPathElement<E> {
	static final Logger logger = Logger.getLogger(EditionPatternParameterPathElement.class.getPackage().getName());

	private EditionPatternParameter parameter;

	public EditionPatternParameterPathElement(EditionPatternParameter anEditionPatternParameter, E container) {
		this(null, anEditionPatternParameter, container);
	}

	public EditionPatternParameterPathElement(String name, EditionPatternParameter anEditionPatternParameter, E container) {
		super(name, anEditionPatternParameter.getEditionPatternType(), container);
		parameter = anEditionPatternParameter;
	}

	@Override
	public EditionPatternInstance getBindingValue(Object target, BindingEvaluationContext context) {
		logger.warning("What to return as " + getVariableName() + " with a " + target + " ? "
				+ (target != null ? "(" + target.getClass().getSimpleName() + ")" : ""));
		if (target instanceof EditionSchemeAction<?>) {
			return (EditionPatternInstance) ((EditionSchemeAction<?>) target).getParameterValue(parameter);
		} else {
			logger.warning("Unexpected: " + target);
			return null;
		}
	}

}