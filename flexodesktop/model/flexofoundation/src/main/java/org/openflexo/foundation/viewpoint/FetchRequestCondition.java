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

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * An EditionPatternConstraint represents a structural constraint attached to an EditionPattern
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FetchRequestCondition.FetchRequestConditionImpl.class)
@XMLElement(xmlTag = "Condition")
public interface FetchRequestCondition extends EditionPatternObject {

	@PropertyIdentifier(type = FetchRequest.class)
	public static final String ACTION_KEY = "action";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String CONDITION_KEY = "condition";

	@Getter(value = ACTION_KEY, inverse = FetchRequest.CONDITIONS_KEY)
	public FetchRequest<?, ?> getAction();

	@Setter(ACTION_KEY)
	public void setAction(FetchRequest<?, ?> action);

	@Getter(value = CONDITION_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getCondition();

	@Setter(CONDITION_KEY)
	public void setCondition(DataBinding<Boolean> condition);

	public boolean evaluateCondition(final Object proposedFetchResult, final EditionSchemeAction action);

	public static abstract class FetchRequestConditionImpl extends EditionPatternObjectImpl implements FetchRequestCondition {

		protected static final Logger logger = FlexoLogger.getLogger(FetchRequestCondition.class.getPackage().getName());

		public static final String SELECTED = "selected";

		private FetchRequest fetchRequest;
		private DataBinding<Boolean> condition;

		public FetchRequestConditionImpl() {
			super();
		}

		@Override
		public EditionPattern getEditionPattern() {
			if (getFetchRequest() != null) {
				return getFetchRequest().getEditionPattern();
			}
			return null;
		}

		private BindingModel bindingModel;

		@Override
		public BindingModel getBindingModel() {
			if (bindingModel == null) {
				bindingModel = buildBindingModel();
			}
			return bindingModel;
		}

		protected BindingModel buildBindingModel() {
			BindingModel returned;
			if (getFetchRequest() != null) {
				/*returned = new BindingModel(getFetchRequest().getActionContainer() != null ? getFetchRequest().getActionContainer()
						.getBindingModel() : getFetchRequest().getBindingModel());*/
				if (getFetchRequest().getEmbeddingIteration() != null) {
					returned = new BindingModel(getFetchRequest().getEmbeddingIteration().getBindingModel());
				} else {
					returned = new BindingModel(getFetchRequest().getBindingModel());
				}
			} else {
				returned = new BindingModel();
			}
			returned.addToBindingVariables(new BindingVariable(SELECTED, getFetchRequest().getFetchedType()) {
				@Override
				public Object getBindingValue(Object target, BindingEvaluationContext context) {
					logger.info("What should i return for " + SELECTED + " ? target " + target + " context=" + context);
					return super.getBindingValue(target, context);
				}

				@Override
				public Type getType() {
					return getFetchRequest().getFetchedType();
				}
			});
			return returned;
		}

		public FetchRequest getFetchRequest() {
			return fetchRequest;
		}

		public void setFetchRequest(FetchRequest fetchRequest) {
			this.fetchRequest = fetchRequest;
			bindingModel = null;
		}

		@Override
		public String getURI() {
			return getFetchRequest().getURI() + "/Constraints_" + Integer.toHexString(hashCode());
		}

		@Override
		public DataBinding<Boolean> getCondition() {
			if (condition == null) {
				condition = new DataBinding<Boolean>(this, Boolean.class, BindingDefinitionType.GET);
				condition.setBindingName("condition");
			}
			return condition;
		}

		@Override
		public void setCondition(DataBinding<Boolean> condition) {
			if (condition != null) {
				condition.setOwner(this);
				condition.setBindingName("condition");
				condition.setDeclaredType(Boolean.class);
				condition.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.condition = condition;
		}

		@Override
		public boolean evaluateCondition(final Object proposedFetchResult, final EditionSchemeAction action) {
			Boolean returned = null;
			try {
				returned = condition.getBindingValue(new BindingEvaluationContext() {
					@Override
					public Object getValue(BindingVariable variable) {
						if (variable.getVariableName().equals(SELECTED)) {
							return proposedFetchResult;
						}
						return action.getValue(variable);
					}
				});
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (returned == null) {
				return false;
			}
			return returned;
		}
	}
}
