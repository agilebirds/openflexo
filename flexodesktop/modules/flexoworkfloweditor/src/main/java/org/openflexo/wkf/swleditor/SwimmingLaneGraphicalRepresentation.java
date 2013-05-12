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
package org.openflexo.wkf.swleditor;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.wkf.processeditor.gr.WKFConnectorGR;

public class SwimmingLaneGraphicalRepresentation extends DrawingGraphicalRepresentation<FlexoProcess> {

	private SwimmingLaneRepresentation swimmingLaneRepresentation;

	public SwimmingLaneGraphicalRepresentation(SwimmingLaneRepresentation aDrawing, FlexoProcess process) {
		super(aDrawing);
		swimmingLaneRepresentation = aDrawing;
	}

	public SwimmingLaneRepresentation getSwimmingLaneRepresentation() {
		return swimmingLaneRepresentation;
	}

	@Override
	public double getWidth() {
		return swimmingLaneRepresentation.getSWLWidth();
	}

	@Override
	public void setWidth(double aValue) {
		swimmingLaneRepresentation.setSWLWidth(aValue);
	}

	@Override
	public double getHeight() {
		return swimmingLaneRepresentation.computeHeight();
	}

	public void updateAllEdgeLayers() {
		for (GraphicalRepresentation<?> processChild : getContainedGraphicalRepresentations()) {
			if (processChild instanceof WKFConnectorGR<?>) {
				((WKFConnectorGR<?>) processChild).updateLayer();
			}
		}
	}

}