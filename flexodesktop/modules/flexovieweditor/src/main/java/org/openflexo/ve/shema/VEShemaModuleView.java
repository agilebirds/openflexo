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
package org.openflexo.ve.shema;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.ve.controller.DiagramPerspective;
import org.openflexo.view.ModuleView;

public class VEShemaModuleView extends JPanel implements ModuleView<View>, PropertyChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(VEShemaModuleView.class.getPackage().getName());

	private VEShemaController _controller;

	public VEShemaModuleView(VEShemaController controller) {
		super();
		setLayout(new BorderLayout());
		_controller = controller;
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(_controller.getToolbox().getStyleToolBar());
		add(topPanel, BorderLayout.NORTH);
		add(new JScrollPane(_controller.getDrawingView()), BorderLayout.CENTER);
		validate();
		getRepresentedObject().getPropertyChangeSupport().addPropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
	}

	public VEShemaController getController() {
		return _controller;
	}

	@Override
	public void deleteModuleView() {
		getRepresentedObject().getPropertyChangeSupport().removePropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
		if (getController() != null) {
			getController().delete();
		}
	}

	@Override
	public DiagramPerspective getPerspective() {
		return getController().getVEController().DIAGRAM_PERSPECTIVE;
	}

	public FlexoProject getProject() {
		return getRepresentedObject().getProject();
	}

	@Override
	public View getRepresentedObject() {
		return _controller.getShema();
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
		getPerspective().focusOnShema(getRepresentedObject());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getRepresentedObject() && evt.getPropertyName().equals(getRepresentedObject().getDeletedProperty())) {
			deleteModuleView();
		}
	}
}
