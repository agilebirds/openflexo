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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represents a connector linking two shapes in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
@ModelEntity
@ImplementationClass(ConnectorGraphicalRepresentationImpl.class)
public interface ConnectorGraphicalRepresentation<O> extends GraphicalRepresentation<O> {

	public static enum ConnectorParameters implements GRParameter {
		connector,
		foreground,
		selectedForeground,
		focusedForeground,
		hasSelectedForeground,
		hasFocusedForeground,
		startSymbol,
		endSymbol,
		middleSymbol,
		startSymbolSize,
		endSymbolSize,
		middleSymbolSize,
		relativeMiddleSymbolLocation,
		applyForegroundToSymbols,
		debugCoveringArea
	}

	@Override
	public abstract void delete();

	@Override
	public abstract Vector<GRParameter> getAllParameters();

	@Override
	public abstract void setsWith(GraphicalRepresentation<?> gr);

	@Override
	public abstract void setsWith(GraphicalRepresentation<?> gr, GRParameter... exceptedParameters);

	public abstract Connector getConnector();

	public abstract void setConnector(Connector aConnector);

	public abstract ForegroundStyle getForeground();

	public abstract void setForeground(ForegroundStyle aForeground);

	public abstract ForegroundStyle getSelectedForeground();

	public abstract void setSelectedForeground(ForegroundStyle aForeground);

	public abstract boolean getHasSelectedForeground();

	public abstract void setHasSelectedForeground(boolean aFlag);

	public abstract ForegroundStyle getFocusedForeground();

	public abstract void setFocusedForeground(ForegroundStyle aForeground);

	public abstract boolean getHasFocusedForeground();

	public abstract void setHasFocusedForeground(boolean aFlag);

	@Override
	public abstract boolean shouldBeDisplayed();

	public abstract void notifyConnectorChanged();

	public abstract ConnectorType getConnectorType();

	public abstract void setConnectorType(ConnectorType connectorType);

	@Override
	public abstract void setText(String text);

	public abstract ShapeGraphicalRepresentation<?> getStartObject();

	public abstract void setStartObject(ShapeGraphicalRepresentation<?> aStartObject);

	public abstract ShapeGraphicalRepresentation<?> getEndObject();

	public abstract void setEndObject(ShapeGraphicalRepresentation<?> anEndObject);

	public abstract void observeRelevantObjects();

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

	public abstract int getExtendedX(double scale);

	public abstract int getExtendedY(double scale);

	/**
	 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the two
	 * related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
	 */
	public abstract Rectangle getNormalizedBounds(double scale);

	@Override
	public abstract boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	@Override
	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	@Override
	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	/**
	 * Return distance from point to connector representation with a given scale
	 * 
	 * @param aPoint
	 *            expressed in local normalized coordinates system
	 * @param scale
	 * @return
	 */
	public abstract double distanceToConnector(FGEPoint aPoint, double scale);

	@Override
	public abstract void paint(Graphics g, DrawingController<?> controller);

	@Override
	public abstract Point getLabelLocation(double scale);

	@Override
	public abstract void setLabelLocation(Point point, double scale);

	@Override
	public abstract boolean hasFloatingLabel();

	@Override
	public abstract String getInspectorName();

	@Override
	public abstract void update(Observable observable, Object notification);

	public boolean isConnectorConsistent();

	public abstract void refreshConnector();

	@Override
	public abstract void setRegistered(boolean aFlag);

	// Override for a custom view management
	public abstract ConnectorView<O> makeConnectorView(DrawingController<?> controller);

	public abstract EndSymbolType getEndSymbol();

	public abstract void setEndSymbol(EndSymbolType endSymbol);

	public abstract double getEndSymbolSize();

	public abstract void setEndSymbolSize(double endSymbolSize);

	public abstract MiddleSymbolType getMiddleSymbol();

	public abstract void setMiddleSymbol(MiddleSymbolType middleSymbol);

	public abstract double getMiddleSymbolSize();

	public abstract void setMiddleSymbolSize(double middleSymbolSize);

	public abstract StartSymbolType getStartSymbol();

	public abstract void setStartSymbol(StartSymbolType startSymbol);

	public abstract double getStartSymbolSize();

	public abstract void setStartSymbolSize(double startSymbolSize);

	public abstract double getRelativeMiddleSymbolLocation();

	public abstract void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation);

	public abstract boolean getApplyForegroundToSymbols();

	public abstract void setApplyForegroundToSymbols(boolean applyForegroundToSymbols);

	public abstract boolean getDebugCoveringArea();

	public abstract void setDebugCoveringArea(boolean debugCoveringArea);

	// Override when required
	@Override
	public abstract void notifyObjectHierarchyHasBeenUpdated();

	public abstract FGEConnectorGraphics getGraphics();

	public abstract List<? extends ControlArea> getControlAreas();

	/**
	 * Override parent's behaviour by enabling start and end object observing
	 */
	@Override
	public abstract void setValidated(boolean validated);

	public abstract ConnectorGraphicalRepresentation<O> clone();

}
