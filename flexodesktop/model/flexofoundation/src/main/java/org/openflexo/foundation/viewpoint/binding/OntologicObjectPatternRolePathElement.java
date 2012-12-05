package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimpleBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.SubClassOfClass;
import org.openflexo.foundation.ontology.SubPropertyOfProperty;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PropertyPatternRole;

public abstract class OntologicObjectPatternRolePathElement<T extends IFlexoOntologyConcept> extends PatternRolePathElement<T> {
	static final Logger logger = Logger.getLogger(OntologicObjectPatternRolePathElement.class.getPackage().getName());

	private SimpleBindingPathElementImpl uriNameProperty;
	private SimpleBindingPathElementImpl uriProperty;
	protected List<BindingPathElement> allProperties;

	// protected Vector<StatementPathElement> accessibleStatements;

	@Override
	public String toString() {
		return "[" + getPatternRole() + "/" + getClass().getSimpleName() + "]";
	}

	public OntologicObjectPatternRolePathElement(OntologicObjectPatternRole aPatternRole, Bindable container) {
		super(aPatternRole, container);
		allProperties = new Vector<BindingPathElement>();
		uriNameProperty = new SimpleBindingPathElementImpl<String>(URINameChanged.URI_NAME_KEY, TypeUtils.getBaseClass(getType()),
				String.class, true, "uri_name_as_supplied_in_ontology") {
			@Override
			public String getBindingValue(Object target, BindingEvaluationContext context) {
				return ((IFlexoOntologyConcept) target).getName();
			}

			@Override
			public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
				if (target instanceof IFlexoOntologyConcept) {
					try {
						logger.info("Rename URI of object " + target + " with " + value);
						((IFlexoOntologyConcept) target).setName(value);
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
				return ((IFlexoOntologyConcept) target).getURI();
			}

			@Override
			public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
				// not relevant because not settable
			}
		};
		allProperties.add(uriProperty);
		// accessibleStatements = new Vector<StatementPathElement>();
		// searchProperties(getOntologicType());
	}

	public BindingPathElement getUriNameProperty() {
		return uriNameProperty;
	}

	public BindingPathElement getUriProperty() {
		return uriProperty;
	}

	@Override
	public List<BindingPathElement> getAllProperties() {
		if (!propertiesFound) {
			searchProperties(getOntologicType());
		}
		return allProperties;
	}

	boolean propertiesFound = false;

	private void searchProperties(IFlexoOntologyConcept ontologicType) {

		if (ontologicType != null) {
			// System.out.println("Properties = "
			// + ((IndividualPatternRole) getPatternRole()).getOntologicType().getPropertiesTakingMySelfAsDomain());
			IFlexoOntologyStructuralProperty[] array = ontologicType.getPropertiesTakingMySelfAsDomain().toArray(
					new IFlexoOntologyStructuralProperty[ontologicType.getPropertiesTakingMySelfAsDomain().size()]);

			// Big trick here
			// A property may shadow another one relatively from its name
			// We try to detect such shadowing, and we put the most specialized property first
			// TODO: This should be properly rewritten in OWLObject

			List<Integer> i1 = new Vector<Integer>();
			List<Integer> i2 = new Vector<Integer>();
			for (int i = 0; i < array.length; i++) {
				for (int j = i + 1; j < array.length; j++) {
					if (array[i].getName().equals(array[j].getName())) {
						// Detected name based shadowing between array[i] and array[j]
						// System.out.println("Detected name based shadowing between " + array[i] + " and " + array[j]);
						if (array[i].getFlexoOntology().getAllImportedOntologies().contains(array[j].getFlexoOntology())) {
							// array[i] appears to be the most specialized, don't do anything
						} else if (array[j].getFlexoOntology().getAllImportedOntologies().contains(array[i].getFlexoOntology())) {
							// array[j] appears to be the most specialized, we need to swap
							i1.add(i);
							i2.add(j);
						}
					}
				}
			}
			for (int i = 0; i < i1.size(); i++) {
				IFlexoOntologyStructuralProperty p1 = array[i1.get(i)];
				IFlexoOntologyStructuralProperty p2 = array[i2.get(i)];
				array[i1.get(i)] = p2;
				array[i2.get(i)] = p1;
				// Swapping p1 and p2
			}

			for (final IFlexoOntologyStructuralProperty property : array) {
				StatementPathElement<?> propertyPathElement = null;
				if (property instanceof IFlexoOntologyObjectProperty) {
					propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
							(IFlexoOntologyObjectProperty) property, true, OntologyObjectPathElement.MAX_LEVELS);
				} else if (property instanceof IFlexoOntologyDataProperty) {
					propertyPathElement = new DataPropertyStatementPathElement(this, (IFlexoOntologyDataProperty) property);
				}
				if (propertyPathElement != null) {
					// System.out.println("add " + propertyPathElement);
					accessibleStatements.add(propertyPathElement);
					allProperties.add(propertyPathElement);
				}
			}*/
			propertiesFound = true;
		}
	}

	public abstract IFlexoOntologyClass getOntologicType();

	@Override
	public abstract Type getType();

	public static class OntologicClassPatternRolePathElement extends OntologicObjectPatternRolePathElement<IFlexoOntologyClass> {
		public OntologicClassPatternRolePathElement(ClassPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}

		@Override
		public Type getType() {
			return SubClassOfClass.getSubClassOfClass(getOntologicType());
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			if (((ClassPatternRole) getPatternRole()).getOntologicType() != null) {
				return ((ClassPatternRole) getPatternRole()).getOntologicType();
			}
			if (getPatternRole().getViewPoint() != null) {
				// Little hack to be refactored
				return getPatternRole().getViewPoint().getOntologyClass("http://www.w3.org/2002/07/owl#Thing");
			}
			return null;
		}
	}

	public static class OntologicIndividualPatternRolePathElement extends OntologicObjectPatternRolePathElement<IFlexoOntologyIndividual> {
		// Vector<StatementPathElement> accessibleStatements;

		public OntologicIndividualPatternRolePathElement(IndividualPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
			// accessibleStatements = new Vector<StatementPathElement>();
			// System.out.println("For role " + aPatternRole + " hash=" + Integer.toHexString(hashCode()));
			// System.out.println("Ontologic type = " + aPatternRole.getOntologicType());
			/*if (aPatternRole.getOntologicType() != null) {
				searchProperties();
			}*/
		}

		// boolean propertiesFound = false;

		/*private void searchProperties() {
			// System.out.println("Properties = "
			// + ((IndividualPatternRole) getPatternRole()).getOntologicType().getPropertiesTakingMySelfAsDomain());
			for (final IFlexoOntologyStructuralProperty property : ((IndividualPatternRole) getPatternRole()).getOntologicType()
					.getPropertiesTakingMySelfAsDomain()) {
				StatementPathElement propertyPathElement = null;
				if (property instanceof IFlexoOntologyObjectProperty) {
					propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
							(IFlexoOntologyObjectProperty) property, true);
				} else if (property instanceof IFlexoOntologyDataProperty) {
					propertyPathElement = new DataPropertyStatementPathElement(this, (IFlexoOntologyDataProperty) property);
				}
				if (propertyPathElement != null) {
					// System.out.println("add " + propertyPathElement);
					accessibleStatements.add(propertyPathElement);
					allProperties.add(propertyPathElement);
				}
			}
			propertiesFound = true;
		}*/

		/*@Override
		public List<BindingPathElement> getAllProperties() {
			if (!propertiesFound && ((IndividualPatternRole) getPatternRole()).getOntologicType() != null) {
				searchProperties();
			}
			// System.out.println("For " + getPatternRole() + " hash=" + Integer.toHexString(hashCode()) + " of "
			// + ((IndividualPatternRole) getPatternRole()).getOntologicType() + " have this " + super.getAllProperties());
			return super.getAllProperties();
		}*/

		@Override
		public Type getType() {
			return IndividualOfClass.getIndividualOfClass(getOntologicType());
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			if (((IndividualPatternRole) getPatternRole()).getOntologicType() != null) {
				return ((IndividualPatternRole) getPatternRole()).getOntologicType();
			}
			// Little hack to be refactored
			return getPatternRole().getViewPoint().getOntologyClass("http://www.w3.org/2002/07/owl#Thing");
			}

	}

	public static class OntologicPropertyPatternRolePathElement<T extends IFlexoOntologyStructuralProperty> extends
			OntologicObjectPatternRolePathElement<T> {
		public OntologicPropertyPatternRolePathElement(PropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}

		@Override
		public Type getType() {
			if (((PropertyPatternRole) getPatternRole()).getParentProperty() != null) {
				return SubPropertyOfProperty.getSubPropertyOfProperty(((PropertyPatternRole) getPatternRole()).getParentProperty());
			}
			return IFlexoOntologyStructuralProperty.class;
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			/*if (getPatternRole().getViewPoint().getViewpointOntology() != null) {
				return getPatternRole().getViewPoint().getViewpointOntology().getClass(RDFURIDefinitions.RDF_PROPERTY_URI);
			}*/
			return null;
		}
	}

	public static class OntologicDataPropertyPatternRolePathElement extends OntologicPropertyPatternRolePathElement<IFlexoOntologyDataProperty> {
		public OntologicDataPropertyPatternRolePathElement(DataPropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}

		@Override
		public Type getType() {
			if (((DataPropertyPatternRole) getPatternRole()).getParentProperty() != null) {
				return SubPropertyOfProperty.getSubPropertyOfProperty(((DataPropertyPatternRole) getPatternRole()).getParentProperty());
			}
			return IFlexoOntologyDataProperty.class;
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			/*if (getPatternRole().getViewPoint().getViewpointOntology() != null) {
				return getPatternRole().getViewPoint().getViewpointOntology().getClass(OWL2URIDefinitions.OWL_DATA_PROPERTY_URI);
			}*/
			return null;
		}

	}

	public static class OntologicObjectPropertyPatternRolePathElement extends
			OntologicPropertyPatternRolePathElement<IFlexoOntologyObjectProperty> {
		public OntologicObjectPropertyPatternRolePathElement(ObjectPropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}

		@Override
		public Type getType() {
			if (((ObjectPropertyPatternRole) getPatternRole()).getParentProperty() != null) {
				return SubPropertyOfProperty.getSubPropertyOfProperty(((ObjectPropertyPatternRole) getPatternRole()).getParentProperty());
			}
			return IFlexoOntologyObjectProperty.class;
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			/*if (getPatternRole().getViewPoint().getViewpointOntology() != null) {
				return getPatternRole().getViewPoint().getViewpointOntology().getClass(OWL2URIDefinitions.OWL_OBJECT_PROPERTY_URI);
			}
			return null;
		}

	}


}
