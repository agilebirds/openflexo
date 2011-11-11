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
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class AddShema extends EditionAction<ShemaPatternRole> {

	private static final Logger logger = Logger.getLogger(AddShema.class.getPackage().getName());

	private ShapePatternRole shapePatternRole;

	public AddShema() {
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddShema;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_SHEMA_INSPECTOR;
	}

	public String getShemaName(EditionSchemeAction action) {
		return (String) getShemaName().getBindingValue(action);
	}

	@Override
	public ShemaPatternRole getPatternRole() {
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
	public void setPatternRole(ShemaPatternRole patternRole) {
		super.setPatternRole(patternRole);
	}

	/*@Override
	protected void updatePatternRoleType()
	{
		if (getPatternRole() == null) {
			return;
		}
	}*/

	public ShapePatternRole getShapePatternRole() {
		return shapePatternRole;
	}

	public void setShapePatternRole(ShapePatternRole shapePatternRole) {
		this.shapePatternRole = shapePatternRole;
	}

	private ViewPointDataBinding shemaName;

	private BindingDefinition SHEMA_NAME = new BindingDefinition("shemaName", String.class, BindingDefinitionType.GET, false);

	public BindingDefinition getShemaNameBindingDefinition() {
		return SHEMA_NAME;
	}

	public ViewPointDataBinding getShemaName() {
		if (shemaName == null)
			shemaName = new ViewPointDataBinding(this, EditionActionBindingAttribute.shemaName, getShemaNameBindingDefinition());
		return shemaName;
	}

	public void setShemaName(ViewPointDataBinding shemaName) {
		shemaName.setOwner(this);
		shemaName.setBindingAttribute(EditionActionBindingAttribute.shemaName);
		shemaName.setBindingDefinition(getShemaNameBindingDefinition());
		this.shemaName = shemaName;
	}

}
