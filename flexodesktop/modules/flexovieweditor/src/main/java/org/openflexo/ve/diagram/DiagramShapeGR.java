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

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.model.dm.ConnectorInserted;
import org.openflexo.foundation.view.diagram.model.dm.ConnectorRemoved;
import org.openflexo.foundation.view.diagram.model.dm.ElementUpdated;
import org.openflexo.foundation.view.diagram.model.dm.ShapeInserted;
import org.openflexo.foundation.view.diagram.model.dm.ShapeRemoved;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementAction;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementSpecification;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.toolbox.ConcatenedList;
import org.openflexo.toolbox.ToolBox;

public class DiagramShapeGR extends ShapeGraphicalRepresentationImpl implements GraphicalFlexoObserver, DiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DiagramShapeGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public DiagramShapeGR(VirtualModelInstanceBuilder builder) {
		super(ShapeType.RECTANGLE, null, null);
	}

	public DiagramShapeGR(DiagramShape aShape, Drawing<?> aDrawing) {
		super(ShapeType.RECTANGLE, aShape, aDrawing);

		registerShapeGR(aShape, aDrawing);
	}

	public boolean isGRRegistered = false;

	public boolean isGRRegistered() {
		return isGRRegistered;
	}

	public void registerShapeGR(DiagramShape aShape, Drawing<?> aDrawing) {
		setDrawable(aShape);
		setDrawing(aDrawing);
		addToMouseClickControls(new DiagramController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new DiagramController.ShowContextualMenuControl(true));
		}
		addToMouseDragControls(new DrawEdgeControl());

		registerMouseClickControls();

		if (aShape != null) {
			aShape.addObserver(this);
		}

		if (aShape != null) {
			aShape.update();
		}
		isGRRegistered = true;
	}

	private void registerMouseClickControls() {
		if (getDrawable() != null) {
			if (getDrawable().getPatternRole() != null) {
				for (GraphicalElementAction.ActionMask mask : getDrawable().getPatternRole().getReferencedMasks()) {
					addToMouseClickControls(new VEMouseClickControl(mask, getDrawable().getPatternRole()));
				}
			}
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

	@Override
	public DiagramRepresentation getDrawing() {
		return (DiagramRepresentation) super.getDrawing();
	}

	public DiagramShape getDiagramShape() {
		return getDrawable();
	}

	@Override
	public int getIndex() {
		if (getDiagramShape() != null) {
			return getDiagramShape().getIndex();
		}
		return super.getIndex();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getDiagramShape()) {
			// logger.info("Notified "+dataModification);
			if (dataModification instanceof ShapeInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ShapeRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ConnectorInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ConnectorRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ElementUpdated) {
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
	protected void hasChanged(FGENotification notification) {
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
	public boolean getAllowToLeaveBounds() {
		return false;
	}*/

	/*@Override
	public String getText() {
		if (getOEShape() != null) {
			return getOEShape().getName();
		}
		return null;
	}

	@Override
	public void setTextNoNotification(String text) {
		if (getOEShape() != null) {
			getOEShape().setName(text);
		}
	}*/

	private ConcatenedList<ControlArea<?>> controlAreas;

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (controlAreas == null) {
			controlAreas = new ConcatenedList<ControlArea<?>>();
			controlAreas.addElementList(super.getControlAreas());

			// Vincent
			/*controlAreas.addElement(new CircularFloatingPalette(this, getDrawable().getDiagram().getRootPane(),
					SimplifiedCardinalDirection.WEST));*/

			if (getDiagramShape().providesSupportAsPrimaryRole() && getDiagramShape().getAvailableLinkSchemeFromThisShape() != null
					&& getDiagramShape().getAvailableLinkSchemeFromThisShape().size() > 0) {
				boolean northDirectionSupported = false;
				boolean eastDirectionSupported = false;
				boolean southDirectionSupported = false;
				boolean westDirectionSupported = false;
				for (LinkScheme ls : getDiagramShape().getAvailableLinkSchemeFromThisShape()) {
					if (ls.getNorthDirectionSupported()) {
						northDirectionSupported = true;
					}
					if (ls.getEastDirectionSupported()) {
						eastDirectionSupported = true;
					}
					if (ls.getSouthDirectionSupported()) {
						southDirectionSupported = true;
					}
					if (ls.getWestDirectionSupported()) {
						westDirectionSupported = true;
					}
				}

				if (northDirectionSupported) {
					controlAreas.addElement(new FloatingPalette(this, getDrawable().getDiagram().getRootPane(),
							SimplifiedCardinalDirection.NORTH));
				}
				if (eastDirectionSupported) {
					controlAreas.addElement(new FloatingPalette(this, getDrawable().getDiagram().getRootPane(),
							SimplifiedCardinalDirection.EAST));
				}
				if (southDirectionSupported) {
					controlAreas.addElement(new FloatingPalette(this, getDrawable().getDiagram().getRootPane(),
							SimplifiedCardinalDirection.SOUTH));
				}
				if (westDirectionSupported) {
					controlAreas.addElement(new FloatingPalette(this, getDrawable().getDiagram().getRootPane(),
							SimplifiedCardinalDirection.WEST));
				}
			}
		}
		return controlAreas;
	}

	/**
	 * We dont want URI to be renamed all the time: we decide here to disable continuous text editing
	 */
	@Override
	public boolean getContinuousTextEditing() {
		return false;
	}

}
