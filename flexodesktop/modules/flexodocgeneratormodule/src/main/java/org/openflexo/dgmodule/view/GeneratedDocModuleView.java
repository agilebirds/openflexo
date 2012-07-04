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
package org.openflexo.dgmodule.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;

/**
 * @author sylvain
 */
public class GeneratedDocModuleView extends JPanel implements ModuleView<GeneratedDoc>, FlexoObserver, FlexoActionSource {
	private GeneratedDoc generatedDocumentation;
	private DGController _controller;
	private JComponent addDGRepositoryButton;
	private JPanel panel;

	private FlexoPerspective<? super GeneratedDoc> declaredPerspective;

	public GeneratedDocModuleView(GeneratedDoc gd, DGController controller, FlexoPerspective<? super GeneratedDoc> perspective) {
		super(new BorderLayout());
		declaredPerspective = perspective;
		generatedDocumentation = gd;
		_controller = controller;
		generatedDocumentation.addObserver(this);
		panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
		add(panel);
		validate();
		updateView();
	}

	private void updateView() {
		if (addDGRepositoryButton != null) {
			panel.remove(addDGRepositoryButton);
		}
		panel.add(addDGRepositoryButton = new FlexoActionButton(AddGeneratedCodeRepository.actionType, this, _controller.getEditor()),
				BorderLayout.CENTER);
		panel.validate();
		panel.repaint();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		updateView();
	}

	@Override
	public void deleteModuleView() {
		_controller.removeModuleView(this);
		generatedDocumentation.deleteObserver(this);
		addDGRepositoryButton = null;
		panel = null;
	}

	@Override
	public FlexoPerspective<? super GeneratedDoc> getPerspective() {
		return declaredPerspective;
	}

	@Override
	public GeneratedDoc getRepresentedObject() {
		return generatedDocumentation;
	}

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() {
		return true;
	}

	/**
	 * Overrides getEditor
	 * 
	 * @see org.openflexo.foundation.action.FlexoActionSource#getEditor()
	 */
	@Override
	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}

	/**
	 * Overrides getFocusedObject
	 * 
	 * @see org.openflexo.foundation.action.FlexoActionSource#getFocusedObject()
	 */
	@Override
	public FlexoModelObject getFocusedObject() {
		return generatedDocumentation;
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

}
