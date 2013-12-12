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
package org.openflexo.vpm.view.widget;

import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.selection.SelectionManager;
import org.openflexo.selection.SelectionManagingDianaEditor;

public class EditionPatternPreviewController extends SelectionManagingDianaEditor<EditionPattern> {

	private static final Logger logger = Logger.getLogger(EditionPatternPreviewController.class.getPackage().getName());

	// We share here instances of EditionPatternPreviewRepresentation because they can be accessed from multiple
	// EditionPatternPreviewComponent
	private static final Map<EditionPattern, EditionPatternPreviewRepresentation> editionPatternPreviewRepresentations = new Hashtable<EditionPattern, EditionPatternPreviewRepresentation>();

	/**
	 * Obtain or build stored EditionPatternPreviewRepresentation (they are all shared because they can be accessed from multiple
	 * EditionPatternPreviewComponent)
	 * 
	 * @param editionPattern
	 * @return
	 */
	private static final EditionPatternPreviewRepresentation obtainEditionPatternPreviewRepresentations(EditionPattern editionPattern) {
		EditionPatternPreviewRepresentation returned = editionPatternPreviewRepresentations.get(editionPattern);
		if (returned == null) {
			returned = new EditionPatternPreviewRepresentation(editionPattern);
			editionPatternPreviewRepresentations.put(editionPattern, returned);
		}
		return returned;
	}

	public EditionPatternPreviewController(EditionPattern editionPattern, SelectionManager sm) {
		super(obtainEditionPatternPreviewRepresentations(editionPattern), sm, EditionPatternPreviewRepresentation.PREVIEW_FACTORY, null);
	}

	@Override
	public void delete() {
		// Drawing is no more deleted since we keep all instances for sharing !!!!
		// getDrawing().delete();
		super.delete();
	}

	public EditionPattern getEditionPattern() {
		return getDrawing().getModel();
	}

}
