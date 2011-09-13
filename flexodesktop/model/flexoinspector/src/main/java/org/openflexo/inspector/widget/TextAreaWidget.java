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

import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;


/**
 * Represents a widget able to edit a Text (more than one line) object
 * 
 * @author bmangez,sguerin
 */
public class TextAreaWidget extends DenaliWidget<String>
{

    private static final Logger logger = Logger.getLogger(TextAreaWidget.class.getPackage().getName());

    public static final String COLUMNS = "columns";
    public static final String ROWS = "rows";
    public static final String VALIDATE_ON_RETURN = "validateOnReturn";
    
    private static final int DEFAULT_COLUMNS = 10;
    private static final int DEFAULT_ROWS = 4;
    public static Font FIXED_SIZE_FONT = new Font("Monospaced", Font.PLAIN, 11);
    public static Font SANS_SERIF_FONT = new Font("SansSerif", Font.PLAIN, 11);

    JTextArea _textArea;
    private JScrollPane pane;
    boolean validateOnReturn;

    public TextAreaWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        _textArea = new JTextArea();
        
        if (model.hasValueForParameter(VALIDATE_ON_RETURN)) {
            validateOnReturn = model.getBooleanValueForParameter(VALIDATE_ON_RETURN);
        } else {
            validateOnReturn = true;
        }
        
        if (model.hasValueForParameter("columns")) {
            _textArea.setColumns(model.getIntValueForParameter("columns"));
        }
        else {
            _textArea.setColumns(DEFAULT_COLUMNS);            
        }
        
        if (model.hasValueForParameter("rows")) {
            _textArea.setRows(model.getIntValueForParameter("rows"));
        }
        else {
            _textArea.setRows(DEFAULT_ROWS);            
        }
        
        if (model.hasValueForParameter("font")) {
            if (model.getValueForParameter("font").equals("SansSerif")) {
                _textArea.setFont(SANS_SERIF_FONT);
            }
            else if (model.getValueForParameter("font").equals("FixedSize")) {
                 _textArea.setFont(FIXED_SIZE_FONT);
            }   
        }
        
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
         _textArea.addFocusListener(new WidgetFocusListener(this) {
            @Override
			public void focusGained(FocusEvent focusEvent)
            {
                super.focusGained(focusEvent);
                _textArea.selectAll();
            }
        });


        _textArea.setAutoscrolls(true);
        _textArea.setLineWrap(true);
        _textArea.setWrapStyleWord(true);
        _textArea.setEnabled(true);
        pane = new JScrollPane(_textArea);
        
        /*{
          public Dimension getPreferredSize()
            {
                Dimension d = _textArea.getPreferredScrollableViewportSize();
                d.height += (getInsets().bottom + getInsets().top + _textArea.getInsets().top + _textArea.getInsets().bottom);
                return d;
            }
          };  */

        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getDynamicComponent().addFocusListener(new WidgetFocusListener(this));
        pane.setMinimumSize(MINIMUM_SIZE);
    }

    @Override
	public Class getDefaultType()
    {
        return String.class;
    }
    
    @Override
    protected synchronized void setModel(InspectableObject value) {
    	super.setModel(value);
    	_textArea.setEditable(!isReadOnly());
    }

    @Override
	public synchronized void updateWidgetFromModel()
    {
     	if (modelUpdating) return;
       	if (_textArea.getText().equals(getStringValue())) return;
       	if (getStringValue() != null && (getStringValue()+"\n").equals(_textArea.getText())) return;
      	//logger.info("BEGIN TextAreaWidget: updateWidgetFromModel()");
      	widgetUpdating = true;
        _textArea.setText(getStringValue());
        widgetUpdating = false;
      	//logger.info("END TextAreaWidget: updateWidgetFromModel()");
       
    }
    
    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
       	//logger.info("BEGIN TextAreaWidget: updateModelFromWidget() value is "+_textArea.getText());
       	modelUpdating = true;
        if (logger.isLoggable(Level.FINE)) logger.fine("updateModelFromWidget() in TextAreaWidget");
        setStringValue(_textArea.getText());
        modelUpdating = false;
       	//logger.info("END TextAreaWidget: updateModelFromWidget() value is "+_textArea.getText());
    }
    
    @Override
	public JComponent getDynamicComponent()
    {
        return pane;
    }

    @Override
	public boolean defaultShouldExpandVertically()
    {
    	return true;
    }
 

}
