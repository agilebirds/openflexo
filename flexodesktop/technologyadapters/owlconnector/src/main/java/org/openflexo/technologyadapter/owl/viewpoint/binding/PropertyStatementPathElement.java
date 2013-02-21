package org.openflexo.technologyadapter.owl.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLProperty;

public abstract class PropertyStatementPathElement extends SimplePathElement {

	private static final Logger logger = Logger.getLogger(PropertyStatementPathElement.class.getPackage().getName());

	private OWLProperty property;

	public static PropertyStatementPathElement makePropertyStatementPathElement(BindingPathElement aParent, OWLProperty property) {
		if (property instanceof OWLDataProperty) {
			return new DataPropertyStatementPathElement(aParent, (OWLDataProperty) property);
		} else if (property instanceof OWLObjectProperty) {
			if (((OWLObjectProperty) property).isLiteralRange()) {
				return new ObjectPropertyStatementAccessingLiteralPathElement(aParent, (OWLObjectProperty) property);
			} else {
				return new ObjectPropertyStatementAccessingObjectPathElement(aParent, (OWLObjectProperty) property);
			}
		} else {
			logger.warning("unexpected property " + property);
			return null;
		}
	}

	private PropertyStatementPathElement(BindingPathElement parent, OWLProperty property) {
		super(parent, property.getName(), OWLIndividual.class);
		this.property = property;
	}

	public OWLProperty getProperty() {
		return property;
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return property.getDisplayableDescription();
	}

	public static class DataPropertyStatementPathElement extends PropertyStatementPathElement {

		private DataPropertyStatementPathElement(BindingPathElement parent, OWLDataProperty property) {
			super(parent, property);
		}

		@Override
		public OWLDataProperty getProperty() {
			return (OWLDataProperty) super.getProperty();
		}

		@Override
		public Type getType() {
			if (getProperty() != null && getProperty().getDataType() != null) {
				return getProperty().getDataType().getAccessedType();
			}
			return Object.class;
		}

		@Override
		public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
			if (target instanceof OWLIndividual) {
				System.out.println("Property " + getPropertyName() + " for individual " + target + " return "
						+ ((OWLIndividual) target).getPropertyValue(getProperty()));
				return ((OWLIndividual) target).getPropertyValue(getProperty());
			}
			logger.warning("Please implement me, target=" + target + " context=" + context);
			return null;
		}

		@Override
		public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
				NullReferenceException {
			if (target instanceof OWLIndividual) {
				System.out.println("Property " + getPropertyName() + " for individual " + target + " sets value " + value);
				((OWLIndividual) target).setPropertyValue(getProperty(), value);
				return;
			}
			logger.warning("Please implement me, target=" + target + " context=" + context);
		}
	}

	public static class ObjectPropertyStatementAccessingObjectPathElement extends PropertyStatementPathElement {

		private ObjectPropertyStatementAccessingObjectPathElement(BindingPathElement parent, OWLObjectProperty property) {
			super(parent, property);
		}

		@Override
		public OWLObjectProperty getProperty() {
			return (OWLObjectProperty) super.getProperty();
		}

		@Override
		public Type getType() {
			if (getProperty().getRange() instanceof OWLClass) {
				return IndividualOfClass.getIndividualOfClass(getProperty().getRange());
			}
			return OWLIndividual.class;
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

	public static class ObjectPropertyStatementAccessingLiteralPathElement extends PropertyStatementPathElement {

		private ObjectPropertyStatementAccessingLiteralPathElement(BindingPathElement parent, OWLObjectProperty property) {
			super(parent, property);
		}

		@Override
		public Type getType() {
			return Object.class;
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
}