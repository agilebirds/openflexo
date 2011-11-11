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

import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.ExternalWSFolder;
import org.openflexo.foundation.ws.WSRepository;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.foundation.ws.action.CreateNewWebService;
import org.openflexo.foundation.ws.action.WSDelete;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.controller.WSESelectionManager;
import org.openflexo.wse.model.WSEPortTypeTableModel;
import org.openflexo.wse.model.WSERepositoryTableModel;
import org.openflexo.wse.model.WSEServiceTableModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEExternalWSFolderView extends WSEView<ExternalWSFolder> {

	private static final Logger logger = Logger.getLogger(WSEExternalWSFolderView.class.getPackage().getName());

	private WSEServiceTableModel serviceTableModel;
	protected WSETabularView serviceTable;

	private WSEPortTypeTableModel portTypeTableModel;
	protected WSETabularView portTypeTable;

	private WSERepositoryTableModel repositoryTableModel;
	protected WSETabularView repositoryTable;

	public WSEExternalWSFolderView(ExternalWSFolder folder, WSEController controller) {
		super(folder, controller, folder.getLocalizedName());
		addAction(new TabularViewAction(CreateNewWebService.actionType, "ws_add_webservice", controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return getWSFolder();
			}
		});
		/*    addAction(new TabularViewAction(UpdateDMRepository.actionType) {
		        protected Vector getGlobalSelection()
		        {
		            return getViewSelection();
		        }

		        protected FlexoModelObject getFocusedObject() 
		        {
		            return getSelectedWSFolder();
		        }           
		    });*/
		addAction(new TabularViewAction(WSDelete.actionType, "delete_webservice", controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return null;
			}
		});
		finalizeBuilding();
	}

	@Override
	protected JComponent buildContentPane() {

		serviceTableModel = new WSEServiceTableModel(getWSFolder(), getWSEController().getProject(), true);
		addToMasterTabularView(serviceTable = new WSETabularView(getWSEController(), serviceTableModel, 5));

		portTypeTableModel = new WSEPortTypeTableModel(null, getWSEController().getProject(), true);
		addToSlaveTabularView(portTypeTable = new WSETabularView(getWSEController(), portTypeTableModel, 6), serviceTable);

		repositoryTableModel = new WSERepositoryTableModel(null, getWSEController().getProject());
		addToSlaveTabularView(repositoryTable = new WSETabularView(getWSEController(), repositoryTableModel, 6), serviceTable);

		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, serviceTable, new JSplitPane(JSplitPane.VERTICAL_SPLIT, portTypeTable,
				repositoryTable));
	}

	public ExternalWSFolder getWSFolder() {
		return getModelObject();
	}

	public WSService getSelectedWSService() {
		WSESelectionManager sm = getWSEController().getWSESelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof WSService)) {
			return (WSService) selection.firstElement();
		}
		if (getSelectedServiceInterface() != null) {
			WSService ws = getWSFolder().getParentOfServiceInterface(getSelectedServiceInterface());
			if (ws != null)
				return ws;
		}
		if (getSelectedWSDLRepository() != null) {
			WSRepository wsr = getWSFolder().getWSRepositoryNamed(getSelectedWSDLRepository().getName());
			if (wsr != null)
				return wsr.getWSService();
		}

		return null;
	}

	public ServiceInterface getSelectedServiceInterface() {
		WSESelectionManager sm = getWSEController().getWSESelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof ServiceInterface)) {
			return (ServiceInterface) selection.firstElement();
		}
		return null;
	}

	public WSDLRepository getSelectedWSDLRepository() {
		WSESelectionManager sm = getWSEController().getWSESelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof WSDLRepository)) {
			return (WSDLRepository) selection.firstElement();
		}
		return null;
	}

	public WSETabularView getServiceTable() {
		return serviceTable;
	}

	public WSETabularView getPortTypeTable() {
		return portTypeTable;
	}

	public WSETabularView getRepositoryTable() {
		return repositoryTable;
	}

}
