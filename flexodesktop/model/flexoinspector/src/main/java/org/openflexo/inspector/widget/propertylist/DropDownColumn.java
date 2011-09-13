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

import java.awt.Component;
import java.util.Enumeration;
import java.util.Hashtable;
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

import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListColumn;


public abstract class DropDownColumn extends AbstractColumn implements EditableColumn
{
	static final Logger logger = Logger.getLogger(PropertyListColumn.class.getPackage().getName());
	   
    private DropDownCellRenderer _cellRenderer;

    private DropDownCellEditor _cellEditor;

    public DropDownColumn(String title, int defaultWidth)
    {
        super(title, defaultWidth, true);
        _cellRenderer = new DropDownCellRenderer();
        _cellEditor = new DropDownCellEditor(new JComboBox());
    }

    @Override
	public Class getValueClass()
    {
        return Object.class;
    }

    @Override
	public Object getValueFor(InspectableObject object)
    {
        return getValue(object);
    }

    public abstract Object getValue(InspectableObject object);

    @Override
	public boolean isCellEditableFor(InspectableObject object)
    {
        return true;
    }

    @Override
	public void setValueFor(InspectableObject object, Object value)
    {
        setValue(object, value);
        notifyValueChangedFor(object);
   }
    
    public abstract void setValue(InspectableObject object, Object value);

    /**
     * @return
     */
    @Override
	public TableCellRenderer getCellRenderer()
    {
        return _cellRenderer;
    }

    protected class DropDownCellRenderer extends PropertyListCellRenderer
    {
        /**
         * 
         * Returns the selector cell renderer.
         * 
         * @param table
         *            the <code>JTable</code>
         * @param value
         *            the value to assign to the cell at
         *            <code>[row, column]</code>
         * @param isSelected
         *            true if cell is selected
         * @param hasFocus
         *            true if cell has focus
         * @param row
         *            the row of the cell to render
         * @param column
         *            the column of the cell to render
         * @return the default table cell renderer
         */
        @Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (returned instanceof JLabel) {
                ((JLabel) returned).setText(renderValue(value));
            }
            return returned;
        }
    }

    protected abstract String renderValue(Object value);

    protected abstract Vector getAvailableValues(InspectableObject object);

    /**
     * Must be overriden if required
     * 
     * @return
     */
    @Override
	public boolean requireCellEditor()
    {
        return true;
    }

    /**
     * Must be overriden if required
     * 
     * @return
     */
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
            _comboBoxModels = new Hashtable<Integer, DropDownComboBoxModel>();
            comboBox = aComboBox;
            comboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
                {
                    Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (returned instanceof JLabel) {
                        ((JLabel) returned).setText(renderValue(value));
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
            DropDownComboBoxModel _comboBoxModel = _comboBoxModels.get(row);
            if (_comboBoxModel == null) {
                _comboBoxModel = new DropDownComboBoxModel(elementAt(row));
                _comboBoxModels.put(row, _comboBoxModel);
            }
            _comboBoxModel.setSelectedItem(value);
            return _comboBoxModel;
        }

        protected class DropDownComboBoxModel extends DefaultComboBoxModel
        {

            /*protected DropDownComboBoxModel()
            {
                super();
                for (Enumeration en = getAvailableValues().elements(); en.hasMoreElements();) {
                    addElement(en.nextElement());
                }
            }*/

            protected DropDownComboBoxModel(InspectableObject element)
            {
                super();
                Vector v = getAvailableValues(element);
                if (v!=null)
                    for (Enumeration en = v.elements(); en.hasMoreElements();) {
                        addElement(en.nextElement());
                    }
                else
                    for (Enumeration en = getAvailableValues(element).elements(); en.hasMoreElements();) {
                        addElement(en.nextElement());
                    }
            }
        }
    }

    /*protected class DropDownCellEditor extends DefaultCellEditor
    {
        private DropDownComboBoxModel _comboBoxModel;

        private JComboBox comboBox;

        public DropDownCellEditor(JComboBox aComboBox)
        {
            super(aComboBox);
            comboBox = aComboBox;
            comboBox.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
                {
                	Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (returned instanceof JLabel) {
                        ((JLabel) returned).setText(renderValue((InspectableObject)value));
                    }
                    return returned;
                }
            });
         }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
        	logger.info("getTableCellEditorComponent() for "+value+" comboBox="+comboBox+" value="+value+" of "+value.getClass().getSimpleName());
            Component returned = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            comboBox.setModel(getComboBoxModel((InspectableObject) value));
            return returned;
        }

        private DropDownComboBoxModel getComboBoxModel(InspectableObject value)
        {
            if (_comboBoxModel == null) {
                _comboBoxModel = new DropDownComboBoxModel(value);
            }
            _comboBoxModel.updateAvailableValuesFromObject(value);
            _comboBoxModel.setSelectedItem(value);
            return _comboBoxModel;
        }

        protected class DropDownComboBoxModel extends DefaultComboBoxModel
        {
        	private Vector availableValues = new Vector();
        	
            protected DropDownComboBoxModel(InspectableObject value)
            {
                super();
                for (Enumeration en = getAvailableValues(value).elements(); en.hasMoreElements();) {
                    addElement(en.nextElement());
                }
            }
            
            protected void updateAvailableValuesFromObject(InspectableObject value)
            {
            	availableValues = getAvailableValues(value);
            }

			public Object getElementAt(int index) 
			{
				return availableValues.get(index);
			}

			public int getSize() 
			{
				return availableValues.size();
			}

        }
    }*/

    @Override
	public String toString()
    {
        return "DropDownColumn " + "@" + Integer.toHexString(hashCode());
    }
}
