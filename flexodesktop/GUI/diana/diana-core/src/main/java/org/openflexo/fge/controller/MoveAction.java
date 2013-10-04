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
package org.openflexo.fge.controller;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.toolbox.ToolBox;

public class MoveAction extends MouseDragControlActionImpl {
	private static final Logger logger = Logger.getLogger(MoveAction.class.getPackage().getName());

	private MoveInfo currentMove = null;
	private DNDInfo currentDND = null;

	private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE,
			new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;
	public static Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE,
			new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	Point initialClickOffset;

	@Override
	public MouseDragControlActionType getActionType() {
		return MouseDragControlActionType.MOVE;
	}

	@Override
	public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Perform mouse DRAGGED on MOVE MouseDragControlActionImpl");
		}
			if (currentMove != null) {
				Point newPointLocation = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
						((DrawingControllerImpl<?>)controller).getDrawingView());

				if (node instanceof ShapeNode
						&& ((ShapeNode<?>) node).getGraphicalRepresentation().isAllowedToBeDraggedOutsideParentContainer()
						&& currentMove.isDnDPattern(newPointLocation, event) && currentDND == null) {
					currentMove.stopDragging();
					currentMove = null;
					currentDND = new DNDInfo(this, (ShapeNode<?>) node, ((DrawingControllerImpl<?>)controller), event);
				} else {
					currentMove.moveTo(newPointLocation);
				}
				return true;
			}
		return false;
	}

	@Override
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Perform mouse PRESSED on MOVE MouseDragControlActionImpl");
		}
		FGEView<?> view = ((DrawingControllerImpl<?>)controller).getDrawingView().viewForNode(node);
		initialClickOffset = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), (Component) view);
		if (node instanceof ShapeNode && !node.getGraphicalRepresentation().getIsReadOnly() && node.getDrawing().isEditable()
				&& ((ShapeNode<?>) node).getGraphicalRepresentation().getLocationConstraints() != LocationConstraints.UNMOVABLE) {
			// Let's go for a move
			currentMove = new MoveInfo((ShapeNode<?>) node, event, view, ((DrawingControllerImpl<?>)controller));
			((DrawingControllerImpl<?>)controller).notifyWillMove(currentMove);
			return true;
		}
		return false;
	}

	@Override
	public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event,
			boolean isSignificativeDrag) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Perform mouse RELEASED on MOVE MouseDragControlActionImpl");
		}
		if (currentMove != null) {
			if (isSignificativeDrag) {
				currentMove.stopDragging();
			}
			((DrawingControllerImpl<?>)controller).notifyHasMoved(currentMove);
			currentMove = null;
			return true;
		}
		return false;
	}

	public static class ShapeNodeTransferable implements Transferable {

		private static DataFlavor _defaultFlavor;

		private final TransferedShapeNode _transferedData;

		public ShapeNodeTransferable(ShapeNode<?> element, Point dragOrigin) {
			_transferedData = new TransferedShapeNode(element, dragOrigin);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { defaultFlavor() };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return true;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return _transferedData;
		}

		public static DataFlavor defaultFlavor() {
			if (_defaultFlavor == null) {
				_defaultFlavor = new DataFlavor(ShapeNodeTransferable.class, "ShapeNode");
			}
			return _defaultFlavor;
		}

	}

	public static class TransferedShapeNode {
		private final Point offset;

		private final ShapeNode<?> transfered;

		public TransferedShapeNode(ShapeNode<?> element, Point dragOffset) {
			super();
			transfered = element;
			offset = dragOffset;
		}

		public Point getOffset() {
			return offset;
		}

		public ShapeNode<?> getTransferedElement() {
			return transfered;
		}

	}

	public void resetCurrentDND() {
		currentDND = null;
	}

}