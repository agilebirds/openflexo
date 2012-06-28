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
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;

public class FIBCustomColumn extends FIBTableColumn {

	private static final Logger logger = Logger.getLogger(FIBCustomColumn.class.getPackage().getName());

	public static enum Parameters implements FIBModelAttribute {
		componentClass, assignments, customRendering, disableTerminateEditOnFocusLost
	}

	private Class componentClass;
	public boolean customRendering = false;
	public boolean disableTerminateEditOnFocusLost = false;

	private Vector<FIBCustomAssignment> assignments;

	public FIBCustomColumn() {
		assignments = new Vector<FIBCustomAssignment>();
	}

	public Class<?> getComponentClass() {
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
						String variableName = FIBCustom.COMPONENT_NAME + "." + annotation.name();
						if (!hasAssignment(variableName)) {
							addToAssignments(new FIBCustomAssignment(this, new DataBinding(variableName), null,
									annotation.type() == FIBCustomComponent.CustomComponentParameter.Type.MANDATORY));
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

	public Vector<FIBCustomAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(Vector<FIBCustomAssignment> assignments) {
		this.assignments = assignments;
	}

	public void addToAssignments(FIBCustomAssignment p) {
		if (getAssignent(p.getVariable().toString()) != null) {
			removeFromAssignments(getAssignent(p.getVariable().toString()));
		}
		p.setCustomColumn(this);
		assignments.add(p);
	}

	public void removeFromAssignments(FIBCustomAssignment p) {
		p.setCustomColumn(null);
		assignments.remove(p);
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

		customComponentBindingModel.addToBindingVariables(new BindingVariableImpl(this, FIBCustom.COMPONENT_NAME, getComponentClass()));
		// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);
	}

	@Override
	public void finalizeTableDeserialization() {
		super.finalizeTableDeserialization();
		for (FIBCustomAssignment assign : getAssignments()) {
			assign.finalizeDeserialization();
		}
	}

	public static class FIBCustomAssignment extends FIBModelObject {
		public static BindingDefinition VARIABLE = new BindingDefinition("variable", Object.class, BindingDefinitionType.GET_SET, true);
		public BindingDefinition VALUE = new BindingDefinition("value", Object.class, BindingDefinitionType.GET, true);

		public static enum Parameters implements FIBModelAttribute {
			variable, value
		}

		private FIBCustomColumn custom;

		private DataBinding variable;
		private DataBinding value;

		private boolean mandatory = true;

		public FIBCustomAssignment() {
		}

		public FIBCustomAssignment(FIBCustomColumn customColumn, DataBinding variable, DataBinding value, boolean mandatory) {
			this();
			this.custom = customColumn;
			this.mandatory = mandatory;
			setVariable(variable);
			setValue(value);
		}

		public boolean isMandatory() {
			return mandatory;
		}

		public FIBCustomColumn getCustomColumn() {
			return custom;
		}

		private void setCustomColumn(FIBCustomColumn custom) {
			this.custom = custom;
			if (value != null) {
				value.setOwner(getCustomColumn());
				value.setBindingAttribute(Parameters.value);
			}
		}

		@Override
		public FIBComponent getRootComponent() {
			if (getCustomColumn() != null) {
				return getCustomColumn().getRootComponent();
			}
			return null;
		}

		public DataBinding getVariable() {
			if (variable == null) {
				variable = new DataBinding(this, Parameters.variable, VARIABLE);
			}
			return variable;
		}

		public void setVariable(DataBinding variable) {
			variable.setOwner(this);
			variable.setBindingAttribute(Parameters.variable);
			variable.setBindingDefinition(VARIABLE);
			this.variable = variable;
			if (custom != null) {
				variable.getBinding();
			}
			if (variable.hasBinding()) {
				VALUE.setType(variable.getBinding().getAccessedType());
				if (value != null) {
					value.setBindingDefinition(VALUE);
				}
			}
		}

		public DataBinding getValue() {
			if (value == null) {
				value = new DataBinding(this, Parameters.value, VALUE);
			}
			return value;
		}

		public void setValue(DataBinding value) {
			if (value != null) {
				value.setOwner(getCustomColumn()); // Warning, still null while deserializing
				value.setBindingAttribute(Parameters.value); // Warning, still null while deserializing
				value.setBindingDefinition(VALUE);
				this.value = value;
			} else {
				getValue();
			}
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();

			if (variable != null) {
				variable.finalizeDeserialization();
			}
			if (value != null) {
				value.setOwner(getCustomColumn());
				value.setBindingAttribute(Parameters.value);
				value.finalizeDeserialization();
			}
		}

		@Override
		public BindingModel getBindingModel() {
			if (getCustomColumn() != null) {
				return getCustomColumn().getCustomComponentBindingModel();
			}
			return null;
		}

		@Override
		public List<? extends FIBModelObject> getEmbeddedObjects() {
			return null;
		}

	}

	@Override
	public ColumnType getColumnType() {
		return ColumnType.Custom;
	}

}
