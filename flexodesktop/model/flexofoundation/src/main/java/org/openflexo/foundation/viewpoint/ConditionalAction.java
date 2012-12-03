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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class ConditionalAction<MS extends ModelSlot<M, MM>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends
		ControlStructureAction<MS, M, MM> {

	private static final Logger logger = Logger.getLogger(ConditionalAction.class.getPackage().getName());

	public ConditionalAction(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.Conditional;
	}

	/*@Override
	public List<PatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles();
	}*/

	public boolean evaluateConditional(EditionSchemeAction action) {
		return (Boolean) getCondition().getBindingValue(action);
	}

	private ViewPointDataBinding condition;

	private BindingDefinition CONDITION = new BindingDefinition("condition", Boolean.class, BindingDefinitionType.GET, true);

	public BindingDefinition getConditionBindingDefinition() {
		return CONDITION;
	}

	public ViewPointDataBinding getCondition() {
		if (condition == null) {
			condition = new ViewPointDataBinding(this, EditionActionBindingAttribute.object, getConditionalBindingDefinition());
		}
		return condition;
	}

	public void setCondition(ViewPointDataBinding condition) {
		if (condition != null) {
			condition.setOwner(this);
			condition.setBindingAttribute(EditionActionBindingAttribute.condition);
			condition.setBindingDefinition(getConditionalBindingDefinition());
		}
		this.condition = condition;
	}

	@Override
	public boolean evaluateCondition(EditionSchemeAction action) {
		if (getCondition().isValid()) {
			return (Boolean) getCondition().getBindingValue(action);
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
		public ViewPointDataBinding getBinding(ConditionalAction object) {
			return object.getCondition();
		}

		@Override
		public BindingDefinition getBindingDefinition(ConditionalAction object) {
			return object.getConditionBindingDefinition();
		}

	}

}
