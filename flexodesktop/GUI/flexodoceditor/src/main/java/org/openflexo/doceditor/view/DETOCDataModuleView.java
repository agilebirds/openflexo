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
package org.openflexo.doceditor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


import org.openflexo.doceditor.controller.DEController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.action.AddTOCRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;

/**
 * @author gpolet
 */
public class DETOCDataModuleView extends JPanel implements ModuleView<TOCData>, FlexoObserver, FlexoActionSource {
	private TOCData _gc;
	private DEController _controller;
	private JComponent component;
	private JPanel topPanel;
	private JPanel panel;

	public DETOCDataModuleView(TOCData gc, DEController controller) {
		super(new BorderLayout());
		_gc = gc;
		_controller = controller;
		_gc.addObserver(this);
		topPanel = new JPanel(new BorderLayout(0, 20));
		topPanel.add(new JLabel(FlexoLocalization.localizedForKey("doc_editor_introduction_text"), SwingConstants.CENTER), BorderLayout.NORTH);
		topPanel.add(panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50)), BorderLayout.CENTER);
		add(topPanel);
		validate();
		updateView();
	}

	private void updateView() {
		if (component != null) {
			panel.remove(component);
		}
		if (_gc.getRepositories().size() == 0) {
			panel.add(component = new FlexoActionButton(AddTOCRepository.actionType, this, _controller.getEditor()));
		} else {
			panel.add(component = new JLabel(FlexoLocalization.localizedForKey("please_select_a_repository"), SwingConstants.CENTER));
		}
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
		component = null;
		panel = null;
	}

	@Override
	public FlexoPerspective getPerspective() {
		return _controller.getCurrentPerspective();
	}

	@Override
	public TOCData getRepresentedObject() {
		return _gc;
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
		return _gc;
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
