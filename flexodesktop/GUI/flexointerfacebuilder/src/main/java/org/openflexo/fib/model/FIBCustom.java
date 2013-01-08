/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.model;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBCustomDynamicModel;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

public class FIBCustom extends FIBWidget {

	private static final Logger logger = Logger.getLogger(FIBCustom.class.getPackage().getName());

	public static enum Parameters implements FIBModelAttribute {
		componentClass, assignments
	}

	public static final String COMPONENT_NAME = "component";

	public static interface FIBCustomComponent<T, C extends JComponent> {
		@Documented
		@Retention(RetentionPolicy.RUNTIME)
		public @interface CustomComponentParameter {
			/** name of parameter */
			String name();

			/** type of parameter */
			Type type();

			/** Enumeration of different types */
			public static enum Type {
				MANDATORY, OPTIONAL
			};
		}

		public C getJComponent();

		public T getEditedObject();

		public void setEditedObject(T object);

		public T getRevertValue();

		public void setRevertValue(T object);

		public void addApplyCancelListener(ApplyCancelListener l);

		public void removeApplyCancelListener(ApplyCancelListener l);

		public Class<T> getRepresentedType();

		public void init(FIBCustom component, FIBController controller);

	}

	private Class componentClass;
	private Class defaultDataClass = null;

	private Vector<FIBCustomAssignment> assignments;

	public FIBCustom() {
		assignments = new Vector<FIBCustomAssignment>();
	}

	@Override
	protected String getBaseName() {
		return "CustomSelector";
	}

	@Override
	public Type getDataType() {
		if (getData() != null && getData().isSet() && getData().isValid()) {
			return getData().getAnalyzedType();
		}
		return getDefaultDataClass();

	}

	@Override
	public Type getDefaultDataClass() {
		if (getComponentClass() != null && defaultDataClass == null) {
			defaultDataClass = getDataClassForComponent(getComponentClass());
		}
		if (defaultDataClass != null) {
			return defaultDataClass;
		}
		return Object.class;
	}

	public Class getComponentClass() {
		return componentClass;

	}

	public void setComponentClass(Class componentClass) {
		FIBAttributeNotification<Class> notification = requireChange(Parameters.componentClass, componentClass);
		if (notification != null) {
			this.componentClass = componentClass;
			if (componentClass != null) {
				createCustomComponentBindingModel();
				for (Method m : componentClass.getMethods()) {
					FIBCustomComponent.CustomComponentParameter annotation = m
							.getAnnotation(FIBCustomComponent.CustomComponentParameter.class);
					if (annotation != null) {
						String variableName = COMPONENT_NAME + "." + annotation.name();
						if (!hasAssignment(variableName)) {
							addToAssignments(new FIBCustomAssignment(this, new DataBinding<Object>(variableName), null,
									annotation.type() == FIBCustomComponent.CustomComponentParameter.Type.MANDATORY));
						}
					}

				}
			}
			hasChanged(notification);
		}
	}

	public boolean hasAssignment(String variableName) {
		return getAssignent(variableName) != null;
	}

	public FIBCustomAssignment getAssignent(String variableName) {
		for (FIBCustomAssignment a : assignments) {
			if (variableName != null && variableName.equals(a.getVariable().toString())) {
				return a;
			}
		}
		return null;
	}

	public Vector<FIBCustomAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(Vector<FIBCustomAssignment> assignments) {
		this.assignments = assignments;
	}

	public void addToAssignments(FIBCustomAssignment a) {
		if (getAssignent(a.getVariable().toString()) != null) {
			removeFromAssignments(getAssignent(a.getVariable().toString()));
		}
		a.setCustom(this);
		assignments.add(a);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBCustomAssignment>(Parameters.assignments, a));
	}

	public void removeFromAssignments(FIBCustomAssignment a) {
		a.setCustom(null);
		assignments.remove(a);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBCustomAssignment>(Parameters.assignments, a));
	}

	private BindingModel customComponentBindingModel;

	public BindingModel getCustomComponentBindingModel() {
		if (customComponentBindingModel == null) {
			createCustomComponentBindingModel();
		}
		return customComponentBindingModel;
	}

	private void createCustomComponentBindingModel() {
		customComponentBindingModel = new BindingModel();

		customComponentBindingModel.addToBindingVariables(new BindingVariable(COMPONENT_NAME, getComponentClass()));
		// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);
	}

	@Override
	public void finalizeDeserialization() {
		super.finalizeDeserialization();
		for (FIBCustomAssignment assign : getAssignments()) {
			assign.finalizeDeserialization();
		}
	}

	@Override
	public Type getDynamicAccessType() {
		Type[] args = new Type[2];
		args[0] = getDataType();
		args[1] = getComponentClass();
		return new ParameterizedTypeImpl(FIBCustomDynamicModel.class, args);
	}

	@Override
	public Boolean getManageDynamicModel() {
		return true;
	}

	public FIBCustomAssignment createAssignment() {
		logger.info("Called createAssignment()");
		FIBCustomAssignment newAssignment = new FIBCustomAssignment();
		addToAssignments(newAssignment);
		return newAssignment;
	}

	public FIBCustomAssignment deleteAssignment(FIBCustomAssignment assignment) {
		logger.info("Called deleteAssignment() with " + assignment);
		removeFromAssignments(assignment);
		return assignment;
	}

	public static class FIBCustomAssignment extends FIBModelObject {
		@Deprecated
		public static BindingDefinition VARIABLE = new BindingDefinition("variable", Object.class, BindingDefinitionType.GET_SET, true);
		@Deprecated
		public BindingDefinition VALUE = new BindingDefinition("value", Object.class, BindingDefinitionType.GET, true);

		public static enum Parameters implements FIBModelAttribute {
			variable, value
		}

		private FIBCustom custom;

		private DataBinding<Object> variable;
		private DataBinding<Object> value;

		private boolean mandatory = true;

		@Override
		public String toString() {
			return "assignement(" + variable + "=" + value + ")";
		}

		public FIBCustomAssignment() {
		}

		public FIBCustomAssignment(FIBCustom custom, DataBinding<Object> variable, DataBinding<Object> value, boolean mandatory) {
			this();
			this.custom = custom;
			this.mandatory = mandatory;
			setVariable(variable);
			setValue(value);
		}

		public boolean isMandatory() {
			return mandatory;
		}

		public FIBCustom getCustom() {
			return custom;
		}

		private void setCustom(FIBCustom custom) {
			this.custom = custom;
			if (value != null) {
				value.setOwner(getCustom());
			}
		}

		@Override
		public FIBComponent getRootComponent() {
			if (getCustom() != null) {
				return getCustom().getRootComponent();
			}
			return null;
		}

		public DataBinding<Object> getVariable() {
			if (variable == null) {
				variable = new DataBinding<Object>(this, Object.class, BindingDefinitionType.GET_SET);
			}
			return variable;
		}

		public void setVariable(DataBinding<Object> variable) {
			if (variable != null) {
				variable.setOwner(this);
				variable.setDeclaredType(Object.class);
				variable.setBindingDefinitionType(BindingDefinitionType.GET_SET);
			}
			this.variable = variable;
			if (custom != null && variable != null) {
				variable.decode();
			}
			logger.warning("Please check that something is not missing HERE");
			/*if (variable.isValid()) {
				VALUE.setType(variable.getAnalyzedType());
				if (value != null) {
					value.setBindingDefinition(VALUE);
				}
			}*/
		}

		public DataBinding<Object> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(getCustom(), Object.class, BindingDefinitionType.GET);
			}
			return value;
		}

		public void setValue(DataBinding<Object> value) {
			if (value != null) {
				value.setOwner(getCustom()); // Warning, still null while deserializing
				value.setDeclaredType(Object.class);
				value.setBindingDefinitionType(BindingDefinitionType.GET);
				this.value = value;
			} else {
				getValue();
			}
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();

			if (variable != null) {
				variable.decode();
			}
			if (value != null) {
				value.setOwner(getCustom());
				value.decode();
			}
		}

		@Override
		public BindingModel getBindingModel() {
			if (getCustom() != null) {
				return getCustom().getCustomComponentBindingModel();
			}
			return null;
		}

		@Override
		public List<? extends FIBModelObject> getEmbeddedObjects() {
			return null;
		}

	}

	/*public String getComponentClassName()
	{
		return componentClassName;
	}

	public void setComponentClassName(String componentClassName)
	{
		FIBAttributeNotification<String> notification = requireChange(
				Parameters.componentClassName, componentClassName);
		if (notification != null) {
			this.componentClassName = componentClassName;
			defaultDataClass = null;
			hasChanged(notification);
		}
	}*/

	private static final Hashtable<Class<?>, Class<?>> DATA_CLASS_FOR_COMPONENT = new Hashtable<Class<?>, Class<?>>();

	/**
	 * Stuff to retrieve default data class from component class<br>
	 * NB: this is STATIC !!!!
	 * 
	 * @param componentClass
	 * @return
	 */
	private static Class<?> getDataClassForComponent(Class<?> componentClass) {
		Class<?> returned = DATA_CLASS_FOR_COMPONENT.get(componentClass);
		if (returned == null) {
			logger.fine("Searching dataClass for " + componentClass);
			FIBCustomComponent customComponent = null;
			for (Constructor constructor : componentClass.getConstructors()) {
				if (constructor.getGenericParameterTypes().length == 1) {
					Object[] args = new Object[1];
					args[0] = null;
					try {
						customComponent = (FIBCustomComponent) constructor.newInstance(args);
						break;
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.warning("While trying to instanciate " + componentClass + " with null");
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassCastException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			logger.fine("customComponent=" + customComponent);
			if (customComponent != null) {
				returned = customComponent.getRepresentedType();
				DATA_CLASS_FOR_COMPONENT.put(componentClass, returned);
				logger.fine("Found " + returned);
				return returned;
			}
			return Object.class;
		}
		return returned;
	}

}
