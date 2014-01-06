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
package org.openflexo.wkf.swleditor.gr;

import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public abstract class WKFObjectGR<O extends WKFObject> extends SWLObjectGR<O> {

	public WKFObjectGR(O object, ShapeType shapeType, SwimmingLaneRepresentation aDrawing) {
		super(object, shapeType, aDrawing);
	}

	@Override
	public O getModel() {
		return super.getModel();
	}

	@Override
	public FlexoWorkflow getWorkflow() {
		return getModel().getWorkflow();
	}

	@Override
	public final boolean getIsVisible() {
		if (getDrawing() != null) {
			return getDrawing().isVisible(getModel());
		} else {
			return true;
		}
	}

}
