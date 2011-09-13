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
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;


/**
 * @author gpolet Created on 5 oct. 2005
 */
public class ReadOnlyCheckBoxWidget extends DenaliWidget<Boolean>
{
	  private static final Logger logger = Logger.getLogger(ReadOnlyCheckBoxWidget.class.getPackage().getName());

    private JPanel _jPanel;

    private JCheckBox _jCheckBox;

    /**
     * @param model
     */
    public ReadOnlyCheckBoxWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);

        _jPanel = new JPanel(new BorderLayout());
        _jPanel.setOpaque(false);
        _jCheckBox = new JCheckBox();
        _jCheckBox.setOpaque(false);
        //_jCheckBox.setBackground(InspectorCst.BACK_COLOR);
        _jCheckBox.setEnabled(false);
       /* _jCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                updateModelFromWidget();
            }
        });*/
        _jPanel.add(_jCheckBox, BorderLayout.WEST);
        _jPanel.add(Box.createGlue(), BorderLayout.CENTER);
        //_jPanel.setBackground(InspectorCst.BACK_COLOR);
        getDynamicComponent().addFocusListener(new WidgetFocusListener(this));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.inspector.widget.DenaliWidget#updateWidgetFromModel()
     */
    @Override
	public void updateWidgetFromModel()
    {
        widgetUpdating = true;
        _jCheckBox.setSelected(getObjectValue()!=null?(Boolean)getObjectValue():false);
        /*if (getType() == Boolean.class) {
            if (getObjectValue() == null) {
                setObjectValue(new Boolean(false));
            }
            _jCheckBox.setSelected(((Boolean) getObjectValue()).booleanValue());
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
        widgetUpdating = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.inspector.widget.DenaliWidget#updateModelFromWidget()
     */
    @Override
	public void updateModelFromWidget()
    {
        // Empty block on purpose since this is read-only <-- Look at this
        // Master comment!
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.inspector.widget.DenaliWidget#getDynamicComponent()
     */
    @Override
	public JComponent getDynamicComponent()
    {
        return _jPanel;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.inspector.widget.DenaliWidget#getDefaultType()
     */
    @Override
	public Class getDefaultType()
    {
        return Boolean.class;
    }

}
