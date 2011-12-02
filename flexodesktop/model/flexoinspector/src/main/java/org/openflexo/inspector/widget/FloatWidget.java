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
 * Represents a widget able to edit a double or an Double object
 * 
 * @author sguerin
 */
public class FloatWidget extends DenaliWidget<Float> {

	static final Logger logger = Logger.getLogger(FloatWidget.class.getPackage().getName());

	private static final String READONLY_TEXTFIELD = "readonlyTextField";

	public static final String MIN_VALUE_PARAM = "minimum";
	public static final String MAX_VALUE_PARAM = "maximum";
	public static final String INCREMENT_VALUE_PARAM = "increment";

	private static final float DEFAULT_MIN_VALUE = Float.NEGATIVE_INFINITY;
	private static final float DEFAULT_MAX_VALUE = Float.POSITIVE_INFINITY;
	private static final float DEFAULT_INC_VALUE = 1.0f;

	protected boolean ignoreTextfieldChanges = false;

	JSpinner valueChooser;

	/**
	 * @param model
	 */
	public FloatWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);

		float min, max, inc;
		if (model.hasValueForParameter(MIN_VALUE_PARAM)) {
			min = model.getFloatValueForParameter(MIN_VALUE_PARAM);
		} else {
			min = DEFAULT_MIN_VALUE;
		}
		if (model.hasValueForParameter(MAX_VALUE_PARAM)) {
			max = model.getFloatValueForParameter(MAX_VALUE_PARAM);
		} else {
			max = DEFAULT_MAX_VALUE;
		}
		if (model.hasValueForParameter(INCREMENT_VALUE_PARAM)) {
			inc = model.getFloatValueForParameter(INCREMENT_VALUE_PARAM);
		} else {
			inc = DEFAULT_INC_VALUE;
		}

		SpinnerNumberModel valueModel = new SpinnerNumberModel(min, min, max, inc);
		valueChooser = new JSpinner(valueModel);
		valueChooser.setEditor(new JSpinner.NumberEditor(valueChooser/*, "#.##"*/));
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
		if (model.hasValueForParameter(READONLY_TEXTFIELD) && model.getBooleanValueForParameter(READONLY_TEXTFIELD)) {
			valueChooser.setEnabled(false);
		}

		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		Float currentValue = null;
		if (getObjectValue() == null) {
			setObjectValue(new Float(0));
		}
		currentValue = getObjectValue();
		ignoreTextfieldChanges = true;

		ignoreTextfieldChanges = false;
		widgetUpdating = false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		modelUpdating = true;
		Number value = (Number) valueChooser.getValue();
		float currentValue = value.floatValue();
		setObjectValue(currentValue);
		modelUpdating = false;
	}

	@Override
	public JComponent getDynamicComponent() {
		return valueChooser;
	}

	@Override
	public Class getDefaultType() {
		return Float.class;
	}

}
