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

import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentationImpl;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.dm.PatternRoleInserted;
import org.openflexo.foundation.viewpoint.dm.PatternRoleRemoved;

public class EditionPatternPreviewShemaGR extends DrawingGraphicalRepresentationImpl<EditionPattern> implements GraphicalFlexoObserver,
		EditionPatternPreviewConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EditionPatternPreviewShemaGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public EditionPatternPreviewShemaGR() {
		this(null);
	}

	public EditionPatternPreviewShemaGR(EditionPatternPreviewRepresentation aDrawing) {
		super(aDrawing);

		setWidth(PREVIEW_DEFAULT_WIDTH);
		setHeight(PREVIEW_DEFAULT_HEIGHT);
		setBackgroundColor(BACKGROUND_COLOR);
		setDrawWorkingArea(false);

		if (aDrawing != null && aDrawing.getEditionPattern() != null) {
			aDrawing.getEditionPattern().addObserver(this);
		}

	}

	@Override
	public EditionPatternPreviewRepresentation getDrawing() {
		return (EditionPatternPreviewRepresentation) super.getDrawing();
	}

	public EditionPattern getEditionPattern() {
		if (getDrawing() != null) {
			return getDrawing().getEditionPattern();
		}
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getEditionPattern()) {
			// logger.info("Notified " + dataModification);
			if ((dataModification instanceof PatternRoleInserted) || (dataModification instanceof PatternRoleRemoved)) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}

}
