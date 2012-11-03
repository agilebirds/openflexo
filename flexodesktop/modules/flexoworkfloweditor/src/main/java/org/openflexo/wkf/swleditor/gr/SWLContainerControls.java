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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyleImpl;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyleImpl;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentationUtils;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.swleditor.SWLEditorConstants;

public class SWLContainerControls extends ControlArea<FGERectangle> implements SWLEditorConstants {

	private final SWLContainerGR _containerGR;

	private final FGERectangle upDownIconsRect;
	private final FGERoundRectangle otherIconsRect;

	private final FGERectangle upIconRect;
	private final FGERectangle downIconRect;
	private final FGERectangle leftIconRect;
	private final FGERectangle rightIconRect;
	private final FGERectangle minusIconRect;
	private final FGERectangle plusIconRect;

	private final ForegroundStyle foregroundStyle;
	private final BackgroundStyle backgroundStyle;

	private static FGERectangle makeRoundRect(SWLContainerGR containerGR) {
		double x = SWIMMING_LANE_BORDER - 5;
		double y = containerGR.getHeight() - 32;
		double width = 50;
		double height = 40;
		return new FGERectangle(x / containerGR.getWidth(), y / containerGR.getHeight(), width / containerGR.getWidth(), height
				/ containerGR.getHeight(), Filling.FILLED);
	}

	public SWLContainerControls(SWLContainerGR containerGR) {
		super((GraphicalRepresentation) containerGR, makeRoundRect(containerGR));

		_containerGR = containerGR;

		foregroundStyle = ForegroundStyleImpl.makeStyle(Color.GRAY, 0.5f);
		backgroundStyle = BackgroundStyleImpl.makeColoredBackground(Color.WHITE);
		backgroundStyle.setUseTransparency(true);
		backgroundStyle.setTransparencyLevel(0.7f);

		AffineTransform at = AffineTransform.getScaleInstance(1 / containerGR.getWidth(), 1 / containerGR.getHeight());

		upDownIconsRect = (FGERectangle) (new FGERectangle(SWIMMING_LANE_BORDER - 5, containerGR.getHeight() - 31, WKFIconLibrary.MINUS
				.getImage().getWidth(null), 29 + WKFIconLibrary.MINUS.getImage().getHeight(null), Filling.FILLED)).transform(at);
		otherIconsRect = (FGERoundRectangle) (new FGERoundRectangle(SWIMMING_LANE_BORDER + 7, containerGR.getHeight() - 31, 37, 37, 20, 20,
				Filling.FILLED)).transform(at);
		;

		upIconRect = (FGERectangle) (new FGERectangle(SWIMMING_LANE_BORDER + 18, _containerGR.getHeight() - 30, WKFIconLibrary.TRIANGLE_UP
				.getImage().getWidth(null), WKFIconLibrary.TRIANGLE_UP.getImage().getHeight(null), Filling.FILLED)).transform(at);
		downIconRect = (FGERectangle) (new FGERectangle(SWIMMING_LANE_BORDER + 18, _containerGR.getHeight() - 10,
				WKFIconLibrary.TRIANGLE_DOWN.getImage().getWidth(null), WKFIconLibrary.TRIANGLE_DOWN.getImage().getHeight(null),
				Filling.FILLED)).transform(at);
		leftIconRect = (FGERectangle) (new FGERectangle(SWIMMING_LANE_BORDER + 8, _containerGR.getHeight() - 20,
				WKFIconLibrary.TRIANGLE_LEFT.getImage().getWidth(null), WKFIconLibrary.TRIANGLE_UP.getImage().getHeight(null),
				Filling.FILLED)).transform(at);
		rightIconRect = (FGERectangle) (new FGERectangle(SWIMMING_LANE_BORDER + 29, _containerGR.getHeight() - 20,
				WKFIconLibrary.TRIANGLE_RIGHT.getImage().getWidth(null), WKFIconLibrary.TRIANGLE_DOWN.getImage().getHeight(null),
				Filling.FILLED)).transform(at);
		minusIconRect = (FGERectangle) (new FGERectangle(SWIMMING_LANE_BORDER - 5, _containerGR.getHeight() - 31, WKFIconLibrary.MINUS
				.getImage().getWidth(null), WKFIconLibrary.MINUS.getImage().getHeight(null), Filling.FILLED)).transform(at);
		plusIconRect = (FGERectangle) (new FGERectangle(SWIMMING_LANE_BORDER - 5, _containerGR.getHeight() - 2, WKFIconLibrary.PLUS
				.getImage().getWidth(null), WKFIconLibrary.PLUS.getImage().getHeight(null), Filling.FILLED)).transform(at);

	}

	@Override
	public boolean isDraggable() {
		return false;
	}

	@Override
	public Rectangle paint(FGEGraphics drawingGraphics) {
		AffineTransform at = GraphicalRepresentationUtils.convertNormalizedCoordinatesAT((GraphicalRepresentation) _containerGR,
				drawingGraphics.getGraphicalRepresentation());

		Graphics2D oldGraphics = drawingGraphics.cloneGraphics();

		drawingGraphics.setDefaultForeground(foregroundStyle);
		drawingGraphics.setDefaultBackground(backgroundStyle);

		upDownIconsRect.transform(at).paint(drawingGraphics);
		otherIconsRect.transform(at).paint(drawingGraphics);

		FGERectangle upRect = (FGERectangle) upIconRect.transform(at);
		FGERectangle downRect = (FGERectangle) downIconRect.transform(at);
		FGERectangle leftRect = (FGERectangle) leftIconRect.transform(at);
		FGERectangle rightRect = (FGERectangle) rightIconRect.transform(at);
		FGERectangle minusRect = (FGERectangle) minusIconRect.transform(at);
		FGERectangle plusRect = (FGERectangle) plusIconRect.transform(at);

		drawingGraphics.drawImage(WKFIconLibrary.TRIANGLE_UP.getImage(), upRect.getNorthWestPt());
		drawingGraphics.drawImage(WKFIconLibrary.TRIANGLE_DOWN.getImage(), downRect.getNorthWestPt());
		drawingGraphics.drawImage(WKFIconLibrary.TRIANGLE_LEFT.getImage(), leftRect.getNorthWestPt());
		drawingGraphics.drawImage(WKFIconLibrary.TRIANGLE_RIGHT.getImage(), rightRect.getNorthWestPt());
		drawingGraphics.drawImage(WKFIconLibrary.MINUS.getImage(), minusRect.getNorthWestPt());
		drawingGraphics.drawImage(WKFIconLibrary.PLUS.getImage(), plusRect.getNorthWestPt());

		/*BackgroundStyle debugBS = BackgroundStyleImpl.makeColoredBackground(Color.BLUE);
		debugBS.setUseTransparency(true);
		drawingGraphics.setDefaultBackground(debugBS);
		getArea().transform(at).paint(drawingGraphics);*/

		drawingGraphics.releaseClonedGraphics(oldGraphics);
		return null;

	}

	/*@Override
	public boolean isApplicable(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent e)
	{
		return super.isApplicable(graphicalRepresentation, controller, e) 
		&& (RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, e, upRect)
				|| RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, e, downRect)
				|| RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, e, minusRect)
				|| RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, e, plusRect));
	}*/

	@Override
	public boolean isClickable() {
		return true;
	}

	@Override
	public boolean clickOnPoint(FGEPoint clickedPoint, int clickCount) {
		if (upIconRect.containsPoint(clickedPoint)) {
			if (_containerGR.getSwimmingLaneHeight() > 20) {
				_containerGR.setSwimmingLaneHeight(_containerGR.getSwimmingLaneHeight() - 10);
			}
			return true;
		}

		if (downIconRect.containsPoint(clickedPoint)) {
			_containerGR.setSwimmingLaneHeight(_containerGR.getSwimmingLaneHeight() + 10);
			return true;
		}

		if (leftIconRect.containsPoint(clickedPoint)) {
			if (_containerGR.getDrawing().getSWLWidth() > 100) {
				_containerGR.getDrawing().setSWLWidth(_containerGR.getDrawing().getSWLWidth() - 30);
			}
			return true;
		}

		if (rightIconRect.containsPoint(clickedPoint)) {
			_containerGR.getDrawing().setSWLWidth(_containerGR.getDrawing().getSWLWidth() + 30);
			return true;
		}

		if (plusIconRect.containsPoint(clickedPoint)) {
			_containerGR.setSwimmingLaneNb(_containerGR.getSwimmingLaneNb() + 1);
			return true;
		}

		if (minusIconRect.containsPoint(clickedPoint)) {
			if (_containerGR.getSwimmingLaneNb() > 1) {
				_containerGR.setSwimmingLaneNb(_containerGR.getSwimmingLaneNb() - 1);
			}
			return true;
		}

		return false;
	}

	/*
	action = new CustomDragControlAction() {
		@Override
		public boolean handleMousePressed(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event) {
			RoleContainerGR.logger.info("handleMousePressed");
			if (RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, event,upRect)) {
				RoleContainerGR.logger.info("UP");
				_containerGR.setSwimmingLaneHeight(_containerGR.getSwimmingLaneHeight()-10);
				return true;
			}
			if (RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, event,downRect)) {
				RoleContainerGR.logger.info("DOWN");
				_containerGR.setSwimmingLaneHeight(_containerGR.getSwimmingLaneHeight()+10);
				return true;
			}
			if (RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, event,minusRect)) {
				RoleContainerGR.logger.info("MINUS");
				_containerGR.setSwimmingLaneNb(_containerGR.getSwimmingLaneNb()-1);
				return true;
			}
			if (RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, event,plusRect)) {
				RoleContainerGR.logger.info("PLUS");
				_containerGR.setSwimmingLaneNb(_containerGR.getSwimmingLaneNb()+1);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event, boolean isSignificativeDrag) {
			return false;
		}
		
		@Override
		public boolean handleMouseDragged(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event) {
			return false;
		}
	};
	
	setAction(action);*/

}