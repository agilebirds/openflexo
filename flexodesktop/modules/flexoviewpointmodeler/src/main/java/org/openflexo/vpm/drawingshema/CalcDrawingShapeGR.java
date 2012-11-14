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
package org.openflexo.vpm.drawingshema;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingConnectorInserted;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingConnectorRemoved;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingShapeInserted;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingShapeRemoved;
import org.openflexo.toolbox.ToolBox;

public class CalcDrawingShapeGR extends ShapeGraphicalRepresentation<ExampleDrawingShape> implements GraphicalFlexoObserver,
		CalcDrawingShemaConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CalcDrawingShapeGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public CalcDrawingShapeGR() {
		super(ShapeType.RECTANGLE, null, null);
	}

	public CalcDrawingShapeGR(ExampleDrawingShape aShape, Drawing<?> aDrawing) {
		super(ShapeType.RECTANGLE, aShape, aDrawing);

		addToMouseClickControls(new CalcDrawingShemaController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new CalcDrawingShemaController.ShowContextualMenuControl(true));
		}
		addToMouseDragControls(new DrawEdgeControl());

		if (aShape != null) {
			aShape.addObserver(this);
		}

	}

	@Override
	public void delete() {
		if (getDrawable() != null) {
			getDrawable().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	public CalcDrawingShemaRepresentation getDrawing() {
		return (CalcDrawingShemaRepresentation) super.getDrawing();
	}

	public ExampleDrawingShape getCalcDrawingShape() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getCalcDrawingShape()) {
			// logger.info("Notified " + dataModification);
			if (dataModification instanceof CalcDrawingShapeInserted || dataModification instanceof CalcDrawingShapeRemoved
					|| dataModification instanceof CalcDrawingConnectorInserted || dataModification instanceof CalcDrawingConnectorRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof NameChanged) {
				// logger.info("received NameChanged notification");
				setText(getText());
				notifyChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
			}
		}
	}

	/*@Override
	public boolean getAllowToLeaveBounds() {
		return false;
	}*/

	/*@Override
	public String getText() {
		if (getCalcDrawingShape() != null) {
			return getCalcDrawingShape().getName();
		}
		return null;
	}

	@Override
	public void setTextNoNotification(String text) {
		if (getCalcDrawingShape() != null) {
			getCalcDrawingShape().setName(text);
		}
	}*/

	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
		if (arg instanceof FGENotification && ((FGENotification) arg).isModelNotification() && getDrawing() != null
				&& !getDrawing().ignoreNotifications() && getCalcDrawingShape() != null && getCalcDrawingShape().getShema() != null
				&& !getCalcDrawingShape().getShema().ignoreNotifications()) {
			getCalcDrawingShape().setChanged();
		}
	}

}
