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
package org.openflexo.fib.controller;

import java.beans.PropertyChangeSupport;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class FIBComponentDynamicModel<T> implements HasPropertyChangeSupport {

	private static final String DELETED = "deleted";
	private FIBComponent component;
	private boolean visible;
	private T data;
	private PropertyChangeSupport propertyChangeSupport;

	public FIBComponentDynamicModel(T data, FIBComponent component) {
		this.component = component;
		propertyChangeSupport = new PropertyChangeSupport(this);
		setData(data);
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	public void delete() {
		data = null;
		propertyChangeSupport.firePropertyChange(DELETED, false, true);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
		propertyChangeSupport.firePropertyChange("data", null, data);
	}

	public FIBComponent getComponent() {
		return component;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + (component != null ? "/" + component.getName() : "") + ",data=" + getData();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		propertyChangeSupport.firePropertyChange("visible", null, visible);
	}
}
