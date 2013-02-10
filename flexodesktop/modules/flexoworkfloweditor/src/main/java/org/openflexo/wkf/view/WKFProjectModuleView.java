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
package org.openflexo.wkf.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.listener.FlexoActionButton;
import org.openflexo.wkf.controller.WKFController;

/**
 * @author sylvain
 */
public class WKFProjectModuleView extends JPanel implements ModuleView<FlexoWorkflow>, FlexoObserver, FlexoActionSource {
	private FlexoWorkflow workflow;
	private WKFController controller;
	private JPanel panel;

	private FlexoPerspective declaredPerspective;

	public WKFProjectModuleView(FlexoWorkflow workflow, WKFController controller, FlexoPerspective perspective) {
		super(new BorderLayout());
		declaredPerspective = perspective;
		this.workflow = workflow;
		this.controller = controller;
		workflow.addObserver(this);
		panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		JButton button = new JButton(WKFIconLibrary.WKF_RP_ACTIVE_ICON);
		button.setText(FlexoLocalization.localizedForKey("switch_to_role_perspective"));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WKFProjectModuleView.this.controller.getControllerModel().setCurrentPerspective(
						WKFProjectModuleView.this.controller.ROLE_EDITOR_PERSPECTIVE);
			}
		});
		buttonPanel.add(new FlexoActionButton(AddSubProcess.actionType, this, controller));
		buttonPanel.add(button);
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
		workflow.deleteObserver(this);
		controller.removeModuleView(this);
		panel = null;
	}

	@Override
	public FlexoPerspective getPerspective() {
		return declaredPerspective;
	}

	@Override
	public FlexoWorkflow getRepresentedObject() {
		return workflow;
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
		return workflow;
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
