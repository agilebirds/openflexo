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
package org.openflexo.fib.utils;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Type;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.BooleanStaticBinding;
import org.openflexo.antar.binding.FloatStaticBinding;
import org.openflexo.antar.binding.IntegerStaticBinding;
import org.openflexo.antar.binding.NullStaticBinding;
import org.openflexo.antar.binding.StaticBinding;
import org.openflexo.antar.binding.StringStaticBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.DateSelector;
import org.openflexo.toolbox.ToolBox;

class StaticBindingPanel extends JPanel {

	public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 9);

	/**
	 * 
	 */
	private final BindingSelectorPanel _bindingSelectorPanel;
	boolean isUpdatingPanel = false;
	protected JCheckBox selectStaticBindingCB = null;
	protected JComboBox selectValueCB = null;
	protected JTextField enterValueTF = null;
	protected DateSelector dateSelector = null;
	protected DurationSelector durationSelector = null;
	protected JSpinner integerValueChooser = null;
	protected JComboBox typeCB = null;

	private EvaluationType currentType;

	protected StaticBindingPanel(BindingSelectorPanel bindingSelectorPanel) {
		_bindingSelectorPanel = bindingSelectorPanel;
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		initStaticBindingPanel();
		updateStaticBindingPanel();
	}

	private void initStaticBindingPanel() {
		selectStaticBindingCB = null;
		selectValueCB = null;
		enterValueTF = null;
		dateSelector = null;
		durationSelector = null;
		integerValueChooser = null;
		selectStaticBindingCB = new JCheckBox(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "define_a_static_value"));
		selectStaticBindingCB.setFont(SMALL_FONT);
		selectStaticBindingCB.setSelected(false);
		selectStaticBindingCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StaticBindingPanel.this._bindingSelectorPanel.setEditStaticValue(selectStaticBindingCB.isSelected());
			}
		});
		add(selectStaticBindingCB);
		if (_bindingSelectorPanel._bindingSelector.getBindingDefinition() == null
				|| _bindingSelectorPanel._bindingSelector.getBindingDefinition().getType() == null) {
			enterValueTF = new JTextField(10);
			enterValueTF.setFont(SMALL_FONT);
			add(enterValueTF);
			currentType = EvaluationType.LITERAL;
			disableStaticBindingPanel();
		} else {
			currentType = kindOf(_bindingSelectorPanel._bindingSelector.getBindingDefinition().getType());
			if (currentType == EvaluationType.BOOLEAN) {
				final String UNSELECTED = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "select_a_value");
				final String TRUE = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "true");
				final String FALSE = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "false");
				String[] availableValues = { UNSELECTED, TRUE, FALSE };
				selectValueCB = new JComboBox(availableValues);
				selectValueCB.setFont(SMALL_FONT);
				selectValueCB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) {
							return;
						}
						if (selectValueCB.getSelectedItem().equals(TRUE)) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(new BooleanStaticBinding(
									_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
											.getBindable(), true));
						} else if (selectValueCB.getSelectedItem().equals(FALSE)) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(new BooleanStaticBinding(
									_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
											.getBindable(), false));
						}
					}
				});
				add(selectValueCB);
			} else if (currentType == EvaluationType.ARITHMETIC_INTEGER) {
				SpinnerNumberModel valueModel = new SpinnerNumberModel(1, Short.MIN_VALUE, Short.MAX_VALUE, 1);
				integerValueChooser = new JSpinner(valueModel);
				integerValueChooser.setEditor(new JSpinner.NumberEditor(integerValueChooser, "#"));
				integerValueChooser.setMinimumSize(integerValueChooser.getPreferredSize());
				integerValueChooser.setValue(1);
				integerValueChooser.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (isUpdatingPanel) {
							return;
						}
						Object v = integerValueChooser.getValue();
						if (v instanceof Integer) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(
									_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
											.getBindable(), (Integer) v));
						} else if (v instanceof Long) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(
									_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
											.getBindable(), (Long) v));
						} else if (v instanceof Short) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(
									_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
											.getBindable(), (Short) v));
						} else if (v instanceof Byte) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(
									_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
											.getBindable(), (Byte) v));
						} else if (v instanceof Character) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(
									_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
											.getBindable(), (Character) v));
						}
					}
				});
				integerValueChooser.setFont(SMALL_FONT);
				integerValueChooser.getEditor().setFont(SMALL_FONT);
				add(integerValueChooser);
			} else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
				enterValueTF = new JTextField(10);
				enterValueTF.setFont(SMALL_FONT);
				enterValueTF.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) {
							return;
						}
						if (_bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(_bindingSelectorPanel._bindingSelector
									.makeStaticBindingFromString(enterValueTF.getText()));
						}
					}
				});
				enterValueTF.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!StaticBindingPanel.this._bindingSelectorPanel._connectButton.isEnabled()
								&& _bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
							StaticBindingPanel.this._bindingSelectorPanel._connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
								StaticBindingPanel.this._bindingSelectorPanel._connectButton.setSelected(true);
							}
						}
					}
				});
				add(enterValueTF);
			} else if (currentType == EvaluationType.STRING) {
				enterValueTF = new JTextField(10);
				enterValueTF.setFont(SMALL_FONT);
				enterValueTF.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) {
							return;
						}
						if (_bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue('"' + enterValueTF.getText() + '"')) {
							_bindingSelectorPanel._bindingSelector.setEditedObject(_bindingSelectorPanel._bindingSelector
									.makeStaticBindingFromString('"' + enterValueTF.getText() + '"'));
						}
					}
				});
				enterValueTF.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!StaticBindingPanel.this._bindingSelectorPanel._connectButton.isEnabled()
								&& enterValueTF.getText().length() > 0) {
							StaticBindingPanel.this._bindingSelectorPanel._connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
								StaticBindingPanel.this._bindingSelectorPanel._connectButton.setSelected(true);
							}
						}
					}
				});
				add(enterValueTF);
			}

			if (_bindingSelectorPanel.getEditStaticValue()) {
				enableStaticBindingPanel();
				for (int i = 0; i < _bindingSelectorPanel.getVisibleColsCount(); i++) {
					_bindingSelectorPanel.listAtIndex(i).setEnabled(false);
				}
			} else {
				disableStaticBindingPanel();
			}

		}

		if (_bindingSelectorPanel._bindingSelector.getBindingDefinition() == null
				|| _bindingSelectorPanel._bindingSelector.getBindingDefinition().getType() == null
				|| TypeUtils.isObject(_bindingSelectorPanel._bindingSelector.getBindingDefinition().getType())) {
			final String SELECT = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "select");
			final String BOOLEAN = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "boolean");
			final String INTEGER = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "integer");
			final String FLOAT = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "float");
			final String STRING = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "string");
			final String DATE = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "date");
			final String DURATION = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "duration");
			final String DKV = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "enum");
			final String NULL = FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "null");
			String[] availableValues = { SELECT, BOOLEAN, INTEGER, FLOAT, STRING, DATE, DURATION, DKV, NULL };
			typeCB = new JComboBox(availableValues);
			typeCB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (isUpdatingPanel) {
						return;
					}
					if (typeCB.getSelectedItem().equals(BOOLEAN)) {
						_bindingSelectorPanel._bindingSelector.setEditedObject(new BooleanStaticBinding(
								_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
										.getBindable(), true));
					} else if (typeCB.getSelectedItem().equals(INTEGER)) {
						_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(
								_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
										.getBindable(), 0));
					} else if (typeCB.getSelectedItem().equals(FLOAT)) {
						_bindingSelectorPanel._bindingSelector.setEditedObject(new FloatStaticBinding(
								_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
										.getBindable(), 0));
					} else if (typeCB.getSelectedItem().equals(STRING)) {
						_bindingSelectorPanel._bindingSelector.setEditedObject(new StringStaticBinding(
								_bindingSelectorPanel._bindingSelector.getBindingDefinition(), _bindingSelectorPanel._bindingSelector
										.getBindable(), ""));
					} else if (typeCB.getSelectedItem().equals(NULL)) {
						_bindingSelectorPanel._bindingSelector.setEditedObject(new NullStaticBinding(_bindingSelectorPanel._bindingSelector
								.getBindingDefinition(), _bindingSelectorPanel._bindingSelector.getBindable()));
					}
				}
			});
			if (typeCB != null) {
				isUpdatingPanel = true;
				if (currentType == EvaluationType.BOOLEAN) {
					typeCB.setSelectedItem(BOOLEAN);
				} else if (currentType == EvaluationType.ARITHMETIC_INTEGER) {
					typeCB.setSelectedItem(INTEGER);
				} else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
					typeCB.setSelectedItem(FLOAT);
				} else if (currentType == EvaluationType.STRING) {
					typeCB.setSelectedItem(STRING);
				} else if (currentType == EvaluationType.DATE) {
					typeCB.setSelectedItem(DATE);
				} else if (currentType == EvaluationType.DURATION) {
					typeCB.setSelectedItem(DURATION);
				} else if (currentType == EvaluationType.ENUM) {
					typeCB.setSelectedItem(DKV);
				}
				isUpdatingPanel = false;
			}

			if (_bindingSelectorPanel._bindingSelector.getEditedObject() instanceof NullStaticBinding) {
				isUpdatingPanel = true;
				typeCB.setSelectedItem(NULL);
				isUpdatingPanel = false;
			}

			typeCB.setFont(SMALL_FONT);
			add(typeCB);
		}

	}

	void willApply() {
		if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
			if (_bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
				_bindingSelectorPanel._bindingSelector.setEditedObject(_bindingSelectorPanel._bindingSelector
						.makeStaticBindingFromString(enterValueTF.getText()));
			}
		} else if (currentType == EvaluationType.STRING) {
			if (_bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue('"' + enterValueTF.getText() + '"')) {
				_bindingSelectorPanel._bindingSelector.setEditedObject(_bindingSelectorPanel._bindingSelector
						.makeStaticBindingFromString('"' + enterValueTF.getText() + '"'));
			}
		}
	}

	private EvaluationType kindOf(Type type) {
		if (TypeUtils.isObject(type) && _bindingSelectorPanel._bindingSelector.getEditedObject() instanceof StaticBinding) {
			return ((StaticBinding) _bindingSelectorPanel._bindingSelector.getEditedObject()).getEvaluationType();
		} else {
			return TypeUtils.kindOfType(type);
		}
	}

	void updateStaticBindingPanel() {
		isUpdatingPanel = true;

		EvaluationType newType;
		if (_bindingSelectorPanel._bindingSelector.getBindingDefinition() == null
				|| _bindingSelectorPanel._bindingSelector.getBindingDefinition().getType() == null) {
			newType = EvaluationType.LITERAL;
		} else {
			newType = kindOf(_bindingSelectorPanel._bindingSelector.getBindingDefinition().getType());
		}
		if (newType != currentType) {
			removeAll();
			initStaticBindingPanel();
			revalidate();
			repaint();
		}
		AbstractBinding edited = _bindingSelectorPanel._bindingSelector.getEditedObject();

		isUpdatingPanel = true;

		if (edited instanceof StaticBinding) {

			if (currentType == EvaluationType.BOOLEAN && edited instanceof BooleanStaticBinding) {
				selectValueCB.setSelectedItem(((BooleanStaticBinding) edited).getStringRepresentation());
			} else if (currentType == EvaluationType.ARITHMETIC_INTEGER && edited instanceof IntegerStaticBinding) {
				integerValueChooser.setValue(((IntegerStaticBinding) edited).getValue());
			} else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
				if (edited instanceof FloatStaticBinding) {
					enterValueTF.setText(((FloatStaticBinding) edited).getStringRepresentation());
				} else if (edited instanceof IntegerStaticBinding) {
					enterValueTF.setText(((IntegerStaticBinding) edited).getStringRepresentation());
				}
			} else if (currentType == EvaluationType.STRING && edited instanceof StringStaticBinding) {
				enterValueTF.setText(((StringStaticBinding) edited).getValue());
			}
		}

		isUpdatingPanel = false;

	}

	void enableStaticBindingPanel() {
		_bindingSelectorPanel._connectButton.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "validate"));
		selectStaticBindingCB.setSelected(true);
		if (selectValueCB != null) {
			selectValueCB.setEnabled(true);
		}
		if (enterValueTF != null) {
			enterValueTF.setEnabled(true);
		}
		if (dateSelector != null) {
			dateSelector.setEnabled(true);
		}
		if (durationSelector != null) {
			durationSelector.setEnabled(true);
		}
		if (integerValueChooser != null) {
			integerValueChooser.setEnabled(true);
		}
	}

	void disableStaticBindingPanel() {
		_bindingSelectorPanel._connectButton.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "connect"));
		selectStaticBindingCB.setSelected(false);
		if (selectValueCB != null) {
			selectValueCB.setEnabled(false);
		}
		if (enterValueTF != null) {
			enterValueTF.setEnabled(false);
		}
		if (dateSelector != null) {
			dateSelector.setEnabled(false);
		}
		if (durationSelector != null) {
			durationSelector.setEnabled(false);
		}
		if (integerValueChooser != null) {
			integerValueChooser.setEnabled(false);
		}
	}
}
