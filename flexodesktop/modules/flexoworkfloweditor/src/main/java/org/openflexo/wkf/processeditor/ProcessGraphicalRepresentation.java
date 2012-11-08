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
package org.openflexo.wkf.processeditor;

import java.awt.Color;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEFiniteGrid;
import org.openflexo.fge.graphics.DrawingDecorationPainter;
import org.openflexo.fge.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.fge.impl.DrawingGraphicalRepresentationImpl;
import org.openflexo.fge.impl.ForegroundStyleImpl;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.gr.WKFConnectorGR;
import org.openflexo.wkf.processeditor.gr.WKFObjectGR;

public class ProcessGraphicalRepresentation extends DrawingGraphicalRepresentationImpl<FlexoProcess> implements ProcessEditorConstants {
	private FlexoProcess process;

	protected static final Color GRID_COLOR = new Color(220, 220, 220);

	protected FGEFiniteGrid constraints;
	private FGESteppedDimensionConstraint dimensionConstraints;
	private boolean showGrid = false;

	protected ProcessGraphicalRepresentation(Drawing<FlexoProcess> drawing, FlexoProcess process) {
		super(drawing);
		this.process = process;
		addToMouseClickControls(new AnnotationMouseClickControl());
		checkAndSetGrid();
		setDecorationPainter(new DrawingDecorationPainter() {

			@Override
			public void paintDecoration(FGEDrawingDecorationGraphics g) {
				if (getShowGrid()) {
					g.useForegroundStyle(ForegroundStyleImpl.makeStyle(GRID_COLOR));
					double w = g.getWidth();
					double h = g.getHeight();
					double scaledHStep = constraints.getHorizontalStep();
					double scaledVStep = constraints.getVerticalStep();
					int m = (int) Math.floor(w / scaledHStep);
					int n = (int) Math.floor(h / scaledVStep);
					int borderSize = getGraphics().getDefaultForeground() != null ? (int) Math.ceil(getGraphics().getDefaultForeground()
							.getLineWidth()) : 0;
					// Vertical lines
					for (int i = 1; i < m + 1; i++) {
						double x1 = i * scaledHStep;
						g.drawLine(x1, borderSize, x1, h - borderSize);
					}
					// Horizontal lines
					for (int i = 1; i < n + 1; i++) {
						double y1 = i * scaledHStep;
						g.drawLine(borderSize, y1, w - borderSize, y1);
					}
				}
			}

			@Override
			public boolean paintBeforeDrawing() {
				return false;
			}
		});
		updateConstraints();
	}

	/**
	 *
	 */
	private void updateConstraints() {
		constraints = new FGEFiniteGrid(new FGEPoint(0, 0), getGridSize(), getGridSize(), getProcessBounds());
		// constraints = new FGEGrid(new FGEPoint(0,0),getGridSize(),getGridSize());
		dimensionConstraints = new FGESteppedDimensionConstraint(getGridSize(), getGridSize());
	}

	private FGERectangle getProcessBounds() {
		return new FGERectangle(0, 0, process.getWidth(BASIC_PROCESS_EDITOR), process.getHeight(BASIC_PROCESS_EDITOR), Filling.FILLED);
	}

	public void updateAlignOnGridOrGridSize() {
		updateConstraints();
		for (GraphicalRepresentation<?> gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof WKFObjectGR && ((WKFObjectGR<?>) gr).getDimensionConstraints() != DimensionConstraints.CONTAINER) {
				updateConstraintsForObject((WKFObjectGR<?>) gr);
			}
		}
	}

	public void updateConstraintsForObject(WKFObjectGR<?> gr) {
		if (gr.getContainerGraphicalRepresentation() == this) {
			if (isAlignedOnGrid()) {
				if (gr.supportAlignOnGrid()) {
					gr.setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
					if (gr.supportResizeToGrid()) {
						gr.setDimensionConstraints(DimensionConstraints.STEP_CONSTRAINED);
					}
					if (FGEConstants.APPLY_CONSTRAINTS_IMMEDIATELY) {
						gr.checkAndUpdateLocationAndDimension();
					}
				}
			} else {
				if (gr.supportResizeToGrid()) {
					gr.setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
				}
				gr.setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
			}
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	public FGEArea getLocationConstraintsForObject(WKFObjectGR<?> gr) {
		if (gr.getContainerGraphicalRepresentation() == this) {
			if (isAlignedOnGrid()) {
				if (gr.supportAlignOnGrid()) {
					FGEFiniteGrid grid = constraints.clone();
					grid.bounds.width -= gr.getWidth();
					grid.bounds.height -= gr.getHeight();
					if (gr.getDimensionConstraints() == DimensionConstraints.UNRESIZABLE) {
						grid.origin.x -= gr.getWidth() / 2;
						grid.origin.y -= gr.getHeight() / 2;
					}
					if (gr.getBorder() != null) {
						grid.origin.x -= gr.getBorder().getLeft();
						grid.origin.y -= gr.getBorder().getTop();
					}
					if (gr.getShape().getShape().getEmbeddingBounds().x > 0) {
						grid.origin.x -= gr.getShape().getShape().getEmbeddingBounds().x * gr.getWidth();
					}
					if (gr.getShape().getShape().getEmbeddingBounds().y > 0) {
						grid.origin.y -= gr.getShape().getShape().getEmbeddingBounds().y * gr.getHeight();
					}
					/*if (gr.getShadowStyle()!=null) {
						grid.origin.x-=gr.getShadowStyle().getShadowDeep();
						grid.origin.y-=gr.getShadowStyle().getShadowDeep();
					}*/
					return grid;
				}
			} else {
				if (gr.getDimensionConstraints() != DimensionConstraints.CONTAINER) {
					return new FGERectangle(0, 0, getDrawingGraphicalRepresentation().getWidth() - gr.getWidth(),
							getDrawingGraphicalRepresentation().getHeight() - gr.getHeight(), Filling.FILLED);
				}
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	private boolean isAlignedOnGrid() {
		return process.getIsAlignedOnGrid(BASIC_PROCESS_EDITOR, WKFPreferences.getAlignOnGrid());
	}

	private int getGridSize() {
		return process.getGridSize(BASIC_PROCESS_EDITOR, WKFPreferences.getGridSize());
	}

	/**
	 *
	 */
	private void checkAndSetGrid() {
		if (!process._booleanGraphicalPropertyForKey(BASIC_PROCESS_EDITOR + "-" + GRID_SIZE_IS_SET, false)) {
			process.setGridSize(WKFPreferences.getGridSize(), BASIC_PROCESS_EDITOR);
			process.setIsAlignedOnGrid(WKFPreferences.getAlignOnGrid(), BASIC_PROCESS_EDITOR);
			process._setGraphicalPropertyForKey(true, BASIC_PROCESS_EDITOR + "-" + GRID_SIZE_IS_SET);
		}
	}

	public FGESteppedDimensionConstraint getDimensionConstraintsForObject(WKFObjectGR<?> gr) {
		if (gr.getContainerGraphicalRepresentation() == this) {
			if (isAlignedOnGrid()) {
				if (gr.supportAlignOnGrid()) {
					if (gr.supportResizeToGrid()) {
						return dimensionConstraints;
					}
				}
			}
		}
		return null;
	}

	@Override
	public double getWidth() {
		return process.getWidth(BASIC_PROCESS_EDITOR, PROCESS_DEFAULT_WIDTH);
	}

	@Override
	public double getHeight() {
		return process.getHeight(BASIC_PROCESS_EDITOR, PROCESS_DEFAULT_HEIGHT);
	}

	@Override
	public void setWidth(double value) {
		process.setWidth(value, BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setHeight(double value) {
		process.setHeight(value, BASIC_PROCESS_EDITOR);
	}

	@Override
	public String toString() {
		return "Graphical representation of " + process;
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
		notifyDrawingNeedsToBeRedrawn();
	}

	public void updateAllEdgeLayers() {
		for (GraphicalRepresentation<?> processChild : getContainedGraphicalRepresentations()) {
			if (processChild instanceof WKFConnectorGR<?>) {
				((WKFConnectorGR<?>) processChild).updateLayer();
			}
		}

	}
}
