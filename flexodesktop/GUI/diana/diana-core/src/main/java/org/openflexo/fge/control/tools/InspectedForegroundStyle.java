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
package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.util.List;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.control.DianaInteractiveViewer;

/**
 * Implementation of {@link ForegroundStyle}, as a container of graphical properties synchronized with a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedForegroundStyle extends InspectedStyle<ForegroundStyle> implements ForegroundStyle {

	public InspectedForegroundStyle(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, controller != null ? controller.getFactory().makeForegroundStyle(Color.BLACK) : null);
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelection() {
		return getController().getSelectedObjects(ShapeNode.class, ConnectorNode.class, GeometricNode.class);
	}

	@Override
	public ForegroundStyle getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof ShapeNode) {
			return ((ShapeNode<?>) node).getForegroundStyle();
		} else if (node instanceof ConnectorNode) {
			return ((ConnectorNode<?>) node).getForegroundStyle();
		} else if (node instanceof GeometricNode) {
			return ((GeometricNode<?>) node).getForegroundStyle();
		}

		// Not applicable
		return null;
	}

	@Override
	public Color getColor() {
		return getPropertyValue(ForegroundStyle.COLOR);
	}

	@Override
	public void setColor(Color aColor) {
		setPropertyValue(ForegroundStyle.COLOR, aColor);
	}

	@Override
	public double getLineWidth() {
		return getPropertyValue(ForegroundStyle.LINE_WIDTH);
	}

	@Override
	public void setLineWidth(double aLineWidth) {
		setPropertyValue(ForegroundStyle.LINE_WIDTH, aLineWidth);
	}

	@Override
	public CapStyle getCapStyle() {
		return getPropertyValue(ForegroundStyle.CAP_STYLE);
	}

	@Override
	public void setCapStyle(CapStyle aCapStyle) {
		setPropertyValue(ForegroundStyle.CAP_STYLE, aCapStyle);
	}

	@Override
	public JoinStyle getJoinStyle() {
		return getPropertyValue(ForegroundStyle.JOIN_STYLE);
	}

	@Override
	public void setJoinStyle(JoinStyle aJoinStyle) {
		setPropertyValue(ForegroundStyle.JOIN_STYLE, aJoinStyle);
	}

	@Override
	public DashStyle getDashStyle() {
		return getPropertyValue(ForegroundStyle.DASH_STYLE);
	}

	@Override
	public void setDashStyle(DashStyle aDashStyle) {
		setPropertyValue(ForegroundStyle.DASH_STYLE, aDashStyle);
	}

	@Override
	public boolean getNoStroke() {
		return getPropertyValue(ForegroundStyle.NO_STROKE);
	}

	@Override
	public void setNoStroke(boolean aFlag) {
		setPropertyValue(ForegroundStyle.NO_STROKE, aFlag);
	}

	@Override
	public float getTransparencyLevel() {
		return getPropertyValue(ForegroundStyle.TRANSPARENCY_LEVEL);
	}

	@Override
	public void setTransparencyLevel(float aLevel) {
		setPropertyValue(ForegroundStyle.TRANSPARENCY_LEVEL, aLevel);
	}

	@Override
	public boolean getUseTransparency() {
		return getPropertyValue(ForegroundStyle.USE_TRANSPARENCY);
	}

	@Override
	public void setUseTransparency(boolean aFlag) {
		setPropertyValue(ForegroundStyle.USE_TRANSPARENCY, aFlag);
	}

	@Override
	public void setColorNoNotification(Color aColor) {
		// TODO remove this
	}

	@Override
	public String toNiceString() {
		// TODO remove this
		return toString();
	}

}
