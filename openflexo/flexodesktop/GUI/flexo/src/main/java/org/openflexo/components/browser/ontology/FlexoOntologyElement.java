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
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.localization.FlexoLocalization;

/**
 * Browser element representing an ontology
 *
 * @author sguerin
 *
 */
public abstract class FlexoOntologyElement extends BrowserElement
{

    protected FlexoOntologyElement(FlexoOntology ontology, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent)
    {
        super(ontology, elementType, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
    	if (getProjectBrowser().getOEViewMode() == OEViewMode.NoHierarchy)
    	{
        	for (OntologyClass concept : getOntology().getClasses()) {
           		addToChilds(concept);
           	}
          	for (OntologyProperty property : getOntology().getObjectProperties()) {
          		if (!getProjectBrowser().showOnlyAnnotationProperties() || property.isAnnotationProperty())
          			addToChilds(property);
           	}
           	for (OntologyProperty property : getOntology().getDataProperties()) {
          		if (!getProjectBrowser().showOnlyAnnotationProperties() || property.isAnnotationProperty())
          			addToChilds(property);
           	}
           	for (OntologyIndividual concept : getOntology().getIndividuals()) {
           		addToChilds(concept);
           	}
    	}

    	if (getProjectBrowser().getOEViewMode() == OEViewMode.PartialHierarchy)
    	{
          	for (OntologyClass concept : getOntology().getRootClasses()) {
           		addToChilds(concept);
           	}
          	for (OntologyProperty concept : getOntology().getRootProperties()) {
           		addToChilds(concept);
           	}
          	/*for (OntologyProperty property : getOntology().getDataProperties()) {
           		addToChilds(property);
           	}
          	for (OntologyProperty property : getOntology().getObjectProperties()) {
           		addToChilds(property);
           	}*/
           	/*for (OntologyIndividual concept : getOntology().getIndividuals()) {
           		addToChilds(concept);
           	}*/
    	}

    }

    @Override
	public String getName()
    {
        return getOntology().getName()+(getOntology().isLoaded()?"":" "+FlexoLocalization.localizedForKey("<unloaded>"));
    }

    protected FlexoOntology getOntology()
    {
        return (FlexoOntology) getObject();
    }

}
