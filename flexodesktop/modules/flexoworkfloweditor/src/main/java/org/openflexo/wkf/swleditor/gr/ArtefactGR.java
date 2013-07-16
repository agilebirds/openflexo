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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.WKFNode;
import org.openflexo.toolbox.ConcatenedList;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class ArtefactGR<O extends WKFArtefact> extends WKFNodeGR<O> {

	private static final Logger logger = Logger.getLogger(ArtefactGR.class.getPackage().getName());
	private ConcatenedList<ControlArea<?>> concatenedList;

	// private boolean isUpdatingPosition = false;

	public ArtefactGR(O annotation, ShapeType shapeType, SwimmingLaneRepresentation aDrawing) {
		super(annotation, shapeType, aDrawing);
		if (getDrawable().getLevel() == FlexoLevel.ACTIVITY) {
			setLayer(ACTIVITY_LAYER - 1);
		} else if (getDrawable().getLevel() == FlexoLevel.OPERATION) {
			setLayer(OPERATION_LAYER - 1);
		} else if (getDrawable().getLevel() == FlexoLevel.ACTION) {
			setLayer(ACTION_LAYER - 1);
		}
		concatenedList = new ConcatenedList<ControlArea<?>>();
		concatenedList.addElementList(super.getControlAreas());
		concatenedList.addElement(new NodePalette(this, annotation.getParentPetriGraph()));
	}

	int getTopBorder() {
		return REQUIRED_SPACE_ON_TOP;
	}

	int getBottomBorder() {
		return REQUIRED_SPACE_ON_BOTTOM;
	}

	int getLeftBorder() {
		return REQUIRED_SPACE_ON_LEFT;
	}

	int getRightBorder() {
		return hasNodePalette() ? REQUIRED_SPACE_ON_RIGHT_FOR_PALETTE : REQUIRED_SPACE_ON_RIGHT;
	}

	public boolean hasNodePalette() {
		return true;
	}

	/**
	 * Looks for relative position in BPE view on related objects
	 */
	@Override
	protected void doLayoutMethod1() {
		if (!getModel().hasLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return;
		}
		Point2D bpePosition = getModel().getLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		FlexoPetriGraph parentPetrigraph = getModel().getParentPetriGraph();
		Vector<WKFNode> v = new Vector<WKFNode>();
		v.addAll(getModel().getAllRelatedFromNodes());
		v.addAll(getModel().getAllRelatedToNodes());
		for (WKFNode node : v) {
			if (node instanceof FlexoPreCondition) {
				node = ((FlexoPreCondition) node).getAttachedNode();
			}
			if (node instanceof FlexoNode && ((FlexoNode) node).isEndNode()
					&& ((FlexoNode) node).getParentPetriGraph().getContainer() instanceof WKFNode) {
				node = (WKFNode) ((FlexoNode) node).getParentPetriGraph().getContainer();
			}
			if (node instanceof WKFArtefact && ((WKFArtefact) node).getParentPetriGraph() == parentPetrigraph) {
				if (node.hasLocationForContext(SWIMMING_LANE_EDITOR)
						&& node.hasLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
					Point2D p1 = node.getLocation(SWIMMING_LANE_EDITOR);
					Point2D p2 = node.getLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
					defaultX = (int) (p2.getX() + bpePosition.getX() - p1.getX());
					defaultY = (int) (p2.getY() + bpePosition.getY() - p1.getY());
					break;
				}
			}
			if (node instanceof PetriGraphNode && ((PetriGraphNode) node).getParentPetriGraph() == parentPetrigraph) {
				GraphicalRepresentation gr = getGraphicalRepresentation(node);
				if (gr != null) {
					AffineTransform at = convertCoordinatesAT(gr.getParentGraphicalRepresentation(), getParentGraphicalRepresentation(),
							1.0);
					if (node.hasLocationForContext(SWIMMING_LANE_EDITOR)
							&& node.hasLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
						Point2D p1 = node.getLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
						Point2D p2 = node.getLocation(SWIMMING_LANE_EDITOR);
						FGEPoint p3 = new FGEPoint(p2).transform(at);
						defaultX = (int) (p3.getX() + bpePosition.getX() - p1.getX());
						defaultY = (int) (p3.getY() + bpePosition.getY() - p1.getY());
						break;
					}
				}
			}
		}
		if (defaultX < 0 && defaultY < 0) {
			defaultX = bpePosition.getX();
			defaultY = bpePosition.getY();
		}
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return concatenedList;
	}

	/*
	 * @Override public void setBorder(org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder border) { if (border ==null) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Cannot set null border on PetriGraphNodeGR"); border = new ShapeBorder(0,0,0,20);
	 * } else { if (border.right<20) border.right=20; if (border.top<10) border.top=10; if (border.bottom<10) border.bottom=10; }
	 * super.setBorder(border); }
	 */

	@Override
	public double getY() {
		return getYNoOffset() + getYOffset();
	}

	private double getYNoOffset() {
		return super.getY();
	}

	@Override
	public void setYNoNotification(double posY) {
		if (getDrawable().hasLocationForContext(SWIMMING_LANE_EDITOR)) {
			getDrawable().setY(posY - getYOffset(), SWIMMING_LANE_EDITOR);
		} else {
			getDrawable().setY(posY, SWIMMING_LANE_EDITOR);
		}
	}

	private double getYOffset() {
		if (getDrawing() != null && getDrawing().isVisible(getModel().getProcess().getPortRegistery())) {
			if (getDrawing().getGraphicalRepresentation(getModel().getProcess().getPortRegistery()) != null) {
				Rectangle rect = getDrawing().getGraphicalRepresentation(getModel().getProcess().getPortRegistery()).getViewBounds(1.0);
				return rect.height;
			} else {
				return 0;
			}
		}
		return 0;
	}

	@Override
	public void notifyObjectHasMoved() {
		super.notifyObjectHasMoved();
		notifyObjectMoved(null);
	}

	@Override
	public double getWidth() {
		return getDrawable().getWidth(SWIMMING_LANE_EDITOR, getDefaultWidth());
	}

	@Override
	public void setWidthNoNotification(double width) {
		getDrawable().setWidth(width, SWIMMING_LANE_EDITOR);
	}

	@Override
	public double getHeight() {
		return getDrawable().getHeight(SWIMMING_LANE_EDITOR, getDefaultHeight());
	}

	@Override
	public void setHeightNoNotification(double height) {
		getDrawable().setHeight(height, SWIMMING_LANE_EDITOR);
	}

	public double getDefaultWidth() {
		if (getDimensionConstraints() == DimensionConstraints.UNRESIZABLE || getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED
				|| !getDrawable().hasDimensionForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return 30.0;
		} else {
			return getDrawable().getWidth(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		}
	}

	public double getDefaultHeight() {
		if (getDimensionConstraints() == DimensionConstraints.UNRESIZABLE || getDimensionConstraints() == DimensionConstraints.HEIGHT_FIXED
				|| !getDrawable().hasDimensionForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return 30.0;
		} else {
			return getDrawable().getHeight(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		}
	}

	@Override
	public String getText() {
		return getDrawable().getText();
	}

	private boolean isUpdatingText = false;

	@Override
	public void setTextNoNotification(String text) {
		isUpdatingText = true;
		getDrawable().setText(text);
		isUpdatingText = false;
	}

	@Override
	protected boolean supportShadow() {
		return false;
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		setBorder(new ShapeBorder(getTopBorder(), getBottomBorder(), getLeftBorder(), getRightBorder()));
		setIsMultilineAllowed(true);
		setTextStyle(createTextStyle());
		if (getDrawable().getTextAlignment() == null || !(getDrawable().getTextAlignment() instanceof ParagraphAlignment)) {
			getDrawable().setTextAlignment(GraphicalRepresentation.ParagraphAlignment.CENTER);
		}
		setParagraphAlignment((ParagraphAlignment) getDrawable().getTextAlignment());
	}

	protected TextStyle createTextStyle() {
		return TextStyle.makeTextStyle(getDrawable().getTextColor(), getArtefactFont().getFont());
	}

	private FlexoFont getArtefactFont() {
		if (getDrawable().getWorkflow() != null) {
			return getDrawable().getWorkflow().getArtefactFont(WKFPreferences.getArtefactFont());
		} else {
			return WKFPreferences.getArtefactFont();
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getDrawable()) {
			if (dataModification instanceof WKFAttributeDataModification) {
				if (((WKFAttributeDataModification) dataModification).getAttributeName().equals("text") && isUpdatingText) {
					return;
				}
				updatePropertiesFromWKFPreferences();
				notifyShapeNeedsToBeRedrawn();
			}
		}
		super.update(observable, dataModification);
	}

}
