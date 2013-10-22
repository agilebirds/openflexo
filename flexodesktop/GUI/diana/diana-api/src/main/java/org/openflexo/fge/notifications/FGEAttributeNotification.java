/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.fge.notifications;

import org.openflexo.fge.GRParameter;

/**
 * Notification thrown when an attribute changed its value from a value to another value
 * 
 * @author sylvain
 * 
 * @param <T>
 */
public class FGEAttributeNotification<T> extends FGENotification {

	public GRParameter<T> parameter;

	public FGEAttributeNotification(GRParameter<T> parameter, T oldValue, T newValue) {
		super(parameter != null ? parameter.getName() : null, oldValue, newValue);
		this.parameter = parameter;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return "FGEAttributeNotification of " + getClass().getSimpleName() + " " + getParameter() + " old: " + oldValue + " new: "
				+ newValue;
	}

	public GRParameter<T> getParameter() {
		return parameter;
	}

	public String propertyName() {
		if (parameter != null) {
			return parameter.getName();
		}
		return null;
	}

	public T newValue() {
		return (T) newValue;
	}

	@Override
	public T oldValue() {
		return (T) super.oldValue();
	}
}