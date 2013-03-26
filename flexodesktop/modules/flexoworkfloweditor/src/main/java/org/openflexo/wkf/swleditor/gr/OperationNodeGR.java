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
import java.awt.Dimension;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.action.OpenActionLevel;
import org.openflexo.foundation.wkf.action.OpenOperationComponent;
import org.openflexo.foundation.wkf.dm.OperationComponentHasBeenSet;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class OperationNodeGR extends AbstractOperationNodeGR {

	private static final Logger logger = Logger.getLogger(OperationNodeGR.class.getPackage().getName());

	private static final Color BG_COLOR = new Color(250, 250, 255);
	private static final int MIN_SPACE = 5;

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	public OperationNodeGR(OperationNode operationNode, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(operationNode, ShapeType.RECTANGLE, aDrawing, isInPalet);

		setMinimalWidth(NODE_MINIMAL_WIDTH);
		setMinimalHeight(NODE_MINIMAL_HEIGHT);

		setIsFloatingLabel(false);
		setRelativeTextX(0.5); // Center label horizontally

		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.2);
		background = BackgroundStyle.makeColorGradientBackground(getMainBgColor(), getOppositeBgColor(),
				ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		setForeground(foreground);
		setBackground(background);

		if (!(operationNode instanceof SelfExecutableOperationNode)) {
			addToMouseClickControls(new PetriGraphOpener(), true);
		}

		updatePropertiesFromWKFPreferences();

		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				if (getOperationNode().hasWOComponent()) {
					if (showWOName()) {
						g.useTextStyle(screenNameLabelTextStyle);
						Dimension labelSize = getNormalizedLabelSize();
						double vGap = getVerticalGap();
						double absoluteComponentLabelCenterY = vGap * 2 + labelSize.height + WKFPreferences.getComponentFont().getSize()
								/ 2 - 3;
						g.drawString(getOperationNode().getWOComponentName(),
								new FGEPoint(0.5, absoluteComponentLabelCenterY / getHeight()), HorizontalTextAlignment.CENTER);
					}
					double r_width = SEIconLibrary.OPERATION_COMPONENT_ICON.getIconWidth() / getWidth();
					double r_height = SEIconLibrary.OPERATION_COMPONENT_ICON.getIconHeight() / getHeight();
					g.drawImage(SEIconLibrary.OPERATION_COMPONENT_ICON.getImage(), new FGEPoint(1 - r_width, 1 - r_height));
				}
			};
		});
		// setDecorationPainter(new NodeDecorationPainter());
		addToMouseClickControls(new OperationComponentOpener());
	}

	protected boolean showWOName() {
		if (getWorkflow() != null) {
			return getWorkflow().getShowWOName(WKFPreferences.getShowWONameInWKF());
		}
		return WKFPreferences.getShowWONameInWKF();
	}

	@Override
	public Color getMainBgColor() {
		return BG_COLOR;
	}

	protected double getVerticalGap() {
		Dimension labelSize = getNormalizedLabelSize();
		return (getHeight() - labelSize.height - WKFPreferences.getComponentFont().getSize()) / 3;
	}

	@Override
	public double getRelativeTextY() {
		if (showWOName() && getOperationNode().hasWOComponent()) {
			Dimension labelSize = getNormalizedLabelSize();
			double vGap = getVerticalGap();
			double absoluteCenterY = vGap + labelSize.height / 2;
			return absoluteCenterY / getHeight();
		}
		return 0.5;
	}

	@Override
	public double getRequiredHeight(double labelHeight) {
		if (showWOName() && getOperationNode().hasWOComponent()) {
			return labelHeight + WKFPreferences.getComponentFont().getSize() + 3 * MIN_SPACE;
		}
		return labelHeight + 2 * MIN_SPACE;
	}

	protected TextStyle screenNameLabelTextStyle;

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		screenNameLabelTextStyle = TextStyle.makeTextStyle(Color.GRAY, WKFPreferences.getComponentFont().getFont());
		/*if (WKFPreferences.getShowWONameInWKF() && getOperationNode().hasWOComponent()) {
			setRelativeTextY(0.35); // Label is located on first third
		}
		else {
			setRelativeTextY(0.5); // Label is located on middle
		}*/
		setIsMultilineAllowed(true);
		setAdjustMinimalWidthToLabelWidth(false);
		setAdjustMinimalHeightToLabelHeight(false);
	}

	@Override
	public Rectangle getShape() {
		return (Rectangle) super.getShape();
	}

	@Override
	public double getWidth() {
		return getNode().getWidth(SWIMMING_LANE_EDITOR);
	}

	@Override
	public void setWidthNoNotification(double width) {
		getNode().setWidth(width, SWIMMING_LANE_EDITOR);
	}

	@Override
	public double getHeight() {
		return getNode().getHeight(SWIMMING_LANE_EDITOR);
	}

	@Override
	public void setHeightNoNotification(double height) {
		getNode().setHeight(height, SWIMMING_LANE_EDITOR);
	}

	public class PetriGraphOpener extends MouseClickControl {

		public PetriGraphOpener() {
			super("Opener", MouseButton.LEFT, 2, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						java.awt.event.MouseEvent event) {
					logger.info("Opening Operation petri graph by double-clicking");
					OpenActionLevel.actionType.makeNewAction(getOperationNode(), null, getDrawing().getEditor()).doAction();
					// Is now performed by receiving notification
					// getDrawing().updateGraphicalObjectsHierarchy();
					return true;
				}
			}, false, false, false, false);
		}

	}

	public class OperationComponentOpener extends MouseClickControl {

		public OperationComponentOpener() {
			super("Component opener", MouseButton.LEFT, 1, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						java.awt.event.MouseEvent event) {
					if (!getOperationNode().hasWOComponent()) {
						return false;
					}
					logger.info("Opening component by alt-clicking");
					OpenOperationComponent.actionType.makeNewAction(getOperationNode(), null, getDrawing().getEditor()).doAction();
					return true;
				}
			}, false, false, false, true);
		}

	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getNode()) {
			if (dataModification instanceof OperationComponentHasBeenSet) {
				// System.out.println("Tiens, on a ajoute un composant");
				updatePropertiesFromWKFPreferences();
				return;
			}
		}
		super.update(observable, dataModification);
	}

}
