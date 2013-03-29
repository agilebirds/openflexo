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
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.logging.FlexoLogger;

/**
 * An EditionPatternConstraint represents a structural constraint attached to an EditionPattern
 * 
 * @author sylvain
 * 
 */
public class FetchRequestCondition extends EditionPatternObject {

	protected static final Logger logger = FlexoLogger.getLogger(FetchRequestCondition.class.getPackage().getName());

	private FetchRequest fetchRequest;
	private DataBinding<Boolean> condition;

	public FetchRequestCondition(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Collection<ViewPointObject> getEmbeddedValidableObjects() {
		return null;
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
			returned = new BindingModel(getFetchRequest().getActionContainer() != null ? getFetchRequest().getActionContainer()
					.getBindingModel() : getFetchRequest().getBindingModel());
		} else {
			returned = new BindingModel();
		}
		returned.addToBindingVariables(new BindingVariable("selected", getFetchRequest().getFetchedType()) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for " + "selected" + " ? target " + target + " context=" + context);
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

	public DataBinding<Boolean> getCondition() {
		if (condition == null) {
			condition = new DataBinding<Boolean>(this, Boolean.class, BindingDefinitionType.GET);
			condition.setBindingName("condition");
		}
		return condition;
	}

	public void setCondition(DataBinding<Boolean> condition) {
		if (condition != null) {
			condition.setOwner(this);
			condition.setBindingName("condition");
			condition.setDeclaredType(Boolean.class);
			condition.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.condition = condition;
	}

}
