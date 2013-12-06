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

import org.openflexo.antar.binding.CustomType;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramType;

/**
 * Represent the type of a EditionPatternInstance of a given EditionPattern
 * 
 * @author sylvain
 * 
 */
public class EditionPatternInstanceType implements CustomType {


	protected EditionPattern editionPattern;

	
	public EditionPatternInstanceType(EditionPattern anEditionPattern) {
		this.editionPattern = anEditionPattern;
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	@Override
	public Class getBaseClass() {
		if (getEditionPattern() instanceof VirtualModel) {
			return VirtualModelInstance.class;
		} else {
			return EditionPatternInstance.class;
		}
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof EditionPatternInstanceType) {
			return editionPattern.isAssignableFrom(((EditionPatternInstanceType) aType).getEditionPattern());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "EditionPatternInstanceType" + ":" + editionPattern.toString();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "EditionPatternInstanceType" + ":" + editionPattern.toString();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}

	public static Type getEditionPatternInstanceType(EditionPattern anEditionPattern) {
		return anEditionPattern.getViewPoint().getInstanceType(anEditionPattern);
	}
}