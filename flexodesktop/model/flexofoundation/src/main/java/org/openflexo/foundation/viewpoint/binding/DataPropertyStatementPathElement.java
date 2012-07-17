package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimpleBindingPathElementImpl;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.owl.DataPropertyStatement;
import org.openflexo.foundation.ontology.owl.OWLIndividual;
import org.openflexo.foundation.ontology.owl.PropertyStatement;

import com.hp.hpl.jena.rdf.model.Literal;

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
		if (target instanceof OntologyObject) {
			OntologyObject object = (OntologyObject) target;
			return object.getPropertyValue(ontologyProperty);
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