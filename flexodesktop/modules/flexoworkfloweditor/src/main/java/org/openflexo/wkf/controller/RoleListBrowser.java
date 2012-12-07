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

/**
 * Browser for WKF module, browse only one process, with details
 * 
 * @author sguerin
 * 
 */
public class RoleListBrowser extends ProjectBrowser {

	private static final Logger logger = Logger.getLogger(RoleListBrowser.class.getPackage().getName());

	protected WKFController _controller;

	public RoleListBrowser(WKFController controller) {
		super(controller);
		_controller = controller;
	}

	@Override
	protected boolean activateBrowsingFor(BrowserElement newElement) {
		return newElement.getObject().getProject() == getRootObject().getProject() && super.activateBrowsingFor(newElement);
	}

	@Override
	public void configure() {
		setFilterStatus(BrowserElementType.DM_MODEL, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.COMPONENT_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.DKV_MODEL, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.MENU_ITEM, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.WS_LIBRARY, BrowserFilterStatus.HIDE);
		// setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.CALC_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OE_SHEMA_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.IMPORTED_PROCESS_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.PROJECT, BrowserFilterStatus.HIDE, true);
		setFilterStatus(BrowserElementType.WORKFLOW, BrowserFilterStatus.HIDE, true);
	}

	@Override
	public boolean showRootNode() {
		return false;
	}

}
