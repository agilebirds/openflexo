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

import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.FlexoProject;
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
public class DMEOAttributeInspectorWidget extends CustomInspectorWidget<DMEOAttribute>
{

    static final Logger logger = Logger.getLogger(DMEOAttributeInspectorWidget.class.getPackage().getName());

    DMEOAttributeSelector _selector;

    public DMEOAttributeInspectorWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        _selector = new DMEOAttributeSelector(null, null) {
            @Override
			public void apply()
            {
                super.apply();
                updateModelFromWidget();
            }

            @Override
			public void cancel()
            {
                super.cancel();
                updateModelFromWidget();
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
        return DMEOAttribute.class;
    }

    @Override
	public synchronized void updateWidgetFromModel()
    {
        _selector.setEditedObject(getObjectValue());
        _selector.setRevertValue(getObjectValue());
        performModelUpdating(getModel());
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
        setObjectValue(_selector.getEditedObject());
    	super.updateModelFromWidget();
   }

    @Override
	public JComponent getDynamicComponent()
    {
        return _selector;
    }

    @Override
	public void setProject(FlexoProject aProject)
    {
        super.setProject(aProject);
        _selector.setProject(aProject);
    }

    protected void setDMEOEntity(DMEOEntity entity)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Setting entity to " + entity);
        _selector.setRootObject(entity);
    }

    @Override
	protected void performModelUpdating(InspectableObject value)
    {
        if (hasValueForParameter("entity")) {
        	if (logger.isLoggable(Level.FINE))
    			logger.fine("performModelUpdating() in DMEOAttributeInspectorWidget in 'entity' value="+value+" value="+getDynamicValueForParameter("entity", value));
            setDMEOEntity((DMEOEntity) getDynamicValueForParameter("entity", value));
        }
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
