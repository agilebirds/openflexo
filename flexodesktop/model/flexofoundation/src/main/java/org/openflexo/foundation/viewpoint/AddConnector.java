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
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.action.DropSchemeAction;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.action.LinkSchemeAction;


public class AddConnector extends AddShemaElementAction<ConnectorPatternRole> {

	private static final Logger logger = Logger.getLogger(LinkSchemeAction.class.getPackage().getName());

	private String fromShape;
	private String toShape;
	
	public AddConnector() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddConnector;
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.ADD_CONNECTOR_INSPECTOR;
	}
	

	public String _getFromShape() 
	{
		return fromShape;
	}

	public void _setFromShape(String fromShape) 
	{
		this.fromShape = fromShape;
	}

	public String _getToShape() 
	{
		return toShape;
	}

	public void _setToShape(String toShape) 
	{
		this.toShape = toShape;
	}

	private Vector<String> availableFromToShapeValues = null;
	
	public Vector<String> getAvailableFromToShapeValues()
	{
		if (availableFromToShapeValues == null) {
			availableFromToShapeValues = new Vector<String>();
			switch (getScheme().getEditionSchemeType()) {
			case DropScheme:
				availableFromToShapeValues.add(CONTAINER);
				availableFromToShapeValues.add(CONTAINER_OF_CONTAINER);
				break;
			case LinkScheme:
				availableFromToShapeValues.add(FROM_TARGET);
				availableFromToShapeValues.add(TO_TARGET);
				break;
			default:
				break;
			}
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				availableFromToShapeValues.add(pr.getPatternRoleName());
			}
			for (EditionPatternParameter p : getScheme().getParameters()) {
				availableFromToShapeValues.add(p.getName());
			}
		}
		return availableFromToShapeValues;
	}

	public ViewShape getFromShape(EditionSchemeAction action)
	{
		if (action instanceof LinkSchemeAction && _getFromShape() == null) 
			return ((LinkSchemeAction)action).getFromShape();
		if (action instanceof DropSchemeAction && _getFromShape() == null 
				&& ((DropSchemeAction)action).getParent() instanceof ViewShape) 
			return (ViewShape)((DropSchemeAction)action).getParent();
		return retrieveOEShape(_getFromShape(), action);
	}
	
	public ViewShape getToShape(EditionSchemeAction action)
	{
		if (action instanceof LinkSchemeAction && _getToShape() == null) 
			return ((LinkSchemeAction)action).getToShape();
		if (action instanceof DropSchemeAction && _getToShape() == null 
				&& ((DropSchemeAction)action).getParent() instanceof ViewShape) 
			return (ViewShape)((DropSchemeAction)action).getParent();
		return retrieveOEShape(_getToShape(), action);
	}

	@Override
	public String toString()
	{
		return "AddConnector "+Integer.toHexString(hashCode())+" patternRole="+getPatternRole();
	}
	
}
