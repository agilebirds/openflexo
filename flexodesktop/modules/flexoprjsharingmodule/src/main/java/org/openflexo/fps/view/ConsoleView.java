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
package org.openflexo.fps.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openflexo.FlexoCst;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.JConsole;

import org.openflexo.fps.CVSConsole;
import org.openflexo.fps.CVSConsole.ConsoleListener;

public class ConsoleView extends JPanel implements ConsoleListener {
	StringBuffer sbContent;

	private JConsole console;

	private JCheckBox showCommandLogs;
	private JCheckBox showErrorsLogs;
	private JCheckBox showInfoLogs;
	private JButton clearConsole;

	public ConsoleView() {
		super(new BorderLayout());

		sbContent = new StringBuffer();

		console = new JConsole();
		console.setText("");
		console.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		console.setFont(FlexoCst.CODE_FONT);
		console.setForeground(Color.DARK_GRAY);
		console.setEditable(false);
		console.setMinimumSize(new Dimension(0, 0));

		add(console, BorderLayout.CENTER);

		CVSConsole.getCVSConsole().addToConsoleListeners(this);

		showCommandLogs = new JCheckBox(FlexoLocalization.localizedForKey("show_cvs_commands"), true);
		showCommandLogs.setFont(FlexoCst.SMALL_FONT);
		showCommandLogs.setBorder(BorderFactory.createEmptyBorder());
		showErrorsLogs = new JCheckBox(FlexoLocalization.localizedForKey("show_cvs_errors"), true);
		showErrorsLogs.setFont(FlexoCst.SMALL_FONT);
		showErrorsLogs.setBorder(BorderFactory.createEmptyBorder());
		showInfoLogs = new JCheckBox(FlexoLocalization.localizedForKey("show_cvs_logs"), false);
		showInfoLogs.setFont(FlexoCst.SMALL_FONT);
		showInfoLogs.setBorder(BorderFactory.createEmptyBorder());

		clearConsole = new JButton(FlexoLocalization.localizedForKey("clear"), GeneratorIconLibrary.CANCEL_ICON);
		clearConsole.setToolTipText(FlexoLocalization.localizedForKey("clear_console"));
		// clearConsole.setBorder(BorderFactory.createEmptyBorder());
		clearConsole.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				console.clear();
			}
		});

		JPanel header = new JPanel(new BorderLayout());
		header.setBorder(BorderFactory.createEmptyBorder());
		JPanel checkBoxes = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		checkBoxes.setBorder(BorderFactory.createEmptyBorder());
		checkBoxes.add(showCommandLogs);
		checkBoxes.add(showErrorsLogs);
		checkBoxes.add(showInfoLogs);

		header.add(checkBoxes, BorderLayout.WEST);
		header.add(clearConsole, BorderLayout.EAST);

		add(header, BorderLayout.NORTH);
	}

	@Override
	public void errorLog(String logString) {
		if (showErrorsLogs.isSelected())
			console.log(logString, Color.RED);
	}

	@Override
	public void log(String logString) {
		if (showInfoLogs.isSelected())
			console.log(logString, Color.BLUE);
	}

	@Override
	public void commandLog(String logString) {
		if (showCommandLogs.isSelected())
			console.log(logString, Color.BLACK);
	}

}
