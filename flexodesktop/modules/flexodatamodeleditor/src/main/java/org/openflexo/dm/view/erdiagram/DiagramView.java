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
package org.openflexo.dm.view.erdiagram;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import org.openflexo.dm.view.controller.DiagramPerspective;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.view.ModuleView;

public class DiagramView extends DrawingView<ERDiagramRepresentation> implements ModuleView<ERDiagram>, PropertyChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DiagramView.class.getPackage().getName());

	public DiagramView(ERDiagramRepresentation aDrawing, ERDiagramController controller) {
		super(aDrawing, controller);
		getRepresentedObject().getPropertyChangeSupport().addPropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
	}

	@Override
	public ERDiagramController getController() {
		return (ERDiagramController) super.getController();
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
	public DiagramPerspective getPerspective() {
		return getController().getDMController().DIAGRAM_PERSPECTIVE;
	}

	public FlexoProject getProject() {
		return getRepresentedObject().getProject();
	}

	@Override
	public ERDiagram getRepresentedObject() {
		return getModel().getDiagram();
	}

	@Override
	public boolean isAutoscrolled() {
		return false;
	}

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
		getPerspective().focusOnDiagram(getRepresentedObject());
	}

	/*private DrawRoleSpecializationAction _drawRoleSpecializationAction;

	public void  setDrawEdgeAction(DrawRoleSpecializationAction action) 
	{
		_drawRoleSpecializationAction = action;
	}

	public void resetDrawEdgeAction() 
	{
		_drawRoleSpecializationAction = null;
		repaint();
	}*/

	@Override
	public void paint(Graphics g) {
		// boolean isBuffering = isBuffering();
		super.paint(g);
		/*	if (_drawRoleSpecializationAction != null && !isBuffering) {
				_drawRoleSpecializationAction.paint(g,getController());
			}*/
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getRepresentedObject() && evt.getPropertyName().equals(getRepresentedObject().getDeletedProperty())) {
			deleteModuleView();
		}
	}
}
