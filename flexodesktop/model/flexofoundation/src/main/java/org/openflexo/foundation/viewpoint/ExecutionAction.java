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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;

@FIBPanel("Fib/ExecutionActionPanel.fib")
@ModelEntity
@ImplementationClass(ExecutionAction.ExecutionActionImpl.class)
@XMLElement
public interface ExecutionAction<MS extends ModelSlot<?>> extends AssignableAction<MS, FlexoObject>{

@PropertyIdentifier(type=DataBinding.class)
public static final String EXECUTION_KEY = "execution";

@Getter(value=EXECUTION_KEY)
@XMLAttribute
public DataBinding getExecution();

@Setter(EXECUTION_KEY)
public void setExecution(DataBinding execution);


public static abstract  class ExecutionAction<MSImpl extends ModelSlot<?>> extends AssignableAction<MS, FlexoObject>Impl implements ExecutionAction<MS
{

	private static final Logger logger = Logger.getLogger(ExecutionAction.class.getPackage().getName());

	private DataBinding<Object> execution;

	public ExecutionActionImpl() {
		super();
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append((getAssignation().isSet() ? getAssignation().toString() + " = " : "") + getExecution().toString() + ";", context);
		return out.toString();
	}

	public DataBinding<Object> getExecution() {
		if (execution == null) {
			execution = new DataBinding<Object>(this, Object.class, BindingDefinitionType.EXECUTE);
			execution.setBindingName("execution");
		}
		return execution;
	}

	public void setExecution(DataBinding<Object> execution) {
		if (execution != null) {
			execution.setOwner(this);
			execution.setBindingName("execution");
			execution.setDeclaredType(Object.class);
			execution.setBindingDefinitionType(BindingDefinitionType.EXECUTE);
		}
		this.execution = execution;
	}

	@Override
	public Type getAssignableType() {
		if (getExecution().isSet() && getExecution().isValid()) {
			return getExecution().getAnalyzedType();
		}
		return Object.class;
	}

	@Override
	public FlexoObject performAction(EditionSchemeAction action) {
		try {
			getExecution().execute(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		if (dataBinding == getExecution()) {
		}
		super.notifiedBindingChanged(dataBinding);
	}

	public static class ExecutionBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<ExecutionAction> {
		public ExecutionBindingIsRequiredAndMustBeValid() {
			super("'execution'_binding_is_not_valid", ExecutionAction.class);
		}

		@Override
		public DataBinding<Object> getBinding(ExecutionAction object) {
			return object.getExecution();
		}

	}

}
}
