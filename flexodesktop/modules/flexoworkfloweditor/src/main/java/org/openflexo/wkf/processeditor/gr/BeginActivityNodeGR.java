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
package org.openflexo.wkf.processeditor.gr;

import java.awt.Color;

import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SWLEditorConstants;


public class BeginActivityNodeGR extends AbstractActivityNodeGR<ActivityNode> {

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private ForegroundStyle painterForeground;
	private BackgroundStyle painterBackground;


	public BeginActivityNodeGR(ActivityNode activityNode, ProcessRepresentation aDrawing, boolean isInPalet)
	{
		super (activityNode, ShapeType.RECTANGLE, aDrawing, isInPalet);

		// Important: width is different from height here to avoid connector blinking when editing layout
		// This little difference allows a kind of hysteresis favourizing horizontal layout
		setWidth(30.1);
		setHeight(30);

		setIsFloatingLabel(true);
		getShape().setIsRounded(true);
		getShape().setArcSize(20);
		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		foreground.setLineWidth(1.0);
		background = BackgroundStyle.makeColoredBackground(getMainBgColor());
		setForeground(foreground);
		setBackground(background);

		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		painterForeground = ForegroundStyle.makeStyle(Color.DARK_GRAY);
		painterForeground.setLineWidth(2.0);
		painterBackground = BackgroundStyle.makeColoredBackground(Color.DARK_GRAY);

		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g)
			{
				g.useForegroundStyle(painterForeground);
				g.drawCircle(0.2, 0.2, 0.6, 0.6);
			}
		});

	}

	@Override
	public Color getMainBgColor() {
		return BG_COLOR;
	}

	public ActivityNode getActivityNode()
	{
		return getDrawable();
	}

	@Override
	public Rectangle getShape()
	{
		return (Rectangle)super.getShape();
	}

	private boolean isInRoot() {
		return getActivityNode().isInRootPetriGraph();
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double _getDefaultX()
	{
		return isInRoot()?50:0;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double _getDefaultY()
	{
		return (getActivityNode().getParentPetriGraph().getIndexForBeginNode(getActivityNode()) * 80)+(isInRoot()?200:0)+DEFAULT_BEGIN_Y_OFFSET+10;
	}

	@Override
	public double getDefaultLabelX() 
	{
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR))
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getX();
		return getLeftBorder()+15;
	}

	@Override
	public double getDefaultLabelY() 
	{
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR))
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getY();
		return getTopBorder()+40;
	}


}
