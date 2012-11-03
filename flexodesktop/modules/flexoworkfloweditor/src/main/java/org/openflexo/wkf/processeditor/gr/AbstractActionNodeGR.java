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

import org.openflexo.fge.TextStyleImpl;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public abstract class AbstractActionNodeGR extends FlexoNodeGR<ActionNode> {

	public AbstractActionNodeGR(ActionNode object, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(object, ShapeType.CIRCLE, aDrawing, isInPalet);
		setLayer(ACTION_LAYER);
		updatePropertiesFromWKFPreferences();
	}

	@Override
	public int getTopBorder() {
		return (isInPalette ? super.getTopBorder() : REQUIRED_SPACE_ON_TOP_FOR_CLOSING_BOX);
	}

	@Override
	public int getBottomBorder() {
		return super.getBottomBorder();
	}

	@Override
	public int getLeftBorder() {
		return super.getLeftBorder();
	}

	@Override
	public int getRightBorder() {
		return super.getRightBorder();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setTextStyle(TextStyleImpl.makeTextStyle(Color.BLACK,
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
