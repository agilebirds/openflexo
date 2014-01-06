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
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeAssociation;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeObjectProperty;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

public class AttributeObjectPropertyFeatureAssociationPathElement extends SimplePathElement {

	private EMFAttributeObjectProperty objectProperty;
	private EMFAttributeAssociation association;

	private static final Logger logger = Logger
			.getLogger(AttributeObjectPropertyFeatureAssociationPathElement.class.getPackage().getName());

	public AttributeObjectPropertyFeatureAssociationPathElement(BindingPathElement parent, EMFAttributeAssociation association,
			EMFAttributeObjectProperty property) {
		super(parent, property.getName(), EMFObjectIndividual.class);
		objectProperty = property;
		this.association = association;
	}

	public EMFAttributeObjectProperty getObjectProperty() {
		return objectProperty;
	}

	@Override
	public Type getType() {
		if (association.getUpperBound() == null || (association.getUpperBound() >= 0 && association.getUpperBound() <= 1)) {
			// Single cardinality
			if (getObjectProperty().getRange() instanceof IFlexoOntologyClass) {
				return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getObjectProperty().getRange());
			}
			return Object.class;
		} else {
			if (getObjectProperty().getRange() instanceof IFlexoOntologyClass) {
				return new ParameterizedTypeImpl(List.class,
						IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getObjectProperty().getRange()));
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
		return "ObjectAttribute " + objectProperty.getDisplayableDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		EMFModel model = ((EMFObjectIndividual) target).getFlexoOntology();
		Object emfAnswer = ((EMFObjectIndividual) target).getObject().eGet(objectProperty.getObject());
		Object returned = model.getConverter().convertIndividualReference(model, emfAnswer);
		// System.out.println("AttributeObjectPropertyFeatureAssociationPathElement, Je retourne " + returned + " of " + (returned != null ?
		// returned.getClass() : null));
		return returned;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		((EMFObjectIndividual) target).getObject().eSet(objectProperty.getObject(), value);
	}
}
