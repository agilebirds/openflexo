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
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyle.CapStyle;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.ForegroundStyle.JoinStyle;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.WKFMessageArtifact;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class MessageGR extends ArtefactGR<WKFMessageArtifact> {

	private static final Logger logger = Logger.getLogger(MessageGR.class.getPackage().getName());
	private static final FGEPoint TOP_RIGHT = new FGEPoint(1, 0);

	private static final FGEPoint CENTER = new FGEPoint(0.5, 0.5);

	private static final FGEPoint ORIGIN = new FGEPoint(0, 0);

	private static final BackgroundStyle INITIATING_BACKGROUND = BackgroundStyle.makeColoredBackground(Color.WHITE);
	private static final BackgroundStyle EXITING_BACKGROUND = BackgroundStyle.makeColoredBackground(Color.GRAY);

	private static final ForegroundStyle FOREGROUND = ForegroundStyle.makeStyle(new Color(0, 34, 73), 1.6f, JoinStyle.JOIN_ROUND,
			CapStyle.CAP_ROUND, DashStyle.PLAIN_STROKE);

	public MessageGR(WKFMessageArtifact dataSource, SwimmingLaneRepresentation aDrawing) {
		super(dataSource, ShapeType.RECTANGLE, aDrawing);
		((Rectangle) getShapeSpecification()).setIsRounded(false);
		setIsFloatingLabel(true);
		setForeground(FOREGROUND);
		if (dataSource.isInitiating()) {
			setBackground(INITIATING_BACKGROUND);
		} else {
			setBackground(EXITING_BACKGROUND);
		}
		setMinimalWidth(10);
		setMinimalHeight(10);
		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.setDefaultForeground(FOREGROUND);
				g.useDefaultForegroundStyle();
				g.drawLine(ORIGIN, CENTER);
				g.drawLine(CENTER, TOP_RIGHT);
			}
		});
	}

	@Override
	public double getDefaultWidth() {
		return 50;
	}

	@Override
	public double getDefaultHeight() {
		return 35;
	}

	@Override
	protected boolean supportShadow() {
		return false;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		super.update(observable, dataModification);
		if (dataModification instanceof WKFAttributeDataModification) {
			String propertyName = ((WKFAttributeDataModification) dataModification).propertyName();
			if (WKFMessageArtifact.INITIATING.equals(propertyName)) {
				if (getDrawable().isInitiating()) {
					setBackground(INITIATING_BACKGROUND);
				} else {
					setBackground(EXITING_BACKGROUND);
				}
			}
		}
	}

}
