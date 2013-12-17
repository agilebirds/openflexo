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
package org.openflexo.vpm.diagrampalette;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.view.ModuleView;
import org.openflexo.vpm.controller.ViewPointPerspective;

public class DiagramPaletteModuleView extends JPanel implements ModuleView<DiagramPalette>, PropertyChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DiagramPaletteModuleView.class.getPackage().getName());

	private final DiagramPaletteEditor _controller;

	public DiagramPaletteModuleView(final DiagramPaletteEditor controller) {
		super();
		setLayout(new BorderLayout());
		_controller = controller;

		add(controller.getDrawingView(), BorderLayout.CENTER);
		revalidate();

		getRepresentedObject().getPropertyChangeSupport().addPropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
	}

	public DiagramPaletteEditor getController() {
		return _controller;
	}

	@Override
	public void deleteModuleView() {
		getRepresentedObject().getPropertyChangeSupport().removePropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
		getController().delete();
	}

	@Override
	public ViewPointPerspective getPerspective() {
		return getController().getVPMController().VIEW_POINT_PERSPECTIVE;
	}

	public FlexoProject getProject() {
		return getRepresentedObject().getProject();
	}

	@Override
	public DiagramPalette getRepresentedObject() {
		return _controller.getDiagramPalette();
	}

	@Override
	public boolean isAutoscrolled() {
		return true;
	}

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
		getPerspective().focusOnPalette(getRepresentedObject());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getRepresentedObject() && evt.getPropertyName().equals(getRepresentedObject().getDeletedProperty())) {
			deleteModuleView();
		}
	}

}
