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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class IterationAction<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends ControlStructureAction<M, MM> {

	private static final Logger logger = Logger.getLogger(IterationAction.class.getPackage().getName());

	private String iteratorName = "item";

	public IterationAction(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.Iteration;
	}

	/*@Override
	public List<PatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles();
	}*/

	private ViewPointDataBinding iteration;

	private BindingDefinition ITERATION = new BindingDefinition("iteration", List.class, BindingDefinitionType.GET, true);

	public BindingDefinition getIterationBindingDefinition() {
		return ITERATION;
	}

	public ViewPointDataBinding getIteration() {
		if (iteration == null) {
			iteration = new ViewPointDataBinding(this, EditionActionBindingAttribute.object, getIterationBindingDefinition());
		}
		return iteration;
	}

	public void setIteration(ViewPointDataBinding iteration) {
		if (iteration != null) {
			iteration.setOwner(this);
			iteration.setBindingAttribute(EditionActionBindingAttribute.iteration);
			iteration.setBindingDefinition(getIterationBindingDefinition());
		}
		this.iteration = iteration;
		rebuildInferedBindingModel();
	}

	@Override
	public void notifyBindingChanged(ViewPointDataBinding binding) {
		super.notifyBindingChanged(binding);
		if (binding == iteration) {
			rebuildInferedBindingModel();
		}
	}

	public String getIteratorName() {
		return iteratorName;
	}

	public void setIteratorName(String iteratorName) {
		this.iteratorName = iteratorName;
		rebuildInferedBindingModel();
	}

	public Type getItemType() {
		if (getIteration() != null && getIteration().hasBinding()) {
			Type accessedType = getIteration().getBinding().getAccessedType();
			if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
				return ((ParameterizedType) accessedType).getActualTypeArguments()[0];
			}
		}
		return Object.class;
	}

	@Override
	protected BindingModel buildInferedBindingModel() {
		BindingModel returned = super.buildInferedBindingModel();
		returned.addToBindingVariables(new BindingVariableImpl(this, getIteratorName(), getItemType()) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for " + getIteratorName() + " ? target " + target + " context=" + context);
				return super.getBindingValue(target, context);
			}

			@Override
			public Type getType() {
				return getItemType();
			}
		});
		return returned;
	}

	public List<?> evaluateIteration(EditionSchemeAction action) {
		if (getIteration().isValid()) {
			return (List<?>) getIteration().getBindingValue(action);
		}
		return null;
	}

	@Override
	public Object performAction(EditionSchemeAction action) {
		List<?> items = evaluateIteration(action);
		if (items != null) {
			for (Object item : items) {
				action.declareVariable(getIteratorName(), item);
				performBatchOfActions(getActions(), action);
			}
		}
		action.dereferenceVariable(getIteratorName());
		return null;
	}

	@Override
	public String getStringRepresentation() {
		if (getIteration().isSet() && getIteration().isValid()) {
			return getIteratorName() + " : " + getIteration();
		}
		return super.getStringRepresentation();
	}

	public static class IterationBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<IterationAction> {
		public IterationBindingIsRequiredAndMustBeValid() {
			super("'iteration'_binding_is_not_valid", IterationAction.class);
		}

		@Override
		public ViewPointDataBinding getBinding(IterationAction object) {
			return object.getIteration();
		}

		@Override
		public BindingDefinition getBindingDefinition(IterationAction object) {
			return object.getIterationBindingDefinition();
		}

	}

}
