package org.openflexo.model.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Deleter;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.PastingPoints;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.StringConverter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.xml.DefaultStringEncoder.Converter;

public class ModelEntity<I> extends ProxyFactory {

	private ModelFactory modelFactory;
	private Class<I> implementedInterface;
	private org.openflexo.model.annotations.ModelEntity entityAnnotation;
	private ImplementationClass implementationClass;
	private XMLElement xmlElement;
	private Constructor[] constructors;
	private PastingPoints pastingPoints;
	// private ProxyMethodHander methodHandler;
	private Hashtable<String, ModelProperty<I>> declaredModelProperties;
	private List<ModelProperty<? super I>> allProperties;
	private String xmlTag;

	private ModelDeleter<I> deleter;
	private boolean isAbstract;

	private Class<? super I> superImplementedInterface;
	private ModelEntity<? super I> superEntity;

	protected ModelEntity(Class<I> implementedInterface, ModelFactory modelFactory) throws ModelDefinitionException {
		// System.out.println("CREATED ModelEntity for "+implementedInterface.getSimpleName());

		declaredModelProperties = new Hashtable<String, ModelProperty<I>>();

		this.modelFactory = modelFactory;
		this.implementedInterface = implementedInterface;
		entityAnnotation = implementedInterface.getAnnotation(org.openflexo.model.annotations.ModelEntity.class);
		implementationClass = implementedInterface.getAnnotation(ImplementationClass.class);
		xmlElement = implementedInterface.getAnnotation(XMLElement.class);
		pastingPoints = implementedInterface.getAnnotation(PastingPoints.class);
		this.isAbstract = entityAnnotation.isAbstract();
		// We resolve here the model super interface
		// The corresponding model entity MUST be resolved later
		for (Class<?> i : implementedInterface.getInterfaces()) {
			if (i.isAnnotationPresent(org.openflexo.model.annotations.ModelEntity.class)) {
				superImplementedInterface = (Class<? super I>) i;
				break;
			}
		}
		setFilter(new MethodFilter() {
			@Override
			public boolean isHandled(Method method) {
				// System.out.println("isHandled for "+method+" in "+method.getDeclaringClass());
				/*return method.getAnnotation(Getter.class) != null
				|| method.getAnnotation(Setter.class) != null
				|| method.getAnnotation(Adder.class) != null
				|| method.getAnnotation(Remover.class) != null;*/

				return Modifier.isAbstract(method.getModifiers()) || method.getName().equals("toString")
						&& method.getParameterTypes().length == 0;
			}
		});
		// methodHandler = new ProxyMethodHander(this);

	}

	protected void init() throws ModelDefinitionException {
		setSuperclass(findValidSuperClass());
		Class<?>[] interfaces = { implementedInterface };
		setInterfaces(interfaces);
		exploreEntity();
		validateEntity();
	}

	private void validateEntity() throws ModelDefinitionException {
		if (getDeclaredDeleter() != null && !getDeclaredDeleter().getDeleter().deletedProperty().equals(Deleter.UNDEFINED)) {
			ModelProperty<? super I> deletedProperty = getModelProperty(getDeclaredDeleter().getDeleter().deletedProperty());
			if (deletedProperty == null) {
				throw new ModelDefinitionException("Interface " + getImplementedInterface().getName()
						+ " declares a deleter but the associated deletedProperty (" + getDeclaredDeleter().getDeleter().deletedProperty()
						+ ") is not declared in hierarchy");
			}
			if (!TypeUtils.isBoolean(deletedProperty.getType())) {
				throw new ModelDefinitionException("Interface " + getImplementedInterface().getName()
						+ " declares a deleter but the associated deletedProperty (" + getDeclaredDeleter().getDeleter().deletedProperty()
						+ ") is not of type boolean/Boolean");
			}
		}
	}

	private Class findValidSuperClass() throws ModelDefinitionException {
		if (implementationClass != null) {
			return implementationClass.value();
		} else {
			if (getSuperEntity() != null) {
				return getSuperEntity().findValidSuperClass();
			} else {
				return modelFactory.getDefaultModelClass();
			}
		}
	}

	// Guillaume: this method is deprecated because we are considering to allow multiple-inheritance within the model
	// Therefore, we should try to limit the number of invokers of this method to reduce the refactoring that will occur when PAMELA
	// will support multiple-inheritance.
	@Deprecated
	public ModelEntity<? super I> getSuperEntity() throws ModelDefinitionException {
		if (superEntity == null) {
			if (superImplementedInterface != null) {
				superEntity = modelFactory.getModelEntity(superImplementedInterface);
			} else {
				return null;
			}
		}
		return superEntity;
	}

	public ModelProperty<I> getDeclaredModelProperty(String propertyIdentifier) throws ModelDefinitionException {
		ModelProperty<I> returned = declaredModelProperties.get(propertyIdentifier);
		if (returned == null && declaresModelProperty(propertyIdentifier)) {
			returned = new ModelProperty<I>(propertyIdentifier, this);
			declaredModelProperties.put(propertyIdentifier, returned);
		}
		return returned;
	}

	public ModelProperty<? super I> getModelProperty(String propertyIdentifier) throws ModelDefinitionException {
		ModelProperty<I> returned = getDeclaredModelProperty(propertyIdentifier);
		if (returned == null && getSuperEntity() != null) {
			return getSuperEntity().getModelProperty(propertyIdentifier);
		}
		return returned;
	}

	public ModelDeleter<I> getDeclaredDeleter() {
		return deleter;
	}

	public ModelDeleter<? super I> getModelDeleter() throws ModelDefinitionException {
		if (deleter != null) {
			return deleter;
		}
		if (getSuperEntity() != null) {
			return getSuperEntity().getModelDeleter();
		}
		return null;
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
			if (anAdder != null && anAdder.id().equals(propertyIdentifier)) {
				return true;
			}
			Remover aRemover = m.getAnnotation(Remover.class);
			if (aRemover != null && aRemover.id().equals(propertyIdentifier)) {
				return true;
			}
		}
		return false;
	}

	protected boolean declaresModelDeleter() {
		return deleter != null;
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
		if (args != null && args.length > 0) {
			Class[] types = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				Object o = args[i];
				if (getModelFactory().isProxyObject(o)) {
					ModelEntity modelEntity = getModelFactory().getModelEntity(o);
					types[i] = modelEntity.getImplementedInterface();
				} else {
					types[i] = args[i].getClass();
				}
			}
		}
		return returned;
	}

	protected PastingPoint retrievePastingPoint(Class type) throws ModelDefinitionException {
		if (pastingPoints != null) {
			for (PastingPoint pp : pastingPoints.value()) {
				if (TypeUtils.isTypeAssignableFrom(pp.type(), type)) {
					return pp;
				}
			}
		}
		ModelEntity<? super I> superModelEntity = getSuperEntity();
		if (superModelEntity != null) {
			return superModelEntity.retrievePastingPoint(type);
		}
		return null;
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

	public int getDeclaredPropertiesSize() {
		return declaredModelProperties.size();
	}

	public Iterator<ModelProperty<I>> getDeclaredProperties() {
		return declaredModelProperties.values().iterator();
	}

	public Iterator<ModelProperty<? super I>> getProperties() throws ModelDefinitionException {
		if (allProperties == null) {
			allProperties = new ArrayList<ModelProperty<? super I>>();
			ModelEntity<? super I> current = this;
			while (current != null) {
				allProperties.addAll(current.declaredModelProperties.values());
				current = current.getSuperEntity();
			}
		}
		return allProperties.iterator();
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

	public boolean isAncestorOf(ModelEntity entity) throws ModelDefinitionException {
		if (entity == null) {
			return false;
		}
		if (entity.getSuperEntity() == this) {
			return true;
		}
		ModelEntity parent = entity.getSuperEntity();
		if (parent != null) {
			return isAncestorOf(parent);
		}
		return false;
	}

	protected void exploreEntity() throws ModelDefinitionException {
		// System.out.println("Explore properties for "+implementedInterface);
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
						propertyIdentifier = anAdder.id();
					} else {
						Remover aRemover = m.getAnnotation(Remover.class);
						if (aRemover != null) {
							propertyIdentifier = aRemover.id();
						}
					}
				}
			}
			if (propertyIdentifier != null) {
				getModelProperty(propertyIdentifier);
			}
			Deleter aDeleter = m.getAnnotation(Deleter.class);
			if (aDeleter != null) {
				if (deleter == null) {
					deleter = new ModelDeleter<I>(this, m);
				} else {
					throw new ModelDefinitionException("Duplicate deleters " + deleter.getDeleterMethod() + " and " + m);
				}
			}
		}
		for (ModelProperty<I> property : declaredModelProperties.values()) {
			getModelFactory().importClass(property.getType());
		}
	}

}
