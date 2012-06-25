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
import org.openflexo.foundation.wkf.ws.MessageEntry;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.selection.SelectionManager;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.model.WSEMessageEntryTableModel;
import org.openflexo.wse.model.WSEMessageTableModel;
import org.openflexo.wse.model.WSEOperationTableModel;

/**
 * View allowing to represent/edit a DMModel object
 * 
 * @author sguerin
 * 
 */
public class WSEPortTypeView extends WSEView<ServiceInterface> {

	private static final Logger logger = Logger.getLogger(WSEPortTypeView.class.getPackage().getName());

	private WSETabularView entryTable;
	private WSEMessageEntryTableModel entryTableModel;

	private WSETabularView messageTable;
	private WSEMessageTableModel messageTableModel;

	private WSETabularView operationTable;
	private WSEOperationTableModel operationTableModel;

	public WSEPortTypeView(ServiceInterface model, WSEController controller) {
		super(model, controller, "ws_portType_($name)");

		finalizeBuilding();
	}

	@Override
	protected JComponent buildContentPane() {
		WSService service = getServiceInterface().getParentService();
		boolean readOnly = false;
		if (service != null && service instanceof ExternalWSService) {
			readOnly = true;
		}
		operationTableModel = new WSEOperationTableModel(getServiceInterface(), getWSEController().getProject(), readOnly);
		operationTable = new WSETabularView(getWSEController(), operationTableModel, 7);
		addToMasterTabularView(operationTable);

		messageTableModel = new WSEMessageTableModel(null, getWSEController().getProject(), readOnly);
		messageTable = new WSETabularView(getWSEController(), messageTableModel, 3);

		entryTableModel = new WSEMessageEntryTableModel(null, getWSEController().getProject(), readOnly);
		entryTable = new WSETabularView(getWSEController(), entryTableModel, 7);

		addToSlaveTabularView(messageTable, operationTable);
		addToSlaveTabularView(entryTable, messageTable);
		// / return new JSplitPane(JSplitPane.VERTICAL_SPLIT, groupTable, new
		// JSplitPane(JSplitPane.VERTICAL_SPLIT,processTable,repositoryTable));

		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, operationTable,
				new JSplitPane(JSplitPane.VERTICAL_SPLIT, messageTable, entryTable));

	}

	public ServiceInterface getServiceInterface() {
		return getModelObject();
	}

	public ServiceOperation getSelectedServiceOperation() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof ServiceOperation) {
			return (ServiceOperation) selection.firstElement();
		}
		if (getSelectedMessageDefinition() != null) {
			return getSelectedMessageDefinition().getOperation();
		}
		if (getSelectedMessageEntry() != null) {
			return ((ServiceMessageDefinition) getSelectedMessageEntry().getMessage()).getOperation();
		}

		return null;
	}

	public MessageEntry getSelectedMessageEntry() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof MessageEntry) {
			return (MessageEntry) selection.firstElement();
		}

		return null;
	}

	public ServiceMessageDefinition getSelectedMessageDefinition() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof ServiceMessageDefinition) {
			return (ServiceMessageDefinition) selection.firstElement();
		}
		if (getSelectedMessageEntry() != null) {
			return (ServiceMessageDefinition) getSelectedMessageEntry().getMessage();
		}
		return null;
	}

	public WSETabularView getMessageTable() {
		return messageTable;
	}

	public WSETabularView getPortTable() {
		return operationTable;
	}

}
