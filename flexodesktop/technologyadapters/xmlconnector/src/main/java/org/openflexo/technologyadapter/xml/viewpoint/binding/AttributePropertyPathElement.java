package org.openflexo.technologyadapter.xml.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.technologyadapter.xml.model.XMLAttribute;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;

public class AttributePropertyPathElement extends SimplePathElement {

	private XMLAttribute property;

	private static final Logger logger = Logger.getLogger(AttributePropertyPathElement.class.getPackage().getName());

	public AttributePropertyPathElement(BindingPathElement parent, XMLAttribute property) {
		super(parent, property.getName(), property.getType());
		this.property = property;
	}

	public XMLAttribute getDataProperty() {
		return property;
	}

	@Override
	public Type getType() {

		return property.getType();
		
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
		Object xsdAnswer = ((XMLIndividual) target).getAttributeValue(getPropertyName());
		// System.out.println("AttributeDataPropertyFeatureAssociationPathElement, returning " + emfAnswer + " of " + (emfAnswer != null ?
		// emfAnswer.getClass() : null));
		return xsdAnswer;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		XMLAttribute prop = ((XMLIndividual) target).getAttributeByName(getPropertyName());
		prop.setValue(value);
	}
}
