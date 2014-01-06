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
import java.awt.Font;
import java.awt.geom.AffineTransform;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SWLEditorConstants;
import org.openflexo.wkf.utils.EventShapePainter;

public class EventNodeGR extends PetriGraphNodeGR<EventNode> {

	public static final int EVENT_NODE_SIZE = 31;
	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private static final Font specialPaletteFont = new Font("SansSerif", Font.BOLD, 10);

	public EventNodeGR(EventNode eventNode, ProcessRepresentation aDrawing) {
		this(eventNode, aDrawing, false);
	}

	public EventNodeGR(EventNode eventNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(eventNode, ShapeType.CIRCLE, aDrawing, isInPalet);
		this.isInPalette = isInPalet;
		setWidth(EVENT_NODE_SIZE);
		setHeight(31);
		setIsFloatingLabel(true);
		updateBackgroundForeground();
		setShapePainter(new EventShapePainter(getDrawable()));

		// if (getEventNode().getImageIcon() != null) {
		// foreground = ForegroundStyle.makeNone();
		// background = BackgroundStyle.makeImageBackground(getEventNode().getImageIcon());
		// ((BackgroundImage)background).setScaleX(1);
		// ((BackgroundImage)background).setScaleY(1);
		//
		// }
		// else {
		// foreground = ForegroundStyle.makeStyle(Color.BLACK);
		// foreground.setLineWidth(getEventNode().isEnd()?1.8:0.6);
		// background = BackgroundStyle.makeColoredBackground(getMainBgColor());
		// }

		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		if (getEventNode().getBoundaryOf() != null) {
			setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
		}
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
			setShadowStyle(ShadowStyle.makeNone());
		}
		if (eventNode.getEventType() == EVENT_TYPE.NonInteruptive || eventNode.getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
			setSpecificStroke(FGEConstants.DASHED);
		}

	}

	@Override
	public boolean hasNodePalette() {
		return !isInPalette;
	}

	@Override
	public int getTopBorder() {
		return isInPalette ? 1 : super.getTopBorder();
	}

	@Override
	public int getBottomBorder() {
		return isInPalette ? 1 : super.getBottomBorder();
	}

	@Override
	public int getLeftBorder() {
		return isInPalette ? 1 : super.getLeftBorder();
	}

	@Override
	public int getRightBorder() {
		return isInPalette ? 1 : super.getRightBorder();
	}

	private void updateBackgroundForeground() {
		foreground = ForegroundStyle.makeDefault();
		foreground.setLineWidth(getEventNode().isEnd() ? 2.0 : 0.6);
		background = BackgroundStyle.makeColoredBackground(getMainBgColor());
		setForeground(foreground);
		setBackground(background);
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setTextStyle(TextStyle.makeTextStyle(Color.BLACK,
				getWorkflow() != null ? getWorkflow().getEventFont(WKFPreferences.getEventNodeFont()).getFont() : WKFPreferences
						.getEventNodeFont().getFont()));
		setIsMultilineAllowed(true);
	}

	public EventNode getEventNode() {
		return getDrawable();
	}

	@Override
	public Circle getShape() {
		return (Circle) super.getShapeSpecification();
	}

	@Override
	public double getDefaultLabelX() {
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getX();
		}
		return getLeftBorder() + 15;
	}

	@Override
	public double getDefaultLabelY() {
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getY();
		}
		return getTopBorder() + getHeight() + getTextStyle().getFont().getSize() + 5;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof DataModification) {
			if ("eventType".equals(dataModification.propertyName()) || "isCatching".equals(dataModification.propertyName())) {
				updateBackgroundForeground();
				notifyShapeNeedsToBeRedrawn();
			} else {
				super.update(observable, dataModification);
			}
		}
	}

	private GraphicalRepresentation parentGR = null;
	private FGEArea parentOutline = null;

	@Override
	public FGEArea getLocationConstrainedArea() {
		if (getEventNode().getBoundaryOf() == null) {
			return null;
		}
		GraphicalRepresentation parent = getContainerGraphicalRepresentation();
		if (parentGR == null || parent != parentGR) {
			if (parent != null && parent instanceof ShapeGraphicalRepresentation) {
				parentOutline = ((ShapeGraphicalRepresentation) parent).getShapeSpecification().getOutline();
				parentOutline = parentOutline.transform(AffineTransform.getScaleInstance(
						((ShapeGraphicalRepresentation) parent).getWidth(), ((ShapeGraphicalRepresentation) parent).getHeight()));
				ShapeBorder parentBorder = ((ShapeGraphicalRepresentation) parent).getBorder();
				parentOutline = parentOutline
						.transform(AffineTransform.getTranslateInstance(parentBorder.left - 20, parentBorder.top - 20));
				// System.out.println("Rebuild outline = "+parentOutline);
				parentGR = parent;
			}
		}
		return parentOutline;
	}
}
