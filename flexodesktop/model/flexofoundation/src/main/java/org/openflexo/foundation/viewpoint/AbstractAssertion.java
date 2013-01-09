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

import java.util.Collection;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionAction.EditionActionBindingAttribute;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public abstract class AbstractAssertion extends EditionSchemeObject {

	private AddIndividual _action;

	public AbstractAssertion(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public String getURI() {
		return null;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	public void setAction(AddIndividual action) {
		_action = action;
	}

	public AddIndividual getAction() {
		return _action;
	}

	public EditionScheme getScheme() {
		return getAction().getScheme();
	}

	@Override
	public EditionScheme getEditionScheme() {
		return getScheme();
	}

	@Override
	public ViewPoint getViewPoint() {
		if (getAction() != null) {
			return getAction().getViewPoint();
		}
		return null;
	}

	public boolean evaluateCondition(EditionSchemeAction action) {
		if (getConditional().isValid()) {
			return (Boolean) getConditional().getBindingValue(action);
		}
		return true;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return getScheme() != null ? getScheme().getEditionPattern() : null;
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionPattern().getBindingModel();
	}

	private ViewPointDataBinding conditional;

	private static final BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET,
			false);

	public BindingDefinition getConditionalBindingDefinition() {
		return CONDITIONAL;
	}

	public ViewPointDataBinding getConditional() {
		if (conditional == null) {
			conditional = new ViewPointDataBinding(this, EditionActionBindingAttribute.conditional, getConditionalBindingDefinition());
		}
		return conditional;
	}

	public void setConditional(ViewPointDataBinding conditional) {
		conditional.setOwner(this);
		conditional.setBindingAttribute(EditionActionBindingAttribute.conditional);
		conditional.setBindingDefinition(getConditionalBindingDefinition());
		this.conditional = conditional;
	}

}
