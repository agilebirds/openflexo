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

package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

/**
 * Represent the type of a DiagramInstance of a given Diagram
 * 
 * @author sylvain
 * 
 */
public class VirtualModelInstanceType extends EditionPatternInstanceType {

	public VirtualModelInstanceType(VirtualModel aVirtualModel) {
		super(aVirtualModel);
		this.editionPattern = aVirtualModel;
	}

	public VirtualModel getVirtualModel() {
		return (VirtualModel) editionPattern;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		if (aType instanceof VirtualModelInstanceType) {
			// TODO: Permissive for now!
			return true;
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "VirtualModel" + ":" + editionPattern;
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "VirtualModel" + ":" + editionPattern;
	}

}