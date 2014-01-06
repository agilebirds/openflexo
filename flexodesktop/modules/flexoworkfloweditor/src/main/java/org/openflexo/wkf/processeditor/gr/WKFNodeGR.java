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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.node.WKFNode;
import org.openflexo.wkf.processeditor.DrawEdgeControl;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SWLEditorConstants;

public abstract class WKFNodeGR<O extends WKFNode> extends WKFObjectGR<O> implements GraphicalFlexoObserver, ProcessEditorConstants {

	private static final Logger logger = Logger.getLogger(WKFNodeGR.class.getPackage().getName());
	protected boolean isUpdatingPosition = false;
	protected double defaultX = -1;
	protected double defaultY = -1;

	public WKFNodeGR(O object, ShapeType shapeType, ProcessRepresentation aDrawing) {
		super(object, shapeType, aDrawing);
		addToMouseDragControls(new DrawEdgeControl());
	}

	@Override
	public final double getX() {
		if (!getNode().hasLocationForContext(BASIC_PROCESS_EDITOR)) {
			if (!isRegistered()) {
				return 0;
			}
			return getNode().getX(BASIC_PROCESS_EDITOR, getDefaultX());
		}
		return getNode().getX(BASIC_PROCESS_EDITOR);
	}

	@Override
	public final void setXNoNotification(double posX) {
		isUpdatingPosition = true;
		getNode().setX(posX, BASIC_PROCESS_EDITOR);
		isUpdatingPosition = false;
	}

	@Override
	public final double getY() {
		if (!getNode().hasLocationForContext(BASIC_PROCESS_EDITOR)) {
			if (!isRegistered()) {
				return 0;
			}
			return getNode().getY(BASIC_PROCESS_EDITOR, getDefaultY());
		}
		return getNode().getY(BASIC_PROCESS_EDITOR);
	}

	@Override
	public final void setYNoNotification(double posY) {
		isUpdatingPosition = true;
		getNode().setY(posY, BASIC_PROCESS_EDITOR);
		isUpdatingPosition = false;
	}

	@Override
	public double getAbsoluteTextX() {
		if (!getNode().hasLabelLocationForContext(BASIC_PROCESS_EDITOR)) {
			if (!isRegistered()) {
				return 0;
			}
			return getNode().getLabelX(BASIC_PROCESS_EDITOR, getDefaultLabelX());
		}
		return getNode().getLabelX(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setAbsoluteTextXNoNotification(double posX) {
		getNode().setLabelX(posX, BASIC_PROCESS_EDITOR);
	}

	@Override
	public double getAbsoluteTextY() {
		if (!getNode().hasLabelLocationForContext(BASIC_PROCESS_EDITOR)) {
			return getNode().getLabelY(BASIC_PROCESS_EDITOR, getDefaultLabelY());
		}
		return getNode().getLabelY(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setAbsoluteTextYNoNotification(double posY) {
		getNode().setLabelY(posY, BASIC_PROCESS_EDITOR);
	}

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		if (getDrawing() != null) {
			getDrawing().getDrawingGraphicalRepresentation().updateConstraintsForObject(this);
		}
		super.notifyObjectHierarchyHasBeenUpdated();
	}

	public O getNode() {
		return getDrawable();
	}

	public double getDefaultX() {
		if (defaultX < 0) {
			doDefaultLayout(_getDefaultX(), _getDefaultY());
		}
		return defaultX;
	}

	public double getDefaultY() {
		if (defaultY < 0) {
			doDefaultLayout(_getDefaultX(), _getDefaultY());
		}
		return defaultY;
	}

	protected double _getDefaultX() {
		return 10;
	}

	protected double _getDefaultY() {
		return 10;
	}

	private boolean isLayingout = false;

	protected boolean hasLocation() {
		return getModel().hasLocationForContext(BASIC_PROCESS_EDITOR);
	}

	protected void doDefaultLayout(double x, double y) {
		if (!isRegistered()) {
			return;
		}
		if (isLayingout) {
			return;
		}
		isLayingout = true;
		try {
			if (getContainerGraphicalRepresentation() != null) {
				doLayoutMethod1();
				if (defaultX < 0 || defaultY < 0) {
					doLayoutMethod2();
				}
				if (defaultX < 0 || defaultY < 0) {
					doLayoutMethod3(x, y);
				}
				if (defaultX >= 0 && defaultY >= 0) {
					notifyObjectMoved();
				}
			}
		} finally {
			isLayingout = false;
		}
	}

	protected void doLayoutMethod1() {
		// Override this method if the view can do a more clever thing than method 2 and 3
	}

	/**
	 * Looks for relative position in BPE view on related objects
	 */
	protected void doLayoutMethod2() {
		for (WKFNodeGR<?> nodeGR : getFromInterestingNodeGR()) {
			double x, y;
			if (!nodeGR.hasLocation()) {
				nodeGR.doDefaultLayout(-1, -1);
				x = nodeGR.defaultX;
				y = nodeGR.defaultY;
			} else {
				x = nodeGR.getX();
				y = nodeGR.getY();
			}
			if (x >= 0 && y >= 0) {
				defaultX = x + nodeGR.getWidth() + 30;
				defaultY = y;
				break;
			}
		}
		if (defaultX < 0 && defaultY < 0) {
			for (WKFNodeGR<?> nodeGR : getToInterestingNodeGR()) {
				double x, y;
				if (!nodeGR.getModel().hasLocationForContext(BASIC_PROCESS_EDITOR)) {
					nodeGR.doDefaultLayout(-1, -1);
					x = nodeGR.defaultX;
					y = nodeGR.defaultY;
				} else {
					x = nodeGR.getModel().getLocation(BASIC_PROCESS_EDITOR).getX();
					y = nodeGR.getModel().getLocation(BASIC_PROCESS_EDITOR).getY();
				}
				if (x >= 0 && y >= 0) {
					defaultX = x - getWidth() - 30;
					defaultY = y;
				}
			}
		}
	}

	/**
	 * Performs dumb layout to try not to be on other views of this level
	 */
	protected void doLayoutMethod3(double x, double y) {
		Iterator<GraphicalRepresentation> en = null;
		double attemptX = x, attemptY = y;
		boolean found = false;
		while (!found) {
			en = getContainerGraphicalRepresentation().getContainedGraphicalRepresentations().iterator();
			found = true;
			while (en.hasNext()) {
				GraphicalRepresentation gr = en.next();
				if (gr instanceof WKFNodeGR<?>) {
					WKFNodeGR<?> rgr = (WKFNodeGR<?>) gr;
					if (rgr != this) {
						if (rgr.hasLocation()) {
							java.awt.Rectangle viewBounds = gr.getViewBounds(1.0);
							if (viewBounds.intersects(new java.awt.Rectangle((int) attemptX, (int) attemptY, (int) getWidth(),
									(int) getHeight()))) {
								// The attempt location intersects with another one, let's move forward
								found = false;
								if (viewBounds.x + viewBounds.width + getWidth() > getDrawingGraphicalRepresentation().getWidth()) {
									// End of line, we go to the next one
									if (attemptY + 10 + getHeight() < getDrawingGraphicalRepresentation().getHeight()) {
										attemptX = x;
										attemptY = attemptY + 10 + getHeight();
										break;
									} else {
										if (logger.isLoggable(Level.WARNING)) {
											logger.warning("Could not find suitable location for node (bpe): " + getModel());
										}
										break;
									}
								} else {
									attemptX = viewBounds.x + viewBounds.width + 10;
									break;
								}
							}
						}
					}
				}
			}
		}
		if (found) {
			defaultX = attemptX;
			defaultY = attemptY;
		} else {
			defaultX = x;
			defaultY = y;
		}
	}

	protected List<WKFNodeGR<?>> getFromInterestingNodeGR() {
		List<WKFNodeGR<?>> v = new ArrayList<WKFNodeGR<?>>();
		Iterator<GraphicalRepresentation> en = getDrawingGraphicalRepresentation().getContainedGraphicalRepresentations().iterator();
		while (en.hasNext()) {
			GraphicalRepresentation gr = en.next();
			if (gr instanceof ConnectorGraphicalRepresentation) {
				ConnectorGraphicalRepresentation connector = (ConnectorGraphicalRepresentation) gr;
				if (connector.getEndObject() == this && connector.getStartObject() instanceof WKFObjectGR<?>) {
					findSiblingGRFromNodeAndAddToVector((WKFObjectGR<?>) connector.getStartObject(), v);
				}
			}
		}
		return v;
	}

	protected List<WKFNodeGR<?>> getToInterestingNodeGR() {
		List<WKFNodeGR<?>> v = new ArrayList<WKFNodeGR<?>>();
		Iterator<GraphicalRepresentation> en = getDrawingGraphicalRepresentation().getContainedGraphicalRepresentations().iterator();
		while (en.hasNext()) {
			GraphicalRepresentation gr = en.next();
			if (gr instanceof ConnectorGraphicalRepresentation) {
				ConnectorGraphicalRepresentation connector = (ConnectorGraphicalRepresentation) gr;
				if (connector.getStartObject() == this && connector.getEndObject() instanceof WKFObjectGR<?>) {
					findSiblingGRFromNodeAndAddToVector((WKFObjectGR<?>) connector.getEndObject(), v);
				}
			}
		}
		return v;
	}

	protected void findSiblingGRFromNodeAndAddToVector(WKFObjectGR<?> gr, List<WKFNodeGR<?>> list) {
		while (gr != null && gr.getParentGraphicalRepresentation() != getParentGraphicalRepresentation()) {
			if (gr.getParentGraphicalRepresentation() instanceof WKFObjectGR<?>) {
				gr = (WKFObjectGR<?>) gr.getParentGraphicalRepresentation();
			} else {
				gr = null;
			}
		}
		if (gr != null && gr instanceof WKFNodeGR<?> && !list.contains(gr)) {
			list.add((WKFNodeGR<?>) gr);
		}
	}

	public double getDefaultLabelX() {
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getX();
		}
		return 0;
	}

	public double getDefaultLabelY() {
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getY();
		}
		return 0;
	}

}
