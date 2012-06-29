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
package org.openflexo.ie.view.controller;

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;

/**
 * Browser for WKF module
 * 
 * @author sguerin
 * 
 */
public class ComponentLibraryBrowser extends ProjectBrowser {

	protected static final Logger logger = Logger.getLogger(ComponentLibraryBrowser.class.getPackage().getName());

	public ComponentLibraryBrowser(IEController controller) {
		super(controller);
	}

	@Override
	public void configure() {
		setFilterStatus(BrowserElementType.OPERATION_COMPONENT, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.POPUP_COMPONENT, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.TAB_COMPONENT, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.SCREENCOMPONENTDEFINITION, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.REUSABLE_WIDGET, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.COMPONENT_FOLDER, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.HTMLTABLE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.TAB_CONTAINER, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.SEQUENCE, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.CONDITIONAL, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.REPETITION, BrowserFilterStatus.HIDE);
	}

	@Override
	public FlexoModelObject getDefaultRootObject() {
		return null;
	}

}
