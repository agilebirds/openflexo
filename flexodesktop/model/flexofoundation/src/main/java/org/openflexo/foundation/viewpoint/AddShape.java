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
import org.openflexo.foundation.view.OEShemaObject;
import org.openflexo.foundation.view.action.DropSchemeAction;
import org.openflexo.foundation.view.action.EditionSchemeAction;


public class AddShape extends AddShemaElementAction<ShapePatternRole> {

	private static final Logger logger = Logger.getLogger(AddShape.class.getPackage().getName());

	private String container;
	
	public AddShape() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddShape;
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.ADD_SHAPE_INSPECTOR;
	}

	public String getContainer() 
	{
		return container;
	}

	public void setContainer(String container) 
	{
		this.container = container;
	}

	private Vector<String> availableContainerValues = null;
	
	public Vector<String> getAvailableContainerValues()
	{
		if (availableContainerValues == null) {
			availableContainerValues = new Vector<String>();
			switch (getScheme().getEditionSchemeType()) {
			case DropScheme:
				availableContainerValues.add(CONTAINER);
				availableContainerValues.add(CONTAINER_OF_CONTAINER);
				break;
			case LinkScheme:
				availableContainerValues.add(FROM_TARGET);
				availableContainerValues.add(TO_TARGET);
				break;
			default:
				break;
			}
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				availableContainerValues.add(pr.getPatternRoleName());
			}
			for (EditionPatternParameter p : getScheme().getParameters()) {
				availableContainerValues.add(p.getName());
			}
		}
		return availableContainerValues;
	}

	
	public OEShemaObject getContainer(EditionSchemeAction action)
	{
		if (action instanceof DropSchemeAction && getContainer() == null) {
			return ((DropSchemeAction)action).getParent();
		}
		return retrieveOEShemaObject(getContainer(), action);
	}
	


}
