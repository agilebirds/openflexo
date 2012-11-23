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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBButtonColumn;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ButtonColumn<T extends Object> extends AbstractColumn<T> implements EditableColumn<T> {
	private static final Logger logger = Logger.getLogger(ButtonColumn.class.getPackage().getName());

	private org.openflexo.swing.ButtonColumn buttonTableColumn;

	public ButtonColumn(FIBButtonColumn columnModel, FIBTableModel tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
		buttonTableColumn = new org.openflexo.swing.ButtonColumn(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getColumnModel().getAction().isSet() && getColumnModel().getAction().isValid()) {
					iteratorObject = elementAt(buttonTableColumn.getClickedRow());
					try {
						getColumnModel().getAction().execute(ButtonColumn.this);
					} catch (TypeMismatchException e1) {
						e1.printStackTrace();
					} catch (NullReferenceException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public FIBButtonColumn getColumnModel() {
		return (FIBButtonColumn) super.getColumnModel();
	}

	@Override
	public Class<T> getValueClass() {
		return TypeUtils.getBaseClass(getColumnModel().getDataClass());
	}

	@Override
	public boolean isCellEditableFor(Object object) {
		return true;
	}

	@Override
	public String toString() {
		return "CustomColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
	}

	// Returns true as cell renderer is required here
	@Override
	public boolean requireCellRenderer() {
		return true;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return buttonTableColumn;
	}

	// Returns true as cell editor is required here
	@Override
	public boolean requireCellEditor() {
		return true;
	}

	// Must be overriden if required
	@Override
	public TableCellEditor getCellEditor() {
		return buttonTableColumn;
	}

}
