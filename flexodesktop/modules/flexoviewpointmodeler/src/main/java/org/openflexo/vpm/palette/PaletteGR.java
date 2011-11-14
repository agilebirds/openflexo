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
package org.openflexo.vpm.palette;

import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementRemoved;

public class PaletteGR extends DrawingGraphicalRepresentation<ViewPointPalette> implements GraphicalFlexoObserver {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PaletteGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public PaletteGR() {
		this(null);
	}

	public PaletteGR(CalcPaletteRepresentation aDrawing) {
		super(aDrawing);

		if (aDrawing != null && aDrawing.getPalette() != null && aDrawing.getPalette().getGraphicalRepresentation() != null) {

			setsWith((GraphicalRepresentation<?>) aDrawing.getPalette().getGraphicalRepresentation());
		}

		addToMouseClickControls(new CalcPaletteController.ShowContextualMenuControl());

		if (aDrawing != null && aDrawing.getPalette() != null) {
			aDrawing.getPalette().setGraphicalRepresentation(this);
			aDrawing.getPalette().addObserver(this);
		}

	}

	@Override
	public void delete() {
		logger.info("Delete paletteGR");
		if (getPalette() != null) {
			getPalette().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	public CalcPaletteRepresentation getDrawing() {
		return (CalcPaletteRepresentation) super.getDrawing();
	}

	public ViewPointPalette getPalette() {
		return getDrawing().getPalette();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getPalette()) {
			// logger.info("Notified "+dataModification);
			if (dataModification instanceof CalcPaletteElementInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof CalcPaletteElementRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}

}
