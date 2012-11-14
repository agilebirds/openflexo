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

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class ParametersContainerModelObject extends ModelObject {

	private static final Logger logger = Logger.getLogger(ParametersContainerModelObject.class.getPackage().getName());

	public Hashtable<String, ParamModel> parameters;

	public ParametersContainerModelObject() {
		parameters = new Hashtable<String, ParamModel>();
	}

	public ParamModel valueForParameter(String name) {
		return parameters.get(name);
	}

	public String getValueForParameter(String name) {
		if (hasValueForParameter(name)) {
			ParamModel param = parameters.get(name);
			return param.value;
		} else {
			return null;
		}
	}

	public boolean getBooleanValueForParameter(String name) {
		String stringValue = getValueForParameter(name);
		if (stringValue != null) {
			return stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("yes");
		}
		return false;
	}

	public int getIntValueForParameter(String name) {
		String stringValue = getValueForParameter(name);
		if (stringValue != null) {
			try {
				return Integer.parseInt(stringValue);
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Invalid number representation !");
				}
			}
		}
		return 0;
	}

	public float getFloatValueForParameter(String name) {
		String stringValue = getValueForParameter(name);
		if (stringValue != null) {
			try {
				return Float.parseFloat(stringValue);
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Invalid number representation !");
				}
			}
		}
		return 0;
	}

	public double getDoubleValueForParameter(String name) {
		String stringValue = getValueForParameter(name);
		if (stringValue != null) {
			try {
				return Double.parseDouble(stringValue);
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Invalid number representation !");
				}
			}
		}
		return 0;
	}

	public boolean hasValueForParameter(String name) {
		return parameters.get(name) != null;
	}

	public void setBooleanValueForParameter(String name, boolean aBoolean) {
		ParamModel param = new ParamModel();
		param.name = name;
		param.value = "" + aBoolean;
		parameters.put(name, param);
	}

	public void setIntValueForParameter(String name, int anInt) {
		ParamModel param = new ParamModel();
		param.name = name;
		param.value = "" + anInt;
		parameters.put(name, param);
	}

	public void setDoubleValueForParameter(String name, double aDouble) {
		ParamModel param = new ParamModel();
		param.name = name;
		param.value = "" + aDouble;
		parameters.put(name, param);
	}

	public void setValueForParameter(String name, String value) {
		ParamModel param = new ParamModel();
		param.name = name;
		param.value = value;
		parameters.put(name, param);
	}

}
