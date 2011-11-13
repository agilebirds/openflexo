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

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.xml.FlexoBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.xmlcode.XMLMapping;

public class FlexoProperty extends FlexoModelObject implements InspectableObject {

	private FlexoModelObject owner;

	private String name;
	private String value;

	public FlexoProperty(FlexoBuilder<?> builder) {
		this(builder.getProject());
	}

	public FlexoProperty(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	public FlexoProperty(FlexoProject project) {
		super(project);
	}

	public FlexoProperty(FlexoProject project, FlexoModelObject owner) {
		this(project);
		this.owner = owner;
	}

	@Override
	public void delete() {
		if (getOwner() != null)
			getOwner().removeFromCustomProperties(this);
		super.delete();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		setChanged();
		notifyObservers(new DataModification(-1, "name", null, name));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		setChanged();
		notifyObservers(new DataModification(-1, "value", null, value));
	}

	public FlexoModelObject getOwner() {
		return owner;
	}

	public void setOwner(FlexoModelObject owner) {
		this.owner = owner;
	}

	@Override
	public FlexoProject getProject() {
		if (getOwner() != null)
			return getOwner().getProject();
		return super.getProject();
	}

	@Override
	public String getClassNameKey() {
		return "flexo_property";
	}

	@Override
	public String getFullyQualifiedName() {
		return getOwner() != null ? getOwner().getFullyQualifiedName() : "No owner" + "." + name + "=" + value;
	}

	@Override
	public XMLMapping getXMLMapping() {
		if (getOwner() != null)
			return getOwner().getXMLMapping();
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		if (getOwner() != null)
			return getOwner().getXMLResourceData();
		return null;
	}

	@Override
	public String getInspectorName() {
		// Never inspected on its own (always through the inspector of another model object)
		return null;
	}
}