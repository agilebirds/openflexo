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
package org.openflexo.components.tabularbrowser;

/*
 * %W% %E%
 *
 * Copyright 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer. 
 *   
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution. 
 *   
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.  
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,   
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.logging.FlexoLogger;

/**
 * This is a wrapper class takes a TreeTableModel and implements the table model interface. The implementation is trivial, with all of the
 * event dispatching support provided by the superclass: the AbstractTableModel.
 * 
 * @see http://java.sun.com/products/jfc/tsc/articles/treetable1/ Updated to fit Flexo architecture
 * 
 * @author Philip Milne
 * @author Scott Violet
 * @author Sylvain Guerin
 */
public class TreeTableModelAdapter extends AbstractModel<FlexoObject, FlexoObject> {

	private static final Logger logger = FlexoLogger.getLogger(TreeTableModelAdapter.class.getPackage().getName());
	private JTree tree;
	private TreeTableModel treeTableModel;

	public TreeTableModelAdapter(FlexoObject modelObject, FlexoProject project, TreeTableModel treeTableModel, JTree tree) {
		super(modelObject, project);
		this.tree = tree;
		this.treeTableModel = treeTableModel;

		tree.addTreeExpansionListener(new TreeExpansionListener() {
			// Don't use fireTableRowsInserted() here;
			// the selection model would get updated twice.
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				// fireTableDataChanged();
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				// fireTableDataChanged();
			}
		});
	}

	// Wrappers, implementing TableModel interface.

	@Override
	public int getColumnCount() {
		return treeTableModel.getColumnCount();
	}

	@Override
	public String getColumnName(int column) {
		return treeTableModel.getColumnName(column);
	}

	@Override
	public Class getColumnClass(int column) {
		return treeTableModel.getColumnClass(column);
	}

	@Override
	public int getRowCount() {
		if (tree != null) {
			return tree.getRowCount();
		} else {
			return 0;
		}
	}

	protected Object nodeForRow(int row) {
		TreePath treePath = tree.getPathForRow(row);
		return treePath.getLastPathComponent();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return treeTableModel.getValueAt(nodeForRow(row), column);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return treeTableModel.isCellEditable(nodeForRow(row), column);
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		treeTableModel.setValueAt(value, nodeForRow(row), column);
	}

	/**
	 * Overrides elementAt
	 * 
	 * @see org.openflexo.components.tabular.model.AbstractModel#elementAt(int)
	 */
	@Override
	public FlexoObject elementAt(int row) {
		Object o = nodeForRow(row);
		if (o == null) {
			return null;
		} else if (o instanceof BrowserElement) {
			return ((BrowserElement) o).getObject();
		} else if (o instanceof FlexoObject) {
			return (FlexoObject) o;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found object of type " + o.getClass().getName() + " while expecting BrowserElement");
			}
			return null;
		}

	}
}
