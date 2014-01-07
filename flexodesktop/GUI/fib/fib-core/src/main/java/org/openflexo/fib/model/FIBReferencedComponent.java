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
@ImplementationClass(FIBReferencedComponent.FIBReferencedComponentImpl.class)
@XMLElement(xmlTag = "FIBReferencedComponent")
public interface FIBReferencedComponent extends FIBWidget {

	@PropertyIdentifier(type = File.class)
	public static final String COMPONENT_FILE_KEY = "componentFile";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DYNAMIC_COMPONENT_FILE_KEY = "dynamicComponentFile";
	@PropertyIdentifier(type = Vector.class)
	public static final String ASSIGNMENTS_KEY = "assignments";

	@Getter(value = COMPONENT_FILE_KEY)
	@XMLAttribute
	public File getComponentFile();

	@Setter(COMPONENT_FILE_KEY)
	public void setComponentFile(File componentFile);

	@Getter(value = DYNAMIC_COMPONENT_FILE_KEY)
	@XMLAttribute
	public DataBinding<File> getDynamicComponentFile();

	@Setter(DYNAMIC_COMPONENT_FILE_KEY)
	public void setDynamicComponentFile(DataBinding<File> dynamicComponentFile);

	@Getter(value = ASSIGNMENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBReferenceAssignment.OWNER_KEY)
	public List<FIBReferenceAssignment> getAssignments();

	@Setter(ASSIGNMENTS_KEY)
	public void setAssignments(List<FIBReferenceAssignment> assignments);

	@Adder(ASSIGNMENTS_KEY)
	public void addToAssignments(FIBReferenceAssignment aAssignment);

	@Remover(ASSIGNMENTS_KEY)
	public void removeFromAssignments(FIBReferenceAssignment aAssignment);

	public static abstract class FIBReferencedComponentImpl extends FIBWidgetImpl implements FIBReferencedComponent {

		private static final Logger logger = Logger.getLogger(FIBReferencedComponent.class.getPackage().getName());

		private File componentFile;
		private DataBinding<File> dynamicComponentFile;

		// TODO: Should be moved to FIBReferencedComponent widget
		// private FIBComponent referencedComponent;
		private Vector<FIBReferenceAssignment> assignments;

		public FIBReferencedComponentImpl() {
			assignments = new Vector<FIBReferenceAssignment>();
		}

		@Override
		public String getBaseName() {
			return "ReferencedComponent";
		}

		@Override
		public Type getDefaultDataClass() {
			/*if (referencedComponent != null) {
				return referencedComponent.getDataType();
			}*/
			return Object.class;
		}

		@Override
		public File getComponentFile() {
			return componentFile;
		}

		@Override
		public void setComponentFile(File componentFile) {
			FIBPropertyNotification<File> notification = requireChange(COMPONENT_FILE_KEY, componentFile);
			if (notification != null) {
				this.componentFile = componentFile;
				// component = null;
				notify(notification);
			}
		}

		@Override
		public DataBinding<File> getDynamicComponentFile() {

			if (dynamicComponentFile == null) {
				dynamicComponentFile = new DataBinding<File>(this, File.class, DataBinding.BindingDefinitionType.GET);
				dynamicComponentFile.setBindingName("componentFile");
				dynamicComponentFile.setCacheable(true);
			}
			return dynamicComponentFile;
		}

		@Override
		public void setDynamicComponentFile(DataBinding<File> dynamicComponentFile) {

			FIBPropertyNotification<DataBinding<File>> notification = requireChange(DYNAMIC_COMPONENT_FILE_KEY, dynamicComponentFile);

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

		@Override
		public Vector<FIBReferenceAssignment> getAssignments() {
			return assignments;
		}

		public void setAssignments(Vector<FIBReferenceAssignment> assignments) {
			this.assignments = assignments;
		}

		@Override
		public void addToAssignments(FIBReferenceAssignment a) {
			if (getAssignment(a.getVariable().toString()) != null) {
				removeFromAssignments(getAssignment(a.getVariable().toString()));
			}
			a.setOwner(this);
			assignments.add(a);
			getPropertyChangeSupport().firePropertyChange(ASSIGNMENTS_KEY, null, assignments);
		}

		@Override
		public void removeFromAssignments(FIBReferenceAssignment a) {
			a.setOwner(null);
			assignments.remove(a);
			getPropertyChangeSupport().firePropertyChange(ASSIGNMENTS_KEY, null, assignments);
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();
			for (FIBReferenceAssignment assign : getAssignments()) {
				assign.finalizeDeserialization();
			}
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = super.getDeclaredBindings();
			returned.add(getDynamicComponentFile());
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
			FIBReferenceAssignment newAssignment = getFactory().newInstance(FIBReferenceAssignment.class);
			addToAssignments(newAssignment);
			return newAssignment;
		}

		public FIBReferenceAssignment deleteAssignment(FIBReferenceAssignment assignment) {
			logger.info("Called deleteAssignment() with " + assignment);
			removeFromAssignments(assignment);
			return assignment;
		}

	}

	@ModelEntity
	@ImplementationClass(FIBReferenceAssignment.FIBReferenceAssignmentImpl.class)
	@XMLElement(xmlTag = "ReferenceAssignment")
	public static interface FIBReferenceAssignment extends FIBModelObject {
		@PropertyIdentifier(type = FIBReferencedComponent.class)
		public static final String OWNER_KEY = "owner";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VARIABLE_KEY = "variable";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VALUE_KEY = "value";
		@PropertyIdentifier(type = Boolean.class)
		public static final String MANDATORY_KEY = "mandatory";

		@Getter(value = OWNER_KEY, inverse = FIBReferencedComponent.ASSIGNMENTS_KEY)
		public FIBReferencedComponent getOwner();

		@Setter(OWNER_KEY)
		public void setOwner(FIBReferencedComponent customColumn);

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

		@Getter(value = MANDATORY_KEY)
		@XMLAttribute
		public boolean isMandatory();

		@Setter(MANDATORY_KEY)
		public void setMandatory(boolean mandatory);

		public void finalizeDeserialization();

		public static abstract class FIBReferenceAssignmentImpl extends FIBModelObjectImpl implements FIBReferenceAssignment {

			@Deprecated
			public static BindingDefinition VARIABLE = new BindingDefinition("variable", Object.class,
					DataBinding.BindingDefinitionType.GET_SET, true);
			@Deprecated
			public BindingDefinition VALUE = new BindingDefinition("value", Object.class, DataBinding.BindingDefinitionType.GET, true);

			private DataBinding<Object> variable;
			private DataBinding<Object> value;

			private final boolean mandatory = true;

			@Override
			public boolean isMandatory() {
				return mandatory;
			}

			@Override
			public void setOwner(FIBReferencedComponent referencedComponent) {
				performSuperSetter(OWNER_KEY, referencedComponent);
				if (value != null) {
					value.setOwner(referencedComponent);
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
					return getOwner().getBindingModel();
				}
				return null;
			}

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
