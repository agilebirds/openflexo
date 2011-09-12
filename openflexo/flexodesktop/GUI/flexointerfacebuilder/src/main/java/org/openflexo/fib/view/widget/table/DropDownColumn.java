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

import java.awt.Component;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBDropDownColumn;


public class DropDownColumn<T extends Object> extends AbstractColumn<T> implements EditableColumn<T>
{
	static final Logger logger = Logger.getLogger(DropDownColumn.class.getPackage().getName());
	   
    private DropDownCellRenderer _cellRenderer;

    private DropDownCellEditor _cellEditor;

    public DropDownColumn(FIBDropDownColumn columnModel, FIBTableModel tableModel, FIBController controller)
    {
        super(columnModel,tableModel,controller);
        _cellRenderer = new DropDownCellRenderer();
        _cellEditor = new DropDownCellEditor(new JComboBox());
     }

    @Override
    public FIBDropDownColumn getColumnModel()
    {
     	return (FIBDropDownColumn)super.getColumnModel();
    }
    
    @Override
	public Class getValueClass()
    {
        return Object.class;
    }

    /**
     * Must be overriden if required
     * 
     * @return
     */
    @Override
	public boolean requireCellRenderer()
    {
        return true;
    }

    @Override
	public TableCellRenderer getCellRenderer()
    {
        return _cellRenderer;
    }

    @Override
	public boolean isCellEditableFor(Object object)
    {
        return true;
    }

    protected class DropDownCellRenderer extends FIBTableCellRenderer<T>
    {
    	public DropDownCellRenderer() {
			super(DropDownColumn.this);
		}
    	
        @Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (returned instanceof JLabel) {
                ((JLabel) returned).setText(renderValue((T)value));
                ((JLabel) returned).setFont(getFont());
            }
            return returned;
        }
    }

    
    protected String renderValue(T value)
    {
     	return getStringRepresentation(value);
    }

    protected List getAvailableValues(Object object)
    {
    	
		if (getColumnModel().staticList !=null) {
			Vector<String> list = new Vector<String>();
			StringTokenizer st = new StringTokenizer(getColumnModel().staticList,",");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
			return list;
		}

		else if (getColumnModel().getList() !=null
				&& getColumnModel().getList().isSet()) {
			
	    	iteratorObject = object;
			Object accessedList = getColumnModel().getList().getBindingValue(this);

			if (accessedList instanceof List) return (List)accessedList;
		}
		
		else if (getColumnModel().getArray() !=null
				&& getColumnModel().getArray().isSet()) {
			
	    	iteratorObject = object;
			Object accessedArray = getColumnModel().getArray().getBindingValue(getController());
			try {
				Object[] array = (Object[])accessedArray;
				Vector<Object> list = new Vector<Object>();
				for (int i=0; i<array.length; i++) {
					list.add(array[i]);
				}
				return list;
			}
			catch(ClassCastException e) {
				logger.warning("ClassCastException "+e.getMessage());
			}
		}
		
		else if (getColumnModel().getData() != null 
				&& getColumnModel().getData().getBinding() != null) {
			Type type = getColumnModel().getData().getBinding().getAccessedType();
			if (type instanceof Class && ((Class)type).isEnum()) {
				Object[] array = ((Class)type).getEnumConstants();
				Vector<Object> list = new Vector<Object>();
				for (int i=0; i<array.length; i++) {
					list.add(array[i]);
				}
				return list;
			}
		}
		logger.warning("Could not access element list");
		return null;
    }

    @Override
	public boolean requireCellEditor()
    {
        return true;
    }

    @Override
	public TableCellEditor getCellEditor()
    {
        return _cellEditor;
    }

    protected class DropDownCellEditor extends DefaultCellEditor
    {
        private Hashtable<Integer, DropDownComboBoxModel> _comboBoxModels;

        private JComboBox comboBox;

        public DropDownCellEditor(JComboBox aComboBox)
        {
            super(aComboBox);
            aComboBox.setFont(getFont());
            _comboBoxModels = new Hashtable<Integer, DropDownComboBoxModel>();
            comboBox = aComboBox;
            comboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
                {
                    Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (returned instanceof JLabel) {
                        ((JLabel) returned).setText(renderValue((T)value));
                        ((JLabel) returned).setFont(getFont());
                    }
                    return returned;
                }
            });
        }

        @Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            Component returned = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            comboBox.setModel(getComboBoxModel(value,row,column));
            return returned;
        }

        protected DropDownComboBoxModel getComboBoxModel(Object value, int row, int column)
        {
			// Don't use cache as it is never refreshed
			// DropDownComboBoxModel _comboBoxModel = _comboBoxModels.get(row);
			// if (_comboBoxModel == null) {
			DropDownComboBoxModel _comboBoxModel = new DropDownComboBoxModel(elementAt(row));
			// _comboBoxModels.put(row, _comboBoxModel);
			// }
            _comboBoxModel.setSelectedItem(value);
            return _comboBoxModel;
        }

        protected class DropDownComboBoxModel extends DefaultComboBoxModel
        {

        	protected DropDownComboBoxModel(Object element)
        	{
        		super();
        		List v = getAvailableValues(element);
        		if (v!=null)
        			for (Iterator it = v.iterator(); it.hasNext();) {
        				Object next = it.next();
        				addElement(next);
        			}
        	}
        }
    }
 
    @Override
	public String toString()
    {
        return "DropDownColumn " + "@" + Integer.toHexString(hashCode());
    }
}
