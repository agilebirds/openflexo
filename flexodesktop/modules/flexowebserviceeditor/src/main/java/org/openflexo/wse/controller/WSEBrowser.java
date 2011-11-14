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
package org.openflexo.wse.controller;

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;

/**
 * Define what to put in the browser for this module
 * 
 * @author yourname
 * 
 */
public class WSEBrowser extends ProjectBrowser {

	protected static final Logger logger = Logger.getLogger(WSEBrowser.class.getPackage().getName());

	// ================================================
	// ================= Variables ===================
	// ================================================

	protected WSEController _controller;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	public WSEBrowser(WSEController controller) {
		super(controller.getEditor(), controller.getSelectionManager() /* Remove this parameter if you don't want browser synchronized with selection */);
		_controller = controller;
		update();
	}

	@Override
	public void configure() {
		// Defines here what kind of element you want to see in your browser
		setFilterStatus(BrowserElementType.PRECONDITION, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.POSTCONDITION, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ROLE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.STATUS, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.DEADLINE, BrowserFilterStatus.HIDE);

		setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.HIDE, true);
		setFilterStatus(BrowserElementType.PORT_REGISTERY, BrowserFilterStatus.HIDE);

		// setFilterStatus(BrowserElementType.PORT, BrowserFilter.DESACTIVATE, false);
		// setFilterStatus(BrowserElementType.WS_REPOSITORY_LIST, BrowserFilter.ACTIVATE, true);

		// setFilterStatus(BrowserElementType.COMPONENT, BrowserFilter.ACTIVATE);
		setFilterStatus(BrowserElementType.ACTIVITY_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ACTION_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATION_NODE, BrowserFilterStatus.HIDE);
		// setFilterStatus(BrowserElementType.BLOC, BrowserFilter.ACTIVATE);
		setFilterStatus(BrowserElementType.SUBPROCESS_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_AND_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_OR_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OPERATOR_IF_NODE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.EVENT_NODE, BrowserFilterStatus.HIDE);

		// Hide DMProperties of Entities in Browser
		setFilterStatus(BrowserElementType.DM_PROPERTY, BrowserFilterStatus.HIDE);

	}

	@Override
	public FlexoModelObject getDefaultRootObject() {
		// Defines here what is the represented root objet (ex workflow for WKF, FlexoComponentLibrary for IE, DataModelEditor for DME,
		// etc...)
		return getProject().getFlexoWSLibrary();
	}

}
