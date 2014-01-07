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
package org.openflexo.fib.view.widget.table;

import java.awt.Component;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBDropDownColumn;

public class DropDownColumn<T, V> extends AbstractColumn<T, V> implements EditableColumn<T, V> {
	static final Logger logger = Logger.getLogger(DropDownColumn.class.getPackage().getName());

	private final DropDownCellRenderer _cellRenderer;

	private final DropDownCellEditor _cellEditor;

	public DropDownColumn(FIBDropDownColumn columnModel, FIBTableModel<T> tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
		_cellRenderer = new DropDownCellRenderer();
		_cellEditor = new DropDownCellEditor(new JComboBox());
	}

	@Override
	public FIBDropDownColumn getColumnModel() {
		return (FIBDropDownColumn) super.getColumnModel();
	}

	@Override
	public Class<V> getValueClass() {

		if (getColumnModel().getData() != null && getColumnModel().getData().isValid()) {
			Type analyzedType = getColumnModel().getData().getAnalyzedType();
			return (Class<V>) TypeUtils.getRawType(analyzedType);
		}

		if (getColumnModel().getStaticList() != null) {
			return (Class<V>) String.class;
		}

		else if (getColumnModel().getList() != null && getColumnModel().getList().isValid()) {
			Type analyzedType = getColumnModel().getList().getAnalyzedType();
			if (analyzedType instanceof ParameterizedType) {
				return (Class<V>) TypeUtils.getRawType(((ParameterizedType) analyzedType).getActualTypeArguments()[0]);
			}
		}

		else if (getColumnModel().getArray() != null && getColumnModel().getArray().isValid()) {
			Type analyzedType = getColumnModel().getArray().getAnalyzedType();
			if (analyzedType instanceof GenericArrayType) {
				return (Class<V>) TypeUtils.getRawType(((GenericArrayType) analyzedType).getGenericComponentType());
			}
		}

		logger.warning("Could not determine value class");
		return (Class<V>) Object.class;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	@Override
	public boolean requireCellRenderer() {
		return true;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return _cellRenderer;
	}

	@Override
	public boolean isCellEditableFor(Object object) {
		return true;
	}

	protected class DropDownCellRenderer extends FIBTableCellRenderer<T, V> {
		public DropDownCellRenderer() {
			super(DropDownColumn.this);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (returned instanceof JLabel) {
				((JLabel) returned).setText(renderValue((T) value));
				((JLabel) returned).setFont(getFont());
			}
			return returned;
		}
	}

	protected String renderValue(T value) {
		return getStringRepresentation(value);
	}

	protected List<V> getAvailableValues(T object) {

		if (getColumnModel().getStaticList() != null) {
			Vector<String> list = new Vector<String>();
			StringTokenizer st = new StringTokenizer(getColumnModel().getStaticList(), ",");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
			return (List<V>) list;
		}

		else if (getColumnModel().getList() != null && getColumnModel().getList().isSet()) {

			iteratorObject = object;
			List<V> accessedList = null;
			try {
				accessedList = (List<V>) getColumnModel().getList().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			if (accessedList instanceof List) {
				return (List<V>) accessedList;
			}
		}

		else if (getColumnModel().getArray() != null && getColumnModel().getArray().isSet()) {

			iteratorObject = object;
			V[] accessedArray = null;
			try {
				accessedArray = (V[]) getColumnModel().getArray().getBindingValue(this);
			} catch (TypeMismatchException e1) {
				e1.printStackTrace();
			} catch (NullReferenceException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
			try {
				V[] array = accessedArray;
				List<V> list = new ArrayList<V>();
				for (int i = 0; i < array.length; i++) {
					list.add(array[i]);
				}
				return list;
			} catch (ClassCastException e) {
				logger.warning("ClassCastException " + e.getMessage());
			}
		}

		else if (getColumnModel().getData() != null && getColumnModel().getData().isValid()) {
			Type type = getColumnModel().getData().getAnalyzedType();
			if (type instanceof Class && ((Class<V>) type).isEnum()) {
				V[] array = ((Class<V>) type).getEnumConstants();
				List<V> list = new ArrayList<V>();
				for (int i = 0; i < array.length; i++) {
					list.add(array[i]);
				}
				return list;
			}
		}
		logger.warning("Could not access element list");
		return null;
	}

	@Override
	public boolean requireCellEditor() {
		return true;
	}

	@Override
	public TableCellEditor getCellEditor() {
		return _cellEditor;
	}

	@SuppressWarnings("serial")
	protected class DropDownCellEditor extends DefaultCellEditor {
		private Hashtable<Integer, DropDownComboBoxModel> _comboBoxModels;

		private JComboBox comboBox;

		public DropDownCellEditor(JComboBox aComboBox) {
			super(aComboBox);
			aComboBox.setFont(getFont());
			_comboBoxModels = new Hashtable<Integer, DropDownComboBoxModel>();
			comboBox = aComboBox;
			comboBox.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (returned instanceof JLabel) {
						((JLabel) returned).setText(renderValue((T) value));
						((JLabel) returned).setFont(getFont());
					}
					return returned;
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			Component returned = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			comboBox.setModel(getComboBoxModel(value, row, column));
			return returned;
		}

		protected DropDownComboBoxModel getComboBoxModel(Object value, int row, int column) {
			// Don't use cache as it is never refreshed
			// DropDownComboBoxModel _comboBoxModel = _comboBoxModels.get(row);
			// if (_comboBoxModel == null) {
			DropDownComboBoxModel _comboBoxModel = new DropDownComboBoxModel(elementAt(row));
			// _comboBoxModels.put(row, _comboBoxModel);
			// }
			_comboBoxModel.setSelectedItem(value);
			return _comboBoxModel;
		}

		protected class DropDownComboBoxModel extends DefaultComboBoxModel {

			protected DropDownComboBoxModel(T element) {
				super();
				List<V> v = getAvailableValues(element);
				if (v != null) {
					for (Iterator<V> it = v.iterator(); it.hasNext();) {
						V next = it.next();
						addElement(next);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "DropDownColumn " + "@" + Integer.toHexString(hashCode());
	}
}
