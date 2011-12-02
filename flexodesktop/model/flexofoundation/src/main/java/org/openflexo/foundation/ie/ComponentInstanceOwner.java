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
package org.openflexo.foundation.ie;

import org.openflexo.foundation.rm.XMLStorageResourceData;

/**
 * Interface for objects that references a component instance. Note that a component instance owner has 3 notifications to forward to its
 * component instance(s): 1) When the owner changes of resource data--> updateDependancies() 2) When the owner is deleted 3) When the
 * component instance is removed from the owner
 * 
 * @author gpolet
 * 
 */
public interface ComponentInstanceOwner {

	public long getFlexoID();

	public void setChanged();

	public String getFullyQualifiedName();

	public abstract XMLStorageResourceData getXMLResourceData();

}
