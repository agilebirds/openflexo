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

import javax.swing.ImageIcon;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class ActivityNodeGR extends NormalAbstractActivityNodeGR<ActivityNode> {

	private static final int MIN_SPACE = 4;

	public ActivityNodeGR(ActivityNode activityNode, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(activityNode, ShapeType.RECTANGLE, aDrawing, isInPalet);
		setVerticalTextAlignment(VerticalTextAlignment.TOP);
		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				if (getImageIcon() != null) {
					g.drawImage(getImageIcon().getImage(), new FGEPoint(0d, 0d));
				}
			};
		});

		updatePropertiesFromWKFPreferences();

	}

	@Override
	public double getRelativeTextY() {
		int vOffset = getImageIcon() != null ? getImageIcon().getIconHeight() : 10;
		return (vOffset + MIN_SPACE) / getHeight();
	}

	@Override
	int getTopBorder() {
		return isInPalette ? 10 : super.getTopBorder();
	}

	@Override
	int getBottomBorder() {
		return isInPalette ? 1 : super.getBottomBorder();
	}

	@Override
	int getLeftBorder() {
		return isInPalette ? 1 : super.getLeftBorder();
	}

	@Override
	int getRightBorder() {
		return isInPalette ? 1 : super.getRightBorder();
	}

	private ImageIcon getImageIcon() {
		if (getActivityNode().getTaskType() == null) {
			return null;
		}
		switch (getActivityNode().getTaskType()) {
		case BusinessRule:
			return WKFIconLibrary.TASKTYPE_BUSINESSRULE;
		case Manual:
			return WKFIconLibrary.TASKTYPE_MANUAL;
		case Receive:
			return WKFIconLibrary.TASKTYPE_RECEIVE;
		case Script:
			return WKFIconLibrary.TASKTYPE_SCRIPT;
		case Send:
			return WKFIconLibrary.TASKTYPE_SEND;
		case Service:
			return WKFIconLibrary.TASKTYPE_SERVICE;
		case User:
			return WKFIconLibrary.TASKTYPE_USER;

		default:
			return null;
		}
	}

	@Override
	public double getRequiredWidth(double labelWidth) {
		double required = super.getRequiredWidth(labelWidth);
		if (getImageIcon() != null) {
			required += getImageIcon().getIconWidth() * 2;
		}
		return required;
	}

	public ActivityNode getActivityNode() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof WKFAttributeDataModification
				&& "taskType".equals(((WKFAttributeDataModification) dataModification).getAttributeName())) {
			checkAndUpdateDimensionIfRequired();
			notifyShapeNeedsToBeRedrawn();
		}
		super.update(observable, dataModification);
	}
}
