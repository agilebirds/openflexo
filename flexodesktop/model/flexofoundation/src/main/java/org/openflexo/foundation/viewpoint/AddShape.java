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

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class AddShape extends AddShemaElementAction<ShapePatternRole> {

	private static final Logger logger = Logger.getLogger(AddShape.class.getPackage().getName());

	public AddShape() {
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddShape;
	}

	@Override
	public List<ShapePatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(ShapePatternRole.class);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_SHAPE_INSPECTOR;
	}

	public ViewObject getContainer(EditionSchemeAction action) {
		if (getPatternRole().getParentShapeAsDefinedInAction()) {
			return (ViewObject) getContainer().getBindingValue(action);
		} else {
			FlexoModelObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getParentShapePatternRole());
			return (ViewObject) action.getEditionPatternInstance().getPatternActor(getPatternRole().getParentShapePatternRole());
		}
	}

	@Override
	public ShapePatternRole getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}

	// FIXME: if we remove this useless code, some FIB won't work (see EditionPatternView.fib, inspect an AddIndividual)
	// Need to be fixed in KeyValueProperty.java
	@Override
	public void setPatternRole(ShapePatternRole patternRole) {
		super.setPatternRole(patternRole);
	}

	private ViewPointDataBinding container;

	private BindingDefinition CONTAINER = new BindingDefinition("container", ViewObject.class, BindingDefinitionType.GET, false);

	public BindingDefinition getContainerBindingDefinition() {
		return CONTAINER;
	}

	public ViewPointDataBinding getContainer() {
		if (container == null) {
			container = new ViewPointDataBinding(this, EditionActionBindingAttribute.container, getContainerBindingDefinition());
		}
		return container;
	}

	public void setContainer(ViewPointDataBinding container) {
		container.setOwner(this);
		container.setBindingAttribute(EditionActionBindingAttribute.container);
		container.setBindingDefinition(getContainerBindingDefinition());
		this.container = container;
	}

}
