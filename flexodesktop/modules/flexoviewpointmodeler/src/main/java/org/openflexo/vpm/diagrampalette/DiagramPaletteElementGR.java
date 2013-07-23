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
package org.openflexo.vpm.diagrampalette;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteElementRemoved;
import org.openflexo.toolbox.ToolBox;

public class DiagramPaletteElementGR extends ShapeGraphicalRepresentation<DiagramPaletteElement> implements GraphicalFlexoObserver {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DiagramPaletteElementGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public DiagramPaletteElementGR() {
		super(ShapeType.RECTANGLE, null, null);
	}

	public DiagramPaletteElementGR(DiagramPaletteElement aShape, Drawing<?> aDrawing) {
		super(ShapeType.RECTANGLE, aShape, aDrawing);

		addToMouseClickControls(new DiagramPaletteController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new DiagramPaletteController.ShowContextualMenuControl(true));
		}

		if (aShape != null) {
			aShape.addObserver(this);
		}

	}

	@Override
	public void delete() {
		// logger.info("Delete DiagramPaletteElementGR");
		if (getDrawable() != null) {
			getDrawable().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	public DiagramPaletteRepresentation getDrawing() {
		return (DiagramPaletteRepresentation) super.getDrawing();
	}

	public DiagramPaletteElement getCalcPaletteElement() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getCalcPaletteElement()) {
			if (dataModification instanceof DiagramPaletteElementInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof DiagramPaletteElementRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification != null && dataModification.propertyName() != null
					&& dataModification.propertyName().equals("boundLabelToElementName")) {
				notifyChange(GraphicalRepresentation.Parameters.text);
			}
		}
	}

	/*@Override
	public boolean getAllowToLeaveBounds() {
		return false;
	}*/

	@Override
	public String getText() {
		if (getCalcPaletteElement() != null && getCalcPaletteElement().getBoundLabelToElementName()) {
			return getCalcPaletteElement().getName();
		}
		return super.getText();
	}

	@Override
	public void setTextNoNotification(String text) {
		if (getCalcPaletteElement() != null) {
			getCalcPaletteElement().setName(text);
		} else {
			super.setTextNoNotification(text);
		}
	}

	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
		if (arg instanceof FGENotification && ((FGENotification) arg).isModelNotification() && getDrawing() != null
				&& !getDrawing().ignoreNotifications() && getCalcPaletteElement() != null
				&& !getCalcPaletteElement().getPalette().ignoreNotifications()) {
			getCalcPaletteElement().setChanged();
		}
	}

}
