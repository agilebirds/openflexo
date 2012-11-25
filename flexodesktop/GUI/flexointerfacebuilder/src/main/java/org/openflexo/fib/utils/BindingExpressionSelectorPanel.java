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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JButton;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.toolbox.ToolBox;

/**
 * This panel is the way a DataBinding is edited when it takes the form of a complex expression (responds true to
 * {@link DataBinding#isExpression()}). This panel is always instanciated in the context of a {@link BindingSelector}
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class BindingExpressionSelectorPanel extends AbstractBindingSelectorPanel {

	private final BindingSelector bindingSelector;
	protected ButtonsControlPanel _controlPanel;
	protected JButton _applyButton;
	protected JButton _cancelButton;
	protected JButton _resetButton;
	private BindingExpressionPanel _expressionPanel;

	/**
	 * @param bindingSelector
	 */
	BindingExpressionSelectorPanel(BindingSelector bindingSelector) {
		super();
		this.bindingSelector = bindingSelector;
	}

	@Override
	public void delete() {
		if (_expressionPanel != null) {
			_expressionPanel.delete();
			_expressionPanel = null;
		}
	}

	@Override
	public Dimension getDefaultSize() {
		return new Dimension(520, 250);
	}

	@Override
	protected void init() {
		setLayout(new BorderLayout());

		_controlPanel = new ButtonsControlPanel() {
			@Override
			public String localizedForKeyAndButton(String key, JButton component) {
				return FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, key, component);
			}
		};
		_applyButton = _controlPanel.addButton("apply", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingExpressionSelectorPanel.this.bindingSelector.apply();
			}
		});
		_cancelButton = _controlPanel.addButton("cancel", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingExpressionSelectorPanel.this.bindingSelector.cancel();
			}
		});
		_resetButton = _controlPanel.addButton("reset", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingExpressionSelectorPanel.this.bindingSelector.setEditedObject(null);
				BindingExpressionSelectorPanel.this.bindingSelector.apply();
			}
		});

		_controlPanel.applyFocusTraversablePolicyTo(_controlPanel, false);

		// BindingExpression newBindingExpression = new BindingExpression(new BindingExpression.BindingValueVariable(new
		// Variable("parent")));

		/*if (bindingSelector.getEditedObject().getExpression() == null || !(bindingSelector.getEditedObject().isExpression())) {
			BindingSelector.logger.severe("Unexpected object " + bindingSelector.getEditedObject().getClass().getSimpleName()
					+ " found instead of BindingExpression");
			bindingSelector.getEditedObject().setExpression(bindingSelector.makeBindingExpression());
		}*/

		/*if (_bindingSelector.getEditedObject() == null || ((BindingExpression) _bindingSelector.getEditedObject()).getExpression() == null) {
			_bindingSelector._editedObject = _bindingSelector.makeBindingExpression();
		}*/

		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("init() called in BindingExpressionSelectorPanel with " + bindingSelector.getEditedObject()
					+ " expression=" + bindingSelector.getEditedObject().getExpression());
		}

		_expressionPanel = new BindingExpressionPanel(bindingSelector.getEditedObject()) {
			@Override
			protected void fireEditedExpressionChanged(DataBinding expression) {
				super.fireEditedExpressionChanged(expression);
				updateApplyButtonStatus();
				BindingExpressionSelectorPanel.this.bindingSelector.checkIfDisplayModeShouldChange(expression, true);
			}
		};

		add(_expressionPanel, BorderLayout.CENTER);
		add(_controlPanel, BorderLayout.SOUTH);

		update();
	}

	@Override
	protected void processTabPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processBackspace() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processDelete() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processDownPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processEnterPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processUpPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processLeftPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void processRightPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void synchronizePanelWithTextFieldValue(String textValue) {
	}

	@Override
	protected void update() {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("update() called for BindingExpressionSelectorPanel");
		}

		/*if (_bindingSelector.getEditedObject() != null && !(_bindingSelector.getEditedObject() instanceof BindingExpression)) {
			BindingSelector.logger.warning("update() called in BindingExpressionSelectorPanel with object of type "
					+ _bindingSelector.getEditedObject().getClass().getSimpleName());
			return;
		}

		BindingExpression bindingExpression = (BindingExpression) _bindingSelector.getEditedObject();

		_expressionPanel.setEditedExpression(bindingExpression);*/

		_expressionPanel.setEditedExpression(bindingSelector.getEditedObject());

		updateApplyButtonStatus();

	}

	void updateApplyButtonStatus() {
		/*if (_bindingSelector.getEditedObject() != null && !(_bindingSelector.getEditedObject() instanceof BindingExpression)) {
			BindingSelector.logger.warning("updateApplyButtonStatus() called in BindingExpressionSelectorPanel with object of type "
					+ _bindingSelector.getEditedObject().getClass().getSimpleName());
			return;
		}*/

		DataBinding<?> bindingExpression = bindingSelector.getEditedObject();

		// Update apply button state
		_applyButton.setEnabled(bindingExpression != null && bindingExpression.isValid());
		if (bindingExpression != null && bindingExpression.isValid()) {
			if (ToolBox.isMacOSLaf()) {
				_applyButton.setSelected(true);
			}
		}
		if (bindingExpression != null) {
			bindingSelector.getTextField().setForeground(bindingExpression.isValid() ? Color.BLACK : Color.RED);
		}
	}

	@Override
	protected void fireBindableChanged() {
		update();
	}

	@Override
	protected void fireBindingDefinitionChanged() {
		update();
	}

	@Override
	protected void willApply() {
	}
}