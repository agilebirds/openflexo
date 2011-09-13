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

import java.awt.Color;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.swing.ColorSelector;

public class FIBColorWidget extends FIBWidgetView<FIBColor,ColorSelector,Color> implements ColorSelectionModel
{

    private static final Logger logger = Logger.getLogger(FIBColorWidget.class.getPackage().getName());

    //public static final Icon ARROW_DOWN = new ImageIconResource("Resources/ArrowDown.gif");

    protected ColorSelector _selector;
	private final Vector<ChangeListener> _listeners;
	
    public FIBColorWidget(FIBColor model, FIBController controller)
    {
        super(model,controller);
        
        _listeners = new Vector<ChangeListener>();
        
        _selector = new ColorSelector(this);
        if (isReadOnly()) {
        	_selector.getDownButton().setEnabled(false);
        }
        	        
       getJComponent().addFocusListener(this);

        updateFont();
    }

    @Override
	public synchronized boolean updateWidgetFromModel()
    {
    	//if (notEquals(getValue(),getSelectedColor())) {
    		widgetUpdating = true;
    		setColor(getValue());
    		widgetUpdating = false;
    		return true;
    		//}
    		//return false;
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized boolean updateModelFromWidget()
    {
    	if (notEquals(getValue(),getSelectedColor())) {
    		if (isReadOnly()) {
				return false;
			}
    		setValue(_selector.getEditedObject());
    		return true;
    	}
    	return false;
    }

    @Override
	public ColorSelector getJComponent()
    {
        return _selector;
    }

	@Override
	public ColorSelector getDynamicJComponent()
	{
		return _selector;
	}

    protected void setColor(Color aColor)
    {
    	_selector.setEditedObject(aColor);
    }

	@Override
	public Color getSelectedColor()
	{
		return getValue();
	}

	@Override
	public void addChangeListener(ChangeListener listener)
	{
		_listeners.add(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener)
	{
		_listeners.remove(listener);
	}

	@Override
	public void setSelectedColor(Color color)
	{
		setValue(color);
		for (ChangeListener l : _listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}

}
