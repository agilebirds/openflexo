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
package org.openflexo.fib.editor.controller;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate.FIBDropTarget;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.Focusable;

/**
 * DTListener a listener that tracks the state of the operation
 * 
 * @see java.awt.dnd.DropTargetListener
 * @see java.awt.dnd.DropTarget
 */
public class DropListener implements DropTargetListener {

	static final Logger logger = FlexoLogger.getLogger(DropListener.class.getPackage().getName());

	private int acceptableActions = DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE;

	private final FIBEditableView<?, ?> editableView;

	private final PlaceHolder placeHolder;

	public DropListener(FIBEditableView<?, ?> editableView, PlaceHolder placeHolder) {
		this.editableView = editableView;
		this.placeHolder = placeHolder;
	}

	public Focusable getTargetComponent() {
		return placeHolder != null ? placeHolder : editableView.getDelegate();
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
		if (e.isDataFlavorSupported(ElementDrag.DEFAULT_FLAVOR)) {
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
		if (e.isLocalTransfer() == true && e.isDataFlavorSupported(ElementDrag.DEFAULT_FLAVOR)) {
			return ElementDrag.DEFAULT_FLAVOR;
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
		if (!isDragFlavorSupported(e)) {
			return false;
		}

		int da = e.getDropAction();
		// we're saying that these actions are necessary
		if ((da & acceptableActions) == 0) {
			return false;
		}

		try {
			FIBDraggable element = (FIBDraggable) e.getTransferable().getTransferData(ElementDrag.DEFAULT_FLAVOR);
			if (element == null) {
				return false;
			}
			Object source = e.getSource();
			if (source instanceof FIBDropTarget) {
				return element.acceptDragging((FIBDropTarget) source);
			}
			return false;

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
		if (isDragOk(e)) {
			getContainerDelegate().addToPlaceHolderVisibleRequesters(getTargetComponent());
			getTargetComponent().setFocused(true);
			/* Some explanations required here
			 * What may happen is that making place holders visible will
			 * place current cursor location inside a newly displayed place
			 * holder, and cause a subsequent exitComponent() event to the
			 * current component, and then a big blinking. We test here that
			 * case and ignore following exitComponent()
			 * SGU/ I'm not sure this behaviour is platform independant
			 * please check...
			 * 
			 */
			if (placeHolder == null && editableView.getPlaceHolders() != null) {
				for (PlaceHolder ph2 : editableView.getPlaceHolders()) {
					if (ph2.getBounds().contains(e.getLocation())) {
						getContainerDelegate().addToPlaceHolderVisibleRequesters(ph2);
					}
				}
			}
			e.acceptDrag(e.getDropAction());
		} else {
			e.rejectDrag();
			return;
		}
	}

	/**
	 * continue "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
	 * 
	 * @param e
	 */
	@Override
	public void dragOver(DropTargetDragEvent e) {
		/*
		 * if (isDragFlavorSupported(e))
		 * getController().getDrawingView().paintDraggedNode
		 * (e,_controller.getDrawingView().getActivePalette().getPaletteView());
		 */
		if (isDragOk(e)) {
			e.acceptDrag(e.getDropAction());
		} else {
			e.rejectDrag();
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent e) {
		if (isDragOk(e)) {
			e.acceptDrag(e.getDropAction());
		} else {
			e.rejectDrag();
		}
	}

	@Override
	public void dragExit(DropTargetEvent e) {
		getContainerDelegate().removeFromPlaceHolderVisibleRequesters(getTargetComponent());
		getTargetComponent().setFocused(false);
	}

	/**
	 * perform action from getSourceActions on the transferrable invoke acceptDrop or rejectDrop invoke dropComplete if its a local (same
	 * JVM) transfer, use StringTransferable.localStringFlavor find a match for the flavor check the operation get the transferable
	 * according to the chosen flavor do the transfer
	 * 
	 * @param e
	 */
	@Override
	public void drop(DropTargetDropEvent e) {
		getContainerDelegate().removeFromPlaceHolderVisibleRequesters(getTargetComponent());
		getTargetComponent().setFocused(false);
		try {
			DataFlavor chosen = chooseDropFlavor(e);
			if (chosen == null) {
				e.rejectDrop();
				return;
			}

			// the actions that the source has specified with
			// DragGestureRecognizer
			int sa = e.getSourceActions();

			if ((sa & acceptableActions) == 0) {
				e.rejectDrop();
				return;
			}

			Object data = null;

			try {

				/*
				 * the source listener receives this action in dragDropEnd. if
				 * the action is DnDConstants.ACTION_COPY_OR_MOVE then the
				 * source receives MOVE!
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

			if (data instanceof FIBDraggable) {

				try {
					FIBDraggable element = (FIBDraggable) data;
					if (element == null) {
						e.rejectDrop();
						return;
					}
					Object source = e.getSource();

					// OK, let's got for the drop
					if (source instanceof FIBDropTarget && element.acceptDragging((FIBDropTarget) source)) {
						Point pt = e.getLocation();
						if (element.elementDragged((FIBDropTarget) source, pt)) {
							e.acceptDrop(acceptableActions);
							e.dropComplete(true);
							logger.info("Drop succeeded");
							return;
						}
					}
					e.rejectDrop();
					e.dropComplete(false);
					return;

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
			// getController().getDrawingView().resetCapturedNode();
		}
	}

	public FIBEditableViewDelegate<?, ?> getContainerDelegate() {
		if (!(editableView.getComponent() instanceof FIBContainer)) {
			if (editableView.getParentView() != null && editableView.getParentView() instanceof FIBEditableView) {
				return ((FIBEditableView<?, ?>) editableView.getParentView()).getDelegate();
			}
		}
		return editableView.getDelegate();
	}

}