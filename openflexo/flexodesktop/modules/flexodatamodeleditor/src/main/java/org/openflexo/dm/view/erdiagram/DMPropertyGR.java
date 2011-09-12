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
import java.awt.Dimension;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DuplicatePropertyNameException;
import org.openflexo.toolbox.ToolBox;


public class DMPropertyGR extends ShapeGraphicalRepresentation<DMProperty> implements GraphicalFlexoObserver, ERDiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DMPropertyGR.class.getPackage().getName());

	public static final int WIDTH = 100;
	public static final int HEIGHT = 40;
	
	private TextStyle propertyNameStyle;
	private TextStyle propertyTypeStyle;

	private BackgroundStyle unfocusedBackground;
	private BackgroundStyle focusedBackground;
	private BackgroundStyle selectedBackground;
	

	public DMPropertyGR(DMProperty aDMProperty, Drawing<?> aDrawing) 
	{
		super(ShapeType.RECTANGLE, aDMProperty, aDrawing);
		//setText(getRole().getName());
		setIsFloatingLabel(false);
		getShape().setIsRounded(false);
		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		updateStyles();
		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(10,10,10,10));
		
		propertyNameStyle = TextStyle.makeTextStyle(Color.DARK_GRAY, ATTRIBUTE_FONT);
		propertyTypeStyle = TextStyle.makeTextStyle(Color.GRAY, ATTRIBUTE_FONT);

		setTextStyle(propertyNameStyle);
		
		setForeground(ForegroundStyle.makeNone());
		setShadowStyle(ShadowStyle.makeNone());
		
		unfocusedBackground = BackgroundStyle.makeEmptyBackground();
		focusedBackground = BackgroundStyle.makeColoredBackground(FOCUSED_COLOR);
		selectedBackground = BackgroundStyle.makeColoredBackground(SELECTED_COLOR);
		setBackground(unfocusedBackground);
		
		
		setIsFocusable(true);
		setDrawControlPointsWhenFocused(false);
		setDrawControlPointsWhenSelected(false);
		
		addToMouseClickControls(new ERDiagramController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM()!=ToolBox.MACOS) {
			addToMouseClickControls(new ERDiagramController.ShowContextualMenuControl(true));
		}
		//addToMouseDragControls(new DrawRoleSpecializationControl());
		
		aDMProperty.addObserver(this);
		
		setDecorationPainter(new DecorationPainter() {
			@Override
			public void paintDecoration(org.openflexo.fge.graphics.FGEShapeDecorationGraphics g) {
				g.useTextStyle(propertyTypeStyle);
				g.drawString(getProperty().getTypeStringRepresentation(), g.getWidth()-PROPERTY_BORDER*3, 20,TextAlignment.RIGHT);
			};
			
			@Override
			public boolean paintBeforeShape()
			{
				return false;
			}
		});


	}
	
	
	private void updateStyles()
	{
		/*foreground = ForegroundStyle.makeStyle(getEntity().getColor());
		foreground.setLineWidth(2);
		background = BackgroundStyle.makeColorGradientBackground(getRole().getColor(), Color.WHITE, ColorGradientDirection.SOUTH_WEST_NORTH_EAST);
		setForeground(foreground);
		setBackground(background);*/
	}
	
	@Override
	public ERDiagramRepresentation getDrawing() 
	{
		return (ERDiagramRepresentation)super.getDrawing();
	}
	
	@Override
	public double getRelativeTextX() 
	{
		Dimension labelSize = getNormalizedLabelSize();
		double absoluteCenterX = labelSize.width/2;
		return absoluteCenterX/getWidth();
	}
	
	@Override
	public double getRelativeTextY() 
	{
		return 0.5;
	}
	

	@Override
	public String getText() 
	{
		return getProperty().getName();
	}
	
	@Override
	public void setTextNoNotification(String text) 
	{
		try {
			getProperty().setName(text);
		} catch (DuplicatePropertyNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DMProperty getProperty()
	{
		return getDrawable();
	}
	
	@Override
	public Rectangle getShape()
	{
		return (Rectangle)super.getShape();
	}
	
	public int getIndex()
	{
		if (getProperty() == null || getProperty().getEntity() == null) return -1;
		return getProperty().getEntity().getOrderedProperties().indexOf(getProperty());
	}
	
	@Override
	public double getX() 
	{
		return 1;
	}
	
	@Override
	public double getY()
	{
		return (getIndex()*PROPERTY_HEIGHT)+HEADER_HEIGHT+PROPERTY_BORDER;
	}
	
	@Override
	public double getWidth()
	{
		GraphicalRepresentation<?> container = getContainerGraphicalRepresentation();
		if (container instanceof DMEntityGR) return ((DMEntityGR)container).getWidth()-1;
		return WIDTH;
	}
	
	@Override
	public double getHeight()
	{
		return PROPERTY_HEIGHT;
	}
	
	@Override
	public void setIsSelected(boolean aFlag)
	{
		boolean old = getIsSelected();
		super.setIsSelected(aFlag);
		if (old != aFlag) {
			if (aFlag) {
				setBackground(selectedBackground);
			}
			else if (!getIsFocused()) {
				setBackground(unfocusedBackground);
			}
			notifyShapeNeedsToBeRedrawn();
		}
	}

	@Override
	public void setIsFocused(boolean aFlag) 
	{
		boolean old = getIsFocused();
		super.setIsFocused(aFlag);
		if (old != aFlag) {
			if (aFlag) {
				setBackground(focusedBackground);
			}
			else if (!getIsSelected()) {
				setBackground(unfocusedBackground);
			}
			notifyShapeNeedsToBeRedrawn();
		}
	}

	@Override
	public void update (FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getProperty()) {
		}
	}

}
