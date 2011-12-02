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
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.icon.IconFactory;

/**
 * Browser element representing the calc library
 * 
 * @author sguerin
 * 
 */
public class OntologyClassElement extends BrowserElement {

	protected OntologyClassElement(OntologyClass concept, ProjectBrowser browser, BrowserElement parent) {
		super(concept, BrowserElementType.ONTOLOGY_CLASS, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		if (getProjectBrowser().getOEViewMode() == OEViewMode.FullHierarchy) {
			for (OntologyClass subClass : getOntologyClass().getSubClasses()) {
				addToChilds(subClass);
			}
			for (OntologyIndividual individual : getOntologyClass().getIndividuals()) {
				addToChilds(individual);
			}
		}

		if (getProjectBrowser().getOEViewMode() == OEViewMode.PartialHierarchy) {
			BrowserElement ontologyElement = findNearestAncestor(BrowserElementType.PROJECT_ONTOLOGY, BrowserElementType.IMPORTED_ONTOLOGY);
			if (ontologyElement instanceof FlexoOntologyElement) {
				FlexoOntology ontology = ((FlexoOntologyElement) ontologyElement).getOntology();
				for (OntologyClass subClass : getOntologyClass().getSubClasses(ontology)) {
					addToChilds(subClass);
				}
				for (OntologyIndividual individual : getOntologyClass().getIndividuals()) {
					if (individual.getFlexoOntology() == ontology) {
						addToChilds(individual);
					}
				}
			}
		}

		for (OntologyStatement s : getOntologyClass().getStatements()) {
			addToChilds(s);
		}
	}

	@Override
	public String getName() {
		return getOntologyClass().getName();
	}

	protected OntologyClass getOntologyClass() {
		return (OntologyClass) getObject();
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
				return getOntologyClass().getFlexoOntology() == ((FlexoOntologyElement) ontologyElement).getOntology();
			}
		}
		return true;

	}
}
