package org.openflexo.technologyadapter.emf.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceAssociation;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

public class ObjectReferenceFeatureAssociationPathElement extends SimplePathElement {

	private EMFReferenceObjectProperty objectProperty;
	private EMFReferenceAssociation association;

	private static final Logger logger = Logger.getLogger(ObjectReferenceFeatureAssociationPathElement.class.getPackage().getName());

	public ObjectReferenceFeatureAssociationPathElement(BindingPathElement parent, EMFReferenceAssociation association,
			EMFReferenceObjectProperty property) {
		super(parent, property.getName(), EMFObjectIndividual.class);
		objectProperty = property;
		this.association = association;
	}

	public EMFReferenceObjectProperty getObjectProperty() {
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
		return "ObjectReference " + objectProperty.getDisplayableDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		EMFModel model = ((EMFObjectIndividual) target).getFlexoOntology();
		EObject object = ((EMFObjectIndividual) target).getObject();
		Object emfAnswer = object.eGet(objectProperty.getObject());
		Object returned = null;

		if (emfAnswer instanceof EObjectEList) {
			returned = model.getConverter().convertIndividualReferenceList(model, object, objectProperty);
		}
		else {
			returned = model.getConverter().convertIndividualReference(model, emfAnswer);
			}
		return returned;

	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
	NullReferenceException {
		if (value instanceof EMFObjectIndividual){
			((EMFObjectIndividual) target).getObject().eSet(objectProperty.getObject(), ((EMFObjectIndividual) value).getObject());
		}
		else {
			((EMFObjectIndividual) target).getObject().eSet(objectProperty.getObject(), value);
		}
	}
}
