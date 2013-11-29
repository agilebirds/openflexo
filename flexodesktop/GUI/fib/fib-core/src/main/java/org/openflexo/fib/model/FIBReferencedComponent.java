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

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;

public class FIBReferencedComponent extends FIBWidget {

	private static final Logger logger = Logger.getLogger(FIBReferencedComponent.class.getPackage().getName());

	private File componentFile;
	private DataBinding<File> dynamicComponentFile;

	public static enum Parameters implements FIBModelAttribute {
		componentFile, dynamicComponentFile, assignments
	}

	// TODO: Should be moved to FIBReferencedComponent widget
	// private FIBComponent referencedComponent;
	private Vector<FIBReferenceAssignment> assignments;

	public FIBReferencedComponent() {
		assignments = new Vector<FIBReferenceAssignment>();
	}

	@Override
	protected String getBaseName() {
		return "ReferencedComponent";
	}

	@Override
	public Type getDefaultDataClass() {
		/*if (referencedComponent != null) {
			return referencedComponent.getDataType();
		}*/
		return Object.class;
	}

	public File getComponentFile() {
		return componentFile;
	}

	public void setComponentFile(File componentFile) {
		FIBAttributeNotification<File> notification = requireChange(Parameters.componentFile, componentFile);
		if (notification != null) {
			this.componentFile = componentFile;
			// component = null;
			notify(notification);
		}
	}

	public DataBinding<File> getDynamicComponentFile() {

		if (dynamicComponentFile == null) {
			dynamicComponentFile = new DataBinding<File>(this, File.class, DataBinding.BindingDefinitionType.GET);
			dynamicComponentFile.setBindingName("componentFile");
			dynamicComponentFile.setCacheable(true);
		}
		return dynamicComponentFile;
	}

	public void setDynamicComponentFile(DataBinding<File> dynamicComponentFile) {

		FIBAttributeNotification<DataBinding<File>> notification = requireChange(Parameters.dynamicComponentFile, dynamicComponentFile);

		if (notification != null) {

			if (dynamicComponentFile != null) {
				dynamicComponentFile.setOwner(this);
				dynamicComponentFile.setDeclaredType(File.class);
				dynamicComponentFile.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				dynamicComponentFile.setBindingName("dynamicComponentFile");
				dynamicComponentFile.setCacheable(true);
			}

			this.dynamicComponentFile = dynamicComponentFile;

			// referencedComponent = null;
			notify(notification);
		}

	}

	@Override
	public Type getDataType() {
		/*if (referencedComponent != null) {
			return referencedComponent.getDataType();
		}*/
		return super.getDataType();
	}

	public boolean hasAssignment(String variableName) {
		return getAssignment(variableName) != null;
	}

	public FIBReferenceAssignment getAssignment(String variableName) {
		for (FIBReferenceAssignment a : assignments) {
			if (variableName != null && variableName.equals(a.getVariable().toString())) {
				return a;
			}
		}
		return null;
	}

	public Vector<FIBReferenceAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(Vector<FIBReferenceAssignment> assignments) {
		this.assignments = assignments;
	}

	public void addToAssignments(FIBReferenceAssignment a) {
		if (getAssignment(a.getVariable().toString()) != null) {
			removeFromAssignments(getAssignment(a.getVariable().toString()));
		}
		a.setReferencedComponent(this);
		assignments.add(a);
		getPropertyChangeSupport().firePropertyChange(Parameters.assignments.name(), null, assignments);
	}

	public void removeFromAssignments(FIBReferenceAssignment a) {
		a.setReferencedComponent(null);
		assignments.remove(a);
		getPropertyChangeSupport().firePropertyChange(Parameters.assignments.name(), null, assignments);
	}

	@Override
	public void finalizeDeserialization() {
		super.finalizeDeserialization();
		for (FIBReferenceAssignment assign : getAssignments()) {
			assign.finalizeDeserialization();
		}
	}

	@Override
	public List<DataBinding<?>> getDependencyBindings() {
		List<DataBinding<?>> returned = super.getDependencyBindings();
		if (getDynamicComponentFile().isSet()) {
			returned.add(getDynamicComponentFile());
		}
		return returned;
	}

	/*
	@Override
	public Boolean getManageDynamicModel() {
		return true;
	}
	 */

	public FIBReferenceAssignment createAssignment() {
		logger.info("Called createAssignment()");
		FIBReferenceAssignment newAssignment = new FIBReferenceAssignment();
		addToAssignments(newAssignment);
		return newAssignment;
	}

	public FIBReferenceAssignment deleteAssignment(FIBReferenceAssignment assignment) {
		logger.info("Called deleteAssignment() with " + assignment);
		removeFromAssignments(assignment);
		return assignment;
	}

	public static class FIBReferenceAssignment extends FIBModelObject {
		@Deprecated
		public static BindingDefinition VARIABLE = new BindingDefinition("variable", Object.class,
				DataBinding.BindingDefinitionType.GET_SET, true);
		@Deprecated
		public BindingDefinition VALUE = new BindingDefinition("value", Object.class, DataBinding.BindingDefinitionType.GET, true);

		public static enum Parameters implements FIBModelAttribute {
			variable, value
		}

		private FIBReferencedComponent custom;

		private DataBinding<?> variable;
		private DataBinding<?> value;

		private boolean mandatory = true;

		@Override
		public String toString() {
			return "assignement(" + variable + "=" + value + ")";
		}

		public FIBReferenceAssignment() {
		}

		public FIBReferenceAssignment(FIBReferencedComponent custom, DataBinding<?> variable, DataBinding<?> value, boolean mandatory) {
			this();
			this.custom = custom;
			this.mandatory = mandatory;
			setVariable(variable);
			setValue(value);
		}

		public boolean isMandatory() {
			return mandatory;
		}

		public FIBReferencedComponent getReferencedComponent() {
			return custom;
		}

		private void setReferencedComponent(FIBReferencedComponent custom) {
			this.custom = custom;
			if (value != null) {
				value.setOwner(getReferencedComponent());
			}
		}

		@Override
		public FIBComponent getComponent() {
			return getReferencedComponent();
		}

		public DataBinding<?> getVariable() {
			if (variable == null) {
				variable = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET_SET);
			}
			return variable;
		}

		public void setVariable(DataBinding<?> variable) {
			if (variable != null) {
				variable.setOwner(this);
				variable.setDeclaredType(Object.class);
				variable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
			}
			this.variable = variable;
			if (custom != null && variable != null) {
				variable.decode();
			}
			/*if (variable.isValid()) {
				VALUE.setType(variable.getAnalyzedType());
				if (value != null) {
					value.setBindingDefinition(VALUE);
				}
			}*/
		}

		public DataBinding<?> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(getReferencedComponent(), Object.class, DataBinding.BindingDefinitionType.GET);
			}
			return value;
		}

		public void setValue(DataBinding<?> value) {
			if (value != null) {
				value.setOwner(getReferencedComponent()); // Warning, still null while deserializing
				value.setDeclaredType(Object.class);
				value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
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
				value.setOwner(getReferencedComponent());
				value.decode();
			}
		}

		// TODO: mettre un cache avec le Binding Model du fils peut-être?
		// + gérer l'agrégation avec le père?

		@Override
		public BindingModel getBindingModel() {
			if (getReferencedComponent() != null) {
				return getReferencedComponent().getBindingModel();
			}
			return null;
		}

		@Override
		public List<? extends FIBModelObject> getEmbeddedObjects() {
			return null;
		}

	}

	public static class DynamicComponentFileBindingMustBeValid extends BindingMustBeValid<FIBReferencedComponent> {
		public DynamicComponentFileBindingMustBeValid() {
			super("'dynamic_componentFile'_binding_is_not_valid", FIBReferencedComponent.class);
		}

		@Override
		public DataBinding<File> getBinding(FIBReferencedComponent object) {
			return object.getDynamicComponentFile();
		}

	}

}
