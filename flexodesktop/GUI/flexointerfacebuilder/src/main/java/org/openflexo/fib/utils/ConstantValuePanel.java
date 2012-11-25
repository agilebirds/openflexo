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

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Constant.ObjectSymbolicConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.DateSelector;
import org.openflexo.toolbox.ToolBox;

/**
 * This panel allows to select or edit a constant value in the context of a {@link BindingSelectorPanel}
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
class ConstantValuePanel extends JPanel {

	public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 9);

	/**
	 * 
	 */
	private final BindingSelectorPanel bindingSelectorPanel;
	boolean isUpdatingPanel = false;
	protected JCheckBox selectStaticBindingCB = null;
	protected JComboBox selectValueCB = null;
	protected JTextField enterValueTF = null;
	protected DateSelector dateSelector = null;
	protected DurationSelector durationSelector = null;
	protected JSpinner integerValueChooser = null;
	protected JComboBox typeCB = null;

	private EvaluationType currentType;

	protected ConstantValuePanel(BindingSelectorPanel bindingSelectorPanel) {
		this.bindingSelectorPanel = bindingSelectorPanel;
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		initConstantValuePanel();
		updateConstantValuePanel();
	}

	private void initConstantValuePanel() {
		selectStaticBindingCB = null;
		selectValueCB = null;
		enterValueTF = null;
		dateSelector = null;
		durationSelector = null;
		integerValueChooser = null;
		selectStaticBindingCB = new JCheckBox(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "define_a_constant_value"));
		selectStaticBindingCB.setFont(SMALL_FONT);
		selectStaticBindingCB.setSelected(false);
		selectStaticBindingCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConstantValuePanel.this.bindingSelectorPanel.setEditStaticValue(selectStaticBindingCB.isSelected());
			}
		});
		add(selectStaticBindingCB);
		if (bindingSelectorPanel.bindingSelector.getBindingDefinition() == null
				|| bindingSelectorPanel.bindingSelector.getBindingDefinition().getType() == null) {
			enterValueTF = new JTextField(10);
			enterValueTF.setFont(SMALL_FONT);
			add(enterValueTF);
			currentType = EvaluationType.LITERAL;
			disableStaticBindingPanel();
		} else {
			currentType = kindOf(bindingSelectorPanel.bindingSelector.getBindingDefinition().getType());
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
							bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(BooleanConstant.TRUE);
							bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
						} else if (selectValueCB.getSelectedItem().equals(FALSE)) {
							bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(BooleanConstant.FALSE);
							bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
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
						if (v instanceof Number) {
							bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(
									new Constant.IntegerConstant(((Number) v).longValue()));
							bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
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
						if (bindingSelectorPanel.bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
							bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(
									bindingSelectorPanel.bindingSelector.makeStaticBindingFromString(enterValueTF.getText()));
							bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
						}
					}
				});
				enterValueTF.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!ConstantValuePanel.this.bindingSelectorPanel.connectButton.isEnabled()
								&& bindingSelectorPanel.bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
							ConstantValuePanel.this.bindingSelectorPanel.connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
								ConstantValuePanel.this.bindingSelectorPanel.connectButton.setSelected(true);
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
						if (bindingSelectorPanel.bindingSelector.isAcceptableStaticBindingValue('"' + enterValueTF.getText() + '"')) {
							bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(
									bindingSelectorPanel.bindingSelector.makeStaticBindingFromString('"' + enterValueTF.getText() + '"'));
							bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
						}
					}
				});
				enterValueTF.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!ConstantValuePanel.this.bindingSelectorPanel.connectButton.isEnabled() && enterValueTF.getText().length() > 0) {
							ConstantValuePanel.this.bindingSelectorPanel.connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
								ConstantValuePanel.this.bindingSelectorPanel.connectButton.setSelected(true);
							}
						}
					}
				});
				add(enterValueTF);
			}

			if (bindingSelectorPanel.getEditStaticValue()) {
				enableStaticBindingPanel();
				for (int i = 0; i < bindingSelectorPanel.getVisibleColsCount(); i++) {
					bindingSelectorPanel.listAtIndex(i).setEnabled(false);
				}
			} else {
				disableStaticBindingPanel();
			}

		}

		if (bindingSelectorPanel.bindingSelector.getBindingDefinition() == null
				|| bindingSelectorPanel.bindingSelector.getBindingDefinition().getType() == null
				|| TypeUtils.isObject(bindingSelectorPanel.bindingSelector.getBindingDefinition().getType())) {
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
						bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(Constant.BooleanConstant.TRUE);
						bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
					} else if (typeCB.getSelectedItem().equals(INTEGER)) {
						bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(new Constant.IntegerConstant(0));
						bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
					} else if (typeCB.getSelectedItem().equals(FLOAT)) {
						bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(new Constant.FloatConstant(0));
						bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
					} else if (typeCB.getSelectedItem().equals(STRING)) {
						bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(new Constant.StringConstant(""));
						bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
					} else if (typeCB.getSelectedItem().equals(NULL)) {
						bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(ObjectSymbolicConstant.NULL);
						bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
					}
				}
			});
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

			if (bindingSelectorPanel.bindingSelector.getEditedObject().getExpression() == ObjectSymbolicConstant.NULL) {
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
			if (bindingSelectorPanel.bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
				bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(
						bindingSelectorPanel.bindingSelector.makeStaticBindingFromString(enterValueTF.getText()));
				bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
			}
		} else if (currentType == EvaluationType.STRING) {
			if (bindingSelectorPanel.bindingSelector.isAcceptableStaticBindingValue('"' + enterValueTF.getText() + '"')) {
				bindingSelectorPanel.bindingSelector.getEditedObject().setExpression(
						bindingSelectorPanel.bindingSelector.makeStaticBindingFromString('"' + enterValueTF.getText() + '"'));
				bindingSelectorPanel.bindingSelector.fireEditedObjectChanged();
			}
		}
	}

	private EvaluationType kindOf(Type type) {
		if (TypeUtils.isObject(type) && bindingSelectorPanel.bindingSelector.getEditedObject().isConstant()) {
			return ((Constant) bindingSelectorPanel.bindingSelector.getEditedObject().getExpression()).getEvaluationType();
		} else {
			return TypeUtils.kindOfType(type);
		}
	}

	void updateConstantValuePanel() {
		isUpdatingPanel = true;

		EvaluationType newType;
		if (bindingSelectorPanel.bindingSelector.getBindingDefinition() == null
				|| bindingSelectorPanel.bindingSelector.getBindingDefinition().getType() == null) {
			newType = EvaluationType.LITERAL;
		} else {
			newType = kindOf(bindingSelectorPanel.bindingSelector.getBindingDefinition().getType());
		}
		if (newType != currentType) {
			removeAll();
			initConstantValuePanel();
			revalidate();
			repaint();
		}
		DataBinding edited = bindingSelectorPanel.bindingSelector.getEditedObject();

		isUpdatingPanel = true;

		if (edited.isConstant()) {

			if (currentType == EvaluationType.BOOLEAN && (edited.getExpression() instanceof BooleanConstant)) {
				selectValueCB.setSelectedItem(edited.getExpression().toString());
			} else if (currentType == EvaluationType.ARITHMETIC_INTEGER && (edited.getExpression() instanceof IntegerConstant)) {
				integerValueChooser.setValue(((IntegerConstant) edited.getExpression()).getValue());
			} else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
				if (edited.getExpression() instanceof FloatConstant) {
					enterValueTF.setText("" + ((FloatConstant) edited.getExpression()).getValue());
				} else if (edited.getExpression() instanceof IntegerConstant) {
					enterValueTF.setText("" + ((IntegerConstant) edited.getExpression()).getValue());
				}
			} else if (currentType == EvaluationType.STRING && (edited.getExpression() instanceof StringConstant)) {
				enterValueTF.setText(((StringConstant) edited.getExpression()).getValue());
			}
		}

		isUpdatingPanel = false;

	}

	void enableStaticBindingPanel() {
		bindingSelectorPanel.connectButton.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "validate"));
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
		bindingSelectorPanel.connectButton.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "connect"));
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
