package org.openflexo.model.factory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Modify;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.StringConverter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.exceptions.PropertyClashException;
import org.openflexo.model.xml.DefaultStringEncoder.Converter;

/**
 * This class represents an instance of the {@link org.openflexo.model.annotations.ModelEntity} annotation declared on an interface.
 * 
 * @author Guillaume
 * 
 * @param <I>
 */
public class ModelEntity<I> extends ProxyFactory {

	/**
	 * The model factory in which this entity is declared
	 */
	private ModelFactory modelFactory;

	/**
	 * The implemented interface corresponding to this model entity
	 */
	private Class<I> implementedInterface;

	/**
	 * The model entity annotation describing this entity
	 */
	private org.openflexo.model.annotations.ModelEntity entityAnnotation;

	/**
	 * The implementationClass associated with this model entity
	 */
	private ImplementationClass implementationClass;

	/**
	 * The {@link XMLElement} annotation, if any
	 */
	private XMLElement xmlElement;

	private Map<String, ModelProperty<? super I>> properties;

	private String xmlTag;

	private Modify modify;
	private boolean isAbstract;

	private Class<?> implementingClass;

	private List<Class<? super I>> superImplementedInterfaces;
	private List<ModelEntity<? super I>> allSuperEntities;
	private List<ModelEntity<? super I>> directSuperEntities;
	private Map<Method, ModelInitializer> initializers;

	protected ModelEntity(Class<I> implementedInterface, ModelFactory modelFactory) throws ModelDefinitionException {
		// System.out.println("CREATED ModelEntity for "+implementedInterface.getSimpleName());

		// declaredModelProperties = new Hashtable<String, ModelProperty<I>>();
		properties = new HashMap<String, ModelProperty<? super I>>();
		this.initializers = new HashMap<Method, ModelInitializer>();
		this.modelFactory = modelFactory;
		this.implementedInterface = implementedInterface;
		entityAnnotation = implementedInterface.getAnnotation(org.openflexo.model.annotations.ModelEntity.class);
		implementationClass = implementedInterface.getAnnotation(ImplementationClass.class);
		xmlElement = implementedInterface.getAnnotation(XMLElement.class);
		modify = implementedInterface.getAnnotation(Modify.class);
		this.isAbstract = entityAnnotation.isAbstract();
		// We resolve here the model super interface
		// The corresponding model entity MUST be resolved later
		for (Class<?> i : implementedInterface.getInterfaces()) {
			if (i.isAnnotationPresent(org.openflexo.model.annotations.ModelEntity.class)) {
				if (this.superImplementedInterfaces == null) {
					this.superImplementedInterfaces = new ArrayList<Class<? super I>>();
				}
				this.superImplementedInterfaces.add((Class<? super I>) i);
			}
		}
		setFilter(new MethodFilter() {
			@Override
			public boolean isHandled(Method method) {
				// System.out.println("isHandled for "+method+" in "+method.getDeclaringClass());
				/*
				 * return method.getAnnotation(Getter.class) != null ||
				 * method.getAnnotation(Setter.class) != null ||
				 * method.getAnnotation(Adder.class) != null ||
				 * method.getAnnotation(Remover.class) != null;
				 */

				return Modifier.isAbstract(method.getModifiers()) || method.getName().equals("toString")
						&& method.getParameterTypes().length == 0 && method.getDeclaringClass() == Object.class;
			}
		});
		// methodHandler = new ProxyMethodHander(this);

	}

	protected void init() throws ModelDefinitionException {
		setSuperclass(findValidSuperClass());
		Class<?>[] interfaces = { implementedInterface };
		setInterfaces(interfaces);
		exploreEntity();
	}

	protected void exploreEntity() throws ModelDefinitionException {

		// 1. Scan for converters
		for (Field field : getImplementedInterface().getDeclaredFields()) {
			StringConverter converter = field.getAnnotation(StringConverter.class);
			if (converter != null) {
				try {
					modelFactory.addConverter((Converter<?>) field.get(null));
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

		// 2. Scan for declared properties
		for (Method m : getImplementedInterface().getDeclaredMethods()) {
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
			if (propertyIdentifier != null) {
				// The next line creates the property
				getModelProperty(propertyIdentifier);
			}
			org.openflexo.model.annotations.Initializer initializer = m.getAnnotation(org.openflexo.model.annotations.Initializer.class);
			if (initializer != null) {
				initializers.put(m, new ModelInitializer(initializer, m));
			}
		}

		// 3. Scan for inherited properties (we only scan direct parent properties, since themselves will scan for their inherited parents)
		if (getDirectSuperEntities() != null) {
			for (ModelEntity<? super I> parentEntity : getDirectSuperEntities()) {
				for (ModelProperty<? super I> property : parentEntity.properties.values()) {
					getModelProperty(property.getPropertyIdentifier());
				}
			}
		}

		// 4. Validate initializers
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

		for (ModelProperty<? super I> property : properties.values()) {
			if (!property.getType().isPrimitive() && !getModelFactory().isStringConvertable(property.getType())) {
				getModelFactory().importClass(property.getType());
			}
		}
	}

	private Class<?> findValidSuperClass() throws ModelDefinitionException {
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
					Class<?> klass = e.findValidSuperClass();
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
				if (implementingClass == null) {
					implementingClass = modelFactory.getDefaultModelClass();
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
				ModelEntity<? super I> superEntity = getModelFactory().getModelEntity(superInterface);
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

	public boolean hasProperty(ModelProperty<?> modelProperty) {
		return properties.containsValue(modelProperty);
	}

	/**
	 * Returns whether this entity or any of its super entity contains a method annotated with the {@link Getter} annotation and with the
	 * identifier <code>propertyIdentifier</code>. The {@link Getter} annotation resulting in the declaration of a property.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @return true if any methods is annotated with the {@link Getter} annotation
	 * @throws ModelDefinitionException
	 */
	private boolean propertyExists(String propertyIdentifier) throws ModelDefinitionException {
		if (properties.get(propertyIdentifier) != null) {
			return true;
		}
		if (declaresProperty(propertyIdentifier)) {
			return true;
		}
		if (getDirectSuperEntities() != null) {
			for (ModelEntity<?> e : getDirectSuperEntities()) {
				if (e.propertyExists(propertyIdentifier)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether the implemented interface associated with <code>this</code> model entity has a method annotated with a {@link Getter}
	 * with its value set to the provided <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 * @return
	 */
	private boolean declaresProperty(String propertyIdentifier) {
		for (Method m : implementedInterface.getMethods()) {
			if (m.isAnnotationPresent(Getter.class) && m.getAnnotation(Getter.class).value().equals(propertyIdentifier)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns and create, if it does not exist yet, the {@link ModelProperty} with the identifier <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @return the property with the identifier <code>propertyIdentifier</code>.
	 * @throws ModelDefinitionException
	 */
	public ModelProperty<? super I> getModelProperty(String propertyIdentifier) throws ModelDefinitionException {
		ModelProperty<? super I> returned = properties.get(propertyIdentifier);
		if (returned == null && !properties.containsKey(propertyIdentifier) && propertyExists(propertyIdentifier)) {
			returned = buildModelProperty(propertyIdentifier);
			// Validation should only occur in the end, when the all graph has been merged. When building the merged model property
			// it can go through a set of invalid model properties
			returned.validate();
			properties.put(propertyIdentifier, returned);
		}
		return returned;
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
		ModelProperty<I> property = null;
		if (propertyExists(propertyIdentifier)) {
			property = ModelProperty.getModelProperty(propertyIdentifier, this);
		} else {
			throw new ModelExecutionException("There is no such property: '" + propertyIdentifier + "'");
		}
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
			if (!parent.propertyExists(propertyIdentifier)) {
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
	private ModelProperty<? super I> combine(ModelProperty<I> property, ModelProperty<? super I> parentProperty) {
		return property.combineWith(parentProperty, property);
	}

	private ModelProperty<? super I> combineAsAncestors(ModelProperty<? super I> property1, ModelProperty<? super I> property2,
			ModelProperty<I> declaredProperty) throws PropertyClashException {
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

	public I newInstance(Object... args) throws IllegalArgumentException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, ModelDefinitionException {
		if (isAbstract()) {
			throw new InstantiationException("Interface " + implementedInterface.getName()
					+ " is declared as an abstract entity, cannot instantiate it");
		}
		ProxyMethodHandler<I> handler = new ProxyMethodHandler<I>(this);
		I returned = (I) create(new Class<?>[0], new Object[0], handler);
		handler.setObject(returned);
		if (args == null) {
			args = new Object[0];
		}
		if (args.length > 0 || hasInitializers()) {
			Class<?>[] types = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				Object o = args[i];
				if (getModelFactory().isProxyObject(o)) {
					ModelEntity<?> modelEntity = getModelFactory().getModelEntity(o);
					types[i] = modelEntity.getImplementedInterface();
				} else {
					types[i] = args[i].getClass();
				}
			}
			ModelInitializer initializerForArgs = getInitializerForArgs(types);
			if (initializerForArgs == null && args.length > 0) {
				StringBuilder sb = new StringBuilder();
				for (Class<?> c : types) {
					if (sb.length() > 0) {
						sb.append(',');
					}
					sb.append(c.getName());

				}
				throw new NoSuchMethodException("Could not find any initializer with args " + sb.toString());
			}
			if (initializerForArgs != null) {
				initializerForArgs.getInitializingMethod().invoke(returned, args);
			}
		}
		return returned;
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
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

	public List<ModelEntity> getAllDescendantsAndMe() throws ModelDefinitionException {
		List<ModelEntity> returned = getAllDescendants();
		returned.add(this);
		return returned;
	}

	public List<ModelEntity> getAllDescendants() throws ModelDefinitionException {
		List<ModelEntity> returned = new ArrayList<ModelEntity>();
		Iterator<ModelEntity> i = getModelFactory().getEntities();
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
				if (entity == this) {
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
			ModelEntity<?> e = getModelFactory().getModelEntity(m.getDeclaringClass());
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
					if (!c.isAssignableFrom(types[i])) {
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

}
