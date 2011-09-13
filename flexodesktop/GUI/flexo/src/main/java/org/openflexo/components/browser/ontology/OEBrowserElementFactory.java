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
import org.openflexo.foundation.ontology.calc.CalcDrawingConnector;
import org.openflexo.foundation.ontology.calc.CalcDrawingShape;
import org.openflexo.foundation.ontology.calc.CalcDrawingShema;
import org.openflexo.foundation.ontology.calc.CalcFolder;
import org.openflexo.foundation.ontology.calc.CalcLibrary;
import org.openflexo.foundation.ontology.calc.CalcPalette;
import org.openflexo.foundation.ontology.calc.CalcPaletteElement;
import org.openflexo.foundation.ontology.calc.EditionPattern;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.foundation.ontology.shema.OEConnector;
import org.openflexo.foundation.ontology.shema.OEShape;
import org.openflexo.foundation.ontology.shema.OEShema;
import org.openflexo.foundation.ontology.shema.OEShemaDefinition;
import org.openflexo.foundation.ontology.shema.OEShemaFolder;
import org.openflexo.foundation.ontology.shema.OEShemaLibrary;


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
        else if (object instanceof CalcLibrary) {
        	return new CalcLibraryElement((CalcLibrary) object, browser,parent);
        }
        else if (object instanceof CalcFolder) {
        	return new CalcFolderElement((CalcFolder) object, browser,parent);
        }
        else if (object instanceof OntologyCalc) {
        	return new OntologyCalcElement((OntologyCalc) object, browser,parent);
        }
        else if (object instanceof EditionPattern) {
        	return new EditionPatternElement((EditionPattern) object, browser,parent);
        }
        else if (object instanceof CalcPalette) {
        	return new CalcPaletteBrowserElement((CalcPalette) object, browser,parent);
        }
        else if (object instanceof CalcPaletteElement) {
        	return new CalcPaletteElementBrowserElement((CalcPaletteElement) object, browser,parent);
        }
        else if (object instanceof CalcDrawingShema) {
        	return new CalcDrawingShemaBrowserElement((CalcDrawingShema) object, browser,parent);
        }
        else if (object instanceof CalcDrawingShape) {
        	return new CalcDrawingShapeBrowserElement((CalcDrawingShape) object, browser,parent);
        }
        else if (object instanceof CalcDrawingConnector) {
        	return new CalcDrawingConnectorBrowserElement((CalcDrawingConnector) object, browser,parent);
        }
       else if (object instanceof OEShemaLibrary) {
        	return new ShemaLibraryElement((OEShemaLibrary) object, browser,parent);
        }
        else if (object instanceof OEShemaFolder) {
        	return new ShemaFolderElement((OEShemaFolder) object, browser,parent);
        }
        else if (object instanceof OEShemaDefinition) {
        	return new ShemaDefinitionElement((OEShemaDefinition) object, browser,parent);
        }
        else if (object instanceof OEShema) {
        	return new OEShemaElement((OEShema) object, browser,parent);
        }
        else if (object instanceof OEShape) {
        	return new OEShapeElement((OEShape) object, browser,parent);
        }
        else if (object instanceof OEConnector) {
        	return new OEConnectorElement((OEConnector) object, browser,parent);
        }

        return null;
    }


}
