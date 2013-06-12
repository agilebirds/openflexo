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
import org.openflexo.technologyadapter.xsd.model.XSOntAttributeAssociation;
import org.openflexo.technologyadapter.xsd.model.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

public class AttributeDataPropertyFeatureAssociationPathElement extends SimplePathElement {

	private XSOntDataProperty dataProperty;
	private XSOntAttributeAssociation association;

	private static final Logger logger = Logger.getLogger(AttributeDataPropertyFeatureAssociationPathElement.class.getPackage().getName());

	public AttributeDataPropertyFeatureAssociationPathElement(BindingPathElement parent, XSOntAttributeAssociation association,
			XSOntDataProperty property) {
		super(parent, property.getName(), property.getRange().getAccessedType());
		this.association = association;
		dataProperty = property;
	}

	public XSOntDataProperty getDataProperty() {
		return dataProperty;
	}

	@Override
	public Type getType() {
		if (association.getUpperBound() == null || (association.getUpperBound() >= 0 && association.getUpperBound() <= 1)) {
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
		return "DataAttribute " + dataProperty.getDisplayableDescription();
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
		((XSOntIndividual) target).getPropertyNamed(getPropertyName()).setValue((String) value);
	}
}
