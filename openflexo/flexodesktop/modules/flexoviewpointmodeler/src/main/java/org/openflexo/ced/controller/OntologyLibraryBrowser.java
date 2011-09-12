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
package org.openflexo.ced.controller;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.ontology.OntologyLibrary;


public class OntologyLibraryBrowser extends CEDBrowser
{
	public OntologyLibraryBrowser(CEDController controller)
	{
		super(controller);
		switchToPartialHierarchyMode(controller.getBaseOntologyLibrary());
	}

	@Override
	public void configure()
	{
		super.configure();
		setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY,BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.CALC_LIBRARY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC, BrowserFilterStatus.HIDE);
	}
	
	public void switchToNoHierarchyMode(OntologyLibrary ontologyLibrary)
	{
		changeFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY,BrowserFilterStatus.SHOW);
		changeFilterStatus(BrowserElementType.ONTOLOGY_CLASS,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT,BrowserFilterStatus.HIDE);
		
		setRootObject(ontologyLibrary);
		setOEViewMode(OEViewMode.NoHierarchy);
	}

	public void switchToPartialHierarchyMode(OntologyLibrary ontologyLibrary)
	{
		changeFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY,BrowserFilterStatus.SHOW);
		changeFilterStatus(BrowserElementType.ONTOLOGY_CLASS,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT,BrowserFilterStatus.HIDE);
		
		setRootObject(ontologyLibrary);
		setOEViewMode(OEViewMode.PartialHierarchy);
	}

	public void switchToFullHierarchyMode(OntologyLibrary ontologyLibrary)
	{
		changeFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY,BrowserFilterStatus.HIDE);
		changeFilterStatus(BrowserElementType.ONTOLOGY_CLASS,BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL,BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY,BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY,BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		changeFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		
		setRootObject(ontologyLibrary);
		setOEViewMode(OEViewMode.FullHierarchy);
	}
	
}