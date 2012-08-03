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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate.FIBDropTarget;
import org.openflexo.fib.model.ComponentConstraints;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.view.FIBView;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.Cloner;

public class PaletteElement implements FIBDraggable /*implements Transferable*/{
	static final Logger logger = FlexoLogger.getLogger(PaletteElement.class.getPackage().getName());

	private final FIBEditorPalette _palette;
	private FIBComponent modelComponent;
	private FIBComponent representationComponent;
	private FIBView view;

	private DragSource dragSource;
	private DragGestureListener dgListener;
	private DragSourceListener dsListener;
	private int dragAction = DnDConstants.ACTION_COPY;
	private Hashtable<JComponent, DragGestureRecognizer> dgr;

	// private static final DataFlavor DATA_FLAVOR = new DataFlavor(PaletteElement.class, "PaletteElement");

	public PaletteElement(FIBComponent modelComponent, FIBComponent representationComponent, FIBEditorPalette palette) {
		this.modelComponent = modelComponent;
		this.representationComponent = representationComponent;
		_palette = palette;

		int x = Integer.parseInt(representationComponent.getParameter("x"));
		int y = Integer.parseInt(representationComponent.getParameter("y"));

		view = FIBController.makeView(representationComponent, FIBAbstractEditor.LOCALIZATION);

		Dimension size = view.getJComponent().getPreferredSize();
		view.getJComponent().setBounds(x, y, size.width, size.height);

		this.dgListener = new DGListener();
		this.dragSource = DragSource.getDefaultDragSource();
		this.dsListener = new DSListener();

		recursivelyRemoveFocusableProperty(view.getJComponent());

		dgr = new Hashtable<JComponent, DragGestureRecognizer>();

		// component, action, listener
		// dgr = dragSource.createDefaultDragGestureRecognizer(view.getJComponent(), dragAction, dgListener);
		recursivelyAddDGR(view.getJComponent());

		enableDragging();

	}

	private void recursivelyRemoveFocusableProperty(JComponent c) {
		c.setRequestFocusEnabled(false);
		c.setFocusable(false);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (c2 instanceof JComponent) {
					recursivelyRemoveFocusableProperty((JComponent) c2);
				}
			}
		}
	}

	private void recursivelyAddDGR(JComponent c) {
		DragGestureRecognizer newDGR = dragSource.createDefaultDragGestureRecognizer(c, dragAction, dgListener);
		dgr.put(c, newDGR);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (c2 instanceof JComponent) {
					recursivelyAddDGR((JComponent) c2);
				}
			}
		}
	}

	public FIBView getView() {
		return view;
	}

	@Override
	public void enableDragging() {
		for (JComponent j : dgr.keySet()) {
			dgr.get(j).setComponent(j);
		}
	}

	@Override
	public void disableDragging() {
		for (JComponent j : dgr.keySet()) {
			dgr.get(j).setComponent(null);
		}
	}

	@Override
	public boolean acceptDragging(FIBDropTarget target) {
		logger.fine("acceptDragging ? for component: " + target.getFIBComponent() + " place holder: " + target.getPlaceHolder());
		return true;
	}

	/*@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DATA_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return true;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return this;
	}*/

	@Override
	public boolean elementDragged(FIBDropTarget target, Point pt) {
		logger.info("Element dragged with component: " + target.getFIBComponent() + " place holder: " + target.getPlaceHolder());

		if (target.getPlaceHolder() == null) {
			boolean deleteIt = JOptionPane.showConfirmDialog(_palette.getEditorController().getEditor().getFrame(),
					target.getFIBComponent() + ": really delete this component (undoable operation) ?", "information",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
			if (!deleteIt) {
				return false;
			}
		}

		FIBComponent newComponent = (FIBComponent) Cloner.cloneObjectWithMapping(modelComponent, FIBLibrary.getFIBMapping());
		newComponent.setLocalizedDictionary(null);
		newComponent.clearParameters();

		try {
			if (target.getPlaceHolder() != null) {
				target.getPlaceHolder().willDelete();
				target.getPlaceHolder().insertComponent(newComponent);
				target.getPlaceHolder().hasDeleted();
				return true;
			}

			else {
				FIBComponent targetComponent = target.getFIBComponent();
				FIBContainer containerComponent = targetComponent.getParent();

				if (containerComponent == null)
					return false;

				if (targetComponent instanceof FIBTab && !(newComponent instanceof FIBPanel))
					return false;

				if (targetComponent.getParent() instanceof FIBTabPanel && newComponent instanceof FIBPanel) {
					// Special case where a new tab is added to a FIBTabPanel
					FIBTab newTabComponent = new FIBTab();
					newTabComponent.setTitle("NewTab");
					newTabComponent.setIndex(((FIBTabPanel) targetComponent.getParent()).getSubComponents().size());
					((FIBTabPanel) targetComponent.getParent()).addToSubComponents(newTabComponent);
					return true;
				} else {
					// Normal case, we replace targetComponent by newComponent
					ComponentConstraints constraints = targetComponent.getConstraints();
					containerComponent.removeFromSubComponentsNoNotification(targetComponent);
					// No notification, we will do it later, to avoid reindexing
					targetComponent.delete();
					containerComponent.addToSubComponents(newComponent, constraints);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("Unexpected exception: " + e);
			return false;
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
			logger.fine("dragGestureRecognized");

			// if the action is ok we go ahead
			// otherwise we punt
			if ((e.getDragAction() & dragAction) == 0)
				return;
			// get the label's text and put it inside a Transferable
			// Transferable transferable = new StringSelection(
			// DragLabel.this.getText() );

			PaletteElementDrag transferable = new PaletteElementDrag(PaletteElement.this, e.getDragOrigin());

			try {
				// initial cursor, transferrable, dsource listener
				e.startDrag(FIBEditorPalette.dropKO, transferable, dsListener);
				FIBEditorPalette.logger.info("Starting drag for " + _palette);
				// getDrawingView().captureDraggedNode(PaletteElementView.this, e);
			} catch (Exception idoe) {
				idoe.printStackTrace();
				FIBEditorPalette.logger.warning("Unexpected exception " + idoe);
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
			if (e.getDragSourceContext().getTransferable() instanceof PaletteElementDrag)
				((PaletteElementDrag) e.getDragSourceContext().getTransferable()).reset();

			// getDrawingView().resetCapturedNode();
			if (e.getDropSuccess() == false) {
				if (FIBEditorPalette.logger.isLoggable(Level.INFO))
					FIBEditorPalette.logger.info("Dropping was not successful");
				return;
			}
			/*
			 * the dropAction should be what the drop target specified in
			 * acceptDrop
			 */
			// this is the action selected by the drop target
			if (e.getDropAction() == DnDConstants.ACTION_MOVE) {
				System.out.println("Coucou, que se passe-t-il par ici ?");
			}

		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragEnter(DragSourceDragEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			// System.out.println("dragEnter() with "+context+" component="+e.getSource());
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
			_palette.getEditorController().setDragSourceContext(e.getDragSourceContext());
			DragSourceContext context = e.getDragSourceContext();
			// System.out.println("dragOver() with "+context+" component="+e.getSource());
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragExit(DragSourceEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			// System.out.println("dragExit() with "+context+" component="+e.getSource());
			// interface
			if (e.getDragSourceContext().getTransferable() instanceof PaletteElementDrag)
				((PaletteElementDrag) e.getDragSourceContext().getTransferable()).reset();
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