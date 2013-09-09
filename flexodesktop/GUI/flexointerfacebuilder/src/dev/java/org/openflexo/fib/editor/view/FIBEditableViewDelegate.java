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
package org.openflexo.fib.editor.view;

import java.awt.Component;
import java.awt.Container;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.controller.DraggedFIBComponent;
import org.openflexo.fib.editor.controller.ExistingElementDrag;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.controller.FIBEditorPalette;
import org.openflexo.fib.editor.view.container.FIBEditableSplitPanelView;
import org.openflexo.fib.model.FIBAddingNotification;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBModelNotification;
import org.openflexo.fib.model.FIBMultipleValues;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRemovingNotification;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.fib.view.container.FIBTabView;
import org.openflexo.fib.view.widget.FIBColorWidget;
import org.openflexo.fib.view.widget.FIBFontWidget;
import org.openflexo.fib.view.widget.FIBNumberWidget;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.Focusable;

public class FIBEditableViewDelegate<M extends FIBComponent, J extends JComponent> implements MouseListener, FocusListener, Focusable {

	static final Logger logger = FlexoLogger.getLogger(FIBEditableViewDelegate.class.getPackage().getName());

	private FIBEditableView<M, J> view;

	private DragSource dragSource;
	private MoveDGListener dgListener;
	private MoveDSListener dsListener;
	private int dragAction = DnDConstants.ACTION_MOVE;

	public FIBEditableViewDelegate(FIBEditableView<M, J> view) {
		this.view = view;

		view.getJComponent().setFocusable(true);
		view.getJComponent().setRequestFocusEnabled(true);

		recursivelyAddListenersTo(view.getJComponent());

		// view.getJComponent().addMouseListener(this);
		// view.getJComponent().addFocusListener(this);

		view.getEditorController().registerViewDelegate(this);

		if (view.getPlaceHolders() != null) {
			for (PlaceHolder ph : view.getPlaceHolders()) {
				// Listen to drag'n'drop events
				new FIBDropTarget(ph);
			}
		}

		dragSource = DragSource.getDefaultDragSource();
		dsListener = new MoveDSListener();
		dgListener = new MoveDGListener();

		if (!(view instanceof FIBEditableSplitPanelView)) {
			DragGestureRecognizer newDGR = dragSource.createDefaultDragGestureRecognizer(view.getDynamicJComponent(), dragAction,
					dgListener);
		}

	}

	public void delete() {
		logger.fine("Delete delegate view=" + view);
		recursivelyDeleteListenersFrom(view.getJComponent());
		view.getEditorController().unregisterViewDelegate(this);
		view = null;
	}

	private void recursivelyAddListenersTo(Component c) {
		/*for (MouseListener ml : c.getMouseListeners()) {
			c.removeMouseListener(ml);
		}*/
		c.addMouseListener(this);
		c.addFocusListener(this);
		// Listen to drag'n'drop events
		new FIBDropTarget(view);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (!isComponentRootComponentForAnyFIBView(c2)) {
					recursivelyAddListenersTo(c2);
				}
			}
		}
	}

	private void recursivelyDeleteListenersFrom(Component c) {
		c.removeMouseListener(this);
		c.removeFocusListener(this);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (!isComponentRootComponentForAnyFIBView(c2)) {
					recursivelyDeleteListenersFrom(c2);
				}
			}
		}
	}

	private boolean isComponentRootComponentForAnyFIBView(Component c) {
		Enumeration<FIBView<?, ?>> en = getController().getViews();
		while (en.hasMoreElements()) {
			FIBView<?, ?> v = en.nextElement();
			if (v.getResultingJComponent() == c) {
				return true;
			}
		}
		return false;
	}

	public FIBEditorController getEditorController() {
		return view.getEditorController();
	}

	public FIBController getController() {
		return view.getEditorController().getController();
	}

	public M getFIBComponent() {
		return view.getComponent();
	}

	public JComponent getJComponent() {
		return view.getJComponent();
	}

	@Override
	public boolean isFocused() {
		return getEditorController().getFocusedObject() == getFIBComponent();
	}

	@Override
	public void setFocused(boolean focused) {
		getEditorController().setFocusedObject(getFIBComponent());
	}

	private List<Object> placeHolderVisibleRequesters = new ArrayList<Object>();

	public void addToPlaceHolderVisibleRequesters(Object requester) {
		if (!placeHolderVisibleRequesters.contains(requester)) {
			placeHolderVisibleRequesters.add(requester);
			updatePlaceHoldersVisibility();
		}
	}

	public void removeFromPlaceHolderVisibleRequesters(Object requester) {
		if (placeHolderVisibleRequesters.remove(requester)) {
			updatePlaceHoldersVisibility();
		}
	}

	private boolean updatePlaceHoldersVisibilityRequested = false;

	private void updatePlaceHoldersVisibility() {
		if (updatePlaceHoldersVisibilityRequested) {
			return;
		}
		updatePlaceHoldersVisibilityRequested = true;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (view != null) {
					boolean visible = placeHolderVisibleRequesters.size() > 0;
					if (view.getPlaceHolders() != null) {
						for (PlaceHolder ph : view.getPlaceHolders()) {
							ph.setVisible(visible);
						}
					}
					updatePlaceHoldersVisibilityRequested = false;
				}
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		getEditorController().setSelectedObject(getFIBComponent());
		view.getJComponent().requestFocusInWindow();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
			getEditorController().getContextualMenu().displayPopupMenu(getFIBComponent(), view.getJComponent(), e);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// getController().setSelectedObject(null);
	}

	@Override
	public void focusGained(FocusEvent e) {
		getEditorController().setSelectedObject(getFIBComponent());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		getEditorController().setFocusedObject(getFIBComponent());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		getEditorController().setFocusedObject(null);
	}

	public void receivedModelNotifications(Observable o, FIBModelNotification dataModification) {
		// System.out.println("receivedModelNotifications o=" + o + " dataModification=" + dataModification);

		if (view == null) {
			logger.warning("Received ModelNotifications for null view !!!");
			return;
		}

		if (o instanceof FIBContainer && view instanceof FIBContainerView) {
			if (dataModification instanceof FIBAddingNotification) {
				if (((FIBAddingNotification) dataModification).getAddedValue() instanceof FIBComponent) {
					// addSubComponent((FIBComponent)((FIBAddingNotification)dataModification).getAddedValue());
					((FIBContainerView) view).updateLayout();
				}
			} else if (dataModification instanceof FIBRemovingNotification) {
				if (((FIBRemovingNotification) dataModification).getRemovedValue() instanceof FIBComponent) {
					// addSubComponent((FIBComponent)((FIBAddingNotification)dataModification).getAddedValue());
					((FIBContainerView) view).updateLayout();
				}
			} else if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBContainer.Parameters.subComponents) {
					((FIBContainerView) view).updateLayout();
				}
			}
		}

		if (o instanceof FIBTab && view instanceof FIBTabView) {
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBTab.Parameters.title) {
					if (view.getParentView() instanceof FIBTabPanelView) {
						// Arghlll how do we update titles on this.
					}

				}
			}
		}

		if (o instanceof FIBPanel && view instanceof FIBPanelView) {
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBPanel.Parameters.border || n.getAttribute() == FIBPanel.Parameters.borderColor
						|| n.getAttribute() == FIBPanel.Parameters.borderTitle || n.getAttribute() == FIBPanel.Parameters.borderTop
						|| n.getAttribute() == FIBPanel.Parameters.borderLeft || n.getAttribute() == FIBPanel.Parameters.borderRight
						|| n.getAttribute() == FIBPanel.Parameters.borderBottom || n.getAttribute() == FIBPanel.Parameters.titleFont
						|| n.getAttribute() == FIBPanel.Parameters.darkLevel) {
					((FIBPanelView) view).updateBorder();
				} else if (n.getAttribute() == FIBPanel.Parameters.layout || n.getAttribute() == FIBPanel.Parameters.flowAlignment
						|| n.getAttribute() == FIBPanel.Parameters.boxLayoutAxis || n.getAttribute() == FIBPanel.Parameters.vGap
						|| n.getAttribute() == FIBPanel.Parameters.hGap || n.getAttribute() == FIBPanel.Parameters.rows
						|| n.getAttribute() == FIBPanel.Parameters.cols || n.getAttribute() == FIBPanel.Parameters.protectContent) {
					((FIBPanelView) view).updateLayout();
				}
			}
		}

		if (o instanceof FIBWidget) {
			FIBWidgetView widgetView = (FIBWidgetView) view;
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBWidget.Parameters.manageDynamicModel) {
					widgetView.getComponent().updateBindingModel();
				} else if (n.getAttribute() == FIBWidget.Parameters.readOnly) {
					widgetView.updateEnability();
				}
			}
		}

		if (o instanceof FIBMultipleValues) {
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBMultipleValues.Parameters.staticList || n.getAttribute() == FIBMultipleValues.Parameters.list
						|| n.getAttribute() == FIBMultipleValues.Parameters.array) {
					view.updateDataObject(view.getDataObject());
				}
			}
		}

		if (o instanceof FIBNumber) {
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBNumber.Parameters.allowsNull) {
					((FIBNumberWidget<?>) view).updateCheckboxVisibility();
				} else if (n.getAttribute() == FIBNumber.Parameters.columns) {
					((FIBNumberWidget<?>) view).updateColumns();
				}
			}
		}

		if (o instanceof FIBFont) {
			FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
			if (n.getAttribute() == FIBFont.Parameters.allowsNull) {
				((FIBFontWidget) view).updateCheckboxVisibility();
			}
		}
		if (o instanceof FIBColor) {
			FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
			if (n.getAttribute() == FIBColor.Parameters.allowsNull) {
				((FIBColorWidget) view).updateCheckboxVisibility();
			}
		}

		/*
		 * if (o instanceof FIBContainer) { if (dataModification instanceof FIBAttributeNotification) { FIBAttributeNotification n =
		 * (FIBAttributeNotification) dataModification; if (n.getAttribute() == FIBContainer.Parameters.subComponents && view instanceof
		 * FIBContainerView) { ((FIBContainerView) view).updateLayout(); } } }
		 */

		if (o instanceof FIBComponent) {
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBComponent.Parameters.constraints || n.getAttribute() == FIBComponent.Parameters.width
						|| n.getAttribute() == FIBComponent.Parameters.height || n.getAttribute() == FIBComponent.Parameters.minWidth
						|| n.getAttribute() == FIBComponent.Parameters.minHeight || n.getAttribute() == FIBComponent.Parameters.maxWidth
						|| n.getAttribute() == FIBComponent.Parameters.maxHeight
						|| n.getAttribute() == FIBComponent.Parameters.useScrollBar
						|| n.getAttribute() == FIBComponent.Parameters.horizontalScrollbarPolicy
						|| n.getAttribute() == FIBComponent.Parameters.verticalScrollbarPolicy) {
					FIBView parentView = view.getParentView();
					FIBEditorController controller = getEditorController();
					if (parentView instanceof FIBContainerView) {
						((FIBContainerView) parentView).updateLayout();
					}
					controller.notifyFocusedAndSelectedObject();
				} else if (n.getAttribute() == FIBComponent.Parameters.data) {
					view.updateDataObject(view.getDataObject());
				} else if (n.getAttribute() == FIBComponent.Parameters.font) {
					((FIBView) view).updateFont();
				} else if (n.getAttribute() == FIBComponent.Parameters.backgroundColor
						|| n.getAttribute() == FIBComponent.Parameters.foregroundColor
						|| n.getAttribute() == FIBComponent.Parameters.opaque) {
					((FIBView) view).updateGraphicalProperties();
					((FIBView) view).getJComponent().revalidate();
					((FIBView) view).getJComponent().repaint();
				}
			}
		}
	}

	public static class FIBDropTarget extends DropTarget {
		private FIBEditableView editableView;
		private PlaceHolder placeHolder = null;

		public FIBDropTarget(FIBEditableView editableView) {
			super(editableView.getJComponent(), DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE, editableView.getEditorController()
					.buildPaletteDropListener(editableView, null), true);
			this.editableView = editableView;
			logger.fine("Made FIBDropTarget for " + getFIBComponent());
		}

		public FIBDropTarget(PlaceHolder placeHolder) {
			super(placeHolder, DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE, placeHolder.getView().getEditorController()
					.buildPaletteDropListener(placeHolder.getView(), placeHolder), true);
			this.placeHolder = placeHolder;
			this.editableView = placeHolder.getView();
			logger.fine("Made FIBDropTarget for " + getFIBComponent());
		}

		public PlaceHolder getPlaceHolder() {
			return placeHolder;
		}

		public boolean isPlaceHolder() {
			return placeHolder != null;
		}

		public FIBComponent getFIBComponent() {
			return editableView.getComponent();
		}

		public FIBEditorController getFIBEditorController() {
			return editableView.getEditorController();
		}

	}

	/**
	 * DGListener a listener that will start the drag. has access to top level's dsListener and dragSource
	 * 
	 * @see java.awt.dnd.DragGestureListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection
	 */
	class MoveDGListener implements DragGestureListener {
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
			if ((e.getDragAction() & dragAction) == 0) {
				return;
				// get the label's text and put it inside a Transferable
				// Transferable transferable = new StringSelection(
				// DragLabel.this.getText() );
			}

			ExistingElementDrag transferable = new ExistingElementDrag(new DraggedFIBComponent(view.getComponent()), e.getDragOrigin());

			try {
				// initial cursor, transferrable, dsource listener
				e.startDrag(FIBEditorPalette.dropKO, transferable, dsListener);
				logger.info("Starting existing element drag for " + view.getComponent());
				// getDrawingView().captureDraggedNode(PaletteElementView.this, e);
			} catch (Exception idoe) {
				idoe.printStackTrace();
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
	public class MoveDSListener implements DragSourceListener {

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragDropEnd(DragSourceDropEvent e) {

			// getDrawingView().resetCapturedNode();
			if (!e.getDropSuccess()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Dropping was not successful");
				}
				return;
			}
			/*
			 * the dropAction should be what the drop target specified in acceptDrop
			 */
			// this is the action selected by the drop target
			if (e.getDropAction() == DnDConstants.ACTION_MOVE) {
				System.out.println("Tiens, que se passe-t-il donc ?");
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

		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragExit(DragSourceEvent e) {

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
