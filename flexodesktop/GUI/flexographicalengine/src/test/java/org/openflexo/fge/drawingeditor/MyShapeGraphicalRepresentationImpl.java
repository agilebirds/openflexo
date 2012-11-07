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

import java.util.logging.Logger;

import org.openflexo.fge.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.drawingeditor.MyDrawing.DrawingBuilder;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;

public class MyShapeGraphicalRepresentationImpl extends ShapeGraphicalRepresentationImpl<MyShape> implements MyShapeGraphicalRepresentation {
	private static final Logger logger = Logger.getLogger(MyShapeGraphicalRepresentationImpl.class.getPackage().getName());

	// Called by PAMELA, do not use
	public MyShapeGraphicalRepresentationImpl() {
	}

	// Called during LOAD
	public MyShapeGraphicalRepresentationImpl(DrawingBuilder builder) {
		this();
		setShapeType(ShapeType.RECTANGLE);
		setDrawing(builder.drawing.getEditedDrawing());
		// this(ShapeType.RECTANGLE, null, builder.drawing.getEditedDrawing());
		initializeDeserialization();
	}

	/*public MyShapeGraphicalRepresentationImpl(ShapeType shapeType, MyShape aDrawable, EditedDrawing aDrawing) {
		super(shapeType, aDrawable, aDrawing);
		addToMouseClickControls(new ShowContextualMenuControl());
		addToMouseDragControls(new DrawEdgeControl());
	}

	public MyShapeGraphicalRepresentationImpl(ShapeGraphicalRepresentation<?> aGR, MyShape aDrawable, EditedDrawing aDrawing) {
		super(aGR, aDrawable, aDrawing);
		setIsFocusable(true);
		setIsSelectable(true);
		setIsReadOnly(false);
		setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		addToMouseClickControls(new ShowContextualMenuControl());
		addToMouseDragControls(new DrawEdgeControl());
	}*/

	@Override
	public MyShapeView makeShapeView(DrawingController<?> controller) {
		return new MyShapeView(this, controller);
	}

	@SuppressWarnings("serial")
	public class MyShapeView extends ShapeView<MyShape> {
		public MyShapeView(MyShapeGraphicalRepresentationImpl aGraphicalRepresentation, DrawingController<?> controller) {
			super(aGraphicalRepresentation, controller);
		}

		@Override
		public MyDrawingController getController() {
			return (MyDrawingController) super.getController();
		}

		@Override
		public MyDrawingView getDrawingView() {
			return (MyDrawingView) super.getDrawingView();
		}

		/*	@Override
			public void update(Observable o, Object aNotification) {
				super.update(o, aNotification);
				if (aNotification instanceof FGENotification) {
					FGENotification notification = (FGENotification)aNotification;
					logger.info("notification="+notification);
				}
			}*/

	}

	/*@Override
	public void setBackground(BackgroundStyle aBackground) 
	{
		super.setBackground(aBackground);
		logger.info("********** setBackground with "+aBackground);
	}*/

}