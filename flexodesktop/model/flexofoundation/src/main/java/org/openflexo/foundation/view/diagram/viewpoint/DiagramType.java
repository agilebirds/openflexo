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

package org.openflexo.foundation.view.diagram.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.CustomType;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceType;
import org.openflexo.foundation.viewpoint.VirtualModel;

/**
 * Represent the type of a DiagramInstance of a given Diagram
 * 
 * @author sylvain
 * 
 */
public class DiagramType extends EditionPatternInstanceType {

	public static DiagramType getDiagramType(DiagramSpecification aDiagram) {
		if (aDiagram == null) {
			return null;
		}
		
		return aDiagram.getInstanceType();
	}

	private DiagramSpecification diagramSpec;

	public DiagramType(DiagramSpecification aDiagramSpec) {
		super(aDiagramSpec);
		this.diagramSpec = aDiagramSpec;
	}

	public DiagramSpecification getDiagram() {
		return diagramSpec;
	}

	@Override
	public Class getBaseClass() {
		if (getDiagram() instanceof VirtualModel) {
			return VirtualModelInstance.class;
		} else {
			return Diagram.class;
		}
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof DiagramType) {
			return diagramSpec.isAssignableFrom(((DiagramType) aType).getDiagram());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "DiagramType" + ":" + diagramSpec;
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "DiagramType" + ":" + diagramSpec;
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}
}