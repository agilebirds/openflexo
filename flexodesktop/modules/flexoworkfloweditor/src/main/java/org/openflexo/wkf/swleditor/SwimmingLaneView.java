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
package org.openflexo.wkf.swleditor;

import java.awt.Graphics;

import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.view.ModuleView;
import org.openflexo.wkf.controller.SwimmingLanePerspective;
import org.openflexo.wkf.swleditor.SWLDrawEdgeControl.SWLDrawEdgeAction;
import org.openflexo.wkf.swleditor.gr.NodePalette;

public class SwimmingLaneView extends DrawingView<SwimmingLaneRepresentation> implements ModuleView<FlexoProcess> {

	public SwimmingLaneView(SwimmingLaneRepresentation aDrawing, SwimmingLaneEditorController controller) {
		super(aDrawing, controller);
	}

	@Override
	public SwimmingLaneEditorController getController() {
		return (SwimmingLaneEditorController) super.getController();
	}

	@Override
	public void deleteModuleView() {
		getController().delete();
	}

	@Override
	public SwimmingLanePerspective getPerspective() {
		return getController().getWKFController().SWIMMING_LANE_PERSPECTIVE;
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
	}

	@Override
	public void willShow() {
		getController().getDrawing().enableGraphicalHierarchy();
	}

	private SWLDrawEdgeAction _swlDrawEdgeAction;

	public void setDrawEdgeAction(SWLDrawEdgeAction action) {
		_swlDrawEdgeAction = action;
		repaint();
	}

	public void resetDrawEdgeAction() {
		_swlDrawEdgeAction = null;
		repaint();
	}

	private NodePalette draggedNodePalette;

	public void setDraggedNodePalette(NodePalette palette) {
		draggedNodePalette = palette;
		repaint();
	}

	public void resetDraggedNodePalette() {
		draggedNodePalette = null;
		repaint();
	}

	@Override
	public void doUnbufferedPaint(Graphics g) {
		super.doUnbufferedPaint(g);
		if (_swlDrawEdgeAction != null) {
			_swlDrawEdgeAction.paint(g, getController());
		}
		if (draggedNodePalette != null) {
			draggedNodePalette.paint(g, getController());
		}
	}

}
