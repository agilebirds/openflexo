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
package org.openflexo.components.widget;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.tabular.model.AbstractColumn;
import org.openflexo.components.tabular.model.CheckColumn;
import org.openflexo.components.tabularbrowser.TabularBrowserModel;
import org.openflexo.components.widget.MultipleObjectSelector.ObjectSelectabilityDelegate;
import org.openflexo.foundation.FlexoModelObject;

class SelectionTabularBrowserModel<E extends FlexoModelObject> extends TabularBrowserModel {
	public static interface SelectionTabularBrowserModelSelectionListener {
		public void notifySelectionChanged();
	}

	protected static final Logger logger = Logger.getLogger(SelectionTabularBrowserModel.class.getPackage().getName());

	protected SelectionColumn _selectionColumn;

	private Vector<SelectionTabularBrowserModelSelectionListener> _selectionListeners;

	private ObjectSelectabilityDelegate<E> _selectabilityDelegate;

	public SelectionTabularBrowserModel(BrowserConfiguration browserConfiguration, ObjectSelectabilityDelegate<E> selectabilityDelegate) {
		super(
				browserConfiguration,
				" ",
				browserConfiguration instanceof MultipleObjectSelector.TabularBrowserConfiguration ? ((MultipleObjectSelector.TabularBrowserConfiguration) browserConfiguration)
						.getBrowsingColumnWidth() : 200);
		_selectionColumn = new SelectionColumn(25);
		insertColumnAtIndex(_selectionColumn, 0);
		if (browserConfiguration instanceof MultipleObjectSelector.TabularBrowserConfiguration) {
			MultipleObjectSelector.TabularBrowserConfiguration bc = (MultipleObjectSelector.TabularBrowserConfiguration) browserConfiguration;
			for (int i = 0; i < bc.getExtraColumnCount(); i++) {
				addToColumns(bc.getExtraColumnAt(i));
			}
		}
		setRowHeight(20);
		_selectionListeners = new Vector<SelectionTabularBrowserModelSelectionListener>();
		_selectabilityDelegate = selectabilityDelegate;
	}

	public void addToSelectionListeners(SelectionTabularBrowserModelSelectionListener l) {
		_selectionListeners.add(l);
	}

	public void removeFromSelectionListeners(SelectionTabularBrowserModelSelectionListener l) {
		_selectionListeners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, Object el, int col) {
		if (el instanceof BrowserElement) {
			BrowserElement element = (BrowserElement) el;
			AbstractColumn<?, ?> column = columnAt(col);
			if (column == _selectionColumn) {
				((SelectionColumn) column).setBooleanValue(element, (Boolean) aValue);
				return;
			}
		}
		super.setValueAt(aValue, el, col);
	}

	public class SelectionColumn extends CheckColumn<E> {
		private Vector<E> _selection;
		private Hashtable<E, Boolean> _hashSet;

		public SelectionColumn(int defaultWidth) {
			super(" ", defaultWidth);
			_selection = new Vector<E>();
			_hashSet = new Hashtable<E, Boolean>();
		}

		public Vector<E> getSelectedObjects() {
			return _selection;
		}

		public void setSelectedObjects(Vector<E> objects) {
			if (_selection.equals(objects)) {
				// Selection remains the same, abort
				return;
			}
			_selection.clear();
			_hashSet.clear();
			_selection.addAll(objects);
			resetSelection();
			for (FlexoModelObject o : objects) {
				addToSelected(o);
				// focusOn(o);
			}
			if (getModel() != null) {
				getModel().fireTableDataChanged();
			}
			// if (objects != null && objects.size() > 0) fireObjectSelected(objects.firstElement());
			// update();
		}

		@Override
		public Boolean getBooleanValue(E object) {
			if (!isSelectable(object)) {
				return Boolean.FALSE;
			}
			Boolean returned = _hashSet.get(object);
			if (returned == null) {
				returned = new Boolean(_selection.contains(object));
				_hashSet.put(object, returned);
			}
			return returned;
		}

		@Override
		public void setBooleanValue(E object, Boolean aBoolean) {
			if (!isSelectable(object)) {
				return;
			}
			if (getConfiguration() instanceof MultipleObjectSelector.TabularBrowserConfiguration) {
				MultipleObjectSelector.TabularBrowserConfiguration bc = (MultipleObjectSelector.TabularBrowserConfiguration) getConfiguration();
				if (!bc.isSelectable(object)) {
					return;
				}
			}

			boolean oldValue = getBooleanValue(object).booleanValue();
			if (oldValue != aBoolean.booleanValue()) {
				if (aBoolean.booleanValue()) {
					Vector<E> newSelection = new Vector<E>();
					newSelection.addAll(_selection);
					newSelection.add(object);
					_selection = newSelection;
					logger.info("On ajoute a la selection qui devient " + _selection);
					_hashSet.put(object, new Boolean(true));
					fireSelectionChanged();
				} else {
					Vector<E> newSelection = new Vector<E>();
					newSelection.addAll(_selection);
					newSelection.remove(object);
					_selection = newSelection;
					logger.info("On retire de la selection qui devient " + _selection);
					_hashSet.put(object, new Boolean(false));
					fireSelectionChanged();
				}
			}
		}

		public void setBooleanValue(BrowserElement element, Boolean aBoolean) {
			if (!isSelectable(element.getObject())) {
				return;
			}
			boolean oldValue = getBooleanValue((E) element.getObject()).booleanValue();
			if (oldValue != aBoolean.booleanValue()) {
				logger.info("Hop: " + aBoolean + " for " + element);
				setBooleanValue((E) element.getObject(), aBoolean);
				for (Enumeration en = element.children(); en.hasMoreElements();) {
					BrowserElement child = (BrowserElement) en.nextElement();
					setBooleanValue(child, aBoolean);
				}
			}

		}

		@Override
		public boolean isCellEditableFor(FlexoModelObject object) {
			logger.info("isCellEditableFor " + object + " return " + isSelectable(object));
			return isSelectable(object);
		}
	}

	public SelectionColumn getSelectionColumn() {
		return _selectionColumn;
	}

	public void fireSelectionChanged() {
		for (SelectionTabularBrowserModelSelectionListener l : _selectionListeners) {
			l.notifySelectionChanged();
		}
	}

	protected boolean isSelectable(FlexoModelObject object) {
		try {
			E castedObject = (E) object;
			return _selectabilityDelegate.isSelectable(castedObject);
		} catch (ClassCastException e) {
			return false;
		}
	}
}