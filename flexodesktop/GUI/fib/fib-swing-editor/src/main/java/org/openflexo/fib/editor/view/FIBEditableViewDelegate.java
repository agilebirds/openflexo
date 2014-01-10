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
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBMultipleValues;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBSplitPanel;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.container.FIBSplitPanelView;
import org.openflexo.fib.view.container.FIBTabView;
import org.openflexo.fib.view.widget.FIBColorWidget;
import org.openflexo.fib.view.widget.FIBFontWidget;
import org.openflexo.fib.view.widget.FIBMultipleValueWidget;
import org.openflexo.fib.view.widget.FIBNumberWidget;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.Focusable;

public class FIBEditableViewDelegate<M extends FIBComponent, J extends JComponent> implements MouseListener, FocusListener, Focusable {

	static final Logger logger = FlexoLogger.getLogger(FIBEditableViewDelegate.class.getPackage().getName());

	private FIBEditableView<M, J> view;

	private final DragSource dragSource;
	private final MoveDGListener dgListener;
	private final MoveDSListener dsListener;
	private final int dragAction = DnDConstants.ACTION_MOVE;

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
		Enumeration<FIBView<?, ?, ?>> en = getController().getViews();
		while (en.hasMoreElements()) {
			FIBView<?, ?, ?> v = en.nextElement();
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

	private final List<Object> placeHolderVisibleRequesters = new ArrayList<Object>();

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

	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {
		// System.out.println("receivedModelNotifications o=" + o + " dataModification=" + dataModification);

		if (view == null) {
			logger.warning("Received ModelNotifications for null view !!!");
			return;
		}

		if (o instanceof FIBContainer && view instanceof FIBContainerView) {
			if (propertyName.equals(FIBContainer.SUB_COMPONENTS_KEY)) {
				((FIBContainerView) view).updateLayout();
			}
		}

		if (o instanceof FIBSplitPanel && view instanceof FIBSplitPanelView) {
			if (propertyName.equals(FIBSplitPanel.SPLIT_KEY)) {
				((FIBSplitPanelView) view).updateLayout();
			}
		}

		if (o instanceof FIBTab && view instanceof FIBTabView) {
			if (propertyName.equals(FIBTab.TITLE_KEY)) {
				// Arghlll how do we update titles on this.
			}
		}

		if (o instanceof FIBPanel && view instanceof FIBPanelView) {
			if (propertyName.equals(FIBPanel.BORDER_KEY) || propertyName.equals(FIBPanel.BORDER_COLOR_KEY)
					|| propertyName.equals(FIBPanel.BORDER_TITLE_KEY) || propertyName.equals(FIBPanel.BORDER_TOP_KEY)
					|| propertyName.equals(FIBPanel.BORDER_LEFT_KEY) || propertyName.equals(FIBPanel.BORDER_RIGHT_KEY)
					|| propertyName.equals(FIBPanel.BORDER_BOTTOM_KEY) || propertyName.equals(FIBPanel.TITLE_FONT_KEY)
					|| propertyName.equals(FIBPanel.DARK_LEVEL_KEY)) {
				((FIBPanelView) view).updateBorder();
			} else if (propertyName.equals(FIBPanel.LAYOUT_KEY) || propertyName.equals(FIBPanel.FLOW_ALIGNMENT_KEY)
					|| propertyName.equals(FIBPanel.BOX_LAYOUT_AXIS_KEY) || propertyName.equals(FIBPanel.V_GAP_KEY)
					|| propertyName.equals(FIBPanel.H_GAP_KEY) || propertyName.equals(FIBPanel.ROWS_KEY)
					|| propertyName.equals(FIBPanel.COLS_KEY) || propertyName.equals(FIBPanel.PROTECT_CONTENT_KEY)) {
				((FIBPanelView) view).updateLayout();
			}
		}

		if (o instanceof FIBWidget) {
			FIBWidgetView widgetView = (FIBWidgetView) view;
			if (propertyName.equals(FIBWidget.MANAGE_DYNAMIC_MODEL_KEY)) {
				widgetView.getComponent().updateBindingModel();
			} else if (propertyName.equals(FIBWidget.READ_ONLY_KEY)) {
				widgetView.updateEnability();
			}
		}

		if (o instanceof FIBMultipleValues) {
			if (propertyName.equals(FIBMultipleValues.STATIC_LIST_KEY) || propertyName.equals(FIBMultipleValues.LIST_KEY)
					|| propertyName.equals(FIBMultipleValues.ARRAY_KEY)) {
				// view.updateDataObject(view.getDataObject());
				((FIBMultipleValueWidget) view).updateMultipleValues();

			}
		}

		if (o instanceof FIBNumber) {
			if (propertyName.equals(FIBNumber.ALLOWS_NULL_KEY)) {
				((FIBNumberWidget<?>) view).updateCheckboxVisibility();
			} else if (propertyName.equals(FIBNumber.COLUMNS_KEY)) {
				((FIBNumberWidget<?>) view).updateColumns();
			}
		}

		if (o instanceof FIBFont) {
			if (propertyName.equals(FIBFont.ALLOWS_NULL_KEY)) {
				((FIBFontWidget) view).updateCheckboxVisibility();
			}
		}
		if (o instanceof FIBColor) {
			if (propertyName.equals(FIBColor.ALLOWS_NULL_KEY)) {
				((FIBColorWidget) view).updateCheckboxVisibility();
			}
		}

		/*
		 * if (o instanceof FIBContainer) { if (dataModification instanceof FIBAttributeNotification) { FIBAttributeNotification n =
		 * (FIBAttributeNotification) dataModification; if (n.getAttribute() == FIBContainer.Parameters.subComponents && view instanceof
		 * FIBContainerView) { ((FIBContainerView) view).updateLayout(); } } }
		 */

		if (o instanceof FIBComponent) {
			if (propertyName.equals(FIBComponent.CONSTRAINTS_KEY) || propertyName.equals(FIBComponent.WIDTH_KEY)
					|| propertyName.equals(FIBComponent.HEIGHT_KEY) || propertyName.equals(FIBComponent.MIN_WIDTH_KEY)
					|| propertyName.equals(FIBComponent.MIN_HEIGHT_KEY) || propertyName.equals(FIBComponent.MAX_WIDTH_KEY)
					|| propertyName.equals(FIBComponent.MAX_HEIGHT_KEY) || propertyName.equals(FIBComponent.USE_SCROLL_BAR_KEY)
					|| propertyName.equals(FIBComponent.HORIZONTAL_SCROLLBAR_POLICY_KEY)
					|| propertyName.equals(FIBComponent.VERTICAL_SCROLLBAR_POLICY_KEY)) {
				FIBView parentView = view.getParentView();
				FIBEditorController controller = getEditorController();
				if (parentView instanceof FIBContainerView) {
					((FIBContainerView) parentView).updateLayout();
				}
				controller.notifyFocusedAndSelectedObject();
			} else if (propertyName.equals(FIBComponent.DATA_KEY)) {
				((FIBView) view).updateData();
			} else if (propertyName.equals(FIBComponent.FONT_KEY)) {
				((FIBView) view).updateFont();
			} else if (propertyName.equals(FIBComponent.BACKGROUND_COLOR_KEY) || propertyName.equals(FIBComponent.FOREGROUND_COLOR_KEY)
					|| propertyName.equals(FIBComponent.OPAQUE_KEY)) {
				((FIBView) view).updateGraphicalProperties();
				((FIBView) view).getJComponent().revalidate();
				((FIBView) view).getJComponent().repaint();
			}
		}
	}

	public static class FIBDropTarget extends DropTarget {
		private final FIBEditableView editableView;
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
