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
package org.openflexo.ve.diagram;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.ve.controller.ViewLibraryPerspective;
import org.openflexo.view.ModuleView;

/**
 * This is the module view dedicated to represent a {@link View}
 * 
 * @author sylvain
 * 
 */
public class DiagramModuleView extends JPanel implements ModuleView<Diagram>, PropertyChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DiagramModuleView.class.getPackage().getName());

	private DiagramController _controller;

	public DiagramModuleView(DiagramController controller) {
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

	public DiagramController getController() {
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
	public ViewLibraryPerspective getPerspective() {
		return getController().getVEController().VIEW_LIBRARY_PERSPECTIVE;
	}

	public FlexoProject getProject() {
		return getRepresentedObject().getProject();
	}

	@Override
	public Diagram getRepresentedObject() {
		return _controller.getDiagram();
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
		getPerspective().focusOnDiagram(getRepresentedObject());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getRepresentedObject() && evt.getPropertyName().equals(getRepresentedObject().getDeletedProperty())) {
			deleteModuleView();
		}
	}
}
