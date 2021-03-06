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

import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class BeginActivityNodeGR extends AbstractActivityNodeGR<ActivityNode> {

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private ForegroundStyle painterForeground;
	private BackgroundStyle painterBackground;

	public BeginActivityNodeGR(ActivityNode activityNode, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(activityNode, ShapeType.RECTANGLE, aDrawing, isInPalet);

		// Important: width is different from height here to avoid connector blinking when editing layout
		// This little difference allows a kind of hysteresis favourizing horizontal layout
		setWidth(31);
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

		setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);

		painterForeground = ForegroundStyle.makeStyle(Color.DARK_GRAY);
		painterForeground.setLineWidth(2.0);
		painterBackground = BackgroundStyle.makeColoredBackground(Color.DARK_GRAY);

		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useForegroundStyle(painterForeground);
				g.drawCircle(0.2, 0.2, 0.6, 0.6);
			}
		});

	}

	public ActivityNode getActivityNode() {
		return getDrawable();
	}

	@Override
	public Rectangle getShape() {
		return (Rectangle) super.getShape();
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double _getDefaultX() {
		return 0;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double _getDefaultY() {
		return getActivityNode().getParentPetriGraph().getIndexForBeginNode(getActivityNode()) * 80 + DEFAULT_BEGIN_Y_OFFSET;
	}

	@Override
	public double getDefaultLabelX() {
		if (getModel().hasLabelLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return getModel().getLabelLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR).getX();
		}
		return getLeftBorder() + 15;
	}

	@Override
	public double getDefaultLabelY() {
		if (getModel().hasLabelLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return getModel().getLabelLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR).getY();
		}
		return getTopBorder() + 40;
	}

}
