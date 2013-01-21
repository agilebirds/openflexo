package org.openflexo.technologyadapter.emf.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;

public class AttributeDataPropertyPathElement extends SimplePathElement {

	private EMFAttributeDataProperty dataProperty;

	private static final Logger logger = Logger.getLogger(AttributeDataPropertyPathElement.class.getPackage().getName());

	public AttributeDataPropertyPathElement(BindingPathElement parent, EMFAttributeDataProperty property) {
		super(parent, property.getName(), property.getRange().getAccessedType());
		dataProperty = property;
	}

	public EMFAttributeDataProperty getDataProperty() {
		return dataProperty;
	}

	@Override
	public Type getType() {
		if (getDataProperty() != null && getDataProperty().getRange() != null) {
			return getDataProperty().getRange().getAccessedType();
		}
		return Object.class;
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return dataProperty.getDisplayableDescription();
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
