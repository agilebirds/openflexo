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
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.SubClassOfClass;
import org.openflexo.foundation.ontology.SubPropertyOfProperty.SubDataPropertyOfProperty;
import org.openflexo.foundation.ontology.SubPropertyOfProperty.SubObjectPropertyOfProperty;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
import org.openflexo.foundation.ontology.owl.DataPropertyStatement;
import org.openflexo.foundation.ontology.owl.IsAStatement;
import org.openflexo.foundation.ontology.owl.OWLObject;
import org.openflexo.foundation.ontology.owl.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.owl.OWLStatement;
import org.openflexo.foundation.ontology.owl.SubClassStatement;
import org.openflexo.foundation.ontology.owl.OntologyRestrictionClass.RestrictionType;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.IsAStatementPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PropertyPatternRole;
import org.openflexo.foundation.viewpoint.RestrictionStatementPatternRole;
import org.openflexo.foundation.viewpoint.StatementPatternRole;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyClassPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyDataPropertyPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyIndividualPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyObjectPropertyPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyPropertyPathElement;

public abstract class OntologicObjectPatternRolePathElement<T extends OntologyObject> extends PatternRolePathElement<T> {
	private static final Logger logger = Logger.getLogger(OntologicObjectPatternRolePathElement.class.getPackage().getName());

	private SimpleBindingPathElementImpl uriNameProperty;
	private SimpleBindingPathElementImpl uriProperty;
	protected List<BindingPathElement> allProperties;

	protected Vector<StatementPathElement> accessibleStatements;

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
		accessibleStatements = new Vector<StatementPathElement>();
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

	private void searchProperties(OntologyObject ontologicType) {

		if (ontologicType != null) {
			// System.out.println("Properties = "
			// + ((IndividualPatternRole) getPatternRole()).getOntologicType().getPropertiesTakingMySelfAsDomain());
			OntologyProperty[] array = ontologicType.getPropertiesTakingMySelfAsDomain().toArray(
					new OntologyProperty[ontologicType.getPropertiesTakingMySelfAsDomain().size()]);

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
						if (array[i].getOntology().getAllImportedOntologies().contains(array[j].getOntology())) {
							// array[i] appears to be the most specialized, don't do anything
						} else if (array[j].getOntology().getAllImportedOntologies().contains(array[i].getOntology())) {
							// array[j] appears to be the most specialized, we need to swap
							i1.add(i);
							i2.add(j);
						}
					}
				}
			}
			for (int i = 0; i < i1.size(); i++) {
				OntologyProperty p1 = array[i1.get(i)];
				OntologyProperty p2 = array[i2.get(i)];
				array[i1.get(i)] = p2;
				array[i2.get(i)] = p1;
				// Swapping p1 and p2
			}

			for (final OntologyProperty property : array) {
				StatementPathElement<?> propertyPathElement = null;
				if (property instanceof OntologyObjectProperty) {
					propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
							(OntologyObjectProperty) property, true, OntologyObjectPathElement.MAX_LEVELS);
				} else if (property instanceof OntologyDataProperty) {
					propertyPathElement = new DataPropertyStatementPathElement(this, (OntologyDataProperty) property);
				}
				if (propertyPathElement != null) {
					// System.out.println("add " + propertyPathElement);
					accessibleStatements.add(propertyPathElement);
					allProperties.add(propertyPathElement);
				}
			}
			propertiesFound = true;
		}
	}

	public abstract OntologyClass getOntologicType();

	@Override
	public abstract Type getType();

	public static class OntologicClassPatternRolePathElement extends OntologicObjectPatternRolePathElement<OntologyClass> {
		public OntologicClassPatternRolePathElement(ClassPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}

		@Override
		public Type getType() {
			return SubClassOfClass.getSubClassOfClass(getOntologicType());
		}

		@Override
		public OntologyClass getOntologicType() {
			if (((ClassPatternRole) getPatternRole()).getOntologicType() != null) {
				return ((ClassPatternRole) getPatternRole()).getOntologicType();
			}
			if (getPatternRole().getViewPoint().getViewpointOntology() != null) {
				return getPatternRole().getViewPoint().getViewpointOntology().getThingConcept();
			}
			return null;
		}
	}

	public static class OntologicIndividualPatternRolePathElement extends OntologicObjectPatternRolePathElement<OntologyIndividual> {
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
			for (final OntologyProperty property : ((IndividualPatternRole) getPatternRole()).getOntologicType()
					.getPropertiesTakingMySelfAsDomain()) {
				StatementPathElement propertyPathElement = null;
				if (property instanceof OntologyObjectProperty) {
					propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
							(OntologyObjectProperty) property, true);
				} else if (property instanceof OntologyDataProperty) {
					propertyPathElement = new DataPropertyStatementPathElement(this, (OntologyDataProperty) property);
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
		public OntologyClass getOntologicType() {
			if (((IndividualPatternRole) getPatternRole()).getOntologicType() != null) {
				return ((IndividualPatternRole) getPatternRole()).getOntologicType();
			}
			if (getPatternRole().getViewPoint().getViewpointOntology() != null) {
				return getPatternRole().getViewPoint().getViewpointOntology().getThingConcept();
			}
			return null;
		}

	}

	public static class OntologicPropertyPatternRolePathElement<T extends OntologyProperty> extends
			OntologicObjectPatternRolePathElement<T> {
		public OntologicPropertyPatternRolePathElement(PropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}

		@Override
		public Type getType() {
			if (((PropertyPatternRole) getPatternRole()).getParentProperty() != null) {
				return SubDataPropertyOfProperty.getSubPropertyOfProperty(((PropertyPatternRole) getPatternRole()).getParentProperty());
			}
			return OntologyProperty.class;
		}

		@Override
		public OntologyClass getOntologicType() {
			if (getPatternRole().getViewPoint().getViewpointOntology() != null) {
				return getPatternRole().getViewPoint().getViewpointOntology().getClass(OntologyObject.RDF_PROPERTY_URI);
			}
			return null;
		}

	}

	public static class OntologicDataPropertyPatternRolePathElement extends OntologicPropertyPatternRolePathElement<OntologyDataProperty> {
		public OntologicDataPropertyPatternRolePathElement(DataPropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}

		@Override
		public Type getType() {
			if (((DataPropertyPatternRole) getPatternRole()).getParentProperty() != null) {
				return SubDataPropertyOfProperty.getSubPropertyOfProperty(((DataPropertyPatternRole) getPatternRole()).getParentProperty());
			}
			return OntologyDataProperty.class;
		}

		@Override
		public OntologyClass getOntologicType() {
			if (getPatternRole().getViewPoint().getViewpointOntology() != null) {
				return getPatternRole().getViewPoint().getViewpointOntology().getClass(OWLObject.OWL_DATA_PROPERTY_URI);
			}
			return null;
		}

	}

	public static class OntologicObjectPropertyPatternRolePathElement extends
			OntologicPropertyPatternRolePathElement<OntologyObjectProperty> {
		public OntologicObjectPropertyPatternRolePathElement(ObjectPropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}

		@Override
		public Type getType() {
			if (((ObjectPropertyPatternRole) getPatternRole()).getParentProperty() != null) {
				return SubObjectPropertyOfProperty.getSubPropertyOfProperty(((ObjectPropertyPatternRole) getPatternRole())
						.getParentProperty());
			}
			return OntologyObjectProperty.class;
		}

		@Override
		public OntologyClass getOntologicType() {
			if (getPatternRole().getViewPoint().getViewpointOntology() != null) {
				return getPatternRole().getViewPoint().getViewpointOntology().getClass(OWLObject.OWL_OBJECT_PROPERTY_URI);
			}
			return null;
		}

	}

	public static class OntologicStatementPatternRolePathElement<T extends OWLStatement> extends PatternRolePathElement<T> {
		private SimpleBindingPathElementImpl displayableRepresentation;
		private OntologyObjectPathElement subject;
		protected List<BindingPathElement> allProperties;

		public OntologicStatementPatternRolePathElement(StatementPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
			allProperties = new Vector<BindingPathElement>();
			displayableRepresentation = new SimpleBindingPathElementImpl<String>("displayableRepresentation", OWLStatement.class,
					String.class, false, "string_representation_of_ontologic_statement_(read_only)") {
				@Override
				public String getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OWLStatement) {
						return ((OWLStatement) target).getDisplayableDescription();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			OntologyClass subjectType = null;
			if (aPatternRole instanceof DataPropertyStatementPatternRole
					&& ((DataPropertyStatementPatternRole) aPatternRole).getDataProperty() != null
					&& ((DataPropertyStatementPatternRole) aPatternRole).getDataProperty().getDomain() instanceof OntologyClass) {
				subjectType = (OntologyClass) ((DataPropertyStatementPatternRole) aPatternRole).getDataProperty().getDomain();
				subject = new OntologyIndividualPathElement("subject", subjectType, this, aPatternRole.getViewPoint()
						.getViewpointOntology()) {
					@Override
					public OntologyIndividual getBindingValue(Object target, BindingEvaluationContext context) {
						if (target instanceof OWLStatement) {
							return (OntologyIndividual) ((OWLStatement) target).getSubject();
						}
						logger.warning("Unexpected " + target);
						return null;
					}

					@Override
					public void setBindingValue(OntologyIndividual value, Object target, BindingEvaluationContext context) {
						// not relevant because not settable
					}

					@Override
					public Type getType() {
						if (((DataPropertyStatementPatternRole) getPatternRole()).getDataProperty() != null
								&& ((DataPropertyStatementPatternRole) getPatternRole()).getDataProperty().getDomain() instanceof OntologyClass) {
							return IndividualOfClass
									.getIndividualOfClass((OntologyClass) ((DataPropertyStatementPatternRole) getPatternRole())
											.getDataProperty().getDomain());
						}
						return super.getType();
					}
				};
			} else if (aPatternRole instanceof ObjectPropertyStatementPatternRole
					&& ((ObjectPropertyStatementPatternRole) aPatternRole).getObjectProperty() != null
					&& ((ObjectPropertyStatementPatternRole) aPatternRole).getObjectProperty().getDomain() instanceof OntologyClass) {
				subjectType = (OntologyClass) ((ObjectPropertyStatementPatternRole) aPatternRole).getObjectProperty().getDomain();
				subject = new OntologyIndividualPathElement("subject", subjectType, this, aPatternRole.getViewPoint()
						.getViewpointOntology()) {
					@Override
					public OntologyIndividual getBindingValue(Object target, BindingEvaluationContext context) {
						if (target instanceof OWLStatement) {
							return (OntologyIndividual) ((OWLStatement) target).getSubject();
						}
						logger.warning("Unexpected " + target);
						return null;
					}

					@Override
					public void setBindingValue(OntologyIndividual value, Object target, BindingEvaluationContext context) {
						// not relevant because not settable
					}

					@Override
					public Type getType() {
						if (((ObjectPropertyStatementPatternRole) getPatternRole()).getObjectProperty() != null
								&& ((ObjectPropertyStatementPatternRole) getPatternRole()).getObjectProperty().getDomain() instanceof OntologyClass) {
							return IndividualOfClass
									.getIndividualOfClass((OntologyClass) ((ObjectPropertyStatementPatternRole) getPatternRole())
											.getObjectProperty().getDomain());
						}
						return super.getType();
					}
				};
			} else {
				subject = new OntologyObjectPathElement("subject", this, aPatternRole.getViewpointOntology()) {
					@Override
					public OntologyObject getBindingValue(Object target, BindingEvaluationContext context) {
						if (target instanceof OWLStatement) {
							return ((OWLStatement) target).getSubject();
						}
						logger.warning("Unexpected " + target);
						return null;
					}

					@Override
					public void setBindingValue(OntologyObject value, Object target, BindingEvaluationContext context) {
						// not relevant because not settable
					}
				};
			}
			allProperties.add(displayableRepresentation);
			allProperties.add(subject);
		}

		@Override
		public List<BindingPathElement> getAllProperties() {
			return allProperties;
		}
	}

	public static class IsAStatementPatternRolePathElement extends OntologicStatementPatternRolePathElement<IsAStatement> {
		private OntologyObjectPathElement parent;

		public IsAStatementPatternRolePathElement(IsAStatementPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
			parent = new OntologyObjectPathElement("parent", this, aPatternRole.getViewpointOntology()) {
				@Override
				public OntologyObject getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof IsAStatement) {
						return ((IsAStatement) target).getParentObject();
					} else if (target instanceof SubClassStatement) {
						return ((SubClassStatement) target).getParent();
					}
					logger.warning("Unexpected " + target + " of " + target.getClass());
					return null;
				}

				@Override
				public void setBindingValue(OntologyObject value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			allProperties.add(parent);
		}

	}

	public static class ObjectPropertyStatementPatternRolePathElement<E extends Bindable> extends
			OntologicStatementPatternRolePathElement<ObjectPropertyStatement> {
		private OntologyObjectPropertyPathElement predicate;
		private OntologyIndividualPathElement object;

		public ObjectPropertyStatementPatternRolePathElement(ObjectPropertyStatementPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
			predicate = new OntologyObjectPropertyPathElement("predicate", this, aPatternRole.getViewPoint().getViewpointOntology()) {
				@Override
				public OntologyObjectProperty getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof ObjectPropertyStatement) {
						return ((ObjectPropertyStatement) target).getPredicate();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(OntologyObjectProperty value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			OntologyClass objectType = null;
			if (aPatternRole.getObjectProperty() instanceof OntologyObjectProperty
					&& ((OntologyObjectProperty) aPatternRole.getObjectProperty()).getRange() instanceof OntologyClass) {
				objectType = (OntologyClass) ((OntologyObjectProperty) aPatternRole.getObjectProperty()).getRange();
			}
			object = new OntologyIndividualPathElement("object", objectType, this, aPatternRole.getViewPoint().getViewpointOntology()) {
				@Override
				public OntologyIndividual getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof ObjectPropertyStatement) {
						return (OntologyIndividual) ((ObjectPropertyStatement) target).getStatementObject();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(OntologyIndividual value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}

				@Override
				public Type getType() {
					if (((ObjectPropertyStatementPatternRole) getPatternRole()).getObjectProperty() != null
							&& ((OntologyObjectProperty) ((ObjectPropertyStatementPatternRole) getPatternRole()).getObjectProperty())
									.getRange() instanceof OntologyClass) {
						return IndividualOfClass
								.getIndividualOfClass((OntologyClass) ((OntologyObjectProperty) ((ObjectPropertyStatementPatternRole) getPatternRole())
										.getObjectProperty()).getRange());
					}
					return super.getType();
				}
			};
			allProperties.add(predicate);
			allProperties.add(object);
		}

		@Override
		public Type getType() {
			// TODO Auto-generated method stub
			return super.getType();
		}

	}

	public static class DataPropertyStatementPatternRolePathElement<E extends Bindable> extends
			OntologicStatementPatternRolePathElement<DataPropertyStatement> {
		private OntologyDataPropertyPathElement predicate;
		private SimpleBindingPathElementImpl<Object> value;

		public DataPropertyStatementPatternRolePathElement(DataPropertyStatementPatternRole aPatternRole, E container) {
			super(aPatternRole, container);
			predicate = new OntologyDataPropertyPathElement("predicate", this, aPatternRole.getViewPoint().getViewpointOntology()) {
				@Override
				public OntologyDataProperty getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof DataPropertyStatement) {
						return ((DataPropertyStatement) target).getPredicate();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(OntologyDataProperty value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			allProperties.add(predicate);
			if (aPatternRole.getDataProperty() != null && aPatternRole.getDataProperty().getDataType() != null) {
				value = new SimpleBindingPathElementImpl<Object>("value", DataPropertyStatement.class, aPatternRole.getDataProperty()
						.getDataType().getAccessedType(), false, "object_of_statement") {
					@Override
					public Object getBindingValue(Object target, BindingEvaluationContext context) {
						if (target instanceof DataPropertyStatement) {
							return ((DataPropertyStatement) target).getValue();
						} else {
							logger.warning("Unexpected: " + target);
							return null;
						}
					}

					@Override
					public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
						// not relevant because not settable
					}
				};
				allProperties.add(value);
			}
		}

	}

	public static class RestrictionStatementPatternRolePathElement extends OntologicStatementPatternRolePathElement<SubClassStatement> {
		private OntologyPropertyPathElement property;
		private OntologyClassPathElement object;
		private SimpleBindingPathElementImpl<RestrictionType> restrictionType;
		private SimpleBindingPathElementImpl<Integer> cardinality;
		private SimpleBindingPathElementImpl<OntologicDataType> dataRange;
		private SimpleBindingPathElementImpl<String> displayableRestriction;

		public RestrictionStatementPatternRolePathElement(RestrictionStatementPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);

			displayableRestriction = new SimpleBindingPathElementImpl<String>("displayableRestriction", OWLStatement.class,
					String.class, false, "string_representation_of_restriction_(read_only)") {
				@Override
				public String getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OWLStatement) {
						return ((OWLStatement) target).getDisplayableDescription();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};

			property = new OntologyPropertyPathElement("property", this, aPatternRole.getViewPoint().getViewpointOntology()) {
				@Override
				public OntologyProperty getBindingValue(Object target, BindingEvaluationContext context) {
					/*if (target instanceof RestrictionStatement) {
						return ((RestrictionStatement) target).getProperty();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}*/
					logger.warning("Not implemented: " + target);
					return null;
				}
			};
			object = new OntologyClassPathElement("object", this, aPatternRole.getViewPoint().getViewpointOntology()) {
				@Override
				public OntologyClass getBindingValue(Object target, BindingEvaluationContext context) {
					/*if (target instanceof ObjectRestrictionStatement) {
						return ((ObjectRestrictionStatement) target).getObject();
					} else if (target instanceof DataRestrictionStatement) {
						logger.warning("object unavailable for DataPropertyStatement: " + target);
						return null;
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}*/
					logger.warning("Not implemented: " + target);
					return null;
				}

				@Override
				public void setBindingValue(OntologyClass value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			/*restrictionType = new SimpleBindingPathElementImpl<RestrictionStatement.RestrictionType>("restrictionType",
					RestrictionStatement.class, RestrictionType.class, true, "restriction_type") {
				@Override
				public RestrictionType getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof RestrictionStatement) {
						return ((RestrictionStatement) target).getRestrictionType();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(RestrictionType value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};*/
			cardinality = new SimpleBindingPathElementImpl<Integer>("cardinality", SubClassStatement.class, Integer.class, true,
					"cardinality_of_restriction") {
				@Override
				public Integer getBindingValue(Object target, BindingEvaluationContext context) {
					/*if (target instanceof RestrictionStatement) {
						return ((RestrictionStatement) target).getCardinality();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}*/
					logger.warning("Not implemented: " + target);
					return null;
				}

				@Override
				public void setBindingValue(Integer value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			dataRange = new SimpleBindingPathElementImpl<OntologicDataType>("dataRange", SubClassStatement.class, OntologicDataType.class,
					true, "data_range_of_restriction") {
				@Override
				public OntologicDataType getBindingValue(Object target, BindingEvaluationContext context) {
					/*if (target instanceof ObjectRestrictionStatement) {
						logger.warning("dataRange unavailable for ObjectRestrictionStatement: " + target);
						return null;
					} else if (target instanceof DataRestrictionStatement) {
						return ((DataRestrictionStatement) target).getDataRange();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}*/
					logger.warning("Not implemented: " + target);
					return null;
				}

				@Override
				public void setBindingValue(OntologicDataType value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			allProperties.add(property);
			allProperties.add(object);
			// allProperties.add(restrictionType);
			allProperties.add(cardinality);
			allProperties.add(dataRange);
		}

	}

}
