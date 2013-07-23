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

import org.openflexo.fge.TextStyle;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public abstract class AbstractActionNodeGR extends FlexoNodeGR<ActionNode> {

	public AbstractActionNodeGR(ActionNode object, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(object, ShapeType.CIRCLE, aDrawing, isInPalet);
		setLayer(ACTION_LAYER);
		updatePropertiesFromWKFPreferences();
	}

	@Override
	int getTopBorder() {
		return isInPalette ? super.getTopBorder() : REQUIRED_SPACE_ON_TOP_FOR_CLOSING_BOX;
	}

	@Override
	int getBottomBorder() {
		return super.getBottomBorder();
	}

	@Override
	int getLeftBorder() {
		return super.getLeftBorder();
	}

	@Override
	int getRightBorder() {
		return super.getRightBorder();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setTextStyle(TextStyle.makeTextStyle(Color.BLACK,
				getWorkflow() != null ? getWorkflow().getActionFont(WKFPreferences.getActionNodeFont()).getFont() : WKFPreferences
						.getActionNodeFont().getFont()));
		setIsMultilineAllowed(true);
	}

	public ActionNode getActionNode() {
		return getDrawable();
	}

	@Override
	public Circle getShape() {
		return (Circle) super.getShape();
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultLabelX() {
		return 25;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultLabelY() {
		return 40 + getBorder().top;
	}

}
