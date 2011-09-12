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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;


/**
 * Represents a widget able to edit a Text (more than one line) object in read only mode
 * 
 * @author bmangez,sguerin
 */
public class ReadOnlyTextAreaWidget extends DenaliWidget<String>
{

    private static final int DEFAULT_COLUMNS = 10;
    private static final int DEFAULT_ROWS = 4;
    public static Font FIXED_SIZE_FONT = new Font("Monospaced", Font.PLAIN, 11);
    public static Font SANS_SERIF_FONT = new Font("SansSerif", Font.PLAIN, 11);

    JTextArea _textArea;
    JScrollPane pane;

    public ReadOnlyTextAreaWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        _textArea = new JTextArea();
        
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
        
        _textArea.setAutoscrolls(true);
        _textArea.setLineWrap(true);
        _textArea.setWrapStyleWord(true);
        _textArea.setBorder(BorderFactory.createLoweredBevelBorder());
        _textArea.setEnabled(true);
        pane = new JScrollPane(_textArea);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getDynamicComponent().addFocusListener(new WidgetFocusListener(this));
        _textArea.setEditable(false);
    }

    @Override
	public Class getDefaultType()
    {
        return String.class;
    }

    @Override
	public synchronized void updateWidgetFromModel()
    {
        widgetUpdating = true;
        _textArea.setText(getStringValue());
        widgetUpdating = false;
        
    }
    
    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
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
