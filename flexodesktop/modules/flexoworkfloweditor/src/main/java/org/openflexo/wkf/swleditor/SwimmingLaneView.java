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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.view.ModuleView;
import org.openflexo.wkf.controller.SwimmingLanePerspective;
import org.openflexo.wkf.swleditor.SWLDrawEdgeControl.SWLDrawEdgeAction;
import org.openflexo.wkf.swleditor.gr.NodePalette;

public class SwimmingLaneView extends DrawingView<SwimmingLaneRepresentation> implements ModuleView<FlexoProcess>, PropertyChangeListener {

	public SwimmingLaneView(SwimmingLaneRepresentation aDrawing, SwimmingLaneEditorController controller) {
		super(aDrawing, controller);
		getRepresentedObject().getPropertyChangeSupport().addPropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
	}

	@Override
	public SwimmingLaneEditorController getController() {
		return (SwimmingLaneEditorController) super.getController();
	}

	@Override
	public void delete() {
		getRepresentedObject().getPropertyChangeSupport().removePropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
		super.delete();
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
		getPaintManager().clearPaintBuffer();
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
	public void paint(Graphics g) {
		boolean isBuffering = isBuffering();
		super.paint(g);
		/*boolean paintRoleConstraints = !isBuffering && (_swlDrawEdgeAction!=null || draggedNodePalette!=null);
		if (paintRoleConstraints) {
			FGEDrawingGraphics graphics = getDrawingGraphicalRepresentation().getGraphics();
			Graphics2D g2 = (Graphics2D)g;
			graphics.createGraphics(g2,getController());
			for(RoleContainerGR roleGR:getController().getDrawing().getAllVisibleRoleContainers()) {
				paintControlArea(roleGR.getLanesArea(), graphics);
			}
		}*/
		if (_swlDrawEdgeAction != null && !isBuffering) {
			_swlDrawEdgeAction.paint(g, getController());
		}
		if (draggedNodePalette != null && !isBuffering) {
			draggedNodePalette.paint(g, getController());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getRepresentedObject() && evt.getPropertyName().equals(getRepresentedObject().getDeletedProperty())) {
			deleteModuleView();
		}
	}
}
