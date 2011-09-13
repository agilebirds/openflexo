/**
 * 
 */
package org.openflexo.fib.view.widget.table;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.view.widget.FIBTableWidget;

class FIBTableCellRenderer<T> extends DefaultTableCellRenderer
    {


	private final AbstractColumn<T> column;
 
		public FIBTableCellRenderer(AbstractColumn<T> aColumn)
		{
			super();
			column = aColumn;
 			setFont(column.getColumnModel().retrieveValidFont());
		}
    	
		public FIBTableModel getTableModel()
		{
			return column.getTableModel();
		}
		
		public FIBTableWidget getTableWidget()
		{
			return column.getTableWidget();
		}
		
		public FIBTable getTable()
		{
			return getTableModel().getTable();
		}
		
        /**
         * 
         * Returns the cell renderer.
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

            if (returned instanceof JComponent) {
            	((JComponent)returned).setToolTipText(this.column.getTooltip(getTableModel().elementAt(row)));
            	((JComponent)returned).setFont(this.column.getColumnModel().retrieveValidFont());
           }

            if (isSelected) {
            	if (getTableWidget().isLastFocusedSelectable()) {
            		setForeground(getTable().getTextSelectionColor());
            		setBackground(getTable().getBackgroundSelectionColor());
            	}
            	else {
            		setForeground(getTable().getTextNonSelectionColor());
            		setBackground(getTable().getBackgroundSecondarySelectionColor());
            	}
            }
            else {
                if (!getTableWidget().isEnabled()) {
                	setForeground(FIBComponent.DISABLED_COLOR);
                }
                else {
                	setForeground(getTable().getTextNonSelectionColor());
                }
        		setBackground(getTable().getBackgroundNonSelectionColor());
            }

 

            return returned;
        }

   }