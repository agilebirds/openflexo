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
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.WSPortTypeFolder;
import org.openflexo.foundation.ws.WSRepositoryFolder;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.selection.SelectionManager;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.model.WSEPortTypeTableModel;
import org.openflexo.wse.model.WSERepositoryTableModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEServiceView extends WSEView<WSService> {

	private static final Logger logger = Logger.getLogger(WSEServiceView.class.getPackage().getName());

	private WSEPortTypeTableModel portTypeTableModel;
	protected WSETabularView portTypeTable;

	private WSERepositoryTableModel repositoryTableModel;
	protected WSETabularView repositoryTable;

	public WSEServiceView(WSService group, WSEController controller) {
		super(group, controller, "ws_service_($name)");

		finalizeBuilding();
	}

	@Override
	protected JComponent buildContentPane() {

		WSService service = getWSService();
		boolean readOnly = false;
		if (service != null && service instanceof ExternalWSService) {
			readOnly = true;
		}
		portTypeTableModel = new WSEPortTypeTableModel(getWSService(), getWSEController().getProject(), readOnly);
		addToMasterTabularView(portTypeTable = new WSETabularView(getWSEController(), portTypeTableModel, 10));

		repositoryTableModel = new WSERepositoryTableModel(getWSService(), getWSEController().getProject());
		addToMasterTabularView(repositoryTable = new WSETabularView(getWSEController(), repositoryTableModel, 10));

		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, portTypeTable, repositoryTable);
	}

	public WSService getWSService() {
		return getModelObject();
	}

	public WSPortTypeFolder getWSPortTypeFolder() {
		return getWSService().getWSPortTypeFolder();
	}

	public WSRepositoryFolder getWSRepositoryFolder() {
		return getWSService().getWSRepositoryFolder();
	}

	public ServiceInterface getSelectedServiceInterface() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof ServiceInterface) {
			return (ServiceInterface) selection.firstElement();
		}
		return null;
	}

	public WSDLRepository getSelectedWSDLRepository() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof WSDLRepository) {
			return (WSDLRepository) selection.firstElement();
		}
		return null;
	}

	public WSETabularView getPortTypeTable() {
		return portTypeTable;
	}

	public WSETabularView getRepositoryTable() {
		return repositoryTable;
	}

}
