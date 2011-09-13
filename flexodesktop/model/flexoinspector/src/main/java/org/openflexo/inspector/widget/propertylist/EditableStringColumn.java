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
package org.openflexo.inspector.widget.propertylist;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.inspector.InspectableObject;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class EditableStringColumn extends StringColumn implements EditableColumn
{
	
	private DefaultCellEditor editor;

    public EditableStringColumn(String title, int defaultWidth)
    {
        super(title, defaultWidth);
    }

    public EditableStringColumn(String title, int defaultWidth, boolean isResizable)
    {
        this(title, defaultWidth, isResizable, true);
    }

    public EditableStringColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle)
    {
        super(title, defaultWidth, isResizable, displayTitle);
    }

    public EditableStringColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle, String font)
    {
        super(title, defaultWidth, isResizable, displayTitle, font);
    }

    @Override
	public boolean isCellEditableFor(InspectableObject object)
    {
        return true;
    }
    
    @Override
    public boolean requireCellRenderer() {
    	return true;
    }

    @Override
    public TableCellRenderer getCellRenderer() {
    	return getDefaultTableCellRenderer();
    }
    
    @Override
    public boolean requireCellEditor() {
    	return true;
    }
    
    @Override
    public TableCellEditor getCellEditor() {
    	if(editor==null) {
    		editor = new DefaultCellEditor(new JTextField()) {
    			@Override
    			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    				final JTextField textfield = (JTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
    				textfield.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    				SwingUtilities.invokeLater(new Runnable(){
    					@Override
						public void run() {
    						textfield.selectAll();
    					}
    				});
    				return textfield;
    			}
    		};
    	}
    	return editor;
    }
    
    @Override
	public void setValueFor(InspectableObject object, Object value)
    {
        setValue(object, (String) value);
        notifyValueChangedFor(object);
   }

    public abstract void setValue(InspectableObject object, String aValue);

    @Override
	public String toString()
    {
        return "EditableStringColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
    }
}
