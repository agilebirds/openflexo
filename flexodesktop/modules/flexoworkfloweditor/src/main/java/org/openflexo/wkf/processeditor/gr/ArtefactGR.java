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

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.WKFNode;
import org.openflexo.toolbox.ConcatenedList;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SWLEditorConstants;

public class ArtefactGR<O extends WKFArtefact> extends WKFNodeGR<O> {

	private static final Logger logger = Logger.getLogger(ArtefactGR.class.getPackage().getName());

	private ConcatenedList<ControlArea<?>> concatenedList;

	// private boolean isUpdatingPosition = false;

	public ArtefactGR(O annotation, ShapeType shapeType, ProcessRepresentation aDrawing) {
		super(annotation, shapeType, aDrawing);
		if (getDrawable().getLevel() == FlexoLevel.ACTIVITY) {
			setLayer(ACTIVITY_LAYER);
		} else if (getDrawable().getLevel() == FlexoLevel.OPERATION) {
			setLayer(OPERATION_LAYER);
		} else if (getDrawable().getLevel() == FlexoLevel.ACTION) {
			setLayer(ACTION_LAYER);
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
	 * Looks for relative position in SWL view on related objects
	 */
	@Override
	protected void doLayoutMethod1() {
		if (!getModel().hasLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return;
		}
		Point2D swlPosition = getModel().getLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR);
		FlexoPetriGraph parentPetrigraph = getModel().getParentPetriGraph();
		Vector<WKFNode> v = new Vector<WKFNode>();
		v.addAll(getModel().getAllRelatedFromNodes());
		v.addAll(getModel().getAllRelatedToNodes());
		for (WKFNode node : v) {
			if (node instanceof FlexoPreCondition) {
				node = ((FlexoPreCondition) node).getAttachedNode();
			}
			if (node instanceof WKFArtefact && ((WKFArtefact) node).getParentPetriGraph() == parentPetrigraph) {
				if (node.hasLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR) && node.hasLocationForContext(BASIC_PROCESS_EDITOR)) {
					Point2D p1 = node.getLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR);
					Point2D p2 = node.getLocation(BASIC_PROCESS_EDITOR);
					defaultX = (int) (p2.getX() + swlPosition.getX() - p1.getX());
					defaultY = (int) (p2.getY() + swlPosition.getY() - p1.getY());
					break;
				}
			}
		}
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return concatenedList;
	}

	/*
	 * @Override public void setBorder(org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder border) { if (border ==null) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Cannot set null border on ArtifactGR"); border = new ShapeBorder(0,0,0,20); } else
	 * { if (border.right<20) border.right=20; if (border.top<10) border.top=10; if (border.bottom<10) border.bottom=10; }
	 * super.setBorder(border); }
	 */

	@Override
	public FGESteppedDimensionConstraint getDimensionConstraintStep() {
		if (getDrawing() != null) {
			return getDrawing().getDrawingGraphicalRepresentation().getDimensionConstraintsForObject(this);
		}
		return null;
	}

	@Override
	public double getWidth() {
		return getDrawable().getWidth(BASIC_PROCESS_EDITOR, getDefaultWidth());
	}

	@Override
	public void setWidthNoNotification(double width) {
		getDrawable().setWidth(width, BASIC_PROCESS_EDITOR);
	}

	@Override
	public double getHeight() {
		return getDrawable().getHeight(BASIC_PROCESS_EDITOR, getDefaultHeight());
	}

	@Override
	public void setHeightNoNotification(double height) {
		getDrawable().setHeight(height, BASIC_PROCESS_EDITOR);
	}

	public double getDefaultWidth() {
		if (getDimensionConstraints() == DimensionConstraints.UNRESIZABLE || getDimensionConstraints() == DimensionConstraints.WIDTH_FIXED
				|| !getDrawable().hasDimensionForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return 30.0;
		} else {
			return getDrawable().getWidth(SWLEditorConstants.SWIMMING_LANE_EDITOR);
		}
	}

	public double getDefaultHeight() {
		if (getDimensionConstraints() == DimensionConstraints.UNRESIZABLE || getDimensionConstraints() == DimensionConstraints.HEIGHT_FIXED
				|| !getDrawable().hasDimensionForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return 30.0;
		} else {
			return getDrawable().getHeight(SWLEditorConstants.SWIMMING_LANE_EDITOR);
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
		if (getDrawable().getTextAlignment() instanceof ParagraphAlignment) {
			setParagraphAlignment(getDrawable().getTextAlignment());
		}
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
