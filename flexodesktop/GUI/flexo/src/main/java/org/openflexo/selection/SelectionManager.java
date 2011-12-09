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
package org.openflexo.selection;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.palette.PalettePanel;

/**
 * Abstract selection manager: there is one SelectionManager for each
 * FlexoModule. This manager is responsible of:
 * <UL>
 * <LI>Selection management: it holds a list of all currently selected objects,
 * as FlexoModelObject instances</LI>
 * <LI>Synchonization of all components synchronized with itself objects, as
 * FlexoModelObject instances</LI>
 * <LI>Focused and last selected objects management</LI>
 * <LI>Inspection management</LI>
 * <LI>Copy/Cut/Paste/SelectAll management by storing and managing a
 * {@link org.openflexo.selection.FlexoClipboard}</LI>
 * </UL>
 * 
 * @author bmangez, sguerin
 */
public abstract class SelectionManager extends Observable {

	private static final Logger logger = Logger
			.getLogger(SelectionManager.class.getPackage().getName());

	protected FlexoClipboard _clipboard;

	private InspectableObject _inspectedObject;

	protected FocusableView _focusedPanel;

	private FlexoModelObject lastSelectedObject;

	private final FlexoController _controller;

	protected ContextualMenuManager _contextualMenuManager;

	/**
	 * This represents the selection, as a Vector of FlexoModelObject
	 */
	private final Vector<FlexoModelObject> _selection;

	/**
	 * This represents all registered SelectionListener instances
	 */
	private final Vector<SelectionListener> _selectionListeners;

	private final Hashtable<String, Object> inspectionContext;

	public static final String MODULE_KEY = "MODULE";

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public SelectionManager(FlexoController controller) {
		super();
		_controller = controller;
		_selection = new Vector<FlexoModelObject>();
		_selectionListeners = new Vector<SelectionListener>();

		inspectionContext = new Hashtable<String, Object>();
		inspectionContext.put(MODULE_KEY, getController().getModule()
				.getModule().getClassName());
	}

	public FlexoController getController() {
		return _controller;
	}

	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}

	@Override
	public void addObserver(Observer o) {
		super.addObserver(o);
		// (new Exception("ici")).printStackTrace();
	}

	// ==========================================================================
	// =================== Selection Management, public A.P.I
	// ===================
	// ==========================================================================

	public void addToSelectionListeners(SelectionListener listener) {
		if (!_selectionListeners.contains(listener)) {
			_selectionListeners.add(listener);
		}
	}

	public void removeFromSelectionListeners(SelectionListener listener) {
		_selectionListeners.remove(listener);
	}

	public void addToSelectionListeners(List<SelectionListener> listeners) {
		for (SelectionListener listener : listeners) {
			addToSelectionListeners(listener);
		}
	}

	public void removeFromSelectionListeners(List<SelectionListener> listeners) {
		_selectionListeners.removeAll(listeners);
	}

	public int getSelectionListenersCount() {
		return _selectionListeners.size();
	}

	public void debugSelectionListeners() {
		for (SelectionListener sl : _selectionListeners) {
			System.out.println(" * " + sl);
		}
	}

	public int getSelectionSize() {
		return getSelection().size();
	}

	/**
	 * Return the current selection, as a Vector of FlexoModelObject
	 * 
	 * @return a Vector of FlexoModelObject
	 */
	public Vector<FlexoModelObject> getSelection() {
		return _selection;
	}

	/**
	 * Returns boolean indicating if current selection contains supplied object
	 * 
	 * @param object
	 * @return a boolean
	 */
	public boolean selectionContains(FlexoModelObject object) {
		return _selection.contains(object);
	}

	/**
	 * Set supplied object to be the current selection
	 * 
	 * @param object
	 *            : the object to add to selection
	 */
	public void setSelectedObject(FlexoModelObject object) {
		resetSelection();
		addToSelected(object);
	}

	/**
	 * Reset selection
	 */
	public void resetSelection() {
		_selection.clear();
		for (Enumeration<SelectionListener> e = _selectionListeners.elements(); e
				.hasMoreElements();) {
			e.nextElement().fireResetSelection();
		}
		setLastSelectedObject(null);
		fireSelectionBecomesEmpty();
		updateInspectorManagement();
	}

	/**
	 * Add supplied object to current selection
	 * 
	 * @param object
	 *            : the object to add to selection
	 */
	public void addToSelected(FlexoModelObject object) {
		internallyAddToSelected(object, true);
		updateInspectorManagement();
	}

	/**
	 * Remove supplied object from current selection
	 * 
	 * @param object
	 *            : the object to remove from selection
	 */
	public void removeFromSelected(FlexoModelObject object) {
		internallyRemoveFromSelected(object);
		updateInspectorManagement();
	}

	/**
	 * Add supplied object to current selection
	 * 
	 * @param object
	 *            : the object to add to selection
	 */
	public void updateSelectionForMaster(SelectionSynchronizedComponent master) {
		for (Enumeration<FlexoModelObject> en = getSelection().elements(); en
				.hasMoreElements();) {
			FlexoModelObject obj = en.nextElement();
			if (!master.mayRepresents(obj)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Removing " + obj
							+ " because master view is not able to show it");
				}
				internallyRemoveFromSelected(obj);
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Keeping " + obj
							+ " because master view is able to show it");
				}
			}
		}
		updateInspectorManagement();
	}

	/**
	 * Add supplied objects to current selection
	 * 
	 * @param objects
	 *            : objects to add to selection, as a Vector of FlexoModelObject
	 */
	public void addToSelected(Vector<? extends FlexoModelObject> objects) {
		if (objects == null || objects.isEmpty()) {
			return;
		}
		for (Enumeration<SelectionListener> e = _selectionListeners.elements(); e
				.hasMoreElements();) {
			SelectionListener sl = e.nextElement();
			sl.fireBeginMultipleSelection();
		}
		for (Enumeration<? extends FlexoModelObject> en = objects.elements(); en
				.hasMoreElements();) {
			FlexoModelObject nextObj = en.nextElement();
			internallyAddToSelected(nextObj, objects.size() == 1);
		}
		for (Enumeration<SelectionListener> e = _selectionListeners.elements(); e
				.hasMoreElements();) {
			SelectionListener sl = e.nextElement();
			sl.fireEndMultipleSelection();
		}
		updateInspectorManagement();
	}

	/**
	 * Remove supplied objects from current selection
	 * 
	 * @param objects
	 *            : objects to remove from selection, as a Vector of
	 *            FlexoModelObject
	 */
	public void removeFromSelected(Vector<? extends FlexoModelObject> objects) {
		if (objects == null || objects.isEmpty()) {
			return;
		}
		for (Enumeration<SelectionListener> e = _selectionListeners.elements(); e
				.hasMoreElements();) {
			SelectionListener sl = e.nextElement();
			sl.fireBeginMultipleSelection();
		}
		internallyRemoveFromSelected(objects);
		for (Enumeration<SelectionListener> e = _selectionListeners.elements(); e
				.hasMoreElements();) {
			SelectionListener sl = e.nextElement();
			sl.fireEndMultipleSelection();
		}
		updateInspectorManagement();
	}

	/**
	 * Sets supplied vector of FlexoModelObjects to be the current Selection
	 * 
	 * @param objects
	 *            : the object to set for current selection, as a Vector of
	 *            FlexoModelObject
	 */
	public void setSelectedObjects(Vector<? extends FlexoModelObject> objects) {
		resetSelection();
		addToSelected(objects);
	}

	private FlexoModelObject _focusedObject;

	/**
	 * Return currently focused object
	 */
	public FlexoModelObject getFocusedObject() {
		return _focusedObject;
	}

	/**
	 * Sets currently focused object
	 */
	protected void setFocusedObject(FlexoModelObject focusedObject) {
		_focusedObject = focusedObject;
	}

	public FlexoModelObject getLastSelectedObject() {
		return lastSelectedObject;
	}

	public void setLastSelectedObject(FlexoModelObject lastSelectedObject) {
		this.lastSelectedObject = lastSelectedObject;
	}

	/**
	 * Request to all registered SelectionListener of this SelectionManager to
	 * be back-synchronized with current selection
	 */
	public void fireUpdateSelection() {
		for (Enumeration<SelectionListener> e = _selectionListeners.elements(); e
				.hasMoreElements();) {
			SelectionListener sl = e.nextElement();
			fireUpdateSelection(sl);
		}
	}

	/**
	 * Request to supplied SelectionListener of this SelectionManager to be
	 * back-synchronized with current selection
	 */
	public void fireUpdateSelection(
			SelectionListener selectionListenerToSynchronize) {
		selectionListenerToSynchronize.fireResetSelection();
		for (Enumeration<FlexoModelObject> e = getSelection().elements(); e
				.hasMoreElements();) {
			FlexoModelObject o = e.nextElement();
			if (!o.isDeleted()) {
				selectionListenerToSynchronize.fireObjectSelected(o);
			}
		}

	}

	// ==========================================================================
	// ================ Selection Management, protected A.P.I
	// ===================
	// ==========================================================================

	/**
	 * Provides a hook called when selection becomes empty
	 * 
	 */
	protected void fireSelectionBecomesEmpty() {
		_clipboard.setCopyEnabled(false);
		_clipboard.setCutEnabled(false);
	}

	/**
	 * Provides a hook called when selection is no more empty
	 * 
	 */
	protected void fireSelectionIsNoMoreEmpty() {
		_clipboard.setCopyEnabled(true);
		_clipboard.setCutEnabled(true);
	}

	/**
	 * Add supplied object to current selection
	 * 
	 * @param object
	 *            : the object to add to selection
	 */
	protected void internallyAddToSelected(FlexoModelObject object) {
		internallyAddToSelected(object, true);
	}

	/**
	 * Add supplied object to current selection
	 * 
	 * @param isNewFocusedObject
	 *            TODO
	 * 
	 * @param object
	 *            : the object to add to selection
	 */
	protected void internallyAddToSelected(FlexoModelObject object,
			boolean isNewFocusedObject) {
		if (!isSelectable(object)) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("internallyAddToSelected with " + object);
		}
		boolean selectionWasEmpty = (getSelectionSize() == 0);
		if (!selectionContains(object)) {
			_selection.add(object);
			for (Enumeration<SelectionListener> e = _selectionListeners
					.elements(); e.hasMoreElements();) {
				SelectionListener sl = e.nextElement();
				sl.fireObjectSelected(object);
			}
			if (selectionWasEmpty && getSelectionSize() > 0) {
				fireSelectionIsNoMoreEmpty();
			}
		}
		if (isNewFocusedObject) {
			setLastSelectedObject(object);
			setFocusedObject(object);
		}
	}

	/**
	 * Remove supplied object from current selection
	 * 
	 * @param object
	 *            : the object to remove from selection
	 */
	protected void internallyRemoveFromSelected(FlexoModelObject object) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("internallyRemoveFromSelected with " + object);
		}
		if (_focusedObject == object) {
			_focusedObject = null;
		}
		if (_inspectedObject == object) {
			_inspectedObject = null;
		}
		if (lastSelectedObject == object) {
			lastSelectedObject = null;
		}
		if (selectionContains(object)) {
			_selection.remove(object);
			for (Enumeration<SelectionListener> e = _selectionListeners
					.elements(); e.hasMoreElements();) {
				SelectionListener sl = e.nextElement();
				sl.fireObjectDeselected(object);
			}
			if (getSelectionSize() == 0) {
				fireSelectionBecomesEmpty();
			}
		}
		fireUpdateSelection();
	}

	/**
	 * Remove supplied objects from current selection
	 * 
	 * @param objects
	 *            : the objects to remove from selection
	 */
	protected void internallyRemoveFromSelected(
			Vector<? extends FlexoModelObject> objects) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("internallyRemoveFromSelected with " + objects);
		}
		for (FlexoModelObject object : objects) {
			if (_focusedObject == object) {
				_focusedObject = null;
			}
			if (_inspectedObject == object) {
				_inspectedObject = null;
			}
			if (lastSelectedObject == object) {
				lastSelectedObject = null;
			}
			if (selectionContains(object)) {
				_selection.remove(object);
				for (Enumeration<SelectionListener> e = _selectionListeners
						.elements(); e.hasMoreElements();) {
					SelectionListener sl = e.nextElement();
					sl.fireObjectDeselected(object);
				}
			}
		}
		if (getSelectionSize() == 0) {
			fireSelectionBecomesEmpty();
		}
		fireUpdateSelection();
	}

	@Override
	public String toString() {
		String returned = super.toString() + "\n";
		for (Enumeration<FlexoModelObject> en = _selection.elements(); en
				.hasMoreElements();) {
			FlexoModelObject o = en.nextElement();
			StringTokenizer st = new StringTokenizer(o.getClass().getName(),
					".");
			String className = "";
			while (st.hasMoreTokens()) {
				className = st.nextToken();
			}
			returned += "> " + className + ":" + o + "\n";
		}
		return returned;
	}

	// ==========================================================================
	// ============================= Cut&Paste Management
	// =======================
	// ==========================================================================

	public boolean performSelectionCopy() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("performSelectionCopy in " + getClass().getName());
		}
		return _clipboard.performSelectionCopy(getSelection());
	}

	public boolean performSelectionPaste() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("performSelectionPaste in " + getClass().getName());
		}
		return _clipboard.performSelectionPaste();
	}

	public boolean performSelectionCut() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Perform selection cut is not implemented");
		}
		return false;
	}

	public boolean hasCopiedData() {
		return _clipboard.hasCopiedData();
	}

	public abstract boolean performSelectionSelectAll();

	public abstract FlexoModelObject getPasteContext();

	public PastingGraphicalContext getPastingGraphicalContext() {
		return null;
	}

	// ============================================================
	// ======================= Inspector ==========================
	// ============================================================

	@Deprecated
	public Hashtable<String, Object> getInspectionContext() {
		return inspectionContext;
	}

	@Deprecated
	public Object setInspectionContext(String key, Object value) {
		Object returned = inspectionContext.get(key);
		inspectionContext.put(key, value);
		return returned;
	}

	@Deprecated
	public Object removeInspectionContext(String key) {
		Object returned = inspectionContext.get(key);
		inspectionContext.remove(key);
		return returned;
	}

	/**
	 * override this method in your Module's selectionManager if you want to
	 * specify another inspector for an object.
	 */
	/*
	 * protected String getInspectorNameForObject(InspectableObject inspectable)
	 * { return null; }
	 */

	private void setCurrentInspectedObject(InspectableObject inspectable) {
		// logger.info("Inspect "+inspectable);
		if (!isInspectable(inspectable)) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setCurrentInspectedObject: " + countObservers()
					+ " observers");
		}
		if (_inspectedObject != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Inspected object was: "
						+ _inspectedObject.getClass().getName()
						+ " with "
						+ ((FlexoModelObject) _inspectedObject)
								.countObservers() + " observers");
			}
		}
		if (inspectable != null) {
			if (_inspectedObject == null
					|| !_inspectedObject.equals(inspectable)) {
				_inspectedObject = inspectable;
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Inspected object is now: "
							+ _inspectedObject.getClass().getName()
							+ " with "
							+ ((FlexoModelObject) _inspectedObject)
									.countObservers() + " observers");
				}
				setChanged();
				// Component focusOwner =
				// KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				notifyObservers(new UniqueSelection(_inspectedObject,
						getInspectionContext()));
				// this code seems to be the cause of the switch module bug
				// if (focusOwner != null) {
				// focusOwner.requestFocusInWindow();
				// focusOwner.requestFocus();
				// }
			}
		}
		if (inspectable instanceof FlexoModelObject) {
			setFocusedObject((FlexoModelObject) inspectable);
		}
	}

	private void setCurrentInspectedObjectToNone() {
		_inspectedObject = null;
		setChanged();
		notifyObservers(new EmptySelection());
		// InspectorController._inspectorWindow.toFront();
	}

	private void setCurrentInspectedObjectToMultiple() {
		_inspectedObject = null;
		setChanged();
		notifyObservers(new MultipleSelection());
		// InspectorController._inspectorWindow.toFront();
	}

	// Note that the updateInspectorManagement method is quite expensive in CPU
	private void updateInspectorManagement() {
		if (getSelectionSize() == 0) {
			setCurrentInspectedObjectToNone();
		} else if (getSelectionSize() == 1) {
			FlexoModelObject selection = getSelection().firstElement();
			if (selection instanceof InspectableObject) {
				setCurrentInspectedObject((InspectableObject) selection);
			} else {
				setCurrentInspectedObjectToNone();
			}
		} else if (getSelectionSize() > 1) {
			setCurrentInspectedObjectToMultiple();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Current selection is now: " + toString());
		}
	}

	// ==========================================================================
	// ============================= Deletion
	// ===================================
	// ==========================================================================

	/**
	 * Returns the root object that can be currently edited
	 * 
	 * @return FlexoModelObject
	 */
	public abstract FlexoModelObject getRootFocusedObject();

	protected boolean isInspectable(InspectableObject object) {
		if (!(object instanceof FlexoModelObject)) {
			return true;
		}
		FlexoModelObject obj = (FlexoModelObject) object;
		if (obj.getContext() != null) {
			if (obj.getContext() instanceof PalettePanel) {
				return (((PalettePanel) obj.getContext()).isEdited());
			}
		}
		return true;
	}

	protected boolean isSelectable(FlexoModelObject object) {
		if (object == null) {
			return false;
		}
		if (object.isDeleted()) {
			return false;
		}
		if (object.getContext() != null) {
			if (object.getContext() instanceof PalettePanel) {
				return (((PalettePanel) object.getContext()).isEdited());
			}
		}
		return true;
	}

	public ContextualMenuManager getContextualMenuManager() {
		return _contextualMenuManager;
	}

}
