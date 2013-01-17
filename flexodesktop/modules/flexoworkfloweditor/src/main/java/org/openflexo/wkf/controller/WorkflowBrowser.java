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

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.module.UserType;

/**
 * Browser for WKF module, browse all processes without details
 * 
 * @author sguerin
 * 
 */
public class WorkflowBrowser extends ProjectBrowser {

	protected static final Logger logger = Logger.getLogger(WorkflowBrowser.class.getPackage().getName());

	public WorkflowBrowser(FlexoProject project) {
		this((WKFController) null);
		setRootObject(project);
	}

	public WorkflowBrowser(WKFController controller) {
		super(controller);
	}

	@Override
	public boolean showOptionsButton() {
		return !UserType.isLite() && !UserType.isCustomerRelease();
	}

	@Override
	protected boolean activateBrowsingFor(BrowserElement newElement) {
		return newElement.getObject().getProject() == getRootObject().getProject() && super.activateBrowsingFor(newElement);
	}

	@Override
	public void configure() {
		setFilterStatus(BrowserElementType.PRECONDITION, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.POSTCONDITION, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ROLE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.STATUS, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.PORT_REGISTERY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.MESSAGE_DEFINITION, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.SERVICE_INTERFACE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.SERVICE_OPERATION, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ACTIVITY_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ACTION_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATION_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.SUBPROCESS_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_AND_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_OR_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_IF_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_LOOP_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_COMPLEX_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_EXCLUSIVE_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_INCLUSIVE_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_SWITCH_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.EVENT_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.GROUP, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.DM_MODEL, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.COMPONENT_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.DKV_MODEL, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.MENU_ITEM, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.WS_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.CALC_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OE_SHEMA_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ROLE_LIST, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.PROJECT, BrowserFilterStatus.HIDE, true);
		setFilterStatus(BrowserElementType.PROCESS_FOLDER, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN, true);
	}

	@Override
	public boolean showRootNode() {
		return false;
	}

}
