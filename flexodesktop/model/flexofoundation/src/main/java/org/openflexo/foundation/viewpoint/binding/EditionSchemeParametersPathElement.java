package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.WilcardTypeImpl;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;

public class EditionSchemeParametersPathElement extends SimplePathElement {
	static final Logger logger = Logger.getLogger(EditionSchemeParametersPathElement.class.getPackage().getName());

	private EditionScheme editionScheme;

	public EditionSchemeParametersPathElement(BindingPathElement parent, EditionScheme anEditionScheme) {
		super(parent, "parameters", new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(EditionSchemeParameter.class)));
		this.editionScheme = anEditionScheme;
	}

	public EditionScheme getEditionScheme() {
		return editionScheme;
	}

	@Override
	public String getLabel() {
		return "parameters";
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return "parameters";
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (target instanceof EditionSchemeAction) {
			return ((EditionSchemeAction) target).getParameters();
		}
		logger.warning("Please implement me, target=" + target + " of " + target.getClass() + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		logger.warning("Please implement me, target=" + target + " context=" + context);
	}

}