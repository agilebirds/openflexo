package org.openflexo.model.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.ReturnedValue;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.xml.InvalidDataException;

public class ModelProperty<I> {

	private ModelEntity<I> modelEntity;
	private Class<?> type;
	private String propertyIdentifier;

	private Getter getter;
	private Setter setter;
	private Adder adder;
	private Remover remover;
	private XMLAttribute xmlAttribute;
	private XMLElement xmlElement;
	private String xmlTag;
	private ReturnedValue returnedValue;
	private Embedded embedded;
	private CloningStrategy cloningStrategy;

	private Method getterMethod;
	private Method setterMethod;
	private Method adderMethod;
	private Method removerMethod;

	private ModelProperty<? super I> superProperty = null;

	protected ModelProperty(String propertyIdentifier, ModelEntity<I> modelEntity) throws ModelDefinitionException {
		// System.out.println("CREATED ModelProperty for "+propertyIdentifier);

		this.modelEntity = modelEntity;
		this.propertyIdentifier = propertyIdentifier;

		for (Method m : modelEntity.getImplementedInterface().getDeclaredMethods()) {
			Getter aGetter = m.getAnnotation(Getter.class);
			Setter aSetter = m.getAnnotation(Setter.class);
			Adder anAdder = m.getAnnotation(Adder.class);
			Remover aRemover = m.getAnnotation(Remover.class);
			if (aGetter != null && aGetter.value().equals(propertyIdentifier)) {
				if (getter != null) {
					throw new ModelDefinitionException("Duplicate getter " + propertyIdentifier + " defined for interface "
							+ modelEntity.getImplementedInterface());
				} else {
					getter = aGetter;
					getterMethod = m;
					switch (getter.cardinality()) {
					case SINGLE:
						type = getterMethod.getReturnType();
						break;
					case LIST:
						type = (Class<?>) ((ParameterizedType) getterMethod.getGenericReturnType()).getActualTypeArguments()[0];
					default:
						break;
					}
					if (type.isPrimitive() && getter.defaultValue().equals(Getter.UNDEFINED)) {
						throw new ModelDefinitionException("No default value defined for primitive property " + this);
					}
					if (!Getter.UNDEFINED.equals(getter.defaultValue())) {
						try {
							defaultValue = getModelFactory().getStringEncoder().fromString(getType(), getter.defaultValue());
						} catch (InvalidDataException e) {
							throw new ModelDefinitionException("Invalid default value for property " + this + " : " + e.getMessage());
						}
					}

				}
			}
			if (aSetter != null && aSetter.value().equals(propertyIdentifier)) {
				if (setter != null) {
					throw new ModelDefinitionException("Duplicate setter " + propertyIdentifier + " defined for interface "
							+ modelEntity.getImplementedInterface());
				} else {
					setter = aSetter;
					setterMethod = m;
				}
			}
			if (anAdder != null && anAdder.id().equals(propertyIdentifier)) {
				if (adder != null) {
					throw new ModelDefinitionException("Duplicate adder " + propertyIdentifier + " defined for interface "
							+ modelEntity.getImplementedInterface());
				} else {
					adder = anAdder;
					adderMethod = m;
				}
			}
			if (aRemover != null && aRemover.id().equals(propertyIdentifier)) {
				if (remover != null) {
					throw new ModelDefinitionException("Duplicate remover " + propertyIdentifier + " defined for interface "
							+ modelEntity.getImplementedInterface());
				} else {
					remover = aRemover;
					removerMethod = m;
				}
			}
		}

		ModelEntity<? super I> superEntity = getModelEntity().getSuperEntity();
		while (superEntity != null) {
			superProperty = superEntity.getDeclaredModelProperty(propertyIdentifier);
			if (superProperty != null) {
				break;
			}
			superEntity = superEntity.getSuperEntity();
		}
		// if (getSuperProperty() != null) System.out.println("Found super property "+superProperty+" for "+this);

		if (getGetter() == null) {
			throw new ModelDefinitionException("No getter defined for " + propertyIdentifier + ", interface "
					+ modelEntity.getImplementedInterface());
		}

		xmlAttribute = retrieveAnnotation(XMLAttribute.class);
		xmlElement = retrieveAnnotation(XMLElement.class);
		returnedValue = retrieveAnnotation(ReturnedValue.class);
		embedded = retrieveAnnotation(Embedded.class);
		cloningStrategy = retrieveAnnotation(CloningStrategy.class);

		switch (getCardinality()) {
		case LIST:
		case MAP:
			if (getAdder() == null) {
				throw new ModelDefinitionException("No adder defined for " + propertyIdentifier + ", interface "
						+ modelEntity.getImplementedInterface());
			}
			if (getRemover() == null) {
				throw new ModelDefinitionException("No remover defined for " + propertyIdentifier + ", interface "
						+ modelEntity.getImplementedInterface());
			}
			break;
		default:
			break;
		}
	}

	public <A extends Annotation> A retrieveAnnotation(Class<A> annotationClass) {
		A returned = null;
		ModelProperty<? super I> current = this;
		while (current != null) {
			if (current.getterMethod != null) {
				returned = current.getterMethod.getAnnotation(annotationClass);
				if (returned != null) {
					return returned;
				}
			}
			current = current.getSuperProperty();
		}
		return null;
	}

	public ModelFactory getModelFactory() {
		return getModelEntity().getModelFactory();
	}

	public ModelEntity<I> getModelEntity() {
		return modelEntity;
	}

	public Class<I> getImplementedInterface() {
		return getModelEntity().getImplementedInterface();
	}

	public Class<?> getType() {
		if (type == null && override()) {
			return getSuperProperty().getType();
		}
		return type;
	}

	public String getPropertyIdentifier() {
		return propertyIdentifier;
	}

	public Getter getGetter() {
		if (getter == null && override()) {
			return getSuperProperty().getGetter();
		}
		return getter;
	}

	public Setter getSetter() {
		if (setter == null && override()) {
			return getSuperProperty().getSetter();
		}
		return setter;
	}

	public Adder getAdder() {
		if (adder == null && override()) {
			return getSuperProperty().getAdder();
		}
		return adder;
	}

	public Remover getRemover() {
		if (remover == null && override()) {
			return getSuperProperty().getRemover();
		}
		return remover;
	}

	public XMLAttribute getXMLAttribute() {
		return xmlAttribute;
	}

	public XMLElement getXMLElement() {
		return xmlElement;
	}

	public String getXMLTag() {
		if (xmlTag == null) {
			xmlTag = xmlAttribute.xmlTag();
			if (xmlTag.equals(XMLAttribute.DEFAULT_XML_TAG)) {
				xmlTag = getGetter().value();
			}
		}
		return xmlTag;
	}

	public Method getGetterMethod() {
		if (getterMethod == null && override()) {
			return getSuperProperty().getGetterMethod();
		}
		return getterMethod;
	}

	public Method getSetterMethod() {
		if (setterMethod == null && override()) {
			return getSuperProperty().getSetterMethod();
		}
		return setterMethod;
	}

	public Method getAdderMethod() {
		if (adderMethod == null && override()) {
			return getSuperProperty().getAdderMethod();
		}
		return adderMethod;
	}

	public Method getRemoverMethod() {
		if (removerMethod == null && override()) {
			return getSuperProperty().getRemoverMethod();
		}
		return removerMethod;
	}

	private Object defaultValue = null;

	public Object getDefaultValue() {
		if (defaultValue == null && override()) {
			return getSuperProperty().getDefaultValue();
		}
		return defaultValue;
	}

	public Cardinality getCardinality() {
		if (cardinality == null) {
			cardinality = getGetter().cardinality();
		}
		return cardinality;
	}

	private ModelProperty<?> inverseProperty = null;
	private Cardinality cardinality;

	// TODO: optimize this (for the case of inverse is incorrectly defined)
	public ModelProperty getInverseProperty() {
		if (inverseProperty == null) {
			if (!getGetter().inverse().equals(Getter.UNDEFINED)) {
				try {
					inverseProperty = findInverseProperty();
				} catch (ModelDefinitionException e) {
					e.printStackTrace();
				}
			} else if (override()) {
				return superProperty.getInverseProperty();
			}
		}
		return inverseProperty;
	}

	private ModelProperty<?> findInverseProperty() throws ModelDefinitionException {
		ModelProperty<?> returned = null;
		if (!getGetter().inverse().equals(Getter.UNDEFINED)) {
			ModelEntity<?> oppositeEntity = getModelFactory().getModelEntity(getType());
			if (oppositeEntity == null) {
				throw new ModelDefinitionException(getModelEntity() + ": Cannot find opposite entity " + getType());
			}
			returned = oppositeEntity.getModelProperty(getGetter().inverse());
			if (returned == null) {
				throw new ModelDefinitionException(getModelEntity() + ": Cannot find inverse property " + getGetter().inverse() + " for "
						+ oppositeEntity.getImplementedInterface().getSimpleName());
			}
		}
		return returned;
	}

	@Override
	public String toString() {
		return "ModelProperty[" + getModelEntity().getImplementedInterface().getSimpleName() + "." + getPropertyIdentifier() + "]";
	}

	public ModelEntity<?> getAccessedEntity() throws ModelDefinitionException {
		return getModelFactory().getModelEntity(getType());
	}

	public ModelProperty<? super I> getSuperProperty() {
		return superProperty;
	}

	public boolean override() {
		return superProperty != null;
	}

	public ReturnedValue getReturnedValue() {
		return returnedValue;
	}

	public Embedded getEmbedded() {
		return embedded;
	}

	public StrategyType getCloningStrategy() {
		if (cloningStrategy == null) {
			if (getModelFactory().isModelEntity(getType())) {
				return StrategyType.REFERENCE;
			} else {
				return StrategyType.CLONE;
			}
		} else {
			return cloningStrategy.value();
		}
	}

	public String getStrategyTypeFactory() {
		return cloningStrategy.factory();
	}
}
