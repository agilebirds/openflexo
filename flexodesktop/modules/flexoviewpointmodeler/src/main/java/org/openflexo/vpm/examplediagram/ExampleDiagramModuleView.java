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
package org.openflexo.vpm.examplediagram;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.view.ModuleView;
import org.openflexo.vpm.controller.ViewPointPerspective;

public class ExampleDiagramModuleView extends JPanel implements ModuleView<ExampleDiagram>, PropertyChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ExampleDiagramModuleView.class.getPackage().getName());

	private ExampleDiagramController _controller;

	public ExampleDiagramModuleView(ExampleDiagramController controller) {
		super();
		setLayout(new BorderLayout());
		_controller = controller;
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(_controller.getToolbox().getToolPanel());
		topPanel.add(_controller.getToolbox().getStyleToolBar());
		topPanel.add(_controller.getToolbox().getLayoutToolBar());
		add(topPanel, BorderLayout.NORTH);
		add(new JScrollPane(_controller.getDrawingView()), BorderLayout.CENTER);
		validate();

		getRepresentedObject().getPropertyChangeSupport().addPropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
	}

	public ExampleDiagramController getController() {
		return _controller;
	}

	@Override
	public void deleteModuleView() {
		getRepresentedObject().getPropertyChangeSupport().removePropertyChangeListener(getRepresentedObject().getDeletedProperty(), this);
		getController().delete();
	}

	@Override
	public ViewPointPerspective getPerspective() {
		return getController().getCEDController().VIEW_POINT_PERSPECTIVE;
	}

	@Override
	public ExampleDiagram getRepresentedObject() {
		return _controller.getExampleDiagram();
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
		getPerspective().focusOnExampleDiagram(getRepresentedObject());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getRepresentedObject() && evt.getPropertyName().equals(getRepresentedObject().getDeletedProperty())) {
			deleteModuleView();
		}
	}
}
