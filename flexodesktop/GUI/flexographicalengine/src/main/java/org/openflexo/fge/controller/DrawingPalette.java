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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.controller.PaletteElement.PaletteElementTransferable;
import org.openflexo.fge.controller.PaletteElement.TransferedPaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.listener.FocusRetriever;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.toolbox.ToolBox;

public class DrawingPalette {

	private static final Logger logger = Logger.getLogger(DrawingPalette.class.getPackage().getName());

	private static Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;

	public static final Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	private DrawingController<?> _controller;

	private final PaletteDrawing _paletteDrawing;
	// This controller is the local controller for displaying the palette, NOT the controller
	// Which this palette is associated to.
	private DrawingController<PaletteDrawing> _paletteController;
	protected Vector<PaletteElement> elements;

	private DragSourceContext dragSourceContext;

	private final int width;
	private final int height;
	private final String title;

	public DrawingPalette(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
		elements = new Vector<PaletteElement>();
		_paletteDrawing = new PaletteDrawing();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Build palette " + title + " " + Integer.toHexString(hashCode()) + " of " + getClass().getName());
		}
	}

	public void delete() {
		_paletteController.delete();
		for (PaletteElement element : elements) {
			element.getGraphicalRepresentation().delete();
		}
		_paletteDrawing.getDrawingGraphicalRepresentation().delete();
		elements = null;
	}

	public String getTitle() {
		return title;
	}

	public void addElement(PaletteElement element) {
		elements.add(element);
		// Try to perform some checks and initialization of
		// expecting behaviour for a PaletteElement
		element.getGraphicalRepresentation().setIsFocusable(false);
		element.getGraphicalRepresentation().setIsSelectable(false);
		element.getGraphicalRepresentation().setIsReadOnly(true);
		element.getGraphicalRepresentation().setLocationConstraints(LocationConstraints.UNMOVABLE);
		// element.getGraphicalRepresentation().addToMouseDragControls(mouseDragControl)
	}

	public void removeElement(PaletteElement element) {
		elements.remove(element);
	}

	public DrawingView<PaletteDrawing> getPaletteView() {
		if (_paletteController == null) {
			makePalettePanel();
		}
		return _paletteController.getDrawingView();
	}

	private JScrollPane scrollPane;

	public JScrollPane getPaletteViewInScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getPaletteView(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return scrollPane;
	}

	public PaletteDrawing getPaletteDrawing() {
		return _paletteDrawing;
	}

	protected void makePalettePanel() {
		for (PaletteElement e : elements) {
			e.getGraphicalRepresentation().setValidated(true);
		}
		_paletteController = new DrawingController<PaletteDrawing>(_paletteDrawing);
		for (PaletteElement e : elements) {
			e.getGraphicalRepresentation().notifyObjectHierarchyHasBeenUpdated();
		}
	}

	public class PaletteDrawing implements Drawing<DrawingPalette> {

		private final DrawingGraphicalRepresentation<DrawingPalette> gr;

		private PaletteDrawing() {
			gr = new DrawingGraphicalRepresentation<DrawingPalette>(this, false);
			gr.setWidth(width);
			gr.setHeight(height);
			gr.setDrawWorkingArea(false);
		}

		@Override
		public List<?> getContainedObjects(Object aDrawable) {
			if (aDrawable == getModel()) {
				return elements;
			} else {
				return null;
			}
		}

		@Override
		public Object getContainer(Object aDrawable) {
			if (aDrawable instanceof PaletteElement) {
				return getModel();
			} else {
				return null;
			}
		}

		@Override
		public DrawingGraphicalRepresentation<DrawingPalette> getDrawingGraphicalRepresentation() {
			return gr;
		}

		@Override
		@SuppressWarnings("unchecked")
		public GraphicalRepresentation<?> getGraphicalRepresentation(Object aDrawable) {
			if (aDrawable == getModel()) {
				return getDrawingGraphicalRepresentation();
			}
			if (aDrawable instanceof PaletteElement) {
				return ((PaletteElement) aDrawable).getGraphicalRepresentation();
			}
			return null;
		}

		@Override
		public DrawingPalette getModel() {
			return DrawingPalette.this;
		}

	}

	// Bout de code a rajouter dans les vues

	/*
	this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, new WKFDTListener(this, controller), true){
		@Override
		public synchronized void dragOver(DropTargetDragEvent dtde) {
			super.dragOver(dtde);
			FlexoProcessView.this.getController().paintDraggedNode(FlexoProcessView.this, dtde);
		}
	});*/

	public PaletteDropListener buildPaletteDropListener(JComponent dropContainer, DrawingController controller) {
		return new PaletteDropListener(dropContainer, controller);
	}

	/**
	 * DTListener a listener that tracks the state of the operation
	 * 
	 * @see java.awt.dnd.DropTargetListener
	 * @see java.awt.dnd.DropTarget
	 */
	public class PaletteDropListener implements DropTargetListener {

		private final int acceptableActions = DnDConstants.ACTION_COPY;
		private final JComponent _dropContainer;
		private final DrawingController _controller;

		public PaletteDropListener(JComponent dropContainer, DrawingController controller) {
			super();
			_dropContainer = dropContainer;
			_controller = controller;
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
			if (e.isDataFlavorSupported(PaletteElementTransferable.defaultFlavor())) {
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
			if (e.isLocalTransfer() == true && e.isDataFlavorSupported(PaletteElementTransferable.defaultFlavor())) {
				return PaletteElementTransferable.defaultFlavor();
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
				PaletteElement element = ((TransferedPaletteElement) e.getTransferable().getTransferData(
						PaletteElementTransferable.defaultFlavor())).getPaletteElement();
				if (element == null) {
					return false;
				}
				GraphicalRepresentation<?> focused = getFocusedObject(e);
				if (focused == null) {
					return false;
				}
				return element.acceptDragging(focused);

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
				getController().getDrawingView().updateCapturedDraggedNodeImagePosition(e,
						_controller.getDrawingView().getActivePalette().getPaletteView());
			}
			if (!isDragOk(e)) {
				if (getDragSourceContext() == null) {
					logger.warning("dragSourceContext should NOT be null for " + DrawingPalette.this.getTitle()
							+ Integer.toHexString(DrawingPalette.this.hashCode()) + " of " + DrawingPalette.this.getClass().getName());
				} else {
					getDragSourceContext().setCursor(dropKO);
				}
				e.rejectDrag();
				return;
			}
			if (getDragSourceContext() == null) {
				logger.warning("dragSourceContext should NOT be null");
			} else {
				getDragSourceContext().setCursor(dropOK);
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
			getController().getDrawingView().resetCapturedNode();
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

				if (data instanceof TransferedPaletteElement) {

					try {
						PaletteElement element = ((TransferedPaletteElement) data).getPaletteElement();
						if (element == null) {
							e.rejectDrop();
							return;
						}
						GraphicalRepresentation<?> focused = getFocusedObject(e);
						if (focused == null) {
							e.rejectDrop();
							return;
						}
						// OK, let's got for the drop
						if (element.acceptDragging(focused)) {
							Component targetComponent = e.getDropTargetContext().getComponent();
							Point pt = e.getLocation();
							FGEPoint modelLocation = new FGEPoint();
							if (targetComponent instanceof FGEView) {
								pt = GraphicalRepresentation.convertPoint(((FGEView<?>) targetComponent).getGraphicalRepresentation(), pt,
										focused, ((FGEView<?>) targetComponent).getScale());
								modelLocation.x = pt.x / ((FGEView<?>) targetComponent).getScale();
								modelLocation.y = pt.y / ((FGEView<?>) targetComponent).getScale();
								modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
								modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
							} else {
								modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
								modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
							}
							if (element.elementDragged(focused, modelLocation)) {
								e.acceptDrop(acceptableActions);
								e.dropComplete(true);
								// logger.info("OK, valid drop, proceed");
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
			} finally {
				// Resets the screenshot stored by the editable drawing view (not the palette drawing view).
				getController().getDrawingView().resetCapturedNode();
			}
		}

		private FocusRetriever getFocusRetriever() {
			if (_dropContainer instanceof FGEView) {
				return ((FGEView) _dropContainer).getDrawingView().getFocusRetriever();
			}
			return null;
		}

		private FGEView getFGEView() {
			if (_dropContainer instanceof FGEView) {
				return (FGEView) _dropContainer;
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

	public DrawingController<?> getController() {
		return _controller;
	}

	protected void registerController(DrawingController<?> controller) {
		_controller = controller;
	}

	public void updatePalette() {
		_paletteController.rebuildDrawingView();
	}

	public DragSourceContext getDragSourceContext() {
		return dragSourceContext;
	}

	public void setDragSourceContext(DragSourceContext dragSourceContext) {
		this.dragSourceContext = dragSourceContext;
	}
}
