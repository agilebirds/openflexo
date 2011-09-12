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
package org.openflexo.components.widget;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.components.widget.binding.FlattenRelationshipDefinitionSelector;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.FlattenRelationshipDefinition;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.WidgetFocusListener;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlattenRelationshipDefinitionSelectorInspectorWidget extends CustomInspectorWidget<FlattenRelationshipDefinition>
{

    static final Logger logger = Logger.getLogger(FlattenRelationshipDefinitionSelectorInspectorWidget.class.getPackage().getName());

    protected FlattenRelationshipDefinitionSelector _selector;
    
    private boolean isUpdatingModel = false;

    public FlattenRelationshipDefinitionSelectorInspectorWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        _selector = new FlattenRelationshipDefinitionSelector(null,null) {
            @Override
			public void apply()
            {
            	isUpdatingModel = true;
                super.apply();
                updateModelFromWidget();
            }

            @Override
			public void cancel()
            {
                super.cancel();
                updateModelFromWidget();
            }

            public BindingVariable createsNewEntry(String name, DMEntity type, DMPropertyImplementationType implementationType, DMEntity selectedEntityInPanel)
            {
            	if(getEditedObject()!=null && getEditedObject().getAccessedType()!=null && !getEditedObject().getAccessedType().getBaseEntity().getIsReadOnly()){
            		BindingVariable returned = performCreatesEntry(name, DMType.makeResolvedDMType(type), implementationType,getEditedObject().getAccessedType().getBaseEntity(),getEditedObject());
            		getTextField().setText(getEditedObject().getStringRepresentation());
            		return returned;
            	}else{
            		return performCreatesEntry(name, DMType.makeResolvedDMType(type), implementationType,null,null);
            	}
                
            }

        };
        getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
            @Override
			public void focusGained(FocusEvent arg0)
            {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Focus gained in " + getClass().getName());
                super.focusGained(arg0);
                _selector.getTextField().requestFocus();
                _selector.getTextField().selectAll();
            }

            @Override
			public void focusLost(FocusEvent arg0)
            {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Focus lost in " + getClass().getName());
                super.focusLost(arg0);
            }
        });
    }

    @Override
	public Class getDefaultType()
    {
        return FlattenRelationshipDefinition.class;
    }

    @Override
	public synchronized void updateWidgetFromModel()
    {
    	if (isUpdatingModel)
    		return;
        if (!_selector.getIsUpdatingModel()) {
            _selector.setEditedObject(getObjectValue());
            _selector.setRevertValue(getObjectValue());
        }
        /*BindingValue currentValue = (BindingValue)getObjectValue();
        if (currentValue != null) {
            currentValue.setBindingDefinition(_selector.getBindingDefinition());
         }*/

    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
    	isUpdatingModel = true;
    	try {
    		setObjectValue(_selector.getEditedObject());
    	} finally { isUpdatingModel = false;}
    	super.updateModelFromWidget();
    }

    @Override
	public JComponent getDynamicComponent()
    {
        return _selector;
    }

    @Override
	protected void performModelUpdating(InspectableObject value)
    {
    	super.performModelUpdating(value);

    	_selector.setEditedObject(getObjectValue());
    	_selector.setRevertValue(getObjectValue());

    	if (hasValueForParameter("source_entity")) {
    		DMEOEntity sourceEntity = (DMEOEntity) getDynamicValueForParameter("source_entity", value);
    		_selector.setSourceEntity(sourceEntity);
    	}
    	_selector.setAllowsEntryCreation(false);

    }

    private Method _createsEntryMethod;

    private Method getCreatesEntryMethod()
    {
        if (_createsEntryMethod == null) {
            String methodName = PropertyModel.getLastAccessor(getValueForParameter("creates_entry"));
            if (getModel() != null) {
                Class targetClass = PropertyModel.getTargetObject(getModel(), getValueForParameter("creates_entry")).getClass();
                Class[] methodClassParams = new Class[3];
                methodClassParams[0] = String.class;
                methodClassParams[1] = DMEntity.class;
                methodClassParams[2] = DMPropertyImplementationType.class;
                try {
                    _createsEntryMethod = targetClass.getMethod(methodName, methodClassParams);
                } catch (SecurityException e) {
                    // Warns about the exception
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("SecurityException raised: " + e.getClass().getName() + ". See console for details.");
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    // Warns about the exception
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("NoSuchMethodException raised: unable to find method " + methodName + " for class " + targetClass);
                    e.printStackTrace();
                }
            }
        }
        return _createsEntryMethod;
    }

    
    
    
    BindingVariable performCreatesEntry(String name, DMType type, DMPropertyImplementationType implementationType, DMEntity newEntryEntity, AbstractBinding editedObject)
    {
    	if (editedObject instanceof FlattenRelationshipDefinition) {
    		FlattenRelationshipDefinition definition = (FlattenRelationshipDefinition)editedObject;
        if (getCreatesEntryMethod() != null) {
            Object[] params = new Object[3];
            params[0] = name;
            params[1] = type;
            params[2] = implementationType;
            //DMEntity whishedEntity = getEditedValue().getAccessedType();
            //System.err.println("whishedEntity:"+whishedEntity.getName());
            try {
            	if(newEntryEntity==null){
            		Object targetObject = PropertyModel.getTargetObject(getModel(), getValueForParameter("creates_entry"));
            		if (logger.isLoggable(Level.INFO))
            			logger.info("invoking " + getCreatesEntryMethod() + " on object" + targetObject);
            		return (BindingVariable) getCreatesEntryMethod().invoke(targetObject, params);
            	}else{
            		DMProperty newProperty = newEntryEntity.createDMProperty(name, type, implementationType);
            		if(editedObject.getBindingDefinition() instanceof WidgetBindingDefinition && newProperty.getEntity() instanceof ComponentDMEntity){
            			((ComponentDMEntity)newProperty.getEntity()).setBindable(newProperty, false);
            		}
            		if(newProperty!=null) definition.addBindingPathElement(newProperty);
            		return definition.getBindingVariable();
            	}
            } catch (IllegalArgumentException e) {
                // Warns about the exception
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // Warns about the exception
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // Warns about the exception
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                e.printStackTrace();
            }
        }
    	}
        return null;
    }

    public Color getColorForObject(BindingValue value) 
    {
        return (value.isBindingValid()?Color.BLACK:Color.RED);
    }

    @Override
    public void fireEditingCanceled() 
    {
    	if (_selector != null) _selector.closePopup();
    }
    
    @Override
    public void fireEditingStopped()     
    {
    	if (_selector != null) _selector.closePopup();
    }


}
