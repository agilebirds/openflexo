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

import org.openflexo.foundation.viewpoint.dm.InspectorEntryInserted;
import org.openflexo.foundation.viewpoint.dm.InspectorEntryRemoved;

/**
 * Represents inspector associated with an Edition Pattern
 * 
 * @author sylvain
 *
 */
public class EditionPatternInspector extends ViewPointObject {

	private String inspectorTitle;
	private EditionPattern _editionPattern;
	private Vector<InspectorEntry> entries;
	
	public static EditionPatternInspector makeEditionPatternInspector(EditionPattern ep)
	{
		EditionPatternInspector returned = new EditionPatternInspector();
		ep.setInspector(returned);
		return returned;
	}
	
	public EditionPatternInspector() 
	{
		super();
		entries = new Vector<InspectorEntry>();
	}

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
		if (getEditionPattern() != null)
			return getEditionPattern().getCalc();
		return null;
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

	public String getInspectorTitle()
	{
		return inspectorTitle;
	}

	public void setInspectorTitle(String inspectorTitle)
	{
		this.inspectorTitle = inspectorTitle;
	}

	public Vector<InspectorEntry> getEntries() 
	{
		return entries;
	}

	public void setEntries(Vector<InspectorEntry> someEntries)
	{
		entries = someEntries;
	}

	public void addToEntries(InspectorEntry anEntry)
	{
		anEntry.setInspector(this);
		entries.add(anEntry);
		setChanged();
		notifyObservers(new InspectorEntryInserted(anEntry, this));
	}

	public void removeFromEntries(InspectorEntry anEntry)
	{
		anEntry.setInspector(null);
		entries.remove(anEntry);
		setChanged();
		notifyObservers(new InspectorEntryRemoved(anEntry, this));
	}
	
	public TextFieldInspectorEntry createNewTextField()
	{
		TextFieldInspectorEntry newEntry = new TextFieldInspectorEntry();
		newEntry.setLabel("new_entry");
		newEntry.setPatternRole(getEditionPattern().getPatternRoles().size() > 0 ? getEditionPattern().getPatternRoles().firstElement() : null);
		addToEntries(newEntry);
		return newEntry;
	}
	
	/*public EditionPatternPropertyModel createNewProperty()
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
	}*/
	
 }
