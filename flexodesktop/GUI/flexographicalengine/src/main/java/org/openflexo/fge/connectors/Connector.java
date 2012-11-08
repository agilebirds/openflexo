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
package org.openflexo.fge.connectors;

import java.util.List;

import javax.swing.ImageIcon;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.FGEObject;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.impl.ConnectorImpl;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity(isAbstract = true)
@ImplementationClass(ConnectorImpl.class)
public interface Connector extends FGEObject {

	public static enum ConnectorType {
		LINE,
		// RECT_LINE,
		RECT_POLYLIN,
		CURVE,
		CURVED_POLYLIN,
		CUSTOM;

		public ImageIcon getIcon() {
			if (this == RECT_POLYLIN) {
				return org.openflexo.fge.FGEIconLibrary.RECT_POLYLIN_CONNECTOR_ICON;
			} else if (this == CURVE) {
				return org.openflexo.fge.FGEIconLibrary.CURVE_CONNECTOR_ICON;
			} else if (this == LINE) {
				return org.openflexo.fge.FGEIconLibrary.LINE_CONNECTOR_ICON;
			}
			return null;
		}

	}

	public ConnectorGraphicalRepresentation<?> getGraphicalRepresentation();

	public void setGraphicalRepresentation(ConnectorGraphicalRepresentation aGraphicalRepresentation);

	public double getStartAngle();

	public double getEndAngle();

	public Object getDrawable();

	public ShapeGraphicalRepresentation getStartObject();

	public ShapeGraphicalRepresentation getEndObject();

	/**
	 * Return value indicating distance from aPoint to connector, asserting aPoint is related to local normalized coordinates system
	 * 
	 * @param aPoint
	 * @return
	 */
	public double distanceToConnector(FGEPoint aPoint, double scale);

	public void setPaintAttributes(FGEConnectorGraphics g);

	public void paintConnector(FGEConnectorGraphics g);

	public void drawConnector(FGEConnectorGraphics g);

	public List<? extends ControlArea> getControlAreas();

	public ConnectorType getConnectorType();

	public void refreshConnector();

	public boolean needsRefresh();

	public void connectorWillBeModified();

	public void connectorHasBeenModified();

	public FGEPoint getMiddleSymbolLocation();

	/**
	 * Perform an area computation related to the both extremity objects
	 * 
	 * If order equals 0, return intersection between shapes representing the two object If order equals 1, return intersection of
	 * 
	 * @param order
	 * @return
	 */
	public FGEArea computeCoveringArea(int order);

	public boolean getDebug();

	public void setDebug(boolean debug);

	/**
	 * Return bounds of actually required area to fully display current connector (which might require to be paint outside normalized
	 * bounds)
	 * 
	 * @return
	 */
	public FGERectangle getConnectorUsedBounds();

	public Connector clone();

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	public FGEPoint getStartLocation();

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	public FGEPoint getEndLocation();

}
