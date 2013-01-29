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

import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;

public class ConditionalAction<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends ControlStructureAction<M, MM> {

	private static final Logger logger = Logger.getLogger(ConditionalAction.class.getPackage().getName());

	private DataBinding<Boolean> condition;

	public ConditionalAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
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
		if (getCondition().isValid()) {
			try {
				return getCondition().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
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
