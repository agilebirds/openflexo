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
package org.openflexo.inspector.widget;

import java.awt.ComponentOrientation;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit an int or an Integer object
 * 
 * @author sguerin
 */
public class IntegerWidget extends DenaliWidget<Integer> {

	static final Logger logger = Logger.getLogger(IntegerWidget.class.getPackage().getName());

	boolean validateOnReturn;

	private static final String VALIDATE_ON_RETURN_PARAM = "validateOnReturn";

	public static final String MIN_VALUE_PARAM = "minimum";
	public static final String MAX_VALUE_PARAM = "maximum";
	public static final String INCREMENT_VALUE_PARAM = "increment";

	private static final int DEFAULT_MIN_VALUE = Integer.MIN_VALUE;
	private static final int DEFAULT_MAX_VALUE = Integer.MAX_VALUE;
	private static final int DEFAULT_INC_VALUE = 1;

	protected boolean ignoreTextfieldChanges = false;

	JSpinner valueChooser;

	/**
	 * @param model
	 */
	public IntegerWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		if (model.hasValueForParameter(VALIDATE_ON_RETURN_PARAM)) {
			validateOnReturn = model.getBooleanValueForParameter(VALIDATE_ON_RETURN_PARAM);
		} else {
			validateOnReturn = false;
		}

		int min, max, inc;
		if (model.hasValueForParameter(MIN_VALUE_PARAM)) {
			min = model.getIntValueForParameter(MIN_VALUE_PARAM);
		} else {
			min = DEFAULT_MIN_VALUE;
		}
		if (model.hasValueForParameter(MAX_VALUE_PARAM)) {
			max = model.getIntValueForParameter(MAX_VALUE_PARAM);
		} else {
			max = DEFAULT_MAX_VALUE;
		}
		if (model.hasValueForParameter(INCREMENT_VALUE_PARAM)) {
			inc = model.getIntValueForParameter(INCREMENT_VALUE_PARAM);
		} else {
			inc = DEFAULT_INC_VALUE;
		}

		SpinnerNumberModel valueModel = new SpinnerNumberModel(min, min, max, inc);
		valueChooser = new JSpinner(valueModel);
		valueChooser.setEditor(new JSpinner.NumberEditor(valueChooser, "#"));
		valueChooser.setValue(1);
		valueChooser.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() == valueChooser) {
					updateModelFromWidget();
				}
			}
		});
		valueChooser.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		JComponent editor = valueChooser.getEditor();
		if (editor instanceof DefaultEditor) {
			((DefaultEditor) editor).getTextField().setHorizontalAlignment(SwingConstants.LEFT);
			if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
				((DefaultEditor) editor).getTextField().setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
			}
		}
		if (isReadOnly()) {
			valueChooser.setEnabled(false);
		}

		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		Integer currentValue = null;

		if (getObjectValue() == null) {
			setObjectValue(new Integer(0));
		}
		currentValue = getObjectValue();

		ignoreTextfieldChanges = true;

		try {
			valueChooser.setValue(currentValue);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			logger.warning("Unexpected exception " + e.getMessage());
		}
		ignoreTextfieldChanges = false;
		widgetUpdating = false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		if (isReadOnly()) {
			return;
		}
		modelUpdating = true;
		int currentValue = (Integer) valueChooser.getValue();
		setObjectValue(currentValue);
		modelUpdating = false;
	}

	@Override
	public JComponent getDynamicComponent() {
		return valueChooser;
	}

	@Override
	public Class<Integer> getDefaultType() {
		return Integer.class;
	}

}
