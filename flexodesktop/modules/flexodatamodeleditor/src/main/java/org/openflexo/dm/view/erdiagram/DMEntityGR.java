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
package org.openflexo.dm.view.erdiagram;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.PropertyRegistered;
import org.openflexo.foundation.dm.dm.PropertyUnregistered;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.toolbox.ToolBox;

public class DMEntityGR extends ShapeGraphicalRepresentation<DMEntity> implements GraphicalFlexoObserver, ERDiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DMEntityGR.class.getPackage().getName());

	private final ForegroundStyle foreground;
	private final BackgroundStyle background;
	private final BackgroundStyle headerBackground;

	public DMEntityGR(DMEntity aDMEntity, Drawing<?> aDrawing) {
		super(ShapeType.RECTANGLE, aDMEntity, aDrawing);
		// setText(getRole().getName());
		setIsFloatingLabel(false);
		getShape().setIsRounded(false);
		setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
		updateStyles();
		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(ENTITY_BORDER, ENTITY_BORDER, ENTITY_BORDER, ENTITY_BORDER));

		setWidth(getDefaultWidth());

		setTextStyle(TextStyle.makeTextStyle(Color.BLACK, ENTITY_FONT));

		addToMouseClickControls(new ERDiagramController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new ERDiagramController.ShowContextualMenuControl(true));
		}
		// addToMouseDragControls(new DrawRoleSpecializationControl());

		aDMEntity.addObserver(this);

		foreground = ForegroundStyle.makeStyle(Color.DARK_GRAY);
		background = BackgroundStyle.makeColoredBackground(Color.WHITE);
		headerBackground = BackgroundStyle.makeColoredBackground(Color.LIGHT_GRAY);

		setDecorationPainter(new DecorationPainter() {
			@Override
			public void paintDecoration(org.openflexo.fge.graphics.FGEShapeDecorationGraphics g) {
				double border = ENTITY_BORDER;
				g.useBackgroundStyle(headerBackground);
				g.fillRect(border, border, g.getWidth()/*-2*border*/- 1, HEADER_HEIGHT);
				g.useForegroundStyle(foreground);
				g.drawRect(border, border, g.getWidth()/*-2*border*/- 1, HEADER_HEIGHT);
				g.drawImage(DMEIconLibrary.iconForObject(getEntity()).getImage(), new FGEPoint(ENTITY_BORDER * 2, ENTITY_BORDER * 1.6));
			};

			@Override
			public boolean paintBeforeShape() {
				return false;
			}
		});

	}

	private void updateStyles() {
		/*foreground = ForegroundStyle.makeStyle(getEntity().getColor());
		foreground.setLineWidth(2);
		background = BackgroundStyle.makeColorGradientBackground(getRole().getColor(), Color.WHITE, ColorGradientDirection.SOUTH_WEST_NORTH_EAST);
		setForeground(foreground);
		setBackground(background);*/
	}

	@Override
	public ERDiagramRepresentation getDrawing() {
		return (ERDiagramRepresentation) super.getDrawing();
	}

	@Override
	public double getRelativeTextX() {
		return 0.5;
	}

	@Override
	public double getRelativeTextY() {
		return HEADER_HEIGHT / 2 / getHeight();
		/*Dimension labelSize = getNormalizedLabelSize();
		double absoluteCenterY = labelSize.height/2;
		return absoluteCenterY/getHeight();*/
	}

	private boolean isUpdatingPosition = false;

	private String getContext() {
		return "diagram_" + getDrawing().getDiagram().getFlexoID();
	}

	@Override
	public double getX() {
		if (!getEntity().hasLocationForContext(getContext())) {
			getEntity().getX(getContext(), getDefaultX());
		}
		return getEntity().getX(getContext());
	}

	@Override
	public void setXNoNotification(double posX) {
		isUpdatingPosition = true;
		getEntity().setX(posX, getContext());
		isUpdatingPosition = false;
	}

	@Override
	public double getY() {
		if (!getEntity().hasLocationForContext(getContext())) {
			getEntity().getY(getContext(), getDefaultY());
		}
		return getEntity().getY(getContext());
	}

	@Override
	public void setYNoNotification(double posY) {
		isUpdatingPosition = true;
		getEntity().setY(posY, getContext());
		isUpdatingPosition = false;
	}

	// Override to implement defaut automatic layout
	public double getDefaultX() {
		return 0;
	}

	// Override to implement defaut automatic layout
	public double getDefaultY() {
		return 0;
	}

	/*@Override
	public void setWidthNoNotification(double aValue) 
	{
		super.setWidthNoNotification(aValue);
		System.out.println("set width to "+aValue);
		for (GraphicalRepresentation child : getContainedGraphicalRepresentations()) {
			if (child instanceof DMPropertyGR) {
				((DMPropertyGR)child).notifyObjectHasResized();
			}
		}
	}*/

	/*@Override
	public double getWidth() 
	{
		if (!getEntity().hasDimensionForContext(getContext())) {
			getEntity().getWidth(getContext(),getDefaultWidth());
		}
		return getEntity().getWidth(getContext());
	}

	@Override
	public void setWidthNoNotification(double width) 
	{
		getEntity().setWidth(width,getContext());
	}

	@Override
	public double getHeight() 
	{
		if (!getEntity().hasDimensionForContext(getContext())) {
			getEntity().getHeight(getContext(),getDefaultHeight());
		}
		return getEntity().getHeight(getContext());
	}

	@Override
	public void setHeightNoNotification(double height)
	{
		getEntity().setHeight(height,getContext());
	}*/

	/*public double getWidth()
	{
		return getDefaultWidth();
	}*/

	@Override
	public double getHeight() {
		return getDefaultHeight();
	}

	// Override to implement defaut automatic layout
	public double getDefaultWidth() {
		return WIDTH;
	}

	// Override to implement defaut automatic layout
	public double getDefaultHeight() {
		return getEntity().getOrderedProperties().size() * PROPERTY_HEIGHT + HEADER_HEIGHT + 2 * PROPERTY_BORDER;
	}

	@Override
	public String getText() {
		return getEntity().getLocalizedName();
	}

	@Override
	public void setTextNoNotification(String text) {
		/*try {
			getEntity().setName(text);
		} catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public DMEntity getEntity() {
		return getDrawable();
	}

	@Override
	public Rectangle getShape() {
		return (Rectangle) super.getShape();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getEntity()) {
			if (dataModification instanceof PropertyRegistered) {
				getDrawing().updateGraphicalObjectsHierarchy();
				for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
					if (gr instanceof DMPropertyGR) {
						((DMPropertyGR) gr).notifyObjectMoved();
					}
				}
				notifyObjectResized();
				notifyShapeNeedsToBeRedrawn();
			} else if (dataModification instanceof PropertyUnregistered) {
				getDrawing().updateGraphicalObjectsHierarchy();
				for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
					if (gr instanceof DMPropertyGR) {
						((DMPropertyGR) gr).notifyObjectMoved();
					}
				}
				notifyObjectResized();
				notifyShapeNeedsToBeRedrawn();
			} else if (dataModification instanceof DMAttributeDataModification) {
				notifyShapeNeedsToBeRedrawn();
			} else if (dataModification instanceof ObjectLocationChanged) {
				if (!isUpdatingPosition) {
					notifyObjectMoved();
				}
			}
		}
	}

	@Override
	public void notifyObjectHasResized() {
		super.notifyObjectHasResized();
		for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof DMPropertyGR) {
				((DMPropertyGR) gr).notifyObjectResized();
			}
		}
	}
}
