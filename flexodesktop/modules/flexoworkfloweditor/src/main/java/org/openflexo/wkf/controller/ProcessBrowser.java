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

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;

/**
 * Browser for WKF module, browse only one process, with details
 * 
 * @author sguerin
 * 
 */
public class ProcessBrowser extends ProjectBrowser {

	private static final Logger logger = Logger.getLogger(ProcessBrowser.class.getPackage().getName());

	public ProcessBrowser(WKFController controller) {
		super(controller);
	}

	@Override
	public void configure() {
		setFilterStatus(BrowserElementType.PRECONDITION, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.POSTCONDITION, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.ROLE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.STATUS, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.MESSAGE_DEFINITION, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.MESSAGE, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.ACTIVITY_NODE, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.ACTION_NODE, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.OPERATION_NODE, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.PROCESS_FOLDER, BrowserFilterStatus.HIDE);
	}

}
