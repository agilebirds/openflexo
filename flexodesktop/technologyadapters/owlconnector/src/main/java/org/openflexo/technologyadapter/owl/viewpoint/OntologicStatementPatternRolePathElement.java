package org.openflexo.technologyadapter.owl.viewpoint;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyClassPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyDataPropertyPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyIndividualPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyObjectPropertyPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyPropertyPathElement;
import org.openflexo.foundation.viewpoint.binding.PatternRolePathElement;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;
import org.openflexo.technologyadapter.owl.model.IsAStatement;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLDataType;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLProperty;
import org.openflexo.technologyadapter.owl.model.OWLRestriction.RestrictionType;
import org.openflexo.technologyadapter.owl.model.OWLStatement;
import org.openflexo.technologyadapter.owl.model.ObjectPropertyStatement;
import org.openflexo.technologyadapter.owl.model.SubClassStatement;

public class OntologicStatementPatternRolePathElement<T extends OWLStatement> extends PatternRolePathElement<T> {

	private static final Logger logger = Logger.getLogger(OntologicStatementPatternRolePathElement.class.getPackage().getName());

	private SimpleBindingPathElementImpl displayableRepresentation;
	private OntologyObjectPathElement subject;
	protected List<BindingPathElement> allProperties;

	public OntologicStatementPatternRolePathElement(StatementPatternRole aPatternRole, Bindable container) {
		super(aPatternRole, container);
		allProperties = new Vector<BindingPathElement>();
		displayableRepresentation = new SimpleBindingPathElementImpl<String>("displayableRepresentation", OWLStatement.class, String.class,
				false, "string_representation_of_ontologic_statement_(read_only)") {
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
		OWLClass subjectType = null;
		if (aPatternRole instanceof DataPropertyStatementPatternRole
				&& ((DataPropertyStatementPatternRole) aPatternRole).getDataProperty() != null
				&& ((DataPropertyStatementPatternRole) aPatternRole).getDataProperty().getDomain() instanceof OWLClass) {
			subjectType = (OWLClass) ((DataPropertyStatementPatternRole) aPatternRole).getDataProperty().getDomain();
			subject = new OntologyIndividualPathElement<OWLIndividual>("subject", subjectType, this) {
				@Override
				public OWLIndividual getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OWLStatement) {
						return (OWLIndividual) ((OWLStatement) target).getSubject();
					}
					logger.warning("Unexpected " + target);
					return null;
				}

				@Override
				public void setBindingValue(OWLIndividual value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}

				@Override
				public Type getType() {
					if (((DataPropertyStatementPatternRole) getPatternRole()).getDataProperty() != null
							&& ((DataPropertyStatementPatternRole) getPatternRole()).getDataProperty().getDomain() instanceof OWLClass) {
						return IndividualOfClass.getIndividualOfClass((OWLClass) ((DataPropertyStatementPatternRole) getPatternRole())
								.getDataProperty().getDomain());
					}
					return super.getType();
				}
			};
		} else if (aPatternRole instanceof ObjectPropertyStatementPatternRole
				&& ((ObjectPropertyStatementPatternRole) aPatternRole).getObjectProperty() != null
				&& ((ObjectPropertyStatementPatternRole) aPatternRole).getObjectProperty().getDomain() instanceof OWLClass) {
			subjectType = (OWLClass) ((ObjectPropertyStatementPatternRole) aPatternRole).getObjectProperty().getDomain();
			subject = new OntologyIndividualPathElement<OWLIndividual>("subject", subjectType, this) {
				@Override
				public OWLIndividual getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OWLStatement) {
						return (OWLIndividual) ((OWLStatement) target).getSubject();
					}
					logger.warning("Unexpected " + target);
					return null;
				}

				@Override
				public void setBindingValue(OWLIndividual value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}

				@Override
				public Type getType() {
					if (((ObjectPropertyStatementPatternRole) getPatternRole()).getObjectProperty() != null
							&& ((ObjectPropertyStatementPatternRole) getPatternRole()).getObjectProperty().getDomain() instanceof OWLClass) {
						return IndividualOfClass.getIndividualOfClass((OWLClass) ((ObjectPropertyStatementPatternRole) getPatternRole())
								.getObjectProperty().getDomain());
					}
					return super.getType();
				}
			};
		} else {
			subject = new OntologyObjectPathElement<OWLConcept<?>>("subject", this) {
				@Override
				public OWLConcept<?> getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OWLStatement) {
						return ((OWLStatement) target).getSubject();
					}
					logger.warning("Unexpected " + target);
					return null;
				}

				@Override
				public void setBindingValue(OWLConcept<?> value, Object target, BindingEvaluationContext context) {
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

	public static class IsAStatementPatternRolePathElement extends OntologicStatementPatternRolePathElement<SubClassStatement> {
		private OntologyObjectPathElement parent;

		public IsAStatementPatternRolePathElement(SubClassStatementPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
			parent = new OntologyObjectPathElement<OWLConcept<?>>("parent", this) {
				@Override
				public OWLConcept<?> getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof IsAStatement) {
						return ((IsAStatement) target).getParentObject();
					} else if (target instanceof SubClassStatement) {
						return ((SubClassStatement) target).getParent();
					}
					logger.warning("Unexpected " + target + " of " + target.getClass());
					return null;
				}

				@Override
				public void setBindingValue(OWLConcept<?> value, Object target, BindingEvaluationContext context) {
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
			predicate = new OntologyObjectPropertyPathElement<OWLObjectProperty>("predicate", this) {
				@Override
				public OWLObjectProperty getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof ObjectPropertyStatement) {
						return ((ObjectPropertyStatement) target).getPredicate();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(OWLObjectProperty value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			OWLClass objectType = null;
			if (aPatternRole.getObjectProperty() instanceof OWLObjectProperty
					&& ((OWLObjectProperty) aPatternRole.getObjectProperty()).getRange() instanceof OWLClass) {
				objectType = ((OWLObjectProperty) aPatternRole.getObjectProperty()).getRange();
			}
			object = new OntologyIndividualPathElement<OWLIndividual>("object", objectType, this) {
				@Override
				public OWLIndividual getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof ObjectPropertyStatement) {
						return (OWLIndividual) ((ObjectPropertyStatement) target).getStatementObject();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(OWLIndividual value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}

				@Override
				public Type getType() {
					if (((ObjectPropertyStatementPatternRole) getPatternRole()).getObjectProperty() != null
							&& ((OWLObjectProperty) ((ObjectPropertyStatementPatternRole) getPatternRole()).getObjectProperty()).getRange() instanceof OWLClass) {
						return IndividualOfClass
								.getIndividualOfClass(((OWLObjectProperty) ((ObjectPropertyStatementPatternRole) getPatternRole())
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
			predicate = new OntologyDataPropertyPathElement<OWLDataProperty>("predicate", this) {
				@Override
				public OWLDataProperty getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof DataPropertyStatement) {
						return ((DataPropertyStatement) target).getPredicate();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(OWLDataProperty value, Object target, BindingEvaluationContext context) {
					// not relevant because not settable
				}
			};
			allProperties.add(predicate);
			if (aPatternRole.getDataProperty() != null && aPatternRole.getDataProperty().getRange() != null) {
				value = new SimpleBindingPathElementImpl<Object>("value", DataPropertyStatement.class, aPatternRole.getDataProperty()
						.getRange().getAccessedType(), false, "object_of_statement") {
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
		private SimpleBindingPathElementImpl<OWLDataType> dataRange;
		private SimpleBindingPathElementImpl<String> displayableRestriction;

		public RestrictionStatementPatternRolePathElement(RestrictionStatementPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);

			displayableRestriction = new SimpleBindingPathElementImpl<String>("displayableRestriction", OWLStatement.class, String.class,
					false, "string_representation_of_restriction_(read_only)") {
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

			property = new OntologyPropertyPathElement<OWLProperty>("property", this) {
				@Override
				public OWLProperty getBindingValue(Object target, BindingEvaluationContext context) {
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
			object = new OntologyClassPathElement<OWLClass>("object", this) {
				@Override
				public OWLClass getBindingValue(Object target, BindingEvaluationContext context) {
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
				public void setBindingValue(OWLClass value, Object target, BindingEvaluationContext context) {
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
			dataRange = new SimpleBindingPathElementImpl<OWLDataType>("dataRange", SubClassStatement.class, OWLDataType.class, true,
					"data_range_of_restriction") {
				@Override
				public OWLDataType getBindingValue(Object target, BindingEvaluationContext context) {
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
				public void setBindingValue(OWLDataType value, Object target, BindingEvaluationContext context) {
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