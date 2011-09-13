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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;


/**
 * @author gpolet
 *
 */
public class ButtonWidget extends DenaliWidget implements ActionListener
{
    private static final Logger logger = FlexoLogger.getLogger(ButtonWidget.class.getPackage().getName());
    private JButton button;
    
    /**
     * @param model
     * @param controller
     */
    protected ButtonWidget(PropertyModel model, AbstractController controller)
    {
        super(model, controller);
        button = new JButton();
        button.setText(FlexoLocalization.localizedForKey(_propertyModel.label,button));
        button.addActionListener(this);
    }

    /**
     * Overrides getDefaultType
     * @see org.openflexo.inspector.widget.DenaliWidget#getDefaultType()
     */
    @Override
    public Class getDefaultType()
    {
        return null;
    }

    /**
     * Overrides getDynamicComponent
     * @see org.openflexo.inspector.widget.DenaliWidget#getDynamicComponent()
     */
    @Override
    public JComponent getDynamicComponent()
    {
        return button;
    }
    
    /**
     * Overrides defaultShouldExpandHorizontally
     * @see org.openflexo.inspector.widget.DenaliWidget#defaultShouldExpandHorizontally()
     */
    @Override
    public boolean defaultShouldExpandHorizontally()
    {
        return false;
    }

    @Override
	public boolean defaultDisplayLabel()
    {
        return false;
    }
    
    /**
     * Overrides updateModelFromWidget
     * @see org.openflexo.inspector.widget.DenaliWidget#updateModelFromWidget()
     */
    @Override
    public void updateModelFromWidget()
    {

    }

    /**
     * Overrides updateWidgetFromModel
     * @see org.openflexo.inspector.widget.DenaliWidget#updateWidgetFromModel()
     */
    @Override
    public void updateWidgetFromModel()
    {

    }

    /**
     * Overrides actionPerformed
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e)
    {
        if (_propertyModel.hasValueForParameter("action")) {
            if (getController().getDelegate()!=null)
                getController().getDelegate().performAction(e, _propertyModel.getValueForParameter("action"), getModel());
            else
                if (logger.isLoggable(Level.SEVERE))
                    logger.severe("No delegate for inspector. Cannot perform action "+_propertyModel.getValueForParameter("action")+" on object "+getModel().toString());
        }
        String s = _propertyModel.name;
        ((KeyValueCoding)getModel()).objectForKey(s);
        
        // Notification required for tab view updating
        notifyInspectedPropertyChanged();
    }

    @Override
	protected synchronized Class retrieveType()
    {
    	// Otherwise call action associated with button
    	return Void.TYPE;
    }
}
