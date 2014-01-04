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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.ApplicationContext;
import org.openflexo.FlexoCst;
import org.openflexo.FlexoMainLocalizer;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationRuleSet;
import org.openflexo.localization.FlexoLocalization;

/**
 * Defines the panel allowing to show and edit a ValidationModel
 * 
 * @author sguerin
 * 
 */
public class ValidationModelViewer extends JPanel implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(ValidationModelViewer.class.getPackage().getName());

	ValidationModel _validationModel;

	ValidationRuleSet _currentRuleSet;

	ValidationRule _currentRule;

	JList _validationModelList;

	JList _ruleSetList;

	private JLabel _title;

	JButton closeButton;

	JButton disableButton;

	JButton editButton;

	JButton saveButton;

	JTextField _ruleName;

	private JTextField _ruleType;

	JTextArea _ruleDescription;

	DocumentListener _ruleNameDocumentListener;

	DocumentListener _ruleDescriptionDocumentListener;

	ConsistencyCheckDialogInterface _consistencyCheckDialog;

	private ApplicationContext applicationContext;

	public ValidationModelViewer(ConsistencyCheckDialogInterface consistencyCheckDialog, ValidationModel validationModel,
			ApplicationContext applicationContext) {
		super();
		setLayout(new BorderLayout());
		this.applicationContext = applicationContext;
		_consistencyCheckDialog = consistencyCheckDialog;
		_validationModel = validationModel;
		_currentRuleSet = new ValidationRuleSet(null, new Vector());

		_title = new JLabel(FlexoLocalization.localizedForKey("validation_model"), SwingConstants.CENTER);
		_title.setFont(FlexoCst.BIG_FONT);
		_title.setForeground(Color.BLACK);
		_title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		_validationModelList = new JList(_validationModel);
		_validationModelList.setCellRenderer(new ValidationModelCellRenderer());
		_validationModelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_validationModelList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting()) {
					return;
				}
				int selectedRow = _validationModelList.getSelectedIndex();
				if (selectedRow >= 0 && _validationModel != null && selectedRow < _validationModel.getSize()) {
					ValidationRuleSet ruleSet = _validationModel.getElementAt(selectedRow);
					setCurrentRuleSet(ruleSet);
				} else {
					setCurrentRuleSet(null);
				}
			}
		});
		JScrollPane leftPanel = new JScrollPane(_validationModelList);

		_ruleSetList = new JList();
		_ruleSetList.setCellRenderer(new RuleSetCellRenderer());
		_ruleSetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_ruleSetList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting()) {
					return;
				}
				int selectedRow = _ruleSetList.getSelectedIndex();
				if (selectedRow >= 0 && _currentRuleSet != null && selectedRow < _currentRuleSet.getSize()) {
					ValidationRule rule = _currentRuleSet.getElementAt(selectedRow);
					setCurrentRule(rule);
				} else {
					setCurrentRule(null);
				}
			}
		});
		JScrollPane rightPanel = new JScrollPane(_ruleSetList);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);

		JPanel rulePanel = new JPanel(new BorderLayout());
		JPanel namePanel = new JPanel(new BorderLayout());
		JPanel typePanel = new JPanel(new BorderLayout());
		JLabel ruleNameLabel = new JLabel();
		ruleNameLabel.setText(FlexoLocalization.localizedForKey("validation_rule_name", ruleNameLabel));
		_ruleName = new JTextField();
		_ruleName.setEditable(false);
		JLabel ruleTypeLabel = new JLabel();
		ruleTypeLabel.setText(FlexoLocalization.localizedForKey("defined_in", ruleTypeLabel));
		_ruleType = new JTextField();
		_ruleType.setEditable(false);
		_ruleDescription = new JTextArea(3, 20);
		_ruleDescription.setLineWrap(true);
		_ruleDescription.setWrapStyleWord(true);
		_ruleDescription.setEditable(false);
		_ruleDescription.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

		_ruleNameDocumentListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent event) {
				updateValue();
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				updateValue();
			}

			@Override
			public void removeUpdate(DocumentEvent event) {
				updateValue();
			}

			private void updateValue() {
				if (_currentRule != null) {
					FlexoLocalization.setLocalizedForKey(_currentRule.getNameKey(), _ruleName.getText());
				}
			}
		};
		_ruleDescriptionDocumentListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent event) {
				updateValue();
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				updateValue();
			}

			@Override
			public void removeUpdate(DocumentEvent event) {
				updateValue();
			}

			private void updateValue() {
				if (_currentRule != null) {
					FlexoLocalization.setLocalizedForKey(_currentRule.getDescriptionKey(), _ruleDescription.getText());
				}
			}
		};

		namePanel.add(ruleNameLabel, BorderLayout.WEST);
		namePanel.add(_ruleName, BorderLayout.CENTER);
		disableButton = new JButton();
		namePanel.add(disableButton, BorderLayout.EAST);
		namePanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));

		typePanel.add(ruleTypeLabel, BorderLayout.WEST);
		typePanel.add(_ruleType, BorderLayout.CENTER);
		typePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));

		JPanel topRuleInfoPanel = new JPanel(new BorderLayout());
		topRuleInfoPanel.add(namePanel, BorderLayout.NORTH);
		topRuleInfoPanel.add(typePanel, BorderLayout.SOUTH);
		topRuleInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		rulePanel.add(topRuleInfoPanel, BorderLayout.NORTH);
		rulePanel.add(_ruleDescription, BorderLayout.CENTER);

		JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane, rulePanel);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		closeButton = new JButton();
		closeButton.setText(FlexoLocalization.localizedForKey("close", closeButton));
		editButton = new JButton();
		editButton.setText(FlexoLocalization.localizedForKey("edit", editButton));
		editButton.setEnabled(true);
		saveButton = new JButton();
		saveButton.setText(FlexoLocalization.localizedForKey("save", saveButton));
		saveButton.setEnabled(false);

		disableButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_currentRule != null) {
					_currentRule.setIsEnabled(!_currentRule.getIsEnabled());
					ValidationModelViewer.this.applicationContext.getGeneralPreferences().setValidationRuleEnabled(_currentRule,
							_currentRule.getIsEnabled());
					// GeneralPreferences.save();
					disableButton.setText(_currentRule.getIsEnabled() ? FlexoLocalization.localizedForKey("disableRule", disableButton)
							: FlexoLocalization.localizedForKey("enableRule", disableButton));
					_ruleSetList.validate();
					_ruleSetList.repaint();
				}
			}
		});
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_consistencyCheckDialog.hide();
			}
		});
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editButton.setEnabled(false);
				saveButton.setEnabled(true);
				_ruleName.setEditable(true);
				_ruleDescription.setEditable(true);
				_ruleName.getDocument().addDocumentListener(_ruleNameDocumentListener);
				_ruleDescription.getDocument().addDocumentListener(_ruleDescriptionDocumentListener);
			}
		});
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editButton.setEnabled(true);
				saveButton.setEnabled(false);
				_ruleName.setEditable(false);
				_ruleDescription.setEditable(false);
				_ruleName.getDocument().removeDocumentListener(_ruleNameDocumentListener);
				_ruleDescription.getDocument().removeDocumentListener(_ruleDescriptionDocumentListener);
				((FlexoMainLocalizer) FlexoLocalization.getMainLocalizer()).saveAllDictionaries();
			}
		});
		controlPanel.add(closeButton);
		controlPanel.add(editButton);
		controlPanel.add(saveButton);

		add(_title, BorderLayout.NORTH);
		add(splitPane2, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		validate();
		splitPane.setResizeWeight(0.4);
		splitPane.setDividerLocation(0.3);
		if (_validationModel.getSize() > 0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (_validationModel.getSize() > 0) {
						_validationModelList.setSelectedIndex(0);
						if (_validationModel.getElementAt(0).getRules().size() > 0) {
							disableButton
									.setText(_validationModel.getElementAt(0).getRules().firstElement().getIsEnabled() ? FlexoLocalization
											.localizedForKey("disableRule", disableButton) : FlexoLocalization.localizedForKey(
											"enableRule", disableButton));
						}
					}
					_consistencyCheckDialog.toFront();
				}
			});
		}
	}

	void setCurrentRuleSet(ValidationRuleSet ruleSet) {
		_currentRuleSet = ruleSet;
		if (_currentRuleSet != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Sets RuleSet to " + ruleSet.getTypeName());
			}
			_ruleSetList.setModel(_currentRuleSet);
			if (_currentRuleSet.getSize() > 0) {
				_ruleSetList.setSelectedIndex(0);
			}
		}
	}

	void setCurrentRule(ValidationRule rule) {
		_currentRule = rule;
		if (rule != null) {
			_ruleName.setText(rule.getLocalizedName());
			_ruleDescription.setText(rule.getLocalizedDescription());
			_ruleType.setText(rule.getTypeName());
			if (rule.getIsEnabled()) {
				disableButton.setText(FlexoLocalization.localizedForKey("disableRule", disableButton));
			} else {
				disableButton.setText(FlexoLocalization.localizedForKey("enableRule", disableButton));
			}
			disableButton.setEnabled(true);
		} else {
			_ruleName.setText("");
			_ruleDescription.setText("");
			_ruleType.setText("");
			disableButton.setEnabled(false);
		}
	}

	public void setValidationModel(ValidationModel validationModel) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setValidationModel() with " + validationModel.getSize());
		}

		_validationModel = validationModel;
		_validationModelList.setModel(validationModel);
		if (_validationModel.getSize() > 0) {
			_validationModelList.setSelectedIndex(0);
		}
		_validationModelList.revalidate();
		_validationModelList.repaint();
	}

	public ValidationModel getValidationModel() {
		return _validationModel;
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
	}

	protected class ValidationModelCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (returned instanceof JLabel) {
				JLabel label = (JLabel) returned;
				label.setText(((ValidationRuleSet) value).getTypeName());
			}
			return returned;
		}
	}

	protected class RuleSetCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (returned instanceof JLabel) {
				JLabel label = (JLabel) returned;
				label.setText(((ValidationRule) value).getLocalizedName());
				if (!((ValidationRule) value).getIsEnabled()) {
					returned.setForeground(Color.GRAY);
				}
			}
			return returned;
		}
	}

}
