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

import java.awt.Point;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle.DashStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.WKFAnnotation;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class AnnotationGR extends ArtefactGR<WKFAnnotation> {

	private static final Logger logger = Logger.getLogger(AnnotationGR.class.getPackage().getName());

	private static final double SMALL_EDGE = 0.1;

	private static final FGEPoint[] INCOMING_ANNOTATION_BORDER = new FGEPoint[] { new FGEPoint(SMALL_EDGE, 0), new FGEPoint(0, 0),
			new FGEPoint(0, 1), new FGEPoint(SMALL_EDGE, 1) };
	private static final FGEPoint[] OUTGOING_ANNOTATION_BORDER = new FGEPoint[] { new FGEPoint(1 - SMALL_EDGE, 0), new FGEPoint(1, 0),
			new FGEPoint(1, 1), new FGEPoint(1 - SMALL_EDGE, 1) };

	// private boolean isUpdatingPosition = false;

	public AnnotationGR(WKFAnnotation annotation, SwimmingLaneRepresentation aDrawing) {
		super(annotation, ShapeType.RECTANGLE, aDrawing);
		int offset = annotation.isBoundingBox() ? 2 : 0;
		if (getDrawable().getLevel() == FlexoLevel.ACTIVITY) {
			setLayer(ACTIVITY_LAYER - offset);
		} else if (getDrawable().getLevel() == FlexoLevel.OPERATION) {
			setLayer(OPERATION_LAYER - offset);
		} else if (getDrawable().getLevel() == FlexoLevel.ACTION) {
			setLayer(ACTION_LAYER - offset);
		}
		setShapePainter(new ShapePainter() {

			@Override
			public void paintShape(FGEShapeGraphics g) {
				if (getAnnotation().isAnnotation()) {
					if (getAnnotation().hasIncomingAssociations() || getAnnotation().hasOutgoingAssociations()) {
						g.setDefaultForeground(ForegroundStyle.makeDefault());
						g.useDefaultForegroundStyle();
						for (int i = 0; i < INCOMING_ANNOTATION_BORDER.length - 1; i++) {
							FGEPoint p1 = INCOMING_ANNOTATION_BORDER[i];
							FGEPoint p2 = INCOMING_ANNOTATION_BORDER[i + 1];
							g.drawLine(p1, p2);
						}
					}
				}
				/*if (getAnnotation().hasOutgoingAssociations()) {
					g.setDefaultForeground(ForegroundStyle.makeDefault());
					g.useDefaultForegroundStyle();
					for (int i = 0; i < OUTGOING_ANNOTATION_BORDER.length-1; i++) {
						FGEPoint p1 = OUTGOING_ANNOTATION_BORDER[i];
						FGEPoint p2 = OUTGOING_ANNOTATION_BORDER[i+1];
						g.drawLine(p1, p2);
					}
				}*/
			}
		});
	}

	public WKFAnnotation getAnnotation() {
		return getDrawable();
	}

	@Override
	public void setSize(FGEDimension newSize) {
		Point p = null;
		if (getDrawing() != null && getDrawing().getGraphicalRepresentation(getModel()) == this) {
			p = getLabelViewCenter(1.0);
		}
		super.setSize(newSize);
		if (p != null) {
			setLabelViewCenter(p, 1.0);
		}
	}

	@Override
	protected void doLayoutMethod3(double x, double y) {
		if (getAnnotation().isAnnotation()) {
			super.doLayoutMethod3(x, y);
		} else {
			Enumeration<GraphicalRepresentation<?>> en = null;
			double attemptX = x, attemptY = y;
			boolean found = false;
			while (!found) {
				en = getContainerGraphicalRepresentation().getContainedGraphicalRepresentations().elements();
				found = true;
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof AnnotationGR && ((AnnotationGR) gr).getDrawable().isBoundingBox()) {
						AnnotationGR rgr = (AnnotationGR) gr;
						if (rgr != this) {
							if (rgr.hasLocation()) {
								java.awt.Rectangle viewBounds = gr.getViewBounds(1.0);
								if (viewBounds.intersects(new java.awt.Rectangle((int) attemptX, (int) attemptY, (int) getWidth(),
										(int) getHeight()))) {
									// The attempt location intersects with another one, let's move forward
									found = false;
									if (viewBounds.x + viewBounds.width + getWidth() > getDrawingGraphicalRepresentation().getWidth()) {
										// End of line, we go to the next one
										if (y + 10 + getHeight() < getDrawingGraphicalRepresentation().getHeight()) {
											attemptX = x;
											attemptY = y + 10 + getHeight();
											break;
										} else {
											if (logger.isLoggable(Level.WARNING)) {
												logger.warning("Could not find suitable location for node (swl): " + getModel());
											}
											break;
										}
									} else {
										attemptX = viewBounds.x + viewBounds.width + 10;
										break;
									}
								}
							}
						}
					}
				}
			}
			if (found) {
				defaultX = attemptX;
				defaultY = attemptY;
			} else {
				defaultX = x;
				defaultY = y;
			}
		}
	}

	@Override
	protected TextStyle createTextStyle() {
		TextStyle ts = super.createTextStyle();
		ts.setIsBackgroundColored(getAnnotation().isBoundingBox());
		return ts;
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		if (getAnnotation().isAnnotation()) {
			setBackground(BackgroundStyle.makeEmptyBackground());
			setForeground(ForegroundStyle.makeNone());
			setIsFloatingLabel(false);
			setAdjustMinimalWidthToLabelWidth(true);
			setAdjustMinimalHeightToLabelHeight(true);
			setAdjustMaximalWidthToLabelWidth(true);
			setAdjustMaximalHeightToLabelHeight(true);
			setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		} else {
			if (getAnnotation().getIsSolidBackground()) {
				setBackground(BackgroundStyle.makeColoredBackground(getAnnotation().getBackgroundColor()));
			} else {
				setBackground(BackgroundStyle.makeEmptyBackground());
			}
			if (getAnnotation().getDashStyle() == null) {
				getAnnotation().setDashStyle(DashStyle.DOT_LINES_DASHES);
			}
			setForeground(ForegroundStyle.makeStyle(getAnnotation().getBorderColor(), 1.0f, (DashStyle) getAnnotation().getDashStyle()));
			((Rectangle) getShape()).setIsRounded(getAnnotation().getIsRounded());
			setIsFloatingLabel(true);
			setAdjustMinimalWidthToLabelWidth(false);
			setAdjustMinimalHeightToLabelHeight(false);
			setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
		}
		setIsMultilineAllowed(true);
		// setTextStyle(TextStyle.makeTextStyle(getAnnotation().getTextColor(), getAnnotation().getTextFont().getTheFont()));
		if (getAnnotation().getTextAlignment() == null) {
			getAnnotation().setTextAlignment(GraphicalRepresentation.TextAlignment.CENTER);
		}
		setTextAlignment((TextAlignment) getAnnotation().getTextAlignment());
	}

}
