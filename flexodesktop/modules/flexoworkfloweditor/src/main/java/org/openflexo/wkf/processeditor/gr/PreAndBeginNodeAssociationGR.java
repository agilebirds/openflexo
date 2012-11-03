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
import java.util.logging.Logger;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.ForegroundStyleImpl;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class PreAndBeginNodeAssociationGR extends WKFConnectorGR<PreAndBeginNodeAssociationGR.PreAndBeginNodeAssociation> {

	private static final Logger logger = Logger.getLogger(Connector.class.getPackage().getName());

	public static class PreAndBeginNodeAssociation {
		private FlexoPreCondition precondition;

		public PreAndBeginNodeAssociation(FlexoPreCondition aPrecondition) {
			precondition = aPrecondition;
		}

		public FlexoPreCondition getPreCondition() {
			return precondition;
		}

		public PetriGraphNode getBeginNode() {
			return precondition.getAttachedBeginNode();
		}
	}

	private ForegroundStyle foreground;

	public PreAndBeginNodeAssociationGR(PreAndBeginNodeAssociation association, ProcessRepresentation aDrawing) {
		super(ConnectorType.LINE, association.getPreCondition(), association.getBeginNode(), association, aDrawing);
		setForeground(ForegroundStyleImpl.makeStyle(Color.RED, 0.4f, DashStyle.BIG_DASHES));
		setEndSymbol(EndSymbolType.PLAIN_ARROW);
		setEndSymbolSize(8);

		setIsFocusable(false);
		setIsSelectable(false);

	}

	public PreAndBeginNodeAssociation getAssociation() {
		return getDrawable();
	}

	public FlexoPreCondition getPreCondition() {
		return getAssociation().getPreCondition();
	}

	public PetriGraphNode getBeginNode() {
		return getAssociation().getBeginNode();
	}

	@Override
	public boolean getIsVisible() {
		return true;
		// return getBeginNode() != null && getBeginNode().getParentPetriGraph().getIsVisible();
	}

	@Override
	public String toString() {
		return "PreAndBeginNodeAssociation of " + getPreCondition();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
	}
}
