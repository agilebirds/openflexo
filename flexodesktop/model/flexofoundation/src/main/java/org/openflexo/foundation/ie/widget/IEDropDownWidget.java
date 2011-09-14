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
package org.openflexo.foundation.ie.widget;

import java.io.Serializable;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * Represents a dropdown widget
 * 
 * @author bmangez
 */
public class IEDropDownWidget extends IEAbstractListWidget implements Serializable
{
	
	
	/**
     * 
     */
    public static final String DROPDOWN_WIDGET = "dropdown_widget";
    private AbstractBinding _bindingIsEdit;
	
	private WidgetBindingDefinition _bindingSelectionDefinition = null;

    public IEDropDownWidget(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

    /**
     * 
     */
    public IEDropDownWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
    }

    @Override
	public void setContentType(DMType contentType) 
    {
       	_bindingSelectionDefinition = null;
       	super.setContentType(contentType);
    }

    @Override
	public WidgetBindingDefinition getBindingSelectionDefinition()
    {
    	if (_bindingSelectionDefinition == null) {
    		_bindingSelectionDefinition = new WidgetBindingDefinition("bindingSelection",getContentType(),this,BindingDefinitionType.GET_SET,true);
        	if (getBindingSelection() != null) 
        		getBindingSelection().setBindingDefinition(_bindingSelectionDefinition);
   	}
    	return _bindingSelectionDefinition;
    }
    
    /**
     * Overrides getClassNameKey
     * 
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return DROPDOWN_WIDGET;
    }
    
    public WidgetBindingDefinition getBindingIsEditDefinition()
    {
        return WidgetBindingDefinition.get(this, "bindingIsEdit", Boolean.TYPE, BindingDefinitionType.GET, false);
    }

    public AbstractBinding getBindingIsEdit()
    {
        if (isBeingCloned())
            return null;
        return _bindingIsEdit;
    }

    public void setBindingIsEdit(AbstractBinding isEdit)
    {
    	_bindingIsEdit = isEdit;
        if (_bindingIsEdit != null) {
        	_bindingIsEdit.setOwner(this);
        	_bindingIsEdit.setBindingDefinition(getBindingNoSelectionStringDefinition());
        }
        setChanged();
        notifyObservers(new IEDataModification("bindingIsEdit", null, isEdit));
    }
    
    @Override
	public String getProcessInstanceDictionaryKey()
	{
		if(isStatusList())
			return FlexoProcess.PROCESSINSTANCE_STATUS_KEY;
		return super.getProcessInstanceDictionaryKey();
	}
}
