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

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.impl.BackgroundStyleImpl;
import org.openflexo.fge.impl.ForegroundStyleImpl;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class NormalAbstractActivityNodeGR<O extends AbstractActivityNode> extends AbstractActivityNodeGR<O> {

	private static final Logger logger = Logger.getLogger(NormalAbstractActivityNodeGR.class.getPackage().getName());

	private ForegroundStyle foreground;

	public NormalAbstractActivityNodeGR(O activity, ShapeType shapeType, SwimmingLaneRepresentation drawing, boolean isInPalet) {
		super(activity, shapeType, drawing, isInPalet);
		setIsFloatingLabel(false);
		setRelativeTextX(0.5); // Center label horizontally
		setRelativeTextY(0.5); // No need to display role here !

		setMinimalWidth(NODE_MINIMAL_WIDTH);
		setMinimalHeight(NODE_MINIMAL_HEIGHT);
		// if (getText() != null && getText().equals("Activity-10"))
		// logger.info("setLocationConstraints() with "+getLocationConstrainedArea());
		setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);

		getShape().setIsRounded(true);

		foreground = ForegroundStyleImpl.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.2);
		setForeground(foreground);

		BackgroundStyle background = BackgroundStyleImpl.makeColorGradientBackground(getMainBgColor(), getOppositeBgColor(),
				ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		setBackground(background);
		// setDecorationPainter(new NodeDecorationPainter());
	}

	@Override
	public Rectangle getShape() {
		return (Rectangle) super.getShape();
	}

	@Override
	public double getWidth() {
		if (!getNode().hasDimensionForContext(SWIMMING_LANE_EDITOR)) {
			getNode().getWidth(SWIMMING_LANE_EDITOR, getDefaultWidth());
		}
		return getNode().getWidth(SWIMMING_LANE_EDITOR);
	}

	@Override
	public void setWidthNoNotification(double width) {
		getNode().setWidth(width, SWIMMING_LANE_EDITOR);
		resetLocationConstrainedArea();
	}

	public double getDefaultWidth() {
		return DEFAULT_ACTIVITY_WIDTH;
	}

	@Override
	public double getHeight() {
		if (!getNode().hasDimensionForContext(SWIMMING_LANE_EDITOR)) {
			getNode().getHeight(SWIMMING_LANE_EDITOR, getDefaultHeight());
		}
		return getNode().getHeight(SWIMMING_LANE_EDITOR);
	}

	@Override
	public void setHeightNoNotification(double height) {
		getNode().setHeight(height, SWIMMING_LANE_EDITOR);
		resetLocationConstrainedArea();
	}

	public double getDefaultHeight() {
		return DEFAULT_ACTIVITY_HEIGHT;
	}

	@Override
	public void notifyObjectHasResized() {
		super.notifyObjectHasResized();
		checkAndUpdateLocationIfRequired();
	}

	/*@Override
	public void setBorder(ShapeBorder border) 
	{
		super.setBorder(border);
		resetLocationConstrainedArea();
	}*/

}
