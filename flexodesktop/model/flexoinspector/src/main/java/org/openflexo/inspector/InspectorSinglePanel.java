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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.openflexo.inspector.model.TabModel;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.InspectorSelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;

public class InspectorSinglePanel extends JPanel implements InspectingWidget {
	private static final Logger logger = Logger.getLogger(InspectorSinglePanel.class.getPackage().getName());

	private final JLabel _nothingToInspectLabel = new JLabel("Nothing to inspect", SwingConstants.CENTER);

	private final JLabel _multipleSelectionLabel = new JLabel("Multiple selection", SwingConstants.CENTER);

	private final TabModelView _tabModelView;

	private JComponent currentPane;
	private JScrollPane _currentScrollPane;

	private final InspectorController _controller;

	public InspectorSinglePanel(InspectorController controller, TabModel tabModel) {
		super(new BorderLayout());
		_controller = controller;
		_tabModelView = new TabModelView(tabModel, null, controller);
		setTabPanelToNone();
	}

	protected void updateTabPanel() {
		if (currentPane != _currentScrollPane) {
			if (currentPane != null) {
				remove(currentPane);
			}
			_currentScrollPane = getScrollPane(_tabModelView);
			currentPane = _currentScrollPane;
			add(_currentScrollPane, BorderLayout.CENTER);
			validate();
			repaint();
		}
	}

	protected void setTabPanelToNone() {
		if (currentPane != null) {
			remove(currentPane);
		}
		currentPane = _nothingToInspectLabel;
		add(currentPane, BorderLayout.CENTER);
		validate();
		repaint();
	}

	protected void setTabPanelToMultiple() {
		if (currentPane != null) {
			remove(currentPane);
		}
		currentPane = _multipleSelectionLabel;
		add(currentPane, BorderLayout.CENTER);
		validate();
		repaint();
	}

	private JScrollPane getScrollPane(JComponent content) {
		JScrollPane answer = new JScrollPane(content);
		// content.setPreferredSize(new Dimension(getSize().height - 40, getSize().width - 20));
		answer.setBorder(BorderFactory.createEmptyBorder());
		return answer;
	}

	@Override
	public void newSelection(InspectorSelection selection) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("newSelection() with " + selection);
		}
		InspectorSelection inspectorSelection = selection;
		if (inspectorSelection instanceof EmptySelection) {
			setTabPanelToNone();
		} else if (inspectorSelection instanceof MultipleSelection) {
			setTabPanelToMultiple();
		} else if (inspectorSelection instanceof UniqueSelection) {
			InspectableObject inspectedObject = ((UniqueSelection) inspectorSelection).getInspectedObject();
			_tabModelView.performObserverSwitch(inspectedObject);
			_tabModelView.valueChange(inspectedObject);
			updateTabPanel();
		}
	}

	@Override
	public void notifiedInspectedObjectChange(InspectableObject newInspectedObject) {
		getController().notifiedInspectedObjectChange(newInspectedObject);
	}

	@Override
	public void notifiedActiveTabChange(String newActiveTabName) {
		// Not relevant for this widget
	}

	@Override
	public InspectorController getController() {
		return _controller;
	}

}
