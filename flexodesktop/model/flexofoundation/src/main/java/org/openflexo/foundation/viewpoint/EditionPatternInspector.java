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

import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.model.TabModel;

// TODO: Use FIB instead as soon as possible
@Deprecated 
public class EditionPatternInspector extends InspectorModel implements InspectableObject {

	private EditionPattern _editionPattern;
	
	public EditionPattern getEditionPattern() 
	{
		return _editionPattern;
	}

	public void setEditionPattern(EditionPattern editionPattern)
	{
		_editionPattern = editionPattern;
	}

	public ViewPoint getCalc() 
	{
		return getEditionPattern().getCalc();
	}

	public ViewPointLibrary getCalcLibrary() 
	{
		return getCalc().getViewPointLibrary();
	}

	@Override
	public String getInspectorName() 
	{
		return null;
	}

	public TabModel getDefaultTabModel()
	{
		if (getTabs().size() == 0) makeDefaultTabModel(null);
		return getTabs().elements().nextElement();
	}
	
	private TabModel makeDefaultTabModel(EditionPattern ep)
	{
		TabModel newTabModel = new TabModel();
		if (ep != null) newTabModel.name = ep.getName();
		newTabModel.index = 0;
		setTabForKey(newTabModel, 0);
		return newTabModel;
	}
	
	public static EditionPatternInspector makeEditionPatternInspector(EditionPattern ep)
	{
		EditionPatternInspector returned = new EditionPatternInspector();
		returned.makeDefaultTabModel(ep);
		ep.setInspector(returned);
		return returned;
	}

	public void finalizeInspectorDeserialization()
	{
		for (String k : getDefaultTabModel().getProperties().keySet()) {
			PropertyModel p = getDefaultTabModel().getProperties().get(k);
			if (p instanceof EditionPatternPropertyModel) ((EditionPatternPropertyModel) p).setInspector(this);
			if (p instanceof EditionPatternPropertyListModel) ((EditionPatternPropertyListModel) p).setInspector(this);
		}
	}
	
	public EditionPatternPropertyModel createNewProperty()
	{
		EditionPatternPropertyModel newProperty = new EditionPatternPropertyModel();
		newProperty.setInspector(this);
		newProperty.label = "newProperty";
		newProperty.setPatternRoleReference(getEditionPattern().getPatternRoles().size() > 0 ? getEditionPattern().getPatternRoles().firstElement() : null);
		newProperty.constraint = getDefaultTabModel().getProperties().size();
		newProperty.name = "data";
		int index = 1;
		while (getDefaultTabModel().getProperties().get(newProperty.getKey()) != null) {
			newProperty.name = "data"+index;
			index++;
		}
		getDefaultTabModel().setPropertyForKey(newProperty, newProperty.getKey());
		return newProperty;
	}
	
	public EditionPatternPropertyListModel createNewTableProperty()
	{
		EditionPatternPropertyListModel newProperty = new EditionPatternPropertyListModel();
		newProperty.setInspector(this);
		newProperty.label = "newProperty";
		newProperty.setPatternRoleReference(getEditionPattern().getPatternRoles().size() > 0 ? getEditionPattern().getPatternRoles().firstElement() : null);
		newProperty.constraint = getDefaultTabModel().getProperties().size();
		newProperty.name = "data";
		int index = 1;
		while (getDefaultTabModel().getProperties().get(newProperty.getKey()) != null) {
			newProperty.name = "data"+index;
			index++;
		}
		getDefaultTabModel().setPropertyForKey(newProperty, newProperty.getKey());
		return newProperty;
	}
	
	public PropertyModel deleteProperty(PropertyModel property)
	{
		getDefaultTabModel().removePropertyWithKey(property.getKey());
		return property;
	}
	
 }
