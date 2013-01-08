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
package org.openflexo.inspector.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.widget.propertylist.AbstractColumn;
import org.openflexo.inspector.widget.propertylist.BooleanColumn;
import org.openflexo.inspector.widget.propertylist.ColorColumn;
import org.openflexo.inspector.widget.propertylist.CustomColumn;
import org.openflexo.inspector.widget.propertylist.DropDownColumn;
import org.openflexo.inspector.widget.propertylist.EditableStringColumn;
import org.openflexo.inspector.widget.propertylist.IconColumn;
import org.openflexo.inspector.widget.propertylist.IntegerColumn;
import org.openflexo.inspector.widget.propertylist.StringColumn;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.xmlcode.AccessorInvocationException;

public class PropertyListColumn extends PropertyModel {

	static final Logger logger = Logger.getLogger(PropertyListColumn.class.getPackage().getName());

	public static final String READ_ONLY_TEXT_FIELD = "READ_ONLY_TEXT_FIELD";
	public static final String TEXT_FIELD = "TEXT_FIELD";
	public static final String CHECKBOX = "CHECKBOX";
	public static final String INTEGER = "INTEGER";
	public static final String DROPDOWN = "DROPDOWN";
	public static final String COLOR = "COLOR";
	public static final String ICON = "ICON";
	public static final String CUSTOM = "CUSTOM";

	private Vector<String> availableWidgetValues;

	public Vector<String> getAvailableWidgetValues() {
		if (availableWidgetValues == null) {
			availableWidgetValues = new Vector<String>();
			availableWidgetValues.add(READ_ONLY_TEXT_FIELD);
			availableWidgetValues.add(TEXT_FIELD);
			availableWidgetValues.add(CHECKBOX);
			availableWidgetValues.add(INTEGER);
			availableWidgetValues.add(DROPDOWN);
			availableWidgetValues.add(ICON);
		}
		return availableWidgetValues;
	}

	public static final int DEFAULT_COLUMN_WIDTH = 80;

	// public String name;
	// public String label;
	// public String widget;
	private AbstractColumn _tableColumn;

	private PropertyListModel _propertyListModel = null;

	public PropertyListColumn() {
		super();
		_tableColumn = null;
	}

	public PropertyListModel getPropertyListModel() {
		return _propertyListModel;
	}

	public void setPropertyListModel(PropertyListModel propertyListModel) {
		_propertyListModel = propertyListModel;
	}

	public void notifyValueChangedFor(InspectableObject object) {
		// Override if required
	}

	public AbstractColumn getTableColumn(AbstractController controller) {
		if (_tableColumn == null) {
			if (getWidget().equals(READ_ONLY_TEXT_FIELD)) {
				_tableColumn = new StringColumn(label, getColumnWidth(), isResizable(), displayTitle(), font()) {
					@Override
					public String getValue(InspectableObject object) {
						Object cellValue = getCellValue(object);
						if (cellValue instanceof String) {
							return (String) cellValue;
						} else if (cellValue != null) {
							return cellValue.toString();
						} else {
							return "null ???";
						}
					}

					@Override
					public void notifyValueChangedFor(InspectableObject object) {
						super.notifyValueChangedFor(object);
						PropertyListColumn.this.notifyValueChangedFor(object);
					}
				};
			} else if (getWidget().equals(TEXT_FIELD)) {
				_tableColumn = new EditableStringColumn(label, getColumnWidth(), isResizable(), displayTitle(), font()) {
					@Override
					public String getValue(InspectableObject object) {
						Object cellValue = getCellValue(object);
						if (cellValue instanceof String) {
							return (String) cellValue;
						} else if (cellValue != null) {
							return cellValue.toString();
						} else {
							return "";
						}
					}

					@Override
					public boolean isCellEditableFor(InspectableObject object) {

						return isEditable(object);
					}

					@Override
					public void setValue(InspectableObject object, String aValue) {
						setCellValue(aValue, object);
					}

					@Override
					public void notifyValueChangedFor(InspectableObject object) {
						super.notifyValueChangedFor(object);
						PropertyListColumn.this.notifyValueChangedFor(object);
					}
				};
			} else if (getWidget().equals(CHECKBOX)) {
				_tableColumn = new BooleanColumn(label, getColumnWidth(), isResizable(), displayTitle()) {
					@Override
					public Boolean getValue(InspectableObject object) {
						return new Boolean(getBooleanCellValue(object));
					}

					@Override
					public void setValue(InspectableObject object, Boolean aValue) {
						setBooleanCellValue(aValue.booleanValue(), object);
					}

					@Override
					public void notifyValueChangedFor(InspectableObject object) {
						super.notifyValueChangedFor(object);
						PropertyListColumn.this.notifyValueChangedFor(object);
					}
				};
			} else if (getWidget().equals(INTEGER)) {
				_tableColumn = new IntegerColumn(label, getColumnWidth(), isResizable(), displayTitle()) {
					@Override
					public Integer getValue(InspectableObject object) {
						return new Integer(getIntegerCellValue(object));
					}

					@Override
					public void setValue(InspectableObject object, Integer aValue) {
						setIntegerCellValue(aValue.intValue(), object);
					}

					@Override
					public void notifyValueChangedFor(InspectableObject object) {
						super.notifyValueChangedFor(object);
						PropertyListColumn.this.notifyValueChangedFor(object);
					}
				};
			} else if (getWidget().equals(DROPDOWN)) {
				_tableColumn = new DropDownColumn(label, getColumnWidth()) {
					@Override
					public Object getValue(InspectableObject object) {
						// logger.info("getValue() for "+object+" return "+getCellValue(object));
						return getCellValue(object);
						/*
						Object cellValue = getCellValue(object);
						if (cellValue instanceof String) {
						    return (String) cellValue;
						} else if (cellValue != null) {
						    return cellValue.toString();
						} else {
						    return null;
						}*/
					}

					@Override
					public void setValue(InspectableObject object, Object aValue) {
						setCellValue(aValue, object);
					}

					@Override
					public String renderValue(Object object) {
						return getStringRepresentation(object);
						// return object.toString();
					}

					@Override
					public List<?> getAvailableValues(InspectableObject object) {

						// logger.info("getAvailableValues() for "+object);

						// if (logger.isLoggable(Level.SEVERE))
						// logger.severe("getAvailableValues() on a "+object+" hasDynList="+hasDynamicList());
						if (hasDynamicList()) {
							return getDynamicList(object);
						} else if (hasStaticList()) {
							return getStaticList();
						} else if (object.getTypeForKey(name) != null && object.getTypeForKey(name).isEnum()) {
							List<Object> returned = new ArrayList<Object>();
							for (Object e : object.getTypeForKey(name).getEnumConstants()) {
								returned.add(e);
							}
							return returned;
						} else {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("There is an error in some configuration file :\n the property named '" + name
										+ "' is supposed to be a list, but it doesn't hold any list definition!");
							}
							return Collections.emptyList();
						}
					}

					@Override
					public void notifyValueChangedFor(InspectableObject object) {
						super.notifyValueChangedFor(object);
						PropertyListColumn.this.notifyValueChangedFor(object);
					}
				};
			} else if (getWidget().equals(COLOR)) {
				_tableColumn = new ColorColumn(label, getColumnWidth(), isResizable(), displayTitle()) {
					@Override
					public Object getValue(InspectableObject object) {
						return getCellValue(object);
					}

					@Override
					public void setValue(InspectableObject object, Object aValue) {

						setCellValue(aValue, object);
						_colorCellEditor.setColor((Color) aValue);
					}

					@Override
					public void notifyValueChangedFor(InspectableObject object) {
						super.notifyValueChangedFor(object);
						PropertyListColumn.this.notifyValueChangedFor(object);
					}
				};

			} else if (getWidget().equals(ICON)) {
				_tableColumn = new IconColumn(label, getColumnWidth(), isResizable(), displayTitle()) {
					@Override
					public Icon getValue(InspectableObject object) {
						return (Icon) getCellValue(object);
					}

					@Override
					public void setValue(InspectableObject object, Icon aValue) {
						setCellValue(aValue, object);
					}

					@Override
					public void notifyValueChangedFor(InspectableObject object) {
						super.notifyValueChangedFor(object);
						PropertyListColumn.this.notifyValueChangedFor(object);
					}

				};
			} else if (getWidget().equals(CUSTOM)) {
				_tableColumn = new CustomColumn(this, label, getColumnWidth(), isResizable(), displayTitle(), controller) {
					@Override
					public void setValue(InspectableObject object, Object aValue) {
						// if (logger.isLoggable(Level.FINE))
						// logger.fine("setCellValue() for property "+name+" and
						// object "+object+" with "+aValue);
						setCellValue(aValue, object);
					}

					@Override
					public Object getValue(InspectableObject object) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("getCellValue() for property " + name + " and object " + object + " returns "
									+ getCellValue(object));
						}
						return getCellValue(object);
					}

					@Override
					public void notifyValueChangedFor(InspectableObject object) {
						super.notifyValueChangedFor(object);
						PropertyListColumn.this.notifyValueChangedFor(object);
					}
				};
			} else {
				logger.warning("Unexpected widget type: " + getWidget());
			}
			if (_tableColumn != null) {
				_tableColumn.setTooltipKey(getValueForParameter("tooltip"));
			}
		}
		return _tableColumn;
	}

	protected boolean modelUpdating = false;

	protected boolean widgetUpdating = false;

	protected boolean valueInError = false;

	/**
	 * This method can be called to store the newValue in the model.
	 * 
	 * @param newValue
	 */
	protected synchronized void setCellValue(Object newValue, InspectableObject object) {
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("setCellValue() for property " + name + " with " + newValue);
			}
			KeyValueCoding target = getTargetObject(object);
			if (target != null) {
				target.setObjectForKey(newValue, getLastAccessor());
			}
		} catch (AccessorInvocationException e) {
			valueInError = true;
			modelUpdating = false;
			/*
			 * if (!getIn.handleException(object, name, newValue, e.getTargetException())) { // Revert to value defined in the model ! if
			 * (logger.isLoggable(Level.INFO)) logger.info("Exception was NOT handled"); } else { if (logger.isLoggable(Level.INFO))
			 * logger.info("Exception was handled"); }
			 */
			valueInError = false;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("setCellValue() with " + newValue + " failed for property " + name + " for object "
						+ object.getClass().getName() + " : exception " + e.getMessage());
			}
			e.printStackTrace();
		}
	}

	public Object getCellValue(InspectableObject object) {
		try {
			if (object == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("model is null");
				}
				return null;
			}
			KeyValueCoding target = getTargetObject(object);
			if (target != null) {
				return target.objectForKey(getLastAccessor());
			} else {
				return null;
			}
		} catch (AccessorInvocationException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getCellValue() failed for property " + name + " for object " + object.getClass().getName()
						+ " : exception " + e.getMessage());
			}
			e.getTargetException().printStackTrace();
			return null;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getObjectValue() failed for property " + name + " for object " + object.getClass().getName()
						+ " : exception " + e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
	}

	protected synchronized void setBooleanCellValue(boolean value, InspectableObject object) {
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("setBooleanCellValue() for property " + name + " with " + value);
			}
			KeyValueCoding target = getTargetObject(object);
			if (target != null) {
				target.setBooleanValueForKey(value, getLastAccessor());
			}
		} catch (AccessorInvocationException e) {
			valueInError = true;
			modelUpdating = false;
			/*
			 * if (!InspectorController.instance().handleException(object, name, new Boolean(value), e.getTargetException())) { // Revert to
			 * value defined in the model ! if (logger.isLoggable(Level.INFO)) logger.info("Exception was NOT handled"); } else { if
			 * (logger.isLoggable(Level.INFO)) logger.info("Exception was handled"); }
			 */
			valueInError = false;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("setBooleanCellValue() with " + value + " failed for property " + name + " for object "
						+ object.getClass().getName() + " : exception " + e.getMessage());
			}
			e.printStackTrace();
		}
	}

	protected synchronized boolean getBooleanCellValue(InspectableObject object) {
		try {
			if (object == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("model is null");
				}
				return false;
			}
			KeyValueCoding target = getTargetObject(object);
			if (target != null) {
				return target.booleanValueForKey(getLastAccessor());
			} else {
				return false;
			}
		} catch (AccessorInvocationException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getCellValue() failed for property " + name + " for object " + object.getClass().getName()
						+ " : exception " + e.getMessage());
			}
			e.getTargetException().printStackTrace();
			return false;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getObjectValue() failed for property " + name + " for object " + object.getClass().getName()
						+ " : exception " + e.getMessage());
			}
			return false;
		}
	}

	protected synchronized void setIntegerCellValue(int value, InspectableObject object) {
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("setIntegerCellValue() for property " + name + " with " + value);
			}
			KeyValueCoding target = getTargetObject(object);
			if (target != null) {
				target.setIntegerValueForKey(value, getLastAccessor());
			}
		} catch (AccessorInvocationException e) {
			valueInError = true;
			modelUpdating = false;
			/*
			 * if (!InspectorController.instance().handleException(object, name, Integer.valueOf(value), e.getTargetException())) { //
			 * Revert to value defined in the model ! if (logger.isLoggable(Level.INFO)) logger.info("Exception was NOT handled"); } else {
			 * if (logger.isLoggable(Level.INFO)) logger.info("Exception was handled"); }
			 */
			valueInError = false;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("setIntegerCellValue() with " + value + " failed for property " + name + " for object "
						+ object.getClass().getName() + " : exception " + e.getMessage());
			}
			e.printStackTrace();
		}
	}

	protected synchronized int getIntegerCellValue(InspectableObject object) {
		try {
			if (object == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("model is null");
				}
				return 0;
			}
			KeyValueCoding target = getTargetObject(object);
			if (target != null) {
				return target.integerValueForKey(getLastAccessor());
			} else {
				return 0;
			}
		} catch (AccessorInvocationException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getCellValue() failed for property " + name + " for object " + object.getClass().getName()
						+ " : exception " + e.getMessage());
			}
			e.getTargetException().printStackTrace();
			return 0;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getObjectValue() failed for property " + name + " for object " + object.getClass().getName()
						+ " : exception " + e.getMessage());
			}
			return 0;
		}
	}

	public KeyValueCoding getTargetObject(InspectableObject anObject) {
		KeyValueCoding object = anObject;
		String listAccessor = name;
		StringTokenizer strTok = new StringTokenizer(listAccessor, ".");
		String accessor;
		Object currentObject = object;
		while (strTok.hasMoreTokens() && currentObject != null && currentObject instanceof KeyValueCoding) {
			accessor = strTok.nextToken();
			if (strTok.hasMoreTokens()) {
				if (currentObject != null) {
					currentObject = ((KeyValueCoding) currentObject).objectForKey(accessor);
				}
			}
		}
		if (currentObject instanceof KeyValueCoding) {
			return (KeyValueCoding) currentObject;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find target object for object=" + object + " listAccessor=" + listAccessor
						+ ": must be a non-null KeyValueCoding object (getting " + currentObject + ")");
			}
			return null;
		}
	}

	private String _lastAccessor;

	@Override
	public String getLastAccessor() {
		if (_lastAccessor == null) {
			String listAccessor = name;
			StringTokenizer strTok = new StringTokenizer(listAccessor, ".");
			String accessor = null;
			while (strTok.hasMoreTokens()) {
				accessor = strTok.nextToken();
			}
			_lastAccessor = accessor;
		}
		return _lastAccessor;
	}

	public static final String COLUMN_WIDTH = "column_width";
	public static final String RESIZABLE = "resizable";
	public static final String DISPLAY_TITLE = "display_title";
	public static final String FONT = "font";
	public static final String FORMAT = "format";
	public static final String DYNAMIC_LIST = "dynamiclist";

	public int getColumnWidth() {
		if (hasValueForParameter(COLUMN_WIDTH)) {
			return getIntValueForParameter(COLUMN_WIDTH);
		}
		return DEFAULT_COLUMN_WIDTH;
	}

	public void setColumnWidth(int columnWidth) {
		setIntValueForParameter(COLUMN_WIDTH, columnWidth);
	}

	public boolean isResizable() {
		if (hasValueForParameter(RESIZABLE)) {
			return getBooleanValueForParameter(RESIZABLE);
		}
		return true;
	}

	public void setIsResizable(boolean isResizable) {
		setBooleanValueForParameter(RESIZABLE, isResizable);
	}

	public boolean displayTitle() {
		if (hasValueForParameter(DISPLAY_TITLE)) {
			return getBooleanValueForParameter(DISPLAY_TITLE);
		}
		return true;
	}

	public void setDisplayTitle(boolean displayTitle) {
		setBooleanValueForParameter(DISPLAY_TITLE, displayTitle);
	}

	public String getFormat() {
		if (hasValueForParameter(FORMAT)) {
			return getValueForParameter(FORMAT);
		}
		return null;
	}

	public void setFormat(String format) {
		setValueForParameter(FORMAT, format);
	}

	public String getDynamicList() {
		if (hasValueForParameter(DYNAMIC_LIST)) {
			return getValueForParameter(DYNAMIC_LIST);
		}
		return null;
	}

	public void setDynamicList(String format) {
		setValueForParameter(DYNAMIC_LIST, format);
	}

	public String font() {
		if (hasValueForParameter(FONT)) {
			return getValueForParameter(FONT);
		}
		return null;
	}

	public void setFont(String font) {
		setValueForParameter(FONT, font);
	}

}
