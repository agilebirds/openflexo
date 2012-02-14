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
package org.openflexo.fib.view.widget;

import java.awt.ComponentOrientation;
import java.awt.event.FocusEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit an Long or an Integer object
 * 
 * @author sguerin
 */
public abstract class FIBNumberWidget<T extends Number> extends FIBWidgetView<FIBNumber, JSpinner, T> {

	static final Logger logger = Logger.getLogger(FIBNumberWidget.class.getPackage().getName());

	boolean validateOnReturn;

	protected boolean ignoreTextfieldChanges = false;

	JSpinner valueChooser;

	/**
	 * @param model
	 */
	public FIBNumberWidget(FIBNumber model, FIBController controller) {
		super(model, controller);
		validateOnReturn = model.getValidateOnReturn();

		Number min = model.getMinValue();
		Number max = model.getMaxValue();
		Number inc = model.getIncrement();

		SpinnerNumberModel valueModel = makeSpinnerModel();
		valueChooser = new JSpinner(valueModel);
		valueChooser.setEditor(new JSpinner.NumberEditor(valueChooser /*, "#.##"*/));
		valueChooser.setValue(getDefaultValue());
		valueChooser.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() == valueChooser && !ignoreTextfieldChanges) {
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

		if (model.getColumns() != null) {
			getTextField().setColumns(model.getColumns());
		} else {
			getTextField().setColumns(getDefaultColumns());
		}

		if (isReadOnly()) {
			valueChooser.setEnabled(false);
		}

		getJComponent().addFocusListener(this);
		getTextField().addFocusListener(this);
		updateFont();
	}

	@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		if (event.getSource() == getTextField()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getTextField().selectAll();
				}
			});
		}
	}

	protected abstract SpinnerNumberModel makeSpinnerModel();

	public abstract T getDefaultValue();

	@Override
	public synchronized boolean updateWidgetFromModel() {
		// logger.info("updateWidgetFromModel() with "+getValue());

		if (notEquals(getValue(), getEditedValue())) {

			widgetUpdating = true;
			T currentValue = null;

			if (getValue() == null) {
				// setValue(getDefaultValue());
				currentValue = getDefaultValue();
			} else {
				try {
					currentValue = getValue();
				} catch (ClassCastException e) {
					logger.warning("ClassCastException: " + e.getMessage());
					logger.warning("Data: " + getWidget().getData() + " returned " + getValue());
				}
			}

			ignoreTextfieldChanges = true;
			try {
				valueChooser.setValue(currentValue);
			} catch (IllegalArgumentException e) {
				logger.warning("IllegalArgumentException: " + e.getMessage());
			}

			ignoreTextfieldChanges = false;
			widgetUpdating = false;

			return true;
		}
		return false;
	}

	protected abstract T getEditedValue();

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), getEditedValue())) {
			if (isReadOnly()) {
				return false;
			}
			modelUpdating = true;
			setValue(getEditedValue());
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public JSpinner getJComponent() {
		return valueChooser;
	}

	@Override
	public JSpinner getDynamicJComponent() {
		return valueChooser;
	}

	public JFormattedTextField getTextField() {
		JComponent editor = valueChooser.getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			return ((JSpinner.DefaultEditor) editor).getTextField();
		}
		return null;
	}

	public abstract int getDefaultColumns();

	public static class FIBByteWidget extends FIBNumberWidget<Byte> {
		public FIBByteWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Byte min = getWidget().retrieveMinValue().byteValue();
			Byte max = getWidget().retrieveMaxValue().byteValue();
			Byte inc = getWidget().retrieveIncrement().byteValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Byte getDefaultValue() {
			return new Byte((byte) 0);
		}

		@Override
		protected Byte getEditedValue() {
			return ((Number) valueChooser.getValue()).byteValue();
		}

		@Override
		public int getDefaultColumns() {
			return 4;
		}
	}

	public static class FIBShortWidget extends FIBNumberWidget<Short> {
		public FIBShortWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Short min = getWidget().retrieveMinValue().shortValue();
			Short max = getWidget().retrieveMaxValue().shortValue();
			Short inc = getWidget().retrieveIncrement().shortValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Short getDefaultValue() {
			return new Short((short) 0);
		}

		@Override
		protected Short getEditedValue() {
			return ((Number) valueChooser.getValue()).shortValue();
		}

		@Override
		public int getDefaultColumns() {
			return 6;
		}
	}

	public static class FIBIntegerWidget extends FIBNumberWidget<Integer> {
		public FIBIntegerWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Integer min = getWidget().retrieveMinValue().intValue();
			Integer max = getWidget().retrieveMaxValue().intValue();
			Integer inc = getWidget().retrieveIncrement().intValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Integer getDefaultValue() {
			if (getWidget().getMinValue() != null && getWidget().getMinValue().intValue() > 0) {
				return getWidget().getMinValue().intValue();
			}
			return new Integer(0);
		}

		@Override
		protected Integer getEditedValue() {
			return ((Number) valueChooser.getValue()).intValue();
		}

		@Override
		public int getDefaultColumns() {
			return 8;
		}
	}

	public static class FIBLongWidget extends FIBNumberWidget<Long> {
		public FIBLongWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Long min = getWidget().retrieveMinValue().longValue();
			Long max = getWidget().retrieveMaxValue().longValue();
			Long inc = getWidget().retrieveIncrement().longValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Long getDefaultValue() {
			return new Long(0);
		}

		@Override
		protected Long getEditedValue() {
			return ((Number) valueChooser.getValue()).longValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}
	}

	public static class FIBFloatWidget extends FIBNumberWidget<Float> {
		public FIBFloatWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			float min = getWidget().retrieveMinValue().floatValue();
			float max = getWidget().retrieveMaxValue().floatValue();
			float inc = getWidget().retrieveIncrement().floatValue();
			try {
				return new SpinnerNumberModel((Number) getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Float getDefaultValue() {
			return new Float(0);
		}

		@Override
		protected Float getEditedValue() {
			return ((Number) valueChooser.getValue()).floatValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}
	}

	public static class FIBDoubleWidget extends FIBNumberWidget<Double> {
		public FIBDoubleWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Double min = getWidget().retrieveMinValue().doubleValue();
			Double max = getWidget().retrieveMaxValue().doubleValue();
			Double inc = getWidget().retrieveIncrement().doubleValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Double getDefaultValue() {
			return new Double(0);
		}

		@Override
		protected Double getEditedValue() {
			return ((Number) valueChooser.getValue()).doubleValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}
	}

}
