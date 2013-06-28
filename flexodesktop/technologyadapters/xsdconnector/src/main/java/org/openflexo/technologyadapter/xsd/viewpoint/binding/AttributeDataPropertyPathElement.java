package org.openflexo.technologyadapter.xsd.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

public class AttributeDataPropertyPathElement extends SimplePathElement {

	private XSOntDataProperty property;

	private static final Logger logger = Logger.getLogger(AttributeDataPropertyPathElement.class.getPackage().getName());

	public AttributeDataPropertyPathElement(BindingPathElement parent, XSOntDataProperty property) {
		super(parent, property.getName(), property.getRange().getAccessedType());
		this.property = property;
	}

	public XSOntDataProperty getDataProperty() {
		return property;
	}

	@Override
	public Type getType() {
		if (property.getUpperBound() == null || (property.getUpperBound() >= 0 && property.getUpperBound() <= 1)) {
			// Single cardinality
			if (getDataProperty() != null && getDataProperty().getRange() != null) {
				return getDataProperty().getRange().getAccessedType();
			}
			return Object.class;
		} else {
			if (getDataProperty() != null && getDataProperty().getRange() != null) {
				return new ParameterizedTypeImpl(List.class, getDataProperty().getRange().getAccessedType());
			}
			return new ParameterizedTypeImpl(List.class, Object.class);
		}
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return "DataAttribute " + property.getDisplayableDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		Object xsdAnswer = ((XSOntIndividual) target).getPropertyValue(getDataProperty());
		// System.out.println("AttributeDataPropertyFeatureAssociationPathElement, returning " + emfAnswer + " of " + (emfAnswer != null ?
		// emfAnswer.getClass() : null));
		return xsdAnswer;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		XSOntProperty prop = ((XSOntIndividual) target).getAttributeByName(getPropertyName());
		((XSOntIndividual) target).addToPropertyValue(prop, value);
	}
}
