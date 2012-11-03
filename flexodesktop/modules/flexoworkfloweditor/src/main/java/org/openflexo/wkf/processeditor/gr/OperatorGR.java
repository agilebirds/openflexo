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

import javax.swing.ImageIcon;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyleImpl;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyleImpl;
import org.openflexo.fge.TextStyleImpl;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SWLEditorConstants;

public abstract class OperatorGR<O extends OperatorNode> extends PetriGraphNodeGR<O> {

	private ForegroundStyle foreground;
	protected BackgroundStyle background;

	public OperatorGR(O operatorNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(operatorNode, ShapeType.LOSANGE, aDrawing, isInPalet);
		// setX(getOperatorNode().getPosX());
		// setY(getOperatorNode().getPosY());
		setWidth(35);
		setHeight(35);

		// setText(getOperatorNode().getName());
		// setAbsoluteTextX(getOperatorNode().getNodeLabelPosX());
		// setAbsoluteTextY(getOperatorNode().getNodeLabelPosY());
		setIsFloatingLabel(true);

		foreground = ForegroundStyleImpl.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.6);

		if (getImageIcon() != null) {
			background = BackgroundStyleImpl.makeImageBackground(getImageIcon());
			((BackgroundStyle.BackgroundImage) background).setScaleX(1);
			((BackgroundStyle.BackgroundImage) background).setScaleY(1);
			((BackgroundStyle.BackgroundImage) background).setDeltaX(-2);
			((BackgroundStyle.BackgroundImage) background).setDeltaY(-3);
		} else {
			background = BackgroundStyleImpl.makeEmptyBackground();
		}

		setForeground(foreground);
		setBackground(background);

		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		if (getOperatorNode().getLevel() == FlexoLevel.ACTIVITY) {
			setLayer(ACTIVITY_LAYER);
		} else if (getOperatorNode().getLevel() == FlexoLevel.OPERATION) {
			setLayer(OPERATION_LAYER);
		} else if (getOperatorNode().getLevel() == FlexoLevel.ACTION) {
			setLayer(ACTION_LAYER);
		}

		updatePropertiesFromWKFPreferences();

	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setTextStyle(TextStyleImpl.makeTextStyle(Color.BLACK,
				getWorkflow() != null ? getWorkflow().getEventFont(WKFPreferences.getEventNodeFont()).getFont() : WKFPreferences
						.getEventNodeFont().getFont()));
		setIsMultilineAllowed(true);
		getShadowStyle().setShadowDepth(1);
		getShadowStyle().setShadowBlur(3);
	}

	public O getOperatorNode() {
		return getDrawable();
	}

	/*public Losange getShape()
	{
		return (Losange)super.getShape();
	}*/

	public abstract ImageIcon getImageIcon();

	@Override
	public double getDefaultLabelX() {
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getX();
		}
		return getLeftBorder() + 15;
	}

	@Override
	public double getDefaultLabelY() {
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getY();
		}
		return getTopBorder() + getHeight() + getTextStyle().getFont().getSize() + 5;
	}

}
