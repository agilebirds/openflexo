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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.validation.Validable;

/**
 * Abstract class representing a fetch request, which is a primitive allowing to browse in the model while configuring requests
 * 
 * @author sylvain
 * 
 */
public abstract class FetchRequest<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, T> extends AssignableAction<M, MM, List<T>> {

	private static final Logger logger = Logger.getLogger(FetchRequest.class.getPackage().getName());

	private Vector<DataBinding<Boolean>> conditions;

	private ConditionOwner conditionOwner;

	public FetchRequest(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		conditions = new Vector<DataBinding<Boolean>>();
		conditionOwner = new ConditionOwner(this);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.FetchRequest;
	}

	public ConditionOwner getConditionOwner() {
		return conditionOwner;
	}

	public abstract Type getFetchedType();

	@Override
	public Type getAssignableType() {
		return new ParameterizedTypeImpl(List.class, getFetchedType());
	}

	public Vector<DataBinding<Boolean>> getConditions() {
		return conditions;
	}

	public void setConditions(Vector<DataBinding<Boolean>> conditions) {
		this.conditions = conditions;
	}

	public void addToConditions(DataBinding<Boolean> condition) {
		conditions.add(new DataBinding<Boolean>(condition.toString(), conditionOwner, Boolean.class, BindingDefinitionType.GET));
	}

	public void removeFromConditions(DataBinding<Boolean> condition) {
		conditions.remove(condition);
	}

	public DataBinding<Boolean> createCondition() {
		DataBinding<Boolean> newCondition = new DataBinding<Boolean>(conditionOwner, Boolean.class, BindingDefinitionType.GET);
		addToConditions(newCondition);
		return newCondition;
	}

	public void deleteCondition(DataBinding<Boolean> aCondition) {
		removeFromConditions(aCondition);
	}

	@Override
	protected BindingModel buildInferedBindingModel() {
		BindingModel returned = super.buildInferedBindingModel();
		returned.addToBindingVariables(new BindingVariable("selected", getFetchedType()) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for " + "selected" + " ? target " + target + " context=" + context);
				return super.getBindingValue(target, context);
			}

			@Override
			public Type getType() {
				return getFetchedType();
			}
		});
		return returned;
	}

	public static class ConditionOwner extends EditionSchemeObject implements Bindable {

		private FetchRequest<?, ?, ?> fetchRequest;

		public ConditionOwner(FetchRequest<?, ?, ?> fetchRequest) {
			super(null);
			this.fetchRequest = fetchRequest;
		}

		@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			return null;
		}

		@Override
		public BindingModel getBindingModel() {
			return fetchRequest.getInferedBindingModel();
		}

		@Override
		public EditionScheme getEditionScheme() {
			return fetchRequest.getEditionScheme();
		}

		@Override
		public EditionPattern getEditionPattern() {
			return fetchRequest.getEditionPattern();
		}

		@Override
		public String getURI() {
			return null;
		}

		@Override
		public BindingFactory getBindingFactory() {
			return fetchRequest.getBindingFactory();
		}

	}

}
