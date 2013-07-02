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
package org.openflexo.foundation.wkf;

import java.awt.Color;

import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;

public class WKFAnnotation extends WKFArtefact implements InspectableObject, DeletableObject, LevelledObject {

	public static final String BACKGROUND_COLOR = "backgroundColor";
	public static final String BORDER_COLOR = "borderColor";
	public static final String DASH_STYLE = "dashStyle";
	public static final String IS_ROUNDED = "isRounded";
	public static final String IS_SOLID_BACKGROUND = "isSolidBackground";

	private boolean isAnnotation = true;

	// ==========================================================
	// ======================= Constructor ======================
	// ==========================================================

	/**
	 * Constructor used during deserialization
	 */
	public WKFAnnotation(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public WKFAnnotation(FlexoProcess process) {
		super(process);
	}

	@Override
	public String getClassNameKey() {
		return "annotation";
	}

	@Override
	public String getFullyQualifiedName() {
		return "ANNOTATION." + getText();
	}

	public boolean _getIsAnnotation() {
		return isAnnotation;
	}

	public void _setIsAnnotation(boolean flag) {
		isAnnotation = flag;
	}

	public boolean isAnnotation() {
		return _getIsAnnotation();
	}

	public boolean isBoundingBox() {
		return !_getIsAnnotation();
	}

	public void setIsAnnotation() {
		_setIsAnnotation(true);
	}

	public void setIsBoundingBox() {
		_setIsAnnotation(false);
	}

	@Override
	public String getInspectorName() {
		if (isBoundingBox()) {
			return Inspectors.WKF.BOUNDING_BOX_INSPECTOR;
		} else {
			return Inspectors.WKF.ANNOTATION_INSPECTOR;
		}
	}

	public Color getBackgroundColor() {
		return getBgColor(DEFAULT, FlexoColor.WHITE_COLOR);
	}

	public void setBackgroundColor(Color aColor) {
		if (requireChange(getBackgroundColor(), aColor)) {
			Color oldColor = getBackgroundColor();
			setBgColor(aColor, DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(BACKGROUND_COLOR, oldColor, aColor));
		}
	}

	public Color getBorderColor() {
		return getFgColor(DEFAULT, FlexoColor.BLACK_COLOR);
	}

	public void setBorderColor(Color aColor) {
		if (requireChange(getBorderColor(), aColor)) {
			Color oldColor = getBorderColor();
			setFgColor(aColor, DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(BORDER_COLOR, oldColor, aColor));
		}
	}

	public DashStyle getDashStyle() {
		return (DashStyle) _graphicalPropertyForKey(DASH_STYLE + "_" + DEFAULT);
	}

	public void setDashStyle(DashStyle dashStyle) {
		if (requireChange(getDashStyle(), dashStyle)) {
			Object oldDashStyle = getDashStyle();
			_setGraphicalPropertyForKey(dashStyle, DASH_STYLE + "_" + DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(DASH_STYLE, oldDashStyle, dashStyle));
		}
	}

	public void setIsRounded(boolean b) {
		if (requireChange(getIsRounded(), b)) {
			boolean oldValue = getIsRounded();
			_setGraphicalPropertyForKey(b, IS_ROUNDED + "_" + DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(IS_ROUNDED, oldValue, b));
		}
	}

	public boolean getIsRounded() {
		return _booleanGraphicalPropertyForKey(IS_ROUNDED + "_" + DEFAULT, true);
	}

	public void setIsSolidBackground(boolean b) {
		if (requireChange(getIsSolidBackground(), b)) {
			boolean oldValue = getIsSolidBackground();
			_setGraphicalPropertyForKey(b, IS_SOLID_BACKGROUND + "_" + DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(IS_SOLID_BACKGROUND, oldValue, b));
		}
	}

	public boolean getIsSolidBackground() {
		return _booleanGraphicalPropertyForKey(IS_SOLID_BACKGROUND + "_" + DEFAULT, false);
	}

}
