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
package org.openflexo.dm.view.erdiagram;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification.RectPolylinAdjustability;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification.RectPolylinConstraints;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.toolbox.ToolBox;

public class DMRelationshipGR extends ConnectorGraphicalRepresentation<RelationshipRepresentation> implements ERDiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DMRelationshipGR.class.getPackage().getName());

	private TextStyle propertyNameStyle;
	private ForegroundStyle foreground;

	public DMRelationshipGR(RelationshipRepresentation aRelationshipRepresentation, Drawing<?> aDrawing) {
		super(ConnectorType.RECT_POLYLIN, (ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(aRelationshipRepresentation
				.getProperty()), (ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(aRelationshipRepresentation
				.getInverseProperty() != null ? aRelationshipRepresentation.getInverseProperty() : aRelationshipRepresentation
				.getDestinationEntity()), aRelationshipRepresentation, aDrawing);
		// setText(getRole().getName());

		updateStyles();

		propertyNameStyle = TextStyle.makeTextStyle(Color.DARK_GRAY, ATTRIBUTE_FONT);

		setTextStyle(propertyNameStyle);

		getConnectorSpecification().setIsRounded(true);
		getConnectorSpecification().setRectPolylinConstraints(RectPolylinConstraints.HORIZONTAL_LAYOUT);
		getConnectorSpecification().setAdjustability(RectPolylinAdjustability.FULLY_ADJUSTABLE);
		getConnectorSpecification().setStraightLineWhenPossible(true);
		getConnectorSpecification().setPixelOverlap(30);

		if (getProperty().getCardinality().isMultiple()) {
			setEndSymbol(EndSymbolType.FILLED_DOUBLE_ARROW);
			setEndSymbolSize(15);
		} else {
			setEndSymbol(EndSymbolType.FILLED_ARROW);
			setEndSymbolSize(10);
		}

		if (getRelationshipRepresentation().getInverseProperty() != null) {
			if (getRelationshipRepresentation().getInverseProperty().getCardinality().isMultiple()) {
				setStartSymbol(StartSymbolType.FILLED_DOUBLE_ARROW);
				setStartSymbolSize(15);
			} else {
				setStartSymbol(StartSymbolType.FILLED_ARROW);
				setStartSymbolSize(10);
			}
		}

		if (getProperty().hasGraphicalPropertyForKey(getStoredPolylinKey())) {
			ensurePolylinConverterIsRegistered();
			polylinIWillBeAdustedTo = (FGERectPolylin) getProperty()._graphicalPropertyForKey(getStoredPolylinKey());
			getConnectorSpecification().setWasManuallyAdjusted(true);
		}

		setForeground(ForegroundStyle.makeStyle(Color.DARK_GRAY, 1.6f));

		setIsFocusable(true);

		addToMouseClickControls(new ResetLayout(), true);
		addToMouseClickControls(new ERDiagramController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new ERDiagramController.ShowContextualMenuControl(true));
		}
		// addToMouseDragControls(new DrawRoleSpecializationControl());

	}

	@Override
	public RectPolylinConnectorSpecification getConnectorSpecification() {
		return (RectPolylinConnectorSpecification) super.getConnectorSpecification();
	}

	private void updateStyles() {
		/*foreground = ForegroundStyle.makeStyle(getEntity().getColor());
		foreground.setLineWidth(2);
		background = BackgroundStyle.makeColorGradientBackground(getRole().getColor(), Color.WHITE, ColorGradientDirection.SOUTH_WEST_NORTH_EAST);
		setForeground(foreground);
		setBackground(background);*/
	}

	@Override
	public ERDiagramRepresentation getDrawing() {
		return (ERDiagramRepresentation) super.getDrawing();
	}

	public RelationshipRepresentation getRelationshipRepresentation() {
		return getDrawable();
	}

	public DMProperty getProperty() {
		return getRelationshipRepresentation().getProperty();
	}

	public class ResetLayout extends MouseClickControl {

		public ResetLayout() {
			super("ResetLayout", MouseButton.LEFT, 2, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
						java.awt.event.MouseEvent event) {
					// logger.info("Reset layout for edge");
					resetLayout();
					return true;
				}
			}, false, false, false, false);
		}

	}

	public void resetLayout() {
		getConnectorSpecification().setWasManuallyAdjusted(false);
	}

	private FGERectPolylin polylinIWillBeAdustedTo;

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		if (polylinIWillBeAdustedTo != null && !getProperty().isDeleted()) {
			getConnectorSpecification().manuallySetPolylin(polylinIWillBeAdustedTo);
			polylinIWillBeAdustedTo = null;
			refreshConnector();
		}
	}

	@Override
	public void refreshConnector() {
		if (!isConnectorConsistent()) {
			// Dont' go further for connector that are inconsistent (this may happen
			// during big model restructurations (for example during a multiple delete)
			return;
		}
		super.refreshConnector();
		storeNewLayout();
	}

	@Override
	public void notifyConnectorChanged() {
		super.notifyConnectorChanged();
		storeNewLayout();
	}

	private String getContext() {
		return "diagram_" + getDrawing().getDiagram().getFlexoID();
	}

	private String getStoredPolylinKey() {
		return "polylin_" + getContext();
	}

	private void storeNewLayout() {
		if (isRegistered()) {
			ensurePolylinConverterIsRegistered();
			if (getConnectorSpecification().getWasManuallyAdjusted() && getConnectorSpecification().getPolylin() != null) {
				if (polylinIWillBeAdustedTo == null) { // Store this layout only in no other layout is beeing registering
					// logger.info("Post "+getPostCondition().getName()+": store new layout to "+connector._getPolylin());
					getProperty()._setGraphicalPropertyForKey(getConnectorSpecification().getPolylin(), getStoredPolylinKey());
				}
			} else {
				if (getProperty().hasGraphicalPropertyForKey(getStoredPolylinKey())) {
					getProperty()._removeGraphicalPropertyWithKey(getStoredPolylinKey());
				}
			}
		}
	}

	private boolean isPolylinConverterRegistered = false;

	private void ensurePolylinConverterIsRegistered() {
		if (!isPolylinConverterRegistered) {
			if (getProperty().getProject().getStringEncoder()._converterForClass(FGERectPolylin.class) == null) {
				getProperty().getProject().getStringEncoder()._addConverter(RECT_POLYLIN_CONVERTER);
			}
			isPolylinConverterRegistered = true;
		}

	}

}
