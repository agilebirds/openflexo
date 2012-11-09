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
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
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
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.MoveAction.ShapeGraphicalRepresentationTransferable;
import org.openflexo.fge.controller.MoveAction.TransferedShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.fge.view.listener.FocusRetriever;

import sun.awt.dnd.SunDragSourceContextPeer;

public class DNDInfo {

	private static final Logger logger = Logger.getLogger(DNDInfo.class.getPackage().getName());

	/**
	 * 
	 */
	private final MoveAction _moveAction;
	private ShapeView<?> shapeView;
	private DragSource dragSource;
	private DragGestureListener dgListener;
	private DragSourceListener dsListener;
	private int dragAction = DnDConstants.ACTION_MOVE;
	private DragGestureRecognizer dgr;
	private DragSourceContext dragSourceContext;
	private DrawingController<?> _controller;
	private ShapeGraphicalRepresentation<?> draggedObject;

	private Hashtable<FGEView<?>, DropTarget> dropTargets;

	DNDInfo(MoveAction moveAction, ShapeGraphicalRepresentation<?> gr, DrawingController<?> controller, final MouseEvent initialEvent) {
		_moveAction = moveAction;
		_controller = controller;
		draggedObject = gr;

		logger.info("DnD gesture recognized, starting DnD");

		this.dgListener = new DGListener();
		this.dragSource = DragSource.getDefaultDragSource();
		this.dsListener = new DSListener();

		shapeView = controller.getDrawingView().shapeViewForObject(gr);

		// component, action, listener
		dgr = this.dragSource.createDefaultDragGestureRecognizer(shapeView, this.dragAction, this.dgListener);

		enableDragging();

		Point initialPoint = _moveAction.initialClickOffset;
		Vector<InputEvent> list = new Vector<InputEvent>();
		list.add(initialEvent);
		DragGestureEvent dge = new DragGestureEvent(dgr, dragAction, initialPoint, list) {
			@Override
			public InputEvent getTriggerEvent() {
				return initialEvent;
			}
		};
		// Hack for bug 1006304
		synchronized (SunDragSourceContextPeer.class) {
			try {
				SunDragSourceContextPeer.checkDragDropInProgress();
			} catch (InvalidDnDOperationException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("For some reason there was still a Dnd in progress. Will set it back to false. God knows why this happens");
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, "Stacktrace for DnD still in progress", e);
				}
				SunDragSourceContextPeer.setDragDropInProgress(false);
			}
		}
		this.dragSource.startDrag(dge, MoveAction.dropKO, new MoveAction.ShapeGraphicalRepresentationTransferable(gr, initialPoint),
				dsListener);
		controller.getDrawingView().captureDraggedNode(shapeView, dge);

		// gr.setIsVisible(false);

	}

	protected void enableDragging() {
		dgr.setComponent(shapeView);

		logger.info("MoveAction, enableDragging for " + shapeView);

		if (dropTargets != null) {
			dropTargets.clear();
		}
		dropTargets = new Hashtable<FGEView<?>, DropTarget>();

		for (GraphicalRepresentation<?> gr : _controller.getDrawingView().getContents().keySet()) {
			FGEView<?> view = _controller.getDrawingView().getContents().get(gr);
			if (((Component) view).getDropTarget() != null) {
				dropTargets.put(view, ((Component) view).getDropTarget());
			}
			((Component) view).setDropTarget(new DropTarget(shapeView, DnDConstants.ACTION_MOVE, new MoveActionDropListener(
					(JComponent) view), true));
		}
	}

	protected void disableDragging() {
		for (FGEView<?> v : dropTargets.keySet()) {
			((Component) v).setDropTarget(dropTargets.get(v));
		}
		dgr.setComponent(null);
		_moveAction.resetCurrentDND();
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

			ShapeGraphicalRepresentationTransferable transferable = new MoveAction.ShapeGraphicalRepresentationTransferable(null,
					e.getDragOrigin());

			try {
				// initial cursor, transferrable, dsource listener
				e.startDrag(DragSource.DefaultCopyNoDrop, transferable, dsListener);
				_controller.getDrawingView().captureDraggedNode(shapeView, e);
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
			try {
				// logger.info("dragDropEnd() "+e);
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
					;
					// setName("");
				}
			} finally {
				_controller.getDrawingView().resetCapturedNode();
				disableDragging();
			}
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragEnter(DragSourceDragEvent e) {
			// logger.info("************** dragEnter() "+e);
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
			// logger.info("dragOver() "+e);
			// interface
			dragSourceContext = e.getDragSourceContext();
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragExit(DragSourceEvent e) {
			// logger.info("dragExit() "+e);
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

	/**
	 * DTListener a listener that tracks the state of the operation
	 * 
	 * @see java.awt.dnd.DropTargetListener
	 * @see java.awt.dnd.DropTarget
	 */
	public class MoveActionDropListener implements DropTargetListener {

		private int acceptableActions = DnDConstants.ACTION_MOVE;
		private JComponent _dropContainer;

		// private static DefaultProcessBuilder _myPrivateBuilder;

		public MoveActionDropListener(JComponent dropContainer) {
			super();
			_dropContainer = dropContainer;
		}

		/**
		 * Called by isDragOk Checks to see if the flavor drag flavor is acceptable
		 * 
		 * @param e
		 *            the DropTargetDragEvent object
		 * @return whether the flavor is acceptable
		 */
		private boolean isDragFlavorSupported(DropTargetDragEvent e) {
			boolean ok = false;
			if (e.isDataFlavorSupported(ShapeGraphicalRepresentationTransferable.defaultFlavor())) {
				ok = true;
			}
			return ok;
		}

		/**
		 * Called by drop Checks the flavors and operations
		 * 
		 * @param e
		 *            the DropTargetDropEvent object
		 * @return the chosen DataFlavor or null if none match
		 */
		private DataFlavor chooseDropFlavor(DropTargetDropEvent e) {
			if (e.isLocalTransfer() == true && e.isDataFlavorSupported(ShapeGraphicalRepresentationTransferable.defaultFlavor())) {
				return ShapeGraphicalRepresentationTransferable.defaultFlavor();
			}
			return null;
		}

		/**
		 * Called by dragEnter and dragOver Checks the flavors and operations
		 * 
		 * @param e
		 *            the event object
		 * @return whether the flavor and operation is ok
		 */
		private boolean isDragOk(DropTargetDragEvent e) {
			if (isDragFlavorSupported(e) == false) {
				return false;
			}

			int da = e.getDropAction();
			// we're saying that these actions are necessary
			if ((da & acceptableActions) == 0) {
				return false;
			}

			try {
				ShapeGraphicalRepresentation<?> element = ((TransferedShapeGraphicalRepresentation) e.getTransferable().getTransferData(
						ShapeGraphicalRepresentationTransferable.defaultFlavor())).getTransferedElement();
				if (element == null) {
					return false;
				}
				GraphicalRepresentation<?> focused = getFocusedObject(e);
				if (focused == null) {
					return false;
				}
				return focused instanceof ShapeGraphicalRepresentation
						&& element.isAllowedToBeDraggedOutsideParentContainerInsideContainer(focused);

			} catch (UnsupportedFlavorException e1) {
				logger.warning("Unexpected: " + e1);
				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				logger.warning("Unexpected: " + e1);
				e1.printStackTrace();
				return false;
			} catch (Exception e1) {
				logger.warning("Unexpected: " + e1);
				e1.printStackTrace();
				return false;
			}
		}

		/**
		 * start "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
		 * 
		 * @param e
		 */
		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!isDragOk(e)) {
				// DropLabel.this.borderColor=Color.red;
				// showBorder(true);
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		/**
		 * continue "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
		 * 
		 * @param e
		 */
		@Override
		public void dragOver(DropTargetDragEvent e) {
			if (isDragFlavorSupported(e)) {
				_controller.getDrawingView().updateCapturedDraggedNodeImagePosition(e, _controller.getDrawingView());
			}
			if (!isDragOk(e)) {
				if (dragSourceContext == null) {
					logger.warning("dragSourceContext should NOT be null");
				} else {
					dragSourceContext.setCursor(MoveAction.dropKO);
				}
				e.rejectDrag();
				return;
			}
			if (dragSourceContext == null) {
				logger.warning("dragSourceContext should NOT be null");
			} else {
				dragSourceContext.setCursor(MoveAction.dropOK);

				/*try {
					ShapeGraphicalRepresentation element = ((TransferedShapeGraphicalRepresentation)e.getTransferable().getTransferData(ShapeGraphicalRepresentationTransferable.defaultFlavor())).getTransferedElement();
					GraphicalRepresentation<?> focused = getFocusedObject(e);
					if (focused instanceof ShapeGraphicalRepresentation) { 
						element.dragOutsideParentContainerInsideContainer((ShapeGraphicalRepresentation<?>)focused,new FGEPoint(0,0),true);
					}

				} catch (UnsupportedFlavorException e1) {
					logger.warning("Unexpected: "+e1);
					e1.printStackTrace();
				} catch (IOException e1) {
					logger.warning("Unexpected: "+e1);
					e1.printStackTrace();
				} catch (Exception e1) {
					logger.warning("Unexpected: "+e1);
					e1.printStackTrace();
				}*/
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!isDragOk(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dragExit(DropTargetEvent e) {
			// interface method
			// _controller.getDrawingView().resetCapturedNode();
		}

		/**
		 * perform action from getSourceActions on the transferrable invoke acceptDrop or rejectDrop invoke dropComplete if its a local
		 * (same JVM) transfer, use StringTransferable.localStringFlavor find a match for the flavor check the operation get the
		 * transferable according to the chosen flavor do the transfer
		 * 
		 * @param e
		 */
		@Override
		public void drop(DropTargetDropEvent e) {
			try {
				DataFlavor chosen = chooseDropFlavor(e);
				if (chosen == null) {
					e.rejectDrop();
					return;
				}

				// the actions that the source has specified with DragGestureRecognizer
				int sa = e.getSourceActions();

				if ((sa & acceptableActions) == 0) {
					e.rejectDrop();
					return;
				}

				Object data = null;

				try {

					/*
					 * the source listener receives this action in dragDropEnd. if the
					 * action is DnDConstants.ACTION_COPY_OR_MOVE then the source
					 * receives MOVE!
					 */

					data = e.getTransferable().getTransferData(chosen);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("data is a " + data.getClass().getName());
					}
					if (data == null) {
						throw new NullPointerException();
					}
				} catch (Throwable t) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Couldn't get transfer data: " + t.getMessage());
					}
					t.printStackTrace();
					e.dropComplete(false);
					return;
				}

				if (data instanceof TransferedShapeGraphicalRepresentation) {

					try {
						ShapeGraphicalRepresentation<?> element = ((TransferedShapeGraphicalRepresentation) data).getTransferedElement();
						if (element == null) {
							e.rejectDrop();
							return;
						}
						GraphicalRepresentation<?> focused = getFocusedObject(e);
						if (!(focused instanceof ShapeGraphicalRepresentation)) {
							e.rejectDrop();
							return;
						}
						// OK, let's got for the drop
						if (element.isAllowedToBeDraggedOutsideParentContainerInsideContainer(focused)) {
							Component targetComponent = e.getDropTargetContext().getComponent();
							Point pt = e.getLocation();
							FGEPoint modelLocation = new FGEPoint();
							if (targetComponent instanceof FGEView) {
								pt = FGEUtils.convertPoint(((FGEView<?>) targetComponent).getGraphicalRepresentation(),
										pt, focused, ((FGEView<?>) targetComponent).getScale());
								modelLocation.x = pt.x / ((FGEView<?>) targetComponent).getScale();
								modelLocation.y = pt.y / ((FGEView<?>) targetComponent).getScale();
								modelLocation.x -= ((TransferedShapeGraphicalRepresentation) data).getOffset().x;
								modelLocation.y -= ((TransferedShapeGraphicalRepresentation) data).getOffset().y;
							} else {
								modelLocation.x -= ((TransferedShapeGraphicalRepresentation) data).getOffset().x;
								modelLocation.y -= ((TransferedShapeGraphicalRepresentation) data).getOffset().y;
							}
							if (element.dragOutsideParentContainerInsideContainer(focused, modelLocation)) {
								e.acceptDrop(acceptableActions);
								e.dropComplete(true);
								return;
							} else {
								e.rejectDrop();
								e.dropComplete(false);
								return;
							}
						}

					} catch (Exception e1) {
						logger.warning("Unexpected: " + e1);
						e1.printStackTrace();
						e.rejectDrop();
						e.dropComplete(false);
						return;
					}

				}

				e.rejectDrop();
				e.dropComplete(false);
				return;
			}

			finally {
				// disableDragging();
			}
		}

		private FocusRetriever getFocusRetriever() {
			if (_dropContainer instanceof FGEView) {
				return ((FGEView<?>) _dropContainer).getDrawingView().getFocusRetriever();
			}
			return null;
		}

		private FGEView<?> getFGEView() {
			if (_dropContainer instanceof FGEView) {
				return (FGEView<?>) _dropContainer;
			}
			return null;
		}

		public GraphicalRepresentation<?> getFocusedObject(DropTargetDragEvent event) {
			if (getFocusRetriever() != null) {
				GraphicalRepresentation<?> returned = getFocusRetriever().getFocusedObject(event);
				if (returned == null) {
					// Since we are in a FGEView, a null value indicates that we are on the Drawing view
					return getFGEView().getGraphicalRepresentation().getDrawingGraphicalRepresentation();
				}
				return returned;
			}
			// No focus retriever: we are not in a FGEView....
			return null;
		}

		public GraphicalRepresentation<?> getFocusedObject(DropTargetDropEvent event) {
			if (getFocusRetriever() != null) {
				GraphicalRepresentation<?> returned = getFocusRetriever().getFocusedObject(event);
				if (returned == null) {
					// Since we are in a FGEView, a null value indicates that we are on the Drawing view
					return getFGEView().getGraphicalRepresentation().getDrawingGraphicalRepresentation();
				}
				return returned;
			}
			// No focus retriever: we are not in a FGEView....
			return null;
		}

	}

}