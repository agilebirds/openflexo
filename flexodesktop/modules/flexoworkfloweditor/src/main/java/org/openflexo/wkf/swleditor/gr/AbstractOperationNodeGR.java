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

import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public abstract class AbstractOperationNodeGR extends FlexoNodeGR<OperationNode> {

	public AbstractOperationNodeGR(OperationNode object, ShapeType shapeType, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(object, shapeType, aDrawing, isInPalet);
		setLayer(OPERATION_LAYER);
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

	public OperationNode getOperationNode() {
		return getDrawable();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setTextStyle(TextStyle.makeTextStyle(Color.BLACK,
				getWorkflow() != null ? getWorkflow().getOperationFont(WKFPreferences.getOperationNodeFont()).getFont() : WKFPreferences
						.getOperationNodeFont().getFont()));
	}
}
