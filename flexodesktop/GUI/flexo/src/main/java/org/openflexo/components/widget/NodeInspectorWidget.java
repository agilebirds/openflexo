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

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.node.AbstractNode;
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
public class NodeInspectorWidget extends CustomInspectorWidget<AbstractNode>
{

    protected static final Logger logger = Logger.getLogger(NodeInspectorWidget.class.getPackage().getName());

    protected NodeSelector _selector;

    public NodeInspectorWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        _selector = new NodeSelector(null, null) {
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
            
            @Override
			public boolean isSelectable (FlexoModelObject object)
            {
                boolean returned = super.isSelectable(object);
                if (returned) {
                    if (hasValueForParameter("isSelectable")) {
                        return getBooleanParameteredValue(getValueForParameter("isSelectable"),object);
                    }
                }
                return returned;
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
        return AbstractNode.class;
    }

    @Override
	public synchronized void updateWidgetFromModel()
    {
        _selector.setEditedObject(getObjectValue());
        _selector.setRevertValue(getObjectValue());
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

    protected void setRootObject(FlexoModelObject rootObject)
    {
    	if (logger.isLoggable(Level.FINE))
    		logger.fine("Setting root object to " + rootObject);
    	_selector.setRootObject(rootObject);
    }

    @Override
	protected void performModelUpdating(InspectableObject value)
    {
    	if (hasValueForParameter("root")) {
    		setRootObject((FlexoModelObject) getDynamicValueForParameter("root", value));
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
