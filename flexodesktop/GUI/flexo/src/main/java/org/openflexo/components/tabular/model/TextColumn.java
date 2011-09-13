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
package org.openflexo.components.tabular.model;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.foundation.FlexoModelObject;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class TextColumn<D extends FlexoModelObject> extends AbstractColumn<D,String> implements EditableColumn<D,String>, HeightAdjustableColumn
{

    public TextColumn(String title, int defaultWidth)
    {
        this(title, defaultWidth, true);
    }

    public TextColumn(String title, int defaultWidth, boolean isResizable)
    {
        this(title, defaultWidth, isResizable, true);
    }

    public TextColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle)
    {
        super(title, defaultWidth, isResizable, displayTitle);
        _textCellEditor = new TextCellEditor();
        _textCellRenderer = new TextCellRenderer();
        _taForRow = new Vector();
    }

    @Override
	public Class getValueClass()
    {
        return String.class;
    }

    @Override
	public String getValueFor(D object)
    {
        return getValue(object);
    }

    public abstract String getValue(D object);

    @Override
	public boolean isCellEditableFor(D object)
    {
        return true;
    }

    @Override
	public void setValueFor(D object, String value)
    {
        setValue(object, value);
    }

    public abstract void setValue(D object, String aValue);

    @Override
	public String toString()
    {
        return "TextColumn " + "@" + Integer.toHexString(hashCode());
    }
    
    /**
     * @return
     */
    @Override
	public TableCellRenderer getCellRenderer()
    {
        return _textCellRenderer;
    }

    private TextCellRenderer _textCellRenderer;

    protected class TextCellRenderer extends TabularViewCellRenderer
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
            TextColumnEditor returned = getEditorForRow(row);
            setComponentBackground(returned.getTextArea(), hasFocus, isSelected, row, column);
            returned.setText((String)value);
            returned.setEditable(false);
            return returned.getTextArea();
        }
    }

    private Vector _taForRow;
    
    protected TextColumnEditor getEditorForRow(int row)
    {
        if (_taForRow.size() <= row) {
            for (int i=_taForRow.size(); i<=row; i++) {
                TextColumnEditor newEditor = new TextColumnEditor(i);
                _taForRow.add(newEditor);
            }
        }
        return (TextColumnEditor)_taForRow.elementAt(row);
    }

    protected class TextColumnEditor
    {
        protected JTextArea _textArea;
        private int _row;
        private TextColumnDocumentListener _docListener;
        protected int knownLC = 0;
        
        protected TextColumnEditor(int row)
        {
            _row = row;
            _textArea = new JTextArea();
            _textArea.setRows(1);
            _textArea.setLineWrap(true);
            _textArea.setWrapStyleWord(true);
            _docListener = new TextColumnDocumentListener(_textArea,row);
            _textArea.getDocument().addDocumentListener(_docListener);           
        }
        
        public void setEditable(boolean b) 
        {
            _textArea.setEditable(b);
        }

        public void setText(String aText) 
        {
            _textArea.getDocument().removeDocumentListener(_docListener);           
            _textArea.setText(aText);
            _textArea.getDocument().addDocumentListener(_docListener);   
            if (knownLC != _textArea.getLineCount()) {
                knownLC = _textArea.getLineCount();
                 fireRowHeightChanged(_row);
            }
            knownLC = _textArea.getLineCount();
        }

        protected class TextColumnDocumentListener implements DocumentListener
        {
            private int _row;
            private JTextArea _textArea;
            
            protected TextColumnDocumentListener(JTextArea textArea, int row)
            {
                super();
                _row = row;
                _textArea = textArea;
            }

            @Override
			public void insertUpdate(DocumentEvent e) 
            {
                textChanged(e);
            }

            @Override
			public void removeUpdate(DocumentEvent e) 
            {
                textChanged(e);
            }

            @Override
			public void changedUpdate(DocumentEvent e) 
            {
                textChanged(e);
            }
            
            private void textChanged(DocumentEvent e) 
            {
                //logger.fine("Text changed for row "+_row+" lc="+_textArea.getLineCount()+" row="+_textArea.getRows()+" height="+_textArea.getHeight());
                if (knownLC != _textArea.getLineCount()) {
                    knownLC = _textArea.getLineCount();
                     fireRowHeightChanged(_row);
                }
            }
        }

        public JTextArea getTextArea() 
        {
            return _textArea;
        }

        public int getDesiredSize() 
        {
           return 16*_textArea.getLineCount();
        }

    }
    
     
    
    @Override
	public boolean requireCellEditor()
    {
        return true;
    }

    @Override
	public TableCellEditor getCellEditor()
    {
        return _textCellEditor;
    }

    private TextCellEditor _textCellEditor;

    protected class TextCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
    {
        //protected Hashtable _taForObject;
       protected JTextArea _currentTA;

        public TextCellEditor()
        {
        }

        @Override
		public void actionPerformed(ActionEvent e)
        {
            fireEditingStopped();
        }

        @Override
		protected void fireEditingStopped() 
        {
            super.fireEditingStopped();
        }
        
       @Override
	public Object getCellEditorValue()
        {
            return _currentTA.getText();
        }
        
         @Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
             TextColumnEditor returned = getEditorForRow(row);
             returned.setText((String)value);
             returned.setEditable(true);
             _currentTA = returned.getTextArea();
            return returned.getTextArea();
        }
    }

    private Vector _rowHeightListeners = new Vector();
    
   @Override
public void addRowHeightListener(RowHeightListener rhl)
   {
       _rowHeightListeners.add(rhl);
   }
    
    @Override
	public void removeRowHeightListener(RowHeightListener rhl)
    {
        _rowHeightListeners.remove(rhl);
    }

    @Override
	public void fireRowHeightChanged(int row)
    {
        //logger.fine("On change de nb de lignes !!!!! row="+row+" desired size is now "+getRowHeight(row));
        if (getRowHeight(row) > 0) {
            for (Enumeration en=_rowHeightListeners.elements(); en.hasMoreElements();) {
                RowHeightListener next = (RowHeightListener)en.nextElement();
                next.notifyRowHeightChanged(row,getRowHeight(row));
            }
        }
    }
    
    @Override
	public int getRowHeight(int row)
    {
        return getEditorForRow(row).getDesiredSize();
    }

}
