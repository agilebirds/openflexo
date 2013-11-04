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
package org.openflexo.dm.view.controller;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;

/**
 * Browser for DM module
 * 
 * @author sguerin
 * 
 */
public class DMBrowser extends ProjectBrowser {

	public DMBrowser(DMController controller) {
		super(controller);
	}

	@Override
	public void configure() {
		setFilterStatus(BrowserElementType.JDK_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.WO_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.EXTERNAL_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.DM_EOPROTOTYPES_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.DM_EXECUTION_MODEL_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
	}

}
