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

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PortMapInserted;
import org.openflexo.foundation.wkf.dm.PortMapRemoved;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class PortmapRegisteryGR extends WKFObjectGR<PortMapRegistery> {

	private static final Logger logger = Logger.getLogger(PortmapRegisteryGR.class.getPackage().getName());

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	public PortmapRegisteryGR(PortMapRegistery portmapRegistery, ProcessRepresentation aDrawing) {
		super(portmapRegistery, ShapeType.RECTANGLE, aDrawing);
		// portmapRegistery.addObserver(this);

		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.2);
		background = BackgroundStyle.makeColoredBackground(Color.ORANGE);

		setForeground(foreground);
		setBackground(background);

		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(0, 0, 0, 0));
		setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		updatePropertiesFromWKFPreferences();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
	}

	@Override
	protected boolean supportShadow() {
		return false;
	}

	private GraphicalRepresentation<?> parentGR = null;
	private FGEArea parentOutline = null;

	@Override
	public FGEArea getLocationConstrainedArea() {
		GraphicalRepresentation<?> parent = getContainerGraphicalRepresentation();
		if (parentGR == null || parent != parentGR) {
			if (parent != null && parent instanceof ShapeGraphicalRepresentation) {
				parentOutline = ((ShapeGraphicalRepresentation<?>) parent).getShape().getOutline();
				parentOutline = parentOutline.transform(AffineTransform.getScaleInstance(
						((ShapeGraphicalRepresentation<?>) parent).getWidth(), ((ShapeGraphicalRepresentation<?>) parent).getHeight()));
				parentOutline = parentOutline.transform(AffineTransform.getTranslateInstance(
						PORTMAP_REGISTERY_WIDTH / 2 - getBorder().left, PORTMAP_REGISTERY_WIDTH / 2 - getBorder().top));
				// System.out.println("Rebuild outline = "+parentOutline);
				parentGR = parent;
			}
		}
		return parentOutline;
	}

	@Override
	public void delete() {
		// PortMapRegistery portMapRegistery = getPortMapRegistery();
		super.delete();
		// portMapRegistery.deleteObserver(this);
	}

	public PortMapRegistery getPortMapRegistery() {
		return getDrawable();
	}

	@Override
	public double getX() {
		if (!getPortMapRegistery().hasLocationForContext(BASIC_PROCESS_EDITOR)) {
			getPortMapRegistery().getX(BASIC_PROCESS_EDITOR, getDefaultX());
		}
		return getPortMapRegistery().getX(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setXNoNotification(double posX) {
		getPortMapRegistery().setX(posX, BASIC_PROCESS_EDITOR);
		refreshOrientation();
	}

	@Override
	public double getY() {
		if (!getPortMapRegistery().hasLocationForContext(BASIC_PROCESS_EDITOR)) {
			getPortMapRegistery().getY(BASIC_PROCESS_EDITOR, getDefaultY());
		}
		return getPortMapRegistery().getY(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setYNoNotification(double posY) {
		getPortMapRegistery().setY(posY, BASIC_PROCESS_EDITOR);
		refreshOrientation();
	}

	@Override
	public final void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getPortMapRegistery()) {
			if ((dataModification instanceof PortMapInserted) || (dataModification instanceof PortMapRemoved)) {
				parentGR = null;
				refreshOrientation();
				// GPO: We need to invalidate the hierarchy because portmaps play with visibility and not with add/remove
				// If we used add/remove to play with the visibility of portmaps, then simply update the graphical objects
				// hierarchy should be sufficient
				// Bug 1006069 Fix: Edges are not displayed anymore when adding ports to the process used by subprocess node
				getDrawing().invalidateGraphicalObjectsHierarchy(getPortMapRegistery());
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ObjectVisibilityChanged) {
				// logger.info("Detected ObjectVisibilityChanged !!!");
				getDrawing().invalidateGraphicalObjectsHierarchy(getPortMapRegistery());
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ObjectLocationChanged) {
				notifyObjectMoved();
			}
		}
	}

	@Override
	public void update(Observable observable, Object dataModification) {
		// System.out.println("Received "+dataModification+" for "+observable);
		if (observable == getContainerGraphicalRepresentation()) {
			if ((dataModification instanceof ObjectWillMove) || (dataModification instanceof ObjectWillResize)
					|| (dataModification instanceof ObjectHasMoved) || (dataModification instanceof ObjectHasResized)
					|| (dataModification instanceof ObjectMove) || (dataModification instanceof ObjectResized)
					|| (dataModification instanceof ShapeChanged)) {
				// Reinit parent outline that will change
				parentGR = null;
			}
		}
		super.update(observable, dataModification);
	}

	private SimplifiedCardinalDirection _orientation;

	public SimplifiedCardinalDirection getOrientation() {
		if (_orientation == null) {
			refreshOrientation();
		}
		return _orientation;
	}

	public void refreshOrientation() {
		// getPortMapRegistery().printObservers();
		// logger.info("Sinon moi en tant que GR, j'ai: "+countObservers()+" observers");
		SubProcessNodeGR subProcessNodeGR = (SubProcessNodeGR) getContainerGraphicalRepresentation();
		if (!GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(this, subProcessNodeGR)) {
			// Might happen when big restructuration (eg multiple deletion, see Bug 1005566)
			return;
		}
		FGEPoint locationInSubProcessNode = GraphicalRepresentation.convertNormalizedPoint(this, new FGEPoint(0.5, 0.5), subProcessNodeGR);
		SimplifiedCardinalDirection orientation = FGEPoint.getSimplifiedOrientation(new FGEPoint(0.5, 0.5), locationInSubProcessNode);
		if (orientation != _orientation) {
			logger.info("Switch to orientation = " + orientation);
			_orientation = orientation;
			int portmapNb = 0;
			for (FlexoPortMap pm : getPortMapRegistery().getPortMaps()) {
				if (pm.getIsVisible()) {
					portmapNb++;
				}
			}
			if (_orientation.isVertical()) { /* NORTH or SOUTH */
				setWidthNoNotification(PortmapGR.PORTMAP_SIZE * portmapNb + PORTMAP_MARGIN * 2);
				setHeightNoNotification(PORTMAP_REGISTERY_WIDTH);
				notifyObjectResized();
			} else { /* EAST or WEST */
				setHeightNoNotification(PortmapGR.PORTMAP_SIZE * portmapNb + PORTMAP_MARGIN * 2);
				setWidthNoNotification(PORTMAP_REGISTERY_WIDTH);
				notifyObjectResized();
			}
			// ((SubProcessNodeGR)getContainerGraphicalRepresentation()).notifyShapeNeedsToBeRedrawn();
		}
	}

	// Override to implement defaut automatic layout
	public double getDefaultX() {
		ShapeGraphicalRepresentation parentGR = (ShapeGraphicalRepresentation) getGraphicalRepresentation(getPortMapRegistery()
				.getSubProcessNode());
		if (parentGR != null) {
			int portmapNb = 0;
			for (FlexoPortMap pm : getPortMapRegistery().getPortMaps()) {
				if (pm.getIsVisible()) {
					portmapNb++;
				}
			}
			double portmapRegisteryWidth = PortmapGR.PORTMAP_SIZE * portmapNb + 6;
			return computeConstrainedLocation(new FGEPoint(
					(parentGR.getWidth() + parentGR.getBorder().left + parentGR.getBorder().right - portmapRegisteryWidth) / 2, 0)).x;
		}
		return 0;
	}

	// Override to implement defaut automatic layout
	public double getDefaultY() {
		return computeConstrainedLocation(new FGEPoint(0, 0)).y;
	}

}
