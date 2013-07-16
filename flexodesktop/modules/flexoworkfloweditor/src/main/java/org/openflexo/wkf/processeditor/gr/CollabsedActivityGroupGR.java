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
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.controller.CustomDragControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.action.OpenGroup;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class CollabsedActivityGroupGR extends WKFObjectGR<ActivityGroup> {

	private static final Logger logger = Logger.getLogger(AbstractNodeGR.class.getPackage().getName());

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	protected TextStyle roleLabelTextStyle;

	public CollabsedActivityGroupGR(ActivityGroup activityGroup, ProcessRepresentation aDrawing) {
		super(activityGroup, ShapeType.RECTANGLE, aDrawing);

		setLayer(ACTIVITY_LAYER);
		addToMouseDragControls(new GroupExpander(), true);
		updatePropertiesFromWKFPreferences();

		setIsFloatingLabel(false);
		setRelativeTextX(0.5); // Center label horizontally
		setRelativeTextY(0.35); // Label is located on first third

		setMinimalWidth(NODE_MINIMAL_WIDTH);
		setMinimalHeight(NODE_MINIMAL_HEIGHT);

		getShape().setIsRounded(true);

		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		foreground.setLineWidth(1);
		foreground.setDashStyle(DashStyle.BIG_DASHES);
		setForeground(foreground);

		updateBackground();

		updatePropertiesFromWKFPreferences();

		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				FGERectangle expandingRect = getExpandingRect();
				g.drawImage(WKFIconLibrary.EXPANDABLE_ICON.getImage(), new FGEPoint(expandingRect.x, expandingRect.y));
			};
		});

	}

	@Override
	public boolean getIsVisible() {
		return true;
	}

	protected FGERectangle getExpandingRect() {
		double r_width = WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() / getWidth();
		double r_height = WKFIconLibrary.EXPANDABLE_ICON.getIconHeight() / getHeight();
		double x = (1 - r_width) / 2;
		double y = 1 - r_height * 1.2;
		if (y < 0.3) {
			y = 1 - r_height * 1.1;
		}
		if (y < 0) {
			y = 0;
		}
		return new FGERectangle(x, y, r_width, r_height);
	}

	private void updateBackground() {
		background = BackgroundStyle.makeColorGradientBackground(getActivityGroup().getColor(), Color.WHITE,
				ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		setBackground(background);
	}

	public ActivityGroup getActivityGroup() {
		return getDrawable();
	}

	@Override
	public Rectangle getShape() {
		return (Rectangle) super.getShape();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setTextStyle(TextStyle.makeTextStyle(Color.BLACK,
				getWorkflow() != null ? getWorkflow().getActivityFont(WKFPreferences.getActivityNodeFont()).getFont() : WKFPreferences
						.getActivityNodeFont().getFont()));
	}

	public class GroupExpander extends MouseDragControl {

		public GroupExpander() {
			super("GroupExpander", MouseButton.LEFT, new CustomDragControlAction() {
				@Override
				public boolean handleMousePressed(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
						MouseEvent event) {
					logger.info("Expand group");
					OpenGroup.actionType.makeNewAction(getActivityGroup(), null, getDrawing().getEditor()).doAction();
					return true;
				}

				@Override
				public boolean handleMouseReleased(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
						MouseEvent event, boolean isSignificativeDrag) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean handleMouseDragged(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
						MouseEvent event) {
					// TODO Auto-generated method stub
					return false;
				}
			}, false, false, false, false);
		}

		@Override
		public boolean isApplicable(GraphicalRepresentation graphicalRepresentation, DrawingController controller, MouseEvent e) {
			return super.isApplicable(graphicalRepresentation, controller, e) && isInsideClosingBox(graphicalRepresentation, controller, e);
		}

	}

	protected boolean isInsideClosingBox(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
			MouseEvent event) {
		ShapeView view = (ShapeView) controller.getDrawingView().viewForObject(graphicalRepresentation);
		FGERectangle expandingRect = getExpandingRect();
		java.awt.Rectangle scaledExpandingRect = convertNormalizedRectangleToViewCoordinates(expandingRect, controller.getScale());
		Point clickLocation = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), view);
		return scaledExpandingRect.contains(clickLocation);
	}

	private static final String COLLABSED = "collabsed_";

	private boolean isUpdatingPosition = false;

	@Override
	public double getX() {
		return getActivityGroup().getX(COLLABSED + BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setXNoNotification(double posX) {
		isUpdatingPosition = true;
		getActivityGroup().setX(posX, COLLABSED + BASIC_PROCESS_EDITOR);
		isUpdatingPosition = false;
	}

	@Override
	public double getY() {
		return getActivityGroup().getY(COLLABSED + BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setYNoNotification(double posY) {
		isUpdatingPosition = true;
		getActivityGroup().setY(posY, COLLABSED + BASIC_PROCESS_EDITOR);
		isUpdatingPosition = false;
	}

	@Override
	public double getWidth() {
		return getActivityGroup().getWidth(COLLABSED + BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setWidthNoNotification(double width) {
		getActivityGroup().setWidth(width, COLLABSED + BASIC_PROCESS_EDITOR);
	}

	@Override
	public double getHeight() {
		return getActivityGroup().getHeight(COLLABSED + BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setHeightNoNotification(double height) {
		getActivityGroup().setHeight(height, COLLABSED + BASIC_PROCESS_EDITOR);
	}

	@Override
	public String getText() {
		return getActivityGroup().getGroupName();
	}

	@Override
	public void setTextNoNotification(String text) {
		getActivityGroup().setGroupName(text);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getActivityGroup()) {
			if (dataModification instanceof WKFAttributeDataModification) {
				if (((WKFAttributeDataModification) dataModification).getAttributeName().equals("color")) {
					updateBackground();
				}
			} else if (dataModification instanceof ObjectVisibilityChanged) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Group visibility changed");
				}
				getDrawing().invalidateGraphicalObjectsHierarchy(getActivityGroup().getProcess());
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}

	@Override
	public String getToolTipText() {
		if (getActivityGroup().getDescription() == null || getActivityGroup().getDescription().trim().equals("")) {
			return "<html><b>" + getActivityGroup().getGroupName() + "</b><br><i>" + FlexoLocalization.localizedForKey("no_description")
					+ "</i></html>";
		}
		return "<html><b>" + getActivityGroup().getGroupName() + "</b><br><i>" + getActivityGroup().getDescription() + "</i></html>";
	}

	@Override
	protected boolean supportShadow() {
		return true;
	}

}
