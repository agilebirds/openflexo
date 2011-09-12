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
package org.openflexo.components.tabular.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.FlexoCst;
import org.openflexo.components.widget.binding.BindingSelector;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.rm.FlexoProject;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class BindingValueColumn<D extends FlexoModelObject> extends CustomColumn<D,AbstractBinding>
{

     protected static final Logger logger = Logger.getLogger(BindingValueColumn.class.getPackage().getName());
      
    public BindingValueColumn(String title, int defaultWidth, FlexoProject project, boolean allowsCompoundBinding, boolean allowsNewEntryCreation)
    {
        super(title, defaultWidth, project);
     }

    public abstract boolean allowsCompoundBinding(AbstractBinding value);

    public abstract boolean allowsNewEntryCreation(AbstractBinding value);

    @Override
	public Class<AbstractBinding> getValueClass()
    {
        return AbstractBinding.class;
    }

    private BindingSelector _viewSelector;

    private BindingSelector _editSelector;

    private void updateSelectorWith(BindingSelector selector,D rowObject, AbstractBinding value)
    {
    	AbstractBinding oldBV = selector.getEditedObject();
        if ((oldBV == null) || (!oldBV.equals(value))) {
            selector.setEditedObjectAndUpdateBDAndOwner(value);
            selector.setRevertValue(value!=null?value.clone():null);
            /*if (allowsNewEntryCreation(value)) {
                selector.allowsNewEntryCreation();
            }
            else {
                selector.denyNewEntryCreation();
            }*/
            selector.setBindable(getBindableFor(value,rowObject));
            selector.setBindingDefinition(getBindingDefinitionFor(value,rowObject));
            if (logger.isLoggable(Level.FINE))
            	logger.fine("Selector: "+selector.toString()+" rowObject="+rowObject+""+" binding="+value+" with BindingDefinition "+getBindingDefinitionFor(value,rowObject));
       }
     }
    
    public abstract Bindable getBindableFor(AbstractBinding value, D rowObject);
    public abstract BindingDefinition getBindingDefinitionFor(AbstractBinding value, D rowObject);
    
    @Override
	protected BindingSelector getViewSelector(D rowObject, AbstractBinding value)
    {
        if (_viewSelector == null) {
            _viewSelector = new BindingSelector(value) {
            	@Override
            	public String toString() {
            		return "VIEW";
            	}
            };
            _viewSelector.setFont(FlexoCst.MEDIUM_FONT);
        }
        updateSelectorWith(_viewSelector,rowObject,value);
        return _viewSelector;
    }

    @Override
	protected BindingSelector getEditSelector(D rowObject, AbstractBinding value)
    {
        if (_editSelector == null) {
            _editSelector = new BindingSelector(value) {
                @Override
				public void apply()
                {
                    super.apply();
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Apply");
                    if (_editedRowObject != null) {
                        setValue(_editedRowObject, getEditedObject());
                    }
                }
               @Override
			public void cancel()
                {
                    super.cancel();
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Cancel");
                    if (_editedRowObject != null) {
                        setValue(_editedRowObject, getRevertValue());
                    }
                }
                	
               	/*@Override
               	public void setEditedObject(AbstractBinding object) {
                   setBindable(getBindableFor(object,_editedRowObject));
                   setBindingDefinition(getBindingDefinitionFor(object,_editedRowObject));
               		super.setEditedObject(object);
               	}*/
               	@Override
               	public String toString() {
               		return "EDIT";
               	}
            };
            _editSelector.setFont(FlexoCst.NORMAL_FONT);
        }
        updateSelectorWith(_editSelector,rowObject,value);
        return _editSelector;
    }
    
    public BindingVariable createsNewEntry(String name, DMEntity type, DMPropertyImplementationType implementationType, DMEntity selectedEntityInPanel)
    {
        // Must be overriden if used
        return null;
    }


}
