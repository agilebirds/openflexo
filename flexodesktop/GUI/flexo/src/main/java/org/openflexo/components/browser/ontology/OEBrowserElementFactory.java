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
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyFolder;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;
import org.openflexo.foundation.viewpoint.ViewPointFolder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;


public class OEBrowserElementFactory implements BrowserElementFactory
{
    @Override
	public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent)
    {

        if (object instanceof OntologyLibrary) {
        	return new OntologyLibraryElement((OntologyLibrary) object, browser,parent);
        }
        else if (object instanceof OntologyFolder) {
        	return new OntologyFolderElement((OntologyFolder) object, browser,parent);
        }
       else if (object instanceof ProjectOntology) {
        	return new ProjectOntologyElement((ProjectOntology) object, browser,parent);
        }
        else if (object instanceof ImportedOntology) {
        	return new ImportedOntologyElement((ImportedOntology) object, browser,parent);
        }
        else if (object instanceof OntologyClass) {
        	return new OntologyClassElement((OntologyClass) object, browser,parent);
        }
        else if (object instanceof OntologyIndividual) {
        	return new OntologyIndividualElement((OntologyIndividual) object, browser,parent);
        }
        else if (object instanceof OntologyDataProperty) {
        	return new OntologyDataPropertyElement((OntologyDataProperty) object, browser,parent);
        }
        else if (object instanceof OntologyObjectProperty) {
        	return new OntologyObjectPropertyElement((OntologyObjectProperty) object, browser,parent);
        }
        else if (object instanceof OntologyStatement) {
        	return new OntologyStatementElement((OntologyStatement) object, browser,parent);
        }
        else if (object instanceof ViewPointLibrary) {
        	return new CalcLibraryElement((ViewPointLibrary) object, browser,parent);
        }
        else if (object instanceof ViewPointFolder) {
        	return new CalcFolderElement((ViewPointFolder) object, browser,parent);
        }
        else if (object instanceof ViewPoint) {
        	return new OntologyCalcElement((ViewPoint) object, browser,parent);
        }
        else if (object instanceof EditionPattern) {
        	return new EditionPatternElement((EditionPattern) object, browser,parent);
        }
        else if (object instanceof ViewPointPalette) {
        	return new CalcPaletteBrowserElement((ViewPointPalette) object, browser,parent);
        }
        else if (object instanceof ViewPointPaletteElement) {
        	return new CalcPaletteElementBrowserElement((ViewPointPaletteElement) object, browser,parent);
        }
        else if (object instanceof ExampleDrawingShema) {
        	return new CalcDrawingShemaBrowserElement((ExampleDrawingShema) object, browser,parent);
        }
        else if (object instanceof ExampleDrawingShape) {
        	return new CalcDrawingShapeBrowserElement((ExampleDrawingShape) object, browser,parent);
        }
        else if (object instanceof ExampleDrawingConnector) {
        	return new CalcDrawingConnectorBrowserElement((ExampleDrawingConnector) object, browser,parent);
        }
       else if (object instanceof ViewLibrary) {
        	return new ShemaLibraryElement((ViewLibrary) object, browser,parent);
        }
        else if (object instanceof ViewFolder) {
        	return new ShemaFolderElement((ViewFolder) object, browser,parent);
        }
        else if (object instanceof ViewDefinition) {
        	return new ShemaDefinitionElement((ViewDefinition) object, browser,parent);
        }
        else if (object instanceof View) {
        	return new OEShemaElement((View) object, browser,parent);
        }
        else if (object instanceof ViewShape) {
        	return new OEShapeElement((ViewShape) object, browser,parent);
        }
        else if (object instanceof ViewConnector) {
        	return new OEConnectorElement((ViewConnector) object, browser,parent);
        }

        return null;
    }


}
