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

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.BackgroundStyleImpl;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyleImpl;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.TextStyleImpl;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.RoleChanged;
import org.openflexo.foundation.wkf.dm.RoleColorChange;
import org.openflexo.foundation.wkf.dm.RoleNameChange;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class NormalAbstractActivityNodeGR<O extends AbstractActivityNode> extends AbstractActivityNodeGR<O> {

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	protected TextStyle roleLabelTextStyle;

	public NormalAbstractActivityNodeGR(O activity, ShapeType shapeType, ProcessRepresentation drawing, boolean isInPalet) {
		super(activity, shapeType, drawing, isInPalet);
		setIsFloatingLabel(false);
		getShape().setIsRounded(true);
		setRelativeTextX(0.5); // Center label horizontally
		foreground = ForegroundStyleImpl.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.2);
		setForeground(foreground);
		updateBackground();
		setMinimalWidth(NODE_MINIMAL_WIDTH);

		/*if (getSubProcessNode().getPortMapRegistery() != null) {
			setMinimalHeight(NODE_MINIMAL_HEIGHT+40);
		}
		else {*/
		setMinimalHeight(NODE_MINIMAL_HEIGHT);
		// }
		// setDecorationPainter(new NodeDecorationPainter());
	}

	@Override
	public boolean supportResizeToGrid() {
		return true;
	}

	@Override
	public FGESteppedDimensionConstraint getDimensionConstraintStep() {
		if (getDrawing() != null) {
			return getDrawing().getDrawingGraphicalRepresentation().getDimensionConstraintsForObject(this);
		}
		return null;
	}

	@Override
	public Rectangle getShape() {
		return (Rectangle) super.getShape();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		roleLabelTextStyle = TextStyleImpl.makeTextStyle(Color.GRAY, getRoleFont().getFont());
	}

	protected FlexoFont getRoleFont() {
		if (getWorkflow() != null) {
			return getWorkflow().getRoleFont(WKFPreferences.getRoleFont());
		} else {
			return WKFPreferences.getRoleFont();
		}
	}

	@Override
	public double getWidth() {
		if (!getNode().hasDimensionForContext(BASIC_PROCESS_EDITOR)) {
			getNode().getWidth(BASIC_PROCESS_EDITOR, getDefaultWidth());
		}
		return getNode().getWidth(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setWidthNoNotification(double width) {
		getNode().setWidth(width, BASIC_PROCESS_EDITOR);
	}

	public double getDefaultWidth() {
		return DEFAULT_ACTIVITY_WIDTH;
	}

	@Override
	public double getHeight() {
		if (!getNode().hasDimensionForContext(BASIC_PROCESS_EDITOR)) {
			getNode().getHeight(BASIC_PROCESS_EDITOR, getDefaultHeight());
		}
		return getNode().getHeight(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setHeightNoNotification(double height) {
		getNode().setHeight(height, BASIC_PROCESS_EDITOR);
	}

	public double getDefaultHeight() {
		return DEFAULT_ACTIVITY_HEIGHT;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof RoleColorChange || dataModification instanceof RoleChanged
				|| "color".equals(dataModification.propertyName())) {
			updateBackground();
			return;
		} else if (dataModification instanceof RoleNameChange) {
			notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
		} else if (dataModification instanceof ObjectSizeChanged) {
			checkAndUpdateDimensionIfRequired();
		}
		super.update(observable, dataModification);
	}

	private void updateBackground() {
		background = BackgroundStyleImpl.makeColorGradientBackground(getMainBgColor(), getOppositeBgColor(),
				ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		setBackground(background);
		updatePropertiesFromWKFPreferences();
	}

}
