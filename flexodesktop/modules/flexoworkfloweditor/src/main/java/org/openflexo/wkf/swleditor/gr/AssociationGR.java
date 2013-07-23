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

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.dm.AssociationRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.dm.WKFDataModification;
import org.openflexo.foundation.wkf.edge.WKFAssociation;
import org.openflexo.foundation.wkf.edge.WKFAssociation.Arrow;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class AssociationGR extends EdgeGR<WKFAssociation> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AssociationGR.class.getPackage().getName());

	public AssociationGR(WKFAssociation edge, SwimmingLaneRepresentation aDrawing) {
		super(edge, aDrawing.getFirstVisibleObject(edge.getStartNode()), aDrawing.getFirstVisibleObject(edge.getEndNode()), aDrawing);
		setForeground(ForegroundStyle.makeStyle(Color.darkGray, 1.0f, DashStyle.DOTS_DASHES));
		setApplyForegroundToSymbols(false);
	}

	public WKFAssociation getAssociation() {
		return getDrawable();
	}

	@Override
	public StartSymbolType getStartSymbol() {
		if (getAssociation().getArrow() == Arrow.END_TO_START || getAssociation().getArrow() == Arrow.BOTH) {
			return StartSymbolType.ARROW;
		}
		return StartSymbolType.NONE;
	}

	@Override
	public double getStartSymbolSize() {
		return 8;
	}

	@Override
	public EndSymbolType getEndSymbol() {
		if (getAssociation().getArrow() == Arrow.START_TO_END || getAssociation().getArrow() == Arrow.BOTH) {
			return EndSymbolType.ARROW;
		}
		return EndSymbolType.NONE;
	}

	@Override
	public double getEndSymbolSize() {
		return 8;
	}

	@Override
	public MiddleSymbolType getMiddleSymbol() {
		return MiddleSymbolType.NONE;
	}

	@Override
	public String toString() {
		return "AssociationGR of " + getAssociation();
	}

	@Override
	public void refreshConnector() {
		if (!isConnectorConsistent()) {
			// Dont' go further for connector that are inconsistent (this may happen
			// during big model restructurations (for example during a multiple delete)
			return;
		}
		if (getConnectorSpecification() instanceof RectPolylinConnectorSpecification) {
			startOrientationFixed = false;
			endOrientationFixed = false;
			// ((RectPolylinConnectorSpecification)getConnector()).setRectPolylinConstraints(RectPolylinConstraints.NONE);
		}
		super.refreshConnector();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof WKFAttributeDataModification
				&& "arrow".equals(((WKFDataModification) dataModification).propertyName())) {
			refreshConnector(true);
		} else if (dataModification instanceof AssociationRemoved) {
			getDrawing().updateGraphicalObjectsHierarchy();
		} else {
			super.update(observable, dataModification);
		}
	}
}
