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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.toolbox.ToolBox;


/**
 * Simple widget allowing to display/edit a String
 * 
 * @author bmangez
 */
public class FIBTextFieldWidget extends FIBWidgetView<FIBTextField,JTextField,String>
{

    private static final Logger logger = Logger.getLogger(FIBTextFieldWidget.class.getPackage()
            .getName());

	private static final int DEFAULT_COLUMNS = 10;

    private JTextField _textField;

    boolean validateOnReturn;

    public FIBTextFieldWidget(FIBTextField model, FIBController controller)
    {
        super(model,controller);
        if (model.passwd) {
            _textField = new JPasswordField() {
                @Override
                public Dimension getMinimumSize()
                {
                    return MINIMUM_SIZE;
                }
            };
        } 
        else {
        	_textField = new JTextField() {
                @Override
                public Dimension getMinimumSize()
                {
                    return MINIMUM_SIZE;
                }
            };
        }
        
        if (isReadOnly()) {
        	_textField.setEditable(false);
        }
        
        validateOnReturn = model.validateOnReturn;
        if (model.columns != null) {
        	_textField.setColumns(model.columns);
        }
        else {
        	_textField.setColumns(DEFAULT_COLUMNS);
        }

       if (model.text != null) _textField.setText(model.text);
        
       _textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void changedUpdate(DocumentEvent e)
            {
                  if ((!validateOnReturn) && (!widgetUpdating)) {
                    updateModelFromWidget();
                }
            }

            @Override
			public void insertUpdate(DocumentEvent e)
            {
                 if ((!validateOnReturn) && (!widgetUpdating)) {
                 	try {
						if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
							if (e.getLength() == 1) {
								char c = _textField.getText().charAt(
										e.getOffset());
								if (c == '´' || c == 'ˆ' || c=='˜'||c=='`'||c=='¨')
									return;
							}
						}
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}             		
                    updateModelFromWidget();
                }
            }

            @Override
			public void removeUpdate(DocumentEvent e)
            {
                if ((!validateOnReturn) && (!widgetUpdating)) {
                    updateModelFromWidget();
                }
            }
        });
        _textField.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                 updateModelFromWidget();
            }
        });
        _textField.addFocusListener(this);

        updateFont();
    }

    @Override
    public void focusGained(FocusEvent arg0)
    {
    	super.focusGained(arg0);
    	_textField.selectAll();
    }


    public Class getDefaultType()
    {
        return String.class;
    }

    @Override
	public synchronized boolean updateWidgetFromModel()
    {
    	if (notEquals(getValue(), _textField.getText())) {
    		if (modelUpdating)
    			return false;
    		widgetUpdating = true;
    		int caret = _textField.getCaretPosition();
    		_textField.setText(getValue());
    		if (caret>-1 && caret<_textField.getDocument().getLength())
    			_textField.setCaretPosition(caret);
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
    	if (notEquals(getValue(), _textField.getText())) {
    		modelUpdating = true;
    		if (logger.isLoggable(Level.FINE)) logger.fine("updateModelFromWidget() in TextFieldWidget");
    		//logger.info("setValue with "+_textField.getText());
    		setValue(_textField.getText());
    		modelUpdating = false;
    		return true;
    	}
    	return false;
    }

	@Override
	public JTextField getJComponent() 
	{
		return _textField;
	}

	@Override
	public JTextField getDynamicJComponent()
	{
		return _textField;
	}

}
