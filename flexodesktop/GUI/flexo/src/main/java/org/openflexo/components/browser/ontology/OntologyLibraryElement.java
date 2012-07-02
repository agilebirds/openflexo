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
package org.openflexo.components.browser.ontology;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.ProjectBrowser.OEViewMode;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyFolder;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * Browser element representing the ontology library
 * 
 * @author sguerin
 * 
 */
public class OntologyLibraryElement extends BrowserElement {

	protected OntologyLibraryElement(OntologyLibrary library, ProjectBrowser browser, BrowserElement parent) {
		super(library, BrowserElementType.ONTOLOGY_LIBRARY, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		if (getProjectBrowser().getOEViewMode() == OEViewMode.FullHierarchy) {
			addToChilds(getOntologyLibrary().getOWLOntology().getRootClass());
			/*for (OntologyObjectProperty property : getOntologyLibrary().getRootObjectProperties()) {
				if (!getProjectBrowser().showOnlyAnnotationProperties() || property.isAnnotationProperty()) {
					addToChilds(property);
				}
			}
			for (OntologyDataProperty property : getOntologyLibrary().getRootDataProperties()) {
				if (!getProjectBrowser().showOnlyAnnotationProperties() || property.isAnnotationProperty()) {
					addToChilds(property);
				}
			}*/
		}

		if (getProjectBrowser().getOEViewMode() == OEViewMode.NoHierarchy
				|| getProjectBrowser().getOEViewMode() == OEViewMode.PartialHierarchy) {
			for (OntologyFolder subFolder : getOntologyLibrary().getRootFolder().getChildren()) {
				addToChilds(subFolder);
			}
			for (FlexoOntology ontology : getOntologyLibrary().getRootFolder().getOntologies()) {
				addToChilds(ontology);
			}
		}

	}

	@Override
	public String getName() {
		return FlexoLocalization.localizedForKey("ontology_library");
	}

	protected OntologyLibrary getOntologyLibrary() {
		return (OntologyLibrary) getObject();
	}

}
