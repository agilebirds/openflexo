/**
 * 
 */
package org.openflexo.model.factory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;

import javax.annotation.Nonnull;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.exceptions.NoSuchEntityException;
import org.openflexo.model.exceptions.UnitializedEntityException;
import org.openflexo.model.xml.InvalidDataException;
import org.openflexo.toolbox.HasPropertyChangeSupport;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class ProxyMethodHandler<I> implements MethodHandler, PropertyChangeListener {

	private static final String MODIFIED = "modified";
	private static final String DESERIALIZING = "deserializing";
	private static final String SERIALIZING = "serializing";
	private Map<String, Object> values;
	private Map<String, Object> oldValues;
	private ModelEntity<I> modelEntity;

	private I object;

	private boolean deleted = false;
	private boolean initialized = false;
	private boolean serializing = false;
	private boolean deserializing = false;
	private boolean createdByCloning = false;
	private boolean beingCloned = false;
	private boolean modified = false;
	private PropertyChangeSupport propertyChangeSupport;
	private boolean initializing;

	private static Method PERFORM_SUPER_GETTER;
	private static Method PERFORM_SUPER_SETTER;
	private static Method PERFORM_SUPER_ADDER;
	private static Method PERFORM_SUPER_REMOVER;
	private static Method PERFORM_SUPER_DELETER;
	private static Method PERFORM_SUPER_FINDER;
	private static Method PERFORM_SUPER_GETTER_ENTITY;
	private static Method PERFORM_SUPER_SETTER_ENTITY;
	private static Method PERFORM_SUPER_ADDER_ENTITY;
	private static Method PERFORM_SUPER_REMOVER_ENTITY;
	private static Method PERFORM_SUPER_DELETER_ENTITY;
	private static Method PERFORM_SUPER_FINDER_ENTITY;
	private static Method PERFORM_SUPER_SET_MODIFIED;
	private static Method GET_MODEL_FACTORY;
	private static Method IS_MODIFIED;
	private static Method SET_MODIFIED;
	private static Method IS_SERIALIZING;
	private static Method IS_DESERIALIZING;
	private static Method TO_STRING;
	private static Method GET_PROPERTY_CHANGE_SUPPORT;
	private static Method CLONE_OBJECT;
	private static Method CLONE_OBJECT_WITH_CONTEXT;
	private static Method IS_CREATED_BY_CLONING;
	private static Method IS_BEING_CLONED;

	static {
		try {
			PERFORM_SUPER_GETTER = AccessibleProxyObject.class.getMethod("performSuperGetter", String.class);
			PERFORM_SUPER_SETTER = AccessibleProxyObject.class.getMethod("performSuperSetter", String.class, Object.class);
			PERFORM_SUPER_ADDER = AccessibleProxyObject.class.getMethod("performSuperAdder", String.class, Object.class);
			PERFORM_SUPER_REMOVER = AccessibleProxyObject.class.getMethod("performSuperRemover", String.class, Object.class);
			PERFORM_SUPER_DELETER = AccessibleProxyObject.class.getMethod("performSuperDelete");
			PERFORM_SUPER_FINDER = AccessibleProxyObject.class.getMethod("performSuperFinder", String.class, Object.class);
			PERFORM_SUPER_GETTER_ENTITY = AccessibleProxyObject.class.getMethod("performSuperGetter", String.class, Class.class);
			PERFORM_SUPER_SETTER_ENTITY = AccessibleProxyObject.class.getMethod("performSuperSetter", String.class, Object.class,
					Class.class);
			PERFORM_SUPER_ADDER_ENTITY = AccessibleProxyObject.class
					.getMethod("performSuperAdder", String.class, Object.class, Class.class);
			PERFORM_SUPER_REMOVER_ENTITY = AccessibleProxyObject.class.getMethod("performSuperRemover", String.class, Object.class,
					Class.class);
			PERFORM_SUPER_DELETER_ENTITY = AccessibleProxyObject.class.getMethod("performSuperDelete", Class.class);
			PERFORM_SUPER_FINDER_ENTITY = AccessibleProxyObject.class.getMethod("performSuperFinder", String.class, Object.class,
					Class.class);
			IS_SERIALIZING = AccessibleProxyObject.class.getMethod("isSerializing");
			IS_DESERIALIZING = AccessibleProxyObject.class.getMethod("isDeserializing");
			IS_MODIFIED = AccessibleProxyObject.class.getMethod("isModified");
			SET_MODIFIED = AccessibleProxyObject.class.getMethod("setModified", boolean.class);
			PERFORM_SUPER_SET_MODIFIED = AccessibleProxyObject.class.getMethod("performSuperSetModified", boolean.class);
			GET_PROPERTY_CHANGE_SUPPORT = HasPropertyChangeSupport.class.getMethod("getPropertyChangeSupport");
			GET_MODEL_FACTORY = AccessibleProxyObject.class.getMethod("getModelFactory");
			TO_STRING = Object.class.getMethod("toString");
			CLONE_OBJECT = CloneableProxyObject.class.getMethod("cloneObject");
			CLONE_OBJECT_WITH_CONTEXT = CloneableProxyObject.class.getMethod("cloneObject", Array.newInstance(Object.class, 0).getClass());
			IS_CREATED_BY_CLONING = CloneableProxyObject.class.getMethod("isCreatedByCloning");
			IS_BEING_CLONED = CloneableProxyObject.class.getMethod("isBeingCloned");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public ProxyMethodHandler(ModelEntity<I> modelEntity) throws ModelDefinitionException {
		this.modelEntity = modelEntity;
		values = new HashMap<String, Object>(modelEntity.getPropertiesSize(), 1.0f);
		initialized = !modelEntity.hasInitializers();
	}

	public I getObject() {
		return object;
	}

	public void setObject(I object) {
		this.object = object;
	}

	public ModelFactory getModelFactory() {
		return getModelEntity().getModelFactory();
	}

	public ModelEntity<I> getModelEntity() {
		return modelEntity;
	}

	@Override
	public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {
		Initializer initializer = method.getAnnotation(Initializer.class);
		if (initializer != null) {
			internallyInvokeInitializer(getModelEntity().getInitializers(method), args);
			return self;
		}
		if (!initialized && !initializing) {
			throw new UnitializedEntityException(getModelEntity());
		}
		Getter getter = method.getAnnotation(Getter.class);
		if (getter != null) {
			String id = getter.value();
			return internallyInvokeGetter(id);
		}

		Setter setter = method.getAnnotation(Setter.class);
		if (setter != null) {
			String id = setter.value();
			internallyInvokeSetter(id, args);
			return null;
		}

		Adder adder = method.getAnnotation(Adder.class);
		if (adder != null) {
			String id = adder.value();
			internallyInvokeAdder(id, args);
			return null;
		}

		Remover remover = method.getAnnotation(Remover.class);
		if (remover != null) {
			String id = remover.value();
			internallyInvokerRemover(id, args);
			return null;
		}

		Finder finder = method.getAnnotation(Finder.class);
		if (finder != null) {
			return internallyInvokeFinder(finder, args);
		}

		if (methodIsEquivalentTo(method, PERFORM_SUPER_GETTER)) {
			return internallyInvokeGetter(modelEntity.getModelProperty((String) args[0]));
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_SETTER)) {
			internallyInvokeSetter(modelEntity.getModelProperty((String) args[0]), args[1]);
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_ADDER)) {
			internallyInvokeAdder(modelEntity.getModelProperty((String) args[0]), args[1]);
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_REMOVER)) {
			internallyInvokeRemover(modelEntity.getModelProperty((String) args[0]), args[1]);
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_DELETER)) {
			internallyInvokeDeleter();
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_FINDER)) {

			internallyInvokeFinder(finder, args);
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_GETTER_ENTITY)) {
			ModelEntity<? super I> e = getModelEntityFromArg((Class<?>) args[1]);
			return internallyInvokeGetter(e.getModelProperty((String) args[0]));
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_SETTER_ENTITY)) {
			ModelEntity<? super I> e = getModelEntityFromArg((Class<?>) args[2]);
			internallyInvokeSetter(e.getModelProperty((String) args[0]), args[1]);
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_ADDER_ENTITY)) {
			ModelEntity<? super I> e = getModelEntityFromArg((Class<?>) args[2]);
			internallyInvokeAdder(e.getModelProperty((String) args[0]), args[1]);
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_REMOVER_ENTITY)) {
			ModelEntity<? super I> e = getModelEntityFromArg((Class<?>) args[2]);
			internallyInvokeRemover(e.getModelProperty((String) args[0]), args[1]);
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_DELETER_ENTITY)) {
			internallyInvokeDeleter();
			return null;
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_FINDER_ENTITY)) {
			Class<?> class1 = (Class<?>) args[2];
			ModelEntity<? super I> e = getModelEntityFromArg(class1);
			finder = e.getFinder((String) args[0]);
			if (finder != null) {
				return internallyInvokeFinder(finder, args);
			} else {
				throw new ModelExecutionException("Not such finder defined. Finder '" + args[0] + "' could not be found on entity "
						+ class1.getName());
			}
		} else if (methodIsEquivalentTo(method, PERFORM_SUPER_FINDER)) {
			finder = getModelEntity().getFinder((String) args[0]);
			if (finder != null) {
				return internallyInvokeFinder(finder, args);
			} else {
				throw new ModelExecutionException("Not such finder defined. Finder '" + args[0] + "' could not be found on entity "
						+ getModelEntity().getImplementedInterface().getName());
			}
		} else if (methodIsEquivalentTo(method, IS_SERIALIZING)) {
			return isSerializing();
		} else if (methodIsEquivalentTo(method, IS_DESERIALIZING)) {
			return isDeserializing();
		} else if (methodIsEquivalentTo(method, IS_MODIFIED)) {
			return isModified();
		} else if (methodIsEquivalentTo(method, SET_MODIFIED) || methodIsEquivalentTo(method, PERFORM_SUPER_SET_MODIFIED)) {
			setModified((Boolean) args[0]);
			return null;
		} else if (methodIsEquivalentTo(method, CLONE_OBJECT)) {
			return cloneObject();
		} else if (methodIsEquivalentTo(method, IS_BEING_CLONED)) {
			return beingCloned;
		} else if (methodIsEquivalentTo(method, IS_CREATED_BY_CLONING)) {
			return createdByCloning;
		} else if (methodIsEquivalentTo(method, GET_PROPERTY_CHANGE_SUPPORT)) {
			return getPropertyChangeSuppport();
		} else if (methodIsEquivalentTo(method, GET_MODEL_FACTORY)) {
			return getModelFactory();
		} else if (methodIsEquivalentTo(method, CLONE_OBJECT_WITH_CONTEXT)) {
			return cloneObject(args);
		} else if (methodIsEquivalentTo(method, TO_STRING)) {
			return internallyInvokeToString();
		}

		System.err.println("Cannot handle method " + method);
		return null;
	}

	private boolean methodIsEquivalentTo(Method method, Method to) {
		return method.getName().equals(to.getName()) && method.getReturnType().equals(to.getReturnType())
				&& Arrays.deepEquals(method.getParameterTypes(), to.getParameterTypes());
	}

	private PropertyChangeSupport getPropertyChangeSuppport() {
		if (propertyChangeSupport == null) {
			propertyChangeSupport = new PropertyChangeSupport(getObject());
		}
		return propertyChangeSupport;
	}

	private void internallyInvokeInitializer(org.openflexo.model.factory.ModelInitializer in, Object[] args)
			throws ModelDefinitionException {
		initializing = true;
		try {
			List<String> parameters = in.getParameters();
			for (int i = 0; i < parameters.size(); i++) {
				String parameter = parameters.get(i);
				if (parameter != null) {
					internallyInvokeSetter(getModelEntity().getModelProperty(parameter), args[i]);
				}
			}
		} finally {
			initialized = true;
			initializing = false;
		}
	}

	private @Nonnull
	ModelEntity<? super I> getModelEntityFromArg(Class<?> class1) throws ModelDefinitionException {
		ModelEntity<?> e = getModelFactory().getModelEntity(class1);
		if (e == null) {
			throw new NoSuchEntityException(class1);
		}
		if (!e.isAncestorOf(modelEntity)) {
			throw new ModelExecutionException(((Class<?>) class1).getName() + " is not a super interface of "
					+ modelEntity.getImplementedInterface().getName());
		}
		// Is e is an ancestor of modelEntity, this means that e is a super interface of the implementedInterface of modelEntity and we can
		// therefore cast e to ModelEntity<? super I>
		return (ModelEntity<? super I>) e;
	}

	private Object internallyInvokeGetter(String propertyIdentifier) throws ModelDefinitionException {
		ModelProperty<? super I> property = modelEntity.getModelProperty(propertyIdentifier);
		return internallyInvokeGetter(property);
	}

	void internallyInvokeSetter(String propertyIdentifier, Object[] args) throws ModelDefinitionException {
		ModelProperty<? super I> property = modelEntity.getModelProperty(propertyIdentifier);
		internallyInvokeSetter(property, args[0]);
	}

	void internallyInvokeAdder(String propertyIdentifier, Object[] args) throws ModelDefinitionException {
		ModelProperty<? super I> property = modelEntity.getModelProperty(propertyIdentifier);
		internallyInvokeAdder(property, args[0]);
	}

	private void internallyInvokerRemover(String id, Object[] args) throws ModelDefinitionException {
		ModelProperty<? super I> property = modelEntity.getModelProperty(id);
		internallyInvokeRemover(property, args[0]);
	}

	private void internallyInvokeDeleter() throws ModelDefinitionException {
		if (deleted) {
			return;
		}
		deleted = true;
		Stack<String> deletedProperties = new Stack<String>();
		// 1. We make a copy of all the values
		oldValues = new HashMap<String, Object>(values);
		/*ModelEntity<? super I> entity = getModelEntity();
		while (entity != null) {
			// 2. We invoke all the deleters of the embedded properties
			ModelDeleter<? super I> deleter = entity.getDeclaredDeleter();
			if (deleter != null) {
				for (String embedded : deleter.getDeleter().embedded()) {
					ModelProperty<? super I> property = getModelEntity().getModelProperty(embedded);
					Object value = invokeGetter(embedded);
					if (value == null) {
						continue;
					}
					List<?> objectsToDelete;
					switch (property.getCardinality()) {
					case SINGLE:
						objectsToDelete = Arrays.asList(value);
						break;
					case LIST:
						objectsToDelete = new ArrayList<Object>((Collection<?>) value);
						break;
					case MAP:
						objectsToDelete = new ArrayList<Object>(((Map<?, ?>) value).values());
						break;
					default:
						continue;
					}
					for (Object toDelete : objectsToDelete) {
						if (toDelete != null) {
							ProxyMethodHandler<Object> handler = getModelFactory().getHandler(toDelete);
							if (handler.getModelEntity().getModelDeleter() != null) {
								handler.invokeDeleter();
							} else {
								// TODO: verify if this is ok or we should rather throw an exception
								handler.internallyInvokeDeleter();
							}
						}
					}
				}
				if (!deleter.getDeleter().deletedProperty().equals(Deleter.UNDEFINED)) {
					deletedProperties.push(deleter.getDeleter().deletedProperty());
				}
			}
			// 3. We nullify all properties accessing another entity of the model (this should allow dereferencing and in the end garbage
			// collection)
			Iterator<?> i = entity.getDeclaredProperties();
			while (i.hasNext()) {
				ModelProperty<? super I> p = (ModelProperty<? super I>) i.next();
				// TODO: verify if this is ok or if we should rather nullify everything
				// The real objective is to ensure that other objects from the model do not reference this.getObject() anymore
				if (p.getAccessedEntity() != null) {
					internallyInvokeSetter(p, null);
				}
			}
			// 4. we invoke the super deleter (if any)
			entity = entity.getSuperEntity();
		}*/
		// 5. we set the deleted property (which in turns will notify observers)
		// TODO: verify if this is ok or we should use another option
		String deletedProperty;
		while (!deletedProperties.isEmpty()) {
			deletedProperty = deletedProperties.pop();
			ModelProperty<? super I> p = getModelEntity().getModelProperty(deletedProperty);
			if (p.getSetter() != null) {
				invokeSetter(p, Boolean.TRUE);
			} else {
				internallyInvokeSetter(p, Boolean.TRUE);
			}
		}
		propertyChangeSupport = null;
	}

	public Object invokeGetter(ModelProperty<? super I> property) {
		try {
			return property.getGetterMethod().invoke(getObject(), (Object[]) null);
		} catch (IllegalArgumentException e) {
			throw new ModelExecutionException(e);
		} catch (IllegalAccessException e) {
			throw new ModelExecutionException(e);
		} catch (InvocationTargetException e) {
			throw new ModelExecutionException(e);
		}
	}

	public void invokeSetter(ModelProperty<? super I> property, Object value) {
		try {
			property.getSetterMethod().invoke(getObject(), value);
		} catch (IllegalArgumentException e) {
			throw new ModelExecutionException(e);
		} catch (IllegalAccessException e) {
			throw new ModelExecutionException(e);
		} catch (InvocationTargetException e) {
			throw new ModelExecutionException(e);
		}
	}

	public void invokeAdder(ModelProperty<? super I> property, Object value) {
		try {
			property.getAdderMethod().invoke(getObject(), value);
		} catch (IllegalArgumentException e) {
			throw new ModelExecutionException(e);
		} catch (IllegalAccessException e) {
			throw new ModelExecutionException(e);
		} catch (InvocationTargetException e) {
			throw new ModelExecutionException(e);
		}
	}

	public void invokeRemover(ModelProperty<? super I> property, Object value) {
		try {
			property.getRemoverMethod().invoke(getObject(), value);
		} catch (IllegalArgumentException e) {
			throw new ModelExecutionException(e);
		} catch (IllegalAccessException e) {
			throw new ModelExecutionException(e);
		} catch (InvocationTargetException e) {
			throw new ModelExecutionException(e);
		}
	}

	public Object invokeGetter(String propertyIdentifier) throws ModelDefinitionException {
		return invokeGetter(getModelEntity().getModelProperty(propertyIdentifier));
	}

	public void invokeSetter(String propertyIdentifier, Object value) throws ModelDefinitionException {
		invokeSetter(getModelEntity().getModelProperty(propertyIdentifier), value);
	}

	public void invokeAdder(String propertyIdentifier, Object value) throws ModelDefinitionException {
		invokeAdder(getModelEntity().getModelProperty(propertyIdentifier), value);
	}

	public void invokeRemover(String propertyIdentifier, Object value) throws ModelDefinitionException {
		invokeRemover(getModelEntity().getModelProperty(propertyIdentifier), value);
	}

	private Object internallyInvokeGetter(ModelProperty<? super I> property) throws ModelDefinitionException {
		switch (property.getCardinality()) {
		case SINGLE:
			return invokeGetterForSingleCardinality(property);
		case LIST:
			return invokeGetterForListCardinality(property);
		case MAP:
			return invokeGetterForMapCardinality(property);
		default:
			throw new ModelExecutionException("Invalid cardinality: " + property.getCardinality());
		}
	}

	private Object invokeGetterForSingleCardinality(ModelProperty<? super I> property) throws ModelDefinitionException {
		if (property.getGetter() == null) {
			throw new ModelExecutionException("Getter is not defined for property " + property);
		}
		if (property.getReturnedValue() != null) {
			// Simple implementation of ReturnedValue. This can be drastically improved
			String returnedValue = property.getReturnedValue().value();
			StringTokenizer st = new StringTokenizer(returnedValue, ".");
			Object value = this;
			ProxyMethodHandler<?> handler = this;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				value = handler.internallyInvokeGetter(token);
				if (value != null) {
					if (st.hasMoreTokens()) {
						if (!(value instanceof ProxyObject)) {
							throw new ModelExecutionException("Cannot invoke " + st.nextToken() + " on object of type "
									+ value.getClass().getName() + " (caused by returned value: " + returnedValue + ")");
						}
						handler = (ProxyMethodHandler<?>) ((ProxyObject) value).getHandler();
					}
				} else {
					return null;
				}
			}
			return value;
		}
		Object returned = values.get(property.getPropertyIdentifier());
		if (returned != null) {
			return returned;
		} else {
			Object defaultValue = property.getDefaultValue();
			if (defaultValue != null) {
				values.put(property.getPropertyIdentifier(), defaultValue);
				return defaultValue;
			}
			if (property.getType().isPrimitive()) {
				throw new ModelExecutionException("No default value defined for primitive property " + property);
			}
			return null;
		}
	}

	private List<?> invokeGetterForListCardinality(ModelProperty<? super I> property) {
		if (property.getGetter() == null) {
			throw new ModelExecutionException("Getter is not defined for property " + property);
		}
		List<?> returned = (List<?>) values.get(property.getPropertyIdentifier());
		if (returned != null) {
			return returned;
		} else {
			Class<? extends List> listClass = getModelFactory().getListImplementationClass();
			try {
				returned = listClass.newInstance();
			} catch (InstantiationException e) {
				throw new ModelExecutionException(e);
			} catch (IllegalAccessException e) {
				throw new ModelExecutionException(e);
			}
			if (returned != null) {
				values.put(property.getPropertyIdentifier(), returned);
				return returned;
			}
			return null;
		}
	}

	private Map<?, ?> invokeGetterForMapCardinality(ModelProperty<? super I> property) {
		if (property.getGetter() == null) {
			throw new ModelExecutionException("Getter is not defined for property " + property);
		}
		Map<?, ?> returned = (Map<?, ?>) values.get(property.getPropertyIdentifier());
		if (returned != null) {
			return returned;
		} else {
			Class<? extends Map> mapClass = getModelFactory().getMapImplementationClass();
			try {
				returned = mapClass.newInstance();
			} catch (InstantiationException e) {
				throw new ModelExecutionException(e);
			} catch (IllegalAccessException e) {
				throw new ModelExecutionException(e);
			}
			if (returned != null) {
				values.put(property.getPropertyIdentifier(), returned);
				return returned;
			}
			return null;
		}
	}

	void internallyInvokeSetter(ModelProperty<? super I> property, Object value) throws ModelDefinitionException {
		switch (property.getCardinality()) {
		case SINGLE:
			invokeSetterForSingleCardinality(property, value);
			break;
		case LIST:
			invokeSetterForListCardinality(property, value);
			break;
		case MAP:
			invokeSetterForMapCardinality(property, value);
			break;
		default:
			throw new ModelExecutionException("Invalid cardinality: " + property.getCardinality());
		}
	}

	private void invokeSetterForSingleCardinality(ModelProperty<? super I> property, Object value) throws ModelDefinitionException {
		if (property.getSetter() == null && !isDeserializing() && !initializing && !createdByCloning) {
			throw new ModelExecutionException("Setter is not defined for property " + property);
		}
		Object oldValue = invokeGetter(property);

		// Is it a real change ?
		if (!isEqual(oldValue, value)) {
			// First handle inverse property for oldValue
			if (property.getInverseProperty() != null) {
				switch (property.getInverseProperty().getCardinality()) {
				case SINGLE:
					if (oldValue != null) {
						getModelFactory().getHandler(oldValue).invokeSetter(property.getInverseProperty(), null);
					}
					break;
				case LIST:
					if (oldValue != null) {
						getModelFactory().getHandler(oldValue).invokeRemover(property.getInverseProperty(), getObject());
					}
					break;
				case MAP:
					break;
				default:
					throw new ModelExecutionException("Invalid cardinality: " + property.getInverseProperty().getCardinality());
				}
			}

			// Now do the job, internally
			if (value == null) {
				values.remove(property.getPropertyIdentifier());
			} else {
				values.put(property.getPropertyIdentifier(), value);
			}
			firePropertyChange(property.getPropertyIdentifier(), oldValue, value);
			if (modelEntity.getModify() != null && modelEntity.getModify().synchWithForward()
					&& property.getPropertyIdentifier().equals(modelEntity.getModify().forward())) {
				if (oldValue instanceof HasPropertyChangeSupport) {
					((HasPropertyChangeSupport) oldValue).getPropertyChangeSupport().removePropertyChangeListener(MODIFIED, this);
				}
				if (value instanceof HasPropertyChangeSupport) {
					((HasPropertyChangeSupport) value).getPropertyChangeSupport().addPropertyChangeListener(MODIFIED, this);
				}
			}
			// First handle inverse property for newValue
			if (property.getInverseProperty() != null) {
				ProxyMethodHandler<Object> handler = getModelFactory().getHandler(value);
				switch (property.getInverseProperty().getCardinality()) {
				case SINGLE:
					if (value != null) {
						handler.invokeSetter(property.getInverseProperty(), getObject());
					}
					break;
				case LIST:
					// System.out.println("Je viens de faire le setter pour "+property+" value="+value);
					if (value != null) {
						handler.invokeAdder(property.getInverseProperty(), getObject());
						// System.out.println("J'ai execute: "+property.getInverseProperty().getAdderMethod()+" avec "+getObject());
					}
					break;
				case MAP:
					break;
				default:
					throw new ModelExecutionException("Invalid cardinality: " + property.getInverseProperty().getCardinality());
				}
			}
			markAsModified();
		}
	}

	private void firePropertyChange(String propertyIdentifier, Object oldValue, Object value) {
		if (getObject() instanceof HasPropertyChangeSupport) {
			((HasPropertyChangeSupport) getObject()).getPropertyChangeSupport().firePropertyChange(propertyIdentifier, oldValue, value);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			if (modelEntity.getModify() != null && modelEntity.getModify().synchWithForward()) {
				Object forwarded = internallyInvokeGetter(modelEntity.getModify().forward());
				if (evt.getSource() == forwarded) {
					if (MODIFIED.equals(evt.getPropertyName())) {
						setModified((Boolean) evt.getNewValue());
					}
				}
			}
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	private void markAsModified() throws ModelDefinitionException {
		if (!isDeserializing() && !isSerializing()) {
			boolean old = modified;
			modified = true;
			if (!old) {
				firePropertyChange(MODIFIED, old, modified);
				if (modelEntity.getModify() != null && modelEntity.getModify().forward() != null) {
					ModelProperty<? super I> modelProperty = modelEntity.getModelProperty(modelEntity.getModify().forward());
					if (modelProperty != null) {
						Object forward = invokeGetter(modelProperty);
						if (forward instanceof ProxyObject) {
							((ProxyMethodHandler<?>) ((ProxyObject) forward).getHandler()).markAsModified();
						}
					}
				}
			}
		}
	}

	private void invokeSetterForListCardinality(ModelProperty<? super I> property, Object value) {
		if (property.getSetter() == null && !isDeserializing() && !initializing && !createdByCloning) {
			throw new ModelExecutionException("Setter is not defined for property " + property);
		}
		// TODO implement this
		throw new UnsupportedOperationException("Setter for LIST: not implemented yet");
	}

	private void invokeSetterForMapCardinality(ModelProperty<? super I> property, Object value) {
		if (property.getSetter() == null && !isDeserializing() && !initializing && !createdByCloning) {
			throw new ModelExecutionException("Setter is not defined for property " + property);
		}
		// TODO implement this
		throw new UnsupportedOperationException("Setter for MAP: not implemented yet");
	}

	void internallyInvokeAdder(ModelProperty<? super I> property, Object value) throws ModelDefinitionException {
		// System.out.println("Invoke ADDER "+property.getPropertyIdentifier());
		switch (property.getCardinality()) {
		case SINGLE:
			throw new ModelExecutionException("Cannot invoke ADDER on " + property.getPropertyIdentifier() + ": Invalid cardinality SINGLE");
		case LIST:
			invokeAdderForListCardinality(property, value);
			break;
		case MAP:
			invokeAdderForMapCardinality(property, value);
			break;
		default:
			throw new ModelExecutionException("Invalid cardinality: " + property.getCardinality());
		}
	}

	private void invokeAdderForListCardinality(ModelProperty<? super I> property, Object value) throws ModelDefinitionException {
		if (property.getAdder() == null && !isDeserializing() && !initializing && !createdByCloning) {
			throw new ModelExecutionException("Adder is not defined for property " + property);
		}
		List list = (List) invokeGetter(property);

		if (!list.contains(value)) {
			list.add(value);
			// Handle inverse property for new value
			if (property.getInverseProperty() != null) {
				switch (property.getInverseProperty().getCardinality()) {
				case SINGLE:
					if (value != null) {
						getModelFactory().getHandler(value).invokeSetter(property.getInverseProperty(), getObject());
					}
					break;
				case LIST:
					if (value != null) {
						getModelFactory().getHandler(value).invokeAdder(property.getInverseProperty(), getObject());
					}
					break;
				case MAP:
					break;
				default:
					throw new ModelExecutionException("Invalid cardinality: " + property.getInverseProperty().getCardinality());
				}
			}
			markAsModified();
		}
	}

	private void invokeAdderForMapCardinality(ModelProperty<? super I> property, Object value) {
		if (property.getAdder() == null && !isDeserializing() && !initializing && !createdByCloning) {
			throw new ModelExecutionException("Adder is not defined for property " + property);
		}
		// TODO implement this
		throw new UnsupportedOperationException("Adder for MAP: not implemented yet");
	}

	private void internallyInvokeRemover(ModelProperty<? super I> property, Object value) throws ModelDefinitionException {
		// System.out.println("Invoke REMOVER "+property.getPropertyIdentifier());
		switch (property.getCardinality()) {
		case SINGLE:
			throw new ModelExecutionException("Cannot invoke REMOVER on " + property.getPropertyIdentifier()
					+ ": Invalid cardinality SINGLE");
		case LIST:
			invokeRemoverForListCardinality(property, value);
			break;
		case MAP:
			invokeRemoverForMapCardinality(property, value);
			break;
		default:
			throw new ModelExecutionException("Invalid cardinality: " + property.getCardinality());
		}
	}

	private void invokeRemoverForListCardinality(ModelProperty<? super I> property, Object value) throws ModelDefinitionException {
		if (property.getRemover() == null) {
			throw new ModelExecutionException("Remover is not defined for property " + property);
		}
		List<?> list = (List<?>) invokeGetter(property);

		if (list.contains(value)) {
			list.remove(value);
			// Handle inverse property for new value
			if (property.getInverseProperty() != null) {
				switch (property.getInverseProperty().getCardinality()) {
				case SINGLE:
					if (value != null) {
						getModelFactory().getHandler(value).invokeSetter(property.getInverseProperty(), null);
					}
					break;
				case LIST:
					if (value != null) {
						getModelFactory().getHandler(value).invokeAdder(property.getInverseProperty(), null);
					}
					break;
				case MAP:
					break;
				default:
					throw new ModelExecutionException("Invalid cardinality: " + property.getInverseProperty().getCardinality());
				}
			}
			markAsModified();
		}
	}

	private void invokeRemoverForMapCardinality(ModelProperty<? super I> property, Object value) {
		if (property.getRemover() == null) {
			throw new ModelExecutionException("Remover is not defined for property " + property);
		}
		// TODO implement this
		throw new UnsupportedOperationException("Remover for MAP: not implemented yet");
	}

	private Object internallyInvokeFinder(@Nonnull Finder finder, Object[] args) throws ModelDefinitionException {
		if (args.length == 0) {
			throw new ModelDefinitionException("Finder " + finder.collection() + " by attribute " + finder.attribute()
					+ " does not declare enough argument!");
		}
		String collectionID = finder.collection();
		ModelProperty<? super I> property = getModelEntity().getModelProperty(collectionID);
		Object collection = internallyInvokeGetter(property);
		if (collection == null) {
			return null;
		}
		Object value = args[0];
		String attribute = finder.attribute();
		if (collection instanceof Map<?, ?>) {
			collection = ((Map<?, ?>) collection).values();
		}
		if (collection instanceof Iterable) {
			if (finder.isMultiValued()) {
				List<Object> objects = new ArrayList<Object>();
				for (Object o : (Iterable<?>) collection) {
					if (isObjectAttributeEquals(o, attribute, value)) {
						objects.add(o);
					}
				}
				return objects;
			} else {
				for (Object o : (Iterable<?>) collection) {
					if (isObjectAttributeEquals(o, attribute, value)) {
						return o;
					}
				}
				return null;
			}
		}
		throw new ModelDefinitionException("finder works only on maps and iterable");
	}

	private boolean isObjectAttributeEquals(Object o, String attribute, Object value) throws ModelDefinitionException {
		ProxyMethodHandler<?> handler = getModelFactory().getHandler(o);
		if (handler != null) {
			Object attributeValue = handler.invokeGetter(attribute);
			return isEqual(attributeValue, value);
		} else {
			throw new ModelDefinitionException("Found object of type " + o.getClass().getName()
					+ " but is not an instanceof ProxyObject:\n" + o);
		}
	}

	private static boolean isEqual(Object oldValue, Object newValue) {
		if (oldValue == null) {
			return newValue == null;
		}
		if (oldValue == newValue) {
			return true;
		}
		return oldValue.equals(newValue);
	}

	/*private Object cloneObject() throws ModelExecutionException, ModelDefinitionException, CloneNotSupportedException
	{
		System.out.println("Tiens je clone "+getObject());

		if (!(getObject() instanceof CloneableProxyObject)) throw new CloneNotSupportedException();

		Hashtable<CloneableProxyObject,Object> clonedObjects = new Hashtable<CloneableProxyObject, Object>();
		Object returned = performClone(clonedObjects);
		for (CloneableProxyObject o : clonedObjects.keySet()) {
			ProxyMethodHandler<?> clonedObjectHandler = getModelFactory().getHandler(o);
			clonedObjectHandler.finalizeClone(clonedObjects);
		}
		return returned;
	}

	private Object appendToClonedObjects(Hashtable<CloneableProxyObject,Object> clonedObjects, CloneableProxyObject objectToCloneOrReference) throws ModelExecutionException, ModelDefinitionException
	{
		Object returned = clonedObjects.get(objectToCloneOrReference);
		if (returned != null) return returned;
		ProxyMethodHandler<?> clonedValueHandler = getModelFactory().getHandler(objectToCloneOrReference);
		returned = clonedValueHandler.performClone(clonedObjects);
		System.out.println("for "+objectToCloneOrReference+" clone is "+returned);
		return returned;
	}

	private Object performClone(Hashtable<CloneableProxyObject,Object> clonedObjects) throws ModelExecutionException, ModelDefinitionException
	{
		System.out.println("******* performClone "+getObject());

		Object returned = null;
		try {
			returned = getModelEntity().newInstance();
		} catch (IllegalArgumentException e) {
			throw new ModelExecutionException(e);
		} catch (NoSuchMethodException e) {
			throw new ModelExecutionException(e);
		} catch (InstantiationException e) {
			throw new ModelExecutionException(e);
		} catch (IllegalAccessException e) {
			throw new ModelExecutionException(e);
		} catch (InvocationTargetException e) {
			throw new ModelExecutionException(e);
		}
		clonedObjects.put((CloneableProxyObject)getObject(),returned);

		ProxyMethodHandler<?> clonedObjectHandler = getModelFactory().getHandler(returned);
		Enumeration<ModelProperty<? super I>> properties = getModelEntity().getProperties();
		while(properties.hasMoreElements()) {
			ModelProperty p = properties.nextElement();
			switch (p.getCardinality()) {
			case SINGLE:
				Object singleValue = invokeGetter(p);
				switch (p.getCloningStrategy()) {
				case CLONE:
					if (getModelFactory().isModelEntity(p.getType()) && singleValue instanceof CloneableProxyObject) {
						appendToClonedObjects(clonedObjects, (CloneableProxyObject)singleValue);
					}
					break;
				case REFERENCE:
					break;
				case FACTORY:
					break;
				case IGNORE:
					break;
				}
				break;
			case LIST:
				List values = (List)invokeGetter(p);
				for (Object value : values) {
					switch (p.getCloningStrategy()) {
					case CLONE:
						if (getModelFactory().isModelEntity(p.getType()) && value instanceof CloneableProxyObject) {
							appendToClonedObjects(clonedObjects, (CloneableProxyObject)value);
						}
						break;
					case REFERENCE:
						break;
					case FACTORY:
						break;
					case IGNORE:
						break;
					}
				}
				break;
			default:
				break;
			}

		}

		return returned;
	}

	private Object finalizeClone(Hashtable<CloneableProxyObject,Object> clonedObjects) throws ModelExecutionException, ModelDefinitionException
	{
		Object clonedObject = clonedObjects.get(getObject());

		System.out.println("Tiens je finalise le clone pour "+getObject()+" le clone c'est "+clonedObject);

		ProxyMethodHandler<?> clonedObjectHandler = getModelFactory().getHandler(clonedObject);

		Enumeration<ModelProperty<? super I>> properties = getModelEntity().getProperties();

		while(properties.hasMoreElements()) {
			ModelProperty p = properties.nextElement();
			switch (p.getCardinality()) {
			case SINGLE:
				Object singleValue = invokeGetter(p);
				switch (p.getCloningStrategy()) {
				case CLONE:
					if (getModelFactory().getStringEncoder().isConvertable(p.getType())) {
						Object clonedValue = null;
						try {
							String clonedValueAsString = getModelFactory().getStringEncoder().toString(singleValue);
							clonedValue = getModelFactory().getStringEncoder().fromString(p.getType(),clonedValueAsString);
						} catch (InvalidDataException e) {
							throw new ModelExecutionException(e);
						}
						clonedObjectHandler.invokeSetter(p,clonedValue);
					}
					else if (getModelFactory().isModelEntity(p.getType()) && singleValue instanceof CloneableProxyObject) {
						Object clonedValue = clonedObjects.get(singleValue);
						clonedObjectHandler.invokeSetter(p,clonedValue);
					}
					break;
				case REFERENCE:
					Object referenceValue = (singleValue != null ? clonedObjects.get(singleValue) : null);
					if (referenceValue == null) referenceValue = singleValue;
					clonedObjectHandler.invokeSetter(p,referenceValue);
					break;
				case FACTORY:
					// TODO Not implemented
					break;
				case IGNORE:
					break;
				}
				break;
			case LIST:
				List values = (List)invokeGetter(p);
				System.out.println("values:"+values.hashCode()+" "+values);
				List valuesToClone = new ArrayList<Object>(values);
				for (Object value : valuesToClone) {
					switch (p.getCloningStrategy()) {
					case CLONE:
						if (getModelFactory().getStringEncoder().isConvertable(p.getType())) {
							Object clonedValue = null;
							try {
								String clonedValueAsString = getModelFactory().getStringEncoder().toString(value);
								clonedValue = getModelFactory().getStringEncoder().fromString(p.getType(),clonedValueAsString);
							} catch (InvalidDataException e) {
								throw new ModelExecutionException(e);
							}
							List l = (List)clonedObjectHandler.invokeGetter(p);
							System.out.println("l:"+l.hashCode()+" "+l);
							clonedObjectHandler.invokeAdder(p,clonedValue);
						}
						else if (getModelFactory().isModelEntity(p.getType()) && value instanceof CloneableProxyObject) {
							Object clonedValue = clonedObjects.get(value);
							clonedObjectHandler.invokeAdder(p,clonedValue);
						}
						break;
					case REFERENCE:
						Object referenceValue = (value != null ? clonedObjects.get(value) : null);
						if (referenceValue == null) referenceValue = value;
						clonedObjectHandler.invokeAdder(p,referenceValue);
						break;
					case FACTORY:
						// TODO Not implemented
						break;
					case IGNORE:
						break;
					}

				}
				break;
			default:
				break;
			}

		}

		return clonedObject;
	}*/

	/**
	 * Clone current object, using meta informations provided by related class All property should be annoted with a @CloningStrategy
	 * annotation which determine the way of handling this property Supplied context is used to determine the closure of objects graph being
	 * constructed during this operation. If a property is marked as @CloningStrategy.CLONE but lead to an object outside scope of cloning
	 * (the closure being computed), then resulting value is nullified. When context is not set, don't compute any closure, and clone all
	 * required objects
	 * 
	 * @param context
	 * @return
	 * @throws ModelExecutionException
	 * @throws ModelDefinitionException
	 * @throws CloneNotSupportedException
	 *             when supplied object is not implementing CloneableProxyObject interface
	 */
	protected Object cloneObject(Object... context) throws ModelExecutionException, ModelDefinitionException, CloneNotSupportedException {
		// System.out.println("Cloning "+getObject());

		if (context != null && context.length == 1 && context[0].getClass().isArray()) {
			context = (Object[]) context[0];
		}

		// Append this object to supplied context
		if (context != null && context.length > 0) {
			Object[] newContext = new Object[context.length + 1];
			for (int i = 0; i < context.length; i++) {
				newContext[i] = context[i];
			}
			newContext[context.length] = getObject();
			context = newContext;
		}

		if (!(getObject() instanceof CloneableProxyObject)) {
			throw new CloneNotSupportedException();
		}

		Hashtable<CloneableProxyObject, Object> clonedObjects = new Hashtable<CloneableProxyObject, Object>();
		Object returned = performClone(clonedObjects, context);
		for (CloneableProxyObject o : clonedObjects.keySet()) {
			ProxyMethodHandler<?> clonedObjectHandler = getModelFactory().getHandler(o);
			clonedObjectHandler.finalizeClone(clonedObjects, context);
		}
		return returned;
	}

	/**
	 * Internally used for cloning computation
	 */
	private Object appendToClonedObjects(Hashtable<CloneableProxyObject, Object> clonedObjects,
			CloneableProxyObject objectToCloneOrReference) throws ModelExecutionException, ModelDefinitionException {
		Object returned = clonedObjects.get(objectToCloneOrReference);
		if (returned != null) {
			return returned;
		}
		ProxyMethodHandler<?> clonedValueHandler = getModelFactory().getHandler(objectToCloneOrReference);
		returned = clonedValueHandler.performClone(clonedObjects);
		// System.out.println("for "+objectToCloneOrReference+" clone is "+returned);
		return returned;
	}

	/**
	 * Internally used for cloning computation
	 */
	private Object performClone(Hashtable<CloneableProxyObject, Object> clonedObjects, Object... context) throws ModelExecutionException,
			ModelDefinitionException {
		// System.out.println("******* performClone "+getObject());
		boolean setIsBeingCloned = !beingCloned;
		beingCloned = true;
		Object returned = null;
		try {
			try {
				returned = getModelEntity().newInstance();
			} catch (IllegalArgumentException e) {
				throw new ModelExecutionException(e);
			} catch (NoSuchMethodException e) {
				throw new ModelExecutionException(e);
			} catch (InstantiationException e) {
				throw new ModelExecutionException(e);
			} catch (IllegalAccessException e) {
				throw new ModelExecutionException(e);
			} catch (InvocationTargetException e) {
				throw new ModelExecutionException(e);
			}
			ProxyMethodHandler<?> clonedObjectHandler = getModelFactory().getHandler(returned);
			clonedObjectHandler.createdByCloning = true;
			clonedObjectHandler.initialized = true;
			try {
				clonedObjects.put((CloneableProxyObject) getObject(), returned);

				Iterator<ModelProperty<? super I>> properties = getModelEntity().getProperties();
				while (properties.hasNext()) {
					ModelProperty p = properties.next();
					switch (p.getCardinality()) {
					case SINGLE:
						Object singleValue = invokeGetter(p);
						switch (p.getCloningStrategy()) {
						case CLONE:
							if (getModelFactory().isModelEntity(p.getType()) && singleValue instanceof CloneableProxyObject) {
								if (!isPartOfContext(singleValue, EmbeddingType.CLOSURE, context)) {
									// Don't do it, outside of context
								} else {
									appendToClonedObjects(clonedObjects, (CloneableProxyObject) singleValue);
								}
							}
							break;
						case REFERENCE:
							break;
						case FACTORY:
							break;
						case IGNORE:
							break;
						}
						break;
					case LIST:
						List values = (List) invokeGetter(p);
						for (Object value : values) {
							switch (p.getCloningStrategy()) {
							case CLONE:
								if (getModelFactory().isModelEntity(p.getType()) && value instanceof CloneableProxyObject) {
									if (!isPartOfContext(value, EmbeddingType.CLOSURE, context)) {
										// Don't do it, outside of context
									} else {
										appendToClonedObjects(clonedObjects, (CloneableProxyObject) value);
									}
								}
								break;
							case REFERENCE:
								break;
							case FACTORY:
								break;
							case IGNORE:
								break;
							}
						}
						break;
					default:
						break;
					}

				}
			} finally {
				clonedObjectHandler.createdByCloning = false;
			}
		} finally {
			if (setIsBeingCloned) {
				beingCloned = false;
			}
		}
		return returned;
	}

	/**
	 * Internally used for cloning computation
	 */
	private Object finalizeClone(Hashtable<CloneableProxyObject, Object> clonedObjects, Object... context) throws ModelExecutionException,
			ModelDefinitionException {
		Object clonedObject = clonedObjects.get(getObject());

		// System.out.println("Finalizing clone for "+getObject()+" clone is "+clonedObject);

		ProxyMethodHandler<?> clonedObjectHandler = getModelFactory().getHandler(clonedObject);
		clonedObjectHandler.createdByCloning = true;
		try {
			Iterator<ModelProperty<? super I>> properties = getModelEntity().getProperties();

			while (properties.hasNext()) {
				ModelProperty p = properties.next();
				// TODO: cross-check that we should invoke continue
				// In the case of the deletedProperty, it is only normal that there are no setters.
				// We should either prevent this by validating that all properties (that are not deleted properties)
				// have a setter or allow properties to live without a setter.
				switch (p.getCardinality()) {
				case SINGLE:
					Object singleValue = invokeGetter(p);
					switch (p.getCloningStrategy()) {
					case CLONE:
						if (getModelFactory().getStringEncoder().isConvertable(p.getType())) {
							Object clonedValue = null;
							try {
								String clonedValueAsString = getModelFactory().getStringEncoder().toString(singleValue);
								clonedValue = getModelFactory().getStringEncoder().fromString(p.getType(), clonedValueAsString);
							} catch (InvalidDataException e) {
								throw new ModelExecutionException(e);
							}
							clonedObjectHandler.internallyInvokeSetter(p, clonedValue);
						} else if (getModelFactory().isModelEntity(p.getType()) && singleValue instanceof CloneableProxyObject) {
							Object clonedValue = clonedObjects.get(singleValue);
							if (!isPartOfContext(singleValue, EmbeddingType.CLOSURE, context)) {
								clonedValue = null;
							}
							clonedObjectHandler.internallyInvokeSetter(p, clonedValue);
						}
						break;
					case REFERENCE:
						Object referenceValue = singleValue != null ? clonedObjects.get(singleValue) : null;
						if (referenceValue == null) {
							referenceValue = singleValue;
						}
						clonedObjectHandler.internallyInvokeSetter(p, referenceValue);
						break;
					case FACTORY:
						if (p.getStrategyTypeFactory().equals("deriveName()")) {
							// TODO: just to test
							// TODO: implement this properly!
							// System.out.println("TODO: implement this (FACTORY whine cloning)");
							// Object factoredValue = ((FlexoModelObject)getObject()).deriveName();
							// clonedObjectHandler.internallyInvokeSetter(p,factoredValue);
						}
						break;
					case IGNORE:
						break;
					}
					break;
				case LIST:
					List<?> values = (List<?>) invokeGetter(p);
					List<?> valuesToClone = new ArrayList<Object>(values);
					for (Object value : valuesToClone) {
						switch (p.getCloningStrategy()) {
						case CLONE:
							if (getModelFactory().getStringEncoder().isConvertable(p.getType())) {
								Object clonedValue = null;
								try {
									String clonedValueAsString = getModelFactory().getStringEncoder().toString(value);
									clonedValue = getModelFactory().getStringEncoder().fromString(p.getType(), clonedValueAsString);
								} catch (InvalidDataException e) {
									throw new ModelExecutionException(e);
								}
								List<?> l = (List<?>) clonedObjectHandler.invokeGetter(p);
								clonedObjectHandler.invokeAdder(p, clonedValue);
							} else if (getModelFactory().isModelEntity(p.getType()) && value instanceof CloneableProxyObject) {
								Object clonedValue = clonedObjects.get(value);
								if (!isPartOfContext(value, EmbeddingType.CLOSURE, context)) {
									clonedValue = null;
								}
								if (clonedValue != null) {
									clonedObjectHandler.internallyInvokeAdder(p, clonedValue);
								}
							}
							break;
						case REFERENCE:
							Object referenceValue = value != null ? clonedObjects.get(value) : null;
							if (referenceValue == null) {
								referenceValue = value;
							}
							clonedObjectHandler.internallyInvokeAdder(p, referenceValue);
							break;
						case FACTORY:
							// TODO Not implemented
							break;
						case IGNORE:
							break;
						}

					}
					break;
				default:
					break;
				}

			}
		} finally {
			clonedObjectHandler.createdByCloning = false;
		}

		return clonedObject;
	}

	/**
	 * Internally used for cloning computation This is the method which determine if a value belongs to derived object graph closure
	 */
	private boolean isPartOfContext(Object aValue, EmbeddingType embeddingType, Object... context) {
		if (context == null || context.length == 0) {
			return true;
		}

		for (Object o : context) {
			if (getModelFactory().isEmbedddedIn(o, aValue, embeddingType, context)) {
				return true;
			}
		}

		// System.out.println("Sorry "+aValue+" is not part of context "+context);

		return false;
	}

	/**
	 * Clone several object, using meta informations provided by related class All property should be annoted with a @CloningStrategy
	 * annotation which determine the way of handling this property
	 * 
	 * The list of objects is used as the context considered to determine the closure of objects graph being constructed during this
	 * operation. If a property is marked as @CloningStrategy.CLONE but lead to an object outside scope of cloning (the closure being
	 * computed), then resulting value is nullified.
	 * 
	 * @param someObjects
	 * @return
	 * @throws ModelExecutionException
	 * @throws ModelDefinitionException
	 * @throws CloneNotSupportedException
	 */
	protected List<Object> cloneObjects(Object... someObjects) throws ModelExecutionException, ModelDefinitionException,
			CloneNotSupportedException {
		if (someObjects != null && someObjects.length == 1 && someObjects[0].getClass().isArray()) {
			someObjects = (Object[]) someObjects[0];
		}

		for (Object o : someObjects) {
			if (!(o instanceof CloneableProxyObject)) {
				throw new CloneNotSupportedException();
			}
		}

		Hashtable<CloneableProxyObject, Object> clonedObjects = new Hashtable<CloneableProxyObject, Object>();

		for (Object o : someObjects) {
			ProxyMethodHandler<?> clonedObjectHandler = getModelFactory().getHandler(o);
			clonedObjectHandler.performClone(clonedObjects, someObjects);
		}

		for (CloneableProxyObject o : clonedObjects.keySet()) {
			ProxyMethodHandler<?> clonedObjectHandler = getModelFactory().getHandler(o);
			clonedObjectHandler.finalizeClone(clonedObjects, someObjects);
		}

		List<Object> returned = new ArrayList<Object>();
		for (int i = 0; i < someObjects.length; i++) {
			Object o = someObjects[i];
			returned.add(clonedObjects.get(o));
		}

		return returned;
	}

	protected Object paste(Clipboard clipboard) throws ModelExecutionException, ModelDefinitionException, CloneNotSupportedException {
		Collection<ModelProperty<? super I>> propertiesAssignableFrom = modelEntity.getPropertiesAssignableFrom(clipboard.getType());
		Collection<ModelProperty<? super I>> pastingPointProperties = Collections2.filter(propertiesAssignableFrom,
				new Predicate<ModelProperty<?>>() {
					@Override
					public boolean apply(ModelProperty<?> arg0) {
						return arg0.getAddPastingPoint() != null || arg0.getSetPastingPoint() != null;
					}
				});
		if (pastingPointProperties.size() == 0) {
			throw new ClipboardOperationException("Cannot paste here: no pasting point found");
		} else if (pastingPointProperties.size() > 1) {
			throw new ClipboardOperationException("Ambiguous pasting operations: several properties are compatible for pasting type "
					+ clipboard.getType());
		}
		return paste(clipboard, pastingPointProperties.iterator().next());
	}

	protected Object paste(Clipboard clipboard, ModelProperty<? super I> modelProperty) throws ModelExecutionException,
			ModelDefinitionException, CloneNotSupportedException {
		if (modelProperty.getSetPastingPoint() == null && modelProperty.getAddPastingPoint() == null) {
			throw new ClipboardOperationException("Cannot paste here: no pasting point found");
		}
		if (modelProperty.getSetPastingPoint() != null && modelProperty.getAddPastingPoint() != null) {
			throw new ClipboardOperationException("Ambiguous pasting operations: both add and set operations are available for property "
					+ modelProperty);
		}
		if (modelProperty.getSetPastingPoint() != null) {
			return paste(clipboard, modelProperty, modelProperty.getSetPastingPoint());
		} else {
			return paste(clipboard, modelProperty, modelProperty.getAddPastingPoint());
		}
	}

	protected Object paste(Clipboard clipboard, ModelProperty<? super I> modelProperty, PastingPoint pp) throws ModelExecutionException,
			ModelDefinitionException, CloneNotSupportedException {
		ModelEntity<?> entity = getModelEntity();
		if (pp == null) {
			throw new ClipboardOperationException("Cannot paste here: no pasting point found");
		}
		// System.out.println("Found pasting point: "+pp);
		if (modelProperty == null) {
			throw new ClipboardOperationException("Cannot paste here: no suitable property found for type " + clipboard.getType());
			// System.out.println("Found property: "+ppProperty);
		}

		Object clipboardContent = clipboard.getContents();
		if (getModelEntity().hasProperty(modelProperty)) {
			if (modelProperty.getSetPastingPoint() == pp) {
				if (!clipboard.isSingleObject()) {
					throw new ClipboardOperationException("Cannot paste here: multiple cardinality clipboard for a SINGLE property");
				}
				invokeSetter(modelProperty, clipboardContent);
			} else if (modelProperty.getAddPastingPoint() == pp) {
				if (clipboard.isSingleObject()) {
					invokeAdder(modelProperty, clipboardContent);
				} else {
					for (Object o : (List) clipboardContent) {
						invokeAdder(modelProperty, o);
					}
				}
			}
		}

		clipboard.consume();
		return clipboardContent;
	}

	@Override
	public String toString() {
		return internallyInvokeToString();
	}

	private String internallyInvokeToString() {
		StringBuilder sb = new StringBuilder();
		List<String> variables = new ArrayList<String>(values.keySet());
		Collections.sort(variables);
		for (String var : variables) {
			Object obj = values.get(var);
			String s = null;
			if (obj != null) {
				if (!(obj instanceof ProxyObject)) {
					s = indent(obj.toString(), var.length() + 1);
				} else {
					s = ((ProxyMethodHandler) ((ProxyObject) obj).getHandler()).getModelEntity().getImplementedInterface().getSimpleName();
				}

			}
			sb.append(var).append("=").append(s).append('\n');
		}
		return sb.toString();
	}

	private static String indent(String s, int indent) {
		if (indent > 0 && s != null) {
			String[] split = s.split("\n\r");
			if (split.length > 1) {
				StringBuilder sb = new StringBuilder();
				for (String string : split) {
					// TODO: optimize this
					for (int i = 0; i < indent; i++) {
						sb.append(' ');
					}
					sb.append(string).append('\n');
				}
				return sb.toString();
			}
		}
		return s;
	}

	public boolean isSerializing() {
		return serializing;
	}

	public void setSerializing(boolean serializing) throws ModelDefinitionException {
		if (this.serializing != serializing) {
			this.serializing = serializing;
			firePropertyChange(SERIALIZING, !serializing, serializing);
			if (!serializing) {
				setModified(false);
			}
		}
	}

	public boolean isDeserializing() {
		return deserializing;
	}

	public void setDeserializing(boolean deserializing) {
		if (this.deserializing != deserializing) {
			this.deserializing = deserializing;
			if (deserializing) {
				// At the begining of the deserialization process, we also need to mark the object as initialized
				initialized = true;
			} else {
				modified = false;
			}
			firePropertyChange(DESERIALIZING, !deserializing, deserializing);
		}
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) throws ModelDefinitionException {
		if (modified) {
			markAsModified();
		} else if (this.modified != modified) {
			this.modified = modified;
			firePropertyChange(MODIFIED, !modified, modified);
		}
	}
}