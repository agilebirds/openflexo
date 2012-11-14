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
package org.openflexo.components.validation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ProblemIssue;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationFinishedNotification;
import org.openflexo.foundation.validation.ValidationInitNotification;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationNotification;
import org.openflexo.foundation.validation.ValidationProgressNotification;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationReport.ReportMode;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationRuleSet;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;

/**
 * Defines the panel allowing to show and edit a ValidationReport
 * 
 * @author sguerin
 * 
 */
public class ValidationReportEditor extends JPanel implements GraphicalFlexoObserver {

	static final Logger logger = Logger.getLogger(ValidationReportEditor.class.getPackage().getName());

	ValidationReport _validationReport;

	ValidationIssue _currentIssue;

	FixProposal _currentFixProposal;

	JTable _validationReportTable;

	JList fixProposalList;

	private final JLabel _title;

	JLabel _subTitle;

	private final JButton disableRuleButton;

	private final JButton fixButton;

	private final JButton checkButton;

	private final JButton closeButton;

	private final JTextArea _ruleDescription;

	JComboBox reportMode;

	ConsistencyCheckDialogInterface _consistencyCheckDialog;

	private ListSelectionListener issueSelectionListener;

	public ValidationReportEditor(ConsistencyCheckDialogInterface consistencyCheckDialog, ValidationReport validationReport) {
		super();
		setLayout(new BorderLayout());
		_consistencyCheckDialog = consistencyCheckDialog;
		_validationReport = validationReport;
		// _currentIssue = new InformationIssue(null, null);

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		_title = new JLabel(_validationReport.getLocalizedTitle(), SwingConstants.CENTER);
		_title.setFont(FlexoCst.BIG_FONT);
		_title.setForeground(Color.BLACK);
		_subTitle = new JLabel(_validationReport.getLocalizedSubTitle(), SwingConstants.CENTER);
		_subTitle.setFont(FlexoCst.SMALL_FONT);
		_subTitle.setForeground(Color.DARK_GRAY);
		titlePanel.add(_title, BorderLayout.CENTER);
		titlePanel.add(_subTitle, BorderLayout.SOUTH);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		reportMode = new JComboBox(ReportMode.values());
		reportMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_validationReport.setMode((ReportMode) reportMode.getSelectedItem());
			}
		});
		reportMode.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				l.setText(((ReportMode) value).getLocalizedName());
				return l;
			}
		});
		reportMode.setSelectedItem(_validationReport.getMode());
		titlePanel.add(reportMode, BorderLayout.EAST);

		_validationReportTable = new JTable(_validationReport);
		for (int i = 0; i < _validationReport.getColumnCount(); i++) {
			TableColumn col = _validationReportTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(getValidationReportPreferedColumnSize(i));
			if (i == 0) {
				col.setResizable(false);
				col.setMinWidth(getValidationReportPreferedColumnSize(i));
				col.setMaxWidth(getValidationReportPreferedColumnSize(i));
			}
		}

		_validationReportTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (_currentIssue != null && _currentIssue.getSize() > 0) {
						applyFixProposal((FixProposal) _currentIssue.getElementAt(0));
					}
				}
			}
		});

		_validationReportTable.setDefaultRenderer(ValidationIssue.class, new ValidationReportCellRenderer());
		_validationReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_validationReportTable.setMinimumSize(new Dimension(600, 150));
		ListSelectionModel rowSM = _validationReportTable.getSelectionModel();
		rowSM.addListSelectionListener(issueSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting()) {
					return;
				}
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					// no rows are selected
					setCurrentIssue(null);
				} else {
					int selectedRow = lsm.getMinSelectionIndex();
					ValidationIssue issue = _validationReport.getIssueAt(selectedRow);
					setCurrentIssue(issue);
					if (_consistencyCheckDialog.getController() instanceof SelectionManagingController) {
						((SelectionManagingController) _consistencyCheckDialog.getController()).selectAndFocusObject(issue
								.getSelectableObject());
					}
					ValidationReportEditor.this.grabFocus();
				}
			}

		});

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(_validationReportTable.getTableHeader(), BorderLayout.NORTH);
		topPanel.add(new JScrollPane(_validationReportTable), BorderLayout.CENTER);
		topPanel.setPreferredSize(new Dimension(500, 300));
		topPanel.setMinimumSize(_validationReportTable.getMinimumSize());
		fixProposalList = new JList();
		fixProposalList.setCellRenderer(new FixProposalRenderer());
		fixProposalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fixProposalList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting()) {
					return;
				}
				int selectedRow = fixProposalList.getSelectedIndex();
				if (selectedRow >= 0 && selectedRow < _currentIssue.getSize()) {
					FixProposal proposal = (FixProposal) _currentIssue.getElementAt(selectedRow);
					setCurrentFixProposal(proposal);
				} else {
					setCurrentFixProposal(null);
				}
			}

		});
		fixProposalList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					applyFixProposal(_currentFixProposal);
				}
			}
		});

		JScrollPane scrollableFixProposalList = new JScrollPane(fixProposalList);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		_ruleDescription = new JTextArea(4, 20);
		_ruleDescription.setLineWrap(true);
		_ruleDescription.setWrapStyleWord(true);
		_ruleDescription.setForeground(Color.DARK_GRAY);
		_ruleDescription.setFont(FlexoCst.MEDIUM_FONT);

		bottomPanel.add(_ruleDescription, BorderLayout.NORTH);
		bottomPanel.add(scrollableFixProposalList, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
		splitPane.setDividerLocation(0.8);
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		disableRuleButton = new JButton();
		disableRuleButton.setText(FlexoLocalization.localizedForKey("disableRule", disableRuleButton));
		fixButton = new JButton();
		fixButton.setText(FlexoLocalization.localizedForKey("fix", fixButton));
		checkButton = new JButton();
		checkButton.setText(FlexoLocalization.localizedForKey("check_again", checkButton));
		checkButton.setEnabled(_validationReport.getRootObject() != null);
		closeButton = new JButton();
		closeButton.setText(FlexoLocalization.localizedForKey(getCloseButtonName(), closeButton));

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		checkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkAgain();
			}
		});
		fixButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyFixProposal(_currentFixProposal);
			}
		});
		disableRuleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disableRule(_currentIssue);
			}
		});
		fixButton.setEnabled(false);
		disableRuleButton.setEnabled(false);
		// if(ModuleLoader.isDevelopperRelease() || ModuleLoader.isMaintainerRelease())
		controlPanel.add(disableRuleButton);
		controlPanel.add(fixButton);
		controlPanel.add(checkButton);
		controlPanel.add(closeButton);

		add(titlePanel, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		validate();

	}

	public String getCloseButtonName() {
		return "close";
	}

	public void close() {
		_consistencyCheckDialog.hide();
	}

	public void checkAgain() {
		if (_validationReport.getRootObject() != null) {
			consistencyCheckWithValidationModel(_validationReport.getRootObject(), _validationReport.getValidationModel());
			// if (_validationReport.getRowCount() > 0) {
			// setCurrentIssue(_validationReport.getIssueAt(_validationReport.getRowCount() - 1));
			// }
		}
	}

	protected void disableRule(ValidationIssue issue) {

		issue.getCause().setIsEnabled(!issue.getCause().getIsEnabled());
		GeneralPreferences.setValidationRuleEnabled(issue.getCause(), issue.getCause().getIsEnabled());
		GeneralPreferences.save();
		disableRuleButton.setText(issue.getCause().getIsEnabled() ? FlexoLocalization.localizedForKey("disableRule", disableRuleButton)
				: FlexoLocalization.localizedForKey("enableRule", disableRuleButton));
	}

	protected void applyFixProposal(FixProposal fixProposal) {
		if (fixProposal != null) {
			if (fixProposal instanceof ParameteredFixProposal) {
				/*AskParametersDialog askParams = new AskParametersDialog(FlexoLocalization.localizedForKey("validation_error_fixing"),
				        fixProposal.getLocalizedMessage(), ((ParameteredFixProposal) fixProposal).getLabels(),
				        ((ParameteredFixProposal) fixProposal).getParams());*/
				((ParameteredFixProposal) fixProposal).updateBeforeApply();
				AskParametersDialog askParams = AskParametersDialog.createAskParametersDialog(fixProposal.getProject(), null,
						FlexoLocalization.localizedForKey("validation_error_fixing"), fixProposal.getLocalizedMessage(),
						((ParameteredFixProposal) fixProposal).getParameters());
				if (askParams.getStatus() == AskParametersDialog.CANCEL) {
					return;
				}
			} else if (fixProposal.askConfirmation()) {
				if (!FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_object"))) {
					return;
				}
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Applying " + fixProposal.getMessage());
			}
			fixProposal.apply();
			if (_validationReport.getRowCount() > 0) {
				setCurrentIssue(_validationReport.getIssueAt(_validationReport.getRowCount() - 1));
			} else {
				setCurrentIssue(null);
			}
			_subTitle.setText(_validationReport.getLocalizedSubTitle());
		}
	}

	void setCurrentIssue(ValidationIssue issue) {
		_currentIssue = issue;
		fixProposalList.setModel(_currentIssue == null ? new DefaultListModel() : _currentIssue);
		if (issue instanceof ProblemIssue) {
			_ruleDescription.setText(((ProblemIssue) issue).getValidationRule().getLocalizedDescription());
		} else {
			_ruleDescription.setText("");
		}
		fixButton.setEnabled(false);
		_currentFixProposal = null;
		if (_currentIssue != null) {
			if (_currentIssue.getSize() > 0) {
				fixProposalList.setSelectedIndex(0);
			}
			disableRuleButton.setEnabled(_currentIssue.getCause() != null);
			if (_currentIssue.getCause() != null) {
				if (_currentIssue.getCause().getIsEnabled()) {
					disableRuleButton.setText(FlexoLocalization.localizedForKey("disableRule", disableRuleButton));
				} else {
					disableRuleButton.setText(FlexoLocalization.localizedForKey("enableRule", disableRuleButton));
				}
			}
		}
	}

	void setCurrentFixProposal(FixProposal proposal) {
		_currentFixProposal = proposal;
		if (proposal != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("setCurrentFixProposal " + _currentFixProposal.getMessage());
			}
			fixButton.setEnabled(true);
		} else {
			fixButton.setEnabled(false);
		}
	}

	private int getValidationReportPreferedColumnSize(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return 20; // icon
		case 1:
			return 330; // message
		case 2:
			return 150; // object
		default:
			return 50;
		}
	}

	public void setValidationReport(ValidationReport validationReport) {
		if (_validationReport.hasCustomLocalizedTitle()) {
			validationReport.setLocalizedTitle(_validationReport.getLocalizedTitle());
		}
		_validationReport = validationReport;
		_validationReport.setMode((ReportMode) reportMode.getSelectedItem());
		_validationReportTable.getSelectionModel().removeListSelectionListener(issueSelectionListener);
		_validationReportTable.setModel(_validationReport);
		_validationReportTable.getSelectionModel().addListSelectionListener(issueSelectionListener);
		for (int i = 0; i < _validationReport.getColumnCount(); i++) {
			TableColumn col = _validationReportTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(getValidationReportPreferedColumnSize(i));
			if (i == 0) {
				col.setResizable(false);
				col.setMinWidth(getValidationReportPreferedColumnSize(i));
				col.setMaxWidth(getValidationReportPreferedColumnSize(i));
			}
		}
		_title.setText(_validationReport.getLocalizedTitle());
		_subTitle.setText(_validationReport.getLocalizedSubTitle());
		fixButton.setEnabled(false);
		checkButton.setEnabled(_validationReport.getRootObject() != null);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_consistencyCheckDialog.toFront();
			}
		});
	}

	public ValidationReport getValidationReport() {
		return _validationReport;
	}

	public void consistencyCheckWithDefaultValidationModel(Validable objectToValidate) {
		consistencyCheckWithValidationModel(objectToValidate, objectToValidate.getDefaultValidationModel());
	}

	public void consistencyCheckWithValidationModel(Validable objectToValidate, ValidationModel validationModel) {
		for (int i = 0; i < validationModel.getSize(); i++) {
			ValidationRuleSet ruleSet = (ValidationRuleSet) validationModel.getElementAt(i);
			for (ValidationRule rule : ruleSet.getRules()) {
				rule.setIsEnabled(GeneralPreferences.isValidationRuleEnabled(rule));
			}
		}
		validationModel.addObserver(this);
		ValidationReport report = objectToValidate.validate(validationModel);
		if (report == null) {
			report = new ValidationReport(validationModel, objectToValidate);
		}
		setValidationReport(report);
		validationModel.deleteObserver(this);
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ValidationNotification) {
			if (dataModification instanceof ValidationInitNotification) {
				ValidationInitNotification initNotification = (ValidationInitNotification) dataModification;
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("validating") + " "
						+ initNotification.getRootObject().getFullyQualifiedName(), initNotification.getNbOfObjectToValidate());
			} else if (dataModification instanceof ValidationProgressNotification) {
				ValidationProgressNotification progressNotification = (ValidationProgressNotification) dataModification;
				ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("validating") + " "
						+ progressNotification.getValidatedObject().getFullyQualifiedName());
			} else if (dataModification instanceof ValidationFinishedNotification) {
				ProgressWindow.hideProgressWindow();
			}

		}
	}

	protected class ValidationReportCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 0) {
				if (returned instanceof JLabel) {
					JLabel label = (JLabel) returned;
					label.setText("");
					label.setIcon(IconLibrary.getIconForValidationIssue((ValidationIssue) value));
				}
			}
			return returned;
		}
	}

	protected class FixProposalRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (returned instanceof JLabel) {
				JLabel label = (JLabel) returned;
				label.setText(((FixProposal) value).getLocalizedMessage());
				label.setIcon(IconLibrary.FIX_PROPOSAL_ICON);
			}
			return returned;
		}
	}

}
