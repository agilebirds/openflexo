/**
 * 
 */
package org.openflexo.fib.view.widget.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.view.widget.FIBTableWidget;

class FIBTableCellRenderer<T> extends DefaultTableCellRenderer {

	private final AbstractColumn<T> column;

	public FIBTableCellRenderer(AbstractColumn<T> aColumn) {
		super();
		column = aColumn;
		setFont(column.getColumnModel().retrieveValidFont());
	}

	public FIBTableModel getTableModel() {
		return column.getTableModel();
	}

	public FIBTableWidget getTableWidget() {
		return column.getTableModel().getTableWidget();
	}

	public FIBTable getTable() {
		return getTableModel().getTable();
	}

	/**
	 * 
	 * Returns the cell renderer.
	 * 
	 * @param table
	 *            the <code>JTable</code>
	 * @param value
	 *            the value to assign to the cell at <code>[row, column]</code>
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
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (returned instanceof JComponent) {
			((JComponent) returned).setToolTipText(this.column.getTooltip(getTableModel().elementAt(row)));
			((JComponent) returned).setFont(this.column.getColumnModel().retrieveValidFont());
		}

		if (returned instanceof JLabel) {
			((JLabel) returned).setText(this.column.getStringRepresentation(value));
		}

		if (isSelected) {
			if (getTableWidget().isLastFocusedSelectable()) {
				if (getTable().getTextSelectionColor() != null) {
					setForeground(getTable().getTextSelectionColor());
				}
				if (getTable().getBackgroundSelectionColor() != null) {
					setBackground(getTable().getBackgroundSelectionColor());
				}
			} else {
				if (getTable().getTextNonSelectionColor() != null) {
					setForeground(getTable().getTextNonSelectionColor());
				}
				if (getTable().getBackgroundSecondarySelectionColor() != null) {
					setBackground(getTable().getBackgroundSecondarySelectionColor());
				}
			}
		} else {
			if (getTableWidget().isEnabled()) {
				if (getTable().getTextNonSelectionColor() != null) {
					setForeground(getTable().getTextNonSelectionColor());
				}
			}
			if (getTable().getBackgroundNonSelectionColor() != null) {
				setBackground(getTable().getBackgroundNonSelectionColor());
			}
		}

		Color specificColor = this.column.getSpecificColor(getTableModel().elementAt(row));
		if (specificColor != null) {
			setForeground(specificColor);
		}

		Color specificBgColor = this.column.getSpecificBgColor(getTableModel().elementAt(row));
		if (specificBgColor != null) {
			setBackground(specificBgColor);
		}

		return returned;
	}

}