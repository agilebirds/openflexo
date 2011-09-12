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
package org.openflexo.fib.view.widget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.view.FIBWidgetView;


/**
 * Represents a widget able to edit a boolean, a Boolean or a String object
 *
 * @author sguerin
 */
public class FIBCheckBoxWidget extends FIBWidgetView<FIBCheckBox,JCheckBox,Boolean>
{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FIBCheckBoxWidget.class.getPackage().getName());

    private final JCheckBox _jCheckBox;

    private boolean isNegate = false;

    /**
     * @param model
     */
    public FIBCheckBoxWidget(FIBCheckBox model, FIBController controller)
    {
        super(model,controller);
        _jCheckBox = new JCheckBox();
        _jCheckBox.setOpaque(false);
        _jCheckBox.setSelected(model.getSelected());
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
        _jCheckBox.addFocusListener(this);
        
        _jCheckBox.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        
        isNegate = model.getNegate();

        updateFont();
    }

    @Override
    public Boolean getValue() 
    {
    	if (super.getValue() instanceof Boolean) {
    		Boolean returned = super.getValue();
    		return returned;
    	}
    	return false;
    }

    @Override
	public synchronized boolean updateWidgetFromModel()
    {
    	if (notEquals(isNegate?!getValue():getValue(),_jCheckBox.isSelected())) {
    		widgetUpdating = true;
    		Boolean value = getValue();
    		if (value != null) {
    			if (isNegate)
    				value = !value;
    			_jCheckBox.setSelected(value);
    		}
    		widgetUpdating = false;
    		return true;
    	}
    	return false;
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized boolean updateModelFromWidget()
    {
    	if (isReadOnly())
    		return false;

    	if (notEquals(isNegate?!getValue():getValue(),_jCheckBox.isSelected())) {
    		setValue(new Boolean(isNegate?!_jCheckBox.isSelected():_jCheckBox.isSelected()));
    		return true;
    	}
    	return false;

    }

    @Override
    public JCheckBox getJComponent() 
    {
    	return _jCheckBox;
    }
    
	@Override
	public JCheckBox getDynamicJComponent()
	{
		return _jCheckBox;
	}

	@Override
	public Boolean getDefaultData()
	{
		return getComponent().getSelected();
	}
	

}
