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

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.WKFNode;
import org.openflexo.toolbox.ConcatenedList;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.processeditor.gr.EdgeGR;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public abstract class PetriGraphNodeGR<O extends PetriGraphNode> extends AbstractNodeGR<O> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PetriGraphNodeGR.class.getPackage().getName());

	protected static final FGEArea CONNECTOR_LOCATION_AREA = FGEUnionArea.makeUnion(new FGEPoint(0, 0.5), new FGEPoint(0.5, 0),
			new FGEPoint(1, 0.5), new FGEPoint(0.5, 1));

	private ConcatenedList<ControlArea<?>> concatenedList;
	protected boolean isInPalette = false;

	// private boolean isUpdatingPosition = false;

	public PetriGraphNodeGR(O node, ShapeType shapeType, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(node, shapeType, aDrawing);
		this.isInPalette = isInPalet;
	}

	@Override
	public FGEArea getAllowedStartAreaForConnectorForDirection(ConnectorGraphicalRepresentation<?> connectorGR, FGEArea area,
			SimplifiedCardinalDirection direction) {
		if (isStartAreaConstrained(connectorGR)) {
			return convertNormalizedPoint(this, direction.getNormalizedRepresentativePoint(), connectorGR);
		}
		return super.getAllowedStartAreaForConnectorForDirection(connectorGR, area, direction);
	}

	@Override
	public FGEArea getAllowedStartAreaForConnector(ConnectorGraphicalRepresentation<?> connectorGR) {
		if (isStartAreaConstrained(connectorGR)) {
			return CONNECTOR_LOCATION_AREA;
		}
		return super.getAllowedStartAreaForConnector(connectorGR);
	}

	protected boolean isStartAreaConstrained(ConnectorGraphicalRepresentation<?> connectorGR) {
		return connectorGR instanceof EdgeGR && !((EdgeGR<?>) connectorGR).startLocationManuallyAdjusted()
				&& ((EdgeGR<?>) connectorGR).getEdge().hasLocationConstraintFlag();
	}

	@Override
	public FGEArea getAllowedEndAreaForConnectorForDirection(ConnectorGraphicalRepresentation<?> connectorGR, FGEArea area,
			SimplifiedCardinalDirection direction) {
		if (isEndAreaConstrained(connectorGR)) {
			return convertNormalizedPoint(this, direction.getNormalizedRepresentativePoint(), connectorGR);
		}
		return super.getAllowedEndAreaForConnectorForDirection(connectorGR, area, direction);
	}

	@Override
	public FGEArea getAllowedEndAreaForConnector(ConnectorGraphicalRepresentation<?> connectorGR) {
		if (isEndAreaConstrained(connectorGR)) {
			return CONNECTOR_LOCATION_AREA;
		}
		return super.getAllowedEndAreaForConnector(connectorGR);
	}

	protected boolean isEndAreaConstrained(ConnectorGraphicalRepresentation<?> connectorGR) {
		return connectorGR instanceof EdgeGR && !((EdgeGR<?>) connectorGR).endLocationManuallyAdjusted()
				&& ((EdgeGR<?>) connectorGR).getEdge().hasLocationConstraintFlag();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setBorder(new ShapeBorder(getTopBorder(), getBottomBorder(), getLeftBorder(), getRightBorder()));
	}

	/**
	 * Looks for relative position in SWL view on related objects
	 */
	@Override
	protected void doLayoutMethod1() {
		if (!getModel().hasLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return;
		}
		Point2D swlPosition = getModel().getLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		FlexoPetriGraph parentPetrigraph = getModel().getParentPetriGraph();
		boolean isInRootPetriGraph = parentPetrigraph.isRootPetriGraph();
		if (!isInRootPetriGraph) {
			defaultX = swlPosition.getX();
			defaultY = swlPosition.getY();
			return;
		}
		Vector<WKFNode> v = new Vector<WKFNode>();
		v.addAll(getModel().getAllRelatedFromNodes());
		v.addAll(getModel().getAllRelatedToNodes());
		for (WKFNode node : v) {
			if (node instanceof FlexoPreCondition) {
				node = ((FlexoPreCondition) node).getAttachedNode();
			}
			if (node instanceof PetriGraphNode && ((PetriGraphNode) node).getParentPetriGraph() == parentPetrigraph) {
				if (isInRootPetriGraph) {
					// We need the node to be in the same role, otherwise there is no useful information
					if (SwimmingLaneRepresentation.getRepresentationRole((PetriGraphNode) node) != SwimmingLaneRepresentation
							.getRepresentationRole(getModel())) {
						continue;
					}
				}
				if (node.hasLocationForContext(SWIMMING_LANE_EDITOR)
						&& node.hasLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
					Point2D p1 = node.getLocation(SWIMMING_LANE_EDITOR);
					Point2D p2 = node.getLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
					defaultX = (int) (p2.getX() + (swlPosition.getX() - p1.getX()));
					defaultY = (int) (p2.getY() + (swlPosition.getY() - p1.getY()));
				}
			}
		}
	}

	int getTopBorder() {
		return REQUIRED_SPACE_ON_TOP;
	}

	int getBottomBorder() {
		return REQUIRED_SPACE_ON_BOTTOM;
	}

	int getLeftBorder() {
		return REQUIRED_SPACE_ON_LEFT;
	}

	int getRightBorder() {
		return hasNodePalette() ? REQUIRED_SPACE_ON_RIGHT_FOR_PALETTE : REQUIRED_SPACE_ON_RIGHT;
	}

	public boolean hasNodePalette() {
		return true;
	}

	/*@Override
	public void setBorder(org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder border) {
		if (hasNodePalette()) {
			if (border ==null) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Cannot set null border on PetriGraphNodeGR with a node palette");
				border = new ShapeBorder(0,0,0,20);
			} else {
				if (border.right<20)
					border.right=20;
			}
		}
		super.setBorder(border);
	}*/

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (hasNodePalette()) {
			if (concatenedList == null) {
				concatenedList = new ConcatenedList<ControlArea<?>>();
				concatenedList.addElementList(super.getControlAreas());
				concatenedList.addElement(new NodePalette(this, getDrawable().getParentPetriGraph()));
			}
			return concatenedList;
		} else {
			concatenedList = null;
			return super.getControlAreas();
		}
	}

}
