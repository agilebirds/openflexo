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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingPalette.PaletteDrawing;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;

public interface PaletteElement extends Serializable {

	public PaletteElementGraphicalRepresentation getGraphicalRepresentation();

	public boolean acceptDragging(GraphicalRepresentation target);

	public boolean elementDragged(GraphicalRepresentation target, FGEPoint dropLocation);

	public DrawingPalette getPalette();

	public static class PaletteElementGraphicalRepresentation extends ShapeGraphicalRepresentation<PaletteElement> {
		public PaletteElementGraphicalRepresentation(ShapeType shapeType, PaletteElement paletteElement, PaletteDrawing paletteDrawing) {
			super(shapeType, paletteElement, paletteDrawing);
		}

		public PaletteElementGraphicalRepresentation(ShapeGraphicalRepresentation<?> shapeGR, PaletteElement paletteElement,
				PaletteDrawing paletteDrawing) {
			super(shapeGR.getShapeType(), paletteElement, paletteDrawing);
			// Copy parameters...
			setsWith(shapeGR);
		}

		@Override
		public ShapeView<PaletteElement> makeShapeView(DrawingController controller) {
			return new PaletteElementView(this, controller);
		}

		@Override
		public final void setIsFocusable(boolean isFocusable) {
			super.setIsFocusable(isFocusable);
		}

		@Override
		public final boolean getIsFocusable() {
			return super.getIsFocusable();
		}

		@Override
		public final void setIsSelectable(boolean isSelectable) {
			super.setIsSelectable(isSelectable);
		}

		@Override
		public final boolean getIsSelectable() {
			return super.getIsSelectable();
		}

		@Override
		public final void setIsReadOnly(boolean readOnly) {
			super.setIsReadOnly(readOnly);
		}

		@Override
		public final boolean getIsReadOnly() {
			return super.getIsReadOnly();
		}

		@Override
		public final void setLocationConstraints(LocationConstraints locationConstraints) {
			super.setLocationConstraints(locationConstraints);
		}

		@Override
		public final LocationConstraints getLocationConstraints() {
			return super.getLocationConstraints();
		}

	}

	public static class PaletteElementView extends ShapeView<PaletteElement> {

		private static final Logger logger = Logger.getLogger(PaletteElementView.class.getPackage().getName());

		private DragSource dragSource;
		private DragGestureListener dgListener;
		private DragSourceListener dsListener;
		private int dragAction = DnDConstants.ACTION_COPY;
		private DragGestureRecognizer dgr;

		/* Local controller ONLY */
		private DrawingController<PaletteDrawing> paletteController;

		public PaletteElementView(PaletteElementGraphicalRepresentation aGraphicalRepresentation,
				DrawingController<PaletteDrawing> controller) {
			super(aGraphicalRepresentation, controller);
			this.dgListener = new DGListener();
			this.dragSource = DragSource.getDefaultDragSource();
			this.dsListener = new DSListener();
			this.paletteController = controller;

			// component, action, listener
			dgr = this.dragSource.createDefaultDragGestureRecognizer(this, this.dragAction, this.dgListener);

			enableDragging();

			if (aGraphicalRepresentation.getToolTipText() != null)
				setToolTipText(aGraphicalRepresentation.getToolTipText());
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			return getToolTipText();
		}

		@Override
		public PaletteElementGraphicalRepresentation getGraphicalRepresentation() {
			return (PaletteElementGraphicalRepresentation) super.getGraphicalRepresentation();
		}

		public PaletteElement getPaletteElement() {
			return getGraphicalRepresentation().getDrawable();
		}

		public DrawingPalette getPalette() {
			return getPaletteElement().getPalette();
		}

		public BufferedImage getBuffer() {
			return getDrawingView().getPaintManager().getScreenshot(getGraphicalRepresentation());
		}

		// ===============================================================
		// ================== Dnd Stuff =================================
		// ===============================================================

		protected void enableDragging() {
			dgr.setComponent(this);

			/**
			 * FIX for bug where element is not draggable when initial click begins on label
			 * 
			 * There is a big trick here: the label view is not a subcomponent of view, so dragging on this component will not be seen by
			 * palette element view, so we need here to force disable mouse listeners registered for this palette view
			 */
			if (getLabelView() != null) {
				getLabelView().disableMouseListeners();
			}

		}

		protected void disableDragging() {
			dgr.setComponent(null);
			if (getLabelView() != null)
				getLabelView().enableMouseListeners();
		}

		/**
		 * DGListener a listener that will start the drag. has access to top level's dsListener and dragSource
		 * 
		 * @see java.awt.dnd.DragGestureListener
		 * @see java.awt.dnd.DragSource
		 * @see java.awt.datatransfer.StringSelection
		 */
		class DGListener implements DragGestureListener {
			/**
			 * Start the drag if the operation is ok. uses java.awt.datatransfer.StringSelection to transfer the label's data
			 * 
			 * @param e
			 *            the event object
			 */
			@Override
			public void dragGestureRecognized(DragGestureEvent e) {
				logger.info("dragGestureRecognized");

				// if the action is ok we go ahead
				// otherwise we punt
				if ((e.getDragAction() & dragAction) == 0)
					return;
				// get the label's text and put it inside a Transferable
				// Transferable transferable = new StringSelection(
				// DragLabel.this.getText() );

				PaletteElementTransferable transferable = new PaletteElementTransferable(getDrawable(), e.getDragOrigin());

				try {
					// initial cursor, transferrable, dsource listener
					e.startDrag(DrawingPalette.dropKO, transferable, dsListener);
					logger.info("Starting drag for " + getGraphicalRepresentation());
					getDrawingView().captureDraggedNode(PaletteElementView.this, e);
				} catch (Exception idoe) {
					logger.warning("Unexpected exception " + idoe);
				}
			}

		}

		/**
		 * DSListener a listener that will track the state of the DnD operation
		 * 
		 * @see java.awt.dnd.DragSourceListener
		 * @see java.awt.dnd.DragSource
		 * @see java.awt.datatransfer.StringSelection
		 */
		public class DSListener implements DragSourceListener {

			/**
			 * @param e
			 *            the event
			 */
			@Override
			public void dragDropEnd(DragSourceDropEvent e) {
				getDrawingView().resetCapturedNode();
				if (e.getDropSuccess() == false) {
					if (logger.isLoggable(Level.INFO))
						logger.info("Dropping was not successful");
					return;
				}
				/*
				 * the dropAction should be what the drop target specified in
				 * acceptDrop
				 */
				// this is the action selected by the drop target
				if (e.getDropAction() == DnDConstants.ACTION_MOVE)
					setName("");
			}

			/**
			 * @param e
			 *            the event
			 */
			@Override
			public void dragEnter(DragSourceDragEvent e) {
				DragSourceContext context = e.getDragSourceContext();
				// intersection of the users selected action, and the source and
				// target actions
				int myaction = e.getDropAction();
				if ((myaction & dragAction) != 0) {
					context.setCursor(DragSource.DefaultCopyDrop);
				} else {
					context.setCursor(DragSource.DefaultCopyNoDrop);
				}
			}

			/**
			 * @param e
			 *            the event
			 */
			@Override
			public void dragOver(DragSourceDragEvent e) {
				// interface
				getPalette().dragSourceContext = e.getDragSourceContext();
			}

			/**
			 * @param e
			 *            the event
			 */
			@Override
			public void dragExit(DragSourceEvent e) {
				// interface
			}

			/**
			 * for example, press shift during drag to change to a link action
			 * 
			 * @param e
			 *            the event
			 */
			@Override
			public void dropActionChanged(DragSourceDragEvent e) {
				DragSourceContext context = e.getDragSourceContext();
				context.setCursor(DragSource.DefaultCopyNoDrop);
			}
		}
	}

	public static class PaletteElementTransferable implements Transferable {

		private static DataFlavor _defaultFlavor;

		private TransferedPaletteElement _transferedData;

		public PaletteElementTransferable(PaletteElement element, Point dragOrigin) {
			_transferedData = new TransferedPaletteElement(element, dragOrigin);
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
				_defaultFlavor = new DataFlavor(PaletteElementTransferable.class, "PaletteElement");
			}
			return _defaultFlavor;
		}

	}

	public static class TransferedPaletteElement {
		private Point _offset;

		private PaletteElement _transfered;

		public TransferedPaletteElement(PaletteElement element, Point dragOffset) {
			super();
			_transfered = element;
			_offset = dragOffset;
		}

		public Point getOffset() {
			return _offset;
		}

		public PaletteElement getPaletteElement() {
			return _transfered;
		}

	}

}
