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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent.CustomComponentParameter;
import org.openflexo.fib.model.FIBCustom.FIBCustomImpl;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBCustomColumn.FIBCustomColumnImpl.class)
@XMLElement(xmlTag = "CustomColumn")
public interface FIBCustomColumn extends FIBTableColumn {

	@PropertyIdentifier(type = Class.class)
	public static final String COMPONENT_CLASS_KEY = "componentClass";
	@PropertyIdentifier(type = List.class)
	public static final String ASSIGNMENTS_KEY = "assignments";
	@PropertyIdentifier(type = String.class)
	public static final String CUSTOM_RENDERING_KEY = "customRendering";
	@PropertyIdentifier(type = String.class)
	public static final String DISABLE_TERMINATE_EDIT_ON_FOCUS_LOST = "disableTerminateEditOnFocusLost";

	@Getter(value = COMPONENT_CLASS_KEY)
	@XMLAttribute
	public Class<?> getComponentClass();

	@Setter(value = COMPONENT_CLASS_KEY)
	public void setComponentClass(Class<?> componentClass);

	@Getter(value = ASSIGNMENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBCustomAssignment.OWNER_KEY)
	public List<FIBCustomAssignment> getAssignments();

	@Setter(ASSIGNMENTS_KEY)
	public void setAssignments(List<FIBCustomAssignment> parameters);

	@Adder(ASSIGNMENTS_KEY)
	public void addToAssignments(FIBCustomAssignment aParameter);

	@Remover(ASSIGNMENTS_KEY)
	public void removeFromAssignments(FIBCustomAssignment aParameter);

	@Getter(value = CUSTOM_RENDERING_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isCustomRendering();

	@Setter(CUSTOM_RENDERING_KEY)
	public void setCustomRendering(boolean customRendering);

	@Getter(value = DISABLE_TERMINATE_EDIT_ON_FOCUS_LOST, defaultValue = "false")
	@XMLAttribute
	public boolean isDisableTerminateEditOnFocusLost();

	@Setter(DISABLE_TERMINATE_EDIT_ON_FOCUS_LOST)
	public void setDisableTerminateEditOnFocusLost(boolean disableTerminateEditOnFocusLost);

	public BindingModel getCustomComponentBindingModel();

	public static abstract class FIBCustomColumnImpl extends FIBTableColumnImpl implements FIBCustomColumn {

		private static final Logger logger = Logger.getLogger(FIBCustomColumn.class.getPackage().getName());

		private Class componentClass;
		private boolean customRendering = false;

		private boolean disableTerminateEditOnFocusLost = false;

		private Vector<FIBCustomAssignment> assignments;

		public FIBCustomColumnImpl() {
			assignments = new Vector<FIBCustomAssignment>();
		}

		@Override
		public Class<?> getComponentClass() {
			return componentClass;

		}

		@Override
		public void setComponentClass(Class componentClass) {
			FIBPropertyNotification<Class> notification = requireChange(COMPONENT_CLASS_KEY, componentClass);
			if (notification != null) {
				this.componentClass = componentClass;
				if (componentClass != null) {
					createCustomComponentBindingModel();
					for (Method m : componentClass.getMethods()) {
						CustomComponentParameter annotation = m.getAnnotation(CustomComponentParameter.class);
						if (annotation != null) {
							String variableName = FIBCustomImpl.COMPONENT_NAME + "." + annotation.name();
							if (!hasAssignment(variableName)) {
								FIBCustomAssignment newAssignment = getFactory().newInstance(FIBCustomAssignment.class);
								newAssignment.setOwner(this);
								newAssignment.setMandatory(annotation.type() == CustomComponentParameter.Type.MANDATORY);
								newAssignment.setVariable(new DataBinding<Object>(variableName));
								newAssignment.setValue(null);
								addToAssignments(newAssignment);
							}
						}

					}
				}
				hasChanged(notification);
			}
		}

		@Override
		public Type getDefaultDataClass() {
			return Object.class;
		}

		public boolean hasAssignment(String variableName) {
			return getAssignent(variableName) != null;
		}

		public FIBCustomAssignment getAssignent(String variableName) {
			for (FIBCustomAssignment a : assignments) {
				if (variableName.equals(a.getVariable().toString())) {
					return a;
				}
			}
			return null;
		}

		@Override
		public Vector<FIBCustomAssignment> getAssignments() {
			return assignments;
		}

		public void setAssignments(Vector<FIBCustomAssignment> assignments) {
			this.assignments = assignments;
		}

		@Override
		public void addToAssignments(FIBCustomAssignment p) {
			if (getAssignent(p.getVariable().toString()) != null) {
				removeFromAssignments(getAssignent(p.getVariable().toString()));
			}
			p.setOwner(this);
			assignments.add(p);
		}

		@Override
		public void removeFromAssignments(FIBCustomAssignment p) {
			p.setOwner(null);
			assignments.remove(p);
		}

		@Override
		public boolean isCustomRendering() {
			return customRendering;
		}

		@Override
		public void setCustomRendering(boolean customRendering) {
			this.customRendering = customRendering;
		}

		@Override
		public boolean isDisableTerminateEditOnFocusLost() {
			return disableTerminateEditOnFocusLost;
		}

		@Override
		public void setDisableTerminateEditOnFocusLost(boolean disableTerminateEditOnFocusLost) {
			this.disableTerminateEditOnFocusLost = disableTerminateEditOnFocusLost;
		}

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

			customComponentBindingModel.addToBindingVariables(new BindingVariable(FIBCustomImpl.COMPONENT_NAME, getComponentClass()));
			// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);
		}

		@Override
		public void finalizeTableDeserialization() {
			super.finalizeTableDeserialization();
			for (FIBCustomAssignment assign : getAssignments()) {
				assign.finalizeDeserialization();
			}
		}

		@Override
		public ColumnType getColumnType() {
			return ColumnType.Custom;
		}

	}

	@ModelEntity
	@ImplementationClass(FIBCustomAssignment.FIBCustomAssignmentImpl.class)
	@XMLElement(xmlTag = "ColumnAssignment")
	public static interface FIBCustomAssignment extends FIBModelObject {
		@PropertyIdentifier(type = FIBCustomColumn.class)
		public static final String OWNER_KEY = "owner";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VARIABLE_KEY = "variable";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VALUE_KEY = "value";
		@PropertyIdentifier(type = Boolean.class)
		public static final String MANDATORY_KEY = "mandatory";

		@Getter(value = OWNER_KEY, inverse = FIBCustomColumn.ASSIGNMENTS_KEY)
		public FIBCustomColumn getOwner();

		@Setter(OWNER_KEY)
		public void setOwner(FIBCustomColumn customColumn);

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
			public void setOwner(FIBCustomColumn customColumn) {
				performSuperSetter(OWNER_KEY, customColumn);
				if (value != null) {
					value.setOwner(customColumn);
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
