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

import java.awt.Dimension;

import javax.swing.ImageIcon;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class ActivityNodeGR extends NormalAbstractActivityNodeGR<ActivityNode> {

	private static final int MIN_SPACE = 5;

	private ForegroundStyle foreground;
	private BackgroundStyle background;
	private boolean isInPalette = false;

	public ActivityNodeGR(ActivityNode activityNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(activityNode, ShapeType.RECTANGLE, aDrawing, isInPalet);

		isInPalette = isInPalet;

		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useTextStyle(roleLabelTextStyle);
				Dimension labelSize = getNormalizedLabelSize();
				double vGap = getVerticalGap();
				double absoluteRoleLabelCenterY = vGap * 2 + labelSize.height + getRoleFont().getSize() / 2 - 3;
				g.drawString(getSubLabel(), new FGEPoint(0.5, absoluteRoleLabelCenterY / getHeight()), TextAlignment.CENTER);
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
		if (getActivityNode().getTaskType() == null)
			return null;
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

	protected double getVerticalGap() {
		Dimension labelSize = getNormalizedLabelSize();
		return (getHeight() - labelSize.height - getRoleFont().getSize()) / 3;
	}

	@Override
	public double getRelativeTextY() {
		Dimension labelSize = getNormalizedLabelSize();
		double vGap = getVerticalGap();
		double absoluteCenterY = vGap + labelSize.height / 2;
		return absoluteCenterY / getHeight();
	}

	@Override
	public double getRequiredHeight(double labelHeight) {
		return labelHeight + getRoleFont().getSize() + 3 * MIN_SPACE;
	}

	@Override
	public double getRequiredWidth(double labelWidth) {
		double required = super.getRequiredWidth(labelWidth);
		if (getImageIcon() != null)
			required += getImageIcon().getIconWidth() * 2;
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
		}
		super.update(observable, dataModification);
	}

}
