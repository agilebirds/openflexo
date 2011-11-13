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
package org.openflexo.javaparser;

import org.openflexo.foundation.dm.DMClassLibrary;
import org.openflexo.foundation.dm.DMType;

import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.Type;

public class FJPType extends DMType {

	private String name;

	public FJPType(String fullName, String name, int dimensions, JavaClassParent context) {
		super(fullName, name, dimensions, context);
		this.name = name;
	}

	public static Type createUnresolved(String name, int dimensions, JavaClassParent context) {
		if (context.getClassLibrary() instanceof DMClassLibrary) {
			Type returned = ((DMClassLibrary) context.getClassLibrary()).retrieveType(name, dimensions, context);
			if (returned == null) {
				returned = new FJPType(null, name, dimensions, context);
				((DMClassLibrary) context.getClassLibrary()).registerType(returned, name, dimensions, context);
			}
			return returned;
		}
		return new FJPType(null, name, dimensions, context);
	}

	@Override
	public String getName() {
		return name;
	}

}
