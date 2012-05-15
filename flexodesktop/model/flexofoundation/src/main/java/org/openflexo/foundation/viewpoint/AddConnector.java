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
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.action.LinkSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class AddConnector extends AddShemaElementAction {

	private static final Logger logger = Logger.getLogger(LinkSchemeAction.class.getPackage().getName());

	public AddConnector() {
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddConnector;
	}

	/*@Override
	public List<ConnectorPatternRole> getAvailablePatternRoles() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getPatternRoles(ConnectorPatternRole.class);
		}
		return null;
	}*/

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_CONNECTOR_INSPECTOR;
	}

	@Override
	public ConnectorPatternRole getPatternRole() {
		PatternRole superPatternRole = super.getPatternRole();
		if (superPatternRole instanceof ConnectorPatternRole) {
			return (ConnectorPatternRole) superPatternRole;
		} else if (superPatternRole != null) {
			// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
			return null;
		}
		return null;
	}

	public ViewShape getFromShape(EditionSchemeAction action) {
		if (getPatternRole() != null && !getPatternRole().getStartShapeAsDefinedInAction()) {
			FlexoModelObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getStartShapePatternRole());
			return (ViewShape) action.getEditionPatternInstance().getPatternActor(getPatternRole().getStartShapePatternRole());
		} else {
			return (ViewShape) getFromShape().getBindingValue(action);
		}
	}

	public ViewShape getToShape(EditionSchemeAction action) {
		if (getPatternRole() != null && !getPatternRole().getEndShapeAsDefinedInAction()) {
			FlexoModelObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getEndShapePatternRole());
			return (ViewShape) action.getEditionPatternInstance().getPatternActor(getPatternRole().getEndShapePatternRole());
		} else {
			return (ViewShape) getToShape().getBindingValue(action);
		}
	}

	@Override
	public String toString() {
		return "AddConnector " + Integer.toHexString(hashCode()) + " patternRole=" + getPatternRole();
	}

	/*@Override
	public ConnectorPatternRole getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}*/

	// FIXME: if we remove this useless code, some FIB won't work (see EditionPatternView.fib, inspect an AddIndividual)
	// Need to be fixed in KeyValueProperty.java
	/*@Override
	public void setPatternRole(ConnectorPatternRole patternRole) {
		super.setPatternRole(patternRole);
	}*/

	private ViewPointDataBinding fromShape;
	private ViewPointDataBinding toShape;

	private BindingDefinition FROM_SHAPE = new BindingDefinition("fromShape", ViewShape.class, BindingDefinitionType.GET, false);

	public BindingDefinition getFromShapeBindingDefinition() {
		return FROM_SHAPE;
	}

	public ViewPointDataBinding getFromShape() {
		if (fromShape == null) {
			fromShape = new ViewPointDataBinding(this, EditionActionBindingAttribute.fromShape, getFromShapeBindingDefinition());
		}
		return fromShape;
	}

	public void setFromShape(ViewPointDataBinding fromShape) {
		fromShape.setOwner(this);
		fromShape.setBindingAttribute(EditionActionBindingAttribute.fromShape);
		fromShape.setBindingDefinition(getFromShapeBindingDefinition());
		this.fromShape = fromShape;
	}

	private BindingDefinition TO_SHAPE = new BindingDefinition("toShape", ViewShape.class, BindingDefinitionType.GET, false);

	public BindingDefinition getToShapeBindingDefinition() {
		return TO_SHAPE;
	}

	public ViewPointDataBinding getToShape() {
		if (toShape == null) {
			toShape = new ViewPointDataBinding(this, EditionActionBindingAttribute.toShape, getToShapeBindingDefinition());
		}
		return toShape;
	}

	public void setToShape(ViewPointDataBinding toShape) {
		toShape.setOwner(this);
		toShape.setBindingAttribute(EditionActionBindingAttribute.toShape);
		toShape.setBindingDefinition(getToShapeBindingDefinition());
		this.toShape = toShape;
	}

	@Override
	public Type getAssignableType() {
		return ViewConnector.class;
	}

}
