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
package org.openflexo.ve.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.diagram.action.CreateDiagram;
import org.openflexo.ve.controller.VEController;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.listener.FlexoActionButton;

/**
 * @author vincent
 */
public class ViewModuleView extends JPanel implements ModuleView<View>, FlexoObserver, FlexoActionSource {
	private View view;
	private VEController controller;
	private JPanel panel;

	private FlexoPerspective declaredPerspective;

	public ViewModuleView(View view, VEController controller, FlexoPerspective perspective) {
		super(new BorderLayout());
		declaredPerspective = perspective;
		this.view = view;
		this.controller = controller;
		view.addObserver(this);
		panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		buttonPanel.add(new FlexoActionButton(CreateDiagram.actionType, this, controller));
		buttonPanel.add(new FlexoActionButton(CreateVirtualModelInstance.actionType, this, controller));
		panel.add(buttonPanel);
		add(panel);
		revalidate();
	}

	@Override
	public void update(final FlexoObservable observable, final DataModification dataModification) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					update(observable, dataModification);
				}
			});
			return;
		}
		if (dataModification instanceof ObjectDeleted) {
			deleteModuleView();
		}
	}

	@Override
	public void deleteModuleView() {
		view.deleteObserver(this);
		controller.removeModuleView(this);
		panel = null;
	}

	@Override
	public FlexoPerspective getPerspective() {
		return declaredPerspective;
	}

	@Override
	public View getRepresentedObject() {
		return view;
	}

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
	}

	/**
	 * Overrides getEditor
	 * 
	 * @see org.openflexo.foundation.action.FlexoActionSource#getEditor()
	 */
	@Override
	public FlexoEditor getEditor() {
		return controller.getEditor();
	}

	/**
	 * Overrides getFocusedObject
	 * 
	 * @see org.openflexo.foundation.action.FlexoActionSource#getFocusedObject()
	 */
	@Override
	public FlexoModelObject getFocusedObject() {
		return view;
	}

	/**
	 * Overrides getGlobalSelection
	 * 
	 * @see org.openflexo.foundation.action.FlexoActionSource#getGlobalSelection()
	 */
	@Override
	public Vector getGlobalSelection() {
		return null;
	}

	@Override
	public boolean isAutoscrolled() {
		return false;
	}

}
