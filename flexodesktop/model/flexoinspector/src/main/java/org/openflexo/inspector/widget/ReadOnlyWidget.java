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

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectorCst;
import org.openflexo.inspector.model.PropertyModel;


/**
 * @author gpolet Created on 5 oct. 2005
 */
public class ReadOnlyWidget extends DenaliWidget<String>
{

    JTextField textfield;

    public static final String COLUMNS_PARAM = "columns";
    private static final int DEFAULT_COLUMNS = 5;

   /**
     * @param model
     */
    public ReadOnlyWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        textfield = new JTextField();
        textfield.setEditable(false);
        textfield.setEnabled(true);
        textfield.setSelectionColor(InspectorCst.SELECTED_TEXT_COLOR);
        textfield.setSelectedTextColor(Color.WHITE);
        textfield.addFocusListener(new FocusListener() {

            @Override
			public void focusGained(FocusEvent e)
            {
                textfield.selectAll();
            }

            @Override
			public void focusLost(FocusEvent e)
            {

            }

        });
        
        if (model.hasValueForParameter(COLUMNS_PARAM)) {
            int colNb = model.getIntValueForParameter(COLUMNS_PARAM);
            textfield.setColumns(colNb > 0 ? colNb : DEFAULT_COLUMNS);
        } else {
        	textfield.setColumns(DEFAULT_COLUMNS);
        }
        textfield.setMinimumSize(MINIMUM_SIZE);
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
        textfield.setText(getStringValue());
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
        return textfield;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openflexo.inspector.widget.DenaliWidget#getDefaultType()
     */
    @Override
	public Class getDefaultType()
    {
        return String.class;
    }

}
