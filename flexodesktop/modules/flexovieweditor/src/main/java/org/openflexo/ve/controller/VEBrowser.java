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
package org.openflexo.ve.controller;

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;

/**
 * Browser in OntologyEditor module
 * 
 * @author yourname
 * 
 */
public abstract class VEBrowser extends ProjectBrowser {

	protected static final Logger logger = Logger.getLogger(VEBrowser.class.getPackage().getName());

	// ================================================
	// ================= Variables ===================
	// ================================================

	protected VEController _controller;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	public VEBrowser(VEController controller) {
		super(controller);
	}

	@Override
	public void configure() {
		setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
		/*		setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);*/

		setFilterStatus(BrowserElementType.CALC_LIBRARY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC, BrowserFilterStatus.SHOW);

		setFilterStatus(BrowserElementType.OE_SHEMA_LIBRARY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.OE_SHEMA_FOLDER, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.OE_SHEMA_DEFINITION, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.OE_SHEMA, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.OE_SHAPE, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.OE_CONNECTOR, BrowserFilterStatus.SHOW);

		setFilterStatus(BrowserElementType.WORKFLOW, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.COMPONENT_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.COMPONENT_FOLDER, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.DM_MODEL, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.DKV_MODEL, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.MENU_ITEM, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.WS_LIBRARY, BrowserFilterStatus.HIDE);
	}

}
