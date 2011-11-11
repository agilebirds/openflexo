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
package org.openflexo.br.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.openflexo.swing.JTextFieldRegExp;
import org.openflexo.wysiwyg.FlexoWysiwygUltraLight;

import org.openflexo.br.BugReport;
import org.openflexo.br.BugReportAction;
import org.openflexo.br.BugReport.IncidentType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.viewer.FlexoLoggingViewerWindow;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.UserType;

/**
 * @author sguerin
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BugReportView extends JPanel {

	protected BugReport _bugReport;

	private BugReportActionsPanel actionPanel;

	private JTabbedPane tabbedPane;

	private JTextField identifier;

	JTextFieldRegExp title;

	private JTextField date;

	private JTextField username;

	private JTextField assignedUsername;

	private JTextField flexoVersion;

	private BugReportWysiwyg context;

	private BugReportWysiwyg description;

	private BugReportWysiwyg technicalDescription;

	private JComboBox severity;

	private JComboBox impact;

	private JComboBox urgency;

	private JComboBox module;

	private JComboBox incidentType;

	protected JTextArea stacktrace;

	private JPanel logRecords;

	protected JComboBox status;

	private JCheckBox includeLogs;

	private JButton seeLogRecords;

	private boolean isNew;

	private Font smallFont;

	NewBugReport _newBugReport;

	public BugReportView(BugReport bugReport, boolean isNew, NewBugReport newBugReport) {
		this(isNew, newBugReport);
		setBugReport(bugReport);
	}

	public BugReportView(boolean isNew, NewBugReport newBugReport) {

		super();
		_newBugReport = newBugReport;
		this.isNew = isNew;

		_bugReport = new BugReport(false);

		setLayout(new BorderLayout());
		smallFont = new Font("Courrier", Font.PLAIN, 10);

		add(tabbedPane = new JTabbedPane());
		tabbedPane.add("General", buildGeneralPanel());
		JPanel technicalPanel = buildTechnicalPanel();
		actionPanel = new BugReportActionsPanel();
		if (!isNew || ModuleLoader.getUserType() == UserType.MAINTAINER || ModuleLoader.getUserType() == UserType.DEVELOPER) {
			tabbedPane.add("Technical", technicalPanel);
			tabbedPane.add("Actions", actionPanel);
		}
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		validate();
		setEdited(isNew);
	}

	public JPanel buildGeneralPanel() {
		GridBagLayout gridbag;

		JPanel returned = new JPanel();
		gridbag = new GridBagLayout();
		returned.setLayout(gridbag);

		addField(returned, gridbag, "identifier", identifier = new JTextField(15), true, false);
		identifier.setEnabled(false);
		addField(returned, gridbag, "title", title = new JTextFieldRegExp(), true, false);
		title.addToErrors(".{1,100}", "Max length of title is 100");
		title.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				checkLength();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				checkLength();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				checkLength();
			}

			private void checkLength() {
				if (_newBugReport == null)
					return;
				if (title.getText() == null) {
					_newBugReport.disableButtons();
				} else if (title.getText().length() < 1 || title.getText().length() > 100) {
					_newBugReport.disableButtons();
				} else {
					_newBugReport.enableButtons();
				}
			}

		});
		addField(returned, gridbag, "date", date = new JTextField(), true, false);
		date.setEnabled(false);
		addField(returned, gridbag, "username", username = new JTextField(), true, false);
		username.setEnabled(true);
		addField(returned, gridbag, "flexo_version", flexoVersion = new JTextField(), true, false);
		flexoVersion.setEnabled(false);
		addField(returned, gridbag, "context", context = new BugReportWysiwyg(), true, true);
		context.setPreferredSize(new Dimension(350, 150));
		addField(returned, gridbag, "description", description = new BugReportWysiwyg(), true, true);
		description.setPreferredSize(new Dimension(350, 150));
		addField(returned, gridbag, "severity", severity = new JComboBox(BugReport.getAvailableSeverity()), false, false);
		addField(returned, gridbag, "impact", impact = new JComboBox(BugReport.getAvailableImpact()), false, false);
		addField(returned, gridbag, "urgency", urgency = new JComboBox(BugReport.getAvailableUrgency()), false, false);
		addField(returned, gridbag, "module", module = new JComboBox(ModuleLoader.availableModules()), false, false);
		if (ModuleLoader.isDevelopperRelease() || ModuleLoader.isMaintainerRelease()) {
			addField(returned, gridbag, "type", incidentType = new JComboBox(IncidentType.values()), false, false);
		}
		addField(returned, gridbag, "status", status = new JComboBox(BugReport.getAvailableStatus()), false, false);
		status.setEnabled(false);
		severity.setEnabled(false);

		returned.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		returned.validate();
		returned.setMinimumSize(gridbag.minimumLayoutSize(returned));
		return returned;
	}

	public JPanel buildTechnicalPanel() {
		GridBagLayout gridbag;

		JPanel returned = new JPanel();
		gridbag = new GridBagLayout();
		returned.setLayout(gridbag);

		addField(returned, gridbag, "assigned_to", assignedUsername = new JTextField(), true, false);
		addField(returned, gridbag, "comments", technicalDescription = new BugReportWysiwyg(), true, true);
		addField(returned, gridbag, "stacktrace", new JScrollPane(stacktrace = new JTextArea(10, 30)), true, true);
		stacktrace.setEnabled(true);
		stacktrace.setFont(smallFont);
		stacktrace.setEditable(false);
		stacktrace.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (stacktrace.getSelectedText() == null || stacktrace.getSelectedText().length() == 0)
					stacktrace.selectAll();
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}
		});
		logRecords = new JPanel();
		logRecords.setLayout(new FlowLayout());
		logRecords.add(includeLogs = new JCheckBox(FlexoLocalization.localizedForKey("include_log_records"), true));
		seeLogRecords = new JButton(FlexoLocalization.localizedForKey("open_logs"));
		seeLogRecords.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FlexoLoggingViewerWindow loggingViewerWindow = new FlexoLoggingViewerWindow(_bugReport.logRecords);
				loggingViewerWindow.setModal(true);
				loggingViewerWindow.requestFocus();
				loggingViewerWindow.setVisible(true);

			}
		});
		logRecords.add(seeLogRecords);
		addField(returned, gridbag, "log_records", logRecords, false, false);

		returned.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		returned.validate();
		returned.setMinimumSize(gridbag.minimumLayoutSize(returned));
		return returned;
	}

	protected void addField(JPanel panel, GridBagLayout gridbag, String text, JComponent component, boolean expandX, boolean expandY) {
		JLabel label = new JLabel(FlexoLocalization.localizedForKey(text));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(3, 3, 3, 3);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.anchor = GridBagConstraints.NORTHEAST;
		gridbag.setConstraints(label, c);
		panel.add(label);
		if (expandX) {
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;
			if (expandY) {
				c.weighty = 1.0;
				// c.gridheight = GridBagConstraints.RELATIVE;
			}
		} else {
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.WEST;

		}
		c.weightx = 2.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(component, c);
		panel.add(component);
	}

	public BugReport getBugReport() {
		updateValues();
		return _bugReport;
	}

	public void setBugReport(BugReport bugReport) {
		updateValues();
		_bugReport = bugReport;
		setEdited(isNew);
		identifier.setText(_bugReport.identifier);
		title.setText(_bugReport.title);
		date.setText(_bugReport.dateAsString());
		username.setText(_bugReport.submissionUserName);
		assignedUsername.setText(_bugReport.assignedUserName);
		flexoVersion.setText(_bugReport.flexoVersion);
		context.setContent(_bugReport.context);
		description.setContent(_bugReport.description);
		technicalDescription.setContent(_bugReport.technicalDescription);
		module.setSelectedItem(_bugReport.module);
		stacktrace.setText(_bugReport.stacktrace);
		identifier.setText(_bugReport.identifier);
		status.setSelectedIndex(_bugReport.getStatus());
		severity.setSelectedIndex(_bugReport.severity);
		impact.setSelectedIndex(_bugReport.impact);
		urgency.setSelectedIndex(_bugReport.urgency);
		includeLogs.setText(FlexoLocalization.localizedForKey(isNew ? "include_log_records"
				: (_bugReport.logRecords == null ? "no_log_record" : "log_records_included")));
		if (_bugReport.logRecords == null) {
			seeLogRecords.setEnabled(false);
			includeLogs.setSelected(false);
		} else {
			seeLogRecords.setEnabled(true);
			includeLogs.setSelected(true);
		}

		actionPanel.updateBugReport();
		actionPanel.showLastAction();

		validate();
		repaint();
	}

	public void updateValues() {
		if (_bugReport != null) {
			_bugReport.identifier = identifier.getText();
			_bugReport.title = title.getText();
			_bugReport.context = context.getBodyContent();
			_bugReport.description = description.getBodyContent();
			_bugReport.submissionUserName = username.getText();
			_bugReport.technicalDescription = technicalDescription.getBodyContent();
			_bugReport.assignedUserName = assignedUsername.getText();
			_bugReport.severity = severity.getSelectedIndex();
			_bugReport.impact = impact.getSelectedIndex();
			_bugReport.urgency = urgency.getSelectedIndex();
			_bugReport.module = (Module) module.getSelectedItem();
			if (incidentType != null && incidentType.getSelectedItem() != null)
				_bugReport.type = (IncidentType) incidentType.getSelectedItem();
			if (!includeLogs.isSelected()) {
				_bugReport.logRecords = null;
			}
			actionPanel.updateActionValues();
		}
	}

	public void setEdited(boolean b) {
		title.setEnabled(b);
		context.setEnabled(b);
		description.setEnabled(b);
		technicalDescription.setEnabled(b);
		assignedUsername.setEnabled(b);
		module.setEnabled(b);
		includeLogs.setEnabled(b && (_bugReport != null) && (_bugReport.logRecords != null));
		severity.setEnabled(b);
		impact.setEnabled(b);
		urgency.setEnabled(b);
		actionPanel.setEdited(b);
	}

	public class BugReportActionsPanel extends JPanel {

		private JTable _brTable;

		private JButton deleteButton;

		private JButton newButton;

		private JTextField actionDate;

		private JTextField actionUsername;

		private JTextArea actionDescription;

		private JComboBox actionStatus;

		protected int _selectedActionRow = 0;

		public BugReportActionsPanel() {
			super();
			setLayout(new BorderLayout());
			init();
			validate();
		}

		public void showLastAction() {
			_selectedActionRow = _bugReport.getRowCount() - 1;
			if (_selectedActionRow >= 0) {
				setSelectedBugReportAction(_bugReport.elementAt(_selectedActionRow));
				_brTable.setRowSelectionInterval(_selectedActionRow, _selectedActionRow);
			} else {
				_selectedActionRow = 0;
			}

		}

		public void updateBugReport() {
			if (_bugReport != null) {
				_brTable.setModel(_bugReport);
				if (_bugReport.getRowCount() > _selectedActionRow) {
					setSelectedBugReportAction(_bugReport.elementAt(_selectedActionRow));
				} else {
					_selectedActionRow = 0;
					clearSelectedBugReportAction();
				}
				status.setSelectedIndex(_bugReport.getStatus());
			}
		}

		private void init() {
			_brTable = new JTable(_bugReport);
			_brTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			_brTable.setColumnSelectionAllowed(false);
			_brTable.setRowSelectionAllowed(true);
			ListSelectionModel rowSM = _brTable.getSelectionModel();
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
						_selectedActionRow = selectedRow;
						setSelectedBugReportAction(_bugReport.elementAt(selectedRow));
					}
				}
			});

			for (int i = 0; i < _bugReport.getColumnCount(); i++) {
				TableColumn col = _brTable.getColumnModel().getColumn(i);
				col.setPreferredWidth(getPreferedColumnSize(i));
			}

			JScrollPane scrollpane = new JScrollPane(_brTable);
			add(scrollpane, BorderLayout.NORTH);
			add(buildActionPanel(), BorderLayout.CENTER);
			add(buttonPanel(), BorderLayout.SOUTH);

		}

		public int getPreferedColumnSize(int arg0) {
			switch (arg0) {
			case 0:
				return 50; // user
			case 1:
				return 50; // date
			case 2:
				return 50; // status
			default:
				return 50;
			}
		}

		public JPanel buildActionPanel() {
			GridBagLayout gridbag;

			JPanel returned = new JPanel();
			gridbag = new GridBagLayout();
			returned.setLayout(gridbag);

			addField(returned, gridbag, "date", actionDate = new JTextField(), true, false);
			actionDate.setEnabled(false);
			addField(returned, gridbag, "username", actionUsername = new JTextField(), true, false);
			actionUsername.setEnabled(false);
			addField(returned, gridbag, "description", new JScrollPane(actionDescription = new JTextArea(5, 30)), true, true);
			actionDescription.setLineWrap(true);
			actionDescription.setWrapStyleWord(true);
			actionDescription.setEnabled(false);
			addField(returned, gridbag, "status", actionStatus = new JComboBox(BugReport.getAvailableStatus()), false, false);
			actionStatus.setEnabled(false);
			actionStatus.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateActionValues();
					updateBugReport();
				}

			});

			returned.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			returned.validate();
			returned.setMinimumSize(gridbag.minimumLayoutSize(returned));
			return returned;
		}

		protected JPanel buttonPanel() {
			JPanel answer = new JPanel(new FlowLayout());
			newButton = new JButton(FlexoLocalization.localizedForKey("new"));
			newButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					_bugReport.addAction();
					_selectedActionRow = _bugReport.getRowCount() - 1;
					updateBugReport();
				}
			});
			answer.add(newButton);
			deleteButton = new JButton(FlexoLocalization.localizedForKey("delete"));
			deleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (_action != null) {
						_bugReport.removeAction(_action);
						updateBugReport();
					}
				}
			});
			answer.add(deleteButton);
			newButton.setEnabled(false);
			deleteButton.setEnabled(false);
			return answer;
		}

		public void setEdited(boolean b) {
			actionStatus.setEnabled(b);
			actionDescription.setEnabled(b);
			newButton.setEnabled(b);
			deleteButton.setEnabled(b && (_action != null));
		}

		BugReportAction _action = null;

		protected void setSelectedBugReportAction(BugReportAction action) {
			updateActionValues();
			_action = action;
			actionDate.setText(_action.dateAsString());
			actionUsername.setText(_action.username);
			actionDescription.setText(_action.description);
			actionStatus.setSelectedIndex(_action.status);
			validate();
			repaint();
		}

		protected void clearSelectedBugReportAction() {
			updateActionValues();
			_action = null;
			actionDate.setText("");
			actionUsername.setText("");
			actionDescription.setText("");
			actionStatus.setSelectedIndex(0);
			validate();
			repaint();

		}

		public void updateActionValues() {
			if (_action != null) {
				_action.status = actionStatus.getSelectedIndex();
				_action.description = actionDescription.getText();

			}
		}

	}

	private class BugReportWysiwyg extends FlexoWysiwygUltraLight {

		BugReportWysiwyg() {
			super(true);
			addBorderAroundToolbar();
		}

		@Override
		public void notifyTextChanged() {
		}
	}

}
