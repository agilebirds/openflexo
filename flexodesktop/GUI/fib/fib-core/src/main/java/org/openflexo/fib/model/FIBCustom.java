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
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.view.widget.FIBCustomWidget;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

@ModelEntity
@ImplementationClass(FIBCustom.FIBCustomImpl.class)
@XMLElement(xmlTag = "Custom")
public interface FIBCustom extends FIBWidget {

	public static interface FIBCustomComponent<V, C extends JComponent> {
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

		public V getEditedObject();

		public void setEditedObject(V object);

		public V getRevertValue();

		public void setRevertValue(V object);

		public void addApplyCancelListener(ApplyCancelListener l);

		public void removeApplyCancelListener(ApplyCancelListener l);

		public Class<V> getRepresentedType();

		public void init(FIBCustom component, FIBController controller);

		public void delete();
	}

	@PropertyIdentifier(type = Class.class)
	public static final String COMPONENT_CLASS_KEY = "componentClass";
	@PropertyIdentifier(type = Vector.class)
	public static final String ASSIGNMENTS_KEY = "assignments";

	@Getter(value = COMPONENT_CLASS_KEY)
	@XMLAttribute(xmlTag = "componentClassName")
	public Class getComponentClass();

	@Setter(COMPONENT_CLASS_KEY)
	public void setComponentClass(Class componentClass);

	@Getter(value = ASSIGNMENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBCustomAssignment.OWNER_KEY)
	public List<FIBCustomAssignment> getAssignments();

	@Setter(ASSIGNMENTS_KEY)
	public void setAssignments(List<FIBCustomAssignment> assignments);

	@Adder(ASSIGNMENTS_KEY)
	public void addToAssignments(FIBCustomAssignment aAssignment);

	@Remover(ASSIGNMENTS_KEY)
	public void removeFromAssignments(FIBCustomAssignment aAssignment);

	@Finder(collection = ASSIGNMENTS_KEY, attribute = FIBCustomAssignment.VARIABLE_KEY)
	public FIBCustomAssignment getAssignent(String variableName);

	public BindingModel getCustomComponentBindingModel();

	public static abstract class FIBCustomImpl extends FIBWidgetImpl implements FIBCustom {

		private static final Logger logger = Logger.getLogger(FIBCustom.class.getPackage().getName());

		public static final String COMPONENT_NAME = "component";

		private Class componentClass;
		private Class defaultDataClass = null;

		private List<FIBCustomAssignment> assignments;

		public FIBCustomImpl() {
			assignments = new Vector<FIBCustomAssignment>();
		}

		@Override
		public String getBaseName() {
			return "CustomSelector";
		}

		@Override
		public Type getDataType() {
			/*if (getData() != null && getData().isSet() && getData().isValid()) {
				return getData().getAnalyzedType();
			}*/
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

		@Override
		public Class getComponentClass() {
			return componentClass;

		}

		@Override
		public void setComponentClass(Class componentClass) {
			FIBPropertyNotification<Class> notification = requireChange(COMPONENT_CLASS_KEY, componentClass);
			if (notification != null) {
				this.componentClass = componentClass;
				if (componentClass != null) {
					customComponentBindingModel = null;
					createCustomComponentBindingModel();
					for (Method m : componentClass.getMethods()) {
						FIBCustomComponent.CustomComponentParameter annotation = m
								.getAnnotation(FIBCustomComponent.CustomComponentParameter.class);
						if (annotation != null) {
							String variableName = COMPONENT_NAME + "." + annotation.name();
							if (!hasAssignment(variableName)) {
								FIBCustomAssignment newAssigment = getFactory().newInstance(FIBCustomAssignment.class);
								newAssigment.setOwner(this);
								newAssigment.setVariable(new DataBinding<Object>(variableName));
								newAssigment.setValue(null);
								newAssigment.setMandatory(annotation.type() == FIBCustomComponent.CustomComponentParameter.Type.MANDATORY);
								addToAssignments(newAssigment);
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

		@Override
		public FIBCustomAssignment getAssignent(String variableName) {
			for (FIBCustomAssignment a : assignments) {
				if (variableName != null && variableName.equals(a.getVariable().toString())) {
					return a;
				}
			}
			return null;
		}

		@Override
		public List<FIBCustomAssignment> getAssignments() {
			return assignments;
		}

		@Override
		public void setAssignments(List<FIBCustomAssignment> assignments) {
			this.assignments = assignments;
		}

		@Override
		public void addToAssignments(FIBCustomAssignment a) {
			if (getAssignent(a.getVariable().toString()) != null) {
				performSuperAdder(ASSIGNMENTS_KEY, a);
				removeFromAssignments(getAssignent(a.getVariable().toString()));
				return;
			}
			/*a.setOwner(this);
			assignments.add(a);
			getPropertyChangeSupport().firePropertyChange(ASSIGNMENTS_KEY, null, assignments);*/
			performSuperAdder(ASSIGNMENTS_KEY, a);
		}

		/*@Override
		public void removeFromAssignments(FIBCustomAssignment a) {
			a.setOwner(null);
			assignments.remove(a);
			getPropertyChangeSupport().firePropertyChange(ASSIGNMENTS_KEY, null, assignments);
		}*/

		private BindingModel customComponentBindingModel;

		@Override
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
			return new ParameterizedTypeImpl(FIBCustomWidget.class, args);
		}

		@Override
		public Boolean getManageDynamicModel() {
			return true;
		}

		public FIBCustomAssignment createAssignment() {
			logger.info("Called createAssignment()");
			FIBCustomAssignment newAssignment = getFactory().newInstance(FIBCustomAssignment.class);
			addToAssignments(newAssignment);
			return newAssignment;
		}

		public FIBCustomAssignment deleteAssignment(FIBCustomAssignment assignment) {
			logger.info("Called deleteAssignment() with " + assignment);
			removeFromAssignments(assignment);
			return assignment;
		}

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

	@ModelEntity
	@ImplementationClass(FIBCustomAssignment.FIBCustomAssignmentImpl.class)
	@XMLElement(xmlTag = "Assignment")
	public static interface FIBCustomAssignment extends FIBModelObject {
		@PropertyIdentifier(type = FIBCustomColumn.class)
		public static final String OWNER_KEY = "owner";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VARIABLE_KEY = "variable";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VALUE_KEY = "value";
		@PropertyIdentifier(type = Boolean.class)
		public static final String MANDATORY_KEY = "mandatory";

		@Getter(value = OWNER_KEY, inverse = FIBCustom.ASSIGNMENTS_KEY)
		public FIBCustom getOwner();

		@Setter(OWNER_KEY)
		public void setOwner(FIBCustom customColumn);

		@Getter(value = VARIABLE_KEY)
		@XMLAttribute
		public DataBinding<Object> getVariable();

		@Setter(VARIABLE_KEY)
		public void setVariable(DataBinding<Object> variable);

		@Getter(value = VALUE_KEY)
		@XMLAttribute
		public DataBinding<Object> getValue();

		@Setter(VALUE_KEY)
		public void setValue(DataBinding<Object> value);

		@Getter(value = MANDATORY_KEY, defaultValue = "false")
		@XMLAttribute
		public boolean isMandatory();

		@Setter(MANDATORY_KEY)
		public void setMandatory(boolean mandatory);

		@DeserializationFinalizer
		public void finalizeDeserialization();

		public static abstract class FIBCustomAssignmentImpl extends FIBModelObjectImpl implements FIBCustomAssignment {
			@Deprecated
			public static BindingDefinition VARIABLE = new BindingDefinition("variable", Object.class,
					DataBinding.BindingDefinitionType.GET_SET, true);
			@Deprecated
			public BindingDefinition VALUE = new BindingDefinition("value", Object.class, DataBinding.BindingDefinitionType.GET, true);

			private DataBinding<Object> variable;
			private DataBinding<Object> value;

			private final boolean mandatory = true;

			public FIBCustomAssignmentImpl() {
			}

			/*public FIBCustomAssignmentImpl(FIBCustomColumn customColumn, DataBinding<Object> variable, DataBinding<Object> value,
					boolean mandatory) {
				this();
				this.mandatory = mandatory;
				setVariable(variable);
				setValue(value);
			}*/

			@Override
			public boolean isMandatory() {
				return mandatory;
			}

			@Override
			public void setOwner(FIBCustom custom) {
				performSuperSetter(OWNER_KEY, custom);
				if (value != null) {
					value.setOwner(custom);
				}
			}

			@Override
			public FIBComponent getComponent() {
				if (getOwner() != null) {
					return getOwner().getComponent();
				}
				return null;
			}

			@Override
			public DataBinding<Object> getVariable() {
				if (variable == null) {
					variable = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET_SET);
				}
				return variable;
			}

			@Override
			public void setVariable(DataBinding<Object> variable) {
				if (variable != null) {
					variable.setOwner(this);
					variable.setDeclaredType(Object.class);
					variable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
				}
				this.variable = variable;
				if (getOwner() != null && variable != null) {
					variable.decode();
				}
				if (variable.isValid()) {
					VALUE.setType(variable.getAnalyzedType());
					if (value != null) {
						value.setBindingDefinition(VALUE);
					}
				}
			}

			@Override
			public DataBinding<Object> getValue() {
				if (value == null) {
					value = new DataBinding<Object>(getOwner(), Object.class, DataBinding.BindingDefinitionType.GET);
				}
				return value;
			}

			@Override
			public void setValue(DataBinding<Object> value) {
				if (value != null) {
					value.setOwner(getOwner()); // Warning, still null while deserializing
					value.setDeclaredType(Object.class);
					value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					this.value = value;
				} else {
					getValue();
				}
			}

			@Override
			public void finalizeDeserialization() {

				if (variable != null) {
					variable.decode();
				}
				if (value != null) {
					value.setOwner(getOwner());
					value.decode();
				}
			}

			@Override
			public BindingModel getBindingModel() {
				if (getOwner() != null) {
					return getOwner().getCustomComponentBindingModel();
				}
				return null;
			}

		}
	}

}
