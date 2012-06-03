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
package org.openflexo.fge.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;

public class FGEPaintManager {

	private static final Logger logger = Logger.getLogger(FGEPaintManager.class.getPackage().getName());

	protected static final Logger paintPrimitiveLogger = Logger.getLogger("PaintPrimitive");
	protected static final Logger paintRequestLogger = Logger.getLogger("PaintRequest");
	protected static final Logger paintStatsLogger = Logger.getLogger("PaintStats");

	private DrawingView<?> _drawingView;

	public FGEPaintManager(DrawingView<?> drawingView) {
		super();
		_drawingView = drawingView;
	}

	public DrawingView<?> getDrawingView() {
		return _drawingView;
	}

	public DrawingController<?> getDrawingController() {
		return _drawingView.getController();
	}

	public void repaint(FGEView<?> view, Rectangle bounds) {
		if (!_drawingView.contains(view)) {
			return;
		}

		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for view " + view + " for " + bounds);
		}
		((JComponent) view).repaint(bounds);

	}

	public void repaint(final FGEView<?> view) {
		if (view.isDeleted()) {
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					repaint(view);
				}
			});
			return;
		}
		if (!_drawingView.contains(view)) {
			return;
		}

		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for view " + view);
		}
		if (view == _drawingView) {
			// clearTemporaryRepaintArea();
			// paintRequestLogger.warning("Called repaint on whole DrawingView. Is it really necessary ?");
		}
		((JComponent) view).repaint();
		if (view.getGraphicalRepresentation().hasFloatingLabel()) {
			LabelView<?> label = view.getLabelView();
			if (label != null) {
				label.repaint();
			}
		}
		// repaintManager.repaintTemporaryRepaintAreas((JComponent)view);

		if (view instanceof ShapeView /*&& isPaintingCacheEnabled()*/) {
			Container parent = ((Component) view).getParent();
			if (parent == null) {
				return;
			}
			// What may happen here ?
			// Control points displayed focus or selection might changed, and to be refresh correctely
			// we must assume that a request to an extended area embedding those control points
			// must be performed (in case of border is not sufficient)
			ShapeGraphicalRepresentation<?> gr = ((ShapeView<?>) view).getGraphicalRepresentation();
			int requiredControlPointSpace = FGEConstants.CONTROL_POINT_SIZE;
			if (gr.getBorder().top * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace, -requiredControlPointSpace,
						((Component) view).getWidth() + requiredControlPointSpace * 2, requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, parent);
				parent.repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (gr.getBorder().bottom * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace, ((Component) view).getHeight()
						- requiredControlPointSpace, ((Component) view).getWidth() + requiredControlPointSpace * 2,
						requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, parent);
				parent.repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (gr.getBorder().left * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace, -requiredControlPointSpace,
						requiredControlPointSpace * 2, ((Component) view).getHeight() + requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, parent);
				parent.repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (gr.getBorder().right * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(((Component) view).getWidth() - requiredControlPointSpace,
						-requiredControlPointSpace, requiredControlPointSpace * 2, ((Component) view).getHeight()
								+ requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, parent);
				parent.repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
		}
	}

	public void repaint(GraphicalRepresentation<?> gr) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for graphical representation " + gr);
		}
		FGEView<?> view = _drawingView.viewForObject(gr);
		if (view != null) {
			repaint(view);
		}
	}

	// *******************************************************************************
	// * Repaint manager *
	// *******************************************************************************

}
