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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.TextStyleImpl;
import org.openflexo.fge.controller.CustomDragControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.action.OpenEmbeddedProcess;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.MultipleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class SubProcessNodeGR extends NormalAbstractActivityNodeGR<SubProcessNode> {

	private static final Logger logger = Logger.getLogger(SubProcessNodeGR.class.getPackage().getName());

	private static final int MIN_SPACE = 5;
	private static final int ICONS_HEIGHT = 20;
	private TextStyle subLabelTextStyle;

	public SubProcessNodeGR(SubProcessNode subProcessNode, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(subProcessNode, ShapeType.RECTANGLE, aDrawing, isInPalet);
		addToMouseDragControls(new ProcessOpener(), true);
		/*setBorder(new ShapeGraphicalRepresentationUtils.ShapeBorder(
				PortmapRegisteryGR.PORTMAP_REGISTERY_WIDTH,
				PortmapRegisteryGR.PORTMAP_REGISTERY_WIDTH,
				PortmapRegisteryGR.PORTMAP_REGISTERY_WIDTH,
				PortmapRegisteryGR.PORTMAP_REGISTERY_WIDTH));*/

		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				FGERectangle expandingRect = getExpandingRect();
				g.drawImage(WKFIconLibrary.EXPANDABLE_ICON.getImage(), new FGEPoint(expandingRect.x, expandingRect.y));

				ImageIcon typeIcon = getImageIcon(getSubProcessNode());
				if (typeIcon != null) {
					FGERectangle additionalSymbolRect = getAdditionalSymbolRect();
					g.drawImage(typeIcon.getImage(), new FGEPoint(additionalSymbolRect.x, additionalSymbolRect.y));
				}

			};
		});

		updatePropertiesFromWKFPreferences();

	}

	@Override
	int getTopBorder() {
		return isInPalette ? 5 : PORTMAP_REGISTERY_WIDTH;
	}

	@Override
	int getBottomBorder() {
		return isInPalette ? 5 : PORTMAP_REGISTERY_WIDTH;
	}

	@Override
	int getLeftBorder() {
		return isInPalette ? 5 : PORTMAP_REGISTERY_WIDTH;
	}

	@Override
	int getRightBorder() {
		return isInPalette ? 5 : PORTMAP_REGISTERY_WIDTH;
	}

	protected FlexoFont getRoleFont() {
		if (getWorkflow() != null) {
			return getWorkflow().getRoleFont(WKFPreferences.getRoleFont());
		}
		return WKFPreferences.getRoleFont();
	}

	protected double getVerticalGap() {
		Dimension labelSize = getNormalizedLabelSize();
		return (getHeight() - labelSize.height - getRoleFont().getSize() - ICONS_HEIGHT - getExtraSpaceAbove() - getExtraSpaceBelow()) / 4;
	}

	@Override
	public double getRelativeTextY() {
		Dimension labelSize = getNormalizedLabelSize();
		double vGap = getVerticalGap();
		double absoluteCenterY = vGap + labelSize.height / 2 + getExtraSpaceAbove();
		return absoluteCenterY / getHeight();
	}

	@Override
	public double getRequiredHeight(double labelHeight) {
		return labelHeight + getRoleFont().getSize() + ICONS_HEIGHT + getExtraSpaceAbove() + getExtraSpaceBelow() + 4 * MIN_SPACE;
	}

	private double getExtraSpaceBelow() {
		return getPortmapRegisteryOrientation() == SimplifiedCardinalDirection.SOUTH ? heightForPortmapRegistery() : 0;
	}

	private double getExtraSpaceAbove() {
		return getPortmapRegisteryOrientation() == SimplifiedCardinalDirection.NORTH ? heightForPortmapRegistery() : 0;
	}

	public FGERectangle getExpandingRect() {
		Dimension labelSize = getNormalizedLabelSize();
		double vGap = getVerticalGap();
		double absoluteIconY = vGap * 3 + labelSize.height + getRoleFont().getSize() + getExtraSpaceAbove();
		double absoluteIconX;
		ImageIcon typeIcon = getImageIcon(getSubProcessNode());
		if (typeIcon == null) {
			absoluteIconX = (getWidth() - WKFIconLibrary.EXPANDABLE_ICON.getIconWidth()) / 2;
		} else {
			absoluteIconX = (getWidth() - WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() - typeIcon.getIconWidth() - MIN_SPACE) / 2;
		}
		return new FGERectangle(absoluteIconX / getWidth(), absoluteIconY / getHeight(), WKFIconLibrary.EXPANDABLE_ICON.getIconWidth()
				/ getWidth(), WKFIconLibrary.EXPANDABLE_ICON.getIconHeight() / getHeight());

	}

	protected FGERectangle getAdditionalSymbolRect() {
		ImageIcon typeIcon = getImageIcon(getSubProcessNode());
		if (typeIcon == null) {
			return null;
		}
		Dimension labelSize = getNormalizedLabelSize();
		double vGap = getVerticalGap();
		double absoluteIconY = vGap * 3 + labelSize.height + getRoleFont().getSize() + getExtraSpaceAbove();
		double absoluteIconX = (getWidth() - WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() - typeIcon.getIconWidth() - MIN_SPACE) / 2
				+ WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() + MIN_SPACE;
		return new FGERectangle(absoluteIconX / getWidth(), absoluteIconY / getHeight(), WKFIconLibrary.EXPANDABLE_ICON.getIconWidth()
				/ getWidth(), WKFIconLibrary.EXPANDABLE_ICON.getIconHeight() / getHeight());

	}

	public static ImageIcon getImageIcon(SubProcessNode spNode) {
		if (spNode instanceof MultipleInstanceSubProcessNode) {
			return WKFIconLibrary.MULTIPLE_INSTANCE_SUBPROCESS_ICON;
		} else if (spNode instanceof SingleInstanceSubProcessNode) {
			return null;
		} else if (spNode instanceof LoopSubProcessNode) {
			return WKFIconLibrary.LOOP_SUBPROCESS_ICON;
		} else if (spNode instanceof WSCallSubProcessNode) {
			return WKFIconLibrary.WS_CALL_SUBPROCESS_ICON;
		} else {
			return null;
		}
	}

	private double heightForPortmapRegistery() {
		return PortmapRegisteryGR.PORTMAP_REGISTERY_WIDTH / 2;
	}

	protected SimplifiedCardinalDirection getPortmapRegisteryOrientation() {
		if (getSubProcessNode().getPortMapRegistery() != null) {
			PortmapRegisteryGR portmapRegisteryGR = (PortmapRegisteryGR) getGraphicalRepresentation(getSubProcessNode()
					.getPortMapRegistery());
			if (portmapRegisteryGR != null) {
				return portmapRegisteryGR.getOrientation();
			}
		}
		return null;
	}

	public SubProcessNode getSubProcessNode() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if ((dataModification instanceof AttributeDataModification)
				&& "subProcess".equals(((AttributeDataModification) dataModification).propertyName())) {
			getDrawing().updateGraphicalObjectsHierarchy();
		}
		super.update(observable, dataModification);
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		subLabelTextStyle = TextStyleImpl.makeTextStyle(Color.GRAY, getRoleFont().getFont());
	}

	public class ProcessOpener extends MouseDragControl {

		public ProcessOpener() {
			super("SWL-Process opener", MouseButton.LEFT, new CustomDragControlAction() {
				@Override
				public boolean handleMousePressed(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						MouseEvent event) {
					logger.info("Opening process");
					OpenEmbeddedProcess.actionType.makeNewAction(getSubProcessNode(), null, getDrawing().getEditor()).doAction();
					getDrawing().updateGraphicalObjectsHierarchy();
					return true;
				}

				@Override
				public boolean handleMouseReleased(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						MouseEvent event, boolean isSignificativeDrag) {
					return false;
				}

				@Override
				public boolean handleMouseDragged(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						MouseEvent event) {
					return false;
				}
			}, false, false, false, false);
		}

		@Override
		public boolean isApplicable(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent e) {
			return super.isApplicable(graphicalRepresentation, controller, e) && isInsideClosingBox(graphicalRepresentation, controller, e);
		}

	}

	protected boolean isInsideClosingBox(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
			MouseEvent event) {
		ShapeView view = (ShapeView) controller.getDrawingView().viewForObject(graphicalRepresentation);
		FGERectangle expandingRect = getExpandingRect();
		java.awt.Rectangle scaledExpandingRect = convertNormalizedRectangleToViewCoordinates(expandingRect, controller.getScale());
		Point clickLocation = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), view);
		return scaledExpandingRect.contains(clickLocation);
	}

}
