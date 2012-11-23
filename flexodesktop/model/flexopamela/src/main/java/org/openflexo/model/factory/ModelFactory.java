package org.openflexo.model.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.xml.DefaultStringEncoder;
import org.openflexo.model.xml.DefaultStringEncoder.Converter;

public class ModelFactory {

	private Class<?> defaultModelClass = Object.class;
	private Class<? extends List> listImplementationClass = Vector.class;
	private Class<? extends Map> mapImplementationClass = Hashtable.class;

	private Map<Class, ModelEntity> modelEntities;
	private Map<Class, PAMELAProxyFactory> proxyFactories;
	private Map<String, ModelEntity> modelEntitiesByXmlTag;

	private DefaultStringEncoder stringEncoder;

	public class PAMELAProxyFactory<I> extends ProxyFactory {
		private final ModelEntity<I> modelEntity;

		public PAMELAProxyFactory(ModelEntity<I> modelEntity) throws ModelDefinitionException {
			super();
			this.modelEntity = modelEntity;
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
			Class<?> implementingClass = modelEntity.getImplementingClass();
			if (implementingClass == null) {
				implementingClass = defaultModelClass;
			}
			setSuperclass(implementingClass);
			Class<?>[] interfaces = { modelEntity.getImplementedInterface() };
			setInterfaces(interfaces);
		}

		public I newInstance(Object... args) throws IllegalArgumentException, NoSuchMethodException, InstantiationException,
				IllegalAccessException, InvocationTargetException, ModelDefinitionException {
			if (modelEntity.isAbstract()) {
				throw new InstantiationException(modelEntity + " is declared as an abstract entity, cannot instantiate it");
			}
			ProxyMethodHandler<I> handler = new ProxyMethodHandler<I>(modelEntity);
			I returned = (I) create(new Class<?>[0], new Object[0], handler);
			handler.setObject(returned);
			if (args == null) {
				args = new Object[0];
			}
			if (args.length > 0 || modelEntity.hasInitializers()) {
				Class<?>[] types = new Class<?>[args.length];
				for (int i = 0; i < args.length; i++) {
					Object o = args[i];
					if (isProxyObject(o)) {
						ModelEntity<?> modelEntity = getModelEntity(o);
						types[i] = modelEntity.getImplementedInterface();
					} else {
						types[i] = o != null ? o.getClass() : null;
					}
				}
				ModelInitializer initializerForArgs = modelEntity.getInitializerForArgs(types);
				if (initializerForArgs != null) {
					initializerForArgs.getInitializingMethod().invoke(returned, args);
				} else {
					if (args.length > 0) {
						StringBuilder sb = new StringBuilder();
						for (Class<?> c : types) {
							if (sb.length() > 0) {
								sb.append(',');
							}
							sb.append(c.getName());

						}
						throw new NoSuchMethodException("Could not find any initializer with args " + sb.toString());
					}
				}
			}
			return returned;
		}
	}

	public ModelFactory() {
		modelEntities = new HashMap<Class, ModelEntity>();
		modelEntitiesByXmlTag = new HashMap<String, ModelEntity>();
		proxyFactories = new HashMap<Class, PAMELAProxyFactory>();
		stringEncoder = new DefaultStringEncoder(this);
	}

	public <I> I newInstance(ModelEntity<I> modelEntity) {
		return newInstance(modelEntity, (Object[]) null);
	}

	public <I> I newInstance(ModelEntity<I> modelEntity, Object... args) {
		return newInstance(modelEntity.getImplementedInterface(), args);
	}

	public <I> I newInstance(Class<I> implementedInterface) {
		return newInstance(implementedInterface, (Object[]) null);
	}

	public <I> I newInstance(Class<I> implementedInterface, Object... args) {
		try {
			PAMELAProxyFactory<I> entity = proxyFactories.get(implementedInterface);
			if (entity != null) {
				return entity.newInstance(args);
			} else {
				throw new ModelExecutionException("Unknown entity '" + implementedInterface.getName()
						+ "'! Did you forget to import it or to annotated it with @ModelEntity?");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new ModelExecutionException(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new ModelExecutionException(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new ModelExecutionException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new ModelExecutionException(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new ModelExecutionException(e);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new ModelExecutionException(e);
		}
	}

	public <T> ModelEntity<T> getModelEntity(Class<T> implementedInterface) throws ModelDefinitionException {
		ModelEntity<T> returned = modelEntities.get(implementedInterface);
		if (returned == null && implementedInterface.getAnnotation(org.openflexo.model.annotations.ModelEntity.class) != null) {
			modelEntities.put(implementedInterface, returned = new ModelEntity<T>(implementedInterface, this));
			ModelEntity<?> put = modelEntitiesByXmlTag.put(returned.getXMLTag(), returned);
			if (put != null) {
				throw new ModelDefinitionException("Two entities define the same XMLTag '" + returned.getXMLTag()
						+ "'. Implemented interfaces: " + returned.getImplementedInterface().getName() + " "
						+ put.getImplementedInterface().getName());
			}
			returned.init();
			proxyFactories.put(implementedInterface, new PAMELAProxyFactory(returned));
		}
		return returned;
	}

	public ModelEntity<?> getModelEntity(String xmlElementName) {
		return modelEntitiesByXmlTag.get(xmlElementName);
	}

	public Iterator<ModelEntity> getEntities() {
		return modelEntities.values().iterator();
	}

	public int getEntityCount() {
		return modelEntities.size();
	}

	public Class<?> getDefaultModelClass() {
		return defaultModelClass;
	}

	public void setDefaultModelClass(Class<?> defaultModelClass) {
		Class<?> old = defaultModelClass;
		this.defaultModelClass = defaultModelClass;
		for (PAMELAProxyFactory<?> factory : proxyFactories.values()) {
			if (factory.getSuperclass() == old) {
				factory.setSuperclass(defaultModelClass);
			}
		}
	}

	public <I> void setImplementingClassForInterface(Class<? extends I> klass, Class<I> interfaceClass) {
		proxyFactories.get(interfaceClass).setSuperclass(klass);
	}

	public <I> void setImplementingClassForEntity(Class<? extends I> klass, ModelEntity<I> entity) {
		setImplementingClassForInterface(klass, entity.getImplementedInterface());
	}

	public Class<? extends List> getListImplementationClass() {
		return listImplementationClass;
	}

	public void setListImplementationClass(Class<? extends List> listImplementationClass) {
		this.listImplementationClass = listImplementationClass;
	}

	public Class<? extends Map> getMapImplementationClass() {
		return mapImplementationClass;
	}

	public void setMapImplementationClass(Class<? extends Map> mapImplementationClass) {
		this.mapImplementationClass = mapImplementationClass;
	}

	public boolean isModelEntity(Class<?> c) {
		return c.getAnnotation(org.openflexo.model.annotations.ModelEntity.class) != null;
	}

	public boolean isStringConvertable(Class<?> c) {
		return getStringEncoder().isConvertable(c);
	}

	public boolean isProxyObject(Object object) {
		return object instanceof ProxyObject;
	}

	public <I> ModelEntity<I> getModelEntity(I object) {
		ProxyMethodHandler<I> handler = getHandler(object);
		if (handler != null) {
			return handler.getModelEntity();
		}
		return null;
	}

	public <I> ProxyMethodHandler<I> getHandler(I object) {
		if (object instanceof ProxyObject) {
			return (ProxyMethodHandler<I>) ((ProxyObject) object).getHandler();
		}
		return null;
	}

	public DefaultStringEncoder getStringEncoder() {
		return stringEncoder;
	}

	public void addConverter(Converter<?> converter) {
		stringEncoder.addConverter(converter);
	}

	public <I> ModelFactory importClass(Class<I> type) throws ModelDefinitionException {
		ModelEntity<I> modelEntity = getModelEntity(type);// Performs the import
		if (modelEntity == null) {
			throw new ModelDefinitionException("Type " + type.getName() + " is not a model entity. Did you forgot to annotated it with @"
					+ org.openflexo.model.annotations.ModelEntity.class.getSimpleName());
		}
		Imports imports = type.getAnnotation(Imports.class);
		if (imports != null) {
			for (Import imp : imports.value()) {
				importClass(imp.value());
			}
		}
		return this;
	}

	public String debug() {
		StringBuffer returned = new StringBuffer();
		returned.append("*************** ModelFactory ****************\n");
		returned.append("Entities number: " + modelEntities.size() + "\n");
		returned.append("StringEncoder: " + stringEncoder + "\n");
		returned.append("listImplementationClass: " + listImplementationClass + "\n");
		returned.append("mapImplementationClass: " + mapImplementationClass + "\n");
		for (ModelEntity entity : modelEntities.values()) {
			returned.append("------------------- ").append(entity.getImplementedInterface().getSimpleName())
					.append(" -------------------\n");
			Iterator<ModelProperty> i;
			try {
				i = entity.getProperties();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
				continue;
			}
			while (i.hasNext()) {
				ModelProperty property = i.next();
				returned.append(property.getPropertyIdentifier()).append(" ").append(property.getCardinality()).append(" type=")
						.append(property.getType().getSimpleName()).append("\n");
			}
		}
		return returned.toString();
	}

	public boolean isEmbedddedIn(Object parentObject, Object childObject, EmbeddingType embeddingType) {
		return getEmbeddedObjects(parentObject, embeddingType).contains(childObject);
	}

	public boolean isEmbedddedIn(Object parentObject, Object childObject, EmbeddingType embeddingType, Object... context) {
		return getEmbeddedObjects(parentObject, embeddingType, context).contains(childObject);
	}

	/**
	 * Build and return a List of embedded objects, using meta informations contained in related class All property should be annotated with
	 * a @Embedded annotation which determine the way of handling this property
	 * 
	 * Supplied context is used to determine the closure of objects graph being constructed during this operation.
	 * 
	 * @param root
	 * @param context
	 * @return
	 */
	public List<Object> getEmbeddedObjects(Object root, EmbeddingType embeddingType, Object... context) {
		if (!isProxyObject(root)) {
			return null;
		}

		List<Object> derivedObjectsFromContext = new ArrayList<Object>();
		if (context != null && context.length > 0) {
			for (Object o : context) {
				derivedObjectsFromContext.add(o);
				derivedObjectsFromContext.addAll(getEmbeddedObjects(o, embeddingType));
			}
		}

		List<Object> returned = new ArrayList<Object>();
		try {
			appendEmbeddedObjects(root, returned, embeddingType);
		} catch (ModelDefinitionException e) {
			throw new ModelExecutionException(e);
		}
		List<Object> discardedObjects = new ArrayList<Object>();
		for (int i = 0; i < returned.size(); i++) {
			Object o = returned.get(i);
			if (o instanceof ConditionalPresence) {
				boolean allOthersArePresent = true;
				for (Object other : ((ConditionalPresence) o).requiredPresence) {
					if (!returned.contains(other) && !derivedObjectsFromContext.contains(other)) {
						allOthersArePresent = false;
						break;
					}
				}
				if (allOthersArePresent && !returned.contains(((ConditionalPresence) o).object)) {
					// Closure is fine and object is not already present, add object
					returned.set(i, ((ConditionalPresence) o).object);
				} else {
					// Discard object
					discardedObjects.add(o);
				}
			}
		}
		for (Object o : discardedObjects) {
			returned.remove(o);
		}
		return returned;
	}

	private class ConditionalPresence {
		private Object object;
		private List<Object> requiredPresence;

		public ConditionalPresence(Object object, List<Object> requiredPresence) {
			super();
			this.object = object;
			this.requiredPresence = requiredPresence;
		}
	}

	private void appendEmbedded(ModelProperty p, Object father, List<Object> list, Object child, EmbeddingType embeddingType)
			throws ModelDefinitionException {
		if (!isProxyObject(child)) {
			return;
		}

		if (p.getEmbedded() == null && p.getComplexEmbedded() == null) {
			// this property is not embedded
			return;
		}

		boolean append = false;
		switch (embeddingType) {
		case CLOSURE:
			append = p.getEmbedded() != null && p.getEmbedded().closureConditions().length == 0 || p.getComplexEmbedded() != null
					&& p.getComplexEmbedded().closureConditions().length == 0;
			break;
		case DELETION:
			append = p.getEmbedded() != null && p.getEmbedded().deletionConditions().length == 0 || p.getComplexEmbedded() != null
					&& p.getComplexEmbedded().deletionConditions().length == 0;
			break;
		}

		if (append) {
			// There is no condition, just append it
			if (!list.contains(child)) {
				list.add(child);
				// System.out.println("Embedded in "+father+" because of "+p+" : "+child);
				appendEmbeddedObjects(child, list, embeddingType);
			}
		} else {
			List<Object> requiredPresence = new ArrayList<Object>();
			if (p.getEmbedded() != null) {
				switch (embeddingType) {
				case CLOSURE:
					for (String c : p.getEmbedded().closureConditions()) {
						ModelEntity closureConditionEntity = getModelEntity(child);
						ModelProperty closureConditionProperty = getModelEntity(child).getModelProperty(c);
						Object closureConditionRequiredObject = getHandler(child).invokeGetter(closureConditionProperty);
						if (closureConditionRequiredObject != null) {
							requiredPresence.add(closureConditionRequiredObject);
						}
					}
					break;
				case DELETION:
					for (String c : p.getEmbedded().deletionConditions()) {
						ModelEntity deletionConditionEntity = getModelEntity(child);
						ModelProperty deletionConditionProperty = getModelEntity(child).getModelProperty(c);
						Object deletionConditionRequiredObject = getHandler(child).invokeGetter(deletionConditionProperty);
						if (deletionConditionRequiredObject != null) {
							requiredPresence.add(deletionConditionRequiredObject);
						}
					}
					break;
				}
				if (requiredPresence.size() > 0) {
					ConditionalPresence conditionalPresence = new ConditionalPresence(child, requiredPresence);
					list.add(conditionalPresence);
				}
			}
			// System.out.println("Embedded in "+father+" : "+child+" conditioned to required presence of "+requiredPresence);
		}
	}

	private void appendEmbeddedObjects(Object father, List<Object> list, EmbeddingType embeddingType) throws ModelDefinitionException {
		ProxyMethodHandler handler = getHandler(father);
		ModelEntity modelEntity = handler.getModelEntity();

		Iterator<ModelProperty<?>> properties = modelEntity.getProperties();
		while (properties.hasNext()) {
			ModelProperty<?> p = properties.next();
			switch (p.getCardinality()) {
			case SINGLE:
				Object oValue = handler.invokeGetter(p);
				appendEmbedded(p, father, list, oValue, embeddingType);
				break;
			case LIST:
				List<?> values = (List<?>) handler.invokeGetter(p);
				for (Object o : values) {
					appendEmbedded(p, father, list, o, embeddingType);
				}
				break;
			default:
				break;
			}
		}
	}

	public Clipboard copy(Object... objects) throws ModelExecutionException, ModelDefinitionException, CloneNotSupportedException {
		return new Clipboard(this, objects);
	}

	public Clipboard cut(Object... objects) throws ModelExecutionException, ModelDefinitionException, CloneNotSupportedException {
		return null;
	}

	public Object paste(Clipboard clipboard, Object context) throws ModelExecutionException, ModelDefinitionException,
			CloneNotSupportedException {
		if (!isProxyObject(context)) {
			throw new ClipboardOperationException("Cannot paste here: context is not valid");
		}

		return getHandler(context).paste(clipboard);
	}

	public Object paste(Clipboard clipboard, ModelProperty<?> modelProperty, Object context) throws ModelExecutionException,
			ModelDefinitionException, CloneNotSupportedException {
		if (!isProxyObject(context)) {
			throw new ClipboardOperationException("Cannot paste here: context is not valid");
		}

		return getHandler(context).paste(clipboard, (ModelProperty) modelProperty);
	}

	public Object paste(Clipboard clipboard, ModelProperty<?> modelProperty, PastingPoint pp, Object context)
			throws ModelExecutionException, ModelDefinitionException, CloneNotSupportedException {
		if (!isProxyObject(context)) {
			throw new ClipboardOperationException("Cannot paste here: context is not valid");
		}

		return getHandler(context).paste(clipboard, (ModelProperty) modelProperty, pp);
	}

}
