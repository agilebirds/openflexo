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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.model.ViewConnector;
import org.openflexo.foundation.view.diagram.model.ViewShape;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement.ConnectorOverridingGraphicalRepresentation;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement.ShapeOverridingGraphicalRepresentation;

public class OEBrowserElementFactory implements BrowserElementFactory {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(OEBrowserElementFactory.class
			.getPackage().getName());

	@Override
	public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {

		/*if (object instanceof ViewPointLibrary) {
			return new CalcLibraryElement((ViewPointLibrary) object, browser, parent);
		}*//*else if (object instanceof ViewPointFolder) {
			return new CalcFolderElement((ViewPointFolder) object, browser, parent);
			}else*/if (object instanceof ViewPoint) {
			return new OntologyCalcElement((ViewPoint) object, browser, parent);
		} else if (object instanceof EditionPattern) {
			return new EditionPatternElement((EditionPattern) object, browser, parent);
		} else if (object instanceof ViewPointPalette) {
			return new CalcPaletteBrowserElement((ViewPointPalette) object, browser, parent);
		} else if (object instanceof ViewPointPaletteElement) {
			return new CalcPaletteElementBrowserElement((ViewPointPaletteElement) object, browser, parent);
		} else if (object instanceof ShapeOverridingGraphicalRepresentation) {
			return new ShapeOverridingGRBrowserElement((ShapeOverridingGraphicalRepresentation) object, browser, parent);
		} else if (object instanceof ConnectorOverridingGraphicalRepresentation) {
			return new ConnectorOverridingGRBrowserElement((ConnectorOverridingGraphicalRepresentation) object, browser, parent);
		} else if (object instanceof ExampleDrawingShema) {
			return new CalcDrawingShemaBrowserElement((ExampleDrawingShema) object, browser, parent);
		} else if (object instanceof ExampleDrawingShape) {
			return new CalcDrawingShapeBrowserElement((ExampleDrawingShape) object, browser, parent);
		} else if (object instanceof ExampleDrawingConnector) {
			return new CalcDrawingConnectorBrowserElement((ExampleDrawingConnector) object, browser, parent);
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
