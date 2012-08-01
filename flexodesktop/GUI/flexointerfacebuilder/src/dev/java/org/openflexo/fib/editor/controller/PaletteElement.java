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
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate.FIBDropTarget;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.ComponentConstraints;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.view.FIBView;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.Cloner;

public class PaletteElement {
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

	protected void enableDragging() {
		for (JComponent j : dgr.keySet()) {
			dgr.get(j).setComponent(j);
		}
	}

	protected void disableDragging() {
		for (JComponent j : dgr.keySet()) {
			dgr.get(j).setComponent(null);
		}
	}

	protected boolean acceptDragging(FIBDropTarget target) {
		logger.fine("acceptDragging ? for component: " + target.getFIBComponent() + " place holder: " + target.getPlaceHolder());
		return true;
	}

	public boolean elementDragged(FIBDropTarget target, Point pt) {
		logger.info("Element dragged with component: " + target.getFIBComponent() + " place holder: " + target.getPlaceHolder());

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
			if (e.getDropAction() == DnDConstants.ACTION_MOVE)
				PaletteElement.this._palette.setName("");

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
			_palette.setDragSourceContext(e.getDragSourceContext());
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

	public static class PaletteElementDrag implements Transferable {
		private FIBEditorController _controller;
		private static DataFlavor _defaultFlavor;

		private PaletteElement _transferedData;

		private Vector<FIBComponent> focusedComponentPath;

		public PaletteElementDrag(PaletteElement element, Point dragOrigin) {
			_transferedData = element;
			focusedComponentPath = new Vector<FIBComponent>();
		}

		public void reset() {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Resetting drag");
			int end = focusedComponentPath.size();
			for (int i = 0; i < end; i++) {
				FIBComponent c2 = focusedComponentPath.remove(focusedComponentPath.size() - 1);
				FIBView v = getController().viewForComponent(c2);
				if (v instanceof FIBEditableView) {
					((FIBEditableView) v).getDelegate().setPlaceHoldersAreVisible(false);
					((FIBEditableView) v).getDelegate().setFocused(false);
				}
			}
		}

		public FIBComponent getCurrentlyFocusedComponent() {
			if (focusedComponentPath.size() > 0)
				return focusedComponentPath.lastElement();
			return null;
		}

		public void enterComponent(FIBComponent c, PlaceHolder ph, Point location) {
			if (getController() == null)
				return;
			if (logger.isLoggable(Level.FINE))
				logger.fine("Drag enter in component " + c + " ph=" + ph);
			Vector<FIBComponent> appendingPath = new Vector<FIBComponent>();
			FIBComponent current = c;
			while (current != null && !focusedComponentPath.contains(current)) {
				appendingPath.insertElementAt(current, 0);
				current = current.getParent();
			}
			for (FIBComponent c2 : appendingPath) {
				focusedComponentPath.add(c2);
				FIBView v = getController().viewForComponent(c2);
				if (v instanceof FIBEditableView) {
					((FIBEditableView) v).getDelegate().setPlaceHoldersAreVisible(true);

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
					if (ph == null && ((FIBEditableView<?, ?>) v).getPlaceHolders() != null) {
						for (PlaceHolder ph2 : ((FIBEditableView<?, ?>) v).getPlaceHolders()) {
							if (ph2.getBounds().contains(location)) {
								temporaryDisable = true;
							}
						}
					}
				}
			}

			if (ph != null)
				ph.setFocused(true);
			else {
				FIBView v = getController().viewForComponent(c);
				if (v instanceof FIBEditableView) {
					((FIBEditableView) v).getDelegate().setFocused(true);
				}
			}
			if (logger.isLoggable(Level.FINE))
				logger.fine("focusedComponentPath=" + focusedComponentPath);
		}

		private boolean temporaryDisable = false;

		public void exitComponent(FIBComponent c, PlaceHolder ph) {
			if (getController() == null)
				return;
			if (logger.isLoggable(Level.FINE))
				logger.fine("Drag exit from component " + c + " ph=" + ph);
			if (temporaryDisable) {
				temporaryDisable = false;
				return;
			}
			if (focusedComponentPath.contains(c)) {
				int index = focusedComponentPath.indexOf(c);
				int end = focusedComponentPath.size();
				for (int i = index; i < end; i++) {
					FIBComponent c2 = focusedComponentPath.remove(focusedComponentPath.size() - 1);
					FIBView v = getController().viewForComponent(c2);
					if (v instanceof FIBEditableView) {
						((FIBEditableView) v).getDelegate().setPlaceHoldersAreVisible(false);
						((FIBEditableView) v).getDelegate().setFocused(false);
					}
				}
			} else {
				// Weird....
			}
			if (ph != null)
				ph.setFocused(false);
			else {
				FIBView v = getController().viewForComponent(c);
				if (v instanceof FIBEditableView) {
					((FIBEditableView) v).getDelegate().setFocused(false);
				}
			}
			if (logger.isLoggable(Level.FINE))
				logger.fine("focusedComponentPath=" + focusedComponentPath);
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
		public PaletteElement getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return _transferedData;
		}

		public static DataFlavor defaultFlavor() {
			if (_defaultFlavor == null) {
				_defaultFlavor = new DataFlavor(PaletteElementDrag.class, "PaletteElement");
			}
			return _defaultFlavor;
		}

		public FIBEditorController getController() {
			return _controller;
		}

		public void setController(FIBEditorController controller) {
			// System.out.println("Setting controller");
			_controller = controller;
		}

	}

}