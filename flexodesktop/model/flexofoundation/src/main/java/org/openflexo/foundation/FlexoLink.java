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

import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.xmlcode.XMLMapping;


public class FlexoLink extends FlexoModelObject implements ReferenceOwner {

	private FlexoLinks links;
	private FlexoModelObjectReference<?> object1;
	private FlexoModelObjectReference<?> object2;

	public FlexoLink(FlexoLinks links, FlexoModelObjectReference<?> object1, FlexoModelObjectReference<?> object2) {
		super(links.getProject());
		this.links = links;
		setObject1(object1);
		setObject2(object2);
	}

	@Override
	public void delete() {
		super.delete();
		links.removeFromLinks(this);
		object1 = null;
		object2 = null;
	}

	public void setLinks(FlexoLinks links) {
		this.links = links;
	}

	@Override
	public String getClassNameKey() {
		return "flexo_link";
	}

	@Override
	public String getFullyQualifiedName() {
		return "LINK";
	}

	@Override
	public long getFlexoID() {
		return -1;
	}

	@Override
	public void setFlexoID(long l) {

	}

	@Override
	public XMLMapping getXMLMapping() {
		return links.getXMLMapping();
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return links;
	}

	public FlexoModelObjectReference<?> getObject1() {
		return object1;
	}

	public void setObject1(FlexoModelObjectReference<?> object1) {
		this.object1 = object1;
		object1.setSerializeClassName(true);
		object1.setOwner(this);
	}

	public FlexoModelObjectReference<?> getObject2() {
		return object2;
	}

	public void setObject2(FlexoModelObjectReference<?> object2) {
		this.object2 = object2;
		object2.setSerializeClassName(true);
		object2.setOwner(this);
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference reference) {
		if (reference==object1)
			links.updateReferencesForObject(object1.getObject(), this);
		if (reference==object2)
			links.updateReferencesForObject(object2.getObject(), this);
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference reference) {
		links.removeFromLinks(this);
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference reference) {
		links.removeFromLinks(this);
	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference reference) {
		setChanged();
	}

}