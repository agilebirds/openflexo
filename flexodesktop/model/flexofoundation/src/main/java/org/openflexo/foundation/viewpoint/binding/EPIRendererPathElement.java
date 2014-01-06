package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.localization.FlexoLocalization;

public class EPIRendererPathElement extends SimplePathElement {

	private static final Logger logger = Logger.getLogger(EPIRendererPathElement.class.getPackage().getName());

	public EPIRendererPathElement(BindingPathElement parent) {
		super(parent, "render", String.class);
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return FlexoLocalization.localizedForKey("renderer");
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		// System.out.println("Renderer for " + target);
		if (target instanceof EditionPatternInstance) {
			// System.out.println("return " + ((EditionPatternInstance) target).getStringRepresentation());
			return ((EditionPatternInstance) target).getStringRepresentation();
		}
		logger.warning("Please implement me, target=" + target + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		// Not applicable
	}

}