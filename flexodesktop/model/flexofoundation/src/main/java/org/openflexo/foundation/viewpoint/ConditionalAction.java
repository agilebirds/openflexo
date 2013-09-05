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
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.toolbox.StringUtils;

public class ConditionalAction extends ControlStructureAction {

	private static final Logger logger = Logger.getLogger(ConditionalAction.class.getPackage().getName());

	private DataBinding<Boolean> condition;

	public ConditionalAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("if " + getCondition().toString() + "", context);
		out.append(" {", context);
		out.append(StringUtils.LINE_SEPARATOR, context);
		for (EditionAction action : getActions()) {
			out.append(action.getFMLRepresentation(context), context, 1);
			out.append(StringUtils.LINE_SEPARATOR, context);
		}

		out.append("}", context);
		return out.toString();
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.Conditional;
	}

	public DataBinding<Boolean> getCondition() {
		if (condition == null) {
			condition = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			condition.setBindingName("condition");
		}
		return condition;
	}

	public void setCondition(DataBinding<Boolean> condition) {
		if (condition != null) {
			condition.setOwner(this);
			condition.setDeclaredType(Boolean.class);
			condition.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			condition.setBindingName("condition");
		}
		this.condition = condition;
	}

	@Override
	public boolean evaluateCondition(EditionSchemeAction action) {
		if (getCondition().isSet() && getCondition().isValid()) {
			try {
				DataBinding condition = getCondition();
				Boolean returned = getCondition().getBindingValue(action);
				if (returned == null) {
					/*System.out.println("Evaluation of " + getCondition() + " returns null");
					DataBinding db1 = new DataBinding<Object>("city1.name", getCondition().getOwner(), Object.class,
							BindingDefinitionType.GET);
					DataBinding db2 = new DataBinding<Object>("city2.mayor.name", getCondition().getOwner(), Object.class,
							BindingDefinitionType.GET);
					System.out.println("city1.name=" + db1.getBindingValue(action));
					System.out.println("city2.mayor.name=" + db2.getBindingValue(action));*/
					return false;
				}
				return returned;
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public String getStringRepresentation() {
		if (getCondition().isSet() && getCondition().isValid()) {
			return getCondition() + " ?";
		}
		return super.getStringRepresentation();
	}

	@Override
	public Object performAction(EditionSchemeAction action) {
		if (evaluateCondition(action)) {
			performBatchOfActions(getActions(), action);
		}
		return null;
	}

	public static class ConditionBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<ConditionalAction> {
		public ConditionBindingIsRequiredAndMustBeValid() {
			super("'condition'_binding_is_not_valid", ConditionalAction.class);
		}

		@Override
		public DataBinding<Boolean> getBinding(ConditionalAction object) {
			return object.getCondition();
		}

	}

}
