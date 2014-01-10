package org.openflexo.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.openflexo.antar.binding.ReflectionUtils;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.Modify;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.StringConverter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.exceptions.PropertyClashException;

/**
 * This class represents an instance of the {@link org.openflexo.model.annotations.ModelEntity} annotation declared on an interface.
 * 
 * @author Guillaume
 * 
 * @param <I>
 */
public class ModelEntity<I> {

	/**
	 * The implemented interface corresponding to this model entity
	 */
	private final Class<I> implementedInterface;

	/**
	 * The model entity annotation describing this entity
	 */
	private final org.openflexo.model.annotations.ModelEntity entityAnnotation;

	/**
	 * The implementationClass associated with this model entity
	 */
	private final ImplementationClass implementationClass;

	/**
	 * The {@link XMLElement} annotation, if any
	 */
	private final XMLElement xmlElement;

	/**
	 * The properties of this entity. The key is the identifier of the property
	 */
	private final Map<String, ModelProperty<? super I>> properties;

	/**
	 * The properties of this entity. The key is the identifier of the property
	 */
	private final Map<ModelMethod, ModelProperty<? super I>> propertyMethods;

	/**
	 * The properties of this entity. The key is the xml attribute name of the property
	 */
	private Map<String, ModelProperty<? super I>> modelPropertiesByXMLAttributeName;

	/**
	 * The xmlTag of this entity, if any
	 */
	private String xmlTag;

	/**
	 * The modify annotation of this entity, if any
	 */
	private Modify modify;

	/**
	 * Whether this entity is an abstract entity. Abstract entities cannot be instantiated.
	 */
	private final boolean isAbstract;

	/**
	 * The default implementing class of this entity. The class can be abstract. This value may be null.
	 */
	private Class<?> implementingClass;

	/**
	 * The list of super interfaces of this entity. This may be null.
	 */
	private List<Class<? super I>> superImplementedInterfaces;

	/**
	 * The complete list of all the super entities of this entity.
	 */
	private List<ModelEntity<? super I>> allSuperEntities;

	/**
	 * The list of super entities (matching the list of super interfaces). This may be null
	 */
	private List<ModelEntity<? super I>> directSuperEntities;

	/**
	 * The initializers of this entity.
	 */
	private final Map<Method, ModelInitializer> initializers;

	DeserializationInitializer deserializationInitializer = null;
	DeserializationFinalizer deserializationFinalizer = null;

	private boolean initialized;

	private final Map<String, ModelProperty<I>> declaredModelProperties;

	private Set<ModelEntity<?>> embeddedEntities;

	private final Map<Class<I>, Set<Method>> delegateImplementations;

	ModelEntity(@Nonnull Class<I> implementedInterface) throws ModelDefinitionException {
		this.implementedInterface = implementedInterface;
		declaredModelProperties = new HashMap<String, ModelProperty<I>>();
		properties = new HashMap<String, ModelProperty<? super I>>();
		propertyMethods = new HashMap<ModelMethod, ModelProperty<? super I>>();
		initializers = new HashMap<Method, ModelInitializer>();
		embeddedEntities = new HashSet<ModelEntity<?>>();
		entityAnnotation = implementedInterface.getAnnotation(org.openflexo.model.annotations.ModelEntity.class);
		implementationClass = implementedInterface.getAnnotation(ImplementationClass.class);
		xmlElement = implementedInterface.getAnnotation(XMLElement.class);
		modify = implementedInterface.getAnnotation(Modify.class);
		isAbstract = entityAnnotation.isAbstract();
		// We resolve here the model super interface
		// The corresponding model entity MUST be resolved later
		for (Class<?> i : implementedInterface.getInterfaces()) {
			if (i.isAnnotationPresent(org.openflexo.model.annotations.ModelEntity.class)) {
				if (superImplementedInterfaces == null) {
					superImplementedInterfaces = new ArrayList<Class<? super I>>();
				}
				superImplementedInterfaces.add((Class<? super I>) i);
			}
		}
		for (Field field : getImplementedInterface().getDeclaredFields()) {
			StringConverter converter = field.getAnnotation(StringConverter.class);
			if (converter != null) {
				try {
					StringConverterLibrary.getInstance().addConverter((Converter<?>) field.get(null));
				} catch (IllegalArgumentException e) {
					// This should not happen since interfaces can only have static fields
					// and we pass 'null'
					throw new ModelDefinitionException("Field " + field + " is not static! Cannot use it as string converter.");
				} catch (IllegalAccessException e) {
					throw new ModelDefinitionException("Illegal access to field " + field);
				} catch (ClassCastException e) {
					throw new ModelDefinitionException("Field " + field.getName() + " is annotated with " + StringConverter.class.getName()
							+ " but the value of the field is not an instance of " + Converter.class.getName());
				}
			}
		}

		// We scan already all the declared properties but we do not resolve their type. We do not resolve inherited properties either.
		for (Method m : getImplementedInterface().getDeclaredMethods()) {
			String propertyIdentifier = getPropertyIdentifier(m);
			if (propertyIdentifier == null || !declaredModelProperties.containsKey(propertyIdentifier)) {
				List<Method> overridenMethods = ReflectionUtils.getOverridenMethods(m);
				for (Method override : overridenMethods) {
					propertyIdentifier = getPropertyIdentifier(override);
					if (propertyIdentifier != null) {
						break;
					}
				}
			}
			if (propertyIdentifier != null && !declaredModelProperties.containsKey(propertyIdentifier)) {
				// The next line creates the property
				ModelProperty<I> property = ModelProperty.getModelProperty(propertyIdentifier, this);
				declaredModelProperties.put(propertyIdentifier, property);
			}
			org.openflexo.model.annotations.Initializer initializer = m.getAnnotation(org.openflexo.model.annotations.Initializer.class);
			if (initializer != null) {
				initializers.put(m, new ModelInitializer(initializer, m));
			}

			org.openflexo.model.annotations.DeserializationFinalizer deserializationFinalizer = m
					.getAnnotation(org.openflexo.model.annotations.DeserializationFinalizer.class);
			if (deserializationFinalizer != null) {
				if (this.deserializationFinalizer == null) {
					this.deserializationFinalizer = new DeserializationFinalizer(deserializationFinalizer, m);
				} else {
					throw new ModelDefinitionException("Duplicated deserialization finalizer found for entity " + getImplementedInterface());
				}
			}

			org.openflexo.model.annotations.DeserializationInitializer deserializationInitializer = m
					.getAnnotation(org.openflexo.model.annotations.DeserializationInitializer.class);
			if (deserializationInitializer != null) {
				if (this.deserializationInitializer == null) {
					this.deserializationInitializer = new DeserializationInitializer(deserializationInitializer, m);
				} else {
					throw new ModelDefinitionException("Duplicated deserialization initializer found for entity "
							+ getImplementedInterface());
				}
			}
		}

		// Init delegate implementations
		delegateImplementations = new HashMap<Class<I>, Set<Method>>();
		for (Class<?> c : getImplementedInterface().getDeclaredClasses()) {
			if (c.getAnnotation(Implementation.class) != null) {
				if (getImplementedInterface().isAssignableFrom(c)) {
					Class<I> candidateImplementation = (Class<I>) c;
					// System.out.println("Found implementation " + candidateImplementation + " for " + getImplementedInterface());
					Set<Method> implementedMethods = new HashSet<Method>();
					for (Method m : candidateImplementation.getDeclaredMethods()) {
						implementedMethods.add(m);
					}
					delegateImplementations.put(candidateImplementation, implementedMethods);
				} else {
					throw new ModelDefinitionException("Found candidate implementation " + c + " for entity " + getImplementedInterface()
							+ " which does not implement " + getImplementedInterface());
				}
			}
		}
	}

	private String getPropertyIdentifier(Method m) {
		String propertyIdentifier = null;
		Getter aGetter = m.getAnnotation(Getter.class);
		if (aGetter != null) {
			propertyIdentifier = aGetter.value();
		} else {
			Setter aSetter = m.getAnnotation(Setter.class);
			if (aSetter != null) {
				propertyIdentifier = aSetter.value();
			} else {
				Adder anAdder = m.getAnnotation(Adder.class);
				if (anAdder != null) {
					propertyIdentifier = anAdder.value();
				} else {
					Remover aRemover = m.getAnnotation(Remover.class);
					if (aRemover != null) {
						propertyIdentifier = aRemover.value();
					}
				}
			}
		}
		return propertyIdentifier;
	}

	void init() throws ModelDefinitionException {

		// System.out.println("Init " + getImplementedInterface() + " with direct super entities " + getDirectSuperEntities());

		// We now resolve our inherited entities and properties
		if (getDirectSuperEntities() != null) {
			embeddedEntities.addAll(getDirectSuperEntities());
		}
		for (ModelProperty<? super I> property : declaredModelProperties.values()) {
			if (property.getType() != null && !StringConverterLibrary.getInstance().hasConverter(property.getType())
					&& !property.getType().isEnum() && !property.isStringConvertable() && !property.ignoreType()) {
				try {
					embeddedEntities.add(ModelEntityLibrary.get(property.getType(), true));
				} catch (ModelDefinitionException e) {
					throw new ModelDefinitionException("Could not retrieve model entity for property " + property + " and entity " + this,
							e);
				}
			}
		}

		// We also resolve our imports
		Imports imports = implementedInterface.getAnnotation(Imports.class);
		if (imports != null) {
			for (Import imp : imports.value()) {
				embeddedEntities.add(ModelEntityLibrary.get(imp.value(), true));
			}
		}

		embeddedEntities = Collections.unmodifiableSet(embeddedEntities);

		checkImplementationsClash();

		// System.out.println("For " + getImplementedInterface() + " embeddedEntities=" + embeddedEntities);
	}

	public Map<Class<I>, Set<Method>> getDelegateImplementations() {
		return delegateImplementations;
	}

	private void checkImplementationsClash() throws ModelDefinitionException {

		// System.out.println("checkImplementationsClash() for " + getImplementedInterface());
		// System.out.println("embeddedEntities=" + embeddedEntities);
		// System.out.println("getDirectSuperEntities()=" + getDirectSuperEntities());

		if (getDirectSuperEntities() != null) {
			Set<Method> implementedMethods = new HashSet<Method>();
			for (ModelEntity<? super I> parentEntity : getDirectSuperEntities()) {
				for (Class<? super I> implClass : parentEntity.delegateImplementations.keySet()) {
					for (Method m : parentEntity.delegateImplementations.get(implClass)) {
						for (Method m2 : implementedMethods) {
							if (PamelaUtils.methodIsEquivalentTo(m, m2)) {
								// We are in the case of implementation clash
								// We must now check if this clash was property handled
								boolean localImplementationWasFound = false;
								for (Class<?> localImplClass : delegateImplementations.keySet()) {
									for (Method m3 : delegateImplementations.get(localImplClass)) {
										if (PamelaUtils.methodIsEquivalentTo(m, m3)) {
											// A local implementation was found
											localImplementationWasFound = true;
											break;
										}
									}
									if (!localImplementationWasFound) {
										break;
									}
								}
								if (!localImplementationWasFound) {
									throw new ModelDefinitionException("Multiple inheritance implementation clash with method " + m
											+ " defined in " + implClass + " and " + m2.getDeclaringClass()
											+ ". Please disambiguate method.");
								}
							}
						}
						implementedMethods.add(m);
						// System.out.println("Consider implementation method " + m + " in " + getImplementedInterface());
					}
				}
			}
		}
	}

	void mergeProperties() throws ModelDefinitionException {
		if (initialized) {
			return;
		}
		properties.putAll(declaredModelProperties);

		// Resolve inherited properties (we only scan direct parent properties, since themselves will scan for their inherited parents)
		if (getDirectSuperEntities() != null) {
			for (ModelEntity<? super I> parentEntity : getDirectSuperEntities()) {
				parentEntity.mergeProperties();
				for (ModelProperty<? super I> property : parentEntity.properties.values()) {
					createMergedProperty(property.getPropertyIdentifier(), true);
				}
			}
		}

		// Validate properties now (they should all have a getter and a return type, etc...
		for (ModelProperty<? super I> p : properties.values()) {
			p.validate();
		}
		initialized = true;
		for (ModelProperty<? super I> p : properties.values()) {
			propertyMethods.put(new ModelMethod(p.getGetterMethod()), p);
			if (p.getSetterMethod() != null) {
				propertyMethods.put(new ModelMethod(p.getSetterMethod()), p);
			}
			if (p.getAdderMethod() != null) {
				propertyMethods.put(new ModelMethod(p.getAdderMethod()), p);
			}
			if (p.getRemoverMethod() != null) {
				propertyMethods.put(new ModelMethod(p.getRemoverMethod()), p);
			}
		}

		// TODO: maybe it would be better to be closer to what constructors do, ie, if there are super-initializer,
		// And none of them are without arguments, then this entity should define an initializer with the same
		// method signature (this is to enforce the developer to be aware of what the parameters do):
		// FlexoModelObject.init(String flexoID) vs AbstractNode.init(String nodeName)-->same signature but the semantics of the parameter
		// is different
		// Validate initializers

		for (ModelInitializer i : initializers.values()) {
			for (String s : i.getParameters()) {
				if (s == null) {
					continue;
				}
				ModelProperty<? super I> modelProperty = getModelProperty(s);
				if (modelProperty == null) {
					throw new ModelDefinitionException("Initializer " + i.getInitializingMethod().toGenericString()
							+ " declares a parameter " + s + " but this entity has no such declared property");
				}
			}
		}
	}

	public Set<ModelEntity<?>> getEmbeddedEntities() {
		return embeddedEntities;
	}

	public Class<?> getImplementingClass() throws ModelDefinitionException {
		if (implementingClass != null) {
			return implementingClass;
		}
		if (implementationClass != null) {
			if (implementedInterface.isAssignableFrom(implementationClass.value())) {
				return implementingClass = implementationClass.value();
			} else {
				throw new ModelDefinitionException("Class " + implementationClass.value().getName()
						+ " is declared as an implementation class of " + this + " but does not extend " + implementedInterface.getName());
			}
		} else {
			if (getDirectSuperEntities() != null) {
				for (ModelEntity<? super I> e : getDirectSuperEntities()) {
					Class<?> klass = e.getImplementingClass();
					if (klass != null) {
						if (implementingClass == null) {
							implementingClass = klass;
						} else {
							throw new ModelDefinitionException("Ambiguous implementing klass for entity '" + this
									+ "'. Found more than one valid super klass: " + implementingClass.getName() + " and "
									+ klass.getName());
						}
					}
				}
			}
		}
		return implementingClass;
	}

	public boolean singleInheritance() {
		return superImplementedInterfaces != null && superImplementedInterfaces.size() == 1;
	}

	public boolean multipleInheritance() {
		return superImplementedInterfaces != null && superImplementedInterfaces.size() > 1;
	}

	public List<ModelEntity<? super I>> getDirectSuperEntities() throws ModelDefinitionException {
		if (directSuperEntities == null && superImplementedInterfaces != null) {
			directSuperEntities = new ArrayList<ModelEntity<? super I>>(superImplementedInterfaces.size());
			for (Class<? super I> superInterface : superImplementedInterfaces) {
				ModelEntity<? super I> superEntity = ModelEntityLibrary.get(superInterface, true);
				directSuperEntities.add(superEntity);
			}
		}
		return directSuperEntities;
	}

	/**
	 * Returns a list of all the (direct & indirect) super entities of this entity.
	 * 
	 * @return all the (direct & indirect) super entities of this entity.
	 * @throws ModelDefinitionException
	 */
	public List<ModelEntity<? super I>> getAllSuperEntities() throws ModelDefinitionException {
		if (allSuperEntities == null && superImplementedInterfaces != null) {
			allSuperEntities = new ArrayList<ModelEntity<? super I>>();
			// 1. We add the direct ancestors of this entity
			allSuperEntities.addAll(getDirectSuperEntities());
			// 2. We add the indirect ancestors of this entity to have a topologically sorted array.
			for (ModelEntity<? super I> superEntity : new ArrayList<ModelEntity<? super I>>(allSuperEntities)) {
				allSuperEntities.addAll(superEntity.getAllSuperEntities());
			}
		}
		return allSuperEntities;
	}

	public boolean hasProperty(String propertyIdentifier) {
		return properties.containsKey(propertyIdentifier);
	}

	public boolean hasProperty(ModelProperty<?> modelProperty) {
		return properties.containsValue(modelProperty);
	}

	public ModelProperty<? super I> getPropertyForMethod(Method method) {
		return propertyMethods.get(new ModelMethod(method));
	}

	public ModelProperty<? super I> getPropertyForXMLAttributeName(String name) throws ModelDefinitionException {
		if (modelPropertiesByXMLAttributeName == null) {
			synchronized (this) {
				if (modelPropertiesByXMLAttributeName == null) {
					modelPropertiesByXMLAttributeName = new HashMap<String, ModelProperty<? super I>>();
					for (ModelProperty<? super I> property : properties.values()) {
						if (property.getXMLTag() != null) {
							modelPropertiesByXMLAttributeName.put(property.getXMLTag(), property);
						}
					}
					modelPropertiesByXMLAttributeName = Collections.unmodifiableMap(modelPropertiesByXMLAttributeName);
				}
			}
		}
		return modelPropertiesByXMLAttributeName.get(name);
	}

	/**
	 * Returns whether the implemented interface associated with <code>this</code> model entity has a method annotated with a {@link Getter}
	 * with its value set to the provided <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 * @return
	 */
	private boolean declaresProperty(String propertyIdentifier) {
		return declaredModelProperties.containsKey(propertyIdentifier);
	}

	/**
	 * Returns the {@link ModelProperty} with the identifier <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @return the property with the identifier <code>propertyIdentifier</code>.
	 * @throws ModelDefinitionException
	 */
	public ModelProperty<? super I> getModelProperty(String propertyIdentifier) throws ModelDefinitionException {
		return properties.get(propertyIdentifier);
	}

	/**
	 * Creates the {@link ModelProperty} with the identifier <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @param create
	 *            whether the property should be create or not, if not found
	 * @return the property with the identifier <code>propertyIdentifier</code>.
	 * @throws ModelDefinitionException
	 */
	void createMergedProperty(String propertyIdentifier, boolean create) throws ModelDefinitionException {
		ModelProperty<? super I> returned = buildModelProperty(propertyIdentifier);
		properties.put(propertyIdentifier, returned);
	}

	/**
	 * Builds the {@link ModelProperty} with identifier <code>propertyIdentifier</code>, if it is declared at least once in the hierarchy
	 * (i.e., at least one method is annotated with the {@link Getter} annotation and the given identifier, <code>propertyIdentifier</code>
	 * ). In case of inheritance, the property is combined with all its ancestors. In case of multiple inheritance of the same property,
	 * conflicts are resolved to the possible extent. In case of contradiction, a {@link PropertyClashException} is thrown.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @return the new, possibly combined, property.
	 * @throws ModelDefinitionException
	 *             in case of an inconsistency in the model of a clash of property inheritance.
	 */
	private ModelProperty<? super I> buildModelProperty(String propertyIdentifier) throws ModelDefinitionException {
		ModelProperty<I> property = ModelProperty.getModelProperty(propertyIdentifier, this);
		if (singleInheritance() || multipleInheritance()) {
			ModelProperty<? super I> parentProperty = buildModelPropertyUsingParentProperties(propertyIdentifier, property);
			return combine(property, parentProperty);
		}
		return property;
	}

	/**
	 * Returns a model property with the identifier <code>propertyIdentifier</code> which is a combination of all the model properties with
	 * the identifier <code>propertyIdentifier</code> of the parent entities. This method may return <code>null</code> in case amongst all
	 * parents, non of them declare a property with identifier <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @param property
	 *            the model property with the identifier defined for <code>this</code> {@link ModelEntity}.
	 * @return
	 * @throws ModelDefinitionException
	 */
	private ModelProperty<? super I> buildModelPropertyUsingParentProperties(String propertyIdentifier, ModelProperty<I> property)
			throws ModelDefinitionException {
		ModelProperty<? super I> returned = null;
		for (ModelEntity<? super I> parent : getDirectSuperEntities()) {
			if (!parent.hasProperty(propertyIdentifier)) {
				continue;
			}
			if (returned == null) {
				returned = parent.getModelProperty(propertyIdentifier);
			} else {
				returned = combineAsAncestors(parent.getModelProperty(propertyIdentifier), returned, property);
			}
		}
		return returned;
	}

	/**
	 * Returns a combined property which is the merge of the property <code>property</code> and its parent property
	 * <code>parentProperty</code>. In case of conflicts, the behaviour defined by <code>property</code> superseeds the one defined by
	 * <code>parentProperty</code>
	 * 
	 * @param property
	 *            the property to merge
	 * @param parentProperty
	 *            the parent property to merge
	 * @return a combined/merged property
	 * @throws ModelDefinitionException
	 */
	private ModelProperty<? super I> combine(ModelProperty<I> property, ModelProperty<? super I> parentProperty)
			throws ModelDefinitionException {
		return property.combineWith(parentProperty, property);
	}

	private ModelProperty<? super I> combineAsAncestors(ModelProperty<? super I> property1, ModelProperty<? super I> property2,
			ModelProperty<I> declaredProperty) throws ModelDefinitionException {
		if (property1 == null) {
			return property2;
		}
		if (property2 == null) {
			return property1;
		}
		checkForContradictions(property1, property2, declaredProperty);
		return property1.combineWith(property2, declaredProperty);
	}

	private void checkForContradictions(ModelProperty<? super I> property1, ModelProperty<? super I> property2,
			ModelProperty<I> declaredProperty) throws PropertyClashException {
		String contradiction = property1.contradicts(property2, declaredProperty);
		if (contradiction != null) {
			throw new PropertyClashException("Property '" + property1.getPropertyIdentifier() + "' contradiction between entity '"
					+ property1.getModelEntity() + "' and entity '" + property2.getModelEntity() + "'.\nReason:" + contradiction);
		}
	}

	protected boolean declaresModelProperty(String propertyIdentifier) {
		for (Method m : getImplementedInterface().getDeclaredMethods()) {
			Getter aGetter = m.getAnnotation(Getter.class);
			if (aGetter != null && aGetter.value().equals(propertyIdentifier)) {
				return true;
			}
			Setter aSetter = m.getAnnotation(Setter.class);
			if (aSetter != null && aSetter.value().equals(propertyIdentifier)) {
				return true;
			}
			Adder anAdder = m.getAnnotation(Adder.class);
			if (anAdder != null && anAdder.value().equals(propertyIdentifier)) {
				return true;
			}
			Remover aRemover = m.getAnnotation(Remover.class);
			if (aRemover != null && aRemover.value().equals(propertyIdentifier)) {
				return true;
			}
		}
		return false;
	}

	public Class<I> getImplementedInterface() {
		return implementedInterface;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public ImplementationClass getImplementationClass() {
		return implementationClass;
	}

	public XMLElement getXMLElement() {
		return xmlElement;
	}

	public String getXMLTag() {
		if (xmlTag == null) {
			if (xmlElement != null) {
				xmlTag = xmlElement.xmlTag();
			}
			if (xmlTag == null || xmlTag.equals(XMLElement.DEFAULT_XML_TAG)) {
				xmlTag = getImplementedInterface().getSimpleName();
			}
		}
		return xmlTag;
	}

	public Iterator<ModelProperty<? super I>> getProperties() throws ModelDefinitionException {
		return properties.values().iterator();
	}

	public int getPropertiesSize() {
		return properties.size();
	}

	@Override
	public String toString() {
		return "ModelEntity[" + getImplementedInterface().getSimpleName() + "]";
	}

	public List<ModelEntity> getAllDescendantsAndMe(ModelContext modelContext) throws ModelDefinitionException {
		List<ModelEntity> returned = getAllDescendants(modelContext);
		returned.add(this);
		return returned;
	}

	public List<ModelEntity> getAllDescendants(ModelContext modelContext) throws ModelDefinitionException {
		List<ModelEntity> returned = new ArrayList<ModelEntity>();
		Iterator<ModelEntity> i = modelContext.getEntities();
		while (i.hasNext()) {
			ModelEntity entity = i.next();
			if (isAncestorOf(entity)) {
				returned.add(entity);
			}
		}
		return returned;
	}

	public boolean isAncestorOf(ModelEntity<?> entity) throws ModelDefinitionException {
		if (entity == null) {
			return false;
		}
		if (entity.getDirectSuperEntities() != null) {
			for (ModelEntity<?> e : entity.getDirectSuperEntities()) {
				if (e == this) {
					return true;
				} else if (isAncestorOf(e)) {
					return true;
				}
			}
		}
		return false;
	}

	private Boolean hasInitializers;

	public boolean hasInitializers() throws ModelDefinitionException {
		if (hasInitializers == null) {
			if (initializers.size() > 0) {
				return hasInitializers = true;
			} else if (getDirectSuperEntities() != null) {
				for (ModelEntity<?> e : getDirectSuperEntities()) {
					if (e.hasInitializers()) {
						return hasInitializers = true;
					}
				}
			}
			return hasInitializers = false;
		}
		return hasInitializers;
	}

	public ModelInitializer getInitializers(Method m) throws ModelDefinitionException {
		if (m.getDeclaringClass() != implementedInterface) {
			ModelEntity<?> e = ModelEntityLibrary.get(m.getDeclaringClass());
			if (e == null) {
				throw new ModelExecutionException("Could not find initializer for method " + m.toGenericString() + ". Make sure that "
						+ m.getDeclaringClass().getName() + " is annotated with ModelEntity and has been imported.");
			}
			return e.getInitializers(m);
		}
		return initializers.get(m);
	}

	public ModelInitializer getInitializerForArgs(Class<?>[] types) throws ModelDefinitionException {
		List<ModelInitializer> list = getPossibleInitializers(types);
		if (list.size() == 0) {
			if (initializers.size() > 0) {
				return null;
			}
			ModelInitializer found = null;
			if (getDirectSuperEntities() != null) {
				for (ModelEntity<? super I> e : getDirectSuperEntities()) {
					ModelInitializer initializer = e.getInitializerForArgs(types);
					if (found == null) {
						found = initializer;
					} else {
						throw new ModelDefinitionException("Initializer clash: " + found.getInitializingMethod().toGenericString()
								+ " cannot be distinguished with " + initializer.getInitializingMethod().toGenericString()
								+ ". Please override initializer in " + getImplementedInterface());
					}

				}
			}
			return found;
		}
		return list.get(0);
	}

	public List<ModelInitializer> getPossibleInitializers(Class<?>[] types) {
		List<ModelInitializer> list = new ArrayList<ModelInitializer>();
		for (ModelInitializer init : initializers.values()) {
			int i = 0;
			Class<?>[] parameterTypes = init.getInitializingMethod().getParameterTypes();
			boolean ok = parameterTypes.length == types.length;
			if (ok) {
				for (Class<?> c : parameterTypes) {
					if (types[i] != null && !c.isAssignableFrom(types[i])) {
						ok = false;
						break;
					}
					i++;
				}
				if (ok) {
					list.add(init);
				}
			}
		}
		return list;
	}

	/**
	 * Return the first found deserialization initializer in the class hierarchy<br>
	 * TODO: manage multiple inheritance issues
	 * 
	 * @return
	 * @throws ModelDefinitionException
	 */
	// TODO: manage multiple inheritance issues
	public DeserializationInitializer getDeserializationInitializer() throws ModelDefinitionException {
		if (deserializationInitializer == null) {
			if (getDirectSuperEntities() != null) {
				for (ModelEntity<?> e : getDirectSuperEntities()) {
					if (e.getDeserializationInitializer() != null) {
						return e.getDeserializationInitializer();
					}
				}
			}
		}
		return deserializationInitializer;
	}

	/**
	 * Return the first found deserialization finalizer in the class hierarchy<br>
	 * TODO: manage multiple inheritance issues
	 * 
	 * @return
	 * @throws ModelDefinitionException
	 */
	// TODO: manage multiple inheritance issues
	public DeserializationFinalizer getDeserializationFinalizer() throws ModelDefinitionException {
		if (deserializationFinalizer == null) {
			if (getDirectSuperEntities() != null) {
				for (ModelEntity<?> e : getDirectSuperEntities()) {
					if (e.getDeserializationFinalizer() != null) {
						return e.getDeserializationFinalizer();
					}
				}
			}
		}
		return deserializationFinalizer;
	}

	/**
	 * Returns the list of model properties of this model entity which are of the type provided by <code>type</code> or any of its
	 * compatible type (ie, a super-type of <code>type</code>).
	 * 
	 * @param type
	 *            the type used for model properties lookup
	 * @return a list of model properties to which an instance of <code>type</code> can be assigned or added
	 */
	public Collection<ModelProperty<? super I>> getPropertiesAssignableFrom(Class<?> type) {
		Collection<ModelProperty<? super I>> ppProperties = new ArrayList<ModelProperty<? super I>>();
		for (ModelProperty<? super I> p : properties.values()) {
			if (TypeUtils.isTypeAssignableFrom(p.getType(), type)) {
				ppProperties.add(p);
			}
		}
		return ppProperties;
	}

	public Modify getModify() throws ModelDefinitionException {
		if (modify != null) {
			return modify;
		} else {
			if (getDirectSuperEntities() != null) {
				for (ModelEntity<? super I> e : getDirectSuperEntities()) {
					if (e.getModify() != null) {
						if (modify == null) {
							modify = e.getModify();
						} else {
							throw new ModelDefinitionException("Duplicated modify annotation on " + this
									+ ". Please add modify annotation on " + implementedInterface.getName());
						}
					}
				}
			}
		}
		return null;
	}

	public Finder getFinder(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean isModelEntity(Class<?> type) {
		return type.isAnnotationPresent(org.openflexo.model.annotations.ModelEntity.class);
	}
}
