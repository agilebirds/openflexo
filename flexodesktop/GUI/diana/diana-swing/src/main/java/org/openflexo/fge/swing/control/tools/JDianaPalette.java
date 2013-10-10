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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.FocusRetriever;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.fge.view.FGEView;

/**
 * A DianaPaletteC represents the graphical tool representing a {@link DrawingPalette} (the model)
 * 
 * @author sylvain
 * 
 */
public class JDianaPalette extends DianaPalette<JComponent, SwingViewFactory> {

	private static final Logger logger = Logger.getLogger(JDianaPalette.class.getPackage().getName());

	private JScrollPane scrollPane;

	private DragSourceContext dragSourceContext;

	public JDianaPalette(DrawingPalette palette) {
		super(palette);
	}

	@Override
	protected void updatePalette(DrawingPalette palette) {
		super.updatePalette(palette);
		if (scrollPane != null) {
			scrollPane.setViewportView(getPaletteView());
		}
	}

	public JScrollPane getPaletteViewInScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getPaletteView(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return scrollPane;
	}

	@SuppressWarnings("unchecked")
	public JDrawingView<DrawingPalette> getPaletteView() {
		return (JDrawingView<DrawingPalette>) super.getPaletteView();
	}

	public PaletteDropListener buildPaletteDropListener(JComponent dropContainer, AbstractDianaEditor<?, ?, ?> controller) {
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
		private final AbstractDianaEditor<?, ?, ?> _controller;

		public PaletteDropListener(JComponent dropContainer, AbstractDianaEditor<?, ?, ?> controller) {
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
				DrawingTreeNode<?, ?> focused = getFocusedObject(e);
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
				getDrawingView().updateCapturedDraggedNodeImagePosition(e, getDrawingView().getActivePalette().getPaletteView());
			}
			if (!isDragOk(e)) {
				if (getDragSourceContext() == null) {
					logger.warning("dragSourceContext should NOT be null for " + getPalette().getTitle()
							+ Integer.toHexString(JDianaPalette.this.hashCode()) + " of " + JDianaPalette.this.getClass().getName());
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
			getDrawingView().resetCapturedNode();
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
						DrawingTreeNode<?, ?> focused = getFocusedObject(e);
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
								pt = FGEUtils.convertPoint(((FGEView<?, ?>) targetComponent).getNode(), pt, focused,
										((FGEView<?, ?>) targetComponent).getScale());
								modelLocation.x = pt.x / ((FGEView<?, ?>) targetComponent).getScale();
								modelLocation.y = pt.y / ((FGEView<?, ?>) targetComponent).getScale();
								modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
								modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
							} else {
								modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
								modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
							}
							if (element.elementDragged(focused, modelLocation)) {
								e.acceptDrop(acceptableActions);
								e.dropComplete(true);
								logger.info("OK, valid drop, proceed");
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
				getDrawingView().resetCapturedNode();
			}
		}

		private FocusRetriever getFocusRetriever() {
			if (_dropContainer instanceof FGEView) {
				return getDrawingView().getFocusRetriever();
			}
			return null;
		}

		private FGEView<?, ?> getFGEView() {
			if (_dropContainer instanceof FGEView) {
				return (FGEView<?, ?>) _dropContainer;
			}
			return null;
		}

		public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDragEvent event) {
			if (getFocusRetriever() != null) {
				DrawingTreeNode<?, ?> returned = getFocusRetriever().getFocusedObject(event);
				if (returned == null) {
					// Since we are in a FGEView, a null value indicates that we are on the Drawing view
					return getFGEView().getDrawingView().getDrawing().getRoot();
				}
				return returned;
			}
			// No focus retriever: we are not in a FGEView....
			return null;
		}

		public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDropEvent event) {
			if (getFocusRetriever() != null) {
				DrawingTreeNode<?, ?> returned = getFocusRetriever().getFocusedObject(event);
				if (returned == null) {
					// Since we are in a FGEView, a null value indicates that we are on the Drawing view
					return getFGEView().getDrawingView().getDrawing().getRoot();
				}
				return returned;
			}
			// No focus retriever: we are not in a FGEView....
			return null;
		}

	}

	public void updatePalette() {
		super.updatePalette();
		if (scrollPane != null) {
			scrollPane.setViewportView(getPaletteView());
		}
	}

	@Override
	public JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) super.getDrawingView();
	}

	public DragSourceContext getDragSourceContext() {
		return dragSourceContext;
	}

	public void setDragSourceContext(DragSourceContext dragSourceContext) {
		this.dragSourceContext = dragSourceContext;
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
