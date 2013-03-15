package org.openflexo.technologyadapter.emf.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeAssociation;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

public class AttributeDataPropertyFeatureAssociationPathElement extends SimplePathElement {

	private EMFAttributeDataProperty dataProperty;
	private EMFAttributeAssociation association;

	private static final Logger logger = Logger.getLogger(AttributeDataPropertyFeatureAssociationPathElement.class.getPackage().getName());

	public AttributeDataPropertyFeatureAssociationPathElement(BindingPathElement parent, EMFAttributeAssociation association,
			EMFAttributeDataProperty property) {
		super(parent, property.getName(), property.getRange().getAccessedType());
		this.association = association;
		dataProperty = property;
	}

	public EMFAttributeDataProperty getDataProperty() {
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
		return dataProperty.getDisplayableDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		return ((EMFObjectIndividual) target).getObject().eGet(dataProperty.getObject());
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		((EMFObjectIndividual) target).getObject().eSet(dataProperty.getObject(), value);
	}
}
