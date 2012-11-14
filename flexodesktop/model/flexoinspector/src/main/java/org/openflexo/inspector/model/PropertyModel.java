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

import java.io.File;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.inspector.InspectableObject;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.KeyValueProperty;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringRepresentable;

public class PropertyModel extends ParametersContainerModelObject implements InnerTabWidget/*,PropertyParameter<Object>*/
{

	private static final Logger logger = Logger.getLogger(PropertyModel.class.getPackage().getName());

	public String _tabModelName;
	public String name;
	public String label;
	private String widget;
	public int constraint;
	public String depends;
	public String help;
	public String conditional;
	public String widgetLayout;

	private TabModel tabModel;

	private String _localizedLabel = null;

	public PropertyModel() {
		super();
	}

	public PropertyModel(String aName) {
		this();
		name = aName;
		finalizePropertyModelDecoding();
	}

	public String getLocalizedLabel() {
		return _localizedLabel;
	}

	public void setLocalizedLabel(String aString) {
		_localizedLabel = aString;
	}

	public void finalizePropertyModelDecoding() {
	}

	public int getGridHeight() {
		if (hasValueForParameter("height")) {
			return Integer.parseInt(getValueForParameter("height"));
		}
		return 24;
	}

	@Override
	public int getIndex() {
		return constraint;
	}

	public boolean hasStaticList() {
		return hasValueForParameter("staticlist");
	}

	public Vector getStaticList() {
		if (hasStaticList()) {
			String list = getValueForParameter("staticlist");
			StringTokenizer strTok = new StringTokenizer(list, ",");
			Vector<String> answer = new Vector<String>();
			while (strTok.hasMoreTokens()) {
				answer.add(strTok.nextToken());
			}
			return answer;
		} else {
			return new Vector<String>();
		}
	}

	public boolean hasDynamicList() {
		return hasValueForParameter("dynamiclist");
	}

	public Vector getDynamicList(InspectableObject object) {
		if (hasDynamicList()) {
			try {
				String listAccessor = getValueForParameter("dynamiclist");
				Object currentObject = getObjectForMultipleAccessors(object, listAccessor);
				if (currentObject instanceof Vector) {
					return (Vector) currentObject;
				} else if (currentObject == null) {
					return new Vector();
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Succeeded access to " + listAccessor + " but answer is not a Vector but a :"
								+ currentObject.getClass().getName() + " value=" + currentObject);
					}
					return new Vector();
				}
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("getDynamicList() failed for property " + name + " for object " + object + " : exception "
							+ e.getMessage());
				}
				e.printStackTrace();
				return new Vector();
			}
		} else {
			return new Vector();
		}
	}

	public boolean hasDynamicHashtable() {
		return hasValueForParameter("dynamichash");
	}

	public static Object getObjectForMultipleAccessors(KeyValueCoding object, String listAccessor) {
		// logger.info("list accessor = "+listAccessor+" for "+object);
		if (listAccessor != null && listAccessor.equals("this")) {
			return object;
		}
		StringTokenizer strTok = new StringTokenizer(listAccessor, ".");
		String accessor;
		Object currentObject = object;
		while (strTok.hasMoreTokens() && currentObject != null && currentObject instanceof KeyValueCoding) {
			accessor = strTok.nextToken();
			if (currentObject != null) {
				currentObject = ((KeyValueCoding) currentObject).objectForKey(accessor);
			}
		}
		return currentObject;
	}

	public Hashtable getDynamicHashtable(InspectableObject object) {
		if (hasDynamicHashtable()) {
			try {
				String listAccessor = getValueForParameter("dynamichash");
				Object currentObject = getObjectForMultipleAccessors(object, listAccessor);
				if (currentObject instanceof Hashtable) {
					return (Hashtable) currentObject;
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Succeeded acces to " + listAccessor + " but answer is not a Hashtable");
					}
					return new Hashtable();
				}
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("getDynamicList() failed for property " + name + " for object " + object + " : exception "
							+ e.getMessage());
				}
				return new Hashtable();
			}
		} else {
			return new Hashtable();
		}
	}

	public boolean hasFormatter() {
		return hasValueForParameter("format");
	}

	public String getFormattedObject(KeyValueCoding object) {
		if (hasFormatter()) {
			try {
				String listAccessor = getValueForParameter("format");
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Format object " + object + " with format " + getValueForParameter("format"));
				}
				Object currentObject = getObjectForMultipleAccessors(object, listAccessor);
				if (currentObject instanceof String) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Format object " + object + " with format " + getValueForParameter("format") + " returns "
								+ currentObject);
					}
					return (String) currentObject;
				} else if (currentObject == null) {
					return "";
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Property " + name + ": succeeded acces to " + listAccessor + " but answer is not a String "
								+ currentObject + " of " + currentObject.getClass().getName());
					}
					return null;
				}
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("getDynamicList() failed for property " + name + " for object " + object + " : exception "
							+ e.getMessage());
				}
				return null;
			}
		}
		return null;
	}

	public String getStringRepresentation(Object object) {
		if (object instanceof String) {
			return (String) object;
		} else if (object instanceof KeyValueCoding && hasFormatter()) {
			return getFormattedObject((KeyValueCoding) object);
		} else if (object instanceof StringConvertable) {
			return ((StringConvertable) object).getConverter().convertToString(object);
		} else if (object instanceof Number) {
			return object.toString();
		} else if (object instanceof Boolean) {
			return object.toString();
		} else if (object instanceof File) {
			return object.toString();
		} else if (object instanceof StringRepresentable) {
			return object.toString();
		} else if (object instanceof Enum) {
			return FlexoLocalization.localizedForKey(((Enum) object).name().toLowerCase());
		} else {
			if (object == null) {
				return "";
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("There is an error in some configuration file :\n the property named '" + name
						+ "' has no string representation formatter ! Object is a "
						+ (object != null ? object.getClass().getName() : "null"));
			}
			return object.toString();
		}
	}

	public boolean isEditable(Object object) {
		if (hasValueForParameter("isEditable") && object instanceof KeyValueCoding) {
			Object currentObject = getObjectForMultipleAccessors((KeyValueCoding) object, getValueForParameter("isEditable"));
			if (currentObject instanceof Boolean) {
				return (Boolean) currentObject;
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("There is an error in model: parameter 'isEditable' seems to be defined but not on a boolean: "
						+ getValueForParameter("isEditable"));
			}
		}
		return true;
	}

	public boolean hasIdentifier() {
		return hasValueForParameter("identifier");
	}

	public String getIdentifiedObject(KeyValueCoding object) {
		if (hasFormatter()) {
			try {
				String listAccessor = getValueForParameter("identifier");
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Identify object " + object + " with identifier " + getValueForParameter("identifier"));
				}
				Object currentObject = getObjectForMultipleAccessors(object, listAccessor);
				if (currentObject instanceof String) {
					return (String) currentObject;
				} else if (currentObject == null) {
					return "null";
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Property " + name + ": succeeded acces to " + listAccessor + " but answer is not a String");
					}
					return null;
				}
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("getDynamicList() failed for property " + name + " for object " + object + " : exception "
							+ e.getMessage());
				}
				return null;
			}
		} else {
			return null;
		}
	}

	public InspectorModel getInspectorModel() {
		if (tabModel != null) {
			return tabModel.getInspectorModel();
		} else {
			logger.warning("TabModel is null !");
			return null;
		}
	}

	public TabModel getTabModel() {
		return tabModel;
	}

	public void setTabModel(TabModel tm) {
		this.tabModel = tm;
		_tabModelName = tm.name;
	}

	// =================================================================
	// ==================== Dynamic access to values ===================
	// =================================================================

	public static String getLastAccessor(String keyPath) {
		KeyValueProperty.PathTokenizer strTok = new KeyValueProperty.PathTokenizer(keyPath);
		String accessor = null;
		while (strTok.hasMoreTokens()) {
			accessor = strTok.nextToken();
		}
		return accessor;

		/*  int lastDotPosition = keyPath.lastIndexOf(".");
		 if (lastDotPosition < 0)
		     return keyPath;
		 return keyPath.substring(lastDotPosition + 1, keyPath.length());*/

	}

	public static KeyValueCoding getTargetObject(KeyValueCoding object, String keyPath) {
		StringTokenizer strTok = new StringTokenizer(keyPath, ".");
		String accessor;
		Object currentObject = object;
		while (strTok.hasMoreTokens() && currentObject != null && currentObject instanceof KeyValueCoding) {
			accessor = strTok.nextToken();
			if (strTok.hasMoreTokens()) {
				if (currentObject != null) {
					currentObject = ((KeyValueCoding) currentObject).objectForKey(accessor);
					// System.out.println ("accessor: "+ accessor+",
					// currentObject="+currentObject);
				}
			}
		}
		if (currentObject instanceof KeyValueCoding) {
			return (KeyValueCoding) currentObject;
		} else if (currentObject != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find target object for object=" + object + " keyPath=" + keyPath
						+ ": must be a KeyValueCoding object (getting " + currentObject + ")");
			}
			return null;
		} else {
			return null;
		}
	}

	public KeyValueCoding getTargetObject(KeyValueCoding inspectable) {
		return PropertyModel.getTargetObject(inspectable, name);
	}

	private String _lastAccessor;

	public String getLastAccessor() {
		if (_lastAccessor == null) {
			_lastAccessor = PropertyModel.getLastAccessor(name);
		}
		return _lastAccessor;
	}

	public synchronized boolean hasObjectValue(KeyValueCoding inspectable) throws AccessorInvocationException {
		if (inspectable == null) {
			return false;
		}
		try {
			KeyValueCoding target = getTargetObject(inspectable);
			if (target != null) {
				target.objectForKey(getLastAccessor());
				return true;
			} else {
				return false;
			}

		} catch (InvalidObjectSpecificationException e) {
			return false;
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getObjectValue() failed for property " + name + " for object " + inspectable.getClass().getName()
						+ " : exception " + e.getMessage());
			}
			return false;
		}
	}

	public synchronized Object getObjectValue(KeyValueCoding inspectable) throws AccessorInvocationException {
		if (inspectable == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inspectable object is null for key " + name + ". We should definitely investigate this.");
			}
			return null;
		}

		try {
			KeyValueCoding target = getTargetObject(inspectable);
			if (target != null) {
				return target.objectForKey(getLastAccessor());
			} else {
				return null;
			}
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getObjectValue() failed for property " + name + " for object " + inspectable.getClass().getName()
						+ " : exception " + e.getMessage());
			}
			return null;
		}
	}

	/**
	 * This method can be called to store the newValue in the model.
	 * 
	 * @param newValue
	 */
	public synchronized void setObjectValue(KeyValueCoding inspectable, Object newValue) throws AccessorInvocationException {
		if (inspectable == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inspectable object is null for key " + name + ". We should definitely investigate this.");
			}
			return;
		}

		Object oldValue = getObjectValue(inspectable);
		// logger.info("Old value="+oldValue+" New value="+newValue);
		if (oldValue == null) {
			if (newValue == null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Same null value. Ignored.");
				}
				return;
			}
		} else if (newValue != null && oldValue.equals(newValue)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Same value. Ignored.");
			}
			return;
		}

		try {
			KeyValueCoding target = getTargetObject(inspectable);
			if (target != null) {
				target.setObjectForKey(newValue, getLastAccessor());
			} else if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Target object is null for key " + name + ". We should definitely investigate this.");
			}
			return;
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("setObjectValue() with " + newValue + " failed for property " + name + " for object "
						+ inspectable.getClass().getName() + " : exception " + e.getMessage());
			}
		}
	}

	public String getKey() {
		return name;
	}

	public boolean allowDelegateHandling() {
		return true;
	}

	public boolean isEditionPatternProperty() {
		return false;
	}

	public boolean isEditionPatternPropertyList() {
		return false;
	}

	public String getWidget() {
		return widget;
	}

	public void setWidget(String widget) {
		this.widget = widget;
	}
}
