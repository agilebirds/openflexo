package org.openflexo.foundation.viewpoint.binding;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimpleBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.DataRestrictionStatement;
import org.openflexo.foundation.ontology.IsAStatement;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.ObjectRestrictionStatement;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.ontology.RestrictionStatement;
import org.openflexo.foundation.ontology.RestrictionStatement.RestrictionType;
import org.openflexo.foundation.ontology.SubClassStatement;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
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
	}

	public BindingPathElement getUriNameProperty() {
		return uriNameProperty;
	}

	public BindingPathElement getUriProperty() {
		return uriProperty;
	}

	@Override
	public List<BindingPathElement> getAllProperties() {
		return allProperties;
	}

	public static class OntologicClassPatternRolePathElement extends OntologicObjectPatternRolePathElement<OntologyClass> {
		public OntologicClassPatternRolePathElement(ClassPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}
	}

	public static class OntologicIndividualPatternRolePathElement extends OntologicObjectPatternRolePathElement<OntologyIndividual> {
		Vector<StatementPathElement> accessibleStatements;

		public OntologicIndividualPatternRolePathElement(IndividualPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
			accessibleStatements = new Vector<StatementPathElement>();
			if (aPatternRole.getOntologicType() != null) {
				for (final OntologyProperty property : aPatternRole.getOntologicType().getPropertiesTakingMySelfAsDomain()) {
					StatementPathElement propertyPathElement = null;
					if (property instanceof OntologyObjectProperty) {
						propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
								(OntologyObjectProperty) property);
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
	}

	public static abstract class OntologicPropertyPatternRolePathElement<T extends OntologyProperty> extends
			OntologicObjectPatternRolePathElement<T> {
		public OntologicPropertyPatternRolePathElement(PropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}
	}

	public static class OntologicDataPropertyPatternRolePathElement extends OntologicPropertyPatternRolePathElement<OntologyDataProperty> {
		public OntologicDataPropertyPatternRolePathElement(DataPropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}
	}

	public static class OntologicObjectPropertyPatternRolePathElement extends
			OntologicPropertyPatternRolePathElement<OntologyObjectProperty> {
		public OntologicObjectPropertyPatternRolePathElement(ObjectPropertyPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}
	}

	public static class OntologicStatementPatternRolePathElement<T extends OntologyStatement> extends PatternRolePathElement<T> {
		private SimpleBindingPathElementImpl displayableRepresentation;
		private OntologyObjectPathElement subject;
		protected List<BindingPathElement> allProperties;

		public OntologicStatementPatternRolePathElement(StatementPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
			allProperties = new Vector<BindingPathElement>();
			displayableRepresentation = new SimpleBindingPathElementImpl<String>("displayableRepresentation", OntologyStatement.class,
					String.class, false, "string_representation_of_ontologic_statement_(read_only)") {
				@Override
				public String getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OntologyStatement) {
						return ((OntologyStatement) target).getDisplayableDescription();
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
			subject = new OntologyObjectPathElement("subject", this) {
				@Override
				public OntologyObject getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OntologyStatement) {
						return ((OntologyStatement) target).getSubject();
					}
					logger.warning("Unexpected " + target);
					return null;
				}

				@Override
				public void setBindingValue(OntologyObject value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
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
			parent = new OntologyObjectPathElement("parent", this) {
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
			predicate = new OntologyObjectPropertyPathElement("predicate", this) {
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
			object = new OntologyIndividualPathElement("object", this) {
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
			};
			allProperties.add(predicate);
			allProperties.add(object);
		}

	}

	public static class DataPropertyStatementPatternRolePathElement<E extends Bindable> extends
			OntologicStatementPatternRolePathElement<DataPropertyStatement> {
		private OntologyDataPropertyPathElement predicate;
		private SimpleBindingPathElementImpl<Object> value;

		public DataPropertyStatementPatternRolePathElement(DataPropertyStatementPatternRole aPatternRole, E container) {
			super(aPatternRole, container);
			predicate = new OntologyDataPropertyPathElement("predicate", this) {
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

	public static class RestrictionStatementPatternRolePathElement extends OntologicStatementPatternRolePathElement<RestrictionStatement> {
		private OntologyPropertyPathElement property;
		private OntologyClassPathElement object;
		private SimpleBindingPathElementImpl<RestrictionType> restrictionType;
		private SimpleBindingPathElementImpl<Integer> cardinality;
		private SimpleBindingPathElementImpl<OntologicDataType> dataRange;
		private SimpleBindingPathElementImpl<String> displayableRestriction;

		public RestrictionStatementPatternRolePathElement(RestrictionStatementPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);

			displayableRestriction = new SimpleBindingPathElementImpl<String>("displayableRestriction", OntologyStatement.class,
					String.class, false, "string_representation_of_restriction_(read_only)") {
				@Override
				public String getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OntologyStatement) {
						return ((OntologyStatement) target).getDisplayableDescription();
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

			property = new OntologyPropertyPathElement("property", this) {
				@Override
				public OntologyProperty getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof RestrictionStatement) {
						return ((RestrictionStatement) target).getProperty();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}
			};
			object = new OntologyClassPathElement("object", this) {
				@Override
				public OntologyClass getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof ObjectRestrictionStatement) {
						return ((ObjectRestrictionStatement) target).getObject();
					} else if (target instanceof DataRestrictionStatement) {
						logger.warning("object unavailable for DataPropertyStatement: " + target);
						return null;
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(OntologyClass value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			restrictionType = new SimpleBindingPathElementImpl<RestrictionStatement.RestrictionType>("restrictionType",
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
			};
			cardinality = new SimpleBindingPathElementImpl<Integer>("cardinality", RestrictionStatement.class, Integer.class, true,
					"cardinality_of_restriction") {
				@Override
				public Integer getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof RestrictionStatement) {
						return ((RestrictionStatement) target).getCardinality();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Integer value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			dataRange = new SimpleBindingPathElementImpl<OntologicDataType>("dataRange", RestrictionStatement.class,
					OntologicDataType.class, true, "data_range_of_restriction") {
				@Override
				public OntologicDataType getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof ObjectRestrictionStatement) {
						logger.warning("dataRange unavailable for ObjectRestrictionStatement: " + target);
						return null;
					} else if (target instanceof DataRestrictionStatement) {
						return ((DataRestrictionStatement) target).getDataRange();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(OntologicDataType value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			allProperties.add(property);
			allProperties.add(object);
			allProperties.add(restrictionType);
			allProperties.add(cardinality);
			allProperties.add(dataRange);
		}

	}

}