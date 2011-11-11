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

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.cp.ConnectorControlPoint;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class ExpanderGR<O extends AbstractNode> extends ConnectorGraphicalRepresentation<ExpanderGR.Expander<O>> implements
		ProcessEditorConstants {

	static final Logger logger = Logger.getLogger(Connector.class.getPackage().getName());

	public static class Expander<N extends AbstractNode> {
		private N node;
		private FlexoPetriGraph pg;

		public Expander(N aNode, FlexoPetriGraph aPetriGraph) {
			node = aNode;
			pg = aPetriGraph;
		}

		public N getFatherNode() {
			return node;
		}

		public FlexoPetriGraph getPetriGraph() {
			return pg;
		}
	}

	protected ForegroundStyle foreground;
	protected ForegroundStyle noneForeground;
	protected BackgroundStyle emptyBackground;

	public ExpanderGR(Expander<O> expander, ProcessRepresentation aDrawing) {
		super(ConnectorType.CUSTOM, (ShapeGraphicalRepresentation<?>) aDrawing.getGraphicalRepresentation(expander.getFatherNode()),
				(ShapeGraphicalRepresentation<?>) aDrawing.getGraphicalRepresentation(expander.getPetriGraph()), expander, aDrawing);

		setConnector(new ExpanderConnector());

		emptyBackground = BackgroundStyle.makeEmptyBackground();

		if (expander.getPetriGraph() instanceof ActivityPetriGraph) {
			foreground = ForegroundStyle.makeStyle(ProcessEditorConstants.ACTIVITY_PG_COLOR, 0.3f);
			noneForeground = ForegroundStyle.makeNone();
			setLayer(ACTIVITY_PG_LAYER);
		} else if (expander.getPetriGraph() instanceof OperationPetriGraph) {
			foreground = ForegroundStyle.makeStyle(ProcessEditorConstants.OPERATION_PG_COLOR, 0.3f);
			noneForeground = ForegroundStyle.makeNone();
			setLayer(OPERATION_PG_LAYER);
		} else if (expander.getPetriGraph() instanceof ActionPetriGraph) {
			foreground = ForegroundStyle.makeStyle(ProcessEditorConstants.ACTION_PG_COLOR, 0.3f);
			noneForeground = ForegroundStyle.makeNone();
			setLayer(ACTION_PG_LAYER);
		}

		setIsFocusable(false);
		setIsSelectable(false);
	}

	@Override
	public ProcessRepresentation getDrawing() {
		return (ProcessRepresentation) super.getDrawing();
	}

	public Expander<O> getExpander() {
		return getDrawable();
	}

	public AbstractNode getFatherNode() {
		return getExpander().getFatherNode();
	}

	public FlexoPetriGraph getPetriGraph() {
		return getExpander().getPetriGraph();
	}

	@Override
	public boolean getIsVisible() {
		return getDrawing().isVisible(getPetriGraph());
	}

	@Override
	public String toString() {
		return "ExpanderGR of " + getFatherNode();
	}

	public class ExpanderConnector extends Connector {
		private boolean firstUpdated = false;
		private Vector<ControlPoint> controlPoints;

		public ExpanderConnector() {
			super(ExpanderGR.this);
			controlPoints = new Vector<ControlPoint>();
		}

		private void updateControlPoints() {
			FGEPoint centerOfStartObject = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), new FGEPoint(0.5, 0.5),
					getGraphicalRepresentation());
			FGEPoint centerOfEndObject = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), new FGEPoint(0.5, 0.5),
					getGraphicalRepresentation());

			SimplifiedCardinalDirection orientation = FGEPoint.getSimplifiedOrientation(centerOfStartObject, centerOfEndObject);

			FGEPoint newStartP1, newStartP2, newEndP1, newEndP2;

			if (orientation == SimplifiedCardinalDirection.NORTH) {
				newStartP1 = getStartObject().getShape().getShape().getBoundingBox().getNorthWestPt();
				newStartP2 = getStartObject().getShape().getShape().getBoundingBox().getNorthEastPt();
				newEndP1 = getEndObject().getShape().getShape().getBoundingBox().getSouthWestPt();
				newEndP2 = getEndObject().getShape().getShape().getBoundingBox().getSouthEastPt();
			} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
				newStartP1 = getStartObject().getShape().getShape().getBoundingBox().getSouthWestPt();
				newStartP2 = getStartObject().getShape().getShape().getBoundingBox().getSouthEastPt();
				newEndP1 = getEndObject().getShape().getShape().getBoundingBox().getNorthWestPt();
				newEndP2 = getEndObject().getShape().getShape().getBoundingBox().getNorthEastPt();
			} else if (orientation == SimplifiedCardinalDirection.WEST) {
				newStartP1 = getStartObject().getShape().getShape().getBoundingBox().getNorthWestPt();
				newStartP2 = getStartObject().getShape().getShape().getBoundingBox().getSouthWestPt();
				newEndP1 = getEndObject().getShape().getShape().getBoundingBox().getNorthEastPt();
				newEndP2 = getEndObject().getShape().getShape().getBoundingBox().getSouthEastPt();
			} else /* if (orientation == SimplifiedCardinalDirection.EAST) */{
				newStartP1 = getStartObject().getShape().getShape().getBoundingBox().getNorthEastPt();
				newStartP2 = getStartObject().getShape().getShape().getBoundingBox().getSouthEastPt();
				newEndP1 = getEndObject().getShape().getShape().getBoundingBox().getNorthWestPt();
				newEndP2 = getEndObject().getShape().getShape().getBoundingBox().getSouthWestPt();
			}

			newStartP1 = getStartObject().getShape().outlineIntersect(newStartP1);
			newStartP2 = getStartObject().getShape().outlineIntersect(newStartP2);
			newEndP1 = getEndObject().getShape().outlineIntersect(newEndP1);
			newEndP2 = getEndObject().getShape().outlineIntersect(newEndP2);

			newStartP1 = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), newStartP1, getGraphicalRepresentation());
			newStartP2 = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), newStartP2, getGraphicalRepresentation());
			newEndP1 = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), newEndP1, getGraphicalRepresentation());
			newEndP2 = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), newEndP2, getGraphicalRepresentation());

			startP1 = new ConnectorControlPoint(getGraphicalRepresentation(), newStartP1);
			startP2 = new ConnectorControlPoint(getGraphicalRepresentation(), newStartP2);
			endP1 = new ConnectorControlPoint(getGraphicalRepresentation(), newEndP1);
			endP2 = new ConnectorControlPoint(getGraphicalRepresentation(), newEndP2);

			controlPoints.clear();
			controlPoints.add(startP1);
			controlPoints.add(startP2);
			controlPoints.add(endP1);
			controlPoints.add(endP2);

		}

		private ConnectorControlPoint startP1;
		private ConnectorControlPoint startP2;
		private ConnectorControlPoint endP1;
		private ConnectorControlPoint endP2;

		@Override
		public void refreshConnector() {
			super.refreshConnector();

			updateControlPoints();

			firstUpdated = true;

		}

		@Override
		public double getStartAngle() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getEndAngle() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void drawConnector(FGEConnectorGraphics g) {
			if (!firstUpdated) {
				refreshConnector();
			}

			BackgroundStyle backgroundToUse = emptyBackground;

			g.setDefaultBackground(backgroundToUse);
			g.setDefaultForeground(noneForeground);

			new FGEPolygon(Filling.FILLED, startP1.getPoint(), endP1.getPoint(), endP2.getPoint(), startP2.getPoint()).paint(g);

			g.setDefaultForeground(foreground);
			g.useDefaultForegroundStyle();
			g.drawLine(startP1.getPoint(), endP1.getPoint());
			g.drawLine(startP2.getPoint(), endP2.getPoint());
		}

		@Override
		public double distanceToConnector(FGEPoint aPoint, double scale) {
			// Never focusable nor inspectable
			return Double.POSITIVE_INFINITY;
		}

		@Override
		public ConnectorType getConnectorType() {
			return ConnectorType.CUSTOM;
		}

		@Override
		public FGERectangle getConnectorUsedBounds() {
			return NORMALIZED_BOUNDS;
		}

		@Override
		public List<ControlPoint> getControlAreas() {
			return controlPoints;
		}

		@Override
		public FGEPoint getMiddleSymbolLocation() {
			return new FGEPoint(0, 0);
		}

		// Not relevant here
		@Override
		public FGEPoint getEndLocation() {
			return null;
		}

		// Not relevant here
		@Override
		public FGEPoint getStartLocation() {
			return null;
		}

	}
}
