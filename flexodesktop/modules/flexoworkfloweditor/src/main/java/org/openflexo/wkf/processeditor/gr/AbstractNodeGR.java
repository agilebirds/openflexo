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
import java.awt.Point;
import java.util.logging.Logger;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.dm.PreInserted;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public abstract class AbstractNodeGR<O extends AbstractNode> extends WKFNodeGR<O> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AbstractNodeGR.class.getPackage().getName());

	// private boolean isUpdatingPosition = false;

	public AbstractNodeGR(O node, ShapeType shapeType, ProcessRepresentation aDrawing) {
		super(node, shapeType, aDrawing);
	}

	@Override
	public String getText() {
		return getNode().getName();
	}

	@Override
	public void setTextNoNotification(String text) {
		getNode().setName(text);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getNode()) {
			if (dataModification instanceof NodeRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof PreInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof PetriGraphHasBeenOpened) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof PetriGraphHasBeenClosed) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof WKFAttributeDataModification) {
				notifyShapeNeedsToBeRedrawn();
			} else if (dataModification instanceof NameChanged) {
				notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
				checkAndUpdateDimensionIfRequired();
			}
		}
		super.update(observable, dataModification);
	}

	@Override
	public String getToolTipText() {
		String nodeDesc = getNode().getDescription();
		String nodeName = getNode().getName();
		if (nodeDesc == null || nodeDesc.trim().equals("")) {
			return "<html><b>" + nodeName + "</b><br><i>" + FlexoLocalization.localizedForKey("no_description") + "</i></html>";
		}
		return "<html>" + (nodeName == null || nodeName.trim().length() == 0 ? "" : "<b>" + nodeName + "</b><br>") + "<i>"
				+ getNode().getDescription() + "</i></html>";
	}

	@Override
	protected boolean supportShadow() {
		return true;
	}

	public boolean showNoChildrenSign() {
		if (getDrawing() == null || getDrawing().getModel() == null) {
			return false;
		}
		if (getNode() instanceof FlexoNode) {
			if (((FlexoNode) getNode()).isBeginOrEndNode()) {
				return false;
			}
		}
		/*if (getNode() instanceof SelfExecutableNode) {
			return !((SelfExecutableNode)getNode()).hasExecutionPetriGraph() || !((SelfExecutableNode)getNode()).getExecutionPetriGraph().hasOtherNodesThanBeginEndNodes();
		} else*/if (getNode() instanceof AbstractActivityNode) {
			return ((AbstractActivityNode) getNode()).mightHaveOperationPetriGraph()
					&& (!((AbstractActivityNode) getNode()).hasContainedPetriGraph() || !((AbstractActivityNode) getNode())
							.getContainedPetriGraph().hasOtherNodesThanBeginEndNodes());
		} else if (getNode() instanceof OperationNode) {
			return ((OperationNode) getNode()).mightHaveActionPetriGraph()
					&& (!((OperationNode) getNode()).hasContainedPetriGraph() || !((OperationNode) getNode()).getContainedPetriGraph()
							.hasOtherNodesThanBeginEndNodes());
		}
		return false;
	}

	public Color getTextColor() {
		return Color.BLACK;
	}

	public Color getMainBgColor() {
		return Color.WHITE;
	}

	public Color getOppositeBgColor() {
		return Color.WHITE;
	}

	protected class NodeDecorationPainter implements DecorationPainter {

		private static final int size = 8;
		private FGEPoint bottomLeft = new FGEPoint(0, 1);
		private FGEDimension dimension = new FGEDimension(size, size);

		public NodeDecorationPainter() {
		}

		@Override
		public boolean paintBeforeShape() {
			return false;
		}

		@Override
		public void paintDecoration(FGEShapeDecorationGraphics g) {
			if (!showNoChildrenSign()) {
				return;
			}
			// Uses a gray line
			g.useForegroundStyle(ForegroundStyle.makeStyle(WKFCst.NODE_BORDER_COLOR));

			// Finds the bottomLeft in the view coordinates (we don't pass the scale here because it will be done by the graphics)
			Point southWest = convertNormalizedPointToViewCoordinates(getShapeSpecification().nearestOutlinePoint(bottomLeft), 1);
			southWest.y -= 1.5 * size;
			southWest.x += size / 2;
			g.drawCircle(new FGEPoint(southWest), dimension);
		}
	}
}
