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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.ProjectBrowser.OEViewMode;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.icon.IconFactory;

/**
 * Browser element representing the calc library
 * 
 * @author sguerin
 * 
 */
public class OntologyDataPropertyElement extends BrowserElement {

	protected OntologyDataPropertyElement(OntologyDataProperty property, ProjectBrowser browser, BrowserElement parent) {
		super(property, BrowserElementType.ONTOLOGY_DATA_PROPERTY, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		if (getProjectBrowser().getOEViewMode() == OEViewMode.FullHierarchy) {
			for (OntologyProperty subProperty : getProperty().getSubProperties()) {
				if (!getProjectBrowser().showOnlyAnnotationProperties() || subProperty.isAnnotationProperty()) {
					addToChilds(subProperty);
				}
			}
		}

		if (getProjectBrowser().getOEViewMode() == OEViewMode.PartialHierarchy) {
			BrowserElement ontologyElement = findNearestAncestor(BrowserElementType.PROJECT_ONTOLOGY, BrowserElementType.IMPORTED_ONTOLOGY);
			if (ontologyElement instanceof FlexoOntologyElement) {
				FlexoOntology ontology = ((FlexoOntologyElement) ontologyElement).getOntology();
				for (OntologyProperty subProperty : getProperty().getSubProperties(ontology)) {
					addToChilds(subProperty);
				}
			}
		}

		for (OntologyStatement s : getProperty().getStatements()) {
			addToChilds(s);
		}
	}

	@Override
	public String getName() {
		return getProperty().getName();
	}

	protected OntologyDataProperty getProperty() {
		return (OntologyDataProperty) getObject();
	}

	@Override
	public Icon getIcon() {
		Icon returned = super.getIcon();

		if (returned instanceof ImageIcon && !isEnabled()) {
			returned = IconFactory.getDisabledIcon((ImageIcon) returned);
		}
		return returned;
	}

	public boolean isEnabled() {
		if (getProjectBrowser().getOEViewMode() == OEViewMode.PartialHierarchy) {
			BrowserElement ontologyElement = findNearestAncestor(BrowserElementType.PROJECT_ONTOLOGY, BrowserElementType.IMPORTED_ONTOLOGY);
			if (ontologyElement instanceof FlexoOntologyElement) {
				return getProperty().getFlexoOntology() == ((FlexoOntologyElement) ontologyElement).getOntology();
			}
		}
		return true;

	}

}
