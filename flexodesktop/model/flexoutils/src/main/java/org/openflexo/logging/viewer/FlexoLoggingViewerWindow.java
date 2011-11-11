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
package org.openflexo.logging.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.logging.LogRecord;
import org.openflexo.logging.LogRecords;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;

/**
 * Class used to graphically represent logs of current or past session of Flexo
 * 
 * @author sguerin
 */
public class FlexoLoggingViewerWindow extends JDialog {

	public static Color BACK_COLOR;

	static final Logger logger = Logger.getLogger(FlexoLoggingViewerWindow.class.getPackage().getName());

	private LogRecords _applicationLogs = null;

	LogRecords _logs = null;

	private Vector<File> _availableLogsFiles = new Vector<File>();

	JComboBox comboBox;

	private JTable loggingTable;

	public FlexoLoggingViewerWindow(File logFile) {
		super((Frame) null, FlexoLocalization.localizedForKey("logging_viewer"));
		init();
		switchToFile(logFile);
	}

	public FlexoLoggingViewerWindow() {
		super((Frame) null, FlexoLocalization.localizedForKey("logging_viewer"));
		init();
	}

	public FlexoLoggingViewerWindow(LogRecords applicationLogs) {
		super((Frame) null, FlexoLocalization.localizedForKey("logging_viewer"));
		_applicationLogs = applicationLogs;
		init();
		switchToApplicationLogging();
	}

	protected LogRecords logsForFile(File aFile) {
		if (!_availableLogsFiles.contains(aFile)) {
			_availableLogsFiles.add(aFile);
			addToAvailableChoices(aFile);
		}
		if (!aFile.exists()) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("File " + aFile.getName() + " doesn't exist. Maybe you have to check your paths ?");
			return null;
		} else {

			XMLCoder.setTransformerFactoryClass("org.apache.xalan.processor.TransformerFactoryImpl");

			StringEncoder.getDefaultInstance()._setDateFormat("yyyy-MM-dd'T'HH:mm:ss");

			try {
				return (LogRecords) XMLDecoder.decodeObjectWithMapping(new FileInputStream(aFile), FlexoLoggingManager.getLoggingMapping());
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning(e.toString());
			}
		}
		return null;
	}

	void switchToApplicationLogging() {
		switchTo(_applicationLogs);
	}

	void switchToFile(File aFile) {
		LogRecords newLogs = logsForFile(aFile);
		if (newLogs != null) {
			switchTo(newLogs);
		}
	}

	private void addToAvailableChoices(File aFile) {
		File newItem = new File(aFile.getAbsolutePath());
		comboBox.insertItemAt(newItem, comboBox.getItemCount());
		comboBox.setSelectedItem(newItem);
	}

	private void init() {
		loggingTable = new JTable();
		loggingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = loggingTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting())
					return;
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					// no rows are selected
				} else {
					int selectedRow = lsm.getMinSelectionIndex();
					LogRecord record = _logs.elementAt(selectedRow);
					// if (record.stackTrace != null) {
					System.err.println("Stack trace for '" + record.message + "':");
					System.err.println(record.getStackTraceAsString());
					/*
					 * int beginAt; if (record.isUnhandledException) { beginAt =
					 * 0; } else { beginAt = 6; } for (int i=beginAt; i<record.stackTrace.length;
					 * i++) { System.err.println("\tat " +
					 * record.stackTrace[i]); }
					 */
					/*
					 * } else { System.err.println ("Stack trace not
					 * available"); }
					 */
				}
			}
		});

		JScrollPane scrollpane = new JScrollPane(loggingTable);
		JPanel viewerPanel = new JPanel(new BorderLayout());
		viewerPanel.setBackground(BACK_COLOR);
		viewerPanel.add(controlPanel(), BorderLayout.NORTH);
		viewerPanel.add(scrollpane, BorderLayout.CENTER);
		viewerPanel.add(buttonPanel(), BorderLayout.SOUTH);

		getContentPane().add(viewerPanel);
		setSize(1000, 600);
		setLocation(100, 100);
	}

	private void switchTo(LogRecords logs) {
		_logs = logs;
		loggingTable.setModel(logs);
		for (int i = 0; i < logs.getColumnCount(); i++) {
			TableColumn col = loggingTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(getPreferedColumnSize(i));
		}
	}

	public int getPreferedColumnSize(int arg0) {
		switch (arg0) {
		case 0:
			return 50; // Level
		case 1:
			return 300; // Message
		case 2:
			return 120; // Package
		case 3:
			return 100; // Class
		case 4:
			return 120; // Method
		case 5:
			return 20; // Sequence
		case 6:
			return 100; // Date
		case 7:
			return 50; // Millis
		case 8:
			return 30; // Thread
		default:
			return 50;
		}
	}

	protected JPanel buttonPanel() {
		JPanel answer = new JPanel(new FlowLayout());
		answer.setBackground(BACK_COLOR);
		JButton closeButton = new JButton(FlexoLocalization.localizedForKey("close"));
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				if (!FlexoLoggingManager.isInitialized()) {
					System.exit(0);
				}
			}
		});
		closeButton.setOpaque(false);
		answer.add(closeButton);
		JButton reloadConfButton;
		if (FlexoLoggingManager.isInitialized()) {
			reloadConfButton = new JButton(FlexoLocalization.localizedForKey("reload_configuration_file"));
			reloadConfButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						LogManager.getLogManager().readConfiguration();
					} catch (IOException excep) {
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Could not read logging configuration");
					}
				}
			});
			reloadConfButton.setOpaque(false);
			answer.add(reloadConfButton);
		}
		return answer;
	}

	protected JPanel controlPanel() {
		String[] availableChoices;

		if (FlexoLoggingManager.isInitialized()) {
			availableChoices = new String[2];
			availableChoices[0] = FlexoLocalization.localizedForKey("application");
			availableChoices[1] = FlexoLocalization.localizedForKey("open_log_file");
		} else {
			availableChoices = new String[1];
			availableChoices[0] = FlexoLocalization.localizedForKey("open_log_file");
		}
		JPanel answer = new JPanel(new FlowLayout());
		answer.setBackground(BACK_COLOR);

		JLabel titleLabel = new JLabel(FlexoLocalization.localizedForKey("showing_logging_informations_for"));
		comboBox = new JComboBox(availableChoices);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = comboBox.getSelectedIndex();
				if ((selectedIndex == 0) && (FlexoLoggingManager.isInitialized())) {
					switchToApplicationLogging();
				} else if (((selectedIndex == 1) && (FlexoLoggingManager.isInitialized()))
						|| ((selectedIndex == 0) && (!FlexoLoggingManager.isInitialized()))) {
					JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setDialogTitle(FlexoLocalization.localizedForKey("select_a_log_file"));
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						// a file has been picked...
						try {
							File newLogFile = chooser.getSelectedFile();
							switchToFile(newLogFile);
						} catch (Exception exception) {
							if (logger.isLoggable(Level.WARNING))
								logger.warning(exception.getClass().getName());
							exception.printStackTrace();
						}
					}
				} else {
					Object selectedObject = comboBox.getSelectedItem();
					if (selectedObject instanceof File) {
						switchToFile((File) selectedObject);
					}
				}
			}
		});
		answer.add(titleLabel);
		answer.add(comboBox);
		answer.setSize(800, 100);
		return answer;

	}
}
