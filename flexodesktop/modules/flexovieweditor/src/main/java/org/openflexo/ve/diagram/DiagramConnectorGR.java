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
package org.openflexo.ve.diagram;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.impl.ConnectorGraphicalRepresentationImpl;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction;
import org.openflexo.fge.controller.MouseControl.MouseButton;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.dm.ElementUpdated;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementAction;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementAction.ActionMask;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementSpecification;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.toolbox.ToolBox;

public class DiagramConnectorGR extends ConnectorGraphicalRepresentationImpl implements GraphicalFlexoObserver, DiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DiagramConnectorGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public DiagramConnectorGR(VirtualModelInstanceBuilder builder) {
		this(null, null);
	}

	public DiagramConnectorGR(DiagramConnector aConnector, Drawing<?> aDrawing) {
		super(ConnectorType.LINE, aDrawing != null ? (ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(aConnector
				.getStartShape()) : null, aDrawing != null ? (ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(aConnector
				.getEndShape()) : null, aConnector, aDrawing);
		// setText(getRole().getName());

		addToMouseClickControls(new DiagramController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new DiagramController.ShowContextualMenuControl(true));
		}

		registerMouseClickControls();

		if (aConnector != null) {
			aConnector.addObserver(this);
		}

	}

	private void registerMouseClickControls() {
		boolean doubleClickUsed = false;
		if (getDrawable() != null) {
			if (getDrawable().getPatternRole() != null) {
				for (GraphicalElementAction.ActionMask mask : getDrawable().getPatternRole().getReferencedMasks()) {
					addToMouseClickControls(new VEMouseClickControl(mask, getDrawable().getPatternRole()));
					doubleClickUsed |= mask == ActionMask.DoubleClick;
				}
			}
		}

		if (!doubleClickUsed) {
			addToMouseClickControls(new MouseClickControl("reset_layout", MouseButton.LEFT, 2, new MouseClickControlAction() {

				@Override
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						MouseEvent event) {
					if (graphicalRepresentation instanceof ConnectorGraphicalRepresentation<?>) {
						getConnectorSpecification().resetConnectorLayout();
				}
					return true;
			}

				@Override
				public PredefinedMouseClickControlActionType getActionType() {
					return PredefinedMouseClickControlActionType.CUSTOM;
				}
			}, false, false, false, false));
		}
	}

	@Override
	public void setValidated(boolean validated) {
		super.setValidated(validated);
		update();
	}

	@Override
	public void delete() {
		if (getDrawable() != null) {
			getDrawable().deleteObserver(this);
		}
		super.delete();
	}

	public DiagramConnector getDiagramConnector() {
		return getDrawable();
	}

	@Override
	public int getIndex() {
		if (getDiagramConnector() != null) {
			return getDiagramConnector().getIndex();
		}
		return super.getIndex();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getDiagramConnector()) {
			/*if (dataModification instanceof NameChanged) {
				// logger.info("received NameChanged notification");
				// notifyChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
				// setText(getText());
			} else*/if (dataModification instanceof ElementUpdated) {
				update();
			}
		}
	}

	private boolean isUpdating = false;

	/**
	 * This method is called whenever a change has been detected in the object affecting this graphical representation. We iterate on all
	 * GraphicalElementSpecification to update data.
	 */
	public void update() {
		isUpdating = true;
		if (getDrawable() != null && getDrawable().getPatternRole() != null) {
			setIsLabelEditable(!getDrawable().getPatternRole().getReadOnlyLabel());
			for (GraphicalElementSpecification grSpec : getDrawable().getPatternRole().getGrSpecifications()) {
				if (grSpec.getValue().isValid()) {
					grSpec.applyToGraphicalRepresentation(this, getDrawable());
				}
			}
		}
		isUpdating = false;

		// setText(getDrawable().getLabelValue());
	}

	/**
	 * This method is called whenever a change has been performed through GraphicalEdition framework. Notification is caught here. If the
	 * model edition matches a non read-only feature
	 * 
	 */
	@Override
	protected void hasChanged(FGEAttributeNotification notification) {
		super.hasChanged(notification);
		if (isUpdating) {
			return;
		}
		if (isValidated()) {
			if (getDrawable() != null && getDrawable().getPatternRole() != null) {
				for (GraphicalElementSpecification grSpec : getDrawable().getPatternRole().getGrSpecifications()) {
					if (grSpec.getFeature().getParameter() == notification.parameter && grSpec.getValue().isValid()
							&& !grSpec.getReadOnly()) {
						Object value = grSpec.applyToModel(this, getDrawable());
						logger.info("Applying to model " + grSpec.getValue() + " new value=" + value);
					}
				}
			}
		}
	}

	/*@Override
	public String getText() {
		if (getOEConnector() != null) {
			return getOEConnector().getName();
		}
		return null;
	}

	@Override
	public void setTextNoNotification(String text) {
		if (getOEConnector() != null) {
			getOEConnector().setName(text);
		}
	}*/

	/**
	 * We dont want URI to be renamed all the time: we decide here to disable continuous text editing
	 */
	/*@Override
	public boolean getContinuousTextEditing() {
		return false;
	}*/

}
