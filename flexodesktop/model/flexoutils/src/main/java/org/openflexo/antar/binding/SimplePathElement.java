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

package org.openflexo.antar.binding;

import java.lang.reflect.Type;

/**
 * Model a simple path element in a binding path, represented by a simple get/set access through a property
 * 
 * @author sylvain
 * 
 */
public abstract class SimplePathElement implements BindingPathElement, SettableBindingPathElement {

	private BindingPathElement parent;
	private String propertyName;
	private Type type;

	public SimplePathElement(BindingPathElement parent, String propertyName, Type type) {
		this.parent = parent;
		this.propertyName = propertyName;
		this.type = type;
	}

	@Override
	public BindingPathElement getParent() {
		return parent;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public Type getType() {
		return type;
	}

	public final void setType(Type type) {
		this.type = type;
	}

	@Override
	public boolean isSettable() {
		return true;
	}

	@Override
	public String getSerializationRepresentation() {
		return getPropertyName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimplePathElement) {
			return getParent().equals(((SimplePathElement) obj).getParent())
					&& getPropertyName().equals(((SimplePathElement) obj).getPropertyName());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getPropertyName().hashCode();
	}
}
