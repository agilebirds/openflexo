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
package org.openflexo.fib.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fib.model.FIBPanel.Layout;

public abstract class ComponentConstraints extends Hashtable<String, String> {

	static final Logger logger = Logger.getLogger(FIBComponent.class.getPackage().getName());

	private static final String INDEX = "index";

	public boolean ignoreNotif = false;

	public String getStringRepresentation() {
		StringBuilder returned = new StringBuilder();
		returned.append(getType().name()).append("(");
		boolean isFirst = true;
		List<String> keys = new ArrayList<String>(keySet());
		Collections.sort(keys);
		for (String key : keys) {
			String v = get(key);
			returned.append(isFirst ? "" : ";").append(key).append("=").append(v);
			isFirst = false;
		}
		returned.append(")");
		return returned.toString();
	}

	private FIBComponent component;

	public ComponentConstraints() {
		super();
	}

	protected ComponentConstraints(String someConstraints) {
		this();
		StringTokenizer st = new StringTokenizer(someConstraints, ";");
		while (st.hasMoreTokens()) {
			String next = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(next, "=");
			String key = null;
			String value = null;
			if (st2.hasMoreTokens()) {
				key = st2.nextToken();
			}
			if (st2.hasMoreTokens()) {
				value = st2.nextToken();
			}
			if (key != null && value != null) {
				put(key, value);
			}
		}
	}

	ComponentConstraints(ComponentConstraints someConstraints) {
		this();
		ignoreNotif = true;
		for (String key : someConstraints.keySet()) {
			put(key, someConstraints.get(key));
		}
		ignoreNotif = false;
		component = someConstraints.component;
	}

	@Override
	public synchronized String put(String key, String value) {
		String returned = super.put(key, value);

		if (component != null && !ignoreNotif) {
			FIBPropertyNotification<ComponentConstraints> notification = new FIBPropertyNotification<ComponentConstraints>(
					(FIBProperty<ComponentConstraints>) FIBProperty.getFIBProperty(FIBComponent.class, FIBComponent.CONSTRAINTS_KEY), this,
					this);
			component.notify(notification);
		}

		return returned;
	}

	protected abstract Layout getType();

	public String getStringValue(String key, String defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			logger.info("Ben je trouve pas....... pourtant=" + this);
			ignoreNotif = true;
			setStringValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return stringValue;
	}

	public void setStringValue(String key, String value) {
		put(key, value);
	}

	public <E extends Enum> E getEnumValue(String key, Class<E> enumType, E defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setEnumValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		for (E en : enumType.getEnumConstants()) {
			if (en.name().equals(stringValue)) {
				return en;
			}
		}
		logger.warning("Found inconsistent value '" + stringValue + "' as " + enumType);
		return defaultValue;
	}

	public void setEnumValue(String key, Enum value) {
		put(key, value.name());
	}

	public int getIntValue(String key, int defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setIntValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return Integer.parseInt(stringValue);
	}

	public void setIntValue(String key, int value) {
		put(key, ((Integer) value).toString());
	}

	public float getFloatValue(String key, float defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setFloatValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return Float.parseFloat(stringValue);
	}

	public void setFloatValue(String key, float value) {
		put(key, ((Float) value).toString());
	}

	public double getDoubleValue(String key, double defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setDoubleValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return Double.parseDouble(stringValue);
	}

	public void setDoubleValue(String key, double value) {
		put(key, ((Double) value).toString());
	}

	public boolean getBooleanValue(String key, boolean defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setBooleanValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return stringValue.equalsIgnoreCase("true");
	}

	public void setBooleanValue(String key, boolean value) {
		put(key, value ? "true" : "false");
	}

	public FIBComponent getComponent() {
		return component;
	}

	public void setComponent(FIBComponent component) {
		this.component = component;
	}

	public abstract void performConstrainedAddition(JComponent container, JComponent contained);

	public final int getIndex() {
		if (hasIndex()) {
			return getIntValue(INDEX, 0);
		}
		return 0;
	}

	public final void setIndex(int x) {
		setIntValue(INDEX, x);
	}

	public final boolean hasIndex() {
		return get(INDEX) != null;
	}

	@Override
	public synchronized String toString() {
		return getClass().getSimpleName() + " " + super.toString();
	}

}
