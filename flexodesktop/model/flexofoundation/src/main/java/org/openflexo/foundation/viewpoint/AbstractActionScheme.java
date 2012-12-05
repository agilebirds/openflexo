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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public abstract class AbstractActionScheme extends EditionScheme {

	public static enum ActionSchemeBindingAttribute implements InspectorBindingAttribute {
		conditional
	}

	private ViewPointDataBinding conditional;

	public AbstractActionScheme(ViewPointBuilder builder) {
		super(builder);
	}

	private BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);

	public BindingDefinition getConditionalBindingDefinition() {
		return CONDITIONAL;
	}

	public ViewPointDataBinding getConditional() {
		if (conditional == null) {
			conditional = new ViewPointDataBinding(this, ActionSchemeBindingAttribute.conditional, getConditionalBindingDefinition());
		}
		return conditional;
	}

	public void setConditional(ViewPointDataBinding conditional) {
		conditional.setOwner(this);
		conditional.setBindingAttribute(ActionSchemeBindingAttribute.conditional);
		conditional.setBindingDefinition(getConditionalBindingDefinition());
		this.conditional = conditional;
	}

	public boolean evaluateCondition(EditionPatternReference editionPatternReference) {
		if (getConditional().isValid()) {
			return (Boolean) getConditional().getBindingValue(editionPatternReference);
		}
		return true;
	}

	@Override
	protected void appendContextualBindingVariables(BindingModel bindingModel) {
		super.appendContextualBindingVariables(bindingModel);
		bindingModel.addToBindingVariables(new EditionPatternPathElement<AbstractActionScheme>(EditionScheme.THIS, getEditionPattern(),
				this));
	}

	@Override
	public void setEditionPattern(EditionPattern editionPattern) {
		super.setEditionPattern(editionPattern);
		updateBindingModels();
	}

}
