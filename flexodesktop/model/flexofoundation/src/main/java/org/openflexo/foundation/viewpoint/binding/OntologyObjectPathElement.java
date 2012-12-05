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
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.OntologyUtils;
import org.openflexo.foundation.ontology.SubClassOfClass;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
import org.openflexo.localization.FlexoLocalization;

public class OntologyObjectPathElement<T extends IFlexoOntologyConcept> implements SimplePathElement<T>, BindingVariable<T> {
	private static final Logger logger = Logger.getLogger(OntologyObjectPathElement.class.getPackage().getName());

	private String name;
	private BindingPathElement parentElement;

	private SimpleBindingPathElementImpl uriNameProperty;
	private SimpleBindingPathElementImpl uriProperty;

	protected List<BindingPathElement> allProperties;
	// private Vector<StatementPathElement> accessibleStatements;

	private IFlexoOntology viewpointOntology;

	public static final int MAX_LEVELS = 3;

	public OntologyObjectPathElement(String name, BindingPathElement aParentElement) {
		super();
		this.name = name;
		parentElement = aParentElement;
		this.viewpointOntology = viewpointOntology;
		allProperties = new Vector<BindingPathElement>();
		// accessibleStatements = new Vector<StatementPathElement>();
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
	}

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
						if (OntologyUtils.getAllImportedOntologies(array[i].getOntology()).contains(array[j].getOntology())) {
							// array[i] appears to be the most specialized, don't do anything
						} else if (OntologyUtils.getAllImportedOntologies(array[j].getOntology()).contains(array[i].getOntology())) {
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

			/*for (final IFlexoOntologyStructuralProperty property : array) {
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

	public IFlexoOntologyClass getOntologicType() {
		if (getViewpointOntology() != null) {
			return getViewpointOntology().getThingConcept();
		}
		return null;
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
		return IFlexoOntologyConcept.class;
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

	public IFlexoOntology getViewpointOntology() {
		return viewpointOntology;
	}

	public static class OntologyClassPathElement extends OntologyObjectPathElement<IFlexoOntologyClass> {
		private IFlexoOntologyClass ontologyType;

		public OntologyClassPathElement(String name, BindingPathElement aParentElement, IFlexoOntology viewpointOntology) {
			this(name, viewpointOntology.getThingConcept(), aParentElement);
		}

		public OntologyClassPathElement(String name, IFlexoOntologyClass ontologyType, BindingPathElement aParentElement) {
			super(name, aParentElement);
			this.ontologyType = ontologyType;
			/*accessibleStatements = new Vector<StatementPathElement>();
			if (ontologyType != null) {
				for (final IFlexoOntologyStructuralProperty property : ontologyType.getPropertiesTakingMySelfAsDomain()) {
					StatementPathElement propertyPathElement = null;
					if (property instanceof IFlexoOntologyObjectProperty) {
						propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
								(IFlexoOntologyObjectProperty) property, true, MAX_LEVELS);
					} else if (property instanceof IFlexoOntologyDataProperty) {
						propertyPathElement = new DataPropertyStatementPathElement(this, (IFlexoOntologyDataProperty) property);
					}
					if (propertyPathElement != null) {
						accessibleStatements.add(propertyPathElement);
						allProperties.add(propertyPathElement);
					}
				}
			}*/
		}

		/*@Override
		public Type getType() {
			if (ontologyType != null) {
				return SubClassOfClass.getSubClassOfClass(ontologyType);
			}
			return IFlexoOntologyClass.class;
		}*/

		@Override
		public Type getType() {
			return SubClassOfClass.getSubClassOfClass(getOntologicType());
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			if (ontologyType != null) {
				return ontologyType;
			}
			if (getViewpointOntology() != null) {
				return getViewpointOntology().getThingConcept();
			}
			return null;
		}

	}

	public static class OntologyIndividualPathElement extends OntologyObjectPathElement<IFlexoOntologyIndividual> {
		private IFlexoOntologyClass ontologyType;

		public OntologyIndividualPathElement(String name, BindingPathElement aParentElement, IFlexoOntology viewpointOntology) {
			this(name, null, aParentElement);
		}

		public OntologyIndividualPathElement(String name, IFlexoOntologyClass ontologyType, BindingPathElement aParentElement) {
			super(name, aParentElement);
			this.ontologyType = ontologyType;
			/*accessibleStatements = new Vector<StatementPathElement>();
			if (ontologyType != null) {
				for (final IFlexoOntologyStructuralProperty property : ontologyType.getPropertiesTakingMySelfAsDomain()) {
					StatementPathElement propertyPathElement = null;
					if (property instanceof IFlexoOntologyObjectProperty) {
						propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
								(IFlexoOntologyObjectProperty) property, true, MAX_LEVELS);
					} else if (property instanceof IFlexoOntologyDataProperty) {
						propertyPathElement = new DataPropertyStatementPathElement(this, (IFlexoOntologyDataProperty) property);
					}
					if (propertyPathElement != null) {
						accessibleStatements.add(propertyPathElement);
						allProperties.add(propertyPathElement);
					}
				}
			}*/
		}

		@Override
		public Type getType() {
			return IndividualOfClass.getIndividualOfClass(getOntologicType());
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			if (ontologyType != null) {
				return ontologyType;
			}
			if (getViewpointOntology() != null) {
				return getViewpointOntology().getThingConcept();
			}
			return null;
		}

	}

	public static abstract class OntologyPropertyPathElement<T extends IFlexoOntologyStructuralProperty> extends
			OntologyObjectPathElement<T> {
		public OntologyPropertyPathElement(String name, BindingPathElement aParentElement) {
			super(name, aParentElement);
		}

		@Override
		public Type getType() {
			return IFlexoOntologyStructuralProperty.class;
		}

	}

	public static class OntologyDataPropertyPathElement extends OntologyPropertyPathElement<IFlexoOntologyDataProperty> {
		public OntologyDataPropertyPathElement(String name, BindingPathElement aParentElement) {
			super(name, aParentElement);
		}

		@Override
		public Type getType() {
			return IFlexoOntologyDataProperty.class;
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			/*if (getViewpointOntology() != null) {
				return getViewpointOntology().getClass(OWL2URIDefinitions.OWL_DATA_PROPERTY_URI);
			}*/
			return null;
		}

	}

	public static class OntologyObjectPropertyPathElement extends OntologyPropertyPathElement<IFlexoOntologyObjectProperty> {
		public OntologyObjectPropertyPathElement(String name, BindingPathElement aParentElement) {
			super(name, aParentElement);
		}

		@Override
		public Type getType() {
			return IFlexoOntologyObjectProperty.class;
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			/*if (getViewpointOntology() != null) {
				return getViewpointOntology().getClass(OWL2URIDefinitions.OWL_OBJECT_PROPERTY_URI);
			}*/
			return null;
		}

	}

}
