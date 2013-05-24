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
package org.openflexo.wse.view;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.WSPortTypeFolder;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.selection.SelectionManager;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.model.WSEOperationTableModel;
import org.openflexo.wse.model.WSEPortTypeTableModel;

/**
 * View allowing to represent/edit a DMModel object
 * 
 * @author sguerin
 * 
 */
public class WSEPortTypeFolderView extends WSEView<WSPortTypeFolder> {

	private static final Logger logger = Logger.getLogger(WSEPortTypeFolderView.class.getPackage().getName());

	private WSETabularView wsPortTypesTable;
	private WSEPortTypeTableModel wsPortTypesTableModel;

	private WSETabularView operationTable;
	private WSEOperationTableModel operationTableModel;

	public WSEPortTypeFolderView(WSPortTypeFolder model, WSEController controller) {
		super(model, controller, model.getName());

		/*    addAction(new TabularViewAction(CreateDMRepository.actionType,"add_repository") {
		        protected Vector getGlobalSelection()
		        {
		            return getViewSelection();
		        }

		        protected FlexoModelObject getFocusedObject() 
		        {
		            return getWSProcessFolder();
		        }           
		    });
		    addAction(new TabularViewAction(UpdateDMRepository.actionType) {
		        protected Vector getGlobalSelection()
		        {
		            return getViewSelection();
		        }

		        protected FlexoModelObject getFocusedObject() 
		        {
		            return getSelectedFlexoProcess();
		        }           
		    });
		   addAction(new TabularViewAction(DMDelete.actionType,"delete_repository") {
		        protected Vector getGlobalSelection()
		        {
		             return getViewSelection();
		        }

		        protected FlexoModelObject getFocusedObject() 
		        {
		            return null;
		        }           
		    });*/
		finalizeBuilding();
	}

	@Override
	protected JComponent buildContentPane() {

		WSService service = getWSPortTypeFolder().getWSService();
		boolean readOnly = false;
		if (service != null && service instanceof ExternalWSService) {
			readOnly = true;
		}
		wsPortTypesTableModel = new WSEPortTypeTableModel(service, getWSEController().getProject(), readOnly);
		wsPortTypesTable = new WSETabularView(getWSEController(), wsPortTypesTableModel, 10);
		addToMasterTabularView(wsPortTypesTable);

		operationTableModel = new WSEOperationTableModel(null, getWSEController().getProject(), readOnly);
		addToSlaveTabularView(operationTable = new WSETabularView(getWSEController(), operationTableModel, 10), wsPortTypesTable);

		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, wsPortTypesTable, operationTable);

	}

	public WSPortTypeFolder getWSPortTypeFolder() {
		return getModelObject();
	}

	public ServiceInterface getSelectedServiceInterface() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof ServiceInterface) {
			return (ServiceInterface) selection.firstElement();
		}
		if (getSelectedServiceOperation() != null) {
			return getSelectedServiceOperation().getServiceInterface();
		}

		return null;
	}

	public ServiceOperation getSelectedServiceOperation() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof ServiceOperation) {
			return (ServiceOperation) selection.firstElement();
		}
		return null;
	}

	public WSETabularView getWSPortTypesTable() {
		return wsPortTypesTable;
	}

	public WSETabularView getOperationsTable() {
		return operationTable;
	}

}
