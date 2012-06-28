package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimpleBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.PropertyStatement;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;

import com.hp.hpl.jena.rdf.model.Literal;

public abstract class ObjectPropertyStatementPathElement<T> extends StatementPathElement<T> {
	private static final Logger logger = Logger.getLogger(ObjectPropertyStatementPathElement.class.getPackage().getName());

	private OntologyObjectProperty ontologyProperty;

	public static ObjectPropertyStatementPathElement makeObjectPropertyStatementPathElement(BindingPathElement aParent,
			OntologyObjectProperty anOntologyProperty, boolean recursive, int levels) {
		if (anOntologyProperty.isLiteralRange()) {
			return new ObjectPropertyStatementAccessingLiteralPathElement(aParent, anOntologyProperty);
		} else {
			return new ObjectPropertyStatementAccessingObjectPathElement(aParent, anOntologyProperty, recursive && levels > 0, levels - 1);
		}
	}

	private ObjectPropertyStatementPathElement(BindingPathElement aParent, OntologyObjectProperty anOntologyProperty) {
		super(aParent);
		ontologyProperty = anOntologyProperty;

	}

	@Override
	public String getLabel() {
		return ontologyProperty.getName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return ontologyProperty.getDisplayableDescription();
	}

	@Override
	public boolean isSettable() {
		return true;
	}

	public OntologyObjectProperty getOntologyProperty() {
		return ontologyProperty;
	}

	public static class ObjectPropertyStatementAccessingObjectPathElement extends ObjectPropertyStatementPathElement<OntologyObject> {

		private SimpleBindingPathElementImpl<String> uriNameProperty;
		private SimpleBindingPathElementImpl<String> uriProperty;

		public ObjectPropertyStatementAccessingObjectPathElement(BindingPathElement aParent, OntologyObjectProperty anOntologyProperty,
				boolean recursive, int levels) {
			super(aParent, anOntologyProperty);

			uriNameProperty = new SimpleBindingPathElementImpl<String>(URINameChanged.URI_NAME_KEY, TypeUtils.getBaseClass(getType()),
					String.class, true, "uri_name_as_supplied_in_ontology") {
				@Override
				public String getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OntologyObject) {
						return ((OntologyObject) target).getName();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
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
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(uriNameProperty);
			uriProperty = new SimpleBindingPathElementImpl<String>(URIChanged.URI_KEY, TypeUtils.getBaseClass(getType()), String.class,
					false, "uri_as_supplied_in_ontology") {
				@Override
				public String getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof OntologyObject) {
						return ((OntologyObject) target).getURI();
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
			allProperties.add(uriProperty);

			/*if (recursive && levels > 0) {
				if (anOntologyProperty.getRange() instanceof OntologyClass) {
					OntologyClass rangeClass = (OntologyClass) anOntologyProperty.getRange();
					for (final OntologyProperty property : rangeClass.getPropertiesTakingMySelfAsDomain()) {
						StatementPathElement propertyPathElement = null;
						if (property instanceof OntologyObjectProperty) {
							propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
									(OntologyObjectProperty) property, true, levels - 1);
						} else if (property instanceof OntologyDataProperty) {
							propertyPathElement = new DataPropertyStatementPathElement(this, (OntologyDataProperty) property);
						}
						if (propertyPathElement != null) {
							allProperties.add(propertyPathElement);
						}
					}
				}
			}*/

		}

		@Override
		public List<BindingPathElement> getAllProperties() {
			if (!propertiesFound && getOntologyProperty().getRange() instanceof OntologyClass) {
				searchProperties((OntologyClass) getOntologyProperty().getRange());
			}
			return allProperties;
		}

		boolean propertiesFound = false;

		private void searchProperties(OntologyClass rangeClass) {

			for (final OntologyProperty property : rangeClass.getPropertiesTakingMySelfAsDomain()) {
				StatementPathElement propertyPathElement = null;
				if (property instanceof OntologyObjectProperty) {
					propertyPathElement = ObjectPropertyStatementPathElement.makeObjectPropertyStatementPathElement(this,
							(OntologyObjectProperty) property, true, OntologyObjectPathElement.MAX_LEVELS);
				} else if (property instanceof OntologyDataProperty) {
					propertyPathElement = new DataPropertyStatementPathElement(this, (OntologyDataProperty) property);
				}
				if (propertyPathElement != null) {
					allProperties.add(propertyPathElement);
				}
			}
			propertiesFound = true;
		}

		public BindingPathElement getUriNameProperty() {
			return uriNameProperty;
		}

		public BindingPathElement getUriProperty() {
			return uriProperty;
		}

		@Override
		public Type getType() {
			if (getOntologyProperty().getRange() instanceof OntologyClass) {
				return IndividualOfClass.getIndividualOfClass((OntologyClass) getOntologyProperty().getRange());
			}
			return OntologyIndividual.class;
		}

		@Override
		public OntologyObject getBindingValue(Object target, BindingEvaluationContext context) {
			if (target instanceof OntologyIndividual) {
				OntologyIndividual individual = (OntologyIndividual) target;
				PropertyStatement statement = individual.getPropertyStatement(getOntologyProperty());
				if (statement == null) {
					return null;
				}
				if (statement instanceof ObjectPropertyStatement) {
					return ((ObjectPropertyStatement) statement).getStatementObject();
				} else {
					logger.warning("Unexpected statement " + statement + " while evaluateBinding()");
					return null;
				}
			} else {
				logger.warning("Unexpected target " + target + " while evaluateBinding()");
				return null;
			}
		}

		@Override
		public void setBindingValue(OntologyObject value, Object target, BindingEvaluationContext context) {
			if (target instanceof OntologyIndividual) {
				OntologyIndividual individual = (OntologyIndividual) target;
				PropertyStatement statement = individual.getPropertyStatement(getOntologyProperty());
				if (statement == null) {
					individual.getOntResource().addProperty(getOntologyProperty().getOntProperty(), value.getOntResource());
					individual.updateOntologyStatements();
					individual.getPropertyChangeSupport().firePropertyChange(getOntologyProperty().getName(), null, value);
				} else if (statement instanceof ObjectPropertyStatement) {
					Object oldValue = ((ObjectPropertyStatement) statement).getStatementObject();
					((ObjectPropertyStatement) statement).setStatementObject(value);
					individual.getPropertyChangeSupport().firePropertyChange(getOntologyProperty().getName(), oldValue, value);
				} else {
					logger.warning("Unexpected statement " + statement + " while evaluating setBindingValue()");
				}
			} else {
				logger.warning("Unexpected target " + target + " while evaluating setBindingValue()");
			}
		}
	}

	public static class ObjectPropertyStatementAccessingLiteralPathElement extends ObjectPropertyStatementPathElement<Object> {

		private SimpleBindingPathElementImpl<String> asStringProperty;
		private SimpleBindingPathElementImpl<Boolean> asBooleanProperty;
		private SimpleBindingPathElementImpl<Integer> asIntegerProperty;
		private SimpleBindingPathElementImpl<Byte> asByteProperty;
		private SimpleBindingPathElementImpl<Short> asShortProperty;
		private SimpleBindingPathElementImpl<Long> asLongProperty;
		private SimpleBindingPathElementImpl<Character> asCharProperty;
		private SimpleBindingPathElementImpl<Float> asFloatProperty;
		private SimpleBindingPathElementImpl<Double> asDoubleProperty;

		public ObjectPropertyStatementAccessingLiteralPathElement(BindingPathElement aParent, OntologyObjectProperty anOntologyProperty) {
			super(aParent, anOntologyProperty);

			asStringProperty = new SimpleBindingPathElementImpl<String>("asString", Object.class, String.class, true,
					"string_value_for_literal") {
				@Override
				public String getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getString();
					} else if (target instanceof String) {
						return (String) target;
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asStringProperty);
			asBooleanProperty = new SimpleBindingPathElementImpl<Boolean>("asBoolean", Object.class, Boolean.class, true,
					"boolean_value_for_literal") {
				@Override
				public Boolean getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getBoolean();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Boolean value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asBooleanProperty);
			asIntegerProperty = new SimpleBindingPathElementImpl<Integer>("asInteger", Object.class, Integer.class, true,
					"int_value_for_literal") {
				@Override
				public Integer getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getInt();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Integer value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asIntegerProperty);
			asByteProperty = new SimpleBindingPathElementImpl<Byte>("asByte", Object.class, Byte.class, true, "byte_value_for_literal") {
				@Override
				public Byte getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getByte();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Byte value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asByteProperty);
			asShortProperty = new SimpleBindingPathElementImpl<Short>("asShort", Object.class, Short.class, true, "short_value_for_literal") {
				@Override
				public Short getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getShort();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Short value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asShortProperty);
			asLongProperty = new SimpleBindingPathElementImpl<Long>("asLong", Object.class, Long.class, true, "long_value_for_literal") {
				@Override
				public Long getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getLong();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Long value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asLongProperty);
			asCharProperty = new SimpleBindingPathElementImpl<Character>("asChar", Object.class, Character.class, true,
					"char_value_for_literal") {
				@Override
				public Character getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getChar();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Character value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asCharProperty);
			asFloatProperty = new SimpleBindingPathElementImpl<Float>("asFloat", Object.class, Float.class, true, "float_value_for_literal") {
				@Override
				public Float getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getFloat();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Float value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asFloatProperty);
			asDoubleProperty = new SimpleBindingPathElementImpl<Double>("asDouble", Object.class, Double.class, true,
					"string_value_for_literal") {
				@Override
				public Double getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof Literal) {
						return ((Literal) target).getDouble();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Double value, Object target, BindingEvaluationContext context) {
					logger.warning("not implemented");
				}
			};
			allProperties.add(asDoubleProperty);
		}

		@Override
		public Type getType() {
			return Object.class;
		}

		@Override
		public Object getBindingValue(Object target, BindingEvaluationContext context) {
			if (target instanceof OntologyObject<?>) {
				OntologyObject<?> object = (OntologyObject<?>) target;
				return object.getPropertyValue(getOntologyProperty());
			} else {
				logger.warning("Unexpected target " + target + " while evaluateBinding()");
				return null;
			}
		}

		@Override
		public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
			logger.warning("Implement me");
			// individual.getPropertyChangeSupport().firePropertyChange(ontologyProperty.getName(), oldValue, value);
		}
	}
}