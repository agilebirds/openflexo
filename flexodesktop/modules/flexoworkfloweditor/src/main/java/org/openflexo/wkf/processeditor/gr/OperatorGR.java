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

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
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
		// setText(getOperatorNode().getName());
		// setAbsoluteTextX(getOperatorNode().getNodeLabelPosX());
		// setAbsoluteTextY(getOperatorNode().getNodeLabelPosY());
		setIsFloatingLabel(true);

		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.6);

		setForeground(foreground);

		updateResizable();

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
	public double getWidth() {
		if (isResizable()) {
			if (!getNode().hasDimensionForContext(BASIC_PROCESS_EDITOR)) {
				getNode().getWidth(BASIC_PROCESS_EDITOR, DEFAULT_ACTIVITY_WIDTH);
			}
			return getNode().getWidth(BASIC_PROCESS_EDITOR);
		} else {
			return 35;
		}
	}

	@Override
	public void setWidthNoNotification(double width) {
		getNode().setWidth(width, BASIC_PROCESS_EDITOR);
	}

	@Override
	public double getHeight() {
		if (isResizable()) {
			if (!getNode().hasDimensionForContext(BASIC_PROCESS_EDITOR)) {
				getNode().getHeight(BASIC_PROCESS_EDITOR, DEFAULT_ACTIVITY_HEIGHT);
			}
			return getNode().getHeight(BASIC_PROCESS_EDITOR);
		} else {
			return 35;
		}
	}

	@Override
	public void setHeightNoNotification(double height) {
		getNode().setHeight(height, BASIC_PROCESS_EDITOR);
	}

	private void updateResizable() {
		if (isResizable()) {
			setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
			setAdjustMinimalWidthToLabelWidth(false);
			setAdjustMinimalHeightToLabelHeight(false);
			setIsFloatingLabel(false);
			setBackground(BackgroundStyle.makeEmptyBackground());
		} else {
			setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
			setIsFloatingLabel(true);
		}
		if (getImageIcon() != null) {
			background = BackgroundStyle.makeImageBackground(getImageIcon());
			((BackgroundImageBackgroundStyle) background).setScaleX(1);
			((BackgroundImageBackgroundStyle) background).setScaleY(1);
			((BackgroundImageBackgroundStyle) background).setDeltaX(-2);
			((BackgroundImageBackgroundStyle) background).setDeltaY(-3);
		} else {
			background = BackgroundStyle.makeColoredBackground(Color.WHITE);
		}
		setBackground(background);
		notifyObjectResized();
		notifyShapeNeedsToBeRedrawn();
	}

	protected boolean isResizable() {
		return getOperatorNode().isResizable(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setTextStyle(TextStyle.makeTextStyle(Color.BLACK,
				getWorkflow() != null ? getWorkflow().getEventFont(WKFPreferences.getEventNodeFont()).getFont() : WKFPreferences
						.getEventNodeFont().getFont()));
		setIsMultilineAllowed(true);
		/*getShadowStyle().setShadowDepth(1);
		getShadowStyle().setShadowBlur(3);*/
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (getDrawable().getResizableKeyForContext(BASIC_PROCESS_EDITOR).equals(dataModification.propertyName())) {
			updateResizable();
		}
		super.update(observable, dataModification);
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
