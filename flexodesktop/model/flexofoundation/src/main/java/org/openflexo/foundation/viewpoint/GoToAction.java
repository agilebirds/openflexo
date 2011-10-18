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

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.action.EditionSchemeAction;


// Note: no Pattern Role for this action !!!
public class GoToAction extends EditionAction {

	private static final Logger logger = Logger.getLogger(GoToAction.class.getPackage().getName());
	
	private String targetObject;
	
	public GoToAction() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.GoToObject;
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.GO_TO_OBJECT_INSPECTOR;
	}

	public String _getTargetObject()
	{
		return targetObject;
	}
	
	public void _setTargetObject(String aTargetObject)
	{
		targetObject = aTargetObject;
	}
	
	private Vector<String> availableTargetObjectValues = null;
	
	public Vector<String> getAvailableTargetObjectValues()
	{
		if (availableTargetObjectValues == null) {
			availableTargetObjectValues = new Vector<String>();
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				availableTargetObjectValues.add(pr.getPatternRoleName());
			}
			for (EditionSchemeParameter p : getScheme().getParameters()) {
				availableTargetObjectValues.add(p.getName());
			}
		}
		return availableTargetObjectValues;
	}


	public FlexoModelObject getTargetObject(EditionSchemeAction action)
	{
		return retrieveFlexoModelObject(_getTargetObject(), action);
	}


	/*@Override
	protected void updatePatternRoleType()
	{
		// Not relevant
	}*/


}
