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
package org.openflexo.ie.view.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.menu.IEMenuBar;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.listener.DoubleClickResponder;
import org.openflexo.ie.view.widget.ButtonPanel;
import org.openflexo.ie.view.widget.DropTabZone;
import org.openflexo.ie.view.widget.IEWidgetView;
import org.openflexo.selection.MouseSelectionManager;
import org.openflexo.selection.SelectionListener;

/**
 * Selection manager dedicated to Interface Editor module
 * 
 * @author sguerin
 */
public class IESelectionManager extends MouseSelectionManager {

	protected static final Logger logger = Logger.getLogger(IESelectionManager.class.getPackage().getName());

	public IESelectionManager(IEController controller) {
		super(controller);
		IEMenuBar menuBar = controller.getEditorMenuBar();
		_clipboard = new IEClipboard(this, menuBar.getEditMenu(controller).copyItem, menuBar.getEditMenu(controller).pasteItem,
				menuBar.getEditMenu(controller).cutItem);
		_contextualMenuManager = new IEContextualMenuManager(this, controller.getEditor());
	}

	@Override
	public void addToSelectionListeners(SelectionListener listener) {
		// TODO Auto-generated method stub
		super.addToSelectionListeners(listener);
	}

	public IEController getIEController() {
		return (IEController) getController();
	}

	@Override
	public boolean performSelectionSelectAll() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("'Select All' not implemented yet in Interface Editor");
		}
		return false;
	}

	@Override
	public void processMouseClicked(JComponent clickedContainer, Point clickedPoint, int clickCount, boolean isShiftDown) {
		if (clickCount == 1) {
			setLastClickedContainer(clickedContainer);
			setLastClickedPoint(clickedPoint);

			if (clickedContainer instanceof IEWidgetView) {
				processSelection(((IEWidgetView) clickedContainer).getModel(), isShiftDown);
			}
			if (clickedContainer instanceof DropTabZone) {
				processSelection(((DropTabZone) clickedContainer).getIEModel(), isShiftDown);
			}
			if (clickedContainer instanceof ButtonPanel) {
				processSelection(((ButtonPanel) clickedContainer).getContainerModel(), isShiftDown);
			}

		} else if ((clickCount == 2) && (clickedContainer instanceof DoubleClickResponder)) {
			((DoubleClickResponder) clickedContainer).performDoubleClick(clickedContainer, clickedPoint, isShiftDown);
		}
	}

	private void processSelection(IEWidget w, boolean isShiftDown) {
		if (w.getIsRootOfPartialComponent()) {
			if (w.getParent() instanceof IEWidget) {
				w = (IEWidget) w.getParent();
			} else {
				return;
			}
		}
		if (w instanceof IESequenceWidget && ((IESequenceWidget) w).isInTD()) {
			w = (IETDWidget) ((IESequenceWidget) w).getParent();
		} else if (w instanceof IESequenceTR && ((IESequenceTR) w).isInHTMLTable()) {
			w = (IEHTMLTableWidget) ((IESequenceTR) w).getParent();
		}
		if (selectionContains(w)) {
			if (isShiftDown) {
				removeFromSelected(w);
			}
		} else {
			if (isShiftDown) {
				addToSelected(w);
			} else {
				resetSelection();
				if (w != null) {
					addToSelected(w);
				}
			}
		}
	}

	@Override
	public boolean performSelectionCut() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("performSelectionCut in " + getClass().getName());
		}
		_clipboard.performSelectionCut(getSelection());
		Enumeration<FlexoModelObject> en = getSelection().elements();
		while (en.hasMoreElements()) {
			FlexoModelObject o = en.nextElement();
			if (o instanceof IETDWidget) {
				IESequenceWidget seq = ((IETDWidget) o).getSequenceWidget();
				seq.removeFromContainer();
				continue;
			}
			if (o instanceof IEWidget) {
				((IEWidget) o).removeFromContainer();
			}
		}
		return true;
	}

	@Override
	public void processMouseEntered(MouseEvent e) {
		if (e.getSource() instanceof IEWidgetView) {
			IEWidgetView p = (IEWidgetView) e.getSource();
			if (!isCurrentlyFocused(p)) {
				if (_focusedPanel != null) {
					removeFocus(_focusedPanel);
				}
				setIsFocused(p);
			}
		}
	}

	@Override
	public void processMouseExited(MouseEvent e) {
		if (e.getSource() instanceof IEWidgetView) {
			IEWidgetView p = (IEWidgetView) e.getSource();
			if (isCurrentlyFocused(p)) {
				removeFocus(p);
			}
		}
	}

	@Override
	public void processMousePressed(MouseEvent e) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("Bounds:" + ((JComponent) e.getSource()).getBounds());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Class:" + e.getSource().getClass().getName());
		}
	}

	@Override
	public void processMouseReleased(MouseEvent e) {
	}

	/**
	 * Returns the root object that can be currently edited
	 * 
	 * @return FlexoModelObject
	 */
	@Override
	public FlexoModelObject getRootFocusedObject() {
		if (getIEController().getCurrentEditedComponent() == null) {
			return null;
		}
		return getIEController().getCurrentEditedComponent().getComponentDefinition();
	}

	@Override
	public FlexoModelObject pasteContextForComponent(JComponent aComponent) {
		if (aComponent instanceof IEWOComponentView) {
			return ((IEWOComponentView) aComponent).getModel();
		} else if (aComponent instanceof IEWidgetView) {
			return ((IEWidgetView) aComponent).getModel();
		}
		return null;
	}

	/**
	 * Add supplied object to current selection
	 * 
	 * @param object
	 *            : the object to add to selection
	 */
	@Override
	protected void internallyAddToSelected(FlexoModelObject object, boolean isNewFocusedObject) {
		if (object instanceof IEWidget && ((IEWidget) object).getWOComponent().getRootSequence() == object) {
			object = ((IEWidget) object).getWOComponent();
		}
		if (object instanceof ComponentDefinition) {
			if (((ComponentDefinition) object).isLoaded()) {
				internallyAddToSelected(((ComponentDefinition) object).getWOComponent(), isNewFocusedObject);
				return;
			}
		}
		super.internallyAddToSelected(object, isNewFocusedObject);
	}

	/**
	 * Remove supplied object from current selection
	 * 
	 * @param object
	 *            : the object to remove from selection
	 */
	@Override
	protected void internallyRemoveFromSelected(FlexoModelObject object) {
		if (object instanceof ComponentDefinition) {
			if (((ComponentDefinition) object).isLoaded()) {
				if (selectionContains(((ComponentDefinition) object).getWOComponent())) {
					super.internallyRemoveFromSelected(((ComponentDefinition) object).getWOComponent());
					return;
				}
			}
		}
		if (object instanceof IEWOComponent) {
			if (selectionContains(((IEWOComponent) object).getComponentDefinition())) {
				super.internallyRemoveFromSelected(((IEWOComponent) object).getComponentDefinition());
				return;
			}
		}
		super.internallyRemoveFromSelected(object);
	}

}
