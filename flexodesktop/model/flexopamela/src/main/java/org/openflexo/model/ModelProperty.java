package org.openflexo.model;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import org.openflexo.antar.binding.ReflectionUtils;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.ClosureCondition;
import org.openflexo.model.annotations.ComplexEmbedded;
import org.openflexo.model.annotations.DeletionCondition;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.ReturnedValue;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.factory.PAMELAConstants;

public class ModelProperty<I> {

	/* Model property identification */
	private ModelEntity<I> modelEntity;
	private String propertyIdentifier;

	/* Model property static definition */
	private Getter getter;
	private Setter setter;
	private Adder adder;
	private Remover remover;
	private XMLAttribute xmlAttribute;
	private XMLElement xmlElement;
	private ReturnedValue returnedValue;
	private Embedded embedded;
	private ComplexEmbedded complexEmbedded;
	private CloningStrategy cloningStrategy;

	private PastingPoint setPastingPoint;
	private PastingPoint addPastingPoint;

	private Method getterMethod;
	private Method setterMethod;
	private Method adderMethod;
	private Method removerMethod;

	private Cardinality cardinality;

	/* Computed values of the model property */
	private Class<?> type;
	private Class<?> keyType;
	private String xmlTag;

	protected static <I> ModelProperty<I> getModelProperty(String propertyIdentifier, ModelEntity<I> modelEntity)
			throws ModelDefinitionException {
		Getter getter = null;
		Setter setter = null;
		Adder adder = null;
		Remover remover = null;
		XMLAttribute xmlAttribute = null;
		XMLElement xmlElement = null;
		ReturnedValue returnedValue = null;
		Embedded embedded = null;
		ComplexEmbedded complexEmbedded = null;
		CloningStrategy cloningStrategy = null;
		PastingPoint setPastingPoint = null;
		PastingPoint addPastingPoint = null;
		Method getterMethod = null;
		Method setterMethod = null;
		Method adderMethod = null;
		Method removerMethod = null;
		Class<I> implementedInterface = modelEntity.getImplementedInterface();
		for (Method m : implementedInterface.getDeclaredMethods()) {
			Getter aGetter = m.getAnnotation(Getter.class);
			Setter aSetter = m.getAnnotation(Setter.class);
			Adder anAdder = m.getAnnotation(Adder.class);
			Remover aRemover = m.getAnnotation(Remover.class);
			if (aGetter == null && aSetter == null && anAdder == null && aRemover == null) {
				for (Method m1 : ReflectionUtils.getOverridenMethods(m)) {
					aGetter = m1.getAnnotation(Getter.class);
					aSetter = m1.getAnnotation(Setter.class);
					anAdder = m1.getAnnotation(Adder.class);
					aRemover = m1.getAnnotation(Remover.class);
					if (aGetter != null || aSetter != null || anAdder != null || aRemover != null) {
						break;
					}
				}
			}
			if (aGetter != null && aGetter.value().equals(propertyIdentifier)) {
				if (getter != null) {
					throw new ModelDefinitionException("Duplicate getter '" + propertyIdentifier + "' defined for interface "
							+ implementedInterface);
				} else {
					getter = aGetter;
					getterMethod = m;
					xmlAttribute = m.getAnnotation(XMLAttribute.class);
					xmlElement = m.getAnnotation(XMLElement.class);
					returnedValue = m.getAnnotation(ReturnedValue.class);
					cloningStrategy = m.getAnnotation(CloningStrategy.class);
					embedded = m.getAnnotation(Embedded.class);
					complexEmbedded = m.getAnnotation(ComplexEmbedded.class);
				}
			}
			if (aSetter != null && aSetter.value().equals(propertyIdentifier)) {
				if (setter != null) {
					throw new ModelDefinitionException("Duplicate setter '" + propertyIdentifier + "' defined for interface "
							+ implementedInterface);
				} else {
					setter = aSetter;
					setterMethod = m;
					setPastingPoint = m.getAnnotation(PastingPoint.class);
				}
			}
			if (anAdder != null && anAdder.value().equals(propertyIdentifier)) {
				if (adder != null) {
					throw new ModelDefinitionException("Duplicate adder '" + propertyIdentifier + "' defined for interface "
							+ implementedInterface);
				} else {
					adder = anAdder;
					adderMethod = m;
					addPastingPoint = m.getAnnotation(PastingPoint.class);
				}
			}
			if (aRemover != null && aRemover.value().equals(propertyIdentifier)) {
				if (remover != null) {
					throw new ModelDefinitionException("Duplicate remover '" + propertyIdentifier + "' defined for interface "
							+ implementedInterface);
				} else {
					remover = aRemover;
					removerMethod = m;
				}
			}
		}
		return new ModelProperty<I>(modelEntity, propertyIdentifier, getter, setter, adder, remover, xmlAttribute, xmlElement,
				returnedValue, embedded, complexEmbedded, cloningStrategy, setPastingPoint, addPastingPoint, getterMethod, setterMethod,
				adderMethod, removerMethod);
	}

	protected ModelProperty(ModelEntity<I> modelEntity, String propertyIdentifier, Getter getter, Setter setter, Adder adder,
			Remover remover, XMLAttribute xmlAttribute, XMLElement xmlElement, ReturnedValue returnedValue, Embedded embedded,
			ComplexEmbedded complexEmbedded, CloningStrategy cloningStrategy, PastingPoint setPastingPoint, PastingPoint addPastingPoint,
			Method getterMethod, Method setterMethod, Method adderMethod, Method removerMethod) throws ModelDefinitionException {
		this.modelEntity = modelEntity;
		this.propertyIdentifier = propertyIdentifier;
		this.getter = getter;
		this.setter = setter;
		this.adder = adder;
		this.remover = remover;
		this.xmlAttribute = xmlAttribute;
		this.xmlElement = xmlElement;
		this.returnedValue = returnedValue;
		this.embedded = embedded;
		this.complexEmbedded = complexEmbedded;
		this.cloningStrategy = cloningStrategy;
		this.setPastingPoint = setPastingPoint;
		this.addPastingPoint = addPastingPoint;
		this.getterMethod = getterMethod;
		this.setterMethod = setterMethod;
		this.adderMethod = adderMethod;
		this.removerMethod = removerMethod;
		if (setterMethod != null) {
			PastingPoint pastingPoint = setterMethod.getAnnotation(PastingPoint.class);
			if (pastingPoint != null) {
				setPastingPoint = pastingPoint;
			}
		}
		if (adderMethod != null) {
			PastingPoint pastingPoint = adderMethod.getAnnotation(PastingPoint.class);
			if (pastingPoint != null) {
				addPastingPoint = pastingPoint;
			}
		}

		if (getter != null) {
			switch (getCardinality()) {
			case SINGLE:
				type = getterMethod.getReturnType();
				break;
			case LIST:
				type = TypeUtils.getBaseClass(((ParameterizedType) getterMethod.getGenericReturnType()).getActualTypeArguments()[0]);
				break;
			case MAP:
				keyType = TypeUtils.getBaseClass(((ParameterizedType) getterMethod.getGenericReturnType()).getActualTypeArguments()[0]);
				type = TypeUtils.getBaseClass(((ParameterizedType) getterMethod.getGenericReturnType()).getActualTypeArguments()[1]);
				break;
			default:
				break;
			}
		}
	}

	public void validate() throws ModelDefinitionException {
		if (propertyIdentifier == null || propertyIdentifier.equals("")) {
			throw new ModelDefinitionException("No property identifier defined!");
		}
		if (getXMLAttribute() != null && getXMLAttribute().xmlTag().equals(PAMELAConstants.CLASS_ATTRIBUTE)
				&& getXMLAttribute().namespace().equals(PAMELAConstants.NS)) {
			throw new ModelDefinitionException("Invalid property identifier '" + PAMELAConstants.CLASS_ATTRIBUTE + "' with namespace "
					+ PAMELAConstants.NS + "!");
		}
		if (getGetter() == null) {
			throw new ModelDefinitionException("No getter defined for " + propertyIdentifier + ", interface "
					+ modelEntity.getImplementedInterface());
		}
		if (type.isPrimitive() && getter.defaultValue().equals(Getter.UNDEFINED)) {
			throw new ModelDefinitionException("No default value defined for primitive property " + this);
		}
		if (!Getter.UNDEFINED.equals(getter.defaultValue())) {
			if (!getType().isEnum()) {
				if (getType().isPrimitive()) {
					Converter<?> converter = StringConverterLibrary.getInstance().getConverter(getType());
					if (converter == null) {
						throw new ModelDefinitionException("No converter for type '" + getType() + "'. Cannot convert default value "
								+ getter.defaultValue());
					} else {
						try {
							defaultValue = converter.convertFromString(getter.defaultValue(), null);
						} catch (InvalidDataException e) {
							e.printStackTrace();
							throw new ModelDefinitionException("String value '" + getter.defaultValue() + "' cannot be converted to a "
									+ getType().getName(), e);
						}
					}
				}
			}
		}

		if (isSerializable() && ignoreType()) {
			throw new ModelDefinitionException("Inconsistent property '" + propertyIdentifier + " for " + getModelEntity()
					+ "'. It cannot be serializable (annotation XMLAttribute or XMLElement) and ignored. "
					+ "If it is string convertable, mark it with the attribute 'stringConvertable'.");
		}

		if (embedded != null && complexEmbedded != null) {
			throw new ModelDefinitionException("Cannot define both " + Embedded.class.getSimpleName() + " and "
					+ ComplexEmbedded.class.getSimpleName() + " on property " + this);
		}

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

		if (getGetterMethod() != null && getGetterMethod().getParameterTypes().length > 0) {
			throw new ModelDefinitionException("Invalid getter method for property '" + propertyIdentifier + "': method "
					+ getGetterMethod().toString() + " must be without parameters");
		}

		if (getSetterMethod() != null) {
			if (getSetterMethod().getParameterTypes().length != 1) {
				throw new ModelDefinitionException("Invalid setter method for property '" + propertyIdentifier + "': method "
						+ getSetterMethod().toString() + " must have exactly 1 parameter");
			}

			if (!TypeUtils.isTypeAssignableFrom(getGetterMethod().getReturnType(), getSetterMethod().getParameterTypes()[0])
					&& !TypeUtils.isTypeAssignableFrom(getSetterMethod().getParameterTypes()[0], getGetterMethod().getReturnType())) {
				throw new ModelDefinitionException("Invalid setter method for property '" + propertyIdentifier + "': method "
						+ getSetterMethod().toString() + " parameter must be assignable from or to "
						+ getGetterMethod().getReturnType().getName());
			}
		}

		if (getAdderMethod() != null) {
			switch (getCardinality()) {
			case LIST:
				if (getAdderMethod().getParameterTypes().length != 1) {
					throw new ModelDefinitionException("Invalid adder method for property '" + propertyIdentifier + "': method "
							+ getAdderMethod().toString() + " must have exactly 1 parameter");
				}
				if (!TypeUtils.isTypeAssignableFrom(type, getAdderMethod().getParameterTypes()[0])) {
					throw new ModelDefinitionException("Invalid adder method for property '" + propertyIdentifier + "': method "
							+ getAdderMethod().toString() + " parameter must be assignable to " + type.getName());
				}
				break;
			case MAP:
				if (getAdderMethod().getParameterTypes().length != 2) {
					throw new ModelDefinitionException("Invalid adder method for property '" + propertyIdentifier + "': method "
							+ getAdderMethod().toString() + " must have exactly 2 parameters");
				}
				if (!TypeUtils.isTypeAssignableFrom(keyType, getAdderMethod().getParameterTypes()[0])) {
					throw new ModelDefinitionException("Invalid adder method for property '" + propertyIdentifier + "': method "
							+ getAdderMethod().toString() + " first parameter must be assignable to " + keyType.getName());
				}
				if (!TypeUtils.isTypeAssignableFrom(type, getAdderMethod().getParameterTypes()[1])) {
					throw new ModelDefinitionException("Invalid adder method for property '" + propertyIdentifier + "': method "
							+ getAdderMethod().toString() + " second parameter must be assignable to " + type.getName());
				}
				break;
			default:
				break;
			}
		}

		if (getRemoverMethod() != null) {
			switch (getCardinality()) {
			case LIST:
				if (getRemoverMethod().getParameterTypes().length != 1) {
					throw new ModelDefinitionException("Invalid remover method for property '" + propertyIdentifier + "': method "
							+ getRemoverMethod().toString() + " must have exactly 1 parameter");
				}
				if (!TypeUtils.isTypeAssignableFrom(type, getRemoverMethod().getParameterTypes()[0])) {
					throw new ModelDefinitionException("Invalid remover method for property '" + propertyIdentifier + "': method "
							+ getRemoverMethod().toString() + " parameter must be assignable to " + type.getName());
				}
				break;
			case MAP:
				if (getRemoverMethod().getParameterTypes().length != 1) {
					throw new ModelDefinitionException("Invalid remover method for property '" + propertyIdentifier + "': method "
							+ getRemoverMethod().toString() + " must have exactly 1 parameter");
				}
				if (!TypeUtils.isTypeAssignableFrom(keyType, getRemoverMethod().getParameterTypes()[0])) {
					throw new ModelDefinitionException("Invalid remover method for property '" + propertyIdentifier + "': method "
							+ getRemoverMethod().toString() + " first parameter must be assignable to " + keyType.getName());
				}

				break;
			default:
				break;
			}
		}
	}

	/**
	 * This method checks that <code>this</code> property does not contradicts <code>property</code>. In case it does, the method verifies
	 * that the optional <code>rulingProperty</code> can rule out the contradiction. If there is a contradiction, then the reason is
	 * returned, else it returns <code>null</code>.
	 * 
	 * @param property
	 *            the property against which to check for contradictions
	 * @param rulingProperty
	 *            the property that needs to rule out any contradiction. Can be <code>null</code> or invalid.
	 * @return the reason of the contradiction, <code>null</code> in case there are no contradictions.
	 */
	public String contradicts(ModelProperty<?> property, ModelProperty<?> rulingProperty) {
		// Model options
		if (!propertyIdentifier.equals(property.getPropertyIdentifier())) {
			return "Property identifier '" + propertyIdentifier + "' is not equal to '" + property.getPropertyIdentifier() + "'";
		}
		if (!getType().equals(property.getType()) && !getType().isAssignableFrom(property.getType())
				&& !property.getType().isAssignableFrom(getType())) {
			// Types are incompatible and this will therefore never work.
			return "Incompatible return type: " + getType().getName() + " is not compatible with " + property.getType().getName();
		}
		if (getCardinality() != property.getCardinality()) {
			if (rulingProperty == null || rulingProperty.getCardinality() == null) {
				return "Cardinality " + getCardinality() + " is not equal to " + property.getCardinality();
			}
		}
		if (hasInverseProperty() && !property.getGetter().inverse().equals(Getter.UNDEFINED)
				&& !getGetter().inverse().equals(property.getGetter().inverse())) {
			if (rulingProperty == null || rulingProperty.getGetter() == null
					|| rulingProperty.getGetter().inverse().equals(Getter.UNDEFINED)) {
				return "Inverse property '" + getGetter().inverse() + "' is not equal to '" + property.getGetter().inverse() + "'";
			}
		}
		if (!getGetter().defaultValue().equals(Getter.UNDEFINED) && !property.getGetter().defaultValue().equals(Getter.UNDEFINED)
				&& !getGetter().defaultValue().equals(property.getGetter().defaultValue())) {
			if (rulingProperty == null || rulingProperty.getGetter() == null
					|| rulingProperty.getGetter().defaultValue().equals(Getter.UNDEFINED)) {
				return "Default value '" + getGetter().defaultValue() + "' is not equal to '" + property.getGetter().defaultValue() + "'";
			}
		}

		// TODO: Fix incompatible get/set etc...
		if (!getGetterMethod().getName().equals(property.getGetterMethod().getName())) {
			if (rulingProperty == null || rulingProperty.getGetterMethod() == null) {
				return "Incompatible getter method " + getGetterMethod().getName() + " " + property.getGetterMethod().getName();
			}
		}

		if (getReturnedValue() != null) {
			if (property.getReturnedValue() != null) {
				if (!getReturnedValue().value().equals(property.getReturnedValue().value())) {
					if (rulingProperty == null) {
						return "Returned value '" + getReturnedValue().value() + "' is not equal to '"
								+ property.getReturnedValue().value() + "'";
					}
				}
			}
		}
		if (cloningStrategy != null) {
			if (property.cloningStrategy != null) {
				if (property.cloningStrategy.value() != property.cloningStrategy.value()) {
					if (rulingProperty == null || rulingProperty.cloningStrategy == null) {
						return "Incompatible cloning strategy: " + cloningStrategy + " is not compatible with " + property.cloningStrategy;
					}
				}
			}
		}
		if (getEmbedded() != null) {
			if (property.getEmbedded() != null) {
				if (!Arrays.equals(getEmbedded().closureConditions(), property.getEmbedded().closureConditions())) {
					if (rulingProperty == null || rulingProperty.getEmbedded() == null && rulingProperty.getComplexEmbedded() == null) {
						return "Embedded closure conditions are not equal";
					}
				}
				if (!Arrays.equals(getEmbedded().deletionConditions(), property.getEmbedded().deletionConditions())) {
					if (rulingProperty == null || rulingProperty.getEmbedded() == null && rulingProperty.getComplexEmbedded() == null) {
						return "Embedded deletion conditions are not equal";
					}
				}
			} else if (property.getComplexEmbedded() != null) {
				if (rulingProperty == null || rulingProperty.getEmbedded() == null && rulingProperty.getComplexEmbedded() == null) {
					return "Cannot define both " + Embedded.class.getSimpleName() + " and " + ComplexEmbedded.class.getSimpleName()
							+ " on the same property.";
				}
			}
		} else if (getComplexEmbedded() != null) {
			if (property.getComplexEmbedded() != null) {
				boolean ok = true;
				List<ClosureCondition> ccList = Arrays.asList(property.getComplexEmbedded().closureConditions());
				for (ClosureCondition c : getComplexEmbedded().closureConditions()) {
					boolean found = false;
					for (ClosureCondition cc : ccList) {
						if (c.equals(cc)) {
							found = true;
							ccList.remove(cc);
							break;
						}
					}
					if (!found) {
						ok = false;
						break;
					}
				}
				ok &= ccList.isEmpty();
				if (!ok) {
					if (rulingProperty == null || rulingProperty.getEmbedded() == null && rulingProperty.getComplexEmbedded() == null) {
						return "Closure conditions are not equal";
					}
				}
				List<DeletionCondition> dcList = Arrays.asList(property.getComplexEmbedded().deletionConditions());
				for (DeletionCondition d : getComplexEmbedded().deletionConditions()) {
					boolean found = false;
					for (DeletionCondition dc : dcList) {
						if (d.equals(dc)) {
							found = true;
							dcList.remove(dc);
							break;
						}
					}
					if (!found) {
						ok = false;
						break;
					}
				}
				ok &= dcList.isEmpty();
				if (!ok) {
					if (rulingProperty == null || rulingProperty.getEmbedded() == null && rulingProperty.getComplexEmbedded() == null) {
						return "Deletion conditions are not equal";
					}
				}
			} else if (property.getEmbedded() != null) {
				if (rulingProperty == null || rulingProperty.getEmbedded() == null && rulingProperty.getComplexEmbedded() == null) {
					return "Cannot define both " + ComplexEmbedded.class.getSimpleName() + " and " + Embedded.class.getSimpleName()
							+ " on the same property.";
				}
			}
		}

		// Serialization options
		if (getXMLAttribute() != null) {
			if (property.getXMLAttribute() != null) {
				if (!getXMLAttribute().xmlTag().equals(XMLAttribute.DEFAULT_XML_TAG)
						&& !property.getXMLAttribute().xmlTag().equals(XMLAttribute.DEFAULT_XML_TAG)
						&& !getXMLAttribute().xmlTag().equals(property.getXMLAttribute().xmlTag())) {
					if (rulingProperty == null || rulingProperty.getXMLAttribute() != null && rulingProperty.getXMLElement() != null) {
						return "XML tag '" + getXMLAttribute().xmlTag() + "' is not equal to '" + property.getXMLAttribute().xmlTag() + "'";
					}
				}
			} else if (property.getXMLElement() != null) {
				if (rulingProperty == null || rulingProperty.getXMLAttribute() != null && rulingProperty.getXMLElement() != null) {
					return "Property '" + propertyIdentifier + "' is declared as an XMLAttribute on " + getModelEntity()
							+ " but as an XMLElement on " + property.getModelEntity();
				}
			}
		} else if (getXMLElement() != null) {
			if (property.getXMLElement() != null) {
				if (!getXMLElement().xmlTag().equals(XMLElement.DEFAULT_XML_TAG)
						&& !property.getXMLElement().xmlTag().equals(XMLElement.DEFAULT_XML_TAG)
						&& !getXMLElement().xmlTag().equals(property.getXMLElement().xmlTag())) {
					if (rulingProperty == null || rulingProperty.getXMLAttribute() != null && rulingProperty.getXMLElement() != null) {
						return "XML tag '" + getXMLElement().xmlTag() + "' is not equal to '" + property.getXMLElement().xmlTag() + "'";
					}
				}
				if (!getXMLElement().context().equals(XMLElement.NO_CONTEXT)
						&& !property.getXMLElement().context().equals(XMLElement.NO_CONTEXT)
						&& !getXMLElement().context().equals(property.getXMLElement().context())) {
					if (rulingProperty == null || rulingProperty.getXMLAttribute() != null && rulingProperty.getXMLElement() != null) {
						return "Context '" + getXMLElement().context() + "' is not equal to '" + property.getXMLElement().context()
								+ "' for property '" + propertyIdentifier + "'";
					}
				}
				if (!getXMLElement().namespace().equals(XMLElement.NO_NAME_SPACE)
						&& !property.getXMLElement().namespace().equals(XMLElement.NO_NAME_SPACE)
						&& !getXMLElement().namespace().equals(property.getXMLElement().namespace())) {
					if (rulingProperty == null || rulingProperty.getXMLAttribute() != null && rulingProperty.getXMLElement() != null) {
						return "Namespace '" + getXMLElement().namespace() + "' is not equal to '" + property.getXMLElement().namespace();

					}
				}
			} else if (property.getXMLAttribute() != null) {
				if (rulingProperty == null || rulingProperty.getXMLAttribute() != null && rulingProperty.getXMLElement() != null) {
					return "Property '" + propertyIdentifier + "' is declared as an XMLElement on " + getModelEntity()
							+ " but as an XMLAttribute on " + property.getModelEntity();
				}
			}
		}
		return null;
	}

	/**
	 * Merges <code>this</code> {@link ModelProperty} with the given <code>property</code>. The <code>rulingProperty</code> takes precedence
	 * whenever it declares any annotation.
	 * 
	 * @param property
	 * @param rulingProperty
	 * @return
	 * @throws ModelDefinitionException
	 */
	protected <J extends I> ModelProperty<I> combineWith(ModelProperty<?> property, ModelProperty<J> rulingProperty)
			throws ModelDefinitionException {
		if (property == null && (rulingProperty == null || rulingProperty == this)) {
			return this;
		}
		Getter getter = null;
		Setter setter = null;
		Adder adder = null;
		Remover remover = null;
		XMLAttribute xmlAttribute = null;
		XMLElement xmlElement = null;
		ReturnedValue returnedValue = null;
		Embedded embedded = null;
		ComplexEmbedded complexEmbedded = null;
		CloningStrategy cloningStrategy = null;
		// PastingPoint setPastingPoint = null;
		// PastingPoint addPastingPoint = null;
		Method getterMethod = null;
		Method setterMethod = null;
		Method adderMethod = null;
		Method removerMethod = null;
		if (rulingProperty != null && rulingProperty.getGetter() != null) {
			getter = rulingProperty.getGetter();
		} else {
			Cardinality cardinality = null;
			String inverse = null;
			String defaultValue = null;
			boolean stringConvertable;
			boolean ignoreType;
			if (getGetter() != null) {
				cardinality = getGetter().cardinality();
			} else {
				cardinality = property.getGetter().cardinality();
			}
			if (getGetter() == null || getGetter().inverse().equals(Getter.UNDEFINED)) {
				inverse = property.getGetter().inverse();
			} else {
				inverse = getGetter().inverse();
			}
			if (getGetter() == null || getGetter().defaultValue().equals(Getter.UNDEFINED)) {
				defaultValue = property.getGetter().defaultValue();
			} else {
				defaultValue = getGetter().defaultValue();
			}
			if (getGetter() == null) {
				stringConvertable = property.getGetter().isStringConvertable();
				ignoreType = property.getGetter().ignoreType();
			} else {
				stringConvertable = getGetter().isStringConvertable();
				ignoreType = getGetter().ignoreType();
			}
			getter = new Getter.GetterImpl(propertyIdentifier, cardinality, inverse, defaultValue, stringConvertable, ignoreType);
		}
		if (rulingProperty != null && rulingProperty.getSetter() != null) {
			setter = rulingProperty.getSetter();
		} else {
			if (getSetter() != null) {
				setter = getSetter();
			} else if (property.getSetter() != null) {
				setter = property.getSetter();
			}
		}
		if (rulingProperty != null && rulingProperty.getAdder() != null) {
			adder = rulingProperty.getAdder();
		} else {
			if (getAdder() != null) {
				adder = getAdder();
			} else if (property.getAdder() != null) {
				adder = property.getAdder();
			}
		}
		if (rulingProperty != null && rulingProperty.getRemover() != null) {
			remover = rulingProperty.getRemover();
		} else {
			if (getRemover() != null) {
				remover = getRemover();
			} else if (property.getRemover() != null) {
				remover = property.getRemover();
			}
		}

		if (rulingProperty != null && (rulingProperty.getEmbedded() != null || rulingProperty.getComplexEmbedded() != null)) {
			embedded = rulingProperty.getEmbedded();
			complexEmbedded = rulingProperty.getComplexEmbedded();
		} else if (getEmbedded() != null) {
			embedded = getEmbedded();
		} else if (getComplexEmbedded() != null) {
			complexEmbedded = getComplexEmbedded();
		} else if (property.getEmbedded() != null) {
			embedded = property.getEmbedded();
		} else if (property.getComplexEmbedded() != null) {
			complexEmbedded = property.getComplexEmbedded();
		}

		if (rulingProperty != null && rulingProperty.getReturnedValue() != null) {
			returnedValue = rulingProperty.getReturnedValue();
		} else if (getReturnedValue() != null) {
			returnedValue = getReturnedValue();
		} else if (property.getReturnedValue() != null) {
			returnedValue = property.getReturnedValue();
		}

		if (rulingProperty != null && rulingProperty.cloningStrategy != null) {
			cloningStrategy = rulingProperty.cloningStrategy;
		} else if (this.cloningStrategy != null) {
			cloningStrategy = this.cloningStrategy;
		} else if (property.cloningStrategy != null) {
			cloningStrategy = property.cloningStrategy;
		}

		if (rulingProperty != null && rulingProperty.getGetterMethod() != null) {
			getterMethod = rulingProperty.getGetterMethod();
		} else {
			if (getGetterMethod() != null) {
				getterMethod = getGetterMethod();
			} else {
				getterMethod = property.getGetterMethod();
			}
		}

		if (rulingProperty != null && rulingProperty.getSetterMethod() != null) {
			setterMethod = rulingProperty.getSetterMethod();
		} else {
			if (getSetterMethod() != null) {
				setterMethod = getSetterMethod();
			} else {
				setterMethod = property.getSetterMethod();
			}
		}

		if (rulingProperty != null && rulingProperty.getAdderMethod() != null) {
			adderMethod = rulingProperty.getAdderMethod();
		} else {
			if (getAdderMethod() != null) {
				adderMethod = getAdderMethod();
			} else {
				adderMethod = property.getAdderMethod();
			}
		}

		if (rulingProperty != null && rulingProperty.getRemoverMethod() != null) {
			removerMethod = rulingProperty.getRemoverMethod();
		} else {
			if (getRemoverMethod() != null) {
				removerMethod = getRemoverMethod();
			} else {
				removerMethod = property.getRemoverMethod();
			}
		}

		if (rulingProperty != null && (rulingProperty.getXMLAttribute() != null || rulingProperty.getXMLElement() != null)) {
			xmlAttribute = rulingProperty.getXMLAttribute();
			xmlElement = rulingProperty.getXMLElement();
		} else if (getXMLAttribute() != null || property.getXMLAttribute() != null) {
			if (getXMLAttribute() != null && !getXMLAttribute().xmlTag().equals(XMLAttribute.DEFAULT_XML_TAG)) {
				xmlAttribute = getXMLAttribute();
			} else {
				xmlAttribute = property.getXMLAttribute();
			}
		} else if (getXMLElement() != null || property.getXMLElement() != null) {
			String xmlTag = XMLElement.DEFAULT_XML_TAG;
			String context = XMLElement.NO_CONTEXT;
			String namespace = XMLElement.NO_NAME_SPACE;
			boolean primary = false;
			if (getXMLElement() != null) {
				if (property.getXMLElement() != null) {
					if (!getXMLElement().xmlTag().equals(XMLElement.DEFAULT_XML_TAG)) {
						xmlTag = getXMLElement().xmlTag();
					} else {
						xmlTag = property.getXMLElement().xmlTag();
					}
					if (!getXMLElement().context().equals(XMLElement.NO_CONTEXT)) {
						context = getXMLElement().context();
					} else {
						context = property.getXMLElement().context();
					}
					if (!getXMLElement().namespace().equals(XMLElement.NO_NAME_SPACE)) {
						namespace = getXMLElement().namespace();
					} else {
						namespace = property.getXMLElement().namespace();
					}
					primary |= getXMLElement().primary();
					primary |= property.getXMLElement().primary();
					xmlElement = new XMLElement.XMLElementImpl(xmlTag, context, namespace, primary);
				} else {
					xmlElement = getXMLElement();
				}
			} else {
				xmlElement = property.getXMLElement();
			}
		}

		addPastingPoint = property.getAddPastingPoint();
		setPastingPoint = property.getSetPastingPoint();

		if (rulingProperty.getAddPastingPoint() != null) {
			addPastingPoint = rulingProperty.getAddPastingPoint();
		}
		if (rulingProperty.getSetPastingPoint() != null) {
			setPastingPoint = rulingProperty.getSetPastingPoint();
		}

		return new ModelProperty<I>(getModelEntity(), getPropertyIdentifier(), getter, setter, adder, remover, xmlAttribute, xmlElement,
				returnedValue, embedded, complexEmbedded, cloningStrategy, setPastingPoint, addPastingPoint, getterMethod, setterMethod,
				adderMethod, removerMethod);
	}

	public ModelEntity<I> getModelEntity() {
		return modelEntity;
	}

	public Class<?> getType() {
		return type;
	}

	public String getPropertyIdentifier() {
		return propertyIdentifier;
	}

	public Getter getGetter() {
		return getter;
	}

	public Setter getSetter() {
		return setter;
	}

	public Adder getAdder() {
		return adder;
	}

	public Remover getRemover() {
		return remover;
	}

	public XMLAttribute getXMLAttribute() {
		return xmlAttribute;
	}

	public XMLElement getXMLElement() {
		return xmlElement;
	}

	public String getXMLContext() {
		if (xmlElement != null) {
			return xmlElement.context();
		} else {
			return "";
		}
	}

	public String getXMLTag() {
		if (xmlTag == null && xmlAttribute != null) {
			xmlTag = xmlAttribute.xmlTag();
			if (xmlTag.equals(XMLAttribute.DEFAULT_XML_TAG)) {
				xmlTag = propertyIdentifier;
			}
		}
		return xmlTag;
	}

	public Method getGetterMethod() {
		return getterMethod;
	}

	public Method getSetterMethod() {
		return setterMethod;
	}

	public Method getAdderMethod() {
		return adderMethod;
	}

	public Method getRemoverMethod() {
		return removerMethod;
	}

	private Object defaultValue;

	public Object getDefaultValue(ModelFactory factory) throws InvalidDataException {
		if (defaultValue != null) {
			return defaultValue;
		} else if (!getGetter().defaultValue().equals(Getter.UNDEFINED)) {
			return factory.getStringEncoder().fromString(getType(), getGetter().defaultValue());
		} else {
			return null;
		}
	}

	public boolean isStringConvertable() {
		if (getGetter() != null) {
			return getGetter().isStringConvertable();
		}
		return false;
	}

	public boolean ignoreType() {
		if (getGetter() != null) {
			return getGetter().ignoreType();
		}
		return false;
	}

	public Cardinality getCardinality() {
		if (cardinality == null && getGetter() != null) {
			cardinality = getGetter().cardinality();
		}
		return cardinality;
	}

	public boolean hasInverseProperty() {
		return !getGetter().inverse().equals(Getter.UNDEFINED);
	}

	public <T> ModelProperty<? super T> getInverseProperty(ModelEntity<T> oppositeEntity) throws ModelDefinitionException {
		if (hasInverseProperty()) {
			if (oppositeEntity == null) {
				throw new ModelDefinitionException(getModelEntity() + ": Cannot find opposite entity " + getType());
			}
			ModelProperty<? super T> inverseProperty = oppositeEntity.getModelProperty(getGetter().inverse());
			if (inverseProperty == null) {
				throw new ModelDefinitionException(getModelEntity() + ": Cannot find inverse property " + getGetter().inverse() + " for "
						+ oppositeEntity.getImplementedInterface().getSimpleName());
			}
			return inverseProperty;
		}
		return null;
	}

	@Override
	public String toString() {
		return "ModelProperty[" + getModelEntity() + "." + getPropertyIdentifier() + "]";
	}

	public ModelEntity<?> getAccessedEntity() throws ModelDefinitionException {
		return ModelEntityLibrary.get(getType());
	}

	public ReturnedValue getReturnedValue() {
		return returnedValue;
	}

	public Embedded getEmbedded() {
		return embedded;
	}

	public ComplexEmbedded getComplexEmbedded() {
		return complexEmbedded;
	}

	public boolean isSerializable() {
		return getXMLAttribute() != null || getXMLElement() != null;
	}

	public StrategyType getCloningStrategy() {
		if (cloningStrategy == null) {
			if (ModelEntityLibrary.has(getType())) {
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

	public PastingPoint getSetPastingPoint() {
		return setPastingPoint;
	}

	public PastingPoint getAddPastingPoint() {
		return addPastingPoint;
	}

}
