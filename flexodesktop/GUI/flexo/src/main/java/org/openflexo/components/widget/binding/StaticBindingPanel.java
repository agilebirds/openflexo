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
package org.openflexo.components.widget.binding;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.FlexoCst;
import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.components.widget.KeySelector;
import org.openflexo.swing.DateSelector;
import org.openflexo.swing.DurationSelector;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.toolbox.Duration.DurationUnit;


import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BooleanStaticBinding;
import org.openflexo.foundation.bindings.DKVBinding;
import org.openflexo.foundation.bindings.DateStaticBinding;
import org.openflexo.foundation.bindings.DurationStaticBinding;
import org.openflexo.foundation.bindings.FloatStaticBinding;
import org.openflexo.foundation.bindings.IntegerStaticBinding;
import org.openflexo.foundation.bindings.StaticBinding;
import org.openflexo.foundation.bindings.StringStaticBinding;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.localization.FlexoLocalization;

class StaticBindingPanel extends JPanel
{
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
	protected KeySelector dkvSelector = null;
	protected JSpinner integerValueChooser = null;
	protected JComboBox typeCB = null;

	private EvaluationType currentType;

	protected StaticBindingPanel(BindingSelectorPanel bindingSelectorPanel)
	{
		_bindingSelectorPanel = bindingSelectorPanel;
		setLayout(new FlowLayout(FlowLayout.LEFT,5,0));
		initStaticBindingPanel();
		updateStaticBindingPanel();
	}

	private void initStaticBindingPanel()
	{
		selectStaticBindingCB = null;
		selectValueCB = null;
		enterValueTF = null;
		dateSelector = null;
		durationSelector = null;
		integerValueChooser = null;
		dkvSelector = null;
		selectStaticBindingCB = new JCheckBox(FlexoLocalization.localizedForKey("define_a_static_value"));
		selectStaticBindingCB.setFont(FlexoCst.SMALL_FONT);
		selectStaticBindingCB.setSelected(false);
		selectStaticBindingCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StaticBindingPanel.this._bindingSelectorPanel.setEditStaticValue(selectStaticBindingCB.isSelected());
			}
		});
		add(selectStaticBindingCB);
		if ((_bindingSelectorPanel._bindingSelector.getBindingDefinition() == null) || (_bindingSelectorPanel._bindingSelector.getBindingDefinition().getType() == null)) {
			enterValueTF = new JTextField(10);
			enterValueTF.setFont(FlexoCst.SMALL_FONT);
			add(enterValueTF);
			currentType = EvaluationType.LITERAL;
			disableStaticBindingPanel();
		}
		else {
			currentType = kindOf(_bindingSelectorPanel._bindingSelector.getBindingDefinition().getType());
			if (currentType == EvaluationType.ENUM) {
				dkvSelector = new KeySelector(_bindingSelectorPanel._bindingSelector.getProject(),null,10) {
					@Override
					public void apply() {
						super.apply();
						Key selectedKey = dkvSelector.getEditedObject();
						BindingSelector.logger.fine("Selected key: "+selectedKey);
						StaticBindingPanel.this._bindingSelectorPanel._bindingSelector.setEditedObject(new DKVBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),selectedKey));
						if (!StaticBindingPanel.this._bindingSelectorPanel._connectButton.isEnabled() && selectedKey != null) {
							StaticBindingPanel.this._bindingSelectorPanel._connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM()==ToolBox.MACOS)
								StaticBindingPanel.this._bindingSelectorPanel._connectButton.setSelected(true);
						}
					}
					@Override
					public void cancel() {
						super.cancel();
						StaticBindingPanel.this._bindingSelectorPanel._bindingSelector.setEditedObject(new DKVBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),null));
					}
					@Override
					public String localizedForKey(String aKey) {
						return FlexoLocalization.localizedForKey(aKey);
					}
				};
				dkvSelector.setDomain(_bindingSelectorPanel._bindingSelector.getBindingDefinition().getType().getDomain());
				dkvSelector.getTextField().setFont(FlexoCst.SMALL_FONT);
				add(dkvSelector);
			}
			else if (currentType == EvaluationType.BOOLEAN) {
				final String UNSELECTED = FlexoLocalization.localizedForKey("select_a_value");
				final String TRUE = FlexoLocalization.localizedForKey("true");
				final String FALSE = FlexoLocalization.localizedForKey("false");
				String[] availableValues = { UNSELECTED, TRUE, FALSE };
				selectValueCB = new JComboBox(availableValues);
				selectValueCB.setFont(FlexoCst.SMALL_FONT);
				selectValueCB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) return;
						if (selectValueCB.getSelectedItem().equals(TRUE))
							_bindingSelectorPanel._bindingSelector.setEditedObject(new BooleanStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),true));
						else if (selectValueCB.getSelectedItem().equals(FALSE))
							_bindingSelectorPanel._bindingSelector.setEditedObject(new BooleanStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),false));
					}
				});
				add(selectValueCB);
			}
			else if (currentType == EvaluationType.ARITHMETIC_INTEGER) {
				SpinnerNumberModel valueModel = new SpinnerNumberModel(1, Short.MIN_VALUE, Short.MAX_VALUE, 1);
				integerValueChooser = new JSpinner(valueModel);
				integerValueChooser.setEditor(new JSpinner.NumberEditor(integerValueChooser, "#"));
				integerValueChooser.setMinimumSize(integerValueChooser.getPreferredSize());
				integerValueChooser.setValue(1);
				integerValueChooser.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e)
					{
						if (isUpdatingPanel) return;
						Object v = integerValueChooser.getValue();
						if (v instanceof Integer)
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),(Integer)v));
						else if (v instanceof Long)
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),(Long)v));
						else if (v instanceof Short)
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),(Short)v));
						else if (v instanceof Byte)
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),(Byte)v));
						else if (v instanceof Character)
							_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),(Character)v));
					}
				});
				integerValueChooser.setFont(FlexoCst.SMALL_FONT);
				integerValueChooser.getEditor().setFont(FlexoCst.SMALL_FONT);
				add(integerValueChooser);
			}
			else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
				enterValueTF = new JTextField(10);
				enterValueTF.setFont(FlexoCst.SMALL_FONT);
				enterValueTF.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) return;
						if (_bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText()))
							_bindingSelectorPanel._bindingSelector.setEditedObject(_bindingSelectorPanel._bindingSelector.makeStaticBindingFromString(enterValueTF.getText()));
					}
				});
				enterValueTF.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!StaticBindingPanel.this._bindingSelectorPanel._connectButton.isEnabled() && _bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
							StaticBindingPanel.this._bindingSelectorPanel._connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM()==ToolBox.MACOS)
								StaticBindingPanel.this._bindingSelectorPanel._connectButton.setSelected(true);
						}
					}
				});
				add(enterValueTF);
			}
			else if (currentType == EvaluationType.STRING) {
				enterValueTF = new JTextField(10);
				enterValueTF.setFont(FlexoCst.SMALL_FONT);
				enterValueTF.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) return;
						if (_bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue('"'+enterValueTF.getText()+'"'))
							_bindingSelectorPanel._bindingSelector.setEditedObject(_bindingSelectorPanel._bindingSelector.makeStaticBindingFromString('"'+enterValueTF.getText()+'"'));
					}
				});
				enterValueTF.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!StaticBindingPanel.this._bindingSelectorPanel._connectButton.isEnabled() && enterValueTF.getText().length() > 0) {
							StaticBindingPanel.this._bindingSelectorPanel._connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM()==ToolBox.MACOS)
								StaticBindingPanel.this._bindingSelectorPanel._connectButton.setSelected(true);
						}							}
				});
				add(enterValueTF);
			}
			else if (currentType == EvaluationType.DATE) {
				dateSelector = new DateSelector() {
					@Override
					public void apply() {
						super.apply();
						Date selectedDate = dateSelector.getEditedObject();
						logger.fine("Selected date: "+selectedDate);
						StaticBindingPanel.this._bindingSelectorPanel._bindingSelector.setEditedObject(new DateStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),selectedDate));
						if (!StaticBindingPanel.this._bindingSelectorPanel._connectButton.isEnabled() && selectedDate != null) {
							StaticBindingPanel.this._bindingSelectorPanel._connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM()==ToolBox.MACOS)
								StaticBindingPanel.this._bindingSelectorPanel._connectButton.setSelected(true);
						}
					}
					@Override
					public void cancel() {
						super.cancel();
						StaticBindingPanel.this._bindingSelectorPanel._bindingSelector.setEditedObject(new DateStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),null));
					}
					@Override
					public String localizedForKey(String aKey) {
						return FlexoLocalization.localizedForKey(aKey);
					}
				};
				dateSelector.getTextField().setFont(FlexoCst.SMALL_FONT);
				add(dateSelector);
			}
			else if (currentType == EvaluationType.DURATION) {
				durationSelector = new DurationSelector(10) {
					@Override
					public void apply() {
						super.apply();
						Duration selectedDuration = durationSelector.getEditedObject();
						logger.fine("Selected duration: "+selectedDuration);
						StaticBindingPanel.this._bindingSelectorPanel._bindingSelector.setEditedObject(new DurationStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),durationSelector.getEditedObject()));
						if (!StaticBindingPanel.this._bindingSelectorPanel._connectButton.isEnabled() && selectedDuration != null) {
							StaticBindingPanel.this._bindingSelectorPanel._connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM()==ToolBox.MACOS)
								StaticBindingPanel.this._bindingSelectorPanel._connectButton.setSelected(true);
						}
					}
					@Override
					public void cancel() {
						super.cancel();
						StaticBindingPanel.this._bindingSelectorPanel._bindingSelector.setEditedObject(new DurationStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),null));
					}
					@Override
					public String localizedForKey(String aKey) {
						return FlexoLocalization.localizedForKey(aKey);
					}
					@Override
					public String localizedForKeyAndButton(String key, JButton component) {
						return FlexoLocalization.localizedForKey(key,component);
					}
				};
				durationSelector.getTextField().setFont(FlexoCst.SMALL_FONT);
				add(durationSelector);
			}

			if (_bindingSelectorPanel.getEditStaticValue()) {
				enableStaticBindingPanel();
				for (int i=0; i<_bindingSelectorPanel.getVisibleColsCount(); i++) _bindingSelectorPanel.listAtIndex(i).setEnabled(false);
			}
			else {
				disableStaticBindingPanel();
			}

		}

		if ((_bindingSelectorPanel._bindingSelector.getBindingDefinition() == null)
				|| (_bindingSelectorPanel._bindingSelector.getBindingDefinition().getType() == null)
				|| (_bindingSelectorPanel._bindingSelector.getBindingDefinition().getType().isObject())) {
			final String SELECT = FlexoLocalization.localizedForKey("select");
			final String BOOLEAN = FlexoLocalization.localizedForKey("boolean");
			final String INTEGER = FlexoLocalization.localizedForKey("integer");
			final String FLOAT = FlexoLocalization.localizedForKey("float");
			final String STRING = FlexoLocalization.localizedForKey("string");
			final String DATE = FlexoLocalization.localizedForKey("date");
			final String DURATION = FlexoLocalization.localizedForKey("duration");
			final String DKV = FlexoLocalization.localizedForKey("enum");
			String[] availableValues = { SELECT, BOOLEAN, INTEGER, FLOAT, STRING, DATE, DURATION, DKV };
			typeCB = new JComboBox(availableValues);
			typeCB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (isUpdatingPanel) return;
					if (typeCB.getSelectedItem().equals(BOOLEAN))
						_bindingSelectorPanel._bindingSelector.setEditedObject(new BooleanStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),true));
					else if (typeCB.getSelectedItem().equals(INTEGER))
						_bindingSelectorPanel._bindingSelector.setEditedObject(new IntegerStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),0));
					else if (typeCB.getSelectedItem().equals(FLOAT))
						_bindingSelectorPanel._bindingSelector.setEditedObject(new FloatStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),0));
					else if (typeCB.getSelectedItem().equals(STRING))
						_bindingSelectorPanel._bindingSelector.setEditedObject(new StringStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),""));
					else if (typeCB.getSelectedItem().equals(DATE))
						_bindingSelectorPanel._bindingSelector.setEditedObject(new DateStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),new Date()));
					else if (typeCB.getSelectedItem().equals(DURATION))
						_bindingSelectorPanel._bindingSelector.setEditedObject(new DurationStaticBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),new Duration(1,DurationUnit.SECONDS)));
					else if (typeCB.getSelectedItem().equals(DKV))
						_bindingSelectorPanel._bindingSelector.setEditedObject(new DKVBinding(_bindingSelectorPanel._bindingSelector.getBindingDefinition(),(FlexoModelObject)_bindingSelectorPanel._bindingSelector.getBindable(),new Key(_bindingSelectorPanel._bindingSelector.getProject().getDKVModel())));
				}
			});
			if (typeCB != null) {
				isUpdatingPanel = true;
				if (currentType == EvaluationType.BOOLEAN) typeCB.setSelectedItem(BOOLEAN);
				else if (currentType == EvaluationType.ARITHMETIC_INTEGER) typeCB.setSelectedItem(INTEGER);
				else if (currentType == EvaluationType.ARITHMETIC_FLOAT) typeCB.setSelectedItem(FLOAT);
				else if (currentType == EvaluationType.STRING) typeCB.setSelectedItem(STRING);
				else if (currentType == EvaluationType.DATE) typeCB.setSelectedItem(DATE);
				else if (currentType == EvaluationType.DURATION) typeCB.setSelectedItem(DURATION);
				else if (currentType == EvaluationType.ENUM) typeCB.setSelectedItem(DKV);
				isUpdatingPanel = false;
			}

			typeCB.setFont(FlexoCst.SMALL_FONT);
			add(typeCB);
		}

	}

	void willApply()
	{
		if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
			if (_bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText()))
				_bindingSelectorPanel._bindingSelector.setEditedObject(_bindingSelectorPanel._bindingSelector.makeStaticBindingFromString(enterValueTF.getText()));
		}
		else if (currentType == EvaluationType.STRING) {
			if (_bindingSelectorPanel._bindingSelector.isAcceptableStaticBindingValue('"'+enterValueTF.getText()+'"'))
				_bindingSelectorPanel._bindingSelector.setEditedObject(_bindingSelectorPanel._bindingSelector.makeStaticBindingFromString('"'+enterValueTF.getText()+'"'));
		}
	}

	private EvaluationType kindOf(DMType type)
	{
		if (type.isObject() && _bindingSelectorPanel._bindingSelector.getEditedObject() instanceof StaticBinding) {
			return ((StaticBinding)_bindingSelectorPanel._bindingSelector.getEditedObject()).getEvaluationType();
		}
		else return DMType.kindOf(type);
	}

	void updateStaticBindingPanel()
	{
		isUpdatingPanel = true;

		EvaluationType newType;
		if (_bindingSelectorPanel._bindingSelector.getBindingDefinition() == null || _bindingSelectorPanel._bindingSelector.getBindingDefinition().getType() == null) {
			newType = EvaluationType.LITERAL;
		}
		else {
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
				selectValueCB.setSelectedItem(((BooleanStaticBinding)edited).getStringRepresentation());
			}
			else if (currentType == EvaluationType.ARITHMETIC_INTEGER && edited instanceof IntegerStaticBinding) {
				integerValueChooser.setValue(((IntegerStaticBinding)edited).getValue());
			}
			else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
				if (edited instanceof FloatStaticBinding) {
					enterValueTF.setText(((FloatStaticBinding)edited).getStringRepresentation());
				}
				else if (edited instanceof IntegerStaticBinding) {
					enterValueTF.setText(((IntegerStaticBinding)edited).getStringRepresentation());
				}
			}
			else if (currentType == EvaluationType.STRING && edited instanceof StringStaticBinding) {
				enterValueTF.setText(((StringStaticBinding)edited).getValue());
			}
			else if (currentType == EvaluationType.DATE && edited instanceof DateStaticBinding) {
				dateSelector.setEditedObject(((DateStaticBinding)edited).getValue());
			}
			else if (currentType == EvaluationType.DURATION && edited instanceof DurationStaticBinding) {
				durationSelector.setEditedObject(((DurationStaticBinding)edited).getValue());
			}
			else if (currentType == EvaluationType.ENUM && edited instanceof DKVBinding) {
				if (((DKVBinding)edited).getValue() != null)
					dkvSelector.setDomain(((DKVBinding)edited).getValue().getDomain());
				dkvSelector.setEditedObject(((DKVBinding)edited).getValue());
			}

		}

		isUpdatingPanel = false;


	}

	void enableStaticBindingPanel()
	{
		_bindingSelectorPanel._connectButton.setText(FlexoLocalization.localizedForKey("validate"));
		selectStaticBindingCB.setSelected(true);
		if (selectValueCB != null) selectValueCB.setEnabled(true);
		if (enterValueTF != null) enterValueTF.setEnabled(true);
		if (dateSelector != null) dateSelector.setEnabled(true);
		if (durationSelector != null) durationSelector.setEnabled(true);
		if (integerValueChooser != null) integerValueChooser.setEnabled(true);
	}

	void disableStaticBindingPanel()
	{
		_bindingSelectorPanel._connectButton.setText(FlexoLocalization.localizedForKey("connect"));
		selectStaticBindingCB.setSelected(false);
		if (selectValueCB != null) selectValueCB.setEnabled(false);
		if (enterValueTF != null) enterValueTF.setEnabled(false);
		if (dateSelector != null) dateSelector.setEnabled(false);
		if (durationSelector != null) durationSelector.setEnabled(false);
		if (integerValueChooser != null) integerValueChooser.setEnabled(false);
	}
}