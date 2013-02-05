package org.openflexo.fge.view;

import java.awt.Point;
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
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.DrawingPalette.PaletteDrawing;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.controller.PaletteElement.PaletteElementGraphicalRepresentation;
import org.openflexo.fge.controller.PaletteElement.PaletteElementTransferable;
import org.openflexo.toolbox.ToolBox;

import sun.awt.dnd.SunDragSourceContextPeer;

public class PaletteElementView extends ShapeView<PaletteElement> {

	private static final Logger logger = Logger.getLogger(PaletteElementView.class.getPackage().getName());

	private DragSource dragSource;
	private DragGestureListener dgListener;
	private DragSourceListener dsListener;
	private int dragAction = DnDConstants.ACTION_COPY;
	private DragGestureRecognizer dgr;
	private DragGestureRecognizer labelDgr;

	/* Local controller ONLY */
	private DrawingController<PaletteDrawing> paletteController;

	public PaletteElementView(PaletteElementGraphicalRepresentation aGraphicalRepresentation, DrawingController<PaletteDrawing> controller) {
		super(aGraphicalRepresentation, controller);
		this.dgListener = new DGListener();
		this.dragSource = DragSource.getDefaultDragSource();
		this.dsListener = new DSListener();
		this.paletteController = controller;

		dgr = createDragGestureRecognizer();
		// component, action, listener
		enableDragging();

		if (aGraphicalRepresentation.getToolTipText() != null) {
			setToolTipText(aGraphicalRepresentation.getToolTipText());
		}
	}

	private DragGestureRecognizer createDragGestureRecognizer() {
		return dragSource.createDefaultDragGestureRecognizer(this, this.dragAction, this.dgListener);
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
			if (labelDgr == null) {
				labelDgr = createDragGestureRecognizer();
			}
			getLabelView().disableTextComponentMouseListeners();
			labelDgr.setComponent(getLabelView().getTextComponent());
		}

	}

	protected void disableDragging() {
		// dgr.setComponent(null);
		if (getLabelView() != null) {
			getLabelView().enableTextComponentMouseListeners();
		}
		if (labelDgr != null) {
			labelDgr.setComponent(null);
		}
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
			if ((e.getDragAction() & dragAction) == 0) {
				return;
				// get the label's text and put it inside a Transferable
				// Transferable transferable = new StringSelection(
				// DragLabel.this.getText() );
			}

			Point p = SwingUtilities.convertPoint(e.getComponent(), e.getDragOrigin(), PaletteElementView.this);
			PaletteElementTransferable transferable = new PaletteElementTransferable(getDrawable(), p);
			if (ToolBox.isMacOS()) {
				// Need to call this on MacOS.
				// Scenario to reproduce issue
				// 1. Drop a sub process node
				// 2. Choose create a new subprocess
				// 3. Try to drop another element-->InvalidDnDOperationException
				synchronized (SunDragSourceContextPeer.class) {
					try {
						SunDragSourceContextPeer.checkDragDropInProgress();
					} catch (InvalidDnDOperationException ex) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("For some reason there was still a Dnd in progress. Will set it back to false. God knows why this happens");
						}
						if (logger.isLoggable(Level.FINE)) {
							logger.log(Level.FINE, "Stacktrace for DnD still in progress", ex);
						}
						SunDragSourceContextPeer.setDragDropInProgress(false);
					}
				}
			}
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
			// Resets the screenshot stored by the palette view.
			getDrawingView().resetCapturedNode();
			if (e.getDropSuccess() == false) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Dropping was not successful");
				}
				return;
			}
			/*
			 * the dropAction should be what the drop target specified in
			 * acceptDrop
			 */
			// this is the action selected by the drop target
			if (e.getDropAction() == DnDConstants.ACTION_MOVE) {
				setName("");
			}
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
			getPalette().setDragSourceContext(e.getDragSourceContext());
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