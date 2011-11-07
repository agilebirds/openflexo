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
package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.openflexo.antar.binding.AbstractBinding.TargetObject;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.controller.FIBTableDynamicModel;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.widget.table.FIBTableActionListener;
import org.openflexo.fib.view.widget.table.FIBTableModel;


/**
 * Widget allowing to display/edit a list of values
 *
 * @author sguerin
 */
public class FIBTableWidget extends FIBWidgetView<FIBTable,JTable,List>
implements TableModelListener, FIBSelectable
{

	private static final Logger logger = Logger.getLogger(FIBTableWidget.class.getPackage().getName());

	private JTable _table;
	private final JPanel _dynamicComponent;
	private final FIBTable _fibTable;
	private FIBTableModel _tableModel;
	// private ListSelectionModel _listSelectionModel;
	private JScrollPane scrollPane;

	public FIBTableWidget(FIBTable fibTable, FIBController controller)
	{
		super(fibTable,controller);
		_fibTable = fibTable;

		_dynamicComponent = new JPanel();
		_dynamicComponent.setOpaque(false);
		_dynamicComponent.setLayout(new BorderLayout());

		buildTable();
	}

	public FIBTableModel getTableModel()
	{
		if (_tableModel == null) {
			_tableModel = new FIBTableModel(_fibTable,this,getController());
		}
		return _tableModel;
	}

	public void setVisibleRowCount(int rows)
	{
		int height = 0;
		for (int row = 0; row < rows; row++) {
			height += _table.getRowHeight(row);
		}
		height+=_table.getTableHeader().getPreferredSize().height;
		int width = 0;
		for (int i = 0; i < getTableModel().getColumnCount(); i++) {
			width+=getTableModel().getDefaultColumnSize(i);
		}
		_dynamicComponent.setMinimumSize(new Dimension(width,height));
		_dynamicComponent.setPreferredSize(new Dimension(width,height));
	}

	/*public JLabel getLabel()
    {
        if (_label == null) {
            _label = new JLabel(_propertyModel.label + " : ", SwingConstants.CENTER);
            _label.setText(FlexoLocalization.localizedForKey(_propertyModel.label, _label));
            _label.setBackground(InspectorCst.BACK_COLOR);
            _label.setFont(DEFAULT_LABEL_FONT);
            if (_propertyModel.help != null && !_propertyModel.help.equals(""))
                _label.setToolTipText(_propertyModel.help);
        }
        return _label;
    }*/

	private static final Vector EMPTY_VECTOR = new Vector();

	@Override
	public synchronized boolean updateWidgetFromModel()
	{
		List valuesBeforeUpdating = getTableModel().getValues();
		Object wasSelected = getSelectedObject();

		boolean returned = false;

		if (notEquals(getValue(), getTableModel().getValues())) {

			returned = true;

			//boolean debug = false;
			//if (getWidget().getName() != null && getWidget().getName().equals("PatternRoleTable")) debug=true;

			//if (debug) System.out.println("valuesBeforeUpdating: "+valuesBeforeUpdating);
			//if (debug) System.out.println("wasSelected: "+wasSelected);

			if (_table.isEditing()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(getComponent().getName() + " - Table is currently editing at col:" + _table.getEditingColumn() + " row:"
							+ _table.getEditingRow());
				}
				_table.getCellEditor().cancelCellEditing();
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(getComponent().getName() + " - Table is NOT currently edited ");
				}
			}

			if (logger.isLoggable(Level.FINE)) {
				logger.fine(getComponent().getName()
						+ " updateWidgetFromModel() with " + getValue()
						+ " dataObject="+getDataObject());
			}


			if (getValue() == null) {
				getTableModel().setValues(EMPTY_VECTOR);
			}
			if (getValue() instanceof List && !getValue().equals(valuesBeforeUpdating)) {
				getTableModel().setValues(getValue());
			}
			getTableModel().setModel(getDataObject());
		}

		// We restore value if and only if we represent same table
		if (equals(getTableModel().getValues(),valuesBeforeUpdating) && wasSelected != null) {
			returned = true;
			setSelectedObject(wasSelected);
		}
		else if (areSameValuesOrderIndifferent(getTableModel().getValues(),valuesBeforeUpdating)) {
			// Same values, only order differs, in this case, still select right object
			returned = true;
			setSelectedObject(wasSelected);
		}
		else {
			if (getComponent().getSelected().isValid()
					&& getComponent().getSelected().getBindingValue(getController()) != null) {
				Object newSelectedObject = getComponent().getSelected().getBindingValue(getController());
				if (returned = notEquals(newSelectedObject, getSelectedObject())) {
					setSelectedObject(newSelectedObject);
				}
			}

			else if (getComponent().getAutoSelectFirstRow()) {
				if (getTableModel().getValues() != null && getTableModel().getValues().size() > 0) {
					returned = true;
					getListSelectionModel().addSelectionInterval(0,0);
					//addToSelection(getTableModel().getValues().get(0));
				}
			}
		}

		return returned;
	}

	public ListSelectionModel getListSelectionModel()
	{
		return _table.getSelectionModel();
	}

	@Override
	public Object getSelectedObject()
	{
		return _tableModel.getSelectedObject();
	}

	@Override
	public Vector<Object> getSelection()
	{
		return _tableModel.getSelection();
	}

	public void setSelectedObject(Object object/*, boolean notify*/)
	{
		if (getValue() == null) {
			return;
		}
		if (object == getSelectedObject()) {
			logger.fine("FIBTableWidget: ignore setSelectedObject");
			return;
		}
		logger.fine("FIBTable: setSelectedObject with object "+object+" current is "+getSelectedObject());
		if (object != null) {
			int index = getValue().indexOf(object);
			if (index > -1) {
				//if (!notify) _table.getSelectionModel().removeListSelectionListener(getTableModel());
				getListSelectionModel().setSelectionInterval(index,index);
				//if (!notify) _table.getSelectionModel().addListSelectionListener(getTableModel());
			}
		}
		else {
			clearSelection();
		}
	}

	public void clearSelection()
	{
		getListSelectionModel().clearSelection();
	}

	@Override
	public synchronized List<TargetObject> getDependingObjects()
	{
		List<TargetObject> returned = super.getDependingObjects();
		appendToDependingObjects(getWidget().getSelected(),returned);
		return returned;
	}


	@Override
	public synchronized boolean updateModelFromWidget()
	{
		return false;
	}

	/**
	 * Implements
	 *
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e)
	{
		if (e instanceof FIBTableModel.ModelObjectHasChanged) {
			FIBTableModel.ModelObjectHasChanged event = (FIBTableModel.ModelObjectHasChanged) e;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Model has changed from " + event.getOldValues() + " to " + event.getNewValues());
			}
		} else if (e instanceof FIBTableModel.RowMoveForObjectEvent) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Reselect object, and then the edited cell");
			}
			FIBTableModel.RowMoveForObjectEvent event = (FIBTableModel.RowMoveForObjectEvent) e;
			getListSelectionModel().removeListSelectionListener(getTableModel());
			getListSelectionModel().addSelectionInterval(event.getNewRow(), event.getNewRow());
			getListSelectionModel().addListSelectionListener(getTableModel());
			_table.setEditingColumn(event.getColumn());
			_table.setEditingRow(event.getNewRow());
		}
	}

	@Override
	public JPanel getJComponent()
	{
		return _dynamicComponent;
	}

	@Override
	public JTable getDynamicJComponent()
	{
		return _table;
	}

	@Override
	public FIBTableDynamicModel createDynamicModel()
	{
		return new FIBTableDynamicModel(null);
	}

	@Override
	public FIBTableDynamicModel getDynamicModel()
	{
		return (FIBTableDynamicModel)super.getDynamicModel();
	}

	@Override
	public void updateLanguage()
	{
		super.updateLanguage();
		updateTable();
		for (FIBTableAction a : getWidget().getActions()) {
			if (getWidget().getLocalize()) {
				getLocalized(a.getName());
			}
		}
	}

	public void updateTable()
	{
		//logger.info("!!!!!!!! updateTable()");

		deleteTable();

		if (_tableModel != null) {
			_tableModel.removeTableModelListener(this);
			_tableModel.delete();
			_tableModel = null;
		}

		buildTable();

		/*logger.info("!!!!!!!!  getDataObject()="+getDataObject());
		logger.info("!!!!!!!!  getValue()="+getValue());
		logger.info("!!!!!!!!  getDynamicModel().data="+getDynamicModel().data);
		logger.info("!!!!!!!!  getComponent().getData()="+getComponent().getData());*/

		updateDataObject(getDataObject());
	}

	private void deleteTable()
	{
		if (_table != null) {
			_table.removeFocusListener(this);
		}
		if (getListSelectionModel() != null) {
			getListSelectionModel().removeListSelectionListener(getTableModel());
		}
		if (scrollPane != null && _fibTable.getCreateNewRowOnClick()) {
			for (MouseListener l : scrollPane.getMouseListeners()) {
				scrollPane.removeMouseListener(l);
			}
		}
		for (MouseListener l : _table.getMouseListeners()) {
			_table.removeMouseListener(l);
		}
	}

	private void buildTable()
	{
		getTableModel().addTableModelListener(this);

		_table = new JTable(getTableModel());
		_table.setShowHorizontalLines(false);
		_table.setShowVerticalLines(false);
		_table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		_table.addFocusListener(this);

		for (int i = 0; i < getTableModel().getColumnCount(); i++) {
			TableColumn col = _table.getColumnModel().getColumn(i);
			//FlexoLocalization.localizedForKey(getController().getLocalizer(),getTableModel().columnAt(i).getTitle());
			col.setWidth(getTableModel().getDefaultColumnSize(i));
			col.setPreferredWidth(getTableModel().getDefaultColumnSize(i));
			if (getTableModel().getColumnResizable(i)) {
				col.setResizable(true);
			} else {
				// L'idee, c'est d'etre vraiment sur ;-) !
				col.setWidth(getTableModel().getDefaultColumnSize(i));
				col.setMinWidth(getTableModel().getDefaultColumnSize(i));
				col.setMaxWidth(getTableModel().getDefaultColumnSize(i));
				col.setResizable(false);
			}
			if (getTableModel().columnAt(i).requireCellRenderer()) {
				col.setCellRenderer(getTableModel().columnAt(i).getCellRenderer());
			}
			if (getTableModel().columnAt(i).requireCellEditor()) {
				col.setCellEditor(getTableModel().columnAt(i).getCellEditor());
			}
		}

		if (_fibTable.getRowHeight() > 0) {
			_table.setRowHeight(_fibTable.getRowHeight());
		}

		_table.setSelectionMode(_fibTable.getSelectionMode().getMode());
		_table.getTableHeader().setReorderingAllowed(false);

		_table.getSelectionModel().addListSelectionListener(getTableModel());

		//_listSelectionModel = _table.getSelectionModel();
		//_listSelectionModel.addListSelectionListener(this);

		scrollPane = new JScrollPane(_table);

		if (_fibTable.getCreateNewRowOnClick()) {
			scrollPane.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (_table.getCellEditor()!=null) {
						_table.getCellEditor().stopCellEditing();
						e.consume();
					}
					if (_fibTable.getCreateNewRowOnClick()) {
						if (!e.isConsumed() && e.getClickCount()==2) {
							//System.out.println("OK, on essaie de gerer un new par double click");
							Enumeration<FIBTableActionListener> en = getTableModel().getFooter().getAddActionListeners();
							while(en.hasMoreElements()) {
								FIBTableActionListener action = en.nextElement();
								if (action.isAddAction()) {
									action.actionPerformed(null);
									break;
								}
							}
						}
					}
				}
			});
		}
		/*_table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				getController().fireMouseClicked(getDynamicModel(),e.getClickCount());
			}
		});*/

		_dynamicComponent.removeAll();
		_dynamicComponent.add(scrollPane, BorderLayout.CENTER);

		if (_fibTable.getShowFooter()) {
			_dynamicComponent.add(getTableModel().getFooter(), BorderLayout.SOUTH);
		}

		setVisibleRowCount(_fibTable.getVisibleRowCount());
		_dynamicComponent.revalidate();
		_dynamicComponent.repaint();
	}

	@Override
	public boolean synchronizedWithSelection()
	{
		return getWidget().getBoundToSelectionManager();
	}

	public boolean isLastFocusedSelectable()
	{
		return getController().getLastFocusedSelectable() == this;
	}

	@Override
	public boolean mayRepresent(Object o)
	{
		try {
			if (getValue() != null) {
				return getValue().contains(o);
			}
		}
		catch (ClassCastException e) {
			logger.warning("ClassCastException in FIBTableWidget: "+e.getMessage());
		}
		return false;
	}

	@Override
	public void objectAddedToSelection(Object o)
	{
		//logger.info(">>>>>>> objectAddedToSelection "+o);
		getTableModel().addToSelectionNoNotification(o);
	}

	@Override
	public void objectRemovedFromSelection(Object o)
	{
		getTableModel().removeFromSelectionNoNotification(o);
	}

	@Override
	public void selectionResetted()
	{
		getTableModel().resetSelectionNoNotification();
	}

	@Override
	public void addToSelection(Object o)
	{
		//logger.info(">>>>>>> addToSelection "+o);
		getTableModel().addToSelection(o);
	}

	@Override
	public void removeFromSelection(Object o)
	{
		getTableModel().removeFromSelection(o);
	}

	@Override
	public void resetSelection()
	{
		getTableModel().resetSelection();
	}

	private static boolean areSameValuesOrderIndifferent(List l1, List l2)
	{
		if (l1 == null || l2 == null) {
			return false;
		}
		if (l1.size() != l2.size()) {
			return false;
		}
		Comparator comparator = new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return o1.hashCode()-o2.hashCode();
			}
		};
		List sortedL1 = new ArrayList();
		sortedL1.addAll(l1);
		Collections.sort(sortedL1,comparator);
		List sortedL2 = new ArrayList();
		sortedL2.addAll(l2);
		Collections.sort(sortedL2,comparator);
		for (int i=0; i<sortedL1.size(); i++) {
			if (!sortedL1.get(i).equals(sortedL2.get(i))) {
				return false;
			}
		}
		return true;
	}
}
