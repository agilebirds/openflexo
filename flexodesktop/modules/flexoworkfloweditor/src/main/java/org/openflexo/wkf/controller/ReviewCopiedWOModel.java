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
package org.openflexo.wkf.controller;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.openflexo.foundation.rm.FlexoOperationComponentResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.NodeCompound;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a model of OperationNode with a WOComponent attached to, and
 * defines how those WOComponents would be copied.
 * 
 * @author sguerin
 */
public class ReviewCopiedWOModel extends AbstractTableModel
{

    private static final Logger logger = Logger.getLogger(ReviewCopiedWOModel.class.getPackage().getName());

    private Vector _operationNodesContainingAWO;

    private Vector _copiedWOList;

    public class CopiedWO
    {
        public OperationNode operationNode;

        public String oldName;

        public String newName;

        public Boolean isToBeCopied;

        public CopiedWO(FlexoProcess process, OperationNode node)
        {
            operationNode = node;
            isToBeCopied = new Boolean(true);

            String componentName = node.getWOComponentName();
            oldName = componentName;
            String resourceIdentifier = FlexoOperationComponentResource.resourceIdentifierForName(componentName);
            if ((process != null) && (process.getProject() != null)) {
                int j = 0;
                String tryMe = resourceIdentifier;
                while (process.getProject().isRegistered(tryMe)) {
                    j++;
                    tryMe = resourceIdentifier + "-" + j;
                }
                if (j == 0) {
                    newName = componentName;
                } else {
                    newName = componentName + "-" + j;
                }
            } else {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Could not access to project, process or project is null !");
            }
        }
    }

    public ReviewCopiedWOModel(NodeCompound compound)
    {
        super();
        _operationNodesContainingAWO = compound.getAllOperationNodesContainingAWO();
        _copiedWOList = new Vector();
        for (int i = 0; i < _operationNodesContainingAWO.size(); i++) {
            _copiedWOList.add(new CopiedWO(compound.getProcess(), (OperationNode) _operationNodesContainingAWO.get(i)));
        }
    }

    @Override
	public int getRowCount()
    {
        if (_copiedWOList == null) {
            return 0;
        }
        return _copiedWOList.size();
    }

    @Override
	public int getColumnCount()
    {
        return 4;
    }

    @Override
	public String getColumnName(int columnIndex)
    {
        if (columnIndex == 0) {
            return " ";
        } else if (columnIndex == 1) {
            return FlexoLocalization.localizedForKey("node_name");
        } else if (columnIndex == 2) {
            return FlexoLocalization.localizedForKey("initial_wo_name");
        } else if (columnIndex == 3) {
            return FlexoLocalization.localizedForKey("new_wo_name");
        }
        return "???";
    }

    @Override
	public Class getColumnClass(int columnIndex)
    {
        if (columnIndex == 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return (((columnIndex == 0) || (columnIndex == 3)) ? true : false);
    }

    public CopiedWO elementAt(int rowIndex)
    {
        return (CopiedWO) _copiedWOList.elementAt(rowIndex);
    }

    @Override
	public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (_operationNodesContainingAWO == null) {
            return null;
        }
        if (columnIndex == 0) {
            return elementAt(rowIndex).isToBeCopied;
        } else if (columnIndex == 1) {
            return elementAt(rowIndex).operationNode.getName();
        } else if (columnIndex == 2) {
            return elementAt(rowIndex).oldName;
        } else if (columnIndex == 3) {
            return elementAt(rowIndex).newName;
        }
        return null;
    }

    @Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
        if (columnIndex == 0) {
            ((CopiedWO) _copiedWOList.get(rowIndex)).isToBeCopied = (Boolean) value;
        } else if (columnIndex == 3) {
            ((CopiedWO) _copiedWOList.get(rowIndex)).newName = (String) value;
        }
    }

    public void selectAll()
    {
        for (int i = 0; i < _copiedWOList.size(); i++) {
            ((CopiedWO) _copiedWOList.get(i)).isToBeCopied = new Boolean(true);
            fireTableCellUpdated(i, 0);
        }
    }

    public void deselectAll()
    {
        for (int i = 0; i < _copiedWOList.size(); i++) {
            ((CopiedWO) _copiedWOList.get(i)).isToBeCopied = new Boolean(false);
            fireTableCellUpdated(i, 0);
        }
    }
}
