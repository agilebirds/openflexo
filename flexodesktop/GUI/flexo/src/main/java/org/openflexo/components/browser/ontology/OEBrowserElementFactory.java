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

import java.util.logging.Level;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.model.ViewConnector;
import org.openflexo.foundation.view.diagram.model.ViewShape;
import org.openflexo.foundation.viewpoint.DiagramPalette;
import org.openflexo.foundation.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.viewpoint.DiagramPaletteElement.ConnectorOverridingGraphicalRepresentation;
import org.openflexo.foundation.viewpoint.DiagramPaletteElement.ShapeOverridingGraphicalRepresentation;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ExampleDiagram;
import org.openflexo.foundation.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.viewpoint.ExampleDiagramShape;
import org.openflexo.foundation.viewpoint.ViewPoint;

public class OEBrowserElementFactory implements BrowserElementFactory {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(OEBrowserElementFactory.class
			.getPackage().getName());

	@Override
	public BrowserElement makeNewElement(FlexoObject object, ProjectBrowser browser, BrowserElement parent) {

		/*if (object instanceof ViewPointLibrary) {
			return new CalcLibraryElement((ViewPointLibrary) object, browser, parent);
		}*//*else if (object instanceof ViewPointFolder) {
			return new CalcFolderElement((ViewPointFolder) object, browser, parent);
			}else*/if (object instanceof ViewPoint) {
			return new OntologyCalcElement((ViewPoint) object, browser, parent);
		} else if (object instanceof EditionPattern) {
			return new EditionPatternElement((EditionPattern) object, browser, parent);
		} else if (object instanceof DiagramPalette) {
			return new CalcPaletteBrowserElement((DiagramPalette) object, browser, parent);
		} else if (object instanceof DiagramPaletteElement) {
			return new CalcPaletteElementBrowserElement((DiagramPaletteElement) object, browser, parent);
		} else if (object instanceof ShapeOverridingGraphicalRepresentation) {
			return new ShapeOverridingGRBrowserElement((ShapeOverridingGraphicalRepresentation) object, browser, parent);
		} else if (object instanceof ConnectorOverridingGraphicalRepresentation) {
			return new ConnectorOverridingGRBrowserElement((ConnectorOverridingGraphicalRepresentation) object, browser, parent);
		} else if (object instanceof ExampleDiagram) {
			return new CalcDrawingShemaBrowserElement((ExampleDiagram) object, browser, parent);
		} else if (object instanceof ExampleDiagramShape) {
			return new CalcDrawingShapeBrowserElement((ExampleDiagramShape) object, browser, parent);
		} else if (object instanceof ExampleDiagramConnector) {
			return new CalcDrawingConnectorBrowserElement((ExampleDiagramConnector) object, browser, parent);
		} else if (object instanceof ViewLibrary) {
			return new ViewLibraryElement((ViewLibrary) object, browser, parent);
		} else if (object instanceof ViewFolder) {
			return new ViewFolderElement((ViewFolder) object, browser, parent);
		} else if (object instanceof ViewDefinition) {
			return new ViewDefinitionElement((ViewDefinition) object, browser, parent);
		} else if (object instanceof View) {
			return new ViewElement((View) object, browser, parent);
		} else if (object instanceof ViewShape) {
			return new OEShapeElement((ViewShape) object, browser, parent);
		} else if (object instanceof ViewConnector) {
			return new OEConnectorElement((ViewConnector) object, browser, parent);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Can't handle object of type " + object.getClass().getName());
			}
		}

		return null;
	}

}
