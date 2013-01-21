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

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public abstract class AbstractAssertion extends EditionSchemeObject {

	private AddIndividual _action;
	private DataBinding<Boolean> conditional;

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
			try {
				return getConditional().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			}
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

	public DataBinding<Boolean> getConditional() {
		if (conditional == null) {
			conditional = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			conditional.setBindingName("conditional");
		}
		return conditional;
	}

	public void setConditional(DataBinding<Boolean> conditional) {
		if (conditional != null) {
			conditional.setOwner(this);
			conditional.setDeclaredType(Boolean.class);
			conditional.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			conditional.setBindingName("conditional");
		}
		this.conditional = conditional;
	}

}
