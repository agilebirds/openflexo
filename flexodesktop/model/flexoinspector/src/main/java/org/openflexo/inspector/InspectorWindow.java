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
package org.openflexo.inspector;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.inspector.selection.InspectorSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.swing.WindowSynchronizer;

/**
 * Inspector window
 * 
 * @author bmangez
 */
public class InspectorWindow extends JDialog implements InspectingWidget {

	private static final WindowSynchronizer inspectorWindowSynchronizer = new WindowSynchronizer();

	private InspectorController _controller;
	private InspectorTabbedPanel _content;

	protected InspectorWindow(JFrame frame, InspectorController controller) {
		super(frame);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		_controller = controller;
		// setIconImage(INSPECT_ICON.getImage());
		getContentPane().setLayout(new BorderLayout());
		setSize(InspectorCst.INSPECTOR_WINDOW_WIDTH, InspectorCst.INSPECTOR_WINDOW_HEIGHT);
		setLocation(752, 405);
		_content = new InspectorTabbedPanel(controller);
		getContentPane().add(_content, BorderLayout.CENTER);
		setFocusable(true);
		updateTitle();
		inspectorWindowSynchronizer.addToSynchronizedWindows(this);
	}

	@Override
	public void dispose() {
		_controller = null;
		_content = null;
		inspectorWindowSynchronizer.removeFromSynchronizedWindows(this);
		removeAll();
		super.dispose();
	}

	@Override
	public void newSelection(InspectorSelection selection) {
		_content.newSelection(selection);
		if (selection instanceof UniqueSelection && _content.currentTabPanel != null) {
			updateTitle(_content.currentTabPanel);
		} else {
			updateTitle();
		}
	}

	private void updateTitle() {
		updateTitle(null);
	}

	private void updateTitle(InspectorModelView currentTabPanel) {
		setTitle(getController().getWindowTitle(currentTabPanel));
	}

	@Override
	public InspectorController getController() {
		return _controller;
	}

	@Override
	public void notifiedInspectedObjectChange(InspectableObject newInspectedObject) {
		_content.notifiedInspectedObjectChange(newInspectedObject);
	}

	@Override
	public void notifiedActiveTabChange(String newActiveTabName) {
		_content.notifiedActiveTabChange(newActiveTabName);
	}

	public InspectorTabbedPanel getContent() {
		return _content;
	}

}
