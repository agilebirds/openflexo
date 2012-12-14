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
package org.openflexo.inspector.widget;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.HasIcon;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class MultipleValuesWidget<T> extends DenaliWidget<T> {

	private static final Logger logger = Logger.getLogger(MultipleValuesWidget.class.getPackage().getName());

	protected static final int UNDEFINED = -1;

	protected static final int CHOICE_LIST = 0;

	protected static final int STATIC_LIST = 1;

	protected static final int DYNAMIC_LIST = 2;

	protected static final int DYNAMIC_HASH = 3;

	protected int valueType = UNDEFINED;

	private DenaliListModel listModel = null;

	private DenaliListCellRenderer listCellRenderer = null;

	protected boolean tryToShowIcon = false;

	/**
	 * @param model
	 */
	protected MultipleValuesWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		if (model.hasValueForParameter("showIcon") && model.getBooleanValueForParameter("showIcon")) {
			tryToShowIcon = true;
		}
	}

	@Override
	protected void setModel(InspectableObject value) {
		super.setModel(value);
		if (isChoiceList()) {
			valueType = CHOICE_LIST;
		} else if (_propertyModel.hasStaticList()) {
			valueType = STATIC_LIST;
		} else if (_propertyModel.hasDynamicList()) {
			valueType = DYNAMIC_LIST;
		} else if (_propertyModel.hasDynamicHashtable()) {
			valueType = DYNAMIC_HASH;
		}
		listCellRenderer = new DenaliListCellRenderer();
		listModel = new DenaliListModel();
	}

	@Override
	public void performUpdate() {
		// logger.info("Updating model for "+this);
		super.performUpdate();
		if (isChoiceList()) {
			valueType = CHOICE_LIST;
		} else if (_propertyModel.hasStaticList()) {
			valueType = STATIC_LIST;
		} else if (_propertyModel.hasDynamicList()) {
			valueType = DYNAMIC_LIST;
		} else if (_propertyModel.hasDynamicHashtable()) {
			valueType = DYNAMIC_HASH;
		}
		listCellRenderer = new DenaliListCellRenderer();
		listModel = new DenaliListModel();
	}

	@Override
	public Class getDefaultType() {
		return List.class;
	}

	protected Object[] getListValues() {
		if (_propertyModel.hasDynamicList()) {
			return _propertyModel.getDynamicList(getModel()).toArray();
		} else if (getType().getEnumConstants() != null) {
			return getType().getEnumConstants();
		} else if (isChoiceList()) {
			if (getObjectValue() == null) {
				try {
					// Dynamic call to getType() class on method
					// availableValues()
					Method availableValuesMethod = getType().getMethod("availableValues", (Class[]) null);
					return ((List) availableValuesMethod.invoke(getType(), (Object[]) null)).toArray();
				} catch (Exception e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
					return new Object[0];
				}
			} else {
				return ((ChoiceList) getObjectValue()).getAvailableValues().toArray();
			}
		} else if (_propertyModel.hasStaticList()) {
			return _propertyModel.getStaticList().toArray();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("There is an error in some configuration file :\n the property named '" + _propertyModel.name
						+ "' is supposed to be a list, but it doesn't hold any list definition!");
			}
			return new Object[0];
		}
	}

	protected Hashtable getHashValues() {
		if (_propertyModel.hasDynamicHashtable()) {
			return _propertyModel.getDynamicHashtable(getModel());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("There is an error in some configuration file :\n the property named '" + _propertyModel.name
						+ "' is supposed to be a hashtable, but it doesn't hold any hashtable definition!");
			}
			return new Hashtable();
		}
	}

	public DenaliListCellRenderer getListCellRenderer() {
		if (listCellRenderer == null) {
			listCellRenderer = new DenaliListCellRenderer();
		}
		return listCellRenderer;
	}

	public DenaliListModel getListModel() {
		if (listModel == null) {
			listModel = new DenaliListModel();
		}
		return listModel;
	}

	public int getValueType() {
		return valueType;
	}

	protected class DenaliListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			DenaliListCellRenderer label = (DenaliListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			if (tryToShowIcon && value instanceof HasIcon) {
				label.setIcon(((HasIcon) value).getIcon());
			}
			if (value != null) {
				String stringRepresentation = getDisplayStringRepresentation(value);
				if (stringRepresentation == null || stringRepresentation.length() == 0) {
					stringRepresentation = "<html><i>" + FlexoLocalization.localizedForKey("empty_string") + "</i></html>";
				}
				label.setText(stringRepresentation);
			} else {
				label.setText(FlexoLocalization.localizedForKey("no_selection"));
			}
			label.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
			return label;
		}
	}

	protected class DenaliListModel implements ListModel {
		private Object[] _values;

		private Hashtable _hashValues;

		protected DenaliListModel() {
			super();
			if (getModel() != null) {
				if (valueType == CHOICE_LIST || valueType == STATIC_LIST || valueType == DYNAMIC_LIST) {
					_values = getListValues();
				} else if (valueType == DYNAMIC_HASH) {
					_hashValues = getHashValues();
				}
			}
			// logger.info("values of model are: "+_values);
		}

		@Override
		public int getSize() {
			if (getModel() != null) {
				if (_values != null && (valueType == CHOICE_LIST || valueType == STATIC_LIST || valueType == DYNAMIC_LIST)) {
					return _values.length;
				} else if (valueType == DYNAMIC_HASH) {
					return _hashValues.size();
				}
			}
			return 0;
		}

		@Override
		public Object getElementAt(int index) {
			if (getModel() != null) {
				if (valueType == CHOICE_LIST || valueType == STATIC_LIST || valueType == DYNAMIC_LIST) {
					return _values[index];
				} else if (valueType == DYNAMIC_HASH) {
					Iterator it = _hashValues.values().iterator();
					for (int i = 0; i < index; i++) {
						if (it.hasNext()) {
							it.next();
						}
					}
					if (it.hasNext()) {
						return it.next();
					}
				}
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Inconsistent data !");
				}
			}
			return null;
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			// Interface
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			// Interface
		}

		public int indexOf(Object cur) {
			for (int i = 0; i < getSize(); i++) {
				if (getElementAt(i).equals(cur)) {
					return i;
				}
			}
			return -1;
		}
	}

}
