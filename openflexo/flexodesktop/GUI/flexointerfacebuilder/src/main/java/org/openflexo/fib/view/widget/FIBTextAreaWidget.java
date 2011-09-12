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

import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.view.FIBWidgetView;


/**
 * Represents a widget able to edit a Text (more than one line) object
 * 
 * @author bmangez,sguerin
 */
public class FIBTextAreaWidget extends FIBWidgetView<FIBTextArea,JTextArea,String>
{

    private static final Logger logger = Logger.getLogger(FIBTextAreaWidget.class.getPackage().getName());

	private static final int DEFAULT_COLUMNS = 30;
	private static final int DEFAULT_ROWS = 5;

    private final JTextArea _textArea;
    //private final JScrollPane pane;
    boolean validateOnReturn;

    public FIBTextAreaWidget(FIBTextArea model, FIBController controller)
    {
        super(model,controller);
        _textArea = new JTextArea();
        validateOnReturn = model.validateOnReturn;
        if (model.columns != null && model.columns > 0) _textArea.setColumns(model.columns);
        else _textArea.setColumns(DEFAULT_COLUMNS);
        if (model.rows != null && model.rows > 0)  _textArea.setRows(model.rows);
        else _textArea.setRows(DEFAULT_ROWS);
        _textArea.setEditable(!isReadOnly());
        if (model.text != null) _textArea.setText(model.text);
        
        _textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void changedUpdate(DocumentEvent e)
            {
                 if ((!validateOnReturn) && (!widgetUpdating))
                     updateModelFromWidget();
            }

            @Override
			public void insertUpdate(DocumentEvent e)
            {
                if ((!validateOnReturn) && (!widgetUpdating))
                    updateModelFromWidget();
            }

            @Override
			public void removeUpdate(DocumentEvent e)
            {
                if ((!validateOnReturn) && (!widgetUpdating))
                    updateModelFromWidget();
            }
        });
        _textArea.addKeyListener(new KeyAdapter() {
            @Override
			public void keyPressed(KeyEvent e)
           {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	updateModelFromWidget();
                }
            }
        });
         _textArea.addFocusListener(this);

        _textArea.setAutoscrolls(true);
        _textArea.setLineWrap(true);
        _textArea.setWrapStyleWord(true);
        _textArea.setEnabled(true);
        /*pane = new JScrollPane(_textArea);
        
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
 
        pane.setMinimumSize(MINIMUM_SIZE);*/

        updateFont();
    }
    
    @Override
    public void focusGained(FocusEvent event)
    {
    	super.focusGained(event);
        _textArea.selectAll();
    }

    @Override
    public void updateDataObject(Object aDataObject)
    {
    	super.updateDataObject(aDataObject);
    	_textArea.setEditable(!isReadOnly());
    }

    @Override
	public synchronized boolean updateWidgetFromModel()
    {
    	if (notEquals(getValue(), _textArea.getText())) {
    		if (modelUpdating) return false;
    		if (getValue() != null && (getValue()+"\n").equals(_textArea.getText())) return false;
    		widgetUpdating = true;
    		_textArea.setText(getValue());
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
    	if (notEquals(getValue(), _textArea.getText())) {
    		modelUpdating = true;
    		if (logger.isLoggable(Level.FINE)) logger.fine("updateModelFromWidget() in TextAreaWidget");
    		setValue(_textArea.getText());
    		modelUpdating = false;
    		return true;
    	}
    	return false;
    }

	@Override
	public JTextArea getJComponent()
	{
		return _textArea;
	}

	@Override
	public JTextArea getDynamicJComponent()
	{
		return _textArea;
	}

	@Override
	public void updateFont()
	{
		super.updateFont();
		if (getFont() != null) _textArea.setFont(getFont());
	}
}
