package org.openflexo.technologyadapter.owl.viewpoint;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimpleBindingPathElementImpl;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.PropertyStatement;

public class DataPropertyStatementPathElement extends StatementPathElement<Object> {
	private static final Logger logger = Logger.getLogger(DataPropertyStatementPathElement.class.getPackage().getName());

	private OntologyDataProperty ontologyProperty;

	private SimpleBindingPathElementImpl<String> asStringProperty;
	private SimpleBindingPathElementImpl<Boolean> asBooleanProperty;
	private SimpleBindingPathElementImpl<Integer> asIntegerProperty;
	private SimpleBindingPathElementImpl<Byte> asByteProperty;
	private SimpleBindingPathElementImpl<Short> asShortProperty;
	private SimpleBindingPathElementImpl<Long> asLongProperty;
	private SimpleBindingPathElementImpl<Character> asCharProperty;
	private SimpleBindingPathElementImpl<Float> asFloatProperty;
	private SimpleBindingPathElementImpl<Double> asDoubleProperty;

	public DataPropertyStatementPathElement(BindingPathElement aParent, OntologyDataProperty anOntologyProperty) {
		super(aParent);
		ontologyProperty = anOntologyProperty;

		if (anOntologyProperty.getDataType() == null) {
			asStringProperty = new SimpleBindingPathElementImpl<String>(PropertyStatement.AS_STRING, Object.class, String.class, true,
					"string_value_for_literal") {
				@Override
				public String getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getStringValue();
					} else if (target instanceof String) {
						return (String) target;
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						System.out.println("sets value [" + value + "] for " + ((PropertyStatement) target).getProperty());
						((PropertyStatement) target).setStringValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}

			};
			allProperties.add(asStringProperty);
			asBooleanProperty = new SimpleBindingPathElementImpl<Boolean>(PropertyStatement.AS_BOOLEAN, Object.class, Boolean.class, true,
					"boolean_value_for_literal") {
				@Override
				public Boolean getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getBooleanValue();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Boolean value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						((PropertyStatement) target).setBooleanValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(asBooleanProperty);
			asIntegerProperty = new SimpleBindingPathElementImpl<Integer>(PropertyStatement.AS_INTEGER, Object.class, Integer.class, true,
					"int_value_for_literal") {
				@Override
				public Integer getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getIntegerValue();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Integer value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						((PropertyStatement) target).setIntegerValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(asIntegerProperty);
			asByteProperty = new SimpleBindingPathElementImpl<Byte>(PropertyStatement.AS_BYTE, Object.class, Byte.class, true,
					"byte_value_for_literal") {
				@Override
				public Byte getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getByteValue();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Byte value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						((PropertyStatement) target).setByteValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(asByteProperty);
			asShortProperty = new SimpleBindingPathElementImpl<Short>(PropertyStatement.AS_SHORT, Object.class, Short.class, true,
					"short_value_for_literal") {
				@Override
				public Short getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getShortValue();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Short value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						((PropertyStatement) target).setShortValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(asShortProperty);
			asLongProperty = new SimpleBindingPathElementImpl<Long>(PropertyStatement.AS_LONG, Object.class, Long.class, true,
					"long_value_for_literal") {
				@Override
				public Long getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getLongValue();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Long value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						((PropertyStatement) target).setLongValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(asLongProperty);
			asCharProperty = new SimpleBindingPathElementImpl<Character>(PropertyStatement.AS_CHARACTER, Object.class, Character.class,
					true, "char_value_for_literal") {
				@Override
				public Character getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getCharacterValue();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Character value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						((PropertyStatement) target).setCharacterValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(asCharProperty);
			asFloatProperty = new SimpleBindingPathElementImpl<Float>(PropertyStatement.AS_FLOAT, Object.class, Float.class, true,
					"float_value_for_literal") {
				@Override
				public Float getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getFloatValue();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Float value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						((PropertyStatement) target).setFloatValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(asFloatProperty);
			asDoubleProperty = new SimpleBindingPathElementImpl<Double>(PropertyStatement.AS_DOUBLE, Object.class, Double.class, true,
					"string_value_for_literal") {
				@Override
				public Double getBindingValue(Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						return ((PropertyStatement) target).getDoubleValue();
					} else {
						logger.warning("Unexpected: " + target);
						return null;
					}
				}

				@Override
				public void setBindingValue(Double value, Object target, BindingEvaluationContext context) {
					if (target instanceof PropertyStatement) {
						((PropertyStatement) target).setDoubleValue(value);
					} else {
						logger.warning("Unexpected: " + target);
					}
				}
			};
			allProperties.add(asDoubleProperty);
		}

	}

	@Override
	public List<BindingPathElement> getAllProperties() {
		return allProperties;
	}

	@Override
	public Type getType() {
		if (ontologyProperty != null && ontologyProperty.getDataType() != null) {
			return ontologyProperty.getDataType().getAccessedType();
		}
		return Object.class;
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

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) {
		if (target instanceof OWLConcept<?>) {
			OWLConcept<?> object = (OWLConcept<?>) target;
			if (ontologyProperty != null && (ontologyProperty.isAnnotationProperty() || ontologyProperty.getDataType() != null)) {
				return object.getPropertyValue(ontologyProperty);
			} else {
				return object.getPropertyStatement(ontologyProperty);
			}
		} else {
			logger.warning("Unexpected target " + target + " while evaluateBinding()");
			return null;
		}
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
		logger.warning("Attempt to process setBindingValue with " + value);
		if (target instanceof OWLIndividual) {
			Object oldValue = null;
			OWLIndividual individual = (OWLIndividual) target;
			logger.info("individual=" + individual);
			logger.info("ontologyProperty=" + ontologyProperty);
			logger.info("individual.getPropertyStatement(ontologyProperty)=" + individual.getPropertyStatement(ontologyProperty));
			PropertyStatement statement = individual.getPropertyStatement(ontologyProperty);
			if (statement == null) {
				individual.addDataPropertyStatement(ontologyProperty, value);
			}
			if (statement instanceof DataPropertyStatement) {
				oldValue = ((DataPropertyStatement) statement).getValue();
				((DataPropertyStatement) statement).setValue(value);
			} else {
				logger.warning("Unexpected statement " + statement + " while evaluateBinding()");
				logger.info("WAS individual=" + individual);
				logger.info("WAS ontologyProperty=" + ontologyProperty);
				logger.info("WAS individual.getPropertyStatement(ontologyProperty)=" + individual.getPropertyStatement(ontologyProperty));
			}
			individual.getPropertyChangeSupport().firePropertyChange(ontologyProperty.getName(), oldValue, value);
		} else {
			logger.warning("Unexpected target " + target + " while evaluateBinding()");
		}
	}

}
