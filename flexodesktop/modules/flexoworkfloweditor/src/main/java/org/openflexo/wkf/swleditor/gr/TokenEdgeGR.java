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

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class TokenEdgeGR extends EdgeGR<TokenEdge> {

	private static final Logger logger = Logger.getLogger(TokenEdgeGR.class.getPackage().getName());

	public TokenEdgeGR(TokenEdge edge, SwimmingLaneRepresentation aDrawing) {
		super(edge, edge.getStartNode(), edge.getEndNode(), aDrawing);

		if (isInduced() && !(aDrawing.getFirstVisibleObject(edge.getStartNode()) instanceof WKFGroup)
				&& !(aDrawing.getFirstVisibleObject(edge.getEndNode()) instanceof WKFGroup)
				&& !(edge.getStartNode() instanceof FlexoNode && ((FlexoNode) edge.getStartNode()).isEndNode()) /* terminal induced edges are not represented in light gray anymore */) {
			setForeground(ForegroundStyle.makeStyle(Color.LIGHT_GRAY, 1.6f));
		}
	}

	@Override
	public StartSymbolType getStartSymbol() {
		if (getTokenEdge().getIsConditional() || getTokenEdge().getIsDefaultFlow()) {
			return super.getStartSymbol();
		} else if (isInduced()) {
			return StartSymbolType.NONE;
		} else {
			return StartSymbolType.FILLED_CIRCLE;
		}
	}

	public TokenEdge getTokenEdge() {
		return getDrawable();
	}

	@Override
	public String toString() {
		return "TokenEdgeGR of " + getTokenEdge();
	}

	@Override
	public void refreshConnector() {
		if (!isConnectorConsistent()) {
			// Dont' go further for connector that are inconsistent (this may happen
			// during big model restructurations (for example during a multiple delete)
			return;
		}
		if (getConnector() instanceof RectPolylinConnector) {
			startOrientationFixed = false;
			endOrientationFixed = false;
			// ((RectPolylinConnector)getConnector()).setRectPolylinConstraints(RectPolylinConstraints.NONE);
		}
		super.refreshConnector();
	}

}
