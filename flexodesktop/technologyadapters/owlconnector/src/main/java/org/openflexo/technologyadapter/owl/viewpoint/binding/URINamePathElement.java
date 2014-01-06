package org.openflexo.technologyadapter.owl.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.owl.model.OWLConcept;

public class URINamePathElement extends SimplePathElement {

	private static final Logger logger = Logger.getLogger(URINamePathElement.class.getPackage().getName());

	public URINamePathElement(BindingPathElement parent) {
		super(parent, "uriName", String.class);
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return FlexoLocalization.localizedForKey("owl_concept_uri_name");
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (target instanceof OWLConcept) {
			return ((OWLConcept) target).getName();
		}
		logger.warning("Please implement me, target=" + target + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		if (target instanceof OWLConcept) {
			((OWLConcept) target).setName((String) value);
			return;
		}
		logger.warning("Please implement me, target=" + target + " context=" + context);
	}

}