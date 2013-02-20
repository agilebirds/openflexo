package org.openflexo.technologyadapter.emf.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

public class ReferenceObjectPropertyPathElement extends SimplePathElement {

	private EMFReferenceObjectProperty objectProperty;

	private static final Logger logger = Logger.getLogger(ReferenceObjectPropertyPathElement.class.getPackage().getName());

	public ReferenceObjectPropertyPathElement(BindingPathElement parent, EMFReferenceObjectProperty property) {
		super(parent, property.getName(), EMFObjectIndividual.class);
		objectProperty = property;
	}

	public EMFReferenceObjectProperty getObjectProperty() {
		return objectProperty;
	}

	@Override
	public Type getType() {
		if (getObjectProperty().getRange() instanceof IFlexoOntologyClass) {
			return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getObjectProperty().getRange());
		}
		return EMFObjectIndividual.class;
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return objectProperty.getDisplayableDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		return ((EMFObjectIndividual) target).getObject().eGet(objectProperty.getObject());
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		((EMFObjectIndividual) target).getObject().eSet(objectProperty.getObject(), value);
	}
}
