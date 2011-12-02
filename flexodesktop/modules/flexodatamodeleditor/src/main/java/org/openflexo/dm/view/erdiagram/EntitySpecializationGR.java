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

import org.jdom.Verifier;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector.RectPolylinAdjustability;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector.RectPolylinConstraints;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.toolbox.ToolBox;

public class EntitySpecializationGR extends ConnectorGraphicalRepresentation<EntitySpecialization> implements ERDiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EntitySpecializationGR.class.getPackage().getName());

	private TextStyle propertyNameStyle;
	private ForegroundStyle foreground;

	public EntitySpecializationGR(EntitySpecialization anEntitySpecialization, Drawing<?> aDrawing) {
		super(ConnectorType.RECT_POLYLIN, (ShapeGraphicalRepresentation<?>) aDrawing.getGraphicalRepresentation(anEntitySpecialization
				.getSpecializedEntity()), (ShapeGraphicalRepresentation<?>) aDrawing.getGraphicalRepresentation(anEntitySpecialization
				.getParentEntity()), anEntitySpecialization, aDrawing);
		// setText(getRole().getName());

		updateStyles();

		propertyNameStyle = TextStyle.makeTextStyle(Color.DARK_GRAY, ATTRIBUTE_FONT);

		setTextStyle(propertyNameStyle);

		getConnector().setIsRounded(true);
		getConnector().setRectPolylinConstraints(RectPolylinConstraints.VERTICAL_LAYOUT);
		getConnector().setAdjustability(RectPolylinAdjustability.FULLY_ADJUSTABLE);
		getConnector().setStraightLineWhenPossible(true);
		getConnector().setPixelOverlap(30);

		setEndSymbol(EndSymbolType.PLAIN_ARROW);
		setEndSymbolSize(15);

		if (getSpecializedEntity().hasGraphicalPropertyForKey(getStoredPolylinKey())) {
			ensurePolylinConverterIsRegistered();
			polylinIWillBeAdustedTo = (FGERectPolylin) getSpecializedEntity()._graphicalPropertyForKey(getStoredPolylinKey());
			getConnector().setWasManuallyAdjusted(true);
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
	public RectPolylinConnector getConnector() {
		return (RectPolylinConnector) super.getConnector();
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

	public EntitySpecialization getEntitySpecialization() {
		return getDrawable();
	}

	public DMEntity getSpecializedEntity() {
		return getEntitySpecialization().getSpecializedEntity();
	}

	public class ResetLayout extends MouseClickControl {

		public ResetLayout() {
			super("ResetLayout", MouseButton.LEFT, 2, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						java.awt.event.MouseEvent event) {
					// logger.info("Reset layout for edge");
					resetLayout();
					return true;
				}
			}, false, false, false, false);
		}

	}

	public void resetLayout() {
		getConnector().setWasManuallyAdjusted(false);
	}

	private FGERectPolylin polylinIWillBeAdustedTo;

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		if (polylinIWillBeAdustedTo != null && !getSpecializedEntity().isDeleted()) {
			getConnector().manuallySetPolylin(polylinIWillBeAdustedTo);
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

	private static String escapeStringForXML(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			if (Verifier.isXMLNameCharacter(c)) {
				sb.append(c);
			} else {
				sb.append("_");
			}
		}
		return sb.toString();
	}

	private String getContext() {
		return "inheritance_" + escapeStringForXML(getEntitySpecialization().getSpecialization().getStringRepresentation()) + "_diagram_"
				+ getDrawing().getDiagram().getFlexoID();
	}

	private String getStoredPolylinKey() {
		return "polylin_" + getContext();
	}

	private void storeNewLayout() {
		if (isRegistered()) {
			ensurePolylinConverterIsRegistered();
			if (getConnector().getWasManuallyAdjusted() && getConnector()._getPolylin() != null) {
				if (polylinIWillBeAdustedTo == null) { // Store this layout only in no other layout is beeing registering
					// logger.info("Post "+getPostCondition().getName()+": store new layout to "+connector._getPolylin());
					getSpecializedEntity()._setGraphicalPropertyForKey(getConnector()._getPolylin(), getStoredPolylinKey());
				}
			} else {
				if (getSpecializedEntity().hasGraphicalPropertyForKey(getStoredPolylinKey())) {
					getSpecializedEntity()._removeGraphicalPropertyWithKey(getStoredPolylinKey());
				}
			}
		}
	}

	private boolean isPolylinConverterRegistered = false;

	private void ensurePolylinConverterIsRegistered() {
		if (!isPolylinConverterRegistered) {
			if (getSpecializedEntity().getProject().getStringEncoder()._converterForClass(FGERectPolylin.class) == null) {
				getSpecializedEntity().getProject().getStringEncoder()._addConverter(RECT_POLYLIN_CONVERTER);
			}
			isPolylinConverterRegistered = true;
		}

	}

	@Override
	public String getText() {
		return getEntitySpecialization().getLabel();
	}

	@Override
	public double getAbsoluteTextX() {
		if (!getSpecializedEntity().hasLabelLocationForContext(getContext())) {
			getSpecializedEntity().getLabelX(getContext(), getDefaultLabelX());
		}
		return getSpecializedEntity().getLabelX(getContext());
	}

	@Override
	public void setAbsoluteTextXNoNotification(double posX) {
		getSpecializedEntity().setLabelX(posX, getContext());
	}

	@Override
	public double getAbsoluteTextY() {
		if (!getSpecializedEntity().hasLabelLocationForContext(getContext())) {
			getSpecializedEntity().getLabelY(getContext(), getDefaultLabelY());
		}
		return getSpecializedEntity().getLabelY(getContext());
	}

	@Override
	public void setAbsoluteTextYNoNotification(double posY) {
		getSpecializedEntity().setLabelY(posY, getContext());
	}

	// Override to implement defaut automatic layout
	public double getDefaultLabelX() {
		return Math.sin(getConnector().getMiddleSymbolAngle()) * 10;
	}

	// Override to implement defaut automatic layout
	public double getDefaultLabelY() {
		return Math.cos(getConnector().getMiddleSymbolAngle()) * 10;
	}

}
