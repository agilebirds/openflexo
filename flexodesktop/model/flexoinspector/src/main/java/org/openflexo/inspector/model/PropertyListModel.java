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
package org.openflexo.inspector.model;

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.inspector.InspectableObject;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class PropertyListModel extends PropertyModel
{

    public static final int DEFAULT_VISIBLE_ROW_COUNT = 10;

    public static final String VISIBLE_ROW_COUNT = "visible_row_count";
    public static final String ROW_HEIGHT = "row_height";

    private Vector<PropertyListColumn> _columns;
    private Vector<PropertyListAction> _actions;
    public boolean createNewRowOnClick = true;

    public PropertyListModel()
    {
        super();
        _columns = new Vector<PropertyListColumn>();
        _actions = new Vector<PropertyListAction>();
    }

    /*public PropertyListModel()
    {
        this(null);
    }*/

    /*public void finalizePropertyListModelDecoding(AbstractController c)
    {

    }*/

    public Vector<PropertyListAction> getActions()
    {
        return _actions;
    }

    public void setActions(Vector<PropertyListAction> actions)
    {
        _actions = actions;
    }

    public void addToActions(PropertyListAction action)
    {
        action.setPropertyListModel(this);
        _actions.add(action);
    }

    public void removeFromActions(PropertyListAction action)
    {
        action.setPropertyListModel(null);
        _actions.remove(action);
    }

    public Vector<PropertyListColumn> getColumns()
    {
        return _columns;
    }

    public void setColumns(Vector<PropertyListColumn> columns)
    {
        _columns = columns;
    }

    public void addToColumns(PropertyListColumn column)
    {
        column.setPropertyListModel(this);
        _columns.add(column);
    }

    public void removeFromColumns(PropertyListColumn column)
    {
        column.setPropertyListModel(null);
        _columns.remove(column);
    }

     public int getVisibleRowCount()
    {
        if (hasValueForParameter(VISIBLE_ROW_COUNT)) {
            return getIntValueForParameter(VISIBLE_ROW_COUNT);
        }
        return DEFAULT_VISIBLE_ROW_COUNT;
    }

    public int getRowHeight()
    {
        if (hasValueForParameter(ROW_HEIGHT)) {
            return getIntValueForParameter(ROW_HEIGHT);
        }
        return -1;
    }

    public PropertyListColumn getPropertyListColumnWithTitle(String title){
    	PropertyListColumn reply = null;
    	Enumeration<PropertyListColumn> en = _columns.elements();
    	while (en.hasMoreElements()) {
			reply = en.nextElement();
			if(reply.name!=null && reply.name.equals(title))return reply;
		}
    	return null;
    }

    private InspectableObject _selectedObject;

    public InspectableObject getSelectedObject()
    {
    	return _selectedObject;
    }

	public void setSelectedObject(InspectableObject selectedObject)
	{
		_selectedObject = selectedObject;
	}
	
	// Might be overriden if we want to inspect a derived object
    public InspectableObject getDerivedModel(InspectableObject baseModel) 
    {
    	return baseModel;
    }
    

}
