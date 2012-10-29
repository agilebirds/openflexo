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
package org.openflexo.fge;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.graphics.FGEGeometricGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represents a geometric object in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
@ModelEntity
@ImplementationClass(GeometricGraphicalRepresentationImpl.class)
public interface GeometricGraphicalRepresentation<O> extends GraphicalRepresentation<O> {

	public static enum GeometricParameters implements GRParameter {
		foreground, background, layer, geometricObject
	}

	@Override
	public abstract Vector<GRParameter> getAllParameters();

	@Override
	public abstract void delete();

	public abstract ForegroundStyle getForeground();

	public abstract void setForeground(ForegroundStyle aForeground);

	public abstract boolean getNoStroke();

	public abstract void setNoStroke(boolean noStroke);

	public abstract BackgroundStyle getBackground();

	public abstract void setBackground(BackgroundStyle aBackground);

	public abstract BackgroundStyleType getBackgroundType();

	public abstract void setBackgroundType(BackgroundStyleType backgroundType);

	@Override
	public abstract int getLayer();

	@Override
	public abstract void setLayer(int layer);

	@Override
	public abstract int getViewX(double scale);

	@Override
	public abstract int getViewY(double scale);

	@Override
	public abstract int getViewWidth(double scale);

	@Override
	public abstract int getViewHeight(double scale);

	@Override
	public abstract Rectangle getViewBounds(double scale);

	public abstract Rectangle getBounds(double scale);

	@Override
	public abstract FGERectangle getNormalizedBounds();

	@Override
	public abstract boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	@Override
	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	@Override
	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	public abstract void paintGeometricObject(FGEGeometricGraphics graphics);

	@Override
	public abstract void paint(Graphics g, DrawingController<?> controller);

	@Override
	public abstract boolean hasFloatingLabel();

	@Override
	public abstract String getInspectorName();

	@Override
	public abstract void update(Observable observable, Object notification);

	public abstract FGEArea getGeometricObject();

	public abstract void setGeometricObject(FGEArea geometricObject);

	public abstract List<ControlPoint> getControlPoints();

	public abstract List<ControlPoint> rebuildControlPoints();

	public abstract void notifyGeometryChanged();

}
