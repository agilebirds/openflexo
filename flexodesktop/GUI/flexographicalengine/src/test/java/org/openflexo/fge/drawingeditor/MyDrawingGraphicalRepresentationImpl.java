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
package org.openflexo.fge.drawingeditor;

import org.openflexo.fge.DrawingGraphicalRepresentationImpl;
import org.openflexo.fge.drawingeditor.MyDrawing.DrawingBuilder;

public class MyDrawingGraphicalRepresentationImpl extends DrawingGraphicalRepresentationImpl<MyDrawing> implements
		MyDrawingGraphicalRepresentation {

	public MyDrawingGraphicalRepresentationImpl() {
		// TODO Auto-generated constructor stub
	}

	// Called for LOAD
	public MyDrawingGraphicalRepresentationImpl(DrawingBuilder builder) {
		this(builder.drawing.getEditedDrawing());
		initializeDeserialization();
	}

	// Called for NEW
	public MyDrawingGraphicalRepresentationImpl(EditedDrawing editedDrawing) {
		super(editedDrawing);

		/*MouseClickControl showContextualMenu 
		= MouseClickControl.makeMouseClickControl("Show contextual menu", MouseButton.RIGHT, 1,
				new CustomClickControlAction() {
			@Override
			public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, java.awt.event.MouseEvent event)
			{
				FGEView<?> view = controller.getDrawingView().viewForObject(graphicalRepresentation);
				Point newPoint = SwingUtilities.convertPoint(
						(Component)event.getSource(), 
						event.getPoint(), 
						(Component)view);
				((MyDrawingController)controller).showContextualMenu(graphicalRepresentation,view,newPoint);
				return false;
			}
		});*/
		addToMouseClickControls(new ShowContextualMenuControl());
	}
}
