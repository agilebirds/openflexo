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
package org.openflexo.prefs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.openflexo.ColorCst;
import org.openflexo.inspector.InspectorModelView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * Defines FlexoPreferences window (very similar to InspectorWindow)
 * 
 * @author sguerin
 */
public class PreferencesWindow extends JFrame
{

	public PreferencesWindow(JMenuBar menubar)
	{
		super();
		if (ToolBox.getPLATFORM().equals(ToolBox.MACOS)) {
			setJMenuBar(menubar);
		}
		getContentPane().setLayout(new BorderLayout());
		setSize(PREFERENCES_WINDOW_WIDTH, PREFERENCES_WINDOW_HEIGHT);
		setLocation(752, 405);
		reset();
		setFocusable(true);
	}

	public static final int PREFERENCES_WINDOW_WIDTH = 475;

	public static final int PREFERENCES_WINDOW_HEIGHT = 500;

	public void reset()
	{
		if (_currentScrollPane != null) {
			getContentPane().remove(_currentScrollPane);
		}
		setTitle("Preferences");
		getContentPane().setBackground(ColorCst.GUI_BACK_COLOR);
		getContentPane().add(_nothingToInspectLabel, BorderLayout.CENTER);
	}

	public void setTabPanel(InspectorModelView tabPanel)
	{
		if (_currentScrollPane != null) {
			getContentPane().remove(_currentScrollPane);
		} else {
			getContentPane().remove(_nothingToInspectLabel);
		}
		_currentScrollPane = getScrollPane(tabPanel);
		getContentPane().add(_currentScrollPane, BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		getContentPane().validate();
		currentTabPanel = tabPanel;
	}

	private JScrollPane getScrollPane(JComponent content)
	{
		JScrollPane answer = new JScrollPane(content);
		//content.setPreferredSize(new Dimension(getSize().height - 100, getSize().width - 20));
		return answer;
	}

	protected JPanel getButtonPanel()
	{
		if (_buttonPanel == null) {
			_buttonPanel = buttonPanel();
		}
		return _buttonPanel;
	}

	private JPanel buttonPanel()
	{
		JPanel answer = new JPanel(new FlowLayout());
		_saveButton = new JButton();
		_saveButton.setText(FlexoLocalization.localizedForKey("save_preferences", _saveButton));
		_saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FlexoPreferences.savePreferences(true);
				_saveButton.setEnabled(false);
				_revertButton.setEnabled(false);
				setVisible(false);
			}
		});
		_revertButton = new JButton();
		_revertButton.setText(FlexoLocalization.localizedForKey("revert_to_saved", _revertButton));
		_revertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FlexoPreferences.revertToSaved();
				_saveButton.setEnabled(false);
				_revertButton.setEnabled(false);
				setVisible(false);
			}
		});
		/*
        _closeButton = new JButton();
        _closeButton.setText(FlexoLocalization.localizedForKey("close_window", _closeButton));
        _closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        });*/
		answer.add(_saveButton);
		answer.add(_revertButton);
		//answer.add(_closeButton);
		_saveButton.setEnabled(false);
		_revertButton.setEnabled(false);
		return answer;
	}

	public void enableSaveAndRevertButtons()
	{
		if (_saveButton != null && _revertButton != null) {
			_saveButton.setEnabled(true);
			_revertButton.setEnabled(true);
		}
	}

	public InspectorModelView currentTabPanel;

	private JScrollPane _currentScrollPane;

	private JPanel _buttonPanel;

	JButton _saveButton;

	JButton _revertButton;

	//private JButton _closeButton;

	private static JLabel _nothingToInspectLabel = new JLabel("No preferences !", SwingConstants.CENTER);
}
