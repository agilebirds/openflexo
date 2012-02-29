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
package org.openflexo.vpm.view.widget;

import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.viewpoint.GraphicalRepresentationChanged;
import org.openflexo.foundation.viewpoint.ShapePatternRole;

public class EditionPatternPreviewShapeGR extends ShapeGraphicalRepresentation<ShapePatternRole> implements GraphicalFlexoObserver,
		EditionPatternPreviewConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EditionPatternPreviewShapeGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public EditionPatternPreviewShapeGR() {
		super(ShapeType.RECTANGLE, null, null);
		initWithDefaultValues();
	}

	public EditionPatternPreviewShapeGR(ShapePatternRole aPatternRole, EditionPatternPreviewRepresentation aDrawing) {
		super(ShapeType.RECTANGLE, aPatternRole, aDrawing);
		parentShape = aPatternRole.getParentShapePatternRole();
		initWithDefaultValues();
		init(aPatternRole, aDrawing);

	}

	private void initWithDefaultValues() {
		setTextStyle(TextStyle.makeTextStyle(DEFAULT_SHAPE_TEXT_COLOR, DEFAULT_FONT));
		setX((WIDTH - DEFAULT_SHAPE_WIDTH) / 2);
		setY((HEIGHT - DEFAULT_SHAPE_HEIGHT) / 2);
		setWidth(DEFAULT_SHAPE_WIDTH);
		setHeight(DEFAULT_SHAPE_HEIGHT);
		setBackground(BackgroundStyle.makeColoredBackground(DEFAULT_SHAPE_BACKGROUND_COLOR));
		setIsFloatingLabel(false);
	}

	private boolean isInitialized = false;

	public boolean isInitialized() {
		return isInitialized;
	}

	public void init(ShapePatternRole aPatternRole, EditionPatternPreviewRepresentation aDrawing) {
		setDrawable(aPatternRole);
		setDrawing(aDrawing);

		if (aPatternRole != null) {
			aPatternRole.addObserver(this);
		}
		isInitialized = true;
	}

	@Override
	public void delete() {
		// System.out.println("Deleted " + this + " for " + getPatternRole());
		if (getDrawable() != null) {
			getDrawable().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	public EditionPatternPreviewRepresentation getDrawing() {
		return (EditionPatternPreviewRepresentation) super.getDrawing();
	}

	public ShapePatternRole getPatternRole() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getPatternRole()) {
			logger.info("Notified " + dataModification);
			/*if ((dataModification instanceof CalcDrawingShapeInserted)
					|| (dataModification instanceof CalcDrawingShapeRemoved)
					|| (dataModification instanceof CalcDrawingConnectorInserted)
					|| (dataModification instanceof CalcDrawingConnectorRemoved)) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
			else*/
			if (dataModification instanceof NameChanged) {
				// logger.info("received NameChanged notification");
				setText(getText());
				notifyChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
			} else if (dataModification instanceof GraphicalRepresentationChanged) {
				logger.info("Handle GR change !!!");
				setsWith((ShapeGraphicalRepresentation<?>) getPatternRole().getGraphicalRepresentation());
			}
		}

	}

	/*@Override
	public boolean getAllowToLeaveBounds() {
		return false;
	}*/

	@Override
	public String getText() {
		if (getPatternRole() != null) {
			if (StringUtils.isNotEmpty(getPatternRole().getExampleLabel())) {
				return getPatternRole().getExampleLabel();
			}
		}
		return null;
	}

	@Override
	public void setTextNoNotification(String text) {
		// not allowed
	}

	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
		if (arg instanceof FGENotification && ((FGENotification) arg).isModelNotification() && getDrawing() != null
				&& !getDrawing().ignoreNotifications() && getPatternRole() != null) {
			getPatternRole().setChanged();
		}
	}

	private ShapePatternRole parentShape;

	@Override
	public void notifyObjectHierarchyWillBeUpdated() {
		super.notifyObjectHierarchyWillBeUpdated();
		if (parentShape != getPatternRole().getParentShapePatternRole()) {
			getDrawing().invalidateGraphicalObjectsHierarchy(getPatternRole());
		}
		parentShape = getPatternRole().getParentShapePatternRole();
	}
}
