package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimpleBindingPathElementImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
import org.openflexo.localization.FlexoLocalization;

public class OntologyObjectPathElement<T extends OntologyObject> implements SimplePathElement<T>, BindingVariable<T> {
	private static final Logger logger = Logger.getLogger(OntologyObjectPathElement.class.getPackage().getName());

	private String name;
	private BindingPathElement parentElement;

	private SimpleBindingPathElementImpl uriNameProperty;
	private SimpleBindingPathElementImpl uriProperty;

	protected List<BindingPathElement> allProperties;

	public OntologyObjectPathElement(String name, BindingPathElement aParentElement) {
		super();
		this.name = name;
		parentElement = aParentElement;
		allProperties = new Vector<BindingPathElement>();
		uriNameProperty = new SimpleBindingPathElementImpl<String>(URINameChanged.URI_NAME_KEY, TypeUtils.getBaseClass(getType()),
				String.class, true, "uri_name_as_supplied_in_ontology") {
			@Override
			public String getBindingValue(Object target, BindingEvaluationContext context) {
				return ((OntologyObject) target).getName();
			}

			@Override
			public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
				if (target instanceof OntologyObject) {
					try {
						logger.info("Rename URI of object " + target + " with " + value);
						((OntologyObject) target).setName(value);
					} catch (Exception e) {
						logger.warning("Unhandled exception: " + e);
						e.printStackTrace();
					}
				} else {
					logger.warning("Unexpected " + target);
				}
			}
		};
		allProperties.add(uriNameProperty);
		uriProperty = new SimpleBindingPathElementImpl<String>(URIChanged.URI_KEY, TypeUtils.getBaseClass(getType()), String.class, false,
				"uri_as_supplied_in_ontology") {
			@Override
			public String getBindingValue(Object target, BindingEvaluationContext context) {
				return ((OntologyObject) target).getURI();
			}

			@Override
			public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
				// not relevant because not settable
			}
		};
		allProperties.add(uriProperty);
	}

	public List<BindingPathElement> getAllProperties() {
		return allProperties;
	}

	@Override
	public Class<? extends Bindable> getDeclaringClass() {
		if (parentElement != null) {
			return TypeUtils.getBaseClass(parentElement.getType());
		}
		return Bindable.class;
	}

	@Override
	public String getSerializationRepresentation() {
		return getLabel();
	}

	@Override
	public boolean isBindingValid() {
		return true;
	}

	@Override
	public Type getType() {
		return OntologyObject.class;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return FlexoLocalization.localizedForKey("view_object");
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public T getBindingValue(Object target, BindingEvaluationContext context) {
		logger.warning("Que dois-je renvoyer pour " + target);
		return null;
	}

	@Override
	public void setBindingValue(T value, Object target, BindingEvaluationContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public Bindable getContainer() {
		return null;
	}

	@Override
	public String getVariableName() {
		return getLabel();
	}

	public static class OntologyClassPathElement extends OntologyObjectPathElement<OntologyClass> {
		public OntologyClassPathElement(String name, BindingPathElement aParentElement) {
			super(name, aParentElement);
		}

		@Override
		public Type getType() {
			return OntologyClass.class;
		}
	}

	public static class OntologyIndividualPathElement extends OntologyObjectPathElement<OntologyIndividual> {
		private OntologyClass ontologyType;
		private Vector<StatementPathElement> accessibleStatements;

		public OntologyIndividualPathElement(String name, BindingPathElement aParentElement) {
			this(name, null, aParentElement);
		}

		public OntologyIndividualPathElement(String name, OntologyClass ontologyType, BindingPathElement aParentElement) {
			super(name, aParentElement);
			this.ontologyType = ontologyType;
			accessibleStatements = new Vector<StatementPathElement>();
			if (ontologyType != null) {
				for (final OntologyProperty property : ontologyType.getPropertiesTakingMySelfAsDomain()) {
					StatementPathElement propertyPathElement = null;
					if (property instanceof OntologyObjectProperty) {
						propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
								(OntologyObjectProperty) property, true);
					} else if (property instanceof OntologyDataProperty) {
						propertyPathElement = new DataPropertyStatementPathElement(this, (OntologyDataProperty) property);
					}
					if (propertyPathElement != null) {
						accessibleStatements.add(propertyPathElement);
						allProperties.add(propertyPathElement);
					}
				}
			}
		}

		@Override
		public Type getType() {
			if (ontologyType != null) {
				return IndividualOfClass.getIndividualOfClass(ontologyType);
			}
			return OntologyIndividual.class;
		}

	}

	public static abstract class OntologyPropertyPathElement<T extends OntologyProperty> extends OntologyObjectPathElement<T> {
		public OntologyPropertyPathElement(String name, BindingPathElement aParentElement) {
			super(name, aParentElement);
		}

		@Override
		public Type getType() {
			return OntologyProperty.class;
		}

	}

	public static class OntologyDataPropertyPathElement extends OntologyPropertyPathElement<OntologyDataProperty> {
		public OntologyDataPropertyPathElement(String name, BindingPathElement aParentElement) {
			super(name, aParentElement);
		}

		@Override
		public Type getType() {
			return OntologyDataProperty.class;
		}

	}

	public static class OntologyObjectPropertyPathElement extends OntologyPropertyPathElement<OntologyObjectProperty> {
		public OntologyObjectPropertyPathElement(String name, BindingPathElement aParentElement) {
			super(name, aParentElement);
		}

		@Override
		public Type getType() {
			return OntologyObjectProperty.class;
		}

	}

}