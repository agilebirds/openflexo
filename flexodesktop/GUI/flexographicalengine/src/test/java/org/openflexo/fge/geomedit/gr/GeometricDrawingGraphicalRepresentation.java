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
package org.openflexo.fge.geomedit.gr;

import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.ShowContextualMenuControl;
import org.openflexo.fge.impl.DrawingGraphicalRepresentationImpl;
import org.openflexo.xmlcode.XMLSerializable;

public class GeometricDrawingGraphicalRepresentation extends DrawingGraphicalRepresentationImpl<GeometricSet> implements XMLSerializable {

	// Called for LOAD
	public GeometricDrawingGraphicalRepresentation(GeomEditBuilder builder) {
		this(builder.drawing);
		initializeDeserialization();
	}

	// Called for NEW
	public GeometricDrawingGraphicalRepresentation(GeometricDrawing editedDrawing) {
		super();
		setDrawing(editedDrawing);
		setWidth(10000);
		setHeight(10000);

		/*MouseClickControl showContextualMenu 
		= MouseClickControl.makeMouseClickControl("Show contextual menu", MouseButton.RIGHT, 1,
				new CustomClickControlAction() {
			@Override
			public boolean handleClick(GraphicalRepresentation graphicalRepresentation, DrawingController controller, java.awt.event.MouseEvent event)
			{
				FGEView view = controller.getDrawingView().viewForObject(graphicalRepresentation);
				Point newPoint = SwingUtilities.convertPoint(
						(Component)event.getSource(), 

						(Component)view);
				((MyDrawingController)controller).showContextualMenu(graphicalRepresentation,view,newPoint);
				return false;
			}
		});*/
		addToMouseClickControls(new ShowContextualMenuControl());
	}

	@Override
	public String getInspectorName() {
		return "Drawing.inspector";
	}

}
