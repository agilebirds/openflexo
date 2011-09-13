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
package org.openflexo.inspector.widget;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;


/**
 * Represents a widget able to edit a boolean, a Boolean or a String object
 *
 * @author sguerin
 */
public class CheckBoxWidget extends DenaliWidget<Object>
{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CheckBoxWidget.class.getPackage().getName());

    private JPanel _jPanel;

    private JCheckBox _jCheckBox;

    private boolean isNegate = false;

    /**
     * @param model
     */
    public CheckBoxWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        _jPanel = new JPanel(new BorderLayout());
        _jPanel.setOpaque(false);
        _jCheckBox = new JCheckBox();
        _jCheckBox.setOpaque(false);
        //_jCheckBox.setBackground(InspectorCst.BACK_COLOR);
        if (isReadOnly())
        	_jCheckBox.setEnabled(false);
        else
	        _jCheckBox.addActionListener(new ActionListener() {
	        	@Override
				public void actionPerformed(ActionEvent e)
	        	{
	        		updateModelFromWidget();
	        	}
	        });
        _jPanel.add(_jCheckBox, BorderLayout.WEST);
        _jPanel.add(Box.createGlue(), BorderLayout.CENTER);
        //_jPanel.setBackground(InspectorCst.BACK_COLOR);
        getDynamicComponent().addFocusListener(new WidgetFocusListener(this));
        Boolean b = getPropertyModel().getBooleanValueForParameter("negate");
        isNegate = (b!=null && b);
    }

    @Override
	public synchronized void updateWidgetFromModel()
    {
		widgetUpdating = true;
        if (getObjectValue() == null) {
            setObjectValue(new Boolean(false));
        }
         boolean value = getObjectValue()!=null?(Boolean)getObjectValue():false;
         if (isNegate)
        	 value = !value;
		_jCheckBox.setSelected(value);
 		widgetUpdating = false;

    	/*
    	if (getType() == Boolean.class) {
            if (getObjectValue() == null) {
                setObjectValue(new Boolean(false));
            }
            boolean value = (getObjectValue()!=null?(Boolean)getObjectValue():false);
            _jCheckBox.setSelected(value);
        } else if (getType() == Boolean.TYPE) {
            _jCheckBox.setSelected(getBooleanValue());
        } else if (getType() == String.class) {
            String value = (String) getObjectValue();
            if (value != null) {
                _jCheckBox.setSelected(value.equalsIgnoreCase("true"));
            } else {
                _jCheckBox.setSelected(false);
            }
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Property " + _propertyModel.name + " is supposed to be a boolean or a Boolean or a String, not a " + getType());
        }*/
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
    	if (isReadOnly())
    		return;

        setObjectValue(new Boolean(isNegate?!_jCheckBox.isSelected():_jCheckBox.isSelected()));

        /*if (getType() == Boolean.class) {
            setObjectValue(new Boolean(_jCheckBox.isSelected()));
        } else if (getType() == Boolean.TYPE) {
            setBooleanValue(_jCheckBox.isSelected());
        } else if (getType() == String.class) {
            setObjectValue(_jCheckBox.isSelected() ? "true" : "false");
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Property " + _propertyModel.name + " is supposed to be a boolean or a Boolean or a String, not a " + getType());
        }*/
    }

    @Override
	public JComponent getDynamicComponent()
    {
        return _jPanel;
    }

    @Override
	public Class getDefaultType()
    {
        return Boolean.class;
    }

}
