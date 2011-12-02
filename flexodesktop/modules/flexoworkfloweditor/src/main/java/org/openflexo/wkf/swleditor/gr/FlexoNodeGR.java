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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public abstract class FlexoNodeGR<O extends FlexoNode> extends PetriGraphNodeGR<O> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoNodeGR.class.getPackage().getName());

	public FlexoNodeGR(O node, ShapeType shapeType, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(node, shapeType, aDrawing, isInPalet);
	}

	@Override
	protected Vector<WKFNodeGR<?>> getFromInterestingNodeGR() {
		Vector<WKFNodeGR<?>> v = super.getFromInterestingNodeGR();
		for (FlexoPreCondition pc : getModel().getPreConditions()) {
			PreConditionGR pcgr = (PreConditionGR) getGraphicalRepresentation(pc);
			if (pcgr != null) {
				v.addAll(pcgr.getFromInterestingNodeGR());
			}
		}
		return v;
	}

	@Override
	protected void doLayoutMethod2() {
		if (getNode().isBeginOrEndNode()) {
			return;
		}
		super.doLayoutMethod2();
	}

}
