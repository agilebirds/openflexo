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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.PatternRole.PatternRoleType;


public class AddShema extends EditionAction<ShemaPatternRole> {

	private static final Logger logger = Logger.getLogger(AddShema.class.getPackage().getName());
	
	private String shemaName;
	private String shapePatternRole;
	
	public AddShema() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddShema;
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.ADD_SHEMA_INSPECTOR;
	}

	public String _getShemaName()
	{
		return shemaName;
	}
	
	public void _setShemaName(String aShemaName)
	{
		shemaName = aShemaName;
	}
	
	private Vector<String> availableShemaNameValues = null;
	
	public Vector<String> getAvailableShemaNameValues()
	{
		if (availableShemaNameValues == null) {
			availableShemaNameValues = new Vector<String>();
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				availableShemaNameValues.add(pr.getPatternRoleName());
			}
			for (EditionPatternParameter p : getScheme().getParameters()) {
				availableShemaNameValues.add(p.getName());
			}
		}
		return availableShemaNameValues;
	}

	public String getShemaName(EditionSchemeAction action)
	{
		return generateStringFromIdentifier(_getShemaName(),action);
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
	

	
	@Override
	protected void updatePatternRoleType()
	{
	}

	public String _getShapePatternRole()
	{
		if ((shapePatternRole == null) && (getAvailableShapePatternRoleValues().size() > 0)) {
			shapePatternRole = getAvailableShapePatternRoleValues().firstElement();
		}
		return shapePatternRole;
	}

	public void _setShapePatternRole(String shapePatternRole)
	{
		this.shapePatternRole = shapePatternRole;
	}

	private Vector<String> availableShapePatternRoleValues = null;
	
	public Vector<String> getAvailableShapePatternRoleValues()
	{
		if (availableShapePatternRoleValues == null) {
			availableShapePatternRoleValues = new Vector<String>();
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				if (pr.getType() == PatternRoleType.Shape) {
					availableShapePatternRoleValues.add(pr.getPatternRoleName());
				}
			}
		}
		return availableShapePatternRoleValues;
	}


	public ShapePatternRole retrieveShapePatternRole()
	{
		return (ShapePatternRole)getEditionPattern().getPatternRole(_getShapePatternRole());
	}


}
