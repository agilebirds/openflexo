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

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.rm.FlexoProject;

public class OntologyLibraryBrowser extends VEBrowser {
	public OntologyLibraryBrowser(VEController controller) {
		super(controller);
	}

	public void setOEViewMode(FlexoProject project, OEViewMode viewMode) {
		switch (viewMode) {
		case FullHierarchy:
			setRootObject(project.getProjectOntologyLibrary());
			break;
		case NoHierarchy:
			setRootObject(project);
			break;
		case PartialHierarchy:
			setRootObject(project);
			break;
		}
		setOEViewMode(viewMode);
	}

	public void setProject(FlexoProject project) {
		setOEViewMode(project, getOEViewMode());
	}

	@Override
	public void configure() {
		super.configure();
		setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.OE_SHEMA_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OE_SHEMA_FOLDER, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.OE_SHEMA_DEFINITION, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
		/*		setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);*/
		setFilterStatus(BrowserElementType.CALC_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC, BrowserFilterStatus.HIDE);
	}

	public void switchToNoHierarchyMode(FlexoProject project) {
		changeFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.SHOW);
		changeFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
		/*		changeFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);*/
		setOEViewMode(project, OEViewMode.NoHierarchy);
	}

	public void switchToPartialHierarchyMode(FlexoProject project) {
		changeFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.SHOW);
		changeFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
		/*		changeFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);*/
		setOEViewMode(project, OEViewMode.PartialHierarchy);
	}

	public void switchToFullHierarchyMode(FlexoProject project) {
		changeFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.HIDE);
		changeFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.HIDE);
		/*		changeFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				changeFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);*/
		setOEViewMode(project, OEViewMode.FullHierarchy);
	}

}