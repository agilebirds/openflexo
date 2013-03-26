package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionPatternPatternRolePathElement<PR extends PatternRole<?>> extends SimplePathElement {

	private static final Logger logger = Logger.getLogger(EditionPatternPatternRolePathElement.class.getPackage().getName());

	private PR patternRole;

	public EditionPatternPatternRolePathElement(BindingPathElement parent, PR patternRole) {
		super(parent, patternRole.getPatternRoleName(), patternRole.getType());
		this.patternRole = patternRole;
	}

	@Override
	public String getLabel() {
		return patternRole.getPatternRoleName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return patternRole.getDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (target instanceof EditionPatternInstance) {
			return ((EditionPatternInstance) target).getPatternActor((PatternRole) patternRole);
		}
		logger.warning("Please implement me, target=" + target + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		if (target instanceof EditionPatternInstance) {
			((EditionPatternInstance) target).setPatternActor(value, (PatternRole) patternRole);
			return;
		}
		logger.warning("Please implement me, target=" + target + " context=" + context);
	}

}