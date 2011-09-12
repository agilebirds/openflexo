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
package org.openflexo.foundation.param;

import java.util.List;
import java.util.Vector;

import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListAction;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.inspector.model.PropertyListModel;
import org.openflexo.inspector.model.PropertyModel;


/**
 * 
 * Abstract definition of a list of parameters
 * 
 * @author sguerin
 */
public class PropertyListParameter<T> extends ParameterDefinition<List<T>> {

	private PropertyListModel _propertyListModel;
    private Vector<PropertyListColumn> _columns;
    private Vector<PropertyListAction> _actions;

    public PropertyListParameter(String name, String label, List<T> list)
    {
        super(name,label,list);
        _columns = new Vector<PropertyListColumn>();
        _actions = new Vector<PropertyListAction>();
    }
    
    public PropertyListParameter(String name, String label, List<T> list,int rowHeight,int visibleRowCount)
    {
        this(name,label,list);
        setRowHeight(rowHeight);
        setVisibleRowCount(visibleRowCount);
    }
    
    public void setRowHeight(int rowHeight)
    {
        addParameter(PropertyListModel.ROW_HEIGHT,""+rowHeight);
    }
    
    public void setVisibleRowCount(int visibleRowCount)
    {
        addParameter(PropertyListModel.VISIBLE_ROW_COUNT,""+visibleRowCount);
    }
    
    public void addTextFieldColumn(String name, String label)
    {
    	addColumn(name,label,PropertyListColumn.TEXT_FIELD);
    }
    
    public void addReadOnlyTextFieldColumn(String name, String label)
    {
    	addColumn(name,label,PropertyListColumn.READ_ONLY_TEXT_FIELD);
    }
    
    public void addCheckboxColumn(String name, String label)
    {
    	addColumn(name,label,PropertyListColumn.CHECKBOX);
    }
    
    public void addDropDownColumn(String name, String label)
    {
    	addColumn(name,label,PropertyListColumn.DROPDOWN);
    }
    
    public void addColorColumn(String name, String label)
    {
    	addColumn(name,label,PropertyListColumn.COLOR);
    }
    
    public void addIconColumn(String name, String label)
    {
    	addColumn(name,label,PropertyListColumn.ICON);
    }
    
    public void addCustomColumn(String name, String label)
    {
    	// TODO supply class name
    	addColumn(name,label,PropertyListColumn.CUSTOM);
    }
    
    public void addTextFieldColumn(String name, String label, int width, boolean resizable)
    {
    	addColumn(name,label,PropertyListColumn.TEXT_FIELD,width,resizable);
    }
    
    public void addTextFieldColumn(String name, String label, int width, boolean resizable, String font)
    {
    	addColumn(name,label,PropertyListColumn.TEXT_FIELD,width,resizable,font);
    }
    
    public void addReadOnlyTextFieldColumn(String name, String label, int width, boolean resizable)
    {
    	addColumn(name,label,PropertyListColumn.READ_ONLY_TEXT_FIELD,width,resizable);
    }
    
    public void addReadOnlyTextFieldColumn(String name, String label, int width, boolean resizable, String font)
    {
    	addColumn(name,label,PropertyListColumn.READ_ONLY_TEXT_FIELD,width,resizable,font);
    }
    
    public void addCheckboxColumn(String name, String label, int width, boolean resizable)
    {
    	addColumn(name,label,PropertyListColumn.CHECKBOX,width,resizable);
    }
    
    public void addDropDownColumn(String name, String label, int width, boolean resizable)
    {
    	addColumn(name,label,PropertyListColumn.DROPDOWN,width,resizable);
    }
    
    public void addColorColumn(String name, String label, int width, boolean resizable)
    {
    	addColumn(name,label,PropertyListColumn.COLOR,width,resizable);
    }
    
    public void addIconColumn(String name, String label, int width, boolean resizable)
    {
    	addColumn(name,label,PropertyListColumn.ICON,width,resizable);
    }
    
    public PropertyListColumn addCustomColumn(String name, String label, String className, int width, boolean resizable)
    {
    	PropertyListColumn newColumn = addColumn(name,label,PropertyListColumn.CUSTOM,width,resizable);
    	newColumn.setValueForParameter("className", className);
    	return newColumn;
    }
    

    private PropertyListColumn addColumn(String name, String label, String widget)
    {
    	PropertyListColumn newColumn = new PropertyListColumn()
    	{
    		@Override
			public void notifyValueChangedFor(InspectableObject object)
    		{
    			notifyValueListeners(getValue(), getValue());
    		}
    	};
    	newColumn.name = name;
    	newColumn.label = label;
    	newColumn.setWidget(widget);
    	_columns.add(newColumn);
    	return newColumn;
    }
    
    private PropertyListColumn addColumn(String name, String label, String widget, int width, boolean resizable)
    {
    	PropertyListColumn newColumn = addColumn(name, label, widget);
    	newColumn.setColumnWidth(width);
    	newColumn.setIsResizable(resizable);
    	return newColumn;
    }
    
    private PropertyListColumn addColumn(String name, String label, String widget, int width, boolean resizable, String font)
    {
    	PropertyListColumn newColumn = addColumn(name, label, widget);
    	newColumn.setColumnWidth(width);
    	newColumn.setIsResizable(resizable);
       	newColumn.setFont(font);
    	return newColumn;
    }
    
    private void addAction(String name, String type, String method, String isAvailable, String help)
    {
    	PropertyListAction newAction = new PropertyListAction();
    	newAction.name = name;
    	newAction.help = help;
    	newAction.type = type;
    	newAction._setMethod(method);
    	newAction._setIsAvailable(isAvailable);
    	_actions.add(newAction);
    }
    
    public void addAddAction(String name, String method, String isAvailable, String help)
    {
    	addAction(name,PropertyListAction.ADD_TYPE,method,isAvailable, help);
    }
    
    public void addDeleteAction(String name, String method, String isAvailable, String help)
    {
    	addAction(name,PropertyListAction.DELETE_TYPE,method,isAvailable, help);
    }
    
    public void addAction(String name, String method, String isAvailable, String help)
    {
    	addAction(name,PropertyListAction.ACTION_TYPE,method,isAvailable, help);
    }
    
    public void addStaticAction(String name, String method, String isAvailable, String help)
    {
    	addAction(name,PropertyListAction.STATIC_ACTION_TYPE,method,isAvailable, help);
    }
    
   @Override
public PropertyModel getPropertyModel() 
    {
        if (_propertyListModel == null) {
        	_propertyListModel = new PropertyListModel();
        	_propertyListModel.name = getName();
        	_propertyListModel.label = getLabel();
        	_propertyListModel.setWidget(getWidgetName());
        	_propertyListModel.depends = getDepends();
        	_propertyListModel.help = getHelp();
        	_propertyListModel.conditional = getConditional();
        	_propertyListModel.parameters = parameters;
        	_propertyListModel.setColumns(_columns);
        	_propertyListModel.setActions(_actions);
        }
        return _propertyListModel;
    }

	@Override
	public String getWidgetName()
	{
		return null;
	}
    
	public T getSelectedObject()
	{
		return (T)_propertyListModel.getSelectedObject();
	}


}
