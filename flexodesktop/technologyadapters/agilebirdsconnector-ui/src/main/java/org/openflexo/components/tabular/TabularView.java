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
package org.openflexo.components.tabular;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.ToggleIconColumn;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionListener;
import org.openflexo.selection.SelectionManager;
import org.openflexo.selection.SelectionSynchronizedComponent;
import org.openflexo.view.controller.AgileBirdsFlexoController;

/**
 * Tabular view representing an AbstractModel
 * 
 * @author sguerin
 * 
 */
public abstract class TabularView extends JPanel implements TableModelListener, ListSelectionListener, SelectionSynchronizedComponent,
		GraphicalFlexoObserver {

	protected final class TableMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			Point p = e.getPoint();
			int col = _table.columnAtPoint(p);
			int row = _table.rowAtPoint(p);
			if (col > -1 && col < _model.getColumnCount() && row > -1 && row < _model.getRowCount()) {

				if (e.getClickCount() == 2) {
					if (_model.isCellEditable(row, col)) {
						if (logger.isLoggable(Level.FINE)) {
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Double-click detected in a editable cell. Do nothing !");
							}
						}
					} else if (row > -1 && row < _model.getRowCount()) {
						if (logger.isLoggable(Level.FINE)) {
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Double-click detected in a NON-editable cell. Select !");
							}
						}
						FlexoObject selectMe = _model.elementAt(row);
						if (_controller.moduleViewForObject(selectMe) != null) {
							_controller.setCurrentEditedObjectAsModuleView(selectMe);
						}
					}
				} else if (e.getClickCount() == 1) {
					if (logger.isLoggable(Level.FINE)) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Simple-click detected !");
						}
					}
					// FlexoObject selectMe = _model.elementAt(row);
					// _focusedObject = selectMe;
					// updateSlaveTabularViews();

					if (_table.getEditingRow() > -1 && _table.getEditingRow() != row) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Change row where edition was started, fire stop editing !");
						}
						TableCellEditor cellEditor = _model.columnAt(col).getCellEditor();
						if (cellEditor != null) {
							cellEditor.stopCellEditing();
							e.consume();
						}
					}
					if (!e.isConsumed()) {
						if (_model.columnAt(col) instanceof ToggleIconColumn) {
							ToggleIconColumn toggleIconColumn = (ToggleIconColumn) _model.columnAt(col);
							toggleIconColumn.toogleValue(row);
						}
					}
				}
			} else {
				if (_table.getEditingRow() > -1 && _table.getEditingRow() != row) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Clicked outside the table, stop cell edition!");
					}
					if (_table.getCellEditor() != null) {
						_table.getCellEditor().stopCellEditing();
					}
					e.consume();
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (!e.isConsumed() && _controller.getSelectionManager() != null) {
				_controller.getSelectionManager().getContextualMenuManager().processMousePressed(e);
			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if (!e.isConsumed() && _controller.getSelectionManager() != null) {
				_controller.getSelectionManager().getContextualMenuManager().processMouseReleased(e);
			}
		}
	}

	protected static final Logger logger = Logger.getLogger(TabularView.class.getPackage().getName());

	protected AgileBirdsFlexoController _controller;

	protected JTable _table;

	protected AbstractModel _model;

	protected ListSelectionModel _listSelectionModel;

	protected Vector<TabularView> _slaveTabularViews;

	protected TabularView _masterTabularView;

	private JScrollPane scrollPane;

	protected Vector<FlexoObject> _selectedObjects;

	protected boolean _selectedObjectsNeedsRecomputing;

	protected boolean selectionHasChanged = false;

	private SelectionListener temporarySelectionListener = new SelectionListener() {

		@Override
		public void fireBeginMultipleSelection() {

		}

		@Override
		public void fireEndMultipleSelection() {

		}

		@Override
		public void fireObjectDeselected(FlexoObject object) {
			selectionHasChanged = true;
		}

		@Override
		public void fireObjectSelected(FlexoObject object) {
			selectionHasChanged = true;
		}

		@Override
		public void fireResetSelection() {
			selectionHasChanged = true;
		}

	};

	public TabularView(AgileBirdsFlexoController controller, AbstractModel model, int visibleRowCount) {
		this(controller, model);
		setVisibleRowCount(visibleRowCount);
	}

	public TabularView(AgileBirdsFlexoController controller, AbstractModel model) {
		super();
		_model = model;
		_controller = controller;
		_slaveTabularViews = new Vector<TabularView>();

		if (model != null) {
			model.addTableModelListener(this);
			if (model.getModel() != null) {
				model.getModel().addObserver(this);
			}
			model.fireTableDataChanged();
		}

		_table = new FlexoJTable(model);
		_table.getTableHeader().setReorderingAllowed(false);
		// _table.setPreferredSize(new Dimension(model.getTotalPreferredWidth(),100));
		_table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		_selectedObjects = new Vector<FlexoObject>();
		_selectedObjectsNeedsRecomputing = false;

		// _table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		initColumnModel();
		/*for (int i = 0; i < model.getColumnCount(); i++) {
		    TableColumn col = _table.getColumnModel().getColumn(i);
		    col.setPreferredWidth(model.getDefaultColumnSize(i));
		    if (model.getColumnResizable(i)) {
		        col.setResizable(true);
		    } else {
		        // L'idee, c'est d'etre vraiment sur ;-) !
		        col.setWidth(model.getDefaultColumnSize(i));
		        col.setMinWidth(model.getDefaultColumnSize(i));
		        col.setMaxWidth(model.getDefaultColumnSize(i));
		        col.setResizable(false);
		    }
		    if (model.columnAt(i).requireCellRenderer()) {
		        col.setCellRenderer(model.columnAt(i).getCellRenderer());
		    }
		     if (model.columnAt(i).requireCellEditor()) {
		        col.setCellEditor(model.columnAt(i).getCellEditor());
		    }
		}*/

		if (model.getRowHeight() > 0) {
			_table.setRowHeight(model.getRowHeight());
		}

		_table.setShowVerticalLines(true);

		_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		_listSelectionModel = _table.getSelectionModel();
		_listSelectionModel.addListSelectionListener(this);

		scrollPane = new JScrollPane(_table);
		setLayout(new BorderLayout());
		add(_table.getTableHeader(), BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		TableMouseListener tableMouseListener = new TableMouseListener();
		_table.addMouseListener(tableMouseListener);
		scrollPane.getViewport().addMouseListener(tableMouseListener);
		_table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				if (!e.isConsumed() && _controller.getSelectionManager() != null) {
					_controller.getSelectionManager().getContextualMenuManager().processMouseMoved(e);
				}
			}
		});
		validate();

	}

	private void initColumnModel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("initColumnModel() with " + _model.getColumnCount() + " columns");
		}
		for (int i = 0; i < _model.getColumnCount(); i++) {
			TableColumn col = _table.getColumnModel().getColumn(i);
			FlexoLocalization.localizedForKey(getModel().columnAt(i).getTitle(), col);
			col.setPreferredWidth(_model.getDefaultColumnSize(i));
			if (_model.getColumnResizable(i)) {
				col.setResizable(true);
			} else {
				// L'idee, c'est d'etre vraiment sur ;-) !
				col.setWidth(_model.getDefaultColumnSize(i));
				col.setMinWidth(_model.getDefaultColumnSize(i));
				col.setMaxWidth(_model.getDefaultColumnSize(i));
				col.setResizable(false);
			}
			if (_model.columnAt(i).requireCellRenderer()) {
				col.setCellRenderer(_model.columnAt(i).getCellRenderer());
			}
			if (_model.columnAt(i).requireCellEditor()) {
				col.setCellEditor(_model.columnAt(i).getCellEditor());
			}
		}
	}

	public AbstractModel getModel() {
		return _model;
	}

	public void setVisibleRowCount(int rows) {
		int height = 0;
		for (int row = 0; row < rows; row++) {
			height += _table.getRowHeight(row);
		}
		_table.setPreferredScrollableViewportSize(new Dimension(_table.getPreferredScrollableViewportSize().width, height));
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// Ignore extra messages.
		if (e.getValueIsAdjusting()) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("valueChanged() ListSelectionEvent=" + e + " ListSelectionModel=" + _listSelectionModel.toString());
		}
		_selectedObjectsNeedsRecomputing = true;

		/* At least one of this item has change */
		int beginIndex = e.getFirstIndex();
		int endIndex = e.getLastIndex();

		Vector<FlexoObject> toBeRemovedFromSelection = new Vector<FlexoObject>();
		Vector<FlexoObject> toBeAddedToSelection = new Vector<FlexoObject>();

		// First remove all selected object that cannot be represented on this view
		/*if (getSelectionManager() != null) {
		    for (Enumeration en=getSelectionManager().getSelection().elements(); en.hasMoreElements();) {
		        FlexoObject next = (FlexoObject)en.nextElement();
		        if (!mayRepresents(next)) {
		            toBeRemovedFromSelection.add(next);
		            if (logger.isLoggable(Level.FINE)) logger.fine("Mark "+next+" for deletion ");
		        }
		    }
		}
		*/
		for (int i = beginIndex; i <= endIndex; i++) {
			if (_listSelectionModel.isSelectedIndex(i) != _controller.getSelectionManager().selectionContains(_model.elementAt(i))) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Selection status for object " + _model.elementAt(i) + " at index " + i + " has changed");
				}
				if (_listSelectionModel.isSelectedIndex(i)) {
					// Change for addition
					toBeAddedToSelection.add(_model.elementAt(i));
				} else {
					// Change for removing
					toBeRemovedFromSelection.add(_model.elementAt(i));
				}
			}
		}

		/*for (Enumeration en=toBeAddedToSelection.elements(); en.hasMoreElements();) {
		    FlexoObject next = (FlexoObject)en.nextElement();
		    getSelectionManager().addToSelected(next);
		}*/
		fireBeginMultipleSelection();
		getSelectionManager().addToSelected(toBeAddedToSelection);
		fireEndMultipleSelection();
		/*
		        for (Enumeration en=toBeRemovedFromSelection.elements(); en.hasMoreElements();) {
		            FlexoObject next = (FlexoObject)en.nextElement();
		            getSelectionManager().removeFromSelected(next);
		        }
		*/
		updateSlaveTabularViews();
		if (getSelectionManager() != null) {
			for (Enumeration en = getSelectionManager().getSelection().elements(); en.hasMoreElements();) {
				FlexoObject next = (FlexoObject) en.nextElement();
				if (!mayRepresents(next)) {
					toBeRemovedFromSelection.add(next);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Mark " + next + " for deletion ");
					}
				}
			}
		}
		getSelectionManager().removeFromSelected(toBeRemovedFromSelection);
	}

	private void updateSlaveTabularViews() {
		Vector<FlexoObject> currentSelection = getSelectedObjects();

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("udpateSlaveTabularViews");
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Current selection: " + currentSelection);
		}
		if (currentSelection.size() == 1) {
			setModelInSlaveTabularViews(currentSelection.firstElement());
		} else {
			setModelInSlaveTabularViews(null);
		}

	}

	private void setModelInSlaveTabularViews(FlexoObject newModel) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setModelInDrivenTableViews with " + newModel);
		}
		if (_slaveTabularViews != null) {
			for (Enumeration<TabularView> en = _slaveTabularViews.elements(); en.hasMoreElements();) {
				TabularView next = en.nextElement();
				next.setModelObject(newModel);
			}
		}
	}

	private void setModelObject(FlexoObject newModel) {
		if (_table.isEditing() && _table.getCellEditor() != null && getModel().getModel() != null && getModel().getModel() != newModel
				&& newModel != null) {
			_table.getCellEditor().stopCellEditing();
		}
		_listSelectionModel.removeListSelectionListener(this);
		getModel().setModel(newModel);
		_listSelectionModel.addListSelectionListener(this);
		_selectedObjectsNeedsRecomputing = true;
	}

	public Vector<FlexoObject> getSelectedObjects() {
		if (_selectedObjectsNeedsRecomputing) {
			_selectedObjects.clear();
			for (int i = 0; i < _model.getRowCount(); i++) {
				if (_listSelectionModel.isSelectedIndex(i)) {
					_selectedObjects.add(_model.elementAt(i));
				}
			}
			_selectedObjectsNeedsRecomputing = false;
		}
		return _selectedObjects;
	}

	public boolean isSelected(Vector<FlexoObject> objectList) {
		return getSelectedObjects().containsAll(objectList);
	}

	public boolean isSelected(FlexoObject object) {
		return getSelectedObjects().contains(object);
	}

	public void selectObject(FlexoObject object) {
		resetSelection();

		// Calling addToSelected does not always work
		if (getSelectionManager() != null) {
			getSelectionManager().addToSelected(object);
		}
	}

	public Vector getObjects() {
		return getSelectedObjects();
	}

	public FlexoObject getObject() {
		return _model.getModel();
	}

	public Vector<TabularView> getSlaveTabularViews() {
		return _slaveTabularViews;
	}

	public void addToSlaveTabularViews(TabularView tabularView) {
		_slaveTabularViews.add(tabularView);
		tabularView.setMasterTabularView(this);
	}

	public void removeFromSlaveTabularViews(TabularView tabularView) {
		_slaveTabularViews.remove(tabularView);
		tabularView.setMasterTabularView(null);
	}

	public TabularView getMasterTabularView() {
		return _masterTabularView;
	}

	public void setMasterTabularView(TabularView tabularView) {
		_masterTabularView = tabularView;
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.view.InspectableObjectView#getInspectedObject()
	 * @see org.openflexo.view.InspectableObjectView#getInspectedObject()
	 */
	public InspectableObject getInspectedObject() {
		if (_model.getModel() instanceof InspectableObject) {
			return (InspectableObject) _model.getModel();
		}
		return null;
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.view.MultipleInspectableObjectView#getInspectedObjects()
	 * @see org.openflexo.view.MultipleInspectableObjectView#getInspectedObjects()
	 */
	public Vector getInspectedObjects() {
		return getObjects();
	}

	protected boolean refreshRequested = false;

	protected void refreshNow() {
		// Before updating the content of the table, we make a copy of the current selection
		Vector<FlexoObject> v = (Vector<FlexoObject>) _controller.getSelectionManager().getSelection().clone();
		selectionHasChanged = false;
		_controller.getSelectionManager().addToSelectionListeners(temporarySelectionListener);
		_model.fireTableDataChanged();
		// After updating the content of the table, we set the selection back to its previous state (content has change but not the
		// selection)
		_controller.getSelectionManager().removeFromSelectionListeners(temporarySelectionListener);
		if (selectionHasChanged) {
			_controller.getSelectionManager().setSelectedObjects(v);// Note that deleted objects are automatically ignored by the SM
		}
		refreshRequested = false;
	}

	/**
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable o, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("update received in TabularView for " + o + " dataModification=" + dataModification);
		}
		synchronized (this) {
			if (refreshRequested) {
				return;
			}
			refreshRequested = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						refreshNow();
					} finally {
						refreshRequested = false;
					}
				}
			});
		}
	}

	/**
	 * Overrides
	 * 
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e instanceof AbstractModel.ModelObjectHasChanged) {
			AbstractModel.ModelObjectHasChanged event = (AbstractModel.ModelObjectHasChanged) e;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Model has changed from " + event.getOldModel() + " to " + event.getNewModel());
			}
			if (event.getOldModel() != null) {
				event.getOldModel().deleteObserver(this);
			}
			if (event.getNewModel() != null) {
				event.getNewModel().addObserver(this);
			}
		} else if (e instanceof AbstractModel.SelectObjectEvent) {
			AbstractModel.SelectObjectEvent event = (AbstractModel.SelectObjectEvent) e;
			selectObject(event.getSelectedObject());
		} else if (e instanceof AbstractModel.RowMoveForObjectEvent) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Reselect object, and then the edited cell");
			}
			AbstractModel.RowMoveForObjectEvent event = (AbstractModel.RowMoveForObjectEvent) e;
			selectObject(event.getEditedObject());
			_table.setEditingColumn(event.getColumn());
			_table.setEditingRow(event.getNewRow());
		} else if (e instanceof AbstractModel.TableStructureHasChanged) {
			_table.setModel(_model);
			_table.createDefaultColumnsFromModel();
			initColumnModel();
		}
	}

	@Override
	public SelectionManager getSelectionManager() {
		return _controller.getSelectionManager();
	}

	@Override
	public Vector<FlexoObject> getSelection() {
		if (getSelectionManager() != null) {
			return getSelectionManager().getSelection();
		}
		return null;
	}

	@Override
	public void resetSelection() {
		if (getSelectionManager() != null) {
			getSelectionManager().resetSelection();
		} else {
			fireResetSelection();
		}
	}

	@Override
	public void addToSelected(FlexoObject object) {
		if (mayRepresents(object)) {
			if (getSelectionManager() != null) {
				getSelectionManager().addToSelected(object);
			} else {
				fireObjectSelected(object);
			}
		}
	}

	@Override
	public void removeFromSelected(FlexoObject object) {
		if (mayRepresents(object)) {
			if (getSelectionManager() != null) {
				getSelectionManager().removeFromSelected(object);
			} else {
				fireObjectDeselected(object);
			}
		}
	}

	@Override
	public void addToSelected(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().addToSelected(objects);
		} else {
			fireBeginMultipleSelection();
			for (Enumeration en = objects.elements(); en.hasMoreElements();) {
				FlexoObject next = (FlexoObject) en.nextElement();
				fireObjectSelected(next);
			}
			fireEndMultipleSelection();
		}
	}

	@Override
	public void removeFromSelected(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().removeFromSelected(objects);
		} else {
			fireBeginMultipleSelection();
			for (Enumeration en = objects.elements(); en.hasMoreElements();) {
				FlexoObject next = (FlexoObject) en.nextElement();
				fireObjectDeselected(next);
			}
			fireEndMultipleSelection();
		}
	}

	@Override
	public void setSelectedObjects(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().setSelectedObjects(objects);
		} else {
			resetSelection();
			addToSelected(objects);
		}
	}

	@Override
	public boolean mayRepresents(FlexoObject anObject) {
		boolean b = getModel().indexOf(anObject) > -1;
		Enumeration<TabularView> en = _slaveTabularViews.elements();
		while (en.hasMoreElements() && !b) {
			TabularView v = en.nextElement();
			b |= v.mayRepresents(anObject);
		}
		return b;
	}

	@Override
	public FlexoObject getFocusedObject() {
		if (getSelectionManager() != null) {
			return getSelectionManager().getFocusedObject();
		}
		return null;
	}

	/**
	 * ATTENTION ICI: manipuler ce code avec precaution !!!
	 */
	@Override
	public void fireObjectSelected(FlexoObject object) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("TabularView for " + getObject() + " fireObjectSelected() with " + object);
		}
		FlexoObject parent = getParentObject(object);
		if (getMasterTabularView() != null && parent != null) {
			// If master tabular view not null (means that is is a slave
			// tabular view), and if current selection has no object selected
			// in master view, then select object in master view
			if (getMasterTabularView().getSelectedObjects().size() == 0) {
				getMasterTabularView().fireObjectSelected(parent);
			}

		}
		if (getModel().indexOf(object) > -1) {
			if (isSelected(object) == false) {
				// Change the selection status of b
				int index = _model.indexOf(object);
				_listSelectionModel.removeListSelectionListener(this);
				_listSelectionModel.addSelectionInterval(index, index);
				_listSelectionModel.addListSelectionListener(this);
				_selectedObjectsNeedsRecomputing = true;
				updateSlaveTabularViews();
			}
		}
	}

	/**
	 * ATTENTION ICI: manipuler ce code avec precaution !!!
	 */
	@Override
	public void fireObjectDeselected(FlexoObject object) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("TabularView for " + getObject() + " fireObjectDeselected() with " + object);
		}
		FlexoObject parent = getParentObject(object);
		if (getMasterTabularView() != null && parent != null) {
			// If master tabular view not null (means that is is a slave
			// tabular view), and if parent of deselected object is selected
			// in parent view but not contained in selection, also deselect
			// parent object from master view
			if (getMasterTabularView().getSelectedObjects().contains(parent) && !getSelectionManager().selectionContains(parent)) {
				getMasterTabularView().fireObjectDeselected(parent);
			}
		}
		if (mayRepresents(object)) {
			if (isSelected(object) == true) {
				// Change the selection status of b
				int index = _model.indexOf(object);
				_listSelectionModel.removeListSelectionListener(this);
				_listSelectionModel.removeSelectionInterval(index, index);
				_listSelectionModel.addListSelectionListener(this);
				_selectedObjectsNeedsRecomputing = true;
				updateSlaveTabularViews();
			}
		}
		if (getSlaveTabularViews().size() > 0 && getSelectedObjects().size() == 0) {
			// If this view is a master view that have slave views
			// and if current selection is now empty, we might check
			// if some slave views needs this master view to be set
			for (Enumeration en = getSlaveTabularViews().elements(); en.hasMoreElements();) {
				TabularView next = (TabularView) en.nextElement();
				next.updateSelection();
			}
		}
	}

	protected abstract FlexoObject getParentObject(FlexoObject object);

	@Override
	public void fireResetSelection() {
		_selectedObjectsNeedsRecomputing = true;
		_listSelectionModel.removeListSelectionListener(this);
		_listSelectionModel.clearSelection();
		_listSelectionModel.addListSelectionListener(this);
	}

	@Override
	public void fireBeginMultipleSelection() {
	}

	@Override
	public void fireEndMultipleSelection() {
	}

	/**
	 * Update selection
	 */
	public void updateSelection() {
		if (getSelectionManager() != null) {
			getSelectionManager().fireUpdateSelection(this);
		}
	}

	public void editCellAt(int col, int row) {
		if (_table != null) {
			_table.editCellAt(row, col);
		}
	}
}
