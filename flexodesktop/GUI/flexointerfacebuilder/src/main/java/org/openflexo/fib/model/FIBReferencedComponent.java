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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.FIBLibrary;

public class FIBReferencedComponent extends FIBWidget implements BindingEvaluationContext {

	private static final Logger logger = Logger.getLogger(FIBReferencedComponent.class.getPackage().getName());

	// This is the Fib File used for the component
	private DataBinding<File> componentFile;


	public static enum Parameters implements FIBModelAttribute {
		componentFile, assignments
	}

	// private File componentFile;
	private FIBComponent component;
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
		if (getComponent() != null) {
			return getComponent().getDataType();
		}
		return Object.class;
	}

	public DataBinding<File>  getComponentFile() {

		if (componentFile == null) {
			componentFile = new DataBinding<File>(this, File.class, DataBinding.BindingDefinitionType.GET);
			componentFile.setBindingName("componentFile");
		}
		return componentFile;
	}

	public void setComponentFile(DataBinding<File>  componentFile) {

		FIBAttributeNotification<DataBinding<File>> notification = requireChange(Parameters.componentFile, componentFile);

		if (notification != null) {

			if (componentFile != null) {
				componentFile.setOwner(this);
				componentFile.setDeclaredType(File.class);
				componentFile.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				componentFile.setBindingName("componentFile");
			}

			this.componentFile = componentFile;

			component = null;
			notify(notification);
		}


	}

	@Override
	public FIBComponent getComponent() {
		if (component == null && getComponentFile() != null) {

			try {
				// TODO : find the right evaluation context
				File fibFile = getComponentFile().getBindingValue((BindingEvaluationContext) this);
				
				component = FIBLibrary.instance().retrieveFIBComponent(fibFile);

			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return component;
	}

	public boolean hasAssignment(String variableName) {
		return getAssignent(variableName) != null;
	}

	public FIBReferenceAssignment getAssignent(String variableName) {
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
		if (getAssignent(a.getVariable().toString()) != null) {
			removeFromAssignments(getAssignent(a.getVariable().toString()));
		}
		a.setReferencedComponent(this);
		assignments.add(a);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBReferenceAssignment>(Parameters.assignments, a));
	}

	public void removeFromAssignments(FIBReferenceAssignment a) {
		a.setReferencedComponent(null);
		assignments.remove(a);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBReferenceAssignment>(Parameters.assignments, a));
	}

	@Override
	public void finalizeDeserialization() {
		super.finalizeDeserialization();
		for (FIBReferenceAssignment assign : getAssignments()) {
			assign.finalizeDeserialization();
		}
	}

	@Override
	public Boolean getManageDynamicModel() {
		return true;
	}

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

	@Override
	public Object getValue(BindingVariable variable) {
		// TODO Auto-generated method stub
		logger.info("This has to be done :"+variable.getLabel());
		return null;
	}

}
