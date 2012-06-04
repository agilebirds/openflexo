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

import java.awt.Graphics;
import java.util.logging.Logger;

import org.openflexo.fge.view.DrawingView;
import org.openflexo.vpm.drawingshema.DrawEdgeControl.DrawEdgeAction;

public class CalcDrawingShemaView extends DrawingView<CalcDrawingShemaRepresentation> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CalcDrawingShemaView.class.getPackage().getName());

	public CalcDrawingShemaView(CalcDrawingShemaRepresentation aDrawing, CalcDrawingShemaController controller) {
		super(aDrawing, controller);
	}

	@Override
	public CalcDrawingShemaController getController() {
		return (CalcDrawingShemaController) super.getController();
	}

	private DrawEdgeAction _drawEdgeAction;

	public void setDrawEdgeAction(DrawEdgeAction action) {
		_drawEdgeAction = action;
	}

	public void resetDrawEdgeAction() {
		_drawEdgeAction = null;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		boolean isBuffering = isBuffering();
		super.paint(g);
		if (_drawEdgeAction != null && !isBuffering) {
			_drawEdgeAction.paint(g, getController());
		}
	}

}
