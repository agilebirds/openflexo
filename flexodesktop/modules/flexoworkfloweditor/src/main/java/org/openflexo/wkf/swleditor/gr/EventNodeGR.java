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
import java.awt.Font;
import java.awt.geom.AffineTransform;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyleImpl;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyleImpl;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyleImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyleImpl;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.dm.RoleChanged;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;
import org.openflexo.wkf.utils.EventShapePainter;

public class EventNodeGR extends PetriGraphNodeGR<EventNode> {

	public static final int EVENT_NODE_SIZE = 31;
	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private static final Font specialPaletteFont = new Font("SansSerif", Font.BOLD, 10);

	public EventNodeGR(EventNode eventNode, SwimmingLaneRepresentation aDrawing) {
		this(eventNode, aDrawing, false);
	}

	public EventNodeGR(EventNode eventNode, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(eventNode, ShapeType.CIRCLE, aDrawing, isInPalet);
		this.isInPalette = isInPalet;
		setWidth(EVENT_NODE_SIZE);
		setHeight(31);
		setIsFloatingLabel(true);

		if (getEventNode().getLevel() == FlexoLevel.ACTIVITY) {
			setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
		}

		updateBackgroundForeground();
		setShapePainter(new EventShapePainter(getDrawable()));
		// if (getEventNode().getImageIcon() != null) {
		// foreground = ForegroundStyleImpl.makeNone();
		// background = BackgroundStyleImpl.makeImageBackground(getEventNode().getImageIcon());
		// ((BackgroundImage)background).setScaleX(1);
		// ((BackgroundImage)background).setScaleY(1);
		// }
		// else {
		// foreground = ForegroundStyleImpl.makeStyle(Color.BLACK);
		//
		// background = BackgroundStyleImpl.makeColoredBackground(getMainBgColor());
		// }

		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		if (getEventNode().getLevel() == FlexoLevel.ACTIVITY) {
			setLayer(ACTIVITY_LAYER + 1);
		} else if (getEventNode().getLevel() == FlexoLevel.OPERATION) {
			setLayer(OPERATION_LAYER + 1);
		} else if (getEventNode().getLevel() == FlexoLevel.ACTION) {
			setLayer(ACTION_LAYER + 1);
		}

		updatePropertiesFromWKFPreferences();
		if (isInPalette) {
			getTextStyle().setFont(specialPaletteFont);
			setShadowStyle(ShadowStyleImpl.makeNone());
		}
		if (eventNode.getEventType() == EVENT_TYPE.NonInteruptive || eventNode.getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
			setSpecificStroke(FGEConstants.DASHED);
		}

	}

	@Override
	int getTopBorder() {
		return (isInPalette ? 1 : super.getTopBorder());
	}

	@Override
	int getBottomBorder() {
		return (isInPalette ? 1 : super.getBottomBorder());
	}

	@Override
	int getLeftBorder() {
		return (isInPalette ? 1 : super.getLeftBorder());
	}

	@Override
	int getRightBorder() {
		return (isInPalette ? 1 : super.getRightBorder());
	}

	private void updateBackgroundForeground() {
		foreground = ForegroundStyleImpl.makeDefault();
		foreground.setLineWidth(getEventNode().isEnd() ? 1.8 : 0.6);
		background = BackgroundStyleImpl.makeColoredBackground(getMainBgColor());
		setForeground(foreground);
		setBackground(background);
	}

	private GraphicalRepresentation<?> parentGR = null;
	private FGEArea parentOutline = null;

	@Override
	public FGEArea getLocationConstrainedArea() {
		if (getEventNode().getBoundaryOf() == null) {
			if (getParentGraphicalRepresentation() instanceof SWLContainerGR && !getNode().isStartOrEnd()) {
				return new FGERectangle(0, 0, ((SWLContainerGR) getParentGraphicalRepresentation()).getWidth() - getWidth(),
						((SWLContainerGR) getParentGraphicalRepresentation()).getHeight() - getHeight(), Filling.FILLED);
			} else {
				return super.getLocationConstrainedArea();
			}
		} else {
			GraphicalRepresentation<?> parent = getContainerGraphicalRepresentation();
			if (parentGR == null || parent != parentGR) {
				if (parent != null && parent instanceof ShapeGraphicalRepresentation) {
					parentOutline = ((ShapeGraphicalRepresentation<?>) parent).getShape().getOutline();
					parentOutline = parentOutline.transform(AffineTransform.getScaleInstance(
							((ShapeGraphicalRepresentation<?>) parent).getWidth(), ((ShapeGraphicalRepresentation<?>) parent).getHeight()));
					ShapeBorder parentBorder = ((ShapeGraphicalRepresentation<?>) parent).getBorder();
					parentOutline = parentOutline.transform(AffineTransform.getTranslateInstance(parentBorder.left - 20,
							parentBorder.top - 20));
					// System.out.println("Rebuild outline = "+parentOutline);
					parentGR = parent;
				}
			}
			return parentOutline;
		}
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setTextStyle(TextStyleImpl.makeTextStyle(Color.BLACK,
				getWorkflow() != null ? getWorkflow().getEventFont(WKFPreferences.getEventNodeFont()).getFont() : WKFPreferences
						.getEventNodeFont().getFont()));
		setIsMultilineAllowed(true);
	}

	public EventNode getEventNode() {
		return getDrawable();
	}

	@Override
	public Circle getShape() {
		return (Circle) super.getShape();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {

		if (dataModification instanceof DataModification) {
			if (dataModification instanceof RoleChanged) {
				resetLocationConstrainedArea();
				getDrawing().requestRebuildCompleteHierarchy();
			} else if ("eventType".equals((dataModification).propertyName()) || "isCatching".equals((dataModification).propertyName())) {
				updateBackgroundForeground();
				notifyShapeNeedsToBeRedrawn();
			} else {
				super.update(observable, dataModification);
			}
			super.update(observable, dataModification);
		}
	}

	@Override
	public double getDefaultLabelX() {
		if (getModel().hasLabelLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return getModel().getLabelLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR).getX();
		}
		return getLeftBorder() + 15;
	}

	@Override
	public double getDefaultLabelY() {
		if (getModel().hasLabelLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return getModel().getLabelLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR).getY();
		}
		return getTopBorder() + getHeight() + getTextStyle().getFont().getSize() + 5;
	}
}
