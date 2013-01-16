package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;

public class EditionSchemeParameterPathElement extends SimplePathElement {

	private static final Logger logger = Logger.getLogger(EditionSchemeParameterPathElement.class.getPackage().getName());

	private EditionSchemeParameter parameter;

	public EditionSchemeParameterPathElement(BindingPathElement parent, EditionSchemeParameter parameter) {
		super(parent, parameter.getName(), parameter.getType());
		this.parameter = parameter;
	}

	@Override
	public String getLabel() {
		return parameter.getName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return parameter.getDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		logger.warning("Please implement me, target=" + target + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		logger.warning("Please implement me, target=" + target + " context=" + context);
	}

}