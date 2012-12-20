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
package org.openflexo.foundation;

import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.xml.FlexoBuilder;
import org.openflexo.xmlcode.XMLSerializable;

public class FlexoProperty extends FlexoObject implements XMLSerializable {

	private FlexoObject owner;

	private String name;
	private String value;

	public FlexoProperty(FlexoBuilder<?> builder) {
		this(builder.getProject());
	}

	public FlexoProperty(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	public FlexoProperty() {
		super();
	}

	public FlexoProperty(FlexoObject owner) {
		this();
		this.owner = owner;
	}

	@Override
	public void delete() {
		if (getOwner() != null) {
			getOwner().removeFromCustomProperties(this);
		}
		super.delete();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setChanged();
		notifyObservers(new DataModification("name", null, name));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		setChanged();
		notifyObservers(new DataModification("value", null, value));
	}

	public FlexoObject getOwner() {
		return owner;
	}

	public void setOwner(FlexoObject owner) {
		this.owner = owner;
	}

	@Override
	public String getFullyQualifiedName() {
		return getOwner() != null ? getOwner().getFullyQualifiedName() : "No owner" + "." + name + "=" + value;
	}

	public XMLStorageResourceData getXMLResourceData() {
		if (getOwner() instanceof FlexoModelObject) {
			return ((FlexoModelObject) getOwner()).getXMLResourceData();
		}
		return null;
	}

}