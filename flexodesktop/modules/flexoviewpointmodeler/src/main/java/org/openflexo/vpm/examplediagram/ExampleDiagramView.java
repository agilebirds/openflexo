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
package org.openflexo.vpm.examplediagram;

import java.awt.Graphics;
import java.util.logging.Logger;

import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.vpm.examplediagram.DrawEdgeControl.DrawEdgeAction;

@SuppressWarnings("serial")
public class ExampleDiagramView extends JDrawingView<ExampleDiagram> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ExampleDiagramView.class.getPackage().getName());

	public ExampleDiagramView(ExampleDiagramEditor controller) {
		super(controller);
	}

	@Override
	public ExampleDiagramEditor getController() {
		return (ExampleDiagramEditor) super.getController();
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
