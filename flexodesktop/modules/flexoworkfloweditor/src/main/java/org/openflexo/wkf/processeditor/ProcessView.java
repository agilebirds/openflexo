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

import java.awt.Graphics;
import java.util.logging.Logger;

import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.WKFEdge;
import org.openflexo.view.ModuleView;
import org.openflexo.wkf.controller.ProcessPerspective;
import org.openflexo.wkf.processeditor.DrawEdgeControl.DrawEdgeAction;
import org.openflexo.wkf.processeditor.gr.EdgeGR;
import org.openflexo.wkf.processeditor.gr.NodePalette;

public class ProcessView extends DrawingView<ProcessRepresentation> implements ModuleView<FlexoProcess> {

	private static final Logger logger = Logger.getLogger(ProcessView.class.getPackage().getName());

	public ProcessView(ProcessRepresentation aDrawing, ProcessEditorController controller) {
		super(aDrawing, controller);
	}

	@Override
	public ProcessGraphicalRepresentation getDrawingGraphicalRepresentation() {
		return getDrawing().getDrawingGraphicalRepresentation();
	}

	@Override
	public ProcessEditorController getController() {
		return (ProcessEditorController) super.getController();
	}

	@Override
	public void deleteModuleView() {
		logger.info("deleteModuleView for process");
		getController().delete();
		delete();
	}

	@Override
	public ProcessPerspective getPerspective() {
		return getController().getWKFController().PROCESS_EDITOR_PERSPECTIVE;
	}

	public FlexoProject getProject() {
		return getRepresentedObject().getProject();
	}

	@Override
	public FlexoProcess getRepresentedObject() {
		return getModel().getProcess();
	}

	@Override
	public boolean isAutoscrolled() {
		return false;
	}

	@Override
	public void willHide() {
		getController().getDrawing().disableGraphicalHierarchy();
		getPaintManager().clearPaintBuffer();
	}

	@Override
	public void willShow() {
		getController().getDrawing().enableGraphicalHierarchy();
	}

	private DrawEdgeAction _drawEdgeAction;

	public void setDrawEdgeAction(DrawEdgeAction action) {
		_drawEdgeAction = action;
	}

	public void resetDrawEdgeAction() {
		_drawEdgeAction = null;
		repaint();
	}

	private NodePalette draggedNodePalette;

	public void setDraggedNodePalette(NodePalette palette) {
		draggedNodePalette = palette;
	}

	public void resetDraggedNodePalette() {
		draggedNodePalette = null;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		boolean isBuffering = isBuffering();
		super.paint(g);
		if (_drawEdgeAction != null && !isBuffering) {
			_drawEdgeAction.paint(g, getController());
		}
		if (draggedNodePalette != null && !isBuffering) {
			draggedNodePalette.paint(g, getController());
		}
	}

	public void refreshConnectors() {
		for (Object gr : getGraphicalRepresentation().getContainedGraphicalRepresentations()) {
			if (gr instanceof EdgeGR) {
				((EdgeGR<WKFEdge<?, ?>>) gr).updatePropertiesFromWKFPreferences();
			}
		}
	}

}
