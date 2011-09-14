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
package org.openflexo.fib.view.widget.table;

import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCheckBoxColumn;



/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class CheckBoxColumn extends AbstractColumn<Boolean> implements EditableColumn<Boolean>
{
    private static final Logger logger = Logger.getLogger(CheckBoxColumn.class.getPackage().getName());

	private DefaultCellEditor editor;

    public CheckBoxColumn(FIBCheckBoxColumn columnModel, FIBTableModel tableModel, FIBController controller)
    {
        super(columnModel,tableModel,controller);
    }

	@Override
	public FIBCheckBoxColumn getColumnModel()
	{
		return (FIBCheckBoxColumn)super.getColumnModel();
	}
	

    @Override
	public Class<Boolean> getValueClass()
    {
        return Boolean.class;
    }

    @Override
	public String toString()
    {
        return "BooleanColumn " + "@" + Integer.toHexString(hashCode());
    }
    
    @Override
	public boolean isCellEditableFor(Object object)
    {
        return true;
    }
    
    
    
   /* @Override
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
    		editor = new DefaultCellEditor(new JCheckBox()) {
    			@Override
    			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    				final JCheckBox checkBox = (JCheckBox)super.getTableCellEditorComponent(table, value, isSelected, row, column);
    				return checkBox;
    			}   			
     		};
    	}
    	return editor;
    }
    
*/
}
