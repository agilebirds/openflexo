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
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class ActivityNodeGR extends NormalAbstractActivityNodeGR<ActivityNode> {

	private static final int MIN_SPACE = 4;

	public ActivityNodeGR(ActivityNode activityNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(activityNode, ShapeType.RECTANGLE, aDrawing, isInPalet);

		isInPalette = isInPalet;
		setVerticalTextAlignment(VerticalTextAlignment.TOP);
		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useTextStyle(roleLabelTextStyle);
				g.drawString(getSubLabel(), new FGEPoint(0.5, (roleLabelTextStyle.getFont().getSize() / 2 + 5) / getHeight()),
						HorizontalTextAlignment.CENTER);
				if (getImageIcon() != null) {
					g.drawImage(getImageIcon().getImage(), new FGEPoint(0d, 0d));
				}
			};
		});

		updatePropertiesFromWKFPreferences();
	}

	/*public int getTopBorder() 
	{
		return (isInPalette ? 10 : super.getTopBorder());
	}
	
	public int getBottomBorder() 
	{
		return (isInPalette ? 1 : super.getBottomBorder());
	}

	public int getLeftBorder() 
	{
		return (isInPalette ? 1 : super.getLeftBorder());
	}
	
	public int getRightBorder() 
	{
		return (isInPalette ? 1 : super.getRightBorder());
	}*/

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
	public double getRelativeTextY() {
		int vOffset;
		if (getImageIcon() != null) {
			vOffset = Math.max(getImageIcon().getIconHeight(), roleLabelTextStyle.getFont().getSize());
		} else {
			vOffset = roleLabelTextStyle.getFont().getSize();
		}
		return (vOffset + MIN_SPACE) / getHeight();
	}

	@Override
	public double getRequiredHeight(double labelHeight) {
		return labelHeight + getRoleFont().getSize() + MIN_SPACE;
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
