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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;


/**
 * @author gpolet
 *
 */
public class RegexpTextfieldWidget extends DenaliWidget<String>
{
    
    private JTextField textfield;
    
    private boolean isUpdatingModel = false;
    
    private boolean isUpdatingWidget = false;
    
    public static final String COLUMNS_PARAM = "columns";
    private static final int DEFAULT_COLUMNS = 5;

    /**
     * @param model
     */
    protected RegexpTextfieldWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        textfield = new JTextField();
        textfield.getDocument().addDocumentListener(new DocumentListener() {

            @Override
			public void changedUpdate(DocumentEvent e)
            {
                checkRegexp();
            }

            @Override
			public void insertUpdate(DocumentEvent e)
            {
                checkRegexp();
            }

            @Override
			public void removeUpdate(DocumentEvent e)
            {
                checkRegexp();
            }

        });
        textfield.addFocusListener(new FocusListener() {

            @Override
			public void focusGained(FocusEvent e)
            {
                
            }

            @Override
			public void focusLost(FocusEvent e)
            {
                updateModelFromWidget();
                updateWidgetFromModel();
            }
            
        });
        textfield.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                updateModelFromWidget();
                updateWidgetFromModel();
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

    void checkRegexp()
    {
        if (textfield.getText()!=null && getPattern().matcher(textfield.getText()).matches())
            textfield.setBackground(Color.WHITE);
        else
            textfield.setBackground(Color.RED);
    }
    
    /**
     * Overrides getDefaultType
     * @see org.openflexo.inspector.widget.DenaliWidget#getDefaultType()
     */
    @Override
    public Class getDefaultType()
    {
        return String.class;
    }

    /**
     * Overrides getDynamicComponent
     * @see org.openflexo.inspector.widget.DenaliWidget#getDynamicComponent()
     */
    @Override
    public JComponent getDynamicComponent()
    {
        return textfield;
    }

    /**
     * Overrides updateModelFromWidget
     * @see org.openflexo.inspector.widget.DenaliWidget#updateModelFromWidget()
     */
    @Override
    public synchronized void updateModelFromWidget()
    {
        isUpdatingModel = true;
        if (!isUpdatingWidget) {
            if (textfield.getText()!=null && getPattern().matcher(textfield.getText()).matches())
                setObjectValue(textfield.getText());
        }
        isUpdatingModel = false;
    }

    /**
     * Overrides updateWidgetFromModel
     * @see org.openflexo.inspector.widget.DenaliWidget#updateWidgetFromModel()
     */
    @Override
    public synchronized void updateWidgetFromModel()
    {
		widgetUpdating = true;
      isUpdatingWidget = true;
        if (!isUpdatingModel) {
            textfield.setText(getStringValue());
        }
        isUpdatingWidget = false;
		widgetUpdating = false;
    }
    
    public Pattern getPattern() {
        return Pattern.compile(getRegexp());
    }
    
    public String getRegexp() {
        if (_propertyModel.getValueForParameter("regexp").indexOf('"')>-1)
            return _propertyModel.getValueForParameter("regexp").substring(1, _propertyModel.getValueForParameter("regexp").length()-1);
        else 
            return getModel().valueForKey(_propertyModel.getValueForParameter("regexp"));
    }

}
