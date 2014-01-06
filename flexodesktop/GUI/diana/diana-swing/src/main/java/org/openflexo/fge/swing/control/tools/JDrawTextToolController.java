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
package org.openflexo.fge.swing.control.tools;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.tools.DrawTextToolController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.swing.view.JDrawingView;

/**
 * Swing implementation for the controller of the DrawText tool<br>
 * As swing component, this controller is driven by {@link MouseEvent}
 * 
 * @author sylvain
 */
public class JDrawTextToolController extends DrawTextToolController<MouseEvent> {

	private static final Logger logger = Logger.getLogger(JDrawTextToolController.class.getPackage().getName());

	public JDrawTextToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		super(controller, control);
	}

	/**
	 * Return the DrawingView of the controller this tool is associated to
	 * 
	 * @return
	 */
	public JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) super.getDrawingView();
	}

	public DrawingTreeNode<?, ?> getFocusedObject(MouseEvent e) {
		return getDrawingView().getFocusRetriever().getFocusedObject(e);
	}

	/**
	 * Return point where event occurs, relative to DrawingView
	 */
	public FGEPoint getPoint(MouseEvent e) {
		Point pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getDrawingView());
		return new FGEPoint(pt.getX(), pt.getY());
	}
}
