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
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
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
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;


public class PortmapGR extends AbstractNodeGR<FlexoPortMap> {

	private static final Logger logger = Logger.getLogger(PortmapGR.class.getPackage().getName());

	public static final int PORTMAP_SIZE = 16;
	public static final int PORTMAP_ROUND_SIZE = 10;

	protected SimplifiedCardinalDirection layoutedAs = null;

	private PortmapRegisteryGR observedContainer;

	public PortmapGR(FlexoPortMap portmap, SwimmingLaneRepresentation aDrawing) {
		super(portmap, ShapeType.CIRCLE, aDrawing);
		setWidth(PORTMAP_ROUND_SIZE);
		setHeight(PORTMAP_ROUND_SIZE);

		setLayer(ACTIVITY_LAYER);

		setForeground(ForegroundStyle.makeStyle(Color.BLACK));
		setBackground(BackgroundStyle.makeColoredBackground(getMainBgColor()));
		// setBorder(new ShapeGraphicalRepresentation.ShapeBorder(0,10,0,0));

		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		layoutAs(SimplifiedCardinalDirection.NORTH);

		setShapePainter(new PortmapGRShapePainter());

		updatePropertiesFromWKFPreferences();

	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		getShadowStyle().setShadowDepth(1);
		getShadowStyle().setShadowBlur(3);
	}

	public FlexoPortMap getPortMap() {
		return getDrawable();
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void update(Observable observable, Object dataModification) {
		if (observable == observedContainer) {
			if ((dataModification instanceof ObjectWillMove) || (dataModification instanceof ObjectWillResize) || (dataModification instanceof ObjectHasMoved) || (dataModification instanceof ObjectHasResized) || (dataModification instanceof ObjectMove) || (dataModification instanceof ObjectResized) || (dataModification instanceof ShapeChanged)) {
				updateLayout();
			}
		}
		super.update(observable, dataModification);
	}

	@Override
	public void delete() {
		if (observedContainer != null) {
			observedContainer.deleteObserver(this);
		}
		super.delete();
	}

	public SimplifiedCardinalDirection getOrientation() {
		return layoutedAs;
	}

	private void updateLayout() {
		if ((getContainerGraphicalRepresentation() != null) && (getContainerGraphicalRepresentation() instanceof PortmapRegisteryGR)) {
			if (observedContainer != getContainerGraphicalRepresentation()) {
				if (observedContainer != null) {
					observedContainer.deleteObserver(this);
				}
				observedContainer = (PortmapRegisteryGR) getContainerGraphicalRepresentation();
				if (observedContainer != null) {
					observedContainer.addObserver(this);
				}
			}
			SimplifiedCardinalDirection orientation = observedContainer.getOrientation();
			if (orientation != layoutedAs) {
				layoutAs(orientation);
			}
		} else {
			logger.warning("Unexpected container: " + getContainerGraphicalRepresentation());
		}
	}

	protected int getIndex() {
		if (observedContainer == null) {
			return 0;
		}
		Vector<FlexoPortMap> visiblePortmaps = new Vector<FlexoPortMap>();
		for (FlexoPortMap pm : observedContainer.getPortMapRegistery().getPortMaps()) {
			if (pm.getIsVisible()) {
				visiblePortmaps.add(pm);
			}
		}

		return visiblePortmaps.indexOf(getPortMap());
	}

	private void layoutAs(SimplifiedCardinalDirection orientation) {
		if (observedContainer == null) {
			return;
		}
		int index = getIndex();

		setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			setX(PortmapRegisteryGR.PORTMAP_MARGIN + index * PORTMAP_SIZE);
			setY(0);
			setBorder(new ShapeGraphicalRepresentation.ShapeBorder(2, 18, 3, 3));
			setLocationConstrainedArea(FGELine.makeHorizontalLine(new FGEPoint(0.5, 0.5)));
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			setX(PortmapRegisteryGR.PORTMAP_MARGIN + index * PORTMAP_SIZE);
			setY(observedContainer.getHeight() - PortmapRegisteryGR.PORTMAP_REGISTERY_WIDTH);
			setBorder(new ShapeGraphicalRepresentation.ShapeBorder(18, 2, 3, 3));
			setLocationConstrainedArea(FGELine.makeHorizontalLine(new FGEPoint(0.5, 0.5)));
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			setX(0);
			setY(PortmapRegisteryGR.PORTMAP_MARGIN + index * PORTMAP_SIZE);
			setBorder(new ShapeGraphicalRepresentation.ShapeBorder(3, 3, 2, 18));
			setLocationConstrainedArea(FGELine.makeVerticalLine(new FGEPoint(0.5, 0.5)));
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			setX(observedContainer.getWidth() - PortmapRegisteryGR.PORTMAP_REGISTERY_WIDTH);
			setY(PortmapRegisteryGR.PORTMAP_MARGIN + index * PORTMAP_SIZE);
			setBorder(new ShapeGraphicalRepresentation.ShapeBorder(3, 3, 18, 2));
			setLocationConstrainedArea(FGELine.makeVerticalLine(new FGEPoint(0.5, 0.5)));
		}
		// System.out.println("layout as "+orientation+" index="+index);
		layoutedAs = orientation;
	}

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		updateLayout();
		super.notifyObjectHierarchyHasBeenUpdated();
	}

	protected ImageIcon getImageIcon() {
		if (layoutedAs == SimplifiedCardinalDirection.NORTH) {
			return WKFIconLibrary.getImageIconForPortmap(getPortMap(),PortMapRegistery.NORTH);
		}
		if (layoutedAs == SimplifiedCardinalDirection.SOUTH) {
			return WKFIconLibrary.getImageIconForPortmap(getPortMap(),PortMapRegistery.SOUTH);
		}
		if (layoutedAs == SimplifiedCardinalDirection.EAST) {
			return WKFIconLibrary.getImageIconForPortmap(getPortMap(),PortMapRegistery.EAST);
		}
		if (layoutedAs == SimplifiedCardinalDirection.WEST) {
			return WKFIconLibrary.getImageIconForPortmap(getPortMap(),PortMapRegistery.WEST);
		}
		return WKFIconLibrary.getImageIconForPortmap(getPortMap());
	}

	private class PortmapGRShapePainter implements ShapePainter {

		@Override
		public void paintShape(FGEShapeGraphics g) {
			if (layoutedAs == null) {
				return;
			}

			if (getImageIcon() != null) {
				if (layoutedAs == SimplifiedCardinalDirection.NORTH) {
					g.drawImage(getImageIcon().getImage(), new FGEPoint(-0.4, 1));
				} else if (layoutedAs == SimplifiedCardinalDirection.SOUTH) {
					g.drawImage(getImageIcon().getImage(), new FGEPoint(-0.4, -1.8));
				} else if (layoutedAs == SimplifiedCardinalDirection.WEST) {
					g.drawImage(getImageIcon().getImage(), new FGEPoint(1, -0.4));
				} else if (layoutedAs == SimplifiedCardinalDirection.EAST) {
					g.drawImage(getImageIcon().getImage(), new FGEPoint(-1.8, -0.4));
				}
			}
		};
	}

	@Override
	public void notifyObjectWillMove() {
		super.notifyObjectWillMove();
	}

	@Override
	public void notifyObjectHasMoved() {
		super.notifyObjectHasMoved();
		FlexoPortMap afterPortmap = null;
		if (observedContainer != null) {
			for (GraphicalRepresentation<?> gr : observedContainer.getContainedGraphicalRepresentations()) {
				if ((gr instanceof PortmapGR) && (gr != this) && gr.getIsVisible()) {
					PortmapGR portmapGR = (PortmapGR) gr;
					if (getOrientation().isVertical()) {
						if (portmapGR.getX() < getX()) {
							afterPortmap = portmapGR.getPortMap();
						}
					}
					if (getOrientation().isHorizontal()) {
						if (portmapGR.getY() < getY()) {
							afterPortmap = portmapGR.getPortMap();
						}
					}
				}
			}
			observedContainer.getPortMapRegistery().reorderPortmaps(getPortMap(), afterPortmap);
			for (GraphicalRepresentation gr : observedContainer.getContainedGraphicalRepresentations()) {
				if (gr instanceof PortmapGR) {
					((PortmapGR) gr).layoutAs(getOrientation());
				}
			}
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getModel()) {
			if (dataModification instanceof ObjectVisibilityChanged) {
				getDrawing().invalidateGraphicalObjectsHierarchy(getPortMap().getPortMapRegistery());
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
		super.update(observable, dataModification);
	}

}
