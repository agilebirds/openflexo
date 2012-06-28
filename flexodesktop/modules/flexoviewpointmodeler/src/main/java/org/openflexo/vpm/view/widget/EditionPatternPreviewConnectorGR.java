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
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.GraphicalRepresentationChanged;

public class EditionPatternPreviewConnectorGR extends ConnectorGraphicalRepresentation<ConnectorPatternRole> implements
		GraphicalFlexoObserver, EditionPatternPreviewConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EditionPatternPreviewConnectorGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public EditionPatternPreviewConnectorGR() {
		super(ConnectorType.LINE, null, null, null, null);
	}

	public EditionPatternPreviewConnectorGR(ConnectorPatternRole aPatternRole, EditionPatternPreviewRepresentation aDrawing) {
		super(ConnectorType.LINE, aDrawing != null ? aDrawing.getStartShape(aPatternRole) : null, aDrawing != null ? aDrawing
				.getEndShape(aPatternRole) : null, aPatternRole, aDrawing);
		// System.out.println("CREATED " + this + " for " + getPatternRole());
		// logger.info("CREATED " + this + " for " + getPatternRole());
		init(aPatternRole, aDrawing);

	}

	private ShapeGraphicalRepresentation<?> startObjectGR;
	private ShapeGraphicalRepresentation<?> endObjectGR;

	@Override
	public ShapeGraphicalRepresentation<?> getStartObject() {
		if (startObjectGR == null && getDrawing() != null) {
			startObjectGR = getDrawing().getStartShape(getPatternRole());
			enableStartObjectObserving(startObjectGR);
		}
		return startObjectGR;
	}

	@Override
	public ShapeGraphicalRepresentation<?> getEndObject() {
		if (endObjectGR == null && getDrawing() != null) {
			endObjectGR = getDrawing().getEndShape(getPatternRole());
			enableEndObjectObserving(endObjectGR);
		}
		return endObjectGR;
	}

	@Override
	public void notifyObjectHierarchyWillBeUpdated() {
		super.notifyObjectHierarchyWillBeUpdated();
		if (startObjectGR != getDrawing().getStartShape(getPatternRole()) || endObjectGR != getDrawing().getEndShape(getPatternRole())) {
			dismissGraphicalRepresentation();
		}
	}

	protected void dismissGraphicalRepresentation() {
		// logger.info("*********** dismissGraphicalRepresentation()");
		disableStartObjectObserving();
		disableEndObjectObserving();
		startObjectGR = null;
		endObjectGR = null;
		getDrawing().invalidateGraphicalObjectsHierarchy(getPatternRole());
	}

	private boolean isInitialized = false;

	public boolean isInitialized() {
		return isInitialized;
	}

	public void init(ConnectorPatternRole aPatternRole, EditionPatternPreviewRepresentation aDrawing) {
		setDrawable(aPatternRole);
		setDrawing(aDrawing);

		setStartObject(aDrawing.getStartShape(aPatternRole));
		setEndObject(aDrawing.getEndShape(aPatternRole));

		if (aPatternRole != null) {
			aPatternRole.addObserver(this);
		}

		isInitialized = true;
	}

	@Override
	public void delete() {
		// logger.info("DELETED " + this + " for " + getPatternRole());
		if (getDrawable() != null) {
			getDrawable().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	public EditionPatternPreviewRepresentation getDrawing() {
		return (EditionPatternPreviewRepresentation) super.getDrawing();
	}

	public ConnectorPatternRole getPatternRole() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getPatternRole()) {
			if (dataModification instanceof NameChanged) {
				// logger.info("received NameChanged notification");
				notifyChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
				// setText(getText());
			}
		} else if (dataModification instanceof GraphicalRepresentationChanged) {
			logger.info("Handle GR change !!!");
			setsWith((ConnectorGraphicalRepresentation<?>) getPatternRole().getGraphicalRepresentation());
		}
	}

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
		// Not allowed
	}

	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
		if (arg instanceof FGENotification && ((FGENotification) arg).isModelNotification() && getDrawing() != null
				&& !getDrawing().ignoreNotifications() && getPatternRole() != null) {
			getPatternRole().setChanged();
		}
	}

}
