package org.openflexo.fge.connectors;

import java.util.List;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEConnectorGraphics;

public interface Connector<CS extends ConnectorSpecification> {

	@SuppressWarnings("unchecked")
	public abstract CS getConnectorSpecification();

	public abstract ConnectorNode<?> getConnectorNode();

	public abstract ShapeNode<?> getStartNode();

	public abstract ShapeNode<?> getEndNode();

	public abstract ConnectorType getConnectorType();

	public abstract double getStartAngle();

	public abstract double getEndAngle();

	public abstract double distanceToConnector(FGEPoint aPoint, double scale);

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	public abstract List<? extends ControlArea<?>> getControlAreas();

	public abstract FGEPoint getMiddleSymbolLocation();

	/**
	 * Return bounds of actually required area to fully display current connector (which might require to be paint outside normalized
	 * bounds)
	 * 
	 * @return
	 */
	public abstract FGERectangle getConnectorUsedBounds();

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	public abstract FGEPoint getStartLocation();

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	public abstract FGEPoint getEndLocation();

	public abstract void paintConnector(FGEConnectorGraphics g);

}